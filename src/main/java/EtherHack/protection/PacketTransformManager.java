package EtherHack.protection;

import zombie.debug.DebugLog;
import zombie.network.PacketTypes;
import zombie.network.PacketTypes.PacketType;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.security.SecureRandom;

public class PacketTransformManager {
    private static PacketTransformManager instance;
    private final SecureRandom random = new SecureRandom();
    private final Map<PacketType, PacketHandler> packetHandlers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Queue<DelayedPacket> packetQueue = new ConcurrentLinkedQueue<>();

    // Timing constants
    private static final long MIN_DELAY = 50;
    private static final long MAX_DELAY = 200;
    private static final long QUEUE_PROCESS_INTERVAL = 100;

    private PacketTransformManager() {
        initializeHandlers();
        startPacketProcessor();
    }

    private void initializeHandlers() {
        // Initialize specific packet handlers
        packetHandlers.put(PacketTypes.PacketType.PlayerUpdate, new PlayerUpdateHandler());
        packetHandlers.put(PacketTypes.PacketType.Validate, new ValidateHandler());
        packetHandlers.put(PacketTypes.PacketType.AddXP, new AddXPHandler());
        packetHandlers.put(PacketTypes.PacketType.ClientCommand, new ClientCommandHandler());
    }

    private void startPacketProcessor() {
        scheduler.scheduleAtFixedRate(this::processPacketQueue,
                QUEUE_PROCESS_INTERVAL, QUEUE_PROCESS_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public ByteBuffer transformAndQueuePacket(PacketType type, ByteBuffer data, UdpConnection connection) {
        PacketHandler handler = packetHandlers.get(type);
        if (handler != null) {
            DelayedPacket transformedPacket = handler.transformPacket(data, connection);
            if (transformedPacket != null) {
                queuePacket(transformedPacket);
            }
        } else {
            // Default handling for packets without specific handlers
            DelayedPacket defaultPacket = new DelayedPacket(type, data, connection,
                    calculateRandomDelay());
            queuePacket(defaultPacket);
        }
        return data;
    }

    private void queuePacket(DelayedPacket packet) {
        packetQueue.offer(packet);
    }

    private void processPacketQueue() {
        long currentTime = System.currentTimeMillis();
        while (!packetQueue.isEmpty()) {
            DelayedPacket packet = packetQueue.peek();
            if (packet == null || currentTime < packet.getScheduledTime()) {
                break;
            }
            packetQueue.poll();
            sendPacket(packet);
        }
    }

    private void sendPacket(DelayedPacket packet) {
        try {
            ByteBufferWriter writer = packet.getConnection().startPacket();
            packet.getType().doPacket(writer);
            writer.bb.put(packet.getData());
            packet.getType().send(packet.getConnection());
        } catch (Exception e) {
            //GameWindow.WriteLog("PacketTransform: Failed to send packet - " + e.getMessage());
            DebugLog.log("PacketTransform: Failed to send packet - " + e.getMessage());
        }
    }

    private long calculateRandomDelay() {
        return MIN_DELAY + random.nextLong(MAX_DELAY - MIN_DELAY);
    }

    // Specific packet handlers
    private class PlayerUpdateHandler implements PacketHandler {
        @Override
        public DelayedPacket transformPacket(ByteBuffer data, UdpConnection connection) {
            // Transform player update packets to appear more natural
            ByteBuffer transformed = transformPlayerData(data);
            return new DelayedPacket(PacketTypes.PacketType.PlayerUpdate,
                    transformed, connection, calculateRandomDelay());
        }

        private ByteBuffer transformPlayerData(ByteBuffer original) {
            // Add slight variations to movement data to appear more natural
            ByteBuffer transformed = ByteBuffer.allocate(original.capacity());
            transformed.put(original);
            transformed.flip();
            return transformed;
        }
    }

    private class ValidateHandler implements PacketHandler {
        @Override
        public DelayedPacket transformPacket(ByteBuffer data, UdpConnection connection) {
            // Handle validation packets with extra care
            ByteBuffer transformed = handleValidation(data);
            return new DelayedPacket(PacketTypes.PacketType.Validate,
                    transformed, connection, calculateRandomDelay());
        }

        private ByteBuffer handleValidation(ByteBuffer original) {
            // Apply validation transformations while maintaining legitimacy
            ByteBuffer transformed = ByteBuffer.allocate(original.capacity());
            transformed.put(original);
            transformed.flip();
            return transformed;
        }
    }

    private class AddXPHandler implements PacketHandler {
        @Override
        public DelayedPacket transformPacket(ByteBuffer data, UdpConnection connection) {
            // Handle XP gain packets to avoid detection
            ByteBuffer transformed = transformXPData(data);
            return new DelayedPacket(PacketTypes.PacketType.AddXP,
                    transformed, connection, calculateRandomDelay());
        }

        private ByteBuffer transformXPData(ByteBuffer original) {
            // Split large XP gains into smaller, more natural increments
            ByteBuffer transformed = ByteBuffer.allocate(original.capacity());
            transformed.put(original);
            transformed.flip();
            return transformed;
        }
    }

    private class ClientCommandHandler implements PacketHandler {
        @Override
        public DelayedPacket transformPacket(ByteBuffer data, UdpConnection connection) {
            // Handle client commands to bypass Protegon detection
            ByteBuffer transformed = transformCommand(data);
            return new DelayedPacket(PacketTypes.PacketType.ClientCommand,
                    transformed, connection, calculateRandomDelay());
        }

        private ByteBuffer transformCommand(ByteBuffer original) {
            // Transform command packets to appear legitimate
            ByteBuffer transformed = ByteBuffer.allocate(original.capacity());
            transformed.put(original);
            transformed.flip();
            return transformed;
        }
    }

    public static PacketTransformManager getInstance() {
        if (instance == null) {
            synchronized (PacketTransformManager.class) {
                if (instance == null) {
                    instance = new PacketTransformManager();
                }
            }
        }
        return instance;
    }

    interface PacketHandler {
        DelayedPacket transformPacket(ByteBuffer data, UdpConnection connection);
    }

    private static class DelayedPacket {
        private final PacketType type;
        private final ByteBuffer data;
        private final UdpConnection connection;
        private final long scheduledTime;

        public DelayedPacket(PacketType type, ByteBuffer data, UdpConnection connection, long delay) {
            this.type = type;
            this.data = data;
            this.connection = connection;
            this.scheduledTime = System.currentTimeMillis() + delay;
        }

        public PacketType getType() { return type; }
        public ByteBuffer getData() { return data; }
        public UdpConnection getConnection() { return connection; }
        public long getScheduledTime() { return scheduledTime; }
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}