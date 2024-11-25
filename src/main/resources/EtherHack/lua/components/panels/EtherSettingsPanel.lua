require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
EtherSettingsPanel = ISPanel:derive("EtherSettingsPanel"); -- Наследование от ISPanel

--*********************************************************
--* Создание метки
--*********************************************************
function EtherSettingsPanel:addLabel(posX, posY, title)
    local label = ISLabel:new(posX, posY + 3, getTextManager():getFontHeight(UIFont.Small), title, 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
    return label
end

--*********************************************************
--* Создание кнопки
--*********************************************************
function EtherSettingsPanel:addButton(posX, posY, buttonTitle, onClick, isOnlyNotInGame)
    local buttonWidth, buttonHeight = 130, 16;
    local button = UIButton:new(posX, posY, buttonWidth, buttonHeight, buttonTitle, onClick)
    button:initialise();
    button:instantiate();
    button:setAnchorLeft(true);
    button:setAnchorRight(false);
    button:setAnchorTop(false);
    button:setAnchorBottom(true);
    button.isOnlyNotInGame = isOnlyNotInGame;
    self:addChild(button);
    table.insert(self.buttonList, button);
    return button
end

--*********************************************************
--* Создание слайдера
--*********************************************************
function EtherSettingsPanel:addSlider(posX, posY, width, height, value, minValue, maxValue, method)
    local slider = UISlider:new(posX, posY, width, height, value, minValue, maxValue, method)
    slider:initialise();
    slider:instantiate();
    self:addChild(slider);
    return slider
end

--*********************************************************
--* Создание кнопки с заголовком
--*********************************************************
function EtherSettingsPanel:addButtonWithLabel(title, buttonTitle, func, isOnlyNotInGame)
    local rows = self.rows;
    local buttonY = 200 + rows * 25;

    self:addLabel(10, buttonY - 3, title)
    self:addButton(self:getWidth() - 130 - 10, buttonY, buttonTitle, func, isOnlyNotInGame)

    self:setScrollHeight(self:getScrollHeight() + 21);
    self.rows = self.rows + 1;
end

--*********************************************************
--* Создание выбора цвета с заголовком
--*********************************************************
function EtherSettingsPanel:addColorPickerWithLabel(title, func, startColor)
    local rows = self.rows;
    local buttonY = 200 + rows * 25;

    self:addLabel(10, buttonY - 3, title)

    local buttonWidth, buttonHeight = 16, 16;
    local button = ISButton:new(self:getWidth() - buttonWidth - 10, buttonY, buttonWidth, buttonHeight, "", self, func)
    button:initialise();
    button.backgroundColor = {r = startColor:getR(), g = startColor:getG(), b = startColor:getB(), a = 1};
	button.backgroundColorMouseOver = {r = startColor:getR(), g = startColor:getG(), b = startColor:getB(), a = 1};

    self:addChild(button);
    table.insert(self.buttonList, button);

    self:setScrollHeight(self:getScrollHeight() + 24);
    self.rows = self.rows + 1;
    return button
end

--*********************************************************
--* Создание заголовка с двумя кнопками
--*********************************************************
function EtherSettingsPanel:addSliderWithLabel(title, sliderMethod, value, minValue, maxValue)
    local rows = self.rows;
    local buttonY = 10 + rows * 25;

    self:addLabel(15, buttonY - 3, title)
    self:addSlider(self:getWidth() - 200 - 50, buttonY + 3, 200, 10, value, minValue, maxValue, sliderMethod)

    self:setScrollHeight(self:getScrollHeight() + 30);

    self.rows = self.rows + 1;
end


--*********************************************************
--* Добавление чекбоксов
--*********************************************************
function EtherSettingsPanel:addCheckBox(title, method, isSelected, isOnlyInGame)
    local rows = self.rows;
    local checkboxX = 15;
    local checkboxY = 10 + rows * 20;

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

    self.rows = self.rows + 1;

    table.insert(self.checkBoxList, checkbox);
end


--*********************************************************
--* Обновление панели
--*********************************************************
function EtherSettingsPanel:updatePanel()
    for i=1, #self.checkBoxList do
        local item = self.checkBoxList[i];
        if item.isOnlyInGame and self.localPlayer == nil then
            item:setEnable(false);
        end
    end
    for i=1, #self.buttonList do
        local item = self.buttonList[i];
        if item.isOnlyNotInGame and self.localPlayer ~= nil then
            item:setEnable(false);
        end
    end
end

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function EtherSettingsPanel:createChildren()
    ISPanel.createChildren(self);

    self:setScrollChildren(true);
    self:setScrollHeight(0);
    self:addScrollBars();

    self:addLabel(10, 10, getTranslate("UI_Settings_ConfigTitle"))
    self.configs = ISScrollingListBox:new(10, 60, self.width - 20, 100);
    self.configs:initialise();
    self.configs:instantiate();
    self.configs.itemheight = 24
    self.configs.selected = 0;
    self.configs.joypadParent = self;
    self.configs.font = UIFont.NewSmall;
    self.configs.doDrawItem = self.drawConfigs;
    self.configs.drawBorder = true;
    self.configs.backgroundColor = {r=0, g=0, b=0, a=0.0};
    self.configs:addColumn(getTranslate("UI_Settings_ConfigName"), 0);
    self:addChild(self.configs);

    self.entry = ISTextEntryBox:new("EtherConfig-"..tostring(getConfigList():size()+1), 10, self.configs.y + self.configs.height + 10, self.width / 2 - 60, 24);
    self.entry.font = UIFont.Small;
    self.entry:initialise();
    self.entry:instantiate();
    self:addChild(self.entry);

    local saveButton = UIButton:new(self.entry.x + self.entry.width + 10, self.entry.y, 80, 24, getTranslate("UI_Settings_ConfigSave"), function ()
        local configName = self.entry:getText();
        if (configName ~= "") then
            saveConfig(configName);
            self:updateConfigsList();
        end
    end)
    saveButton:initialise();
    saveButton:instantiate();
    saveButton:setAnchorLeft(true);
    saveButton:setAnchorRight(false);
    saveButton:setAnchorTop(false);
    saveButton:setAnchorBottom(true);
    saveButton.update = function ()
        local text = self.entry:getText();
        if (text ~= "") then
            saveButton.isEnable = true;
        else
            saveButton.isEnable = false;
        end
    end
    self:addChild(saveButton)

    local loadButton = UIButton:new(saveButton.x + saveButton.width + 10, saveButton.y, 80, 24, getTranslate("UI_Settings_ConfigLoad"), function ()
        local configName = self.configs.items[self.configs.selected].item;
        if (configName ~= nil) then
            loadConfig(configName);
            EtherMain.accentColor = {r = getAccentUIColor():getR(), g = getAccentUIColor():getG(), b = getAccentUIColor():getB(), a = 1.0};
        end
    end)
    loadButton:initialise();
    loadButton:instantiate();
    loadButton:setAnchorLeft(true);
    loadButton:setAnchorRight(false);
    loadButton:setAnchorTop(false);
    loadButton:setAnchorBottom(true);
    loadButton.update = function ()
        local config = self.configs.items[self.configs.selected];
        if (config ~= nil) then
            loadButton.isEnable = true;
        else
            loadButton.isEnable = false;
        end
    end

    self:addChild(loadButton)

    local deleteButton = UIButton:new(loadButton.x + loadButton.width + 10, loadButton.y, 80, 24, getTranslate("UI_Settings_ConfigDelete"), function ()
        local configName = self.configs.items[self.configs.selected].item;
        if (configName ~= nil) then
            deleteConfig(configName);
            self:updateConfigsList();
        end
    end)
    deleteButton:initialise();
    deleteButton:instantiate();
    deleteButton:setAnchorLeft(true);
    deleteButton:setAnchorRight(false);
    deleteButton:setAnchorTop(false);
    deleteButton:setAnchorBottom(true);
    deleteButton.update = function ()
        local config = self.configs.items[self.configs.selected];
        if (config ~= nil) then
            deleteButton.isEnable = true;
        else
            deleteButton.isEnable = false;
        end
    end
    self:addChild(deleteButton)

    self.accentColor = self:addColorPickerWithLabel(getTranslate("UI_Settings_AccentColor"), function ()
        local picker = ISColorPicker:new(getMouseX(), getMouseY())
        picker:initialise()
        picker.pickedTarget = self
        picker.resetFocusTo = self
        picker:setInitialColor(getAccentUIColor());
        picker.pickedFunc = function (target, color, mouseUp)
            self.accentColor.backgroundColor = {r = getAccentUIColor():getR(), g = getAccentUIColor():getG(), b = getAccentUIColor():getB(), a = 1.0};
            setAccentUIColor(color.r, color.g, color.b);
            EtherMain.accentColor = {r = getAccentUIColor():getR(), g = getAccentUIColor():getG(), b = getAccentUIColor():getB(), a = 1.0};
        end;
        picker:addToUIManager();
    end, getAccentUIColor())

    self.playerColors = self:addColorPickerWithLabel(getTranslate("UI_Settings_PlayersColor"), function ()
        local picker = ISColorPicker:new(getMouseX(), getMouseY())
        picker:initialise()
        picker.pickedTarget = self
        picker.resetFocusTo = self
        picker:setInitialColor(getPlayersUIColor());
        picker.pickedFunc = function (target, color, mouseUp)
            self.playerColors.backgroundColor = {r = getPlayersUIColor():getR(), g = getPlayersUIColor():getG(), b = getPlayersUIColor():getB(), a = 1.0};
            setPlayersUIColor(color.r, color.g, color.b);
        end;
        picker:addToUIManager();
    end, getPlayersUIColor())

    self.vehicleColors = self:addColorPickerWithLabel(getTranslate("UI_Settings_VehicleColor"), function ()
        local picker = ISColorPicker:new(getMouseX(), getMouseY())
        picker:initialise()
        picker.pickedTarget = self
        picker.resetFocusTo = self
        picker:setInitialColor(getVehicleUIColor());
        picker.pickedFunc = function (target, color, mouseUp)
            self.vehicleColors.backgroundColor = {r = getVehicleUIColor():getR(), g = getVehicleUIColor():getG(), b = getVehicleUIColor():getB(), a = 1.0};
            setVehicleUIColor(color.r, color.g, color.b);
        end;
        picker:addToUIManager();
    end, getVehicleUIColor())

    self.zombieColors = self:addColorPickerWithLabel(getTranslate("UI_Settings_ZombiesColor"), function ()
        local picker = ISColorPicker:new(getMouseX(), getMouseY())
        picker:initialise()
        picker.pickedTarget = self
        picker.resetFocusTo = self
        picker:setInitialColor(getZombieUIColor());
        picker.pickedFunc = function (target, color, mouseUp)
            self.zombieColors.backgroundColor = {r = getZombieUIColor():getR(), g = getZombieUIColor():getG(), b = getZombieUIColor():getB(), a = 1.0};
            setZombieUIColor(color.r, color.g, color.b);
        end;
        picker:addToUIManager();
    end, getZombieUIColor())

    self:addButtonWithLabel(getTranslate("UI_Settings_ResetLuaLabel"), getTranslate("UI_Settings_ResetLuaButton"), function ()
        getCore():ResetLua("default", "Force")
    end, true);

    self:updateConfigsList();
    self:updatePanel();
end


--*********************************************************
--* Инициализация списка конфигов
--*********************************************************
function EtherSettingsPanel:updateConfigsList()
    self.lastSelectedIndex = self.configs.selected or 0;
    self.configs:clear();

    local configList = getConfigList();
    for i=0, configList:size() - 1 do
        local config = configList:get(i)
        self.configs:addItem("Config", config);
    end
    self.configs.selected = self.lastSelectedIndex;
end

--*********************************************************
--* Отрисовка конфигов
--*********************************************************
function EtherSettingsPanel:drawConfigs(y, item, alt)
    if y + self:getYScroll() + self.itemheight < 0 or y + self:getYScroll() >= self.height then
        return y + self.itemheight
    end

    if self.selected == item.index then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b);
    end

    if alt then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, 0.3, 0.3, 0.3);
    end


    self:drawText(tostring(item.item), 5 + self.columns[1].size, y + 5, 1, 1, 1, 1, UIFont.Small);

    return y + self.itemheight;
end

--*********************************************************
--* Обработка событий колесика мыши
--*********************************************************
function EtherSettingsPanel:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del * 40));
	return true;
end

--*********************************************************
--* Обработка prerender
--*********************************************************
function EtherSettingsPanel:prerender()
    self:setStencilRect(0,10,self:getWidth(),self:getHeight() - 20);
    ISPanel.prerender(self);
end

--*********************************************************
--* Обработка render
--*********************************************************
function EtherSettingsPanel:render()
    ISPanel.render(self);
    self:clearStencilRect();
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EtherSettingsPanel:new(posX, posY, width, height)
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
    self.buttonList = {}; -- Список всех кнопок
    self.rows = 0;

    return menuTableData;
end