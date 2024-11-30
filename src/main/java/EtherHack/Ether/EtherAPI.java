package EtherHack.Ether;

import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.*;
import zombie.Lua.LuaManager;
import zombie.SandboxOptions;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Color;
import zombie.core.Core;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.core.textures.Texture;
import zombie.ui.UIFont;
import zombie.vehicles.BaseVehicle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Класс EtherAPI предоставляет API для взаимодействия с игрой EtherHack.
 */
public class EtherAPI {

    private Exposer exposer;
    private final EtherLuaMethods etherLuaMethods = new EtherLuaMethods();
    public HashMap<String, Texture> textureCache = new HashMap<>(); // Кэш пользовательских текстур
    HashMap<String, float[]> originalWeaponStats = new HashMap<>(); // Изначальные характеристики оружия

    public Color mainUIAccentColor; // Акцентный цвет интерфейса
    public Color vehiclesUIColor; // Цвет для всех UI элементов машин
    public Color zombiesUIColor; // Цвет для всех UI элементов зомби
    public Color playersUIColor; // Цвет для всех UI элементов игроков

    public boolean isPlayerInSafeTeleported; // Игрок в режиме телепортации
    public boolean isMultiHitZombies; // Мультиатака по зомби
    public boolean isExtraDamage; // Режим мгновенного убийства
    public boolean isTimedActionCheat; // Быстрое выполнение действий
    public boolean isEnableGodMode; // Режим бессмертия
    public boolean isEnableNoclip; // Режим неосязаемости
    public boolean isEnableInvisible; // Режим невидимости
    public boolean isEnableNightVision; // Ночное зрение
    public boolean isZombieDontAttack; // Зомби игнорируют игрока
    public boolean isNoRecoil; // Отключение разброса
    public boolean isBypassDebugMode; // Режим отладки
    public boolean isUnlimitedCarry; // Бесконечная грузоподъемность
    public boolean isUnlimitedCondition; // Бесконечная прочность предмета в руках
    public boolean isUnlimitedEndurance; // Бесконечная выносливость
    public boolean isUnlimitedAmmo; // Бесконечная выносливость
    public boolean isAutoRepairItems; // Автопочинка предметов в инвентаре
    public boolean isDisableFatigue; // Отключение усталости
    public boolean isDisableHunger; // Отключение голода
    public boolean isDisableThirst; // Отключение жажды
    public boolean isDisableDrunkenness; // Отключение опьянения
    public boolean isDisableAnger; // Отключение злости
    public boolean isDisableFear; // Отключение страха
    public boolean isDisablePain; // Отключение боли
    public boolean isDisablePanic; // Отключение паники
    public boolean isDisableMorale; // Отключение морали
    public boolean isDisableStress; // Отключение стресса
    public boolean isDisableSickness; // Отключение болезненности
    public boolean isDisableStressFromCigarettes; // Отключение стресса от сигарет
    public boolean isDisableSanity; // Отключение невменяемости
    public boolean isDisableBoredomLevel; // Отключение скуки
    public boolean isDisableUnhappynessLevel; // Отключение несчастья
    public boolean isDisableWetness; // Отключение промокаемости (условной)
    public boolean isDisableInfectionLevel; // Отключение инфекции
    public boolean isDisableFakeInfectionLevel; // Отключение фальшивой инфекции
    public boolean isOptimalCalories; // Поддержание оптимальных калорий
    public boolean isOptimalWeight; // Поддержание оптимального веса


    public boolean isVisualsEnable; // Включение обработки визуальных эффектов
    public boolean isVisualsPlayersEnable; // Включение обработки визуальных эффектов для игроков
    public boolean isVisualsVehiclesEnable; // Включение обработки визуальных эффектов для транспорта
    public boolean isVisualsZombiesEnable; // Включение обработки визуальных эффектов для зомби
    public boolean isVisualDrawToLocalPlayer; // Рисование визуальных эффектов для локального игрока
    public boolean isVisualDrawPlayerNickname; // Рисование ника игрока
    public boolean isVisualDrawCredits; // Рисование информации о чите
    public boolean isVisualDrawPlayerInfo; // Рисование информации о игроке
    public boolean isVisualDrawLineToVehicle; // Рисование линий до транспорта
    public boolean isVisualDrawLineToPlayers; // Рисование линий до игроков
    public boolean isVisualEnable360Vision; // Отрисовка всех предметов на 360 градусов


    public boolean isMapDrawLocalPlayer; // Рисование на миникарте локального игрока
    public boolean isMapDrawAllPlayers; // Рисование на миникарте всех игроков
    public boolean isMapDrawVehicles; // Рисование на миникарте транспорт
    public boolean isMapDrawZombies; // Рисование на миникарте зомби

    /**
     * Сохранение конфигурационного файла
     * @param configFileName название файла настроек
     */
    public void saveConfig(String configFileName){
        String fixedFileName = "EtherHack/config/"  + configFileName + ".properties";

        Properties config = new Properties();

        config.setProperty("mainUIAccentColor", ColorUtils.colorToString(mainUIAccentColor));
        config.setProperty("vehiclesUIColor", ColorUtils.colorToString(vehiclesUIColor));
        config.setProperty("zombiesUIColor", ColorUtils.colorToString(zombiesUIColor));
        config.setProperty("playersUIColor", ColorUtils.colorToString(playersUIColor));

        config.setProperty("isPlayerInSafeTeleported", Boolean.toString(isPlayerInSafeTeleported));
        config.setProperty("isMultiHitZombies", Boolean.toString(isMultiHitZombies));

        config.setProperty("isPlayerInSafeTeleported", Boolean.toString(isPlayerInSafeTeleported));
        config.setProperty("isMultiHitZombies", Boolean.toString(isMultiHitZombies));
        config.setProperty("isExtraDamage", Boolean.toString(isExtraDamage));
        config.setProperty("isTimedActionCheat", Boolean.toString(isTimedActionCheat));
        config.setProperty("isEnableGodMode", Boolean.toString(isEnableGodMode));
        config.setProperty("isEnableNoclip", Boolean.toString(isEnableNoclip));
        config.setProperty("isEnableInvisible", Boolean.toString(isEnableInvisible));
        config.setProperty("isEnableNightVision", Boolean.toString(isEnableNightVision));
        config.setProperty("isZombieDontAttack", Boolean.toString(isZombieDontAttack));
        config.setProperty("isNoRecoil", Boolean.toString(isNoRecoil));
        config.setProperty("isBypassDebugMode", Boolean.toString(isBypassDebugMode));
        config.setProperty("isUnlimitedCarry", Boolean.toString(isUnlimitedCarry));
        config.setProperty("isUnlimitedCondition", Boolean.toString(isUnlimitedCondition));
        config.setProperty("isUnlimitedEndurance", Boolean.toString(isUnlimitedEndurance));
        config.setProperty("isUnlimitedAmmo", Boolean.toString(isUnlimitedAmmo));
        config.setProperty("isAutoRepairItems", Boolean.toString(isAutoRepairItems));
        config.setProperty("isDisableFatigue", Boolean.toString(isDisableFatigue));
        config.setProperty("isDisableHunger", Boolean.toString(isDisableHunger));
        config.setProperty("isDisableThirst", Boolean.toString(isDisableThirst));
        config.setProperty("isDisableDrunkenness", Boolean.toString(isDisableDrunkenness));
        config.setProperty("isDisableAnger", Boolean.toString(isDisableAnger));
        config.setProperty("isDisableFear", Boolean.toString(isDisableFear));
        config.setProperty("isDisablePain", Boolean.toString(isDisablePain));
        config.setProperty("isDisablePanic", Boolean.toString(isDisablePanic));
        config.setProperty("isDisableMorale", Boolean.toString(isDisableMorale));
        config.setProperty("isDisableStress", Boolean.toString(isDisableStress));
        config.setProperty("isDisableSickness", Boolean.toString(isDisableSickness));
        config.setProperty("isDisableStressFromCigarettes", Boolean.toString(isDisableStressFromCigarettes));
        config.setProperty("isDisableSanity", Boolean.toString(isDisableSanity));
        config.setProperty("isDisableBoredomLevel", Boolean.toString(isDisableBoredomLevel));
        config.setProperty("isDisableUnhappynessLevel", Boolean.toString(isDisableUnhappynessLevel));
        config.setProperty("isDisableWetness", Boolean.toString(isDisableWetness));
        config.setProperty("isDisableInfectionLevel", Boolean.toString(isDisableInfectionLevel));
        config.setProperty("isDisableFakeInfectionLevel", Boolean.toString(isDisableFakeInfectionLevel));
        config.setProperty("isOptimalCalories", Boolean.toString(isOptimalCalories));
        config.setProperty("isOptimalWeight", Boolean.toString(isOptimalWeight));

        config.setProperty("isVisualsEnable", Boolean.toString(isVisualsEnable));
        config.setProperty("isVisualsPlayersEnable", Boolean.toString(isVisualsPlayersEnable));
        config.setProperty("isVisualsVehiclesEnable", Boolean.toString(isVisualsVehiclesEnable));
        config.setProperty("isVisualsZombiesEnable", Boolean.toString(isVisualsZombiesEnable));
        config.setProperty("isVisualDrawToLocalPlayer", Boolean.toString(isVisualDrawToLocalPlayer));
        config.setProperty("isVisualDrawPlayerNickname", Boolean.toString(isVisualDrawPlayerNickname));
        config.setProperty("isVisualDrawCredits", Boolean.toString(isVisualDrawCredits));
        config.setProperty("isVisualDrawPlayerInfo", Boolean.toString(isVisualDrawPlayerInfo));
        config.setProperty("isVisualDrawLineToVehicle", Boolean.toString(isVisualDrawLineToVehicle));
        config.setProperty("isVisualDrawLineToPlayers", Boolean.toString(isVisualDrawLineToPlayers));
        config.setProperty("isVisualEnable360Vision", Boolean.toString(isVisualEnable360Vision));

        config.setProperty("isMapDrawLocalPlayer", Boolean.toString(isMapDrawLocalPlayer));
        config.setProperty("isMapDrawAllPlayers", Boolean.toString(isMapDrawAllPlayers));
        config.setProperty("isMapDrawVehicles", Boolean.toString(isMapDrawVehicles));
        config.setProperty("isMapDrawZombies", Boolean.toString(isMapDrawZombies));

        try (FileOutputStream out = new FileOutputStream(fixedFileName)) {
            config.store(out, null);
        } catch (IOException e) {
            Logger.printLog("Error while saving config: " + e);
        }
    }

    /**
     * Загрузка конфигурационного файла и применение настроек
     * @param configFileName название файла настроек
     */
    public void loadConfig(String configFileName) {
        String fixedFileName = "EtherHack/config/"  + configFileName + ".properties";

        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream(fixedFileName)) {
            config.load(fis);
        } catch (IOException e) {
            Logger.printLog("The config file was not found. Loading canceled.");
            return;
        }

        mainUIAccentColor = ConfigUtils.getColorFromConfig(config, "mainUIAccentColor", new Color(56, 239, 125));
        vehiclesUIColor = ConfigUtils.getColorFromConfig(config, "vehiclesUIColor", new Color(150, 150, 200));
        zombiesUIColor = ConfigUtils.getColorFromConfig(config, "zombiesUIColor", new Color(255, 150, 100));
        playersUIColor = ConfigUtils.getColorFromConfig(config, "playersUIColor", new Color(255, 50, 100));

        isPlayerInSafeTeleported = ConfigUtils.getBooleanFromConfig(config, "isPlayerInSafeTeleported", false);
        isMultiHitZombies = ConfigUtils.getBooleanFromConfig(config, "isMultiHitZombies", false);
        isExtraDamage = ConfigUtils.getBooleanFromConfig(config, "isExtraDamage", false);
        isTimedActionCheat = ConfigUtils.getBooleanFromConfig(config, "isTimedActionCheat", false);
        isEnableGodMode = ConfigUtils.getBooleanFromConfig(config, "isEnableGodMode", false);
        isEnableNoclip = ConfigUtils.getBooleanFromConfig(config, "isEnableNoclip", false);
        isEnableInvisible = ConfigUtils.getBooleanFromConfig(config, "isEnableInvisible", false);
        isEnableNightVision = ConfigUtils.getBooleanFromConfig(config, "isEnableNightVision", false);
        isZombieDontAttack = ConfigUtils.getBooleanFromConfig(config, "isZombieDontAttack", false);
        isNoRecoil = ConfigUtils.getBooleanFromConfig(config, "isNoRecoil", false);
        isBypassDebugMode = ConfigUtils.getBooleanFromConfig(config, "isBypassDebugMode", false);
        isUnlimitedCarry = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedCarry", false);
        isUnlimitedCondition = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedCondition", false);
        isUnlimitedEndurance = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedEndurance", false);
        isUnlimitedAmmo = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedAmmo", false);
        isAutoRepairItems = ConfigUtils.getBooleanFromConfig(config, "isAutoRepairItems", false);
        isDisableFatigue = ConfigUtils.getBooleanFromConfig(config, "isDisableFatigue", false);
        isDisableHunger = ConfigUtils.getBooleanFromConfig(config, "isDisableHunger", false);
        isDisableThirst = ConfigUtils.getBooleanFromConfig(config, "isDisableThirst", false);
        isDisableDrunkenness = ConfigUtils.getBooleanFromConfig(config, "isDisableDrunkenness", false);
        isDisableAnger = ConfigUtils.getBooleanFromConfig(config, "isDisableAnger", false);
        isDisableFear = ConfigUtils.getBooleanFromConfig(config, "isDisableFear", false);
        isDisablePain = ConfigUtils.getBooleanFromConfig(config, "isDisablePain", false);
        isDisablePanic = ConfigUtils.getBooleanFromConfig(config, "isDisablePanic", false);
        isDisableMorale = ConfigUtils.getBooleanFromConfig(config, "isDisableMorale", false);
        isDisableStress = ConfigUtils.getBooleanFromConfig(config, "isDisableStress", false);
        isDisableSickness = ConfigUtils.getBooleanFromConfig(config, "isDisableSickness", false);
        isDisableStressFromCigarettes = ConfigUtils.getBooleanFromConfig(config, "isDisableStressFromCigarettes", false);
        isDisableSanity = ConfigUtils.getBooleanFromConfig(config, "isDisableSanity", false);
        isDisableBoredomLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableBoredomLevel", false);
        isDisableUnhappynessLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableUnhappynessLevel", false);
        isDisableWetness = ConfigUtils.getBooleanFromConfig(config, "isDisableWetness", false);
        isDisableInfectionLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableInfectionLevel", false);
        isDisableFakeInfectionLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableFakeInfectionLevel", false);
        isOptimalCalories = ConfigUtils.getBooleanFromConfig(config, "isOptimalCalories", false);
        isOptimalWeight = ConfigUtils.getBooleanFromConfig(config, "isOptimalWeight", false);

        isVisualsEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsEnable", false);
        isVisualsPlayersEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsPlayersEnable", false);
        isVisualsVehiclesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsVehiclesEnable", false);
        isVisualsZombiesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsZombiesEnable", false);
        isVisualDrawToLocalPlayer = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawToLocalPlayer", false);
        isVisualDrawPlayerNickname = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerNickname", false);
        isVisualDrawCredits = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawCredits", true);
        isVisualDrawPlayerInfo = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerInfo", false);
        isVisualDrawLineToVehicle = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToVehicle", false);
        isVisualDrawLineToPlayers = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToPlayers", false);
        isVisualEnable360Vision = ConfigUtils.getBooleanFromConfig(config, "isVisualEnable360Vision", false);

        isMapDrawLocalPlayer = ConfigUtils.getBooleanFromConfig(config, "isMapDrawLocalPlayer", true);
        isMapDrawAllPlayers = ConfigUtils.getBooleanFromConfig(config, "isMapDrawAllPlayers", false);
        isMapDrawVehicles = ConfigUtils.getBooleanFromConfig(config, "isMapDrawVehicles", false);
        isMapDrawZombies = ConfigUtils.getBooleanFromConfig(config, "isMapDrawZombies", false);

    }

    /**
     * Инициализация авто-загружаемого конфига
     */
    private void initStartupConfig() {
        Properties config = new Properties();
        try (FileInputStream fis = new FileInputStream("EtherHack/config/startup.properties")) {
            config.load(fis);
        } catch (IOException e) {
            Logger.printLog("Startup file not found. Loading default settings.");
        }

        mainUIAccentColor = ConfigUtils.getColorFromConfig(config, "mainUIAccentColor", new Color(56, 239, 125));
        vehiclesUIColor = ConfigUtils.getColorFromConfig(config, "vehiclesUIColor", new Color(150, 150, 200));
        zombiesUIColor = ConfigUtils.getColorFromConfig(config, "zombiesUIColor", new Color(255, 150, 100));
        playersUIColor = ConfigUtils.getColorFromConfig(config, "playersUIColor", new Color(255, 50, 100));

        isPlayerInSafeTeleported = ConfigUtils.getBooleanFromConfig(config, "isPlayerInSafeTeleported", false);
        isMultiHitZombies = ConfigUtils.getBooleanFromConfig(config, "isMultiHitZombies", false);
        isExtraDamage = ConfigUtils.getBooleanFromConfig(config, "isExtraDamage", false);
        isTimedActionCheat = ConfigUtils.getBooleanFromConfig(config, "isTimedActionCheat", false);
        isEnableGodMode = ConfigUtils.getBooleanFromConfig(config, "isEnableGodMode", false);
        isEnableNoclip = ConfigUtils.getBooleanFromConfig(config, "isEnableNoclip", false);
        isEnableInvisible = ConfigUtils.getBooleanFromConfig(config, "isEnableInvisible", false);
        isEnableNightVision = ConfigUtils.getBooleanFromConfig(config, "isEnableNightVision", false);
        isZombieDontAttack = ConfigUtils.getBooleanFromConfig(config, "isZombieDontAttack", false);
        isNoRecoil = ConfigUtils.getBooleanFromConfig(config, "isNoRecoil", false);
        isBypassDebugMode = ConfigUtils.getBooleanFromConfig(config, "isBypassDebugMode", false);
        isUnlimitedCarry = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedCarry", false);
        isUnlimitedCondition = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedCondition", false);
        isUnlimitedEndurance = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedEndurance", false);
        isUnlimitedAmmo = ConfigUtils.getBooleanFromConfig(config, "isUnlimitedAmmo", false);
        isAutoRepairItems = ConfigUtils.getBooleanFromConfig(config, "isAutoRepairItems", false);
        isDisableFatigue = ConfigUtils.getBooleanFromConfig(config, "isDisableFatigue", false);
        isDisableHunger = ConfigUtils.getBooleanFromConfig(config, "isDisableHunger", false);
        isDisableThirst = ConfigUtils.getBooleanFromConfig(config, "isDisableThirst", false);
        isDisableDrunkenness = ConfigUtils.getBooleanFromConfig(config, "isDisableDrunkenness", false);
        isDisableAnger = ConfigUtils.getBooleanFromConfig(config, "isDisableAnger", false);
        isDisableFear = ConfigUtils.getBooleanFromConfig(config, "isDisableFear", false);
        isDisablePain = ConfigUtils.getBooleanFromConfig(config, "isDisablePain", false);
        isDisablePanic = ConfigUtils.getBooleanFromConfig(config, "isDisablePanic", false);
        isDisableMorale = ConfigUtils.getBooleanFromConfig(config, "isDisableMorale", false);
        isDisableStress = ConfigUtils.getBooleanFromConfig(config, "isDisableStress", false);
        isDisableSickness = ConfigUtils.getBooleanFromConfig(config, "isDisableSickness", false);
        isDisableStressFromCigarettes = ConfigUtils.getBooleanFromConfig(config, "isDisableStressFromCigarettes", false);
        isDisableSanity = ConfigUtils.getBooleanFromConfig(config, "isDisableSanity", false);
        isDisableBoredomLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableBoredomLevel", false);
        isDisableUnhappynessLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableUnhappynessLevel", false);
        isDisableWetness = ConfigUtils.getBooleanFromConfig(config, "isDisableWetness", false);
        isDisableInfectionLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableInfectionLevel", false);
        isDisableFakeInfectionLevel = ConfigUtils.getBooleanFromConfig(config, "isDisableFakeInfectionLevel", false);
        isOptimalCalories = ConfigUtils.getBooleanFromConfig(config, "isOptimalCalories", false);
        isOptimalWeight = ConfigUtils.getBooleanFromConfig(config, "isOptimalWeight", false);

        isVisualsEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsEnable", false);
        isVisualsPlayersEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsPlayersEnable", false);
        isVisualsVehiclesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsVehiclesEnable", false);
        isVisualsZombiesEnable = ConfigUtils.getBooleanFromConfig(config, "isVisualsZombiesEnable", false);
        isVisualDrawToLocalPlayer = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawToLocalPlayer", false);
        isVisualDrawPlayerNickname = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerNickname", false);
        isVisualDrawCredits = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawCredits", true);
        isVisualDrawPlayerInfo = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawPlayerInfo", false);
        isVisualDrawLineToVehicle = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToVehicle", false);
        isVisualDrawLineToPlayers = ConfigUtils.getBooleanFromConfig(config, "isVisualDrawLineToPlayers", false);
        isVisualEnable360Vision = ConfigUtils.getBooleanFromConfig(config, "isVisualEnable360Vision", false);

        isMapDrawLocalPlayer = ConfigUtils.getBooleanFromConfig(config, "isMapDrawLocalPlayer", true);
        isMapDrawAllPlayers = ConfigUtils.getBooleanFromConfig(config, "isMapDrawAllPlayers", false);
        isMapDrawVehicles = ConfigUtils.getBooleanFromConfig(config, "isMapDrawVehicles", false);
        isMapDrawZombies = ConfigUtils.getBooleanFromConfig(config, "isMapDrawZombies", false);
    }

    /**
     * Создает экземпляр класса EtherAPI и регистрирует его как подписчика событий.
     */
    public EtherAPI() {
        initStartupConfig();
        EventSubscriber.register(this);
    }

    /**
     * Загружает API в контекст игры.
     */
    @SubscribeLuaEvent(
            eventName = "OnResetLua"
    )
    @SubscribeLuaEvent(
            eventName = "OnMainMenuEnter"
    )
    public void loadAPI() {
        Logger.printLog("Loading EtherAPI...");
        if (exposer != null) {
            exposer.destroy();
        }

        exposer = new Exposer(LuaManager.converterManager, LuaManager.platform, LuaManager.env);
        exposer.exposeAPI(etherLuaMethods);
    }

    /**
     * Восстановление характеристик всего оружия в инвентаре
     */
    public void resetWeaponsStats(){
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) return;

        ArrayList<InventoryItem> inventoryItems = localPlayer.getInventory().getItems();

        if (inventoryItems != null && !inventoryItems.isEmpty()) {
            for (InventoryItem item: inventoryItems) {
                if(!(item instanceof HandWeapon weapon) || !item.getStringItemType().equals("RangedWeapon") && !item.getStringItemType().equals("MeleeWeapon")) {
                    continue;
                }
                String type = weapon.getFullType();

                // Если для данного типа оружия есть сохраненные значения, применяем их
                if (originalWeaponStats.containsKey(type)) {
                    float[] values = originalWeaponStats.get(type);
                    weapon.setExtraDamage(values[0]);
                    weapon.setMaxDamage(values[1]);
                    weapon.setMinDamage(values[2]);
                    weapon.setMaxRange(values[3]);
                    weapon.setMinRange(values[4]);
                    weapon.setHitChance((int) values[5]); // Приводим к int, так как setHitChance принимает int
                    weapon.setCritDmgMultiplier(values[6]);
                }
            }
        }
    }


    /**
     * Обновление API, которые относятся исключительно к локальному игроку
     */
    private void updateLocalPlayerFeatures(){
        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) return;

        InventoryItem playerItem = localPlayer.getPrimaryHandItem();

        if (isExtraDamage) {
            if (playerItem != null && ((playerItem.getStringItemType().equals("RangedWeapon") || playerItem.getStringItemType().equals("MeleeWeapon")) && playerItem instanceof HandWeapon weapon)) {
                String weaponType = weapon.getFullType();

                // Сохраняем изначальные значения только если они еще не сохранены
                if (!originalWeaponStats.containsKey(weaponType)) {
                    originalWeaponStats.put(weaponType, new float[]{
                            weapon.getExtraDamage(),
                            weapon.getMaxDamage(),
                            weapon.getMinDamage(),
                            weapon.getMaxRange(),
                            weapon.getMinRange(),
                            (float) weapon.getHitChance(),
                            weapon.getCritDmgMultiplier()
                    });
                }

                weapon.setExtraDamage(100000f);
                weapon.setMaxDamage(1000000f);
                weapon.setMinDamage(1000000f);
                weapon.setMaxRange(10000f);
                weapon.setMinRange(0f);
                weapon.setHitChance(100);
                weapon.setCritDmgMultiplier(100000f);
            }
        }



        if ((Boolean)SandboxOptions.instance.getOptionByName("MultiHitZombies").asConfigOption().getValueAsObject() != isMultiHitZombies) {
            SandboxOptions.instance.set("MultiHitZombies", isMultiHitZombies);
        };

        if(localPlayer.isTimedActionInstantCheat() != isTimedActionCheat) localPlayer.setTimedActionInstantCheat(isTimedActionCheat);
        if(localPlayer.isWearingNightVisionGoggles() != isEnableNightVision) localPlayer.setWearingNightVisionGoggles(isEnableNightVision);
        if(localPlayer.isGodMod() != isEnableGodMode) localPlayer.setGodMod(isEnableGodMode);
        if(localPlayer.isNoClip() != isEnableNoclip) localPlayer.setNoClip(isEnableNoclip);
        if(localPlayer.isInvisible() != isEnableInvisible) localPlayer.setInvisible(isEnableInvisible);
        if(localPlayer.isZombiesDontAttack() != isZombieDontAttack) localPlayer.setZombiesDontAttack(isZombieDontAttack);

        if (isNoRecoil) {
            if (playerItem != null && playerItem.getStringItemType().equals("RangedWeapon") && playerItem instanceof HandWeapon weapon){
                weapon.setRecoilDelay(0);
                weapon.setCriticalChance(100.0f);
                weapon.setAlwaysKnockdown(true);
                weapon.setAimingTime(0);
            }
        }


        if (isUnlimitedAmmo) {
            if (playerItem != null && playerItem.getStringItemType().equals("RangedWeapon")) {
                playerItem.setCurrentAmmoCount(playerItem.getMaxAmmo());
            }
        }

        if(isUnlimitedCondition) {
            if (playerItem != null)
            {
                if(playerItem.getHaveBeenRepaired() > 1) playerItem.setHaveBeenRepaired(1);
                playerItem.setCondition(playerItem.getConditionMax());
            }
        }

        if(isAutoRepairItems){
            ArrayList<InventoryItem> inventoryItems = localPlayer.getInventory().getItems();

            if (inventoryItems != null && !inventoryItems.isEmpty()) {
                for (InventoryItem item: inventoryItems) {
                    if(item == null) continue;

                    if(item.isBroken()) item.setBroken(false);

                    item.setHaveBeenRepaired(1);

                    if (item.getVisual() != null) {
                        for (int i = 0; i < BloodBodyPartType.MAX.index(); i++) {
                            item.getVisual().removeHole(i);
                            item.getVisual().removeDirt();
                            item.getVisual().removeBlood();
                        }
                    }

                    item.setWet(false);
                    item.setInfected(false);
                    item.setCondition(item.getConditionMax());
                }
            }
        }

        if (isUnlimitedEndurance) localPlayer.getStats().setEndurance(1.0f);
        if (isDisableFatigue) localPlayer.getStats().setFatigue(0.0f);
        if (isDisableHunger) localPlayer.getStats().setHunger(0.0f);
        if (isDisableThirst) localPlayer.getStats().setThirst(0.0f);
        if (isDisableDrunkenness) localPlayer.getStats().setDrunkenness(0.0f);
        if (isDisableAnger) localPlayer.getStats().setAnger(0.0f);
        if (isDisableFear) localPlayer.getStats().setFear(0.0f);
        if (isDisablePain) localPlayer.getStats().setPain(0.0f);
        if (isDisablePanic) localPlayer.getStats().setPanic(0.0f);
        if (isDisableMorale) localPlayer.getStats().setMorale(1.0f);
        if (isDisableStress) localPlayer.getStats().setStress(0.0f);
        if (isDisableSickness) localPlayer.getStats().setSickness(0.0f);
        if (isDisableStressFromCigarettes) localPlayer.getStats().setStressFromCigarettes(0.0f);
        if (isDisableSanity) localPlayer.getStats().setSanity(1.0f);
        if (isDisableBoredomLevel) localPlayer.getBodyDamage().setBoredomLevel(0.0f);
        if (isDisableUnhappynessLevel) localPlayer.getBodyDamage().setUnhappynessLevel(0.0f);
        if (isDisableWetness) localPlayer.getBodyDamage().setWetness(0.0f);
        if (isDisableInfectionLevel) localPlayer.getBodyDamage().setInfectionLevel(0.0f);
        if (isDisableFakeInfectionLevel) localPlayer.getBodyDamage().setFakeInfectionLevel(0.0f);
        if (isOptimalCalories) localPlayer.getNutrition().setCalories(1200.0f);
        if (isOptimalWeight) localPlayer.getNutrition().setWeight(80.0f);
    }

    /**
     * Обновление состояние режима отладки
     */
    private void bypassDebugMode() {
        boolean isGameActive = GameClient.bIngame;
        boolean isAntiCheatProtectionEnabled = ServerOptions.instance.getBoolean("AntiCheatProtectionType12");
        boolean isServerMode = GameServer.bServer;
        boolean isCooperativeMode = GameServer.bCoop;
        Core.bDebug = isGameActive && isBypassDebugMode && (!isAntiCheatProtectionEnabled && isServerMode || isCooperativeMode || !isServerMode);
    }

    /**
     * Обновление визуального API
     */
    @SubscribeLuaEvent(
            eventName = "OnPostUIDraw"
    )
    public void updateVisuals(){
        try {
            updatePlayersVisuals();
            updateVehiclesVisuals();
            updateZombiesVisuals();
            updateUltraPlayerVision();
        }
        catch (Exception e){}
    }

    /**
     * Отключение прозрачности при выходе объекта за угол обзора
     */
    public void updateUltraPlayerVision(){
        if (!isVisualEnable360Vision) return;

        ArrayList<BaseVehicle> vehicles = IsoWorld.instance.getCell().getVehicles();

        if (vehicles != null && !vehicles.isEmpty()) {
            for (BaseVehicle vehicle : vehicles) {
                vehicle.setAlpha(100.0f);
            }
        };

        ArrayList<IsoZombie> zombies = IsoWorld.instance.getCell().getZombieList();
        if (zombies != null && !zombies.isEmpty()) {
            for (IsoZombie zombie : zombies) {
                zombie.setAlpha(100.0f);
            }
        };

        ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();
        if (players != null && !players.isEmpty()) {
            for (IsoPlayer player : players) {
                if(!player.isLocalPlayer()) {
                    player.setAlpha(100.0f);
                }
            }
        }
    }
    /**
     * Обновление визуальных эффектов - транспорт
     */
    private void updateVehiclesVisuals(){
        if (!isVisualsEnable || !isVisualsVehiclesEnable) return;

        IsoPlayer localPlayer = IsoPlayer.getInstance();

        if (localPlayer == null) return;

        ArrayList<BaseVehicle> vehicles = IsoWorld.instance.getCell().getVehicles();

        float posLocalPlayerX = PlayerUtils.getScreenPositionX(localPlayer);
        float posLocalPlayerY = PlayerUtils.getScreenPositionY(localPlayer);

        float colorA = vehiclesUIColor.a;
        float colorR = vehiclesUIColor.r;
        float colorG = vehiclesUIColor.g;
        float colorB = vehiclesUIColor.b;

        if (vehicles == null && vehicles.isEmpty()) return;

        for (BaseVehicle vehicle : vehicles) {
            float vehiclePosX = VehicleUtils.getScreenPositionX(vehicle);
            float vehiclePosY = VehicleUtils.getScreenPositionY(vehicle);

            Rendering.drawTextCenterWithShadow("ID:" + vehicle.getScriptName(), UIFont.Small, vehiclePosX, vehiclePosY, colorR, colorG, colorB, colorA);
            Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_VehicleSpeed") + vehicle.getMaxSpeed(), UIFont.Small, vehiclePosX, vehiclePosY + 10, colorR, colorG, colorB, colorA); // Работает верно, больше 60 - вне обзора камеры, даже при зуме

            if (isVisualDrawLineToVehicle){
                int distance = (int)PlayerUtils.getDistanceBetweenPlayerAndVehicle(localPlayer, vehicle);

                int textDistance = Math.max(30, Math.min(150, distance));
                float totalLength = (float)Math.sqrt(Math.pow(vehiclePosX - posLocalPlayerX, 2) + Math.pow(vehiclePosY - posLocalPlayerY, 2));
                float ratio = textDistance / totalLength;

                float textPosX = posLocalPlayerX + ratio * (vehiclePosX - posLocalPlayerX);
                float textPosY = posLocalPlayerY + 60 + ratio * (vehiclePosY - posLocalPlayerY);

                Rendering.drawLine((int)vehiclePosX, (int)vehiclePosY, (int)posLocalPlayerX, (int)posLocalPlayerY + 60, colorR, colorG, colorB, 0.8f, 1);

                Rendering.drawTextCenterWithShadow(String.valueOf(distance), UIFont.Small, textPosX, textPosY, colorR, colorG, colorB, colorA);
            }
        }
    }

    /**
     * Обновление визуальных эффектов - зомби
     */
    private void updateZombiesVisuals(){
        if (!isVisualsEnable || !isVisualsZombiesEnable) return;

        IsoPlayer localPlayer = IsoPlayer.getInstance();

        if (localPlayer == null) return;

        ArrayList<IsoZombie> zombies = IsoWorld.instance.getCell().getZombieList();

        float colorA = zombiesUIColor.a;
        float colorR = zombiesUIColor.r;
        float colorG = zombiesUIColor.g;
        float colorB = zombiesUIColor.b;

        if (zombies == null && zombies.isEmpty()) return;

        for (IsoZombie zombie : zombies) {
            float posX = ZombieUtils.getScreenPositionX(zombie);
            float posY = ZombieUtils.getScreenPositionY(zombie);

            int health = (int)(zombie.getHealth() * 100);

            Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieTitle"), UIFont.Small, posX, posY, colorR, colorG, colorB, colorA);
            Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieHealth") + health, UIFont.Small, posX, posY + 10, colorR, colorG, colorB, colorA);
        }
    }

    /**
     * Обновление визуальных эффектов - игроки
     */
    private void updatePlayersVisuals(){
        if (!isVisualsEnable || !isVisualsPlayersEnable) return;

        IsoPlayer localPlayer = IsoPlayer.getInstance();
        if (localPlayer == null) return;

        ArrayList<IsoPlayer> players = GameClient.instance.getPlayers();

        float posLocalPlayerX = PlayerUtils.getScreenPositionX(localPlayer);
        float posLocalPlayerY = PlayerUtils.getScreenPositionY(localPlayer);

        float colorA = playersUIColor.a;
        float colorR = playersUIColor.r;
        float colorG = playersUIColor.g;
        float colorB = playersUIColor.b;

        if (players == null && players.isEmpty()) return;

        for (IsoPlayer player : players) {
            float playerPosX = PlayerUtils.getScreenPositionX(player);
            float playerPosY = PlayerUtils.getScreenPositionY(player);

            if (player.isLocalPlayer() && !isVisualDrawToLocalPlayer) continue;

            if (isVisualDrawPlayerNickname) Rendering.drawTextCenterWithShadow(player.getUsername(), UIFont.Small, playerPosX, playerPosY - 30, colorR, colorG, colorB, colorA);

            if (isVisualDrawPlayerInfo) {
                String firstHandItem = player.getPrimaryHandItem() != null ? player.getPrimaryHandItem().getDisplayName() : "None";
                String secondHandItem = player.getSecondaryHandItem() != null ? player.getSecondaryHandItem().getDisplayName() : "None";
                Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_PrimaryHand") + firstHandItem, UIFont.Small, playerPosX, playerPosY + 70, colorR, colorG, colorB, colorA);
                Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_SecondaryHand") + secondHandItem, UIFont.Small, playerPosX, playerPosY + 80, colorR, colorG, colorB, colorA);
            }

            if(!player.isLocalPlayer()) {
                if (isVisualDrawLineToPlayers && PlayerUtils.getDistanceBetweenPlayers(localPlayer, player) < 150.0f)
                {
                    int distance = (int)PlayerUtils.getDistanceBetweenPlayers(player, localPlayer);
                    int textDistance = Math.max(30, Math.min(150, distance));

                    float totalLength = (float)Math.sqrt(Math.pow(playerPosX - posLocalPlayerX, 2) + Math.pow(playerPosY - posLocalPlayerY, 2));
                    float ratio = textDistance / totalLength;

                    float textPosX = posLocalPlayerX + ratio * (playerPosX - posLocalPlayerX);
                    float textPosY = posLocalPlayerY + 60 + ratio * (playerPosY - posLocalPlayerY);

                    Rendering.drawLine((int)playerPosX, (int)playerPosY, (int)posLocalPlayerX, (int)posLocalPlayerY + 60, colorR, colorG, colorB, 0.8f, 1);

                    Rendering.drawTextCenterWithShadow(String.valueOf(distance), UIFont.Small, textPosX, textPosY, colorR, colorG, colorB, colorA);
                }
            }
        }
    }

    /**
     * Обновление API каждый рендеринг дисплея
     */
    @SubscribeLuaEvent(
            eventName = "OnRenderTick"
    )
    public void updateAPI(){
        updateLocalPlayerFeatures();
        bypassDebugMode();
    }
}
