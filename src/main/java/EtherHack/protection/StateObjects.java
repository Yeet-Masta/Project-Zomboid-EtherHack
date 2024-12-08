package EtherHack.protection;

import zombie.characters.skills.PerkFactory;

import zombie.inventory.InventoryItem;
import zombie.characters.IsoPlayer;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StateObjects {
    public static class SkillState {
        public final PerkFactory.Perk perk;
        public final float xp;
        public final long timestamp;

        public SkillState(PerkFactory.Perk perk, float xp) {
            this.perk = perk;
            this.xp = xp;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static class InventoryState {
        public final InventoryItem item;
        public final int count;
        public final String container;
        public final long timestamp;

        public InventoryState(InventoryItem item, int count, String container) {
            this.item = item;
            this.count = count;
            this.container = container;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public static class PlayerState {
        public final float x;
        public final float y;
        public final float z;
        public final float health;
        public final long timestamp;

        public PlayerState(float x, float y, float z, float health) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.health = health;
            this.timestamp = System.currentTimeMillis();
        }

        public static PlayerState fromPlayer(IsoPlayer player) {
            return new PlayerState(
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    player.getHealth()
            );
        }
    }

    public static class StateChangeValidator {
        private static final Map<String, Long> lastUpdates = new ConcurrentHashMap<>();
        private static final Map<String, Object> lastValues = new ConcurrentHashMap<>();

        public static boolean validateStateChange(String key, Object newValue, long minInterval) {
            Long lastUpdate = lastUpdates.get(key);
            if (lastUpdate != null) {
                long timeSince = System.currentTimeMillis() - lastUpdate;
                if (timeSince < minInterval) {
                    return false;
                }
            }

            Object lastValue = lastValues.get(key);
            if (lastValue != null && lastValue.equals(newValue)) {
                return false;
            }

            lastUpdates.put(key, System.currentTimeMillis());
            lastValues.put(key, newValue);
            return true;
        }

        public static void clearValidation(String key) {
            lastUpdates.remove(key);
            lastValues.remove(key);
        }
    }

    public static class NetworkStateHelper {
        public static ByteBuffer serializeState(Object state) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            if (state instanceof SkillState) {
                serializeSkillState(buffer, (SkillState)state);
            } else if (state instanceof InventoryState) {
                serializeInventoryState(buffer, (InventoryState)state);
            } else if (state instanceof PlayerState) {
                serializePlayerState(buffer, (PlayerState)state);
            }

            buffer.flip();
            return buffer;
        }

        private static void serializeSkillState(ByteBuffer buffer, SkillState state) {
            buffer.putInt(state.perk.index());
            buffer.putFloat(state.xp);
            buffer.putLong(state.timestamp);
        }

        private static void serializeInventoryState(ByteBuffer buffer, InventoryState state) {
            // Add inventory serialization logic
        }

        private static void serializePlayerState(ByteBuffer buffer, PlayerState state) {
            buffer.putFloat(state.x);
            buffer.putFloat(state.y);
            buffer.putFloat(state.z);
            buffer.putFloat(state.health);
            buffer.putLong(state.timestamp);
        }
    }
}