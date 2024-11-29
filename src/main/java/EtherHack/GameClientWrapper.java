package EtherHack;

import EtherHack.utils.Logger;
import zombie.network.GameClient;
import zombie.network.ZomboidNetData;
import zombie.characters.IsoPlayer;
import se.krka.kahlua.vm.KahluaTable;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Wrapper for GameClient that exposes private methods publicly for compilation
 */
public class GameClientWrapper {
    private final GameClient gameClient;

    public GameClientWrapper(GameClient client) {
        this.gameClient = client;
    }

    // Wrap the private methods we need
    public boolean gameLoadingDealWithNetData(ZomboidNetData data) {
        try {
            return (boolean)getMethod("gameLoadingDealWithNetData").invoke(gameClient, data);
        } catch (Exception e) {
            return false;
        }
    }

    public void mainLoopDealWithNetData(ZomboidNetData data) {
        try {
            getMethod("mainLoopDealWithNetData").invoke(gameClient, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mainLoopHandlePacketInternal(ZomboidNetData data, ByteBuffer buffer) {
        try {
            getMethod("mainLoopHandlePacketInternal").invoke(gameClient, data, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delayPacket(int x, int y, int z) {
        try {
            getMethod("delayPacket").invoke(gameClient, x, y, z);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IsoPlayer getPlayerFromUsername(String username) {
        try {
            return (IsoPlayer)getMethod("getPlayerFromUsername").invoke(gameClient, username);
        } catch (Exception e) {
            return null;
        }
    }

    // Add other method wrappers as needed...

    // Helper method to get private methods using reflection
    private java.lang.reflect.Method getMethod(String name, Class<?>... paramTypes) throws Exception {
        java.lang.reflect.Method method = GameClient.class.getDeclaredMethod(name, paramTypes);
        method.setAccessible(true);
        return method;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<ZomboidNetData> getIncomingNetData() {
        try {
            // Get the ArrayList field directly
            Field field = GameClient.class.getDeclaredField("incomingNetData");
            field.setAccessible(true);
            return (ArrayList<ZomboidNetData>) field.get(GameClient.instance);
        } catch (Exception e) {
            Logger.printLog("Failed to access incomingNetData: " + e.getMessage());
            return new ArrayList<>(); // Return empty list as fallback
        }
    }

    public void clearIncomingNetData() {
        ArrayList<ZomboidNetData> netData = getIncomingNetData();
        if (netData != null) {
            netData.clear();
        }
    }

    // Static helper to get instance
    public static GameClientWrapper get() {
        return new GameClientWrapper(GameClient.instance);
    }
}

