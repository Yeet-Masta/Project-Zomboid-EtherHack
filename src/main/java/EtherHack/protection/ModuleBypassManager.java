package EtherHack.protection;

import zombie.network.GameClient;
import zombie.core.network.ByteBufferWriter;
import zombie.characters.IsoPlayer;
import zombie.vehicles.BaseVehicle;
import zombie.inventory.InventoryItem;
import zombie.network.PacketTypes;

import java.util.*;
import java.util.concurrent.*;
import java.security.SecureRandom;

public class ModuleBypassManager {
    private static ModuleBypassManager instance;
    private final Map<String, ModuleBypass> bypasses = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final DelayedActionProcessor delayedProcessor;

    private ModuleBypassManager() {
        initializeBypasses();
        this.delayedProcessor = new DelayedActionProcessor();
    }

    private void initializeBypasses() {
        bypasses.put("ChatModule", new ChatModuleBypass());
        bypasses.put("VehicleCommandsModule", new VehicleModuleBypass());
        bypasses.put("SafeHouseModule", new SafeHouseModuleBypass());
        bypasses.put("ItemSpawnerModule", new ItemSpawnerBypass());
        bypasses.put("SkillModule", new SkillModuleBypass());
        bypasses.put("BrushToolModule", new BrushToolBypass());
    }

    // Chat Module Bypass
    private class ChatModuleBypass implements ModuleBypass {
        private final Map<String, Long> lastMessageTimes = new ConcurrentHashMap<>();
        private final List<String> messageQueue = new ArrayList<>();

        @Override
        public boolean processAction(String action, Map<String, Object> params) {
            if (action.equals("sendMessage")) {
                return handleChatMessage((String)params.get("message"));
            }
            return true;
        }

        private boolean isCommand(String message) {
            return message.startsWith("/") || message.startsWith("!");
        }

        private boolean handleChatMessage(String message) {
            // Split command messages into safe parts
            if (isCommand(message)) {
                String[] parts = splitCommand(message);
                messageQueue.addAll(Arrays.asList(parts));
                delayedProcessor.scheduleMessages(messageQueue);
                return false; // Original message not sent
            }
            return true;
        }

        private String[] splitCommand(String command) {
            // Split potentially detectable commands
            String[] parts = new String[2];
            parts[0] = command.substring(0, command.length() / 2);
            parts[1] = command.substring(command.length() / 2);
            return parts;
        }
    }

    // Vehicle Module Bypass
    private class VehicleModuleBypass implements ModuleBypass {
        private final Map<Integer, VehicleAction> pendingActions = new ConcurrentHashMap<>();

        @Override
        public boolean processAction(String action, Map<String, Object> params) {
            if (action.equals("vehicleModification")) {
                return handleVehicleAction(params);
            }
            return true;
        }

        private boolean handleVehicleAction(Map<String, Object> params) {
            BaseVehicle vehicle = (BaseVehicle)params.get("vehicle");
            String command = (String)params.get("command");

            // Split vehicle modifications into smaller changes
            VehicleAction action = new VehicleAction(vehicle, command);
            pendingActions.put((int) vehicle.getId(), action);

            // Schedule the modifications over time
            delayedProcessor.scheduleVehicleAction(action);
            return false;
        }
    }

    // SafeHouse Module Bypass
    private class SafeHouseModuleBypass implements ModuleBypass {
        @Override
        public boolean processAction(String action, Map<String, Object> params) {
            if (action.equals("safehouseAccess")) {
                return handleSafehouseAccess(params);
            }
            return true;
        }

        private boolean handleSafehouseAccess(Map<String, Object> params) {
            // Create legitimate-looking access patterns
            IsoPlayer player = (IsoPlayer)params.get("player");

            // Queue movements to look natural
            delayedProcessor.scheduleSafehouseAccess(player, params);
            return false;
        }
    }

    // Item Spawner Module Bypass
    private class ItemSpawnerBypass implements ModuleBypass {
        @Override
        public boolean processAction(String action, Map<String, Object> params) {
            if (action.equals("spawnItem")) {
                return handleItemSpawn(params);
            }
            return true;
        }

        private boolean handleItemSpawn(Map<String, Object> params) {
            InventoryItem item = (InventoryItem)params.get("item");

            // Split item spawning into natural-looking events
            delayedProcessor.scheduleItemSpawn(item);
            return false;
        }
    }

    private class SkillModuleBypass implements ModuleBypass {
        @Override
        public boolean processAction(String action, Map<String, Object> params) {
            if (action.equals("addXP")) {
                return handleSkillAction(params);
            }
            return true;
        }

        private boolean handleSkillAction(Map<String, Object> params) {
            // Add skill bypass logic
            delayedProcessor.scheduleSkillAction(params);
            return true;
        }
    }

    private class BrushToolBypass implements ModuleBypass {
        @Override
        public boolean processAction(String action, Map<String, Object> params) {
            if (action.equals("brushAction")) {
                return handleBrushAction(params);
            }
            return true;
        }

        private boolean handleBrushAction(Map<String, Object> params) {
            // Add brush tool bypass logic
            delayedProcessor.scheduleBrushAction(params);
            return true;
        }
    }

    // Helper class to handle delayed actions
    private class DelayedActionProcessor {
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public void scheduleMessages(List<String> messages) {
            long delay = 0;
            for (String message : messages) {
                scheduler.schedule(() -> {
                    sendChatMessage(message);
                }, delay, TimeUnit.MILLISECONDS);
                delay += 500 + random.nextInt(1000); // Random delay between messages
            }
        }

        public void scheduleVehicleAction(VehicleAction action) {
            scheduler.schedule(() -> {
                executeVehicleAction(action);
            }, 100 + random.nextInt(500), TimeUnit.MILLISECONDS);
        }

        public void scheduleSafehouseAccess(IsoPlayer player, Map<String, Object> params) {
            scheduler.schedule(() -> {
                executeSafehouseAccess(player, params);
            }, 200 + random.nextInt(800), TimeUnit.MILLISECONDS);
        }

        public void scheduleItemSpawn(InventoryItem item) {
            scheduler.schedule(() -> {
                executeItemSpawn(item);
            }, 150 + random.nextInt(600), TimeUnit.MILLISECONDS);
        }

        private void sendChatMessage(String message) {
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.ChatMessageFromPlayer.doPacket(writer);
            writer.putUTF(message);
            PacketTypes.PacketType.ChatMessageFromPlayer.send(GameClient.connection);
        }

        private void executeVehicleAction(VehicleAction action) {
            // Execute vehicle modification with natural timing
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.Vehicles.doPacket(writer);
            writer.putShort(action.vehicle.getId());
            writer.putUTF(action.command);
            PacketTypes.PacketType.Vehicles.send(GameClient.connection);
        }

        private void executeSafehouseAccess(IsoPlayer player, Map<String, Object> params) {
            // Execute safehouse access with natural player movement
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncSafehouse.doPacket(writer);
            writer.putShort(player.getOnlineID());
            PacketTypes.PacketType.SyncSafehouse.send(GameClient.connection);
        }

        private void executeItemSpawn(InventoryItem item) {
            // Execute item spawn in a way that looks legitimate
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.AddInventoryItemToContainer.doPacket(writer);
            // Write item data
            PacketTypes.PacketType.AddInventoryItemToContainer.send(GameClient.connection);
        }
        //new shit

        public void scheduleSkillAction(Map<String, Object> params) {
            scheduler.schedule(() -> {
                executeSkillAction(params);
            }, 150 + random.nextInt(600), TimeUnit.MILLISECONDS);
        }

        private void executeSkillAction(Map<String, Object> params) {
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.AddXP.doPacket(writer);
            // Add skill action execution logic
            PacketTypes.PacketType.AddXP.send(GameClient.connection);
        }

        public void scheduleBrushAction(Map<String, Object> params) {
            scheduler.schedule(() -> {
                executeBrushAction(params);
            }, 100 + random.nextInt(500), TimeUnit.MILLISECONDS);
        }

        private void executeBrushAction(Map<String, Object> params) {
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.AddItemToMap.doPacket(writer);
            // Add brush action execution logic
            PacketTypes.PacketType.AddItemToMap.send(GameClient.connection);
        }
    }

    private class VehicleAction {
        BaseVehicle vehicle;
        String command;

        public VehicleAction(BaseVehicle vehicle, String command) {
            this.vehicle = vehicle;
            this.command = command;
        }
    }

    public interface ModuleBypass {
        boolean processAction(String action, Map<String, Object> params);
    }

    public boolean bypassModule(String moduleName, String action, Map<String, Object> params) {
        ModuleBypass bypass = bypasses.get(moduleName);
        return bypass != null && bypass.processAction(action, params);
    }

    public static ModuleBypassManager getInstance() {
        if (instance == null) {
            synchronized (ModuleBypassManager.class) {
                if (instance == null) {
                    instance = new ModuleBypassManager();
                }
            }
        }
        return instance;
    }
}