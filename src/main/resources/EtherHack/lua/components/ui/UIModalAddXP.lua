require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
UIModalAddXP = ISPanel:derive("UIModalAddXP");

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function UIModalAddXP:createChildren()
    self.perkList = {}
    for i=1,Perks.getMaxIndex() do
        local perk = PerkFactory.getPerk(Perks.fromIndex(i - 1));
        if perk and perk:getParent() ~= Perks.None then
            table.insert(self.perkList, perk)
        end
    end
    table.sort(self.perkList, function(a,b) return not string.sort(a:getName(), b:getName()) end)

    self.combo = ISComboBox:new(10, 10, self.width - 20, 30, nil,nil);
    self.combo:initialise();
    self:addChild(self.combo);
    for i=1,#self.perkList do
        local perk = self.perkList[i]
        self.combo:addOption(perk:getName() .. " (" .. PerkFactory.getPerkName(perk:getParent()) .. ")");
    end

    local amountLbl = ISLabel:new(10, self.combo.x + self.combo.height, 20, getText("IGUI_PlayerStats_Amount"), 1, 1, 1, 1, UIFont.Small, true)
    amountLbl:initialise()
    amountLbl:instantiate()
    self:addChild(amountLbl)

    self.entry = ISTextEntryBox:new("1", 10, amountLbl.x + amountLbl.height + 30, self.width - 20, 24);
    self.entry.font = UIFont.Small;
    self.entry:initialise();
    self.entry:instantiate();
    self.entry:setOnlyNumbers(true);
    self:addChild(self.entry);

    self.acceptButton = UIButton:new(10, self.height - 35, 100, 25, getTranslate("UI_PlayerEditor_PlayerSkills_ModalAccept"), 
    function() 
        local amount = self.entry:getText();
        local skill = self.perkList[self.combo.selected];

        UIModalAddXP.instance:setVisible(false);
        UIModalAddXP.instance:removeFromUIManager();
        UIModalAddXP.instance = nil;

        if amount and amount ~= "" then
            amount = tonumber(amount);
            getPlayer():getXp():AddXP(skill:getType(), amount, false, false, true);
            sendAddXp(getPlayer(), skill:getType(), amount);
            UISkillTable.instance:updateSkills();
        end
    end)
    self.acceptButton:initialise();
    self.acceptButton:instantiate();
    self.acceptButton:setAnchorLeft(true);
    self.acceptButton:setAnchorRight(false);
    self.acceptButton:setAnchorTop(false);
    self.acceptButton:setAnchorBottom(true);
    self.acceptButton.isOnlyInGame = true;
    self:addChild(self.acceptButton);

    self.closeButton = UIButton:new(self.acceptButton.x + self.acceptButton.width + 10, self.height - 35, 100, 25, getTranslate("UI_PlayerEditor_PlayerSkills_ModalClose"), 
    function() 
        UIModalAddXP.instance:setVisible(false);
        UIModalAddXP.instance:removeFromUIManager();
        UIModalAddXP.instance = nil;
    end)
    self.closeButton:initialise();
    self.closeButton:instantiate();
    self.closeButton:setAnchorLeft(true);
    self.closeButton:setAnchorRight(false);
    self.closeButton:setAnchorTop(false);
    self.closeButton:setAnchorBottom(true);
    self.closeButton.isOnlyInGame = true;
    self:addChild(self.closeButton);
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function UIModalAddXP:new()
    local menuTableData = {};
    local width, height = 230, 130;
    local positionX = getCore():getScreenWidth() / 2 - width / 2;
    local positionY = getCore():getScreenHeight() / 2 - height/ 2;

    menuTableData = ISPanel:new(positionX, positionY, width, height);
    setmetatable(menuTableData, self);
    self.__index = self;
    menuTableData.variableColor={r=0.9, g=0.55, b=0.1, a=1};
    menuTableData.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    menuTableData.backgroundColor = {r=0, g=0, b=0, a=0.8};
    menuTableData.moveWithMouse = true;
    UIModalAddXP.instance = menuTableData;

    return menuTableData;
end
