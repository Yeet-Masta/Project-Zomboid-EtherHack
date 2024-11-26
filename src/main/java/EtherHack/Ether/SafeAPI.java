package EtherHack.Ether;

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

    // Time in ms before a method name is rotated
    private static final long METHOD_LIFETIME = 30_000; // 30 seconds

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
                "toggleDisableFakeInfectionLevel",
                "toggleDisableInfectionLevel",
                "toggleDisableWetness",
                //"instanceof" [DEBUG]
                // Add other methods that need protection
        };

        for (String method : protectedMethods) {
            obfuscatedNames.put(method, generateSafeName());
            methodTimestamps.put(method, System.currentTimeMillis());
        }
    }

    // Generate a safe random name
    private String generateSafeName() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder name = new StringBuilder("_");

        // Add random letters (5-10 characters)
        int length = 5 + random.nextInt(6);
        for (int i = 0; i < length; i++) {
            name.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        // Add timestamp component for uniqueness
        name.append("_").append(System.nanoTime() % 100000);

        return name.toString();
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
        List<String> cleanGlobals = new ArrayList<>();
        for (String global : globals) {
            if (!obfuscatedNames.containsValue(global)) {
                cleanGlobals.add(global);
            }
        }
        return cleanGlobals;
    }

    // Generate a unique key for method verification
    public String generateVerificationKey() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuilder key = new StringBuilder();
        for (byte b : bytes) {
            key.append(String.format("%02x", b));
        }
        return key.toString();
    }
}