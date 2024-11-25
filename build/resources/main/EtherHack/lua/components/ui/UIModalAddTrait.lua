
require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
UIModalAddTrait = ISPanel:derive("UIModalAddTrait");

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function UIModalAddTrait:createChildren()
    for i=0,TraitFactory.getTraits():size()-1 do
        local trait = TraitFactory.getTraits():get(i);
        if not self.localPlayer:getTraits():contains(trait:getType()) then
            if trait:getCost() >= 0 then
                table.insert(self.goodTraits, trait)
            else
                table.insert(self.badTraits, trait)
            end
        end
    end

    self.acceptButton = UIButton:new(10, self.height - 35, 100, 25, getTranslate("UI_PlayerEditor_PlayerTraits_ModalAccept"), 
    function() 
        UIModalAddTrait.instance:setVisible(false);
        UIModalAddTrait.instance:removeFromUIManager();
        UIModalAddTrait.instance = nil;

        local list = self.badTraits;
        if self.traitsSelector.isChecked then
            list = self.goodTraits;
        end
        local trait = list[self.combo.selected];
        
        self.localPlayer:getTraits():add(trait:getType());
        SyncXp(self.localPlayer);
        UITraitsTable.instance:updateTraits();
    end)
    self.acceptButton:initialise();
    self.acceptButton:instantiate();
    self.acceptButton:setAnchorLeft(true);
    self.acceptButton:setAnchorRight(false);
    self.acceptButton:setAnchorTop(false);
    self.acceptButton:setAnchorBottom(true);
    self.acceptButton.isOnlyInGame = true;
    self:addChild(self.acceptButton);
    table.insert(self.buttonList, self.acceptButton);

    self.closeButton = UIButton:new(self.acceptButton.x + self.acceptButton.width + 10, self.height - 35, 100, 25, getTranslate("UI_PlayerEditor_PlayerTraits_ModalClose"), 
    function() 
        UIModalAddTrait.instance:setVisible(false);
        UIModalAddTrait.instance:removeFromUIManager();
        UIModalAddTrait.instance = nil;
    end)
    self.closeButton:initialise();
    self.closeButton:instantiate();
    self.closeButton:setAnchorLeft(true);
    self.closeButton:setAnchorRight(false);
    self.closeButton:setAnchorTop(false);
    self.closeButton:setAnchorBottom(true);
    self.closeButton.isOnlyInGame = true;
    self:addChild(self.closeButton);
    table.insert(self.buttonList, self.closeButton);

    self.combo = ISComboBox:new(10, 10, self.width - 20, 30, nil,nil);
    self.combo:initialise();
    self.goodTrait = {};
    self:addChild(self.combo);

    self.traitsSelector = UICheckbox:new(10, self.combo.y + self.combo.height + 10, getTranslate("UI_PlayerEditor_PlayerTraits_IsGoodTrait"), true, function ()
        self:updateTraitsList();
    end)
    self.traitsSelector:initialise();
    self.traitsSelector:instantiate();
    self:addChild(self.traitsSelector);

    self:updateTraitsList();
end

--*********************************************************
--* Обновление черт характера
--*********************************************************
function UIModalAddTrait:updateTraitsList()
    self.combo:clear();
    local list = self.badTraits;
    if self.traitsSelector.isChecked then
        list = self.goodTraits;
    end
    local tooltipMap = {};
    for _,v in ipairs(list) do
        self.combo:addOption(v:getLabel());
        tooltipMap[v:getLabel()] = v:getDescription();
    end
    self.combo:setToolTipMap(tooltipMap);

    if self.traitsSelector.isChecked then
        local hc = getCore():getGoodHighlitedColor()
        self.combo.textColor = {r=hc:getR(), g=hc:getG(), b=hc:getB(),a=0.9};
    else
        local hc = getCore():getBadHighlitedColor()
        self.combo.textColor = {r=hc:getR(), g=hc:getG(), b=hc:getB(),a=0.9};
    end
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function UIModalAddTrait:new()
    local menuTableData = {};

    local width, height = 230, 110;
    local positionX = getCore():getScreenWidth() / 2 - width / 2;
    local positionY = getCore():getScreenHeight() / 2 - height/ 2;

    menuTableData = ISPanel:new(positionX, positionY, width, height);
    setmetatable(menuTableData, self);
    self.__index = self;
    menuTableData.variableColor={r=0.9, g=0.55, b=0.1, a=1};
    menuTableData.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    menuTableData.backgroundColor = {r=0, g=0, b=0, a=0.8};
    menuTableData.localPlayer = getPlayer();
    menuTableData.comboList = {};
    menuTableData.goodTraits = {};
    menuTableData.badTraits = {};
    menuTableData.buttonList = {};
    menuTableData.moveWithMouse = true;
    UIModalAddTrait.instance = menuTableData;
    return menuTableData;
end
