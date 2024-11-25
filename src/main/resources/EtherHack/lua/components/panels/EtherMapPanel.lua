require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
EtherMapPanel = ISPanel:derive("EtherMapPanel"); -- Наследование от ISPanel

--*********************************************************
--* Обработка prerender
--*********************************************************
function EtherMapPanel:prerender()
    self:setStencilRect(0,10,self:getWidth(),self:getHeight() - 20);
    ISPanel.prerender(self);
end

--*********************************************************
--* Обработка render
--*********************************************************
function EtherMapPanel:render()
    ISPanel.render(self);
    self:clearStencilRect();

    if self.localPlayer == nil then 
        self:drawTextCentre(self.workInGameText, self.width / 2, self.height / 2, 1.0, 1.0, 1.0, 1.0, UIFont.Large)
    end;
end

--*********************************************************
--* Обработка событий колесика мыши
--*********************************************************
function EtherMapPanel:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del * 40));

    if self:getMouseX() > 10 and self:getMouseY() > 10 and self:getMouseX() < self.map.width + 10 and self:getMouseY() < self.map.height + 10 then
        self.map:onMouseWheel(del);
    end 
	return true;
end

--*********************************************************
--* Добавление чекбоксов
--*********************************************************
function EtherMapPanel:addCheckBox(title, method, isSelected)
    local rows = self.rows;
    local checkboxX = 10;
    local checkboxY = self.map.y + self.map.height + 10 + rows * 20;

    local checkbox = UICheckbox:new(checkboxX, checkboxY, title, isSelected, method);
    checkbox:initialise();
    checkbox:instantiate();
    checkbox:setAnchorLeft(true);
    checkbox:setAnchorRight(false);
    checkbox:setAnchorTop(false);
    checkbox:setAnchorBottom(true);
    self:addChild(checkbox);

    self:setScrollHeight(self:getScrollHeight() + checkbox.height + 5);

    self.rows = self.rows + 1;

    table.insert(self.uiElements, checkbox);
end

--*********************************************************
--* Создание метки
--*********************************************************
function EtherMapPanel:addLabel(posX, posY, title)
    local label = ISLabel:new(posX, posY + 3, getTextManager():getFontHeight(UIFont.Small), title, 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
    return label
end

--*********************************************************
--* Создание кнопки
--*********************************************************
function EtherMapPanel:addButton(posX, posY, buttonTitle, onClick)
    local buttonWidth, buttonHeight = 130, 16;
    local button = UIButton:new(posX, posY, buttonWidth, buttonHeight, buttonTitle, onClick)
    button:initialise();
    button:instantiate();
    button:setAnchorLeft(true);
    button:setAnchorRight(false);
    button:setAnchorTop(false);
    button:setAnchorBottom(true);
    self:addChild(button);
    table.insert(self.uiElements, button);
    return button
end

--*********************************************************
--* Создание кнопки с заголовком
--*********************************************************
function EtherMapPanel:addButtonWithLabel(title, buttonTitle, func)
    local rows = self.rows;
    local buttonY = self.map.y + self.map.height + 10 + rows * 25;
    
    self:addLabel(10, buttonY - 3, title)
    local button = self:addButton(self:getWidth() - 130 - 20, buttonY, buttonTitle, func)

    self.rows = self.rows + 1;

    return button
end

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function EtherMapPanel:createChildren()
    ISPanel.createChildren(self);

    self:setScrollChildren(true)
    self:setScrollHeight(0)
    self:addScrollBars();

    if self.localPlayer == nil then return end;

    self.map = UIMap:new(10, 10, self.width - 20, self.height - 200)
    self.map:initialise()
    self.map:instantiate()
    self.map:initDataAndStyle()
    self.map.mapAPI:resetView()
    self.map:restoreSettings()
    self:addChild(self.map)

    self:addButtonWithLabel(getTranslate("UI_Map_MiniMapOpenLabel"), getTranslate("UI_Map_MiniMapOpenButton"), function ()
        UIMovableMiniMap.openPanel()
    end)

    self:addCheckBox(getTranslate("UI_Map_DrawLocalPlayer"), function (isChecked)
        toggleMapDrawLocalPlayer(isChecked)
    end, isMapDrawLocalPlayer())

    self:addCheckBox(getTranslate("UI_Map_DrawOtherPlayers"), function (isChecked)
        toggleMapDrawAllPlayers(isChecked)
    end, isMapDrawAllPlayers())

    self:addCheckBox(getTranslate("UI_Map_DrawVehicles"), function (isChecked)
        toggleMapDrawVehicles(isChecked)
    end, isMapDrawVehicles())

    self:addCheckBox(getTranslate("UI_Map_DrawZombies"), function (isChecked)
        toggleMapDrawZombies(isChecked)
    end, isMapDrawZombies())

end
--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EtherMapPanel:new(posX, posY, width, height)
    local menuTableData = {};

    menuTableData = ISPanel:new(posX, posY, width, height);
    setmetatable(menuTableData, self);
    menuTableData.background = true;
	menuTableData.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.0};
	menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    menuTableData.moveWithMouse = true;
    menuTableData.workInGameText = getTranslate("UI_Map_PanelWorkOnlyInGame");
    menuTableData.localPlayer = getPlayer();
    self.__index = self;

    self.uiElements = {};
    self.rows = 0;

    return menuTableData;
end