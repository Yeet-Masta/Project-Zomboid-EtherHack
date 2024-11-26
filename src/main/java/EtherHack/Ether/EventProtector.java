package EtherHack.Ether;

import EtherHack.GameClientWrapper;
import EtherHack.utils.Logger;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.packets.PlayerPacket;
import zombie.network.PacketTypes;
import zombie.network.ZomboidNetData;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * EventProtector - Prevents anti-cheat from monitoring login/logout events
 */
public class EventProtector {
    private static EventProtector instance;
    private final GameClientWrapper wrapper;
    private final Map<String, Long> lastChecks = new HashMap<>();
    private final Set<String> protectedEvents = new HashSet<>();
    private static final long CHECK_COOLDOWN = 5000; // 5 seconds cooldown between checks

    public EventProtector() {
        this.wrapper = GameClientWrapper.get();
    }

    public static EventProtector getInstance() {
        if (instance == null) {
            instance = new EventProtector();
        }
        return instance;
    }

    /**
     * Protects against event detection and handles packet interception

    public boolean handlePacket(ByteBuffer buffer, ZomboidNetData data) {
        try {
            // Intercept and potentially modify packet data
            preprocessPacket(buffer, data);

            // Use wrapper to safely call GameClient methods
            if (!wrapper.gameLoadingDealWithNetData(data)) {
                wrapper.mainLoopDealWithNetData(data);
            }

            return true;
        } catch (Exception e) {
            Logger.printLog("Error handling packet: " + e.getMessage());
            return false;
        }
    }

    private void preprocessPacket(ByteBuffer buffer, ZomboidNetData data) {
        // Add any packet preprocessing/cleaning here
        if (buffer != null && data != null) {
            // Example: Clean certain packet types
            switch(data.type) {
                case Validate:
                    cleanValidatePacket(buffer);
                    break;
                case PlayerConnect:
                    cleanPlayerConnectPacket(buffer);
                    break;
                // Add other cases as needed
            }
        }
    }

    private void cleanValidatePacket(ByteBuffer buffer) {
        // Clean validation packets
        try {
            // Add validation packet cleaning logic
        } catch (Exception e) {
            Logger.printLog("Error cleaning validate packet: " + e.getMessage());
        }
    }

    private void cleanPlayerConnectPacket(ByteBuffer buffer) {
        // Clean player connect packets
        try {
            // Add player connect packet cleaning logic
        } catch (Exception e) {
            Logger.printLog("Error cleaning player connect packet: " + e.getMessage());
        }
    }*/

    private void initializeProtectedEvents() {
        protectedEvents.add("OnPlayerConnect");
        protectedEvents.add("OnPlayerDisconnect");
        protectedEvents.add("OnCreatePlayer");
        protectedEvents.add("OnLogin");
        protectedEvents.add("OnLoginState");
        protectedEvents.add("OnLoginStateSuccess");
        protectedEvents.add("OnCharacterConnect");
        protectedEvents.add("OnCoopClientConnect");
        protectedEvents.add("OnGameStart");
        protectedEvents.add("OnCreateLivingCharacter");
        protectedEvents.add("OnClientCommand");
        protectedEvents.add("OnServerCommand");
    }

    public void installProtection() {
        try {
            // Use the wrapper to clear incoming data
            GameClientWrapper wrapper = GameClientWrapper.get();

            // Instead of directly accessing incomingNetData, use the wrapper
            if (GameClient.instance != null) {
                wrapper.mainLoopDealWithNetData(new ZomboidNetData());
            }

            IsoPlayer player = IsoPlayer.getInstance();
            if (player != null) {
                player.setOnlineID(generateSafeID());
                // Use reflection to set connected state since it's private
                setFieldValue(player, "connected", true);
            }
        } catch (Exception e) {
            Logger.printLog("Failed to install protection: " + e.getMessage());
        }
    }

    private void sendFakePlayerUpdate(IsoPlayer player) {
        try {
            if (GameClient.connection != null && PlayerPacket.l_send.playerPacket.set(player)) {
                ByteBufferWriter writer = GameClient.connection.startPacket();
                PacketTypes.PacketType.PlayerUpdate.doPacket(writer);
                PlayerPacket.l_send.playerPacket.write(writer);
                PacketTypes.PacketType.PlayerUpdate.send(GameClient.connection);
            }
        } catch (Exception e) {
            Logger.printLog("Error sending player update: " + e.getMessage());
        }
    }

    private short generateSafeID() {
        return (short) (Math.abs(new Random().nextInt()) % 10000 + 1);
    }

    public boolean shouldBlockEvent(String eventName) {
        if (!protectedEvents.contains(eventName)) {
            return false;
        }

        long now = System.currentTimeMillis();
        Long lastCheck = lastChecks.get(eventName);
        if (lastCheck == null || (now - lastCheck) > CHECK_COOLDOWN) {
            lastChecks.put(eventName, now);
            return true;
        }

        return false;
    }

    private static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            Logger.printLog("Error setting field value: " + e.getMessage());
        }
    }

    // Modified method to hook into network packets
    public static void filterIncomingPackets() {
        try {
            GameClientWrapper wrapper = GameClientWrapper.get();
            if (GameClient.instance != null) {
                // Fix getIncomingPackets to getIncomingNetData
                ConcurrentLinkedQueue<ZomboidNetData> netData = wrapper.getIncomingNetData();

                // Use proper type comparison for PacketTypes
                netData.removeIf(packet -> {
                    if (packet == null) return true;

                    short packetId = packet.type.getId();
                    return packetId == PacketTypes.PacketType.Validate.getId() ||
                            packetId == PacketTypes.PacketType.PlayerConnect.getId() ||
                            packetId == PacketTypes.PacketType.Login.getId() ||
                            packetId == PacketTypes.PacketType.PlayerUpdateReliable.getId();
                });
            }
        } catch (Exception e) {
            Logger.printLog("Error filtering packets: " + e.getMessage());
        }
    }

    // Call this method periodically to maintain protection
    public void maintain() {
        filterIncomingPackets();

        // Use wrapper to handle player updates
        IsoPlayer player = IsoPlayer.getInstance();
        if (player != null) {
            try {
                // Use reflection to check connection state
                Field connectedField = player.getClass().getDeclaredField("connected");
                connectedField.setAccessible(true);
                boolean connected = (boolean)connectedField.get(player);

                if (connected) {
                    sendFakePlayerUpdate(player);
                }
            } catch (Exception e) {
                Logger.printLog("Error maintaining connection: " + e.getMessage());
            }
        }
    }
}