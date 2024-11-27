package EtherHack.Ether;

import EtherHack.utils.Logger;

import java.nio.ByteBuffer;
import java.util.concurrent.*;
import java.security.SecureRandom;
import java.util.*;

import zombie.GameWindow;
import zombie.core.network.ByteBufferWriter;
import zombie.network.PacketTypes;
import zombie.network.GameClient;

public class ProtectionManagerX {
    private static ProtectionManagerX instance;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, String> virtualGlobals = new ConcurrentHashMap<>();
    private final Map<String, Object> realFunctions = new ConcurrentHashMap<>();
    private final Set<String> protectedNames = new ConcurrentSkipListSet<>();
    private static final String MODULE_ID = "EtherHammerX";
    private final Map<String, Long> keyTimestamps = new ConcurrentHashMap<>();
    private final Map<String, String> responseCache = new ConcurrentHashMap<>();
    private String currentKey;
    private String pendingKey;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ProtectionManagerX() {
        initializeProtection();
    }

    void initializeProtection() {
        // Store real functions securely
        storeFunctions();
        // Initialize fake global table
        createVirtualGlobals();
        // Start key rotation
        startKeyRotation();
    }

    private void storeFunctions() {
        for (String funcName : Arrays.asList(
                "getAntiCheat8Status",
                "getAntiCheat12Status",
                "getExtraTexture",
                "hackAdminAccess",
                // ... other functions ...
                "safePlayerTeleport"
        )) {
            String obfuscatedName = generateObfuscatedName();
            protectedNames.add(obfuscatedName);
            realFunctions.put(obfuscatedName, createProxyFunction(funcName));
        }
    }

    private String generateObfuscatedName() {
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        return "_" + Base64.getEncoder().encodeToString(randomBytes).substring(0, 8);
    }

    // Create virtual _G table for Lua
    private void createVirtualGlobals() {
        for (String name : protectedNames) {
            virtualGlobals.put(generateObfuscatedName(), "function() end");
        }
    }

    // Handle key rotation and packet encryption
    private void startKeyRotation() {
        scheduler.scheduleAtFixedRate(() -> {
            currentKey = generateNewKey();
            pendingKey = generateNewKey();
        }, 0, 10, TimeUnit.SECONDS);
    }

    private String generateNewKey() {
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public Object invokeFunction(String name, Object... args) {
        String realName = protectedNames.stream()
                .filter(n -> n.endsWith(name))
                .findFirst()
                .orElse(null);

        if (realName != null) {
            ProxyFunction func = (ProxyFunction)realFunctions.get(realName);
            if (func != null) {
                return func.invoke(args);
            }
        }
        return null;
    }

    // Proxy interface for function calls
    interface ProxyFunction {
        Object invoke(Object... args);
    }

    private ProxyFunction createProxyFunction(String name) {
        return (args) -> {
            // Add dynamic validation and obfuscation here
            String validationKey = generateValidationKey();
            // Execute real function through proxy
            return executeSecurely(name, validationKey, args);
        };
    }

    private String generateValidationKey() {
        return UUID.randomUUID().toString();
    }

    private Object executeSecurely(String name, String validationKey, Object... args) {
        // Add security checks and validation here
        return null; // Replace with actual execution
    }

    // Method to handle EtherHammerX packets
    public void handlePacket(String command, Map<String, Object> data) {
        switch(command) {
            case "join_request":
                handleJoinRequest(data);
                break;
            case "heartbeat_request":
                handleHeartbeatRequest(data);
                break;
        }
    }

    private String generateFakeResponse(String serverFragment) {
        // Generate believable but fake response
        return UUID.randomUUID().toString();
    }

    public synchronized static ProtectionManagerX getInstance() {
        if (instance == null) {
            instance = new ProtectionManagerX();
        }
        return instance;
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private String generateHeartbeatResponse() {
        String username = GameClient.username;
        if (username == null) return null;

        long timestamp = System.currentTimeMillis();
        String fragment = String.format("%d_%s_%s",
                timestamp,
                generateSafeHash(username),
                generateSafeHash(currentKey)
        );

        // Cache the response for verification
        responseCache.put(currentKey, fragment);
        keyTimestamps.put(currentKey, timestamp);

        // Clean old cache entries
        cleanResponseCache();

        return fragment;
    }

    private String generateSafeHash(String input) {
        // Generate a hash that looks valid but contains manipulated data
        long hash = 0;
        for (byte b : input.getBytes()) {
            hash = ((hash << 5) - hash) + b;
        }
        return Long.toHexString(hash);
    }

    private void cleanResponseCache() {
        long now = System.currentTimeMillis();
        keyTimestamps.entrySet().removeIf(entry ->
                now - entry.getValue() > TimeUnit.MINUTES.toMillis(5)
        );
        responseCache.keySet().removeIf(key ->
                !keyTimestamps.containsKey(key)
        );
    }

    public void handleNetworkPacket(String command, Map<String, Object> data) {
        try {
            switch(command) {
                case "join_request":
                    handleJoinRequest(data);
                    break;
                case "heartbeat_request":
                    handleHeartbeatRequest(data);
                    break;
                case "key_rotation":
                    handleKeyRotation(data);
                    break;
                default:
                    Logger.printLog("Unknown command received: " + command);
            }
        } catch (Exception e) {
            Logger.printLog("Error handling network packet: " + e.getMessage());
        }
    }

    private void handleJoinRequest(Map<String, Object> data) {
        String serverFragment = (String)data.get("message");
        String username = GameClient.username;
        if (username == null) return;

        // Generate response that looks valid
        String fragment2 = generateSafeHash(username + System.currentTimeMillis());

        // Send response packet
        sendPacket("join_response", new HashMap<String, Object>() {{
            put("message", fragment2);
            put("fragment", serverFragment); // Echo server fragment
            put("timestamp", System.currentTimeMillis());
        }});
    }

    private void handleHeartbeatRequest(Map<String, Object> data) {
        String response = generateHeartbeatResponse();
        if (response == null) return;

        // Send heartbeat response packet
        sendPacket("heartbeat_response", new HashMap<String, Object>() {{
            put("message", response);
            put("key", currentKey);
            put("timestamp", System.currentTimeMillis());
        }});
    }

    private void handleKeyRotation(Map<String, Object> data) {
        // Handle key rotation from server
        String newKey = (String)data.get("key");
        if (newKey != null && validKey(newKey)) {
            pendingKey = newKey;
        }
    }

    private boolean validKey(String key) {
        // Add validation logic here
        return key != null && key.length() >= 32;
    }

    private void sendPacket(String command, Map<String, Object> data) {
        try {
            if (GameClient.connection == null) return;

            // Start ModData packet
            ByteBufferWriter writer = GameClient.connection.startPacket();
            PacketTypes.PacketType.PlayerUpdateReliable.doPacket(writer);

            // Write module ID and command
            writer.putUTF(MODULE_ID);  // Module ID (EtherHammerX)
            writer.putUTF(command);    // Command name

            // Write data map
            writer.putInt(data.size()); // Number of entries
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                writer.putUTF(entry.getKey());

                Object value = entry.getValue();
                if (value instanceof String) {
                    writer.putByte((byte)0);
                    writer.putUTF((String)value);
                }
                else if (value instanceof Long) {
                    writer.putByte((byte)1);
                    writer.putLong((Long)value);
                }
                else if (value instanceof Integer) {
                    writer.putByte((byte)2);
                    writer.putInt((Integer)value);
                }
                else if (value instanceof Float) {
                    writer.putByte((byte)3);
                    writer.putFloat((Float)value);
                }
                else if (value instanceof Boolean) {
                    writer.putByte((byte)4);
                    writer.putBoolean((Boolean)value);
                }
            }

            // Send the packet
            PacketTypes.PacketType.PlayerUpdate.send(GameClient.connection);

        } catch (Exception e) {
            Logger.printLog("Error sending packet: " + e.getMessage());
        }
    }

    // Helper method to read data from incoming packets
    private Map<String, Object> readPacketData(ByteBuffer buffer) {
        Map<String, Object> data = new HashMap<>();
        try {
            int entries = buffer.getInt();
            for (int i = 0; i < entries; i++) {
                String key = GameWindow.ReadStringUTF(buffer);
                byte type = buffer.get();

                Object value;
                switch (type) {
                    case 0: // String
                        value = GameWindow.ReadStringUTF(buffer);
                        break;
                    case 1: // Long
                        value = buffer.getLong();
                        break;
                    case 2: // Integer
                        value = buffer.getInt();
                        break;
                    case 3: // Float
                        value = buffer.getFloat();
                        break;
                    case 4: // Boolean
                        value = buffer.get() != 0;
                        break;
                    default:
                        continue;
                }

                data.put(key, value);
            }
        } catch (Exception e) {
            Logger.printLog("Error reading packet data: " + e.getMessage());
        }
        return data;
    }

    public void processIncomingPacket(String module, String command, ByteBuffer data) {
        if (!module.equals("EtherHammerX")) return;

        try {
            Map<String, Object> packetData = readPacketData(data);
            handleNetworkPacket(command, packetData);
        } catch (Exception e) {
            Logger.printLog("Error processing incoming packet: " + e.getMessage());
        }
    }

    private ByteBuffer mapToBuffer(Map<String, Object> data) {
        // Convert map to byte buffer for network transmission
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4096); // Adjust size as needed
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                buffer.putInt(entry.getKey().length());
                buffer.put(entry.getKey().getBytes());

                Object value = entry.getValue();
                if (value instanceof String) {
                    buffer.put((byte)0);
                    buffer.putInt(((String)value).length());
                    buffer.put(((String)value).getBytes());
                } else if (value instanceof Long) {
                    buffer.put((byte)1);
                    buffer.putLong((Long)value);
                }
                // Add other types as needed
            }
            buffer.flip();
            return buffer;
        } catch (Exception e) {
            Logger.printLog("Error converting map to buffer: " + e.getMessage());
            return ByteBuffer.allocate(0);
        }
    }

}