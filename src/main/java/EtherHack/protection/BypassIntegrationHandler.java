package EtherHack.protection;

import EtherHack.utils.Logger;
import EtherHack.Ether.EnhancedSafeAPI;
import zombie.network.GameClient;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;

import java.lang.reflect.Field;
import java.util.Map;

public class BypassIntegrationHandler {
    private final ModuleBypassManager bypassManager;
    private final PacketTransformManager packetManager;
    private final EnhancedSafeAPI safeAPI;

    public BypassIntegrationHandler() {
        this.bypassManager =    ModuleBypassManager.getInstance();
        this.packetManager = PacketTransformManager.getInstance();
        this.safeAPI = EnhancedSafeAPI.getInstance();
    }

    public boolean handleAction(String moduleName, String action, Map<String, Object> params) {
        // First try module-specific bypass
        if (bypassManager.bypassModule(moduleName, action, params)) {
            return true;
        }

        // If bypass fails, try packet transformation
        return transformAndSendPacket(moduleName, action, params);
    }

    private boolean transformAndSendPacket(String moduleName, String action, Map<String, Object> params) {
        // Transform the packet
        ByteBufferWriter writer = GameClient.connection.startPacket();

        // Get correct packet type based on module and action
        PacketTypes.PacketType packetType = getPacketTypeForAction(moduleName, action);
        if (packetType != null) {
            packetType.doPacket(writer);

            // Apply module-specific transformations
            if (safeAPI.handleModuleAction(moduleName, action, params)) {
                // Write transformed parameters
                writePacketParams(writer, params);

                // Send through packet manager for final transformation
                packetManager.transformAndQueuePacket(packetType, writer.bb, GameClient.connection);
                return true;
            }
        }

        return false;
    }

    private void writePacketParams(ByteBufferWriter writer, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof String) {
                writer.putUTF((String)entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                writer.putInt((Integer)entry.getValue());
            } else if (entry.getValue() instanceof Short) {
                writer.putShort((Short)entry.getValue());
            } else if (entry.getValue() instanceof Byte) {
                writer.putByte((Byte)entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                writer.putFloat((Float)entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                writer.putByte((byte)((Boolean)entry.getValue() ? 1 : 0));
            }
        }
    }

    private PacketTypes.PacketType getPacketTypeForAction(String moduleName, String action) {
        switch (moduleName) {
            case "ChatModule":
                return PacketTypes.PacketType.ChatMessageFromPlayer;
            case "VehicleCommandsModule":
                return PacketTypes.PacketType.Vehicles;
            case "SafeHouseModule":
                return PacketTypes.PacketType.SyncSafehouse;
            case "ItemSpawnerModule":
                return PacketTypes.PacketType.AddInventoryItemToContainer;
            case "SkillModule":
                return PacketTypes.PacketType.AddXP;
            case "BrushToolModule":
                return PacketTypes.PacketType.AddItemToMap;
            default:
                return null;
        }
    }

    public static void installBypass() {
        BypassIntegrationHandler handler = new BypassIntegrationHandler();

        // Install hooks into GameClient
        try {
            Field bypassField = GameClient.class.getDeclaredField("bypassHandler");
            bypassField.setAccessible(true);
            bypassField.set(GameClient.instance, handler);
        } catch (Exception e) {
            Logger.printLog("Failed to install bypass handler: " + e.getMessage());
        }
    }
}