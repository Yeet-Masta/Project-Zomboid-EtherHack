require "ISUI/ISPanel"

EtherPlayerEditor = ISPanel:derive("EtherPlayerEditor");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)


function EtherPlayerEditor:initialise()
    ISPanel.initialise(self);
    self:create();
end


function EtherPlayerEditor:setVisible(visible)
    --    self.parent:setVisible(visible);
    self.javaObject:setVisible(visible);
    for _,v in ipairs(self.windows) do
       v:removeFromUIManager();
    end
end

function EtherPlayerEditor:subPanelPreRender()
    self:setStencilRect(0,0,self:getWidth(),self:getHeight());
    ISPanel.prerender(self);
end

function EtherPlayerEditor:subPanelRender()
    ISPanel.render(self);
    self:clearStencilRect();
end

function EtherPlayerEditor:prerender()
    self.selectedPerk = nil
    ISPanel.prerender(self)
end

function EtherPlayerEditor:render()
    EtherPlayerEditor.instance = self -- to support reloading in lua debugger

    local z = 20

    self:updateWeight()

    self:updateButtons();

    self:drawText(getText("IGUI_PlayerStats_PlayerStats"), self.width/2 - (getTextManager():MeasureStringX(UIFont.Cred1, getText("IGUI_PlayerStats_PlayerStats")) / 2), 10, 1,1,1,1, UIFont.Cred1);

    self:setStencilRect(0,60,self:getWidth(),self:getHeight()-60);

    local items = {
        getText("IGUI_PlayerStats_Username"), self.char:getUsername(), nil,
        getText("IGUI_PlayerStats_DisplayName"), self.char:getDisplayName(), self.changeUsernameBtn,
        getText("UI_characreation_forename") .. ":", self.char:getDescriptor():getForename(), self.changeForename,
        getText("UI_characreation_surname") .. ":", self.char:getDescriptor():getSurname(), self.changeSurname,
        getText("IGUI_PlayerStats_Profession"), getText("IGUI_PlayerStats_Profession"), self.changeProfession,
        getText("IGUI_char_Survived_For") .. ":", self.char:getTimeSurvived(), nil,
        getText("IGUI_char_Zombies_Killed") .. ":", tostring(self.char:getZombieKills()), nil
    }

    local labelWidMax = 0
    local valueWidMax = 0
    local buttonMaxWid = 0
    for i=1,#items,3 do
        local labelWid = getTextManager():MeasureStringX(UIFont.Small, items[i])
        labelWidMax = math.max(labelWidMax, labelWid)
        local valueWid = getTextManager():MeasureStringX(UIFont.Small, items[i+1])
        valueWidMax = math.max(valueWidMax, valueWid)
        local button = items[i+2]
        if button then
            buttonMaxWid = math.max(buttonMaxWid, button.width)
        end
    end

    local btnPadY = self.buttonPadY
    z = 0;
    for i=1,#items,3 do
        self.mainPanel:drawText(items[i], 10, z + btnPadY, self.variableColor.r,self.variableColor.g,self.variableColor.b,self.variableColor.a, UIFont.Small)
        self.mainPanel:drawText(items[i+1], 10 + labelWidMax + 10, z + btnPadY, 1,1,1,1, UIFont.Small)
        local button = items[i+2]
        if button then
            button:setX(10 + labelWidMax + 10 + valueWidMax + 10)
            button:setY(z)
        end
        z = z + self.buttonHeight + 1
    end

    local nextColumnX = 10 + labelWidMax + 10 + valueWidMax + 10 + buttonMaxWid + 40
    local nextColumnZ = 0
    
    local chatMuted = getText("Sandbox_ThumpNoChasing_option1");
    if not self.char:isAllChatMuted() then
        chatMuted = getText("Sandbox_ThumpNoChasing_option2")
    end
    items = {
        getText("IGUI_PlayerStats_AccessLevel"), self.char:getAccessLevel(), self.changeAccessLvlBtn,
        getText("IGUI_PlayerStats_ChatMuted"), chatMuted, self.muteAllBtn,
        getText("IGUI_PlayerStats_UserLogs"), tostring(#self.userlogs), self.userlogBtn,
        getText("IGUI_PlayerStats_WarningPts"), tostring(self.warningPoint), self.warningPointsBtn,
        getText("IGUI_char_Weight") .. ":", tostring(math.floor(self.char:getNutrition():getWeight())), self.weightBtn
    }

    labelWidMax = 0
    valueWidMax = 0
    for i=1,#items,3 do
        local labelWid = getTextManager():MeasureStringX(UIFont.Small, items[i])
        labelWidMax = math.max(labelWidMax, labelWid)
        local valueWid = getTextManager():MeasureStringX(UIFont.Small, items[i+1])
        valueWidMax = math.max(valueWidMax, valueWid)
    end

    for i=1,#items,3 do
        self.mainPanel:drawText(items[i], nextColumnX, nextColumnZ + btnPadY, self.variableColor.r,self.variableColor.g,self.variableColor.b,self.variableColor.a, UIFont.Small)
        self.mainPanel:drawText(items[i+1], nextColumnX + labelWidMax + 10, nextColumnZ + btnPadY, 1,1,1,1, UIFont.Small)
        local button = items[i+2]
        if button then
            button:setX(nextColumnX + labelWidMax + 10 + valueWidMax + 10)
            button:setY(nextColumnZ)
        end
        nextColumnZ = nextColumnZ + self.buttonHeight + 1
    end

    nextColumnZ = nextColumnZ + self.buttonHeight + 1
    self.manageInvBtn:setX(nextColumnX);
    self.manageInvBtn:setY(nextColumnZ);

    z = z + 20;
    self.mainPanel:drawText(getText("IGUI_char_Traits") .. ":", 10, z, self.variableColor.r,self.variableColor.g,self.variableColor.b,self.variableColor.a, UIFont.Small);
    z = z + FONT_HGT_SMALL + 4
    if #self.traits > 0 then
        local traitWidMax = 0
        local buttonWidMax = 0
        for i,v in ipairs(self.traits) do
            traitWidMax = math.max(traitWidMax, 25 + getTextManager():MeasureStringX(UIFont.Small, v.label))
            buttonWidMax = self.traitsRemoveButtons[v.label].width
        end
        local x = 25;
        local y = z + 10;
        local rowHgt = math.max(self.buttonHeight, 18 + 2)
        local dyTex = (rowHgt - 18) / 2
        for i,v in ipairs(self.traits) do
            v:setY(y + dyTex);
            v:setX(x);
            v:setVisible(true);
            self.mainPanel:drawText(v.label, 25 + x, y, 1,1,1,1, UIFont.Small)
            self.traitsRemoveButtons[v.label]:setY(y)
            self.traitsRemoveButtons[v.label]:setX(x + traitWidMax + 10)
            if x + 40 + (traitWidMax + 10 + buttonWidMax) * 2 <= self.width - 10 - 30 then
                x = x + 40 + traitWidMax + 10 + buttonWidMax;
            else
                x = 25;
                if i < #self.traits then
                    y = y + rowHgt
                end
            end
        end
        x = 25;
        y = y + rowHgt + 10;
        self.mainPanel:drawRectBorder(10, z, self.width - 30, y + self.buttonHeight + 10 - z, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
        z = y;
        self.addTraitBtn:setY(z);
        self.addTraitBtn:setX(x);
    else
        self.mainPanel:drawRectBorder(10, z, self.width - 30, 10 + self.buttonHeight + 10, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
        z = z + 10;
        self.addTraitBtn:setY(z);
        self.addTraitBtn:setX(25);
    end

    z = z + self.buttonHeight + 10 + 20

    z = z + self.xpListBox.itemheight -- column titles
    self.xpListBox:setY(z);
    self.addXpBtn:setY(self.xpListBox:getY() + self.xpListBox.height + 10);
    self.addXpBtn:setX(self.xpListBox:getX());
    self.addLvlBtn:setY(self.addXpBtn:getY());
    self.addLvlBtn:setX(self.addXpBtn:getRight() + 5);
    self.loseLvlBtn:setY(self.addXpBtn:getY());
    self.loseLvlBtn:setX(self.addLvlBtn:getRight() + 5);
    self.maxAllSkillBtn:setY(self.addXpBtn:getY());
    self.maxAllSkillBtn:setX(self.loseLvlBtn:getRight() + 5);

    local yoff = 0;
    local columnLeft = self.xpListBox.columnLeft
    local columnWidth = self.xpListBox.columnWidth
    self.mainPanel:drawRectBorder(self.xpListBox.x, self.xpListBox.y - self.xpListBox.itemheight + yoff, self.xpListBox:getWidth(), self.xpListBox.itemheight + 1, 1, self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self.mainPanel:drawRect(self.xpListBox.x, 1 + self.xpListBox.y - self.xpListBox.itemheight + yoff, self.xpListBox.width, self.xpListBox.itemheight,self.listHeaderColor.a,self.listHeaderColor.r, self.listHeaderColor.g, self.listHeaderColor.b);
    self.mainPanel:drawRect(self.xpListBox.x + columnLeft[2], 1 + self.xpListBox.y - self.xpListBox.itemheight + yoff, 1, self.xpListBox.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self.mainPanel:drawRect(self.xpListBox.x + columnLeft[3], 1 + self.xpListBox.y - self.xpListBox.itemheight + yoff, 1, self.xpListBox.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self.mainPanel:drawRect(self.xpListBox.x + columnLeft[4], 1 + self.xpListBox.y - self.xpListBox.itemheight + yoff, 1, self.xpListBox.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self.mainPanel:drawRect(self.xpListBox.x + columnLeft[5], 1 + self.xpListBox.y - self.xpListBox.itemheight + yoff, 1, self.xpListBox.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self.mainPanel:drawRect(self.xpListBox.x + columnLeft[5] + columnWidth[5], 1 + self.xpListBox.y - self.xpListBox.itemheight + yoff, 1, self.xpListBox.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);

    yoff = yoff + (self.xpListBox.itemheight - FONT_HGT_SMALL) / 2
    self.mainPanel:drawText(self.xpListBox.columnLabel[1], self.xpListBox.x + 5, self.xpListBox.y - self.xpListBox.itemheight + yoff, 1,1,1,1,UIFont.Small);
    self.mainPanel:drawText(self.xpListBox.columnLabel[2], self.xpListBox.x + columnLeft[2] + 10, self.xpListBox.y - self.xpListBox.itemheight + yoff, 1,1,1,1,UIFont.Small);
    self.mainPanel:drawText(self.xpListBox.columnLabel[3], self.xpListBox.x + columnLeft[3] + 10, self.xpListBox.y - self.xpListBox.itemheight + yoff, 1,1,1,1,UIFont.Small);
    self.mainPanel:drawText(self.xpListBox.columnLabel[4], self.xpListBox.x + columnLeft[4] + 10, self.xpListBox.y - self.xpListBox.itemheight + yoff, 1,1,1,1,UIFont.Small);
    self.mainPanel:drawText(self.xpListBox.columnLabel[5], self.xpListBox.x + columnLeft[5] + 10, self.xpListBox.y - self.xpListBox.itemheight + yoff, 1,1,1,1,UIFont.Small);

    self.mainPanel:setScrollHeight(self.addXpBtn:getBottom() + 20)

    local panelHeight = self.mainPanel.y + self.mainPanel:getScrollHeight()
    self:setHeight(math.min(panelHeight, getCore():getScreenHeight() - 40))
--    self:setHeight(600)

    self:clearStencilRect();
end

function EtherPlayerEditor:updateWeight()
    self.syncWeightTimer = self.syncWeightTimer + 1
    if self.syncWeightTimer > 100 then
        local args = { id = self.char:getOnlineID() }
        sendClientCommand(getPlayer(), 'player', 'syncWeight', args)
        self.syncWeightTimer = 0
    end
end

function EtherPlayerEditor:canModifyThis()
    if not isClient() then return true; end
   return ((self.char:getCurrentSquare() and self.char:isExistInTheWorld()) or not self.char:getCurrentSquare()) and ( (self.admin:getAccessLevel() == "Admin") or (self.admin:getAccessLevel() == "Moderator" and (self.char:getAccessLevel() == "None" or self.char:getAccessLevel() == "GM" or self.char:getAccessLevel() == "Overseer" or self.char:getAccessLevel() == "Observer")) )
end

function EtherPlayerEditor:updateButtons()
    local buttonEnable = true;
    self.addTraitBtn.enable = buttonEnable;
    self.changeProfession.enable = buttonEnable;
    self.changeForename.enable = buttonEnable;
    self.changeSurname.enable = buttonEnable;
    self.muteAllBtn.enable = buttonEnable;
    self.addXpBtn.enable = buttonEnable;
    self.addLvlBtn.enable = buttonEnable and (self.selectedPerk ~= nil)
    self.loseLvlBtn.enable = buttonEnable and (self.selectedPerk ~= nil)
    self.maxAllSkillBtn.enable = buttonEnable
    self.userlogBtn.enable = buttonEnable;
    self.manageInvBtn.enable = buttonEnable;
    self.warningPointsBtn.enable = buttonEnable;
    self.changeAccessLvlBtn.enable = buttonEnable;
    self.changeUsernameBtn.enable = buttonEnable;
    for _,image in ipairs(self.traits) do
        self.traitsRemoveButtons[image.label].enable = buttonEnable;
    end
end

function EtherPlayerEditor:onMouseWheelXXX(del)
    if not self.xpListBox:isMouseOver() then
        self:setYScroll(self:getYScroll() - (del*50));
        return true;
    else
        return false;
    end
end


function EtherPlayerEditor:create()

    self.mainPanel = ISPanel:new(0, 60, self:getWidth(), self:getHeight() - 60)
    self.mainPanel:initialise()
    self.mainPanel:instantiate()
    self.mainPanel:setAnchorRight(true)
    self.mainPanel:setAnchorLeft(true)
    self.mainPanel:setAnchorTop(true)
    self.mainPanel:setAnchorBottom(true)
    self.mainPanel:noBackground()
    self.mainPanel.borderColor = {r=0, g=0, b=0, a=0};
    self.mainPanel.moveWithMouse = true;
    self.mainPanel.render = EtherPlayerEditor.subPanelRender
    self.mainPanel.prerender = EtherPlayerEditor.subPanelPreRender
    self.mainPanel:addScrollBars();
    self:addChild(self.mainPanel)
    self.mainPanel:setScrollChildren(true)

    self.mainPanel.onMouseWheel = function(self, del)
        if self:getScrollHeight() > 0 then
            if self.parent.xpListBox:isMouseOver() then
                return false
            end
            self:setYScroll(self:getYScroll() - (del * 40))
            return true
        end
        return false
    end

    self.closeBtn = ISButton:new(self.width - 10 - 100, 10, 100, math.max(25, FONT_HGT_SMALL + 3 * 2), getText("UI_btn_close"), self, self.onOptionMouseDown);
    self.closeBtn.internal = "CLOSE";
    self.closeBtn:initialise();
    self.closeBtn:instantiate();
    self.closeBtn:setAnchorLeft(true);
    self.closeBtn:setAnchorTop(true);
    self.closeBtn:setAnchorBottom(false);
    self.closeBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.closeBtn);

    self.addTraitBtn = ISButton:new(16, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_AddTrait"), self, self.onOptionMouseDown);
    self.addTraitBtn.internal = "ADDTRAIT";
    self.addTraitBtn:initialise();
    self.addTraitBtn:instantiate();
    self.addTraitBtn:setAnchorLeft(true);
    self.addTraitBtn:setAnchorTop(false);
    self.addTraitBtn:setAnchorBottom(true);
    self.addTraitBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.addTraitBtn);

    self.changeProfession = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_Change"), self, self.onOptionMouseDown);
    self.changeProfession.internal = "CHANGEPROFESSION";
    self.changeProfession:initialise();
    self.changeProfession:instantiate();
    self.changeProfession:setAnchorLeft(true);
    self.changeProfession:setAnchorTop(false);
    self.changeProfession:setAnchorBottom(true);
    self.changeProfession.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.changeProfession);

    self.changeUsernameBtn = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_Change"), self, self.onOptionMouseDown);
    self.changeUsernameBtn.internal = "CHANGENAME";
    self.changeUsernameBtn.changedName = "Displayname";
    self.changeUsernameBtn:initialise();
    self.changeUsernameBtn:instantiate();
    self.changeUsernameBtn:setAnchorLeft(true);
    self.changeUsernameBtn:setAnchorTop(false);
    self.changeUsernameBtn:setAnchorBottom(true);
    self.changeUsernameBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.changeUsernameBtn);

    self.changeForename = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_Change"), self, self.onOptionMouseDown);
    self.changeForename.internal = "CHANGENAME";
    self.changeForename.changedName = "Forename";
    self.changeForename:initialise();
    self.changeForename:instantiate();
    self.changeForename:setAnchorLeft(true);
    self.changeForename:setAnchorTop(false);
    self.changeForename:setAnchorBottom(true);
    self.changeForename.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.changeForename);

    self.changeSurname = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_Change"), self, self.onOptionMouseDown);
    self.changeSurname.internal = "CHANGENAME";
    self.changeSurname.changedName = "Surname";
    self.changeSurname:initialise();
    self.changeSurname:instantiate();
    self.changeSurname:setAnchorLeft(true);
    self.changeSurname:setAnchorTop(false);
    self.changeSurname:setAnchorBottom(true);
    self.changeSurname.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.changeSurname);

    self.muteAllBtn = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("UI_Scoreboard_Mute"), self, self.onOptionMouseDown);
    self.muteAllBtn.internal = "MUTEALL";
    self.muteAllBtn:initialise();
    self.muteAllBtn:instantiate();
    self.muteAllBtn:setAnchorLeft(true);
    self.muteAllBtn:setAnchorTop(false);
    self.muteAllBtn:setAnchorBottom(true);
    self.muteAllBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.muteAllBtn);

    self.changeAccessLvlBtn = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_Change"), self, self.onOptionMouseDown);
    self.changeAccessLvlBtn.internal = "CHANGEACCESSLEVEL";
    self.changeAccessLvlBtn:initialise();
    self.changeAccessLvlBtn:instantiate();
    self.changeAccessLvlBtn:setAnchorLeft(true);
    self.changeAccessLvlBtn:setAnchorTop(false);
    self.changeAccessLvlBtn:setAnchorBottom(true);
    self.changeAccessLvlBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.changeAccessLvlBtn);

    self.userlogBtn = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_See"), self, self.onOptionMouseDown);
    self.userlogBtn.internal = "SEEUSERLOG";
    self.userlogBtn:initialise();
    self.userlogBtn:instantiate();
    self.userlogBtn:setAnchorLeft(true);
    self.userlogBtn:setAnchorTop(false);
    self.userlogBtn:setAnchorBottom(true);
--    self.userlogBtn.enable = false;
    self.userlogBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.userlogBtn);

    self.warningPointsBtn = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_Add"), self, self.onOptionMouseDown);
    self.warningPointsBtn.internal = "ADDWARNINGPOINT";
    self.warningPointsBtn:initialise();
    self.warningPointsBtn:instantiate();
    self.warningPointsBtn:setAnchorLeft(true);
    self.warningPointsBtn:setAnchorTop(false);
    self.warningPointsBtn:setAnchorBottom(true);
    self.warningPointsBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.warningPointsBtn);

    self.weightBtn = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_char_Weight"), self, self.onOptionMouseDown);
    self.weightBtn.internal = "CHANGEWEIGHT";
    self.weightBtn:initialise();
    self.weightBtn:instantiate();
    self.weightBtn:setAnchorLeft(true);
    self.weightBtn:setAnchorTop(false);
    self.weightBtn:setAnchorBottom(true);
    self.weightBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.weightBtn);
    
    self.manageInvBtn = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_ManageInventory", self.char:getUsername()), self, self.onOptionMouseDown);
    self.manageInvBtn.internal = "MANAGEINV";
    self.manageInvBtn:initialise();
    self.manageInvBtn:instantiate();
    self.manageInvBtn:setAnchorLeft(true);
    self.manageInvBtn:setAnchorTop(false);
    self.manageInvBtn:setAnchorBottom(true);
    self.manageInvBtn.borderColor = self.buttonBorderColor;
    self.mainPanel:addChild(self.manageInvBtn);
    if self.char == self.admin then
        self.manageInvBtn:setVisible(false);
    end

    self.xpListBox = ISScrollingListBox:new(10, 30, self.width - 30, 300);
    self.xpListBox:initialise();
    self.xpListBox:instantiate();
    self.xpListBox.itemheight = FONT_HGT_SMALL + 2 * 2;
    self.xpListBox.selected = 0;
    self.xpListBox.joypadParent = self;
    self.xpListBox.font = UIFont.NewSmall;
    self.xpListBox.doDrawItem = self.drawPerk;
    self.xpListBox.drawBorder = true;
    self.xpListBox.mainUI = self;
    self.mainPanel:addChild(self.xpListBox);

    self.xpListBox.columnLabel = {
        getText("IGUI_PlayerStats_Perk"),
        getText("IGUI_PlayerStats_Level"),
        getText("IGUI_PlayerStats_XP"),
        getText("IGUI_PlayerStats_Boost"),
        getText("IGUI_PlayerStats_Multiplier")
    }
    self.xpListBox.columnWidth = { 140, 50, 70, 80, 120 }
    self.xpListBox.columnWidth[3] = getTextManager():MeasureStringX(self.xpListBox.font, "99999.99/99999")
    self:updateColumns()

    self.addXpBtn = ISButton:new(16, self.xpListBox.y + 200, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_AddXP"), self, self.onOptionMouseDown);
    self.addXpBtn.internal = "ADDXP";
    self.addXpBtn:initialise();
    self.mainPanel:addChild(self.addXpBtn);

--    self.addGlobalXP.borderColor = self.buttonBorderColor;
    
    self.addLvlBtn = ISButton:new(self.addXpBtn:getRight() + 5, self.addXpBtn.y, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_LevelUp"), self, self.onOptionMouseDown);
    self.addLvlBtn.internal = "LEVELPERK";
    self.addLvlBtn:initialise();
    self.mainPanel:addChild(self.addLvlBtn);
    
    self.loseLvlBtn = ISButton:new(self.addLvlBtn:getRight() + 5, self.addXpBtn.y, self.buttonWidth, self.buttonHeight, getText("IGUI_PlayerStats_LevelDown"), self, self.onOptionMouseDown);
    self.loseLvlBtn.internal = "LOWERPERK";
    self.loseLvlBtn:initialise();
    self.mainPanel:addChild(self.loseLvlBtn);

    self.maxAllSkillBtn = ISButton:new(self.loseLvlBtn:getRight() + 5, self.addXpBtn.y, self.buttonWidth, self.buttonHeight, "Max All Skills", self, self.onOptionMouseDown);
    self.maxAllSkillBtn.internal = "MAXALLSKILL";
    self.maxAllSkillBtn:initialise();
    self.mainPanel:addChild(self.maxAllSkillBtn);

--    self:addScrollBars();

    self:loadTraits();
    self:loadProfession();
    self:loadPerks();
    self:loadUserlog();
end

function EtherPlayerEditor:onOptionMouseDown(button, x, y)
    if button.internal == "CLOSE" then
        self:setVisible(false);
        self:removeFromUIManager();
    end

    if button.internal == "ADDTRAIT" then
        local modal = ISPlayerStatsChooseTraitUI:new(self.x + 200, self.y + 200, 350, 250, nil, EtherPlayerEditor.onAddTrait, self.char)
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end

    if button.internal == "CHANGENAME" then
        local defaultTxt = "";
        if button.changedName == "Forename" then
            defaultTxt = EtherPlayerEditor.instance.char:getDescriptor():getForename();
        elseif button.changedName == "Surname" then
            defaultTxt = EtherPlayerEditor.instance.char:getDescriptor():getSurname();
        elseif button.changedName == "Displayname" then
            defaultTxt = EtherPlayerEditor.instance.char:getDisplayName();
        end

        local modal = ISTextBox:new(self.x + 200, 200, 280, 180, "Change " .. button.changedName, defaultTxt, nil, EtherPlayerEditor.onChangeName, self.char:getPlayerNum(), self.char, button.changedName);
        modal.changedName = button.changedName;
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end

    if button.internal == "CHANGEPROFESSION" then
        local modal = ISPlayerStatsChooseProfessionUI:new(self.x + 200, self.y + 200, 350, 250, nil, EtherPlayerEditor.onChangeProfession, self.char)
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end

    if button.internal == "CHANGEACCESSLEVEL" then
        local modal = ISPlayerStatsChooseAccessLevelUI:new(self.x + 200, self.y + 200, 350, 250, nil, EtherPlayerEditor.onChangeAccessLevel, self.char, self.admin)
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end

    if button.internal == "ADDXP" then
        local modal = ISPlayerStatsAddXPUI:new(self.x + 200, self.y + 200, 300, 250, nil, EtherPlayerEditor.onAddXP)
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end
    
    if button.internal == "LEVELPERK" then
        self.char:LevelPerk(self.selectedPerk.perk, false);
        self.char:getXp():setXPToLevel(self.selectedPerk.perk, self.char:getPerkLevel(self.selectedPerk.perk));
        SyncXp(self.char)
        self:loadPerks();
        if self.selectedPerk.perk == Perks.Strength or self.selectedPerk.perk == Perks.Fitness then
            self:loadTraits();
        end
    end
    
    if button.internal == "LOWERPERK" then
        self.char:LoseLevel(self.selectedPerk.perk);
        self.char:getXp():setXPToLevel(self.selectedPerk.perk, self.char:getPerkLevel(self.selectedPerk.perk));
        SyncXp(self.char)
        self:loadPerks();
        if self.selectedPerk.perk == Perks.Strength or self.selectedPerk.perk == Perks.Fitness then
            self:loadTraits();
        end
    end

    if button.internal == "MAXALLSKILL" then
        for i=0, Perks.getMaxIndex() - 1 do
            local perk = PerkFactory.getPerk(Perks.fromIndex(i));
            if perk and perk:getParent() ~= Perks.None then
                for i=1, 10 do
                    self.char:LevelPerk(perk, false);
                    self.char:getXp():setXPToLevel(perk, self.char:getPerkLevel(perk));
                    SyncXp(self.char)
                end
            end
        end
        self:loadPerks();
        self:loadTraits();
    end

    if button.internal == "SEEUSERLOG" then
        requestUserlog(self.char:getUsername());
        local modal = ISPlayerStatsUserlogUI:new(self.x + 200, self.y + 200, 600, 550, nil, EtherPlayerEditor.onUserlogOption, self.char, self.userlogs);
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end

    if button.internal == "ADDWARNINGPOINT" then
        local modal = ISPlayerStatsWarningPointUI:new(self.x + 200, self.y + 200, 300, 250, nil, EtherPlayerEditor.onAddWarningPoint, self.char);
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end

    if button.internal == "CHANGEWEIGHT" then
        local modal = ISTextBox:new(self.x + 200, 200, 280, 180, "Change weight (30-130)", tostring(math.floor(self.char:getNutrition():getWeight())), nil, EtherPlayerEditor.onChangeWeight, self.char:getPlayerNum(), self.char);
        modal:initialise();
        modal.entry:setOnlyNumbers(true)
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end

    if button.internal == "MUTEALL" then
        self.char:setAllChatMuted(not self.char:isAllChatMuted());
        sendPlayerStatsChange(self.char);
    end
    
    if button.internal == "MANAGEINV" then
        local modal = ISPlayerStatsManageInvUI:new(self.x + 100, self.y + 100, 900, 650, self.char);
        modal:initialise();
        modal:addToUIManager();
        table.insert(EtherPlayerEditor.instance.windows, modal);
    end
end

function EtherPlayerEditor:onAddWarningPoint(button, reason, amount)
    addWarningPoint(EtherPlayerEditor.instance.char:getUsername(), reason, tonumber(amount));
    EtherPlayerEditor.instance.warningPoint = EtherPlayerEditor.instance.warningPoint + tonumber(amount);
    requestUserlog(EtherPlayerEditor.instance.char:getUsername());
end

function EtherPlayerEditor:onAddTrait(button, trait)
    if button.internal == "OK" then
        EtherPlayerEditor.instance.char:getTraits():add(trait:getType());
        SyncXp(EtherPlayerEditor.instance.char);
        EtherPlayerEditor.instance:loadTraits();
    end
end

function EtherPlayerEditor:onChangeProfession(button, prof)
    if button.internal == "OK" then
        EtherPlayerEditor.instance.char:getDescriptor():setProfession(prof:getType());
        sendPlayerStatsChange(EtherPlayerEditor.instance.char);
        EtherPlayerEditor.instance:loadProfession()
    end
end

function EtherPlayerEditor:onChangeAccessLevel(button, accessLevel)
    if button.internal == "OK" then
        EtherPlayerEditor.instance.char:setAccessLevel(accessLevel);
        sendPlayerStatsChange(EtherPlayerEditor.instance.char);
    end
end

--[[
function EtherPlayerEditor:onAddGlobalXP(button, player)
    if button.internal == "OK" then
        if button.parent.entry:getText() and button.parent.entry:getText() ~= "" then
            EtherPlayerEditor.instance.char:getXp():addGlobalXP(tonumber(button.parent.entry:getText()));
            sendAddXp(EtherPlayerEditor.instance.char, nil, tonumber(button.parent.entry:getText()), false, true);
--            EtherPlayerEditor.instance.loadPerks();
        end
    end
end
--]]

function EtherPlayerEditor:onChangeName(button, player, changedName)
    if button.internal == "OK" then
        local doneIt = true;
        if (changedName ~= "Tag" and button.parent.entry:getText() and button.parent.entry:getText() ~= "") or changedName == "Tag" then
            if changedName == "Forename" then
                if EtherPlayerEditor.instance.char:getDescriptor():getForename() == button.parent.entry:getText() then
                    doneIt = false;
                else
                    EtherPlayerEditor.instance.char:getDescriptor():setForename(button.parent.entry:getText());
                    sendPlayerStatsChange(EtherPlayerEditor.instance.char);
                end
            elseif changedName == "Surname" then
                if EtherPlayerEditor.instance.char:getDescriptor():getSurname() == button.parent.entry:getText() then
                    doneIt = false;
                else
                    EtherPlayerEditor.instance.char:getDescriptor():setSurname(button.parent.entry:getText());
                    sendPlayerStatsChange(EtherPlayerEditor.instance.char);
                end
            elseif changedName == "Displayname" then
                if EtherPlayerEditor.instance.char:getDisplayName() == button.parent.entry:getText() then
                    doneIt = false;
                else
                    EtherPlayerEditor.instance.char:setDisplayName(button.parent.entry:getText());
                    sendPlayerStatsChange(EtherPlayerEditor.instance.char);
                end
            end
            if not doneIt then
                local modal = ISModalDialog:new(0,0, 250, 150,  getText("IGUI_PlayerStats_SameName", changedName), false, nil, nil, nil);
                modal:initialise()
                modal:addToUIManager()
                table.insert(EtherPlayerEditor.instance.windows, modal);
            end
        end
    end
end

function EtherPlayerEditor:onChangeWeight(button, player)
    if button.internal == "OK" then
        if button.parent.entry:getText() and button.parent.entry:getText() ~= "" then
            local w = tonumber(button.parent.entry:getText())
            if w >= 30 and w <= 130 then
                player:getNutrition():setWeight(w)
                local args = { weight = w, id = player:getOnlineID() }
                sendClientCommand(getPlayer(), 'player', 'setWeight', args)
            end
        end
    end
end

function EtherPlayerEditor:onRemoveTrait(button, x, y)
    self.char:getTraits():remove(button.internal);
    SyncXp(self.char);
    self:loadTraits();
end

EtherPlayerEditor.loadTraits = function(self)
    for _,image in ipairs(self.traits) do
        self.mainPanel:removeChild(image)
        self.mainPanel:removeChild(self.traitsRemoveButtons[image.label]);
    end
    self.traits = {};

    for i=0, self.char:getTraits():size() - 1 do
        local trait = TraitFactory.getTrait(self.char:getTraits():get(i));
        if trait and trait:getTexture() then
            local textImage = ISImage:new(0, 0, trait:getTexture():getWidth(), trait:getTexture():getHeight(), trait:getTexture());
            textImage:initialise();
            textImage.label = trait:getLabel();
            textImage:setMouseOverText(trait:getDescription());
            textImage:setVisible(false);
            self.mainPanel:addChild(textImage);
            table.insert(self.traits, textImage);
            local newButton = ISButton:new(self.buttonOffset, self.height - 30, self.buttonWidth, self.buttonHeight, getText("ContextMenu_Remove"), self, self.onRemoveTrait);
            newButton.internal = trait:getType();
            newButton:initialise();
            newButton:instantiate();
            newButton:setAnchorLeft(true);
            newButton:setAnchorTop(false);
            newButton:setAnchorBottom(true);
            newButton.borderColor = self.buttonBorderColor;
            self.mainPanel:addChild(newButton);
            self.traitsRemoveButtons[trait:getLabel()] = newButton;
        end
    end
    self.Strength = self.char:getPerkLevel(Perks.Strength)
    self.Fitness = self.char:getPerkLevel(Perks.Fitness)
end

EtherPlayerEditor.loadProfession = function(self)
    self.profession = nil;
    if self.char:getDescriptor() and self.char:getDescriptor():getProfession() then
        local prof = ProfessionFactory.getProfession(self.char:getDescriptor():getProfession());
        if prof then
            self.profession = prof:getName();
        end
    end
end



EtherPlayerEditor.loadPerks = function(self)
    local previousSelection = self.xpListBox.selected;
    self.xpListBox:clear();
    local maxNameWidth = 0
    for i=0, Perks.getMaxIndex() - 1 do
        local perk = PerkFactory.getPerk(Perks.fromIndex(i));
        if perk and perk:getParent() ~= Perks.None then
            local newPerk = {};
            newPerk.perk = Perks.fromIndex(i);
            newPerk.name = perk:getName() .. " (" .. PerkFactory.getPerkName(perk:getParent()) .. ")";
            newPerk.level = self.char:getPerkLevel(Perks.fromIndex(i));
            newPerk.xpToLevel = perk:getXpForLevel(newPerk.level + 1);
            newPerk.xp = self.char:getXp():getXP(newPerk.perk) - ISSkillProgressBar.getPreviousXpLvl(perk, newPerk.level);
            newPerk.xp = round(newPerk.xp,2)
            local xpBoost = self.char:getXp():getPerkBoost(newPerk.perk);
            if xpBoost == 1 then
                newPerk.boost = "75%";
            elseif xpBoost == 2 then
                newPerk.boost = "100%";
            elseif xpBoost == 3 then
                newPerk.boost = "125%";
            else
                newPerk.boost = "50%";
            end
            newPerk.multiplier = self.char:getXp():getMultiplier(newPerk.perk);
            self.xpListBox:addItem(newPerk.name, newPerk);
            maxNameWidth = math.max(maxNameWidth, getTextManager():MeasureStringX(self.xpListBox.font, newPerk.name))
        end
    end
    self.xpListBox:sort()
    self.xpListBox.selected = previousSelection;
    self.xpListBox.columnWidth[1] = maxNameWidth + 15
    self:updateColumns()
end

function EtherPlayerEditor:drawPerk(y, item, alt)
    local a = 0.9;

    self:drawRectBorder(0, (y), self:getWidth(), self.itemheight - 1, a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    if self.selected == item.index then
        self:drawRect(0, (y), self:getWidth(), self.itemheight - 1, 0.3, 0.7, 0.35, 0.15);
        self.mainUI.selectedPerk = item.item;
    end

    self:drawRect(self.columnLeft[2], y-1, 1, self.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(self.columnLeft[3], y-1, 1, self.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(self.columnLeft[4], y-1, 1, self.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(self.columnLeft[5], y-1, 1, self.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);
    self:drawRect(self.columnLeft[5] + self.columnWidth[5], y-1, 1, self.itemheight,1,self.borderColor.r, self.borderColor.g, self.borderColor.b);

    local yoff = 2;
    self:drawText(item.item.name, 5, y + yoff, 1, 1, 1, a, self.font);
    self:drawText(item.item.level .. "", self.columnLeft[2] + 10, y + yoff, 1, 1, 1, a, self.font);
    if item.item.xpToLevel == -1 then
        self:drawText("MAX", self.columnLeft[3] + 10, y + yoff, 1, 1, 1, a, self.font);
    else
        self:drawText(item.item.xp .. "/" .. item.item.xpToLevel, self.columnLeft[3] + 10, y + yoff, 1, 1, 1, a, self.font);
    end
    self:drawText(item.item.boost, self.columnLeft[4] + 10, y + yoff, 1, 1, 1, a, self.font);
    self:drawText(item.item.multiplier .. "", self.columnLeft[5] + 10, y + yoff, 1, 1, 1, a, self.font);

    return y + self.itemheight;
end

function EtherPlayerEditor:onAddXP(button, perk, amount, addGlobalXP)
    if amount and amount ~= "" then
        amount = tonumber(amount);
        EtherPlayerEditor.instance.char:getXp():AddXP(perk:getType(), amount, false, false, true);
        sendAddXp(EtherPlayerEditor.instance.char, perk:getType(), amount);
        EtherPlayerEditor.instance:loadPerks();
    end
end

function EtherPlayerEditor:updateColumns()
    local left = 0
    self.xpListBox.columnLeft = {}
    for i=1,#self.xpListBox.columnWidth do
        self.xpListBox.columnLeft[i] = left
        local labelWidth = getTextManager():MeasureStringX(UIFont.Small, self.xpListBox.columnLabel[i])
        self.xpListBox.columnWidth[i] = math.max(self.xpListBox.columnWidth[i], labelWidth + 20)
        left = left + self.xpListBox.columnWidth[i]
    end
end

function EtherPlayerEditor:new(x, y, width, height, playerChecked, admin)
    local o = {};
    o = ISPanel:new(x, y, width, height);
    setmetatable(o, self);
    self.__index = self;
    o.char = playerChecked;
    o.variableColor={r=0.9, g=0.55, b=0.1, a=1};
    o.admin = admin;
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    o.listHeaderColor = {r=0.4, g=0.4, b=0.4, a=0.3};
    o.backgroundColor = {r=0, g=0, b=0, a=0.8};
    o.buttonBorderColor = {r=0.7, g=0.7, b=0.7, a=0.5};
    o.traits = {}
    o.traitsRemoveButtons = {};
    o.perks = {};
    o.perksAddXPButtons = {};
    o.userlogs = {};

    o.xoffset = 110;
    o.buttonOffset = 220;
    o.buttonWidth = 60;
    o.buttonPadY = 1
    o.buttonHeight = FONT_HGT_SMALL + o.buttonPadY * 2;
    o.warningPoint = 0;
    o.syncWeightTimer = 50
    EtherPlayerEditor.instance = o;
    o.windows = {};
    o.moveWithMouse = true;
    ISDebugMenu.RegisterClass(self);
    return o;
end

EtherPlayerEditor.loadUserlog = function(self)
    requestUserlog(self.char:getUsername());
end

EtherPlayerEditor.receiveUserLog = function(username, logs)
    if not EtherPlayerEditor.instance or username ~= EtherPlayerEditor.instance.char:getUsername() then return; end
    EtherPlayerEditor.instance.userlogs = {};
--    EtherPlayerEditor.instance.userlogBtn.enable = logs:size() > 0;
    EtherPlayerEditor.instance.warningPoint = 0;
    for i=0,logs:size()-1 do
        local log = logs:get(i);
        if log:getType() == UserlogType.WarningPoint:toString() then
            EtherPlayerEditor.instance.warningPoint = EtherPlayerEditor.instance.warningPoint + log:getAmount();
        end
        table.insert(EtherPlayerEditor.instance.userlogs, {type = log:getType(), text = log:getText(), issuedBy = log:getIssuedBy(), amount = log:getAmount()});
    end
end

function EtherPlayerEditor.OnOpenPanel()
    if EtherPlayerEditor.instance then
        EtherPlayerEditor.instance:close()
    end
    local x = getCore():getScreenWidth() / 2 - (800 / 2);
    local y = getCore():getScreenHeight() / 2 - (800 / 2);
    local ui = EtherPlayerEditor:new(x,y,800,800, getPlayer(), getPlayer())
    ui:initialise();
    ui:addToUIManager();
    ui:setVisible(true);
end

Events.OnReceiveUserlog.Add(EtherPlayerEditor.receiveUserLog);
