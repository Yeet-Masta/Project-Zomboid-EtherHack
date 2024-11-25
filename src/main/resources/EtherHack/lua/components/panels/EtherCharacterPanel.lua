require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
EtherCharacterPanel = ISPanel:derive("EtherCharacterPanel"); -- Наследование от ISPanel

--*********************************************************
--* Добавление чекбоксов
--*********************************************************
function EtherCharacterPanel:addCheckBox(title, method, isSelected, isOnlyInGame)
    local checkBoxAmount = #self.checkBoxList;
    local checkboxX = 15;
    local checkboxY = 10 + checkBoxAmount * 20;

    local checkbox = UICheckbox:new(checkboxX, checkboxY, title, isSelected, method);
    checkbox:initialise();
    checkbox:instantiate();
    checkbox:setAnchorLeft(true);
    checkbox:setAnchorRight(false);
    checkbox:setAnchorTop(false);
    checkbox:setAnchorBottom(true);
    checkbox.isOnlyInGame = isOnlyInGame;
    self:addChild(checkbox);

    self:setScrollHeight(self:getScrollHeight() + checkbox.height + 5);

    table.insert(self.checkBoxList, checkbox);
end

--*********************************************************
--* Обработка событий колесика мыши
--*********************************************************
function EtherCharacterPanel:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del * 40));
	return true;
end

--*********************************************************
--* Обновление панели
--*********************************************************
function EtherCharacterPanel:updatePanel()
    for i=1, #self.checkBoxList do
        local item = self.checkBoxList[i];
        if item.isOnlyInGame and self.localPlayer == nil then
        item:setEnable(false);
        end
    end
end

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function EtherCharacterPanel:createChildren()
    ISPanel.createChildren(self);

    self:setScrollChildren(true)
    self:setScrollHeight(0)
    self:addScrollBars();

    self:addCheckBox(getTranslate("UI_CharacterPanel_MultiHitZombies"), function(isChecked)
        toggleMultiHitZombies(isChecked);
    end, isMultiHitZombies(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_ZombieDontAttack"), function(isChecked)
        toggleZombieDontAttack(isChecked);
    end, isZombieDontAttack(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_BuildCheat"), function(isChecked)
        ISBuildMenu.cheat = isChecked;
    end, ISBuildMenu.cheat, false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_FarmingCheat"), function(isChecked)
        ISFarmingMenu.cheat = isChecked;
    end, ISFarmingMenu.cheat, false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_GodMode"), function(isChecked)
        toggleGodMode(isChecked);
    end, isEnableGodMode(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_TimedActionCheat"), function(isChecked)
        toggleTimedActionCheat(isChecked);
    end, isTimedActionCheat(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_NoClip"), function(isChecked)
        toggleNoclip(isChecked);
    end, isEnableNoclip(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_Invisible"), function(isChecked)
        toggleInvisible(isChecked);
    end, isEnableInvisible(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_NightVision"), function(isChecked)
        toggleNightVision(isChecked);
    end, isEnableNightVision(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_InstantKill"), function(isChecked)
        toggleExtraDamage(isChecked);
        if(not isChecked) then
            resetWeaponsStats()
        end
    end, isExtraDamage(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_UnlimitedCarry"), function(isChecked)
        toggleEnableUnlimitedCarry(isChecked);
    end, isEnableUnlimitedCarry(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_UnlimitedEndurance"), function(isChecked)
        toggleUnlimitedEndurance(isChecked);
    end, isUnlimitedEndurance(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_UnlimitedAmmo"), function(isChecked)
        toggleUnlimitedAmmo(isChecked);
    end, isUnlimitedAmmo(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_UnlimitedCondition"), function(isChecked)
        toggleUnlimitedCondition(isChecked);
    end, isUnlimitedCondition(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_AutoRepairsItems"), function(isChecked)
        toggleAutoRepairItems(isChecked);
    end, isAutoRepairItems(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableRecoil"), function(isChecked)
        toggleNoRecoil(isChecked)
    end, isNoRecoil(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableFatigue"), function(isChecked)
        toggleDisableFatigue(isChecked);
    end, isDisableFatigue(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableHunger"), function(isChecked)
        toggleDisableHunger(isChecked);
    end, isDisableHunger(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableThirst"), function(isChecked)
        toggleDisableThirst(isChecked);
    end, isDisableThirst(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableDrunkenness"), function(isChecked)
        toggleDisableDrunkenness(isChecked);
    end, isDisableDrunkenness(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableAnger"), function(isChecked)
        toggleDisableAnger(isChecked);
    end, isDisableAnger(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableFear"), function(isChecked)
        toggleDisableFear(isChecked);
    end, isDisableFear(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisablePain"), function(isChecked)
        toggleDisablePain(isChecked);
    end, isDisablePain(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisablePanic"), function(isChecked)
        toggleDisablePanic(isChecked);
    end, isDisablePanic(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableMorale"), function(isChecked)
        toggleDisableMorale(isChecked);
    end, isDisableMorale(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableStress"), function(isChecked)
        toggleDisableStress(isChecked);
    end, isDisableStress(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableSickness"), function(isChecked)
        toggleDisableSickness(isChecked);
    end, isDisableSickness(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableStressFromCigarettes"), function(isChecked)
        toggleDisableStressFromCigarettes(isChecked);
    end, isDisableStressFromCigarettes(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableSanity"), function(isChecked)
        toggleDisableSanity(isChecked);
    end, isDisableSanity(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableBoredomLevel"), function(isChecked)
        toggleDisableBoredomLevel(isChecked);
    end, isDisableBoredomLevel(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableUnhappynessLevel"), function(isChecked)
        toggleDisableUnhappynessLevel(isChecked);
    end, isDisableUnhappynessLevel(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableWetness"), function(isChecked)
        toggleDisableWetness(isChecked);
    end, isDisableWetness(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableInfectionLevel"), function(isChecked)
        toggleDisableInfectionLevel(isChecked);
    end, isDisableInfectionLevel(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_DisableFakeInfectionLevel"), function(isChecked)
        toggleDisableFakeInfectionLevel(isChecked);
    end, isDisableFakeInfectionLevel(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_OptimalCalories"), function(isChecked)
        toggleOptimalCalories(isChecked);
    end, isOptimalCalories(), false);

    self:addCheckBox(getTranslate("UI_CharacterPanel_OptimalWeight"), function(isChecked)
        toggleOptimalWeight(isChecked);
    end, isOptimalWeight(), false);
    
    self:updatePanel();
end

--*********************************************************
--* Обработка prerender
--*********************************************************
function EtherCharacterPanel:prerender()
    self:setStencilRect(0,10,self:getWidth(),self:getHeight() - 20);
    ISPanel.prerender(self);
end

--*********************************************************
--* Обработка render
--*********************************************************
function EtherCharacterPanel:render()
    ISPanel.render(self);
    self:clearStencilRect();
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EtherCharacterPanel:new(posX, posY, width, height)
    local menuTableData = {};

    menuTableData = ISPanel:new(posX, posY, width, height);
    setmetatable(menuTableData, self);
    menuTableData.background = true;
	menuTableData.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.0};
	menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    menuTableData.moveWithMouse = true;
    menuTableData.localPlayer = getPlayer();
    self.__index = self;

    self.checkBoxList = {}; -- Список всех чекбоксов

    return menuTableData;
end