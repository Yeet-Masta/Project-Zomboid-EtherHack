package EtherHack.Ether;

import EtherHack.GameClientWrapper;
import EtherHack.utils.Logger;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.packets.PlayerPacket;
import zombie.network.PacketTypes;
import zombie.network.ZomboidNetData;

import java.nio.ByteBuffer;
import java.util.*;

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
            // Clear incoming network data queue
            if (GameClient.instance != null) {
                GameClient.instance.incomingNetData.clear();
                // Reset connection flags
                setFieldValue(GameClient.instance, "bCoopClientDisconnect", false);
                setFieldValue(GameClient.instance, "bConnected", true);
                setFieldValue(GameClient.instance, "bDone", false);
            }

            // Override player state
            IsoPlayer player = IsoPlayer.getInstance();
            if (player != null) {
                player.setOnlineID(generateSafeID());
                player.setConnected(true);

                // Send a fake player update to maintain connection
                sendFakePlayerUpdate(player);
            }

        } catch (Exception e) {
            Logger.printLog("Failed to install event protection: " + e.getMessage());
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
            if (GameClient.instance != null && !GameClient.instance.incomingNetData.isEmpty()) {
                // Filter out handshake and player info packets
                GameClient.instance.incomingNetData.removeIf(packet -> {
                    if (packet == null) return true;
                    byte packetType = packet.type;
                    // Filter specific packet types that might be used for detection
                    return packetType == PacketTypes.PacketType.ClientConnect.getId() ||
                            packetType == PacketTypes.PacketType.PlayerConnect.getId() ||
                            packetType == PacketTypes.PacketType.Login.getId() ||
                            packetType == PacketTypes.PacketType.PlayerUpdateReliable.getId();
                });
            }
        } catch (Exception e) {
            Logger.printLog("Error filtering packets: " + e.getMessage());
        }
    }

    // Call this method periodically to maintain protection
    public void maintain() {
        filterIncomingPackets();
        // Send periodic updates to maintain connection if needed
        IsoPlayer player = IsoPlayer.getInstance();
        if (player != null && player.isConnected()) {
            sendFakePlayerUpdate(player);
        }
    }
}