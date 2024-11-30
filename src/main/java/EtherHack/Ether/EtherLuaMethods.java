package EtherHack.Ether;

import EtherHack.utils.Logger;
import EtherHack.utils.PlayerUtils;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.network.packets.PlayerPacket;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс EtherLuaMethods содержит методы Lua, доступные в игре EtherHack.
 */
public final class EtherLuaMethods {
    public EtherLuaMethods() {
    }

    /**
     * Получение цвета UI зомби
     */
    @LuaMethod(
            name = "getZombieUIColor",
            global = true
    )
    public static Color getZombieUIColor() {
        return  EtherMain.getInstance().etherAPI.zombiesUIColor;
    }

    /**
     * Установка цвета UI зомби
     */
    @LuaMethod(
            name = "setZombieUIColor",
            global = true
    )
    public static void setZombieUIColor(float r, float g, float b) {
        Color color = new Color(r,g, b);
        EtherMain.getInstance().etherAPI.zombiesUIColor = color;
    }

    /**
     * Получение цвета UI машин
     */
    @LuaMethod(
            name = "getVehicleUIColor",
            global = true
    )
    public static Color getVehicleUIColor() {
        return  EtherMain.getInstance().etherAPI.vehiclesUIColor;
    }

    /**
     * Установка цвета UI машин
     */
    @LuaMethod(
            name = "setVehicleUIColor",
            global = true
    )
    public static void setVehicleUIColor(float r, float g, float b) {
        Color color = new Color(r,g, b);
        EtherMain.getInstance().etherAPI.vehiclesUIColor = color;
    }

    /**
     * Получение цвета UI игроков
     */
    @LuaMethod(
            name = "getPlayersUIColor",
            global = true
    )
    public static Color getPlayersUIColor() {
        return  EtherMain.getInstance().etherAPI.playersUIColor;
    }

    /**
     * Установка цвета UI игроков
     */
    @LuaMethod(
            name = "setPlayersUIColor",
            global = true
    )
    public static void setPlayersUIColor(float r, float g, float b) {
        Color color = new Color(r,g, b);
        EtherMain.getInstance().etherAPI.playersUIColor = color;
    }

    /**
     * Установка акцентного цвета интерфейса
     */
    @LuaMethod(
            name = "setAccentUIColor",
            global = true
    )
    public static void setAccentUIColor(float r, float g, float b) {
        Color color = new Color(r,g, b);
        EtherMain.getInstance().etherAPI.mainUIAccentColor = color;
    }

    /**
     * Удаление выбранных настроек
     */
    @LuaMethod(
            name = "deleteConfig",
            global = true
    )
    public static void deleteConfig(String configName) {
        Path configFilePath = Paths.get("EtherHack/config/" + configName + ".properties");

        try {
            Files.deleteIfExists(configFilePath);
        } catch (IOException e) {
            Logger.printLog("The file '" + configName + "' does not exist. Deletion canceled. Exception: " + e.getMessage());
        }
    }

    /**
     * Получение всех конфигурационных файлов
     */
    @LuaMethod(
            name = "getConfigList",
            global = true
    )
    public static ArrayList<String> getConfigList() {
        ArrayList<String> configFiles = new ArrayList<>();

        try {
            Path configFolderPath = Paths.get("EtherHack/config");

            List<Path> fileList = Files.list(configFolderPath)
                    .filter(file -> file.toString().endsWith(".properties"))
                    .toList();

            for(Path filePath: fileList){
                String fileName = filePath.getFileName().toString().replace(".properties","");
                configFiles.add(fileName);
            }
        } catch (IOException e) {
            Logger.printLog("An error occurred while getting the list of config files: " + e);
            return null;
        }

        return configFiles;
    }

    /**
     * Загрузка настроек чита
     */
    @LuaMethod(
            name = "loadConfig",
            global = true
    )
    public static void loadConfig(String configName) {
        EtherMain.getInstance().etherAPI.loadConfig(configName);
    }

    /**
     * Сохранение настроек чита
     */
    @LuaMethod(
            name = "saveConfig",
            global = true
    )
    public static void saveConfig(String configName) {
        EtherMain.getInstance().etherAPI.saveConfig(configName);
    }


    /**
     * Безопасная телепортация по координатам
     */
    @LuaMethod(
            name = "safePlayerTeleport",
            global = true
    )
    public static void safePlayerTeleport(int x, int y) {
        EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = true;

        IsoPlayer player = IsoPlayer.getInstance();

        float z = player.z;

        float deltaX = (float)x - player.x;
        float deltaY = (float)y - player.y;
        float deltaZ = z - player.z;
        float remX = Math.abs(deltaX);
        float remY = Math.abs(deltaY);
        float remZ = Math.abs(deltaZ);
        float maxSpeed = 10.0f;
        float deltaTime = 0.1f;

        while (remX > 0.f || remY > 0.f || remZ > 0.f) {
            float maxMove = maxSpeed * deltaTime;
            float moveX = Math.min(Math.min(remX, maxMove), 1.0f);
            float moveY = Math.min(Math.min(remY, maxMove), 1.0f);
            float moveZ = Math.min(Math.min(remZ, maxMove), 1.0f);

            remX -= moveX;
            remY -= moveY;
            remZ -= moveZ;

            if (deltaX < 0.f) {
                moveX = -moveX;
            }

            if (deltaY < 0.f) {
                moveY = -moveY;
            }

            if (deltaZ < 0.f) {
                moveZ = -moveZ;
            }

            player.setX(player.x + moveX);
            player.setY(player.y + moveY);
            player.setZ(player.z + moveZ);
            player.setLx(player.getX());
            player.setLy(player.getY());
            player.setLz(player.getZ());

            GameClient.instance.sendPlayer(player);

            if (GameClient.connection != null && PlayerPacket.l_send.playerPacket.set(player)) {
                ByteBufferWriter writer = GameClient.connection.startPacket();

                PacketTypes.PacketType.PlayerUpdateReliable.doPacket(writer);
                PlayerPacket.l_send.playerPacket.write(writer);
                PacketTypes.PacketType.PlayerUpdateReliable.send(GameClient.connection);
            }
        }
        EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = false;
    }

    /**
     * Проверка состояния безопасной телепортации у игрока
     */
    @LuaMethod(
            name = "isPlayerInSafeTeleported",
            global = true
    )
    public static boolean isPlayerInSafeTeleported() {
        return EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported;
    }

    /**
     * Изучить все рецепты
     */
    @LuaMethod(
            name = "learnAllRecipes",
            global = true
    )
    public static void learnAllRecipes() {
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) return;

        ArrayList<Recipe> recipesList = ScriptManager.instance.getAllRecipes();
        if (recipesList != null) {
            for (Recipe recipe: recipesList) {
                if (recipe.getOriginalname() == null) continue;

                localPlayer.learnRecipe(recipe.getOriginalname());
            }
        }
    }

    /**
     * Выдача предметов
     * @param itemID название предмета
     * @param amount количество
     */
    @LuaMethod(
            name = "giveItem",
            global = true
    )
    public static void giveItem(InventoryItem itemID, int amount) {
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) return;

        for (int i = 0; i < amount; i++) {
            localPlayer.getInventory().AddItem(itemID);
        }
    }

    /**
     * Выдача предметов
     * @param itemID название предмета
     * @param amount количество
     */
    @LuaMethod(
            name = "giveItem",
            global = true
    )
    public static void giveItem(String itemID, int amount) {
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) return;

        for (int i = 0; i < amount; i++) {
            localPlayer.getInventory().AddItem(itemID);
        }
    }

    /**
     * Получение расстояние между игроками
     * @param player1 первый игрок
     * @param player2 второй игрок
     * @return расстояние между игроками
     */
    @LuaMethod(
            name = "getDistanceBetweenPlayers",
            global = true
    )
    public static float getDistanceBetweenPlayers(IsoPlayer player1, IsoPlayer player2) {
        return PlayerUtils.getDistanceBetweenPlayers(player1, player2);
    }

    /**
     * Состояние режима запрета компляции файлов с подозрительными словами
     */
    @LuaMethod(
            name = "isBlockCompileLuaWithBadWords",
            global = true
    )
    public static boolean isBlockCompileLuaWithBadWords() {
        return EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords;
    }

    /**
     * Переключение состояния режима запрета компляции файлов с подозрительными словами
     */
    @LuaMethod(
            name = "toggleBlockCompileLuaWithBadWords",
            global = true
    )
    public static void toggleBlockCompileLuaWithBadWords(boolean isToggled) {
        EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords = isToggled;
    }

    /**
     * Состояние режима запрета компляции файлов с упоминанием чита
     */
    @LuaMethod(
            name = "isBlockCompileLuaAboutEtherHack",
            global = true
    )
    public static boolean isBlockCompileLuaAboutEtherHack() {
        return EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack;
    }

    /**
     * Переключение состояния режима запрета компляции файлов с упоминанием чита
     */
    @LuaMethod(
            name = "toggleBlockCompileLuaAboutEtherHack",
            global = true
    )
    public static void toggleBlockCompileLuaAboutEtherHack(boolean isToggled) {
        EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack = isToggled;
    }

    /**
     * Состояние режима запрета компляции стандартных файлов - логгеров
     */
    @LuaMethod(
            name = "isBlockCompileDefaultLua",
            global = true
    )
    public static boolean isBlockCompileDefaultLua() {
        return EtherLuaCompiler.getInstance().isBlockCompileDefaultLua;
    }

    /**
     * Переключение состояния режима запрета компляции стандартных файлов - логгеров
     */
    @LuaMethod(
            name = "toggleBlockCompileDefaultLua",
            global = true
    )
    public static void toggleBlockCompileDefaultLua(boolean isToggled) {
        EtherLuaCompiler.getInstance().isBlockCompileDefaultLua = isToggled;
    }

    /**
     * Состояние режима невидимости
     */
    @LuaMethod(
            name = "isEnableInvisible",
            global = true
    )
    public static boolean isEnableInvisible() {
        return EtherMain.getInstance().etherAPI.isEnableInvisible;
    }

    /**
     * Переключение состояния режима невидимости
     */
    @LuaMethod(
            name = "toggleInvisible",
            global = true
    )
    public static void toggleInvisible(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isEnableInvisible = isToggled;
    }

    /**
     * Состояние режима невидимости для зомби
     */
    @LuaMethod(
            name = "isZombieDontAttack",
            global = true
    )
    public static boolean isZombieDontAttack() {
        return EtherMain.getInstance().etherAPI.isZombieDontAttack;
    }

    /**
     * Переключение состояния режима невидимости для зомби
     */
    @LuaMethod(
            name = "toggleZombieDontAttack",
            global = true
    )
    public static void toggleZombieDontAttack(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isZombieDontAttack = isToggled;
    }

    /**
     * Состояние режима неосязаемости
     */
    @LuaMethod(
            name = "isEnableNoclip",
            global = true
    )
    public static boolean isEnableNoclip() {
        return EtherMain.getInstance().etherAPI.isEnableNoclip;
    }

    /**
     * Переключение состояния режима неосязаемости
     */
    @LuaMethod(
            name = "toggleNoclip",
            global = true
    )
    public static void toggleNoclip(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isEnableNoclip = isToggled;
    }

    /**
     * Состояние режима бессмертия
     */
    @LuaMethod(
            name = "isEnableGodMode",
            global = true
    )
    public static boolean isEnableGodMode() {
        return EtherMain.getInstance().etherAPI.isEnableGodMode;
    }

    /**
     * Переключение состояния режима бессмертия
     */
    @LuaMethod(
            name = "toggleGodMode",
            global = true
    )
    public static void toggleGodMode(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isEnableGodMode = isToggled;
    }

    /**
     * Состояние режима ночного зрения
     */
    @LuaMethod(
            name = "isEnableNightVision",
            global = true
    )
    public static boolean isEnableNightVision() {
        return EtherMain.getInstance().etherAPI.isEnableNightVision;
    }

    /**
     * Переключение состояния режима ночного зрения
     */
    @LuaMethod(
            name = "toggleNightVision",
            global = true
    )
    public static void toggleNightVision(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isEnableNightVision = isToggled;
    }

    /**
     * Состояние режима отключения разброса
     */
    @LuaMethod(
            name = "isNoRecoil",
            global = true
    )
    public static boolean isNoRecoil() {
        return EtherMain.getInstance().etherAPI.isNoRecoil;
    }

    /**
     * Переключение состояния режима отключения разброса
     */
    @LuaMethod(
            name = "toggleNoRecoil",
            global = true
    )
    public static void toggleNoRecoil(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isNoRecoil = isToggled;
    }

    /**
     * Состояние режима автопочинки предметов в инвентаре
     */
    @LuaMethod(
            name = "isAutoRepairItems",
            global = true
    )
    public static boolean isAutoRepairItems() {
        return EtherMain.getInstance().etherAPI.isAutoRepairItems;
    }

    /**
     * Переключение состояния режима автопочинки предметов в инвентаре
     */
    @LuaMethod(
            name = "toggleAutoRepairItems",
            global = true
    )
    public static void toggleAutoRepairItems(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isAutoRepairItems = isToggled;
    }

    /**
     * Восстановление всех характеристик оружия в инвентаре
     */
    @LuaMethod(
            name = "resetWeaponsStats",
            global = true
    )
    public static void resetWeaponsStats() {
        EtherMain.getInstance().etherAPI.resetWeaponsStats();
    }

    /**
     * Состояние режима мгновенного убийства
     */
    @LuaMethod(
            name = "isExtraDamage",
            global = true
    )
    public static boolean isExtraDamage() {
        return EtherMain.getInstance().etherAPI.isExtraDamage;
    }

    /**
     * Переключение состояния режима мгновенного убийства
     */
    @LuaMethod(
            name = "toggleExtraDamage",
            global = true
    )
    public static void toggleExtraDamage(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isExtraDamage = isToggled;
    }

    /**
     * Состояние режима быстрый действий
     */
    @LuaMethod(
            name = "isTimedActionCheat",
            global = true
    )
    public static boolean isTimedActionCheat() {
        return EtherMain.getInstance().etherAPI.isTimedActionCheat;
    }

    /**
     * Переключение состояния режима быстрый действий
     */
    @LuaMethod(
            name = "toggleTimedActionCheat",
            global = true
    )
    public static void toggleTimedActionCheat(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isTimedActionCheat = isToggled;
    }

    /**
     * Состояние режима мультиатаки по зомби
     */
    @LuaMethod(
            name = "isMultiHitZombies",
            global = true
    )
    public static boolean isMultiHitZombies() {
        return EtherMain.getInstance().etherAPI.isMultiHitZombies;
    }

    /**
     * Переключение состояния режима мультиатаки по зомби
     */
    @LuaMethod(
            name = "toggleMultiHitZombies",
            global = true
    )
    public static void toggleMultiHitZombies(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isMultiHitZombies = isToggled;
    }

    /**
     * Состояние режима бесконечной прочности в руках
     */
    @LuaMethod(
            name = "isUnlimitedCondition",
            global = true
    )
    public static boolean isUnlimitedCondition() {
        return EtherMain.getInstance().etherAPI.isUnlimitedCondition;
    }

    /**
     * Переключение состояния режима бесконечной прочности в руках
     */
    @LuaMethod(
            name = "toggleUnlimitedCondition",
            global = true
    )
    public static void toggleUnlimitedCondition(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isUnlimitedCondition = isToggled;
    }

     /**
     * Состояние режима отрисовки всех объектов на 360 градусов
     */
    @LuaMethod(
            name = "isVisualEnable360Vision",
            global = true
    )
    public static boolean isVisualEnable360Vision() {
        return EtherMain.getInstance().etherAPI.isVisualEnable360Vision;
    }

    /**
     * Переключение состояния режима отрисовки всех объектов на 360 градусов
     */
    @LuaMethod(
            name = "toggleVisualEnable360Vision",
            global = true
    )
    public static void toggleVisualEnable360Vision(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualEnable360Vision = isToggled;
    }

    /**
     * Состояние режима отрисовки линий до игроков
     */
    @LuaMethod(
            name = "isVisualDrawLineToPlayers",
            global = true
    )
    public static boolean isVisualDrawLineToPlayers() {
        return EtherMain.getInstance().etherAPI.isVisualDrawLineToPlayers;
    }

    /**
     * Переключение состояния режима отрисовки линий до игроков
     */
    @LuaMethod(
            name = "toggleVisualDrawLineToPlayers",
            global = true
    )
    public static void toggleVisualDrawLineToPlayers(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualDrawLineToPlayers = isToggled;
    }

    /**
     * Состояние режима отрисовки линий до транспорта
     */
    @LuaMethod(
            name = "isVisualDrawLineToVehicle",
            global = true
    )
    public static boolean isVisualDrawLineToVehicle() {
        return EtherMain.getInstance().etherAPI.isVisualDrawLineToVehicle;
    }

    /**
     * Переключение состояния режима отрисовки линий до транспорта
     */
    @LuaMethod(
            name = "toggleVisualDrawLineToVehicle",
            global = true
    )
    public static void toggleVisualDrawLineToVehicle(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualDrawLineToVehicle = isToggled;
    }

    /**
     * Состояние режима отрисовки зомби на миникарте
     */
    @LuaMethod(
            name = "isMapDrawZombies",
            global = true
    )
    public static boolean isMapDrawZombies() {
        return EtherMain.getInstance().etherAPI.isMapDrawZombies;
    }

    /**
     * Переключение состояния режима отрисовки зомби на миникарте
     */
    @LuaMethod(
            name = "toggleMapDrawZombies",
            global = true
    )
    public static void toggleMapDrawZombies(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isMapDrawZombies = isToggled;
    }

    /**
     * Состояние режима отрисовки транспорта на миникарте
     */
    @LuaMethod(
            name = "isMapDrawVehicles",
            global = true
    )
    public static boolean isMapDrawVehicles() {
        return EtherMain.getInstance().etherAPI.isMapDrawVehicles;
    }

    /**
     * Переключение состояния режима отрисовки транспорта на миникарте
     */
    @LuaMethod(
            name = "toggleMapDrawVehicles",
            global = true
    )
    public static void toggleMapDrawVehicles(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isMapDrawVehicles = isToggled;
    }

    /**
     * Состояние режима отрисовки всех игроков на миникарте
     */
    @LuaMethod(
            name = "isMapDrawAllPlayers",
            global = true
    )
    public static boolean isMapDrawAllPlayers() {
        return EtherMain.getInstance().etherAPI.isMapDrawAllPlayers;
    }

    /**
     * Переключение состояния режима отрисовки всех игроков на миникарте
     */
    @LuaMethod(
            name = "toggleMapDrawAllPlayers",
            global = true
    )
    public static void toggleMapDrawAllPlayers(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isMapDrawAllPlayers = isToggled;
    }

    /**
     * Состояние режима отрисовки локального игрока на миникарте
     */
    @LuaMethod(
            name = "isMapDrawLocalPlayer",
            global = true
    )
    public static boolean isMapDrawLocalPlayer() {
        return EtherMain.getInstance().etherAPI.isMapDrawLocalPlayer;
    }

    /**
     * Переключение состояния режима отрисовки локального игрока на миникарте
     */
    @LuaMethod(
            name = "toggleMapDrawLocalPlayer",
            global = true
    )
    public static void toggleMapDrawLocalPlayer(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isMapDrawLocalPlayer = isToggled;
    }

    /**
     * Состояние режима отрисовки информации о игроке
     */
    @LuaMethod(
            name = "isVisualDrawPlayerInfo",
            global = true
    )
    public static boolean isVisualDrawPlayerInfo() {
        return EtherMain.getInstance().etherAPI.isVisualDrawPlayerInfo;
    }

    /**
     * Переключение состояния режима отрисовки информации о игроке
     */
    @LuaMethod(
            name = "toggleVisualDrawPlayerInfo",
            global = true
    )
    public static void toggleVisualDrawPlayerInfo(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualDrawPlayerInfo = isToggled;
    }

    /**
     * Состояние режима отрисовки эффектов для зомби
     */
    @LuaMethod(
            name = "isVisualsZombiesEnable",
            global = true
    )
    public static boolean isVisualsZombiesEnable() {
        return EtherMain.getInstance().etherAPI.isVisualsZombiesEnable;
    }

    /**
     * Переключение состояния режима отрисовки эффектов для зомби
     */
    @LuaMethod(
            name = "toggleVisualsZombiesEnable",
            global = true
    )
    public static void toggleVisualsZombiesEnable(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualsZombiesEnable = isToggled;
    }

    /**
     * Состояние режима отрисовки эффектов для транспорта
     */
    @LuaMethod(
            name = "isVisualsVehiclesEnable",
            global = true
    )
    public static boolean isVisualsVehiclesEnable() {
        return EtherMain.getInstance().etherAPI.isVisualsVehiclesEnable;
    }

    /**
     * Переключение состояния режима отрисовки эффектов для транспорта
     */
    @LuaMethod(
            name = "toggleVisualsVehiclesEnable",
            global = true
    )
    public static void toggleVisualsVehiclesEnable(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualsVehiclesEnable = isToggled;
    }

    /**
     * Состояние режима отрисовки эффектов для игроков
     */
    @LuaMethod(
            name = "isVisualsPlayersEnable",
            global = true
    )
    public static boolean isVisualsPlayersEnable() {
        return EtherMain.getInstance().etherAPI.isVisualsPlayersEnable;
    }

    /**
     * Переключение состояния режима отрисовки эффектов для игроков
     */
    @LuaMethod(
            name = "toggleVisualsPlayersEnable",
            global = true
    )
    public static void toggleVisualsPlayersEnable(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualsPlayersEnable = isToggled;
    }

    /**
     * Состояние режима отрисовки информации о чите
     */
    @LuaMethod(
            name = "isVisualDrawCredits",
            global = true
    )
    public static boolean isVisualDrawCredits() {
        return EtherMain.getInstance().etherAPI.isVisualDrawCredits;
    }

    /**
     * Переключение состояния режима отрисовки информации о чите
     */
    @LuaMethod(
            name = "toggleVisualDrawCredits",
            global = true
    )
    public static void toggleVisualDrawCredits(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualDrawCredits = isToggled;
    }

    /**
     * Состояние режима отрисовки ника игрока
     */
    @LuaMethod(
            name = "isVisualDrawPlayerNickname",
            global = true
    )
    public static boolean isVisualDrawPlayerNickname() {
        return EtherMain.getInstance().etherAPI.isVisualDrawPlayerNickname;
    }

    /**
     * Переключение состояния режима отрисовки ника игрока
     */
    @LuaMethod(
            name = "toggleVisualDrawPlayerNickname",
            global = true
    )
    public static void toggleVisualDrawPlayerNickname(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualDrawPlayerNickname = isToggled;
    }

    /**
     * Состояние режима отрисовки визуальных эффектов для локального игрока
     */
    @LuaMethod(
            name = "isVisualDrawToLocalPlayer",
            global = true
    )
    public static boolean isVisualDrawToLocalPlayer() {
        return EtherMain.getInstance().etherAPI.isVisualDrawToLocalPlayer;
    }

    /**
     * Переключение состояния режима отрисовки визуальных эффектов для локального игрока
     */
    @LuaMethod(
            name = "toggleVisualDrawToLocalPlayer",
            global = true
    )
    public static void toggleVisualDrawToLocalPlayer(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualDrawToLocalPlayer = isToggled;
    }

    /**
     * Состояние режима отрисовки визуальных эффектов
     */
    @LuaMethod(
            name = "isVisualsEnable",
            global = true
    )
    public static boolean isVisualsEnable() {
        return EtherMain.getInstance().etherAPI.isVisualsEnable;
    }

    /**
     * Переключение состояния режима отрисовки визуальных эффектов
     */
    @LuaMethod(
            name = "toggleVisualsEnable",
            global = true
    )
    public static void toggleVisualsEnable(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isVisualsEnable = isToggled;
    }

    /**
     * Состояние режима отладки
     */
    @LuaMethod(
            name = "isBypassDebugMode",
            global = true
    )
    public static boolean isBypassDebugMode() {
        return EtherMain.getInstance().etherAPI.isBypassDebugMode;
    }

    /**
     * Переключение состояния режима отладки
     */
    @LuaMethod(
            name = "toggleBypassDebugMode",
            global = true
    )
    public static void toggleBypassDebugMode(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isBypassDebugMode = isToggled;
    }

    /**
     * Переключение состояния бесконечной выносливости
     */
    @LuaMethod(
            name = "toggleUnlimitedEndurance",
            global = true
    )
    public static void toggleUnlimitedEndurance(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isUnlimitedEndurance = isToggled;
    }

    /**
     * Состояние бесконечной выносливости
     */
    @LuaMethod(
            name = "isUnlimitedEndurance",
            global = true
    )
    public static boolean isUnlimitedEndurance() {
        return EtherMain.getInstance().etherAPI.isUnlimitedEndurance;
    }

    /**
     * Переключение состояния бесконечного количества патронов
     */
    @LuaMethod(
            name = "toggleUnlimitedAmmo",
            global = true
    )
    public static void toggleUnlimitedAmmo(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isUnlimitedAmmo = isToggled;
    }

    /**
     * Состояние бесконечного количества патронов
     */
    @LuaMethod(
            name = "isUnlimitedAmmo",
            global = true
    )
    public static boolean isUnlimitedAmmo() {
        return EtherMain.getInstance().etherAPI.isUnlimitedAmmo;
    }

    /**
     * Переключение состояния отключения усталости
     */
    @LuaMethod(
            name = "toggleDisableFatigue",
            global = true
    )
    public static void toggleDisableFatigue(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableFatigue = isToggled;
    }

    /**
     * Состояние отключения усталости
     */
    @LuaMethod(
            name = "isDisableFatigue",
            global = true
    )
    public static boolean isDisableFatigue() {
        return EtherMain.getInstance().etherAPI.isDisableFatigue;
    }

    /**
     * Переключение состояния отключения голода
     */
    @LuaMethod(
            name = "toggleDisableHunger",
            global = true
    )
    public static void toggleDisableHunger(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableHunger = isToggled;
    }

    /**
     * Состояние отключения голода
     */
    @LuaMethod(
            name = "isDisableHunger",
            global = true
    )
    public static boolean isDisableHunger() {
        return EtherMain.getInstance().etherAPI.isDisableHunger;
    }

    /**
     * Переключение состояния отключения жажды
     */
    @LuaMethod(
            name = "toggleDisableThirst",
            global = true
    )
    public static void toggleDisableThirst(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableThirst = isToggled;
    }

    /**
     * Состояние отключения жажды
     */
    @LuaMethod(
            name = "isDisableThirst",
            global = true
    )
    public static boolean isDisableThirst() {
        return EtherMain.getInstance().etherAPI.isDisableThirst;
    }

    /**
     * Переключение состояния отключения опьянения
     */
    @LuaMethod(
            name = "toggleDisableDrunkenness",
            global = true
    )
    public static void toggleDisableDrunkenness(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableDrunkenness = isToggled;
    }

    /**
     * Состояние отключения опьянения
     */
    @LuaMethod(
            name = "isDisableDrunkenness",
            global = true
    )
    public static boolean isDisableDrunkenness() {
        return EtherMain.getInstance().etherAPI.isDisableDrunkenness;
    }

    /**
     * Переключение состояния отключения злости
     */
    @LuaMethod(
            name = "toggleDisableAnger",
            global = true
    )
    public static void toggleDisableAnger(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableAnger = isToggled;
    }

    /**
     * Состояние отключения злости
     */
    @LuaMethod(
            name = "isDisableAnger",
            global = true
    )
    public static boolean isDisableAnger() {
        return EtherMain.getInstance().etherAPI.isDisableAnger;
    }

    /**
     * Переключение состояния отключения страха
     */
    @LuaMethod(
            name = "toggleDisableFear",
            global = true
    )
    public static void toggleDisableFear(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableFear = isToggled;
    }

    /**
     * Состояние отключения страха
     */
    @LuaMethod(
            name = "isDisableFear",
            global = true
    )
    public static boolean isDisableFear() {
        return EtherMain.getInstance().etherAPI.isDisableFear;
    }

    /**
     * Переключение состояния отключения боли
     */
    @LuaMethod(
            name = "toggleDisablePain",
            global = true
    )
    public static void toggleDisablePain(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisablePain = isToggled;
    }

    /**
     * Состояние отключения боли
     */
    @LuaMethod(
            name = "isDisablePain",
            global = true
    )
    public static boolean isDisablePain() {
        return EtherMain.getInstance().etherAPI.isDisablePain;
    }

    /**
     * Переключение состояния отключения паники
     */
    @LuaMethod(
            name = "toggleDisablePanic",
            global = true
    )
    public static void toggleDisablePanic(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisablePanic = isToggled;
    }

    /**
     * Состояние отключения паники
     */
    @LuaMethod(
            name = "isDisablePanic",
            global = true
    )
    public static boolean isDisablePanic() {
        return EtherMain.getInstance().etherAPI.isDisablePanic;
    }

    /**
     * Переключение состояния отключения морали
     */
    @LuaMethod(
            name = "toggleDisableMorale",
            global = true
    )
    public static void toggleDisableMorale(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableMorale = isToggled;
    }

    /**
     * Состояние отключения морали
     */
    @LuaMethod(
            name = "isDisableMorale",
            global = true
    )
    public static boolean isDisableMorale() {
        return EtherMain.getInstance().etherAPI.isDisableMorale;
    }

    /**
     * Переключение состояния отключения стресса
     */
    @LuaMethod(
            name = "toggleDisableStress",
            global = true
    )
    public static void toggleDisableStress(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableStress = isToggled;
    }

    /**
     * Состояние отключения стресса
     */
    @LuaMethod(
            name = "isDisableStress",
            global = true
    )
    public static boolean isDisableStress() {
        return EtherMain.getInstance().etherAPI.isDisableStress;
    }

    /**
     * Переключение состояния отключения болезненности
     */
    @LuaMethod(
            name = "toggleDisableSickness",
            global = true
    )
    public static void toggleDisableSickness(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableSickness = isToggled;
    }

    /**
     * Состояние отключения болезненности
     */
    @LuaMethod(
            name = "isDisableSickness",
            global = true
    )
    public static boolean isDisableSickness() {
        return EtherMain.getInstance().etherAPI.isDisableSickness;
    }

    /**
     * Переключение состояния отключения стресса от сигарет
     */
    @LuaMethod(
            name = "toggleDisableStressFromCigarettes",
            global = true
    )
    public static void toggleDisableStressFromCigarettes(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableStressFromCigarettes = isToggled;
    }

    /**
     * Состояние отключения стресса от сигарет
     */
    @LuaMethod(
            name = "isDisableStressFromCigarettes",
            global = true
    )
    public static boolean isDisableStressFromCigarettes() {
        return EtherMain.getInstance().etherAPI.isDisableStressFromCigarettes;
    }

    /**
     * Переключение состояния отключения невменяемости
     */
    @LuaMethod(
            name = "toggleDisableSanity",
            global = true
    )
    public static void toggleDisableSanity(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableSanity = isToggled;
    }

    /**
     * Состояние отключения невменяемости
     */
    @LuaMethod(
            name = "isDisableSanity",
            global = true
    )
    public static boolean isDisableSanity() {
        return EtherMain.getInstance().etherAPI.isDisableSanity;
    }

    /**
     * Переключение состояния отключения скуки
     */
    @LuaMethod(
            name = "toggleDisableBoredomLevel",
            global = true
    )
    public static void toggleDisableBoredomLevel(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableBoredomLevel = isToggled;
    }

    /**
     * Состояние отключения скуки
     */
    @LuaMethod(
            name = "isDisableBoredomLevel",
            global = true
    )
    public static boolean isDisableBoredomLevel() {
        return EtherMain.getInstance().etherAPI.isDisableBoredomLevel;
    }

    /**
     * Переключение состояния отключения несчастья
     */
    @LuaMethod(
            name = "toggleDisableUnhappynessLevel",
            global = true
    )
    public static void toggleDisableUnhappynessLevel(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableUnhappynessLevel = isToggled;
    }

    /**
     * Состояние отключения несчастья
     */
    @LuaMethod(
            name = "isDisableUnhappynessLevel",
            global = true
    )
    public static boolean isDisableUnhappynessLevel() {
        return EtherMain.getInstance().etherAPI.isDisableUnhappynessLevel;
    }

    /**
     * Переключение состояния отключения промокаемости (условной)
     */
    @LuaMethod(
            name = "toggleDisableWetness",
            global = true
    )
    public static void toggleDisableWetness(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableWetness = isToggled;
    }

    /**
     * Состояние отключения промокаемости (условной)
     */
    @LuaMethod(
            name = "isDisableWetness",
            global = true
    )
    public static boolean isDisableWetness() {
        return EtherMain.getInstance().etherAPI.isDisableWetness;
    }

    /**
     * Переключение состояния отключения инфекции
     */
    @LuaMethod(
            name = "toggleDisableInfectionLevel",
            global = true
    )
    public static void toggleDisableInfectionLevel(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableInfectionLevel = isToggled;
    }

    /**
     * Состояние отключения инфекции
     */
    @LuaMethod(
            name = "isDisableInfectionLevel",
            global = true
    )
    public static boolean isDisableInfectionLevel() {
        return EtherMain.getInstance().etherAPI.isDisableInfectionLevel;
    }

    /**
     * Переключение состояния отключения фальшивой инфекции
     */
    @LuaMethod(
            name = "toggleDisableFakeInfectionLevel",
            global = true
    )
    public static void toggleDisableFakeInfectionLevel(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isDisableFakeInfectionLevel = isToggled;
    }

    /**
     * Состояние отключения фальшивой инфекции
     */
    @LuaMethod(
            name = "isDisableFakeInfectionLevel",
            global = true
    )
    public static boolean isDisableFakeInfectionLevel() {
        return EtherMain.getInstance().etherAPI.isDisableFakeInfectionLevel;
    }

    /**
     * Переключение состояния поддержания оптимальных калорий
     */
    @LuaMethod(
            name = "toggleOptimalCalories",
            global = true
    )
    public static void toggleOptimalCalories(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isOptimalCalories = isToggled;
    }

    /**
     * Состояние поддержания оптимальных калорий
     */
    @LuaMethod(
            name = "isOptimalCalories",
            global = true
    )
    public static boolean isOptimalCalories() {
        return EtherMain.getInstance().etherAPI.isOptimalCalories;
    }

    /**
     * Переключение состояния поддержания оптимального веса
     */
    @LuaMethod(
            name = "toggleOptimalWeight",
            global = true
    )
    public static void toggleOptimalWeight(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isOptimalWeight = isToggled;
    }

    /**
     * Состояние поддержания оптимального веса
     */
    @LuaMethod(
            name = "isOptimalWeight",
            global = true
    )
    public static boolean isOptimalWeight() {
        return EtherMain.getInstance().etherAPI.isOptimalWeight;
    }

    /**
     * Переключение состояния бесконечной грузоподъемности
     */
    @LuaMethod(
            name = "toggleEnableUnlimitedCarry",
            global = true
    )
    public static void toggleEnableUnlimitedCarry(boolean isToggled) {
        EtherMain.getInstance().etherAPI.isUnlimitedCarry = isToggled;
    }

    /**
     * Состояние бесконечной грузоподъемности
     */
    @LuaMethod(
            name = "isEnableUnlimitedCarry",
            global = true
    )
    public static boolean isEnableUnlimitedCarry() {
        return EtherMain.getInstance().etherAPI.isUnlimitedCarry;
    }


    /**
     * Получает состояние античита тип 12
     */
    @LuaMethod(
            name = "getAntiCheat12Status",
            global = true
    )
    public static boolean getAntiCheat12Status() {
        return ServerOptions.instance.getBoolean("AntiCheatProtectionType12");
    }

    /**
     * Получает состояние античита тип 8
     */
    @LuaMethod(
            name = "getAntiCheat8Status",
            global = true
    )
    public static boolean getAntiCheat8Status() {
        return ServerOptions.instance.getBoolean("AntiCheatProtectionType8");
    }

    /**
     * Подключает и запускает указанный Lua-файл в контексте игры.
     * @param path путь к Lua-файлу
     */
    @LuaMethod(
            name = "requireExtra",
            global = true
    )
    public static void requireExtra(String path) {
        String fixedPath = path.endsWith(".lua") ? path : path + ".lua";

        if (!EtherMain.getInstance().etherLuaManager.luaFilesList.contains(fixedPath)) {
            EtherMain.getInstance().etherLuaManager.luaFilesList.add(fixedPath);
        }

        Path p = Paths.get(fixedPath);
        String filename = p.getFileName().toString();
        filename = filename.substring(0, filename.lastIndexOf("."));

        EtherLuaCompiler.getInstance().addWordToBlacklistLuaCompiler(filename);
        EtherLuaCompiler.getInstance().addPathToWhiteListLuaCompiler(fixedPath);

        LuaManager.RunLua(fixedPath);
    }

    /**
     * Получение текстуры из заданного пути к изображению.
     * @param relativePath Относительный путь к файлу изображения.
     * @return Текстура, созданная из файла изображения, или null, если произошло исключение.
     */
    @LuaMethod(
            name = "getExtraTexture",
            global = true
    )
    public static Texture getExtraTexture(String relativePath) {
        if(!relativePath.endsWith(".png")) {
            Logger.printLog("Incorrect path to the image file. Required .png");
            return null;
        }

        HashMap<String, Texture> textureCache = EtherMain.getInstance().etherAPI.textureCache;

        // Если текстура уже загружена, возвращаем ее из кэша
        if (textureCache.containsKey(relativePath)) {
            return textureCache.get(relativePath);
        }

        // В противном случае загружаем текстуру и сохраняем в кэше
        try {
            FileInputStream fis = new FileInputStream(Paths.get(relativePath).toFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            Texture texture = new Texture(relativePath, bis, false);
            textureCache.put(relativePath, texture);  // Добавляем текстуру в кэш
            return texture;
        } catch (Exception e) {
            Logger.printLog("Error when reading the image: " + e);
            return null;
        }
    }

    /**
     * Получает перевод для указанного ключа с подстановкой аргументов.
     * @param key       ключ перевода
     * @param args      аргументы для подстановки
     * @return          перевод с подстановкой аргументов
     */
    @LuaMethod(
            name = "getTranslate",
            global = true
    )
    public static String getTranslate(String key, KahluaTable args) {
        return EtherMain.getInstance().etherTranslator.getTranslate(key, args);
    }

    /**
     * Получает перевод для указанного ключа.
     * @param key       ключ перевода
     * @return          перевод
     */
    @LuaMethod(
            name = "getTranslate",
            global = true
    )
    public static String getTranslate(String key) {
        return EtherMain.getInstance().etherTranslator.getTranslate(key);
    }

    /**
     * Устанавливает доступ администратора для локального игрока.
     */
    @LuaMethod(
            name = "hackAdminAccess",
            global = true
    )
    public static void hackAdminAccess() {
        for (IsoPlayer p : GameClient.instance.getPlayers()) {
            if (p.isLocalPlayer()) {
                p.accessLevel = "admin";
                p.accessLevel.equals("admin");
            }
        }
    }

    /**
     * Получение акцентного цвета интерфейса
     */
    @LuaMethod(
            name = "getAccentUIColor",
            global = true
    )
    public static Color getAccentUIColor() {
        return  EtherMain.getInstance().etherAPI.mainUIAccentColor;
    }
}
