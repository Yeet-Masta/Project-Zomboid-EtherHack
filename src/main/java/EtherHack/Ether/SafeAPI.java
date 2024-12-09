package EtherHack.Ether;

import zombie.characters.IsoPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SafeAPI - Core protection system for EtherHack
 * Manages function obfuscation, proxying, and dynamic generation
 */
public class SafeAPI {
    private static SafeAPI instance;
    private final Map<String, String> obfuscatedNames = new ConcurrentHashMap<>();
    private final Map<String, Long> methodTimestamps = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final Map<String, String> currentKeys = new ConcurrentHashMap<>();

    // Time in ms before a method name is rotated
    private static final long METHOD_LIFETIME = 10_000; // 30 seconds

    private SafeAPI() {
        initializeObfuscation();
    }

    public static SafeAPI getInstance() {
        if (instance == null) {
            instance = new SafeAPI();
        }
        return instance;
    }

    // Initialize obfuscated names for all protected methods
    private void initializeObfuscation() {
        String[] protectedMethods = {
                "getAntiCheat8Status",
                "getAntiCheat12Status",
                "getExtraTexture",
                "hackAdminAccess",
                "isDisableFakeInfectionLevel",
                "isDisableInfectionLevel",
                "isDisableWetness",
                "isEnableUnlimitedCarry",
                "isOptimalWeight",
                "isOptimalCalories",
                "isPlayerInSafeTeleported",
                "learnAllRecipes",
                "requireExtra",
                "safePlayerTeleport",
                "toggleEnableUnlimitedCarry",
                "toggleOptimalWeight",
                "toggleOptimalCalories",
                "vehicle",
                "toggleDisableFakeInfectionLevel",
                "toggleDisableInfectionLevel",
                "toggleDisableWetness",
                "repairPart",
                "setPartCondition",
                "repair",
                "setContainerContentAmount",
                "instanceof"
                // Add other methods that need protection
        };

        for (String method : protectedMethods) {
            obfuscatedNames.put(method, generateSafeName());
            methodTimestamps.put(method, System.currentTimeMillis());
        }
    }

    // Generate a safe random name
    private String generateSafeName() {
        // Make names look like standard Lua functions
        String[] prefixes = {"_fn_", "lua_", "game_", "core_"};
        String prefix = prefixes[random.nextInt(prefixes.length)];
        return prefix + UUID.randomUUID().toString().substring(0, 8);
    }

    public String generateResponseKey(String serverFragment) {
        String username = IsoPlayer.getInstance().getUsername();
        String baseKey = "EtherHammerX_" + username;
        String clientFragment = UUID.randomUUID().toString();
        String fullKey = baseKey + serverFragment + clientFragment;

        // Store for verification
        currentKeys.put(username, fullKey);

        return clientFragment;
    }

    public boolean verifyHeartbeat(String key) {
        String username = IsoPlayer.getInstance().getUsername();
        String currentKey = currentKeys.get(username);
        return currentKey != null && currentKey.equals(key);
    }

    // Get safe name for a method, rotating if necessary
    public String getSafeName(String originalName) {
        Long timestamp = methodTimestamps.get(originalName);
        if (timestamp == null || System.currentTimeMillis() - timestamp > METHOD_LIFETIME) {
            String newName = generateSafeName();
            obfuscatedNames.put(originalName, newName);
            methodTimestamps.put(originalName, System.currentTimeMillis());
            return newName;
        }
        return obfuscatedNames.get(originalName);
    }

    // Get original name from safe name
    public String getOriginalName(String safeName) {
        for (Map.Entry<String, String> entry : obfuscatedNames.entrySet()) {
            if (entry.getValue().equals(safeName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Clean global table for handshake
    public List<String> cleanGlobalTable(List<String> globals) {
        Set<String> protected_names = new HashSet<>(obfuscatedNames.values());
        return globals.stream()
                .filter(name -> !protected_names.contains(name))
                .collect(java.util.stream.Collectors.toList());
    }

    // Generate a unique key for method verification
    public String generateVerificationKey() {
        byte[] keyBytes = new byte[16];
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}