--*********************************************************
--* Объект EtherHackAPI
--*********************************************************
EtherHack = {}

--*********************************************************
--* API EtherHack
--*********************************************************
EtherHack.API = {
    -- Приватные переменные
    private = {
        isCharacterNeeds = false,
        isUnlimitedEndurance = false,
        isDisableFatigue = false,
        isDisableHunger = false,
        isDisableThirst = false,
    },

    -- Получение состояние режима мультиудара
    openAdminMenu = function()
        if EtherAdminPanel.instance then
            EtherAdminPanel.instance:close()
        end
        local modal = EtherAdminPanel:new(200, 200, 350, 400)
        modal:initialise();
        modal:addToUIManager();
        return
    end,
    -- Получение состояние режима мультиудара
    isMultiHitZombies = function()
        return getSandboxOptions():getOptionByName("MultiHitZombies"):getValue();
    end,

    -- Изменение состояние режима мультиудара
    toggleMultiHitZombies = function(changeOptionTarget, joypadIndex, isSelected)
        getSandboxOptions():set("MultiHitZombies", isSelected);
    end,
    
    -- Получение состояние режима жажды
    isDisableThirst = function()
        return EtherHack.API.private.isDisableThirst;
    end,

    -- Изменение состояние режима жажды
    toggleDisableThirst = function(changeOptionTarget, joypadIndex, isSelected)
        EtherHack.API.private.isDisableThirst = isSelected;
    end,

    -- Получение состояние режима голода
    isDisableHunger = function()
        return EtherHack.API.private.isDisableHunger;
    end,

    -- Изменение состояние режима голода
    toggleDisableHunger = function(changeOptionTarget, joypadIndex, isSelected)
        EtherHack.API.private.isDisableHunger = isSelected;
    end,

    -- Получение состояние режима усталости
    isDisableFatigue = function()
        return EtherHack.API.private.isDisableFatigue;
    end,

    -- Изменение состояние режима усталости
    toggleDisableFatigue = function(changeOptionTarget, joypadIndex, isSelected)
        EtherHack.API.private.isDisableFatigue = isSelected;
    end,
    
    -- Получение состояние режима выносливости
    isUnlimitedEndurance = function()
        return EtherHack.API.private.isUnlimitedEndurance;
    end,

    -- Изменение состояние режима выносливости
    toggleUnlimitedEndurance = function(changeOptionTarget, joypadIndex, isSelected)
        EtherHack.API.private.isUnlimitedEndurance = isSelected;
    end,

    -- Получение состояние режима потребностей персонажа
    isCharacterNeeds = function()
        return EtherHack.API.private.isCharacterNeeds;
    end,

    -- Изменение состояние режима потребностей персонажа
    toggleCharacterNeeds = function(changeOptionTarget, joypadIndex, isSelected)
        EtherHack.API.private.isCharacterNeeds = isSelected;
    end,

    -- Состояние режима бесконечной грузоподъемности
    isEtherUnlimitedCarry = function()
        return isEtherUnlimitedCarry();
    end,

    -- Состояние обхода запрета на режим отладки
    isEtherBypassDebugMode = function()
        return isEtherBypassDebugMode();
    end,

    -- Получение состояния сессии (в игре или нет)
    isEtherInGame = function()
        return isEtherInGame();
    end,

    -- Получить заголовок для окна
    getEtherUITitle = function()
        return getEtherUITitle();
    end,

    -- Добавить очки навыков при создании персонажа
    addProfessionPoint = function(point)
        local UIElementsList = UIManager.getUI()
		for i=1, UIElementsList:size() do
			local UIElementInstance = UIElementsList:get(i-1);
            if UIElementInstance:getTable() and UIElementInstance:getTable():getChildren() ~= nil then
                for _, child in pairs(UIElementInstance:getTable():getChildren()) do
                    if child.Type == "CharacterCreationProfession" then
                        child.pointToSpend = child.pointToSpend + point;
                        break;
                    end
                end
            end
		end
    end,
    -- Включение режима разработки
    setDebugBypass = function(changeOptionTarget, joypadIndex, isSelected)
        setEtherBypassDebugMode(isSelected);
    end,

    -- Получение прав администратора
    setAdminAccess = function()
        setAdmin();
        setEtherAdminAccess();
    end,

    -- Выдача предметов
    addItem = function(id, amount)
        getPlayer():getInventory():AddItems(id, amount);
    end,

    -- Метод для переключения состояния "Invisible"
    toggleInvisible = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        getPlayer():setInvisible(isSelected);
    end,

    -- Метод для переключения состояния "GodMode"
    toggleGodMode = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        getPlayer():setGodMod(isSelected);
    end,

    -- Метод для переключения состояния "NoClip"
    toggleNoClip = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        getPlayer():setNoClip(isSelected);
    end,

    -- Метод для переключения режима бесконечной грузоподъемности
    toggleUnlimitedCarry = function(changeOptionTarget, joypadIndex, isSelected)
        if not isEtherInGame() or getPlayer() == nil then return end
        setEtherUnlimitedCarry(isSelected);
    end,
};

EtherHack.EveryOneMinute = function()
    local player = getPlayer();
    local stats = player:getStats();
    local body = player:getBodyDamage();
    local nutrition = player:getNutrition()

    if EtherHack.API.isUnlimitedEndurance() or EtherHack.API.isCharacterNeeds() then
        stats:setEndurance(1);
    end

    if EtherHack.API.isDisableFatigue() or EtherHack.API.isCharacterNeeds() then
        stats:setFatigue(0);
    end

    if EtherHack.API.isDisableHunger() or EtherHack.API.isCharacterNeeds() then
        stats:setHunger(0);
    end

    if EtherHack.API.isDisableThirst() or EtherHack.API.isCharacterNeeds() then
        stats:setThirst(0);
    end

    if EtherHack.API.isCharacterNeeds() then
        stats:setHunger(0);
        stats:setThirst(0);
        stats:setFatigue(0);
        stats:setEndurance(1);
        stats:setDrunkenness(0);
        stats:setAnger(0);
        stats:setFear(0);
        stats:setPain(0);
        stats:setPanic(0);
        stats:setMorale(1);
        stats:setStress(0);
        stats:setSickness(0);
        stats:setStressFromCigarettes(0);
        stats:setSanity(1);

        player:setTimeSinceLastSmoke(0);

        body:setBoredomLevel(0);
        body:setUnhappynessLevel(0);
        body:setWetness(0);
        body:setOverallBodyHealth(100);
        body:setInfectionLevel(0);
        body:setFakeInfectionLevel(0);
        body:setFoodSicknessLevel(0);

        nutrition:setCalories(2200);
        nutrition:setWeight(80);
    end
end

Events.EveryOneMinute.Add(EtherHack.EveryOneMinute)