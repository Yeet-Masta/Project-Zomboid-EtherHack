require "ISUI/ISPanel"

EtherAdminMenu = ISPanel:derive("EtherAdminMenu");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)

--************************************************************************--
--** ISPanel:initialise
--**
--************************************************************************--

function EtherAdminMenu:initialise()
    ISPanel.initialise(self);
    self:create();
end


function EtherAdminMenu:setVisible(visible)
    --    self.parent:setVisible(visible);
    self.javaObject:setVisible(visible);
end

function EtherAdminMenu:render()
    local z = 20;

    self:drawText(getText("IGUI_AdminPanel_AdminPanel"), self.width/2 - (getTextManager():MeasureStringX(UIFont.Medium, getText("IGUI_AdminPanel_AdminPanel")) / 2), z, 1,1,1,1, UIFont.Medium);
    z = z + 30;

end

function EtherAdminMenu:create()
    local btnWid = 150
    local btnHgt = math.max(25, FONT_HGT_SMALL + 3 * 2)
    local btnGapY = 5
    local padBottom = 10

    local y = 70;

    -- if isCoopHost() then
        self.beAdmin = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_EnableAdminPower"), self, EtherAdminMenu.onOptionMouseDown);
        self.beAdmin.internal = "BEADMIN";
        self.beAdmin:initialise();
        self.beAdmin:instantiate();
        self.beAdmin.borderColor = self.buttonBorderColor;
        -- if getAccessLevel() == "admin" then
            self.beAdmin.title = getText("IGUI_AdminPanel_DisableAdminPower");
        -- end
        self:addChild(self.beAdmin);
        self.beAdmin.tooltip = getText("IGUI_AdminPanel_TooltipAdminPower");
        y = y + btnHgt + btnGapY;
    -- end

    self.dbBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_SeeDB"), self, EtherAdminMenu.onOptionMouseDown);
    self.dbBtn.internal = "WHITELIST";
    self.dbBtn:initialise();
    self.dbBtn:instantiate();
    self.dbBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.dbBtn);

    -- if getAccessLevel() == "observer" then
        self.dbBtn.enable = false;
    -- end

    y = y + btnHgt + btnGapY

    self.checkStatsBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_CheckYourStats"), self, EtherAdminMenu.onOptionMouseDown);
    self.checkStatsBtn.internal = "CHECKSTATS";
    self.checkStatsBtn:initialise();
    self.checkStatsBtn:instantiate();
    self.checkStatsBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.checkStatsBtn);

    y = y + btnHgt + btnGapY

    local title = getText("IGUI_AdminPanel_AdminPower")
    self.adminPowerBtn = ISButton:new(10, y, btnWid, btnHgt, title, self, EtherAdminMenu.onOptionMouseDown);
    self.adminPowerBtn.internal = "ADMINPOWER";
    self.adminPowerBtn:initialise();
    self.adminPowerBtn:instantiate();
    self.adminPowerBtn.borderColor = self.buttonBorderColor;
    self.adminPowerBtn.tooltip = getText("IGUI_AdminPanel_TooltipEditAdminPower");
    self:addChild(self.adminPowerBtn);
    
    y = y + btnHgt + btnGapY
    
    local title = getText("IGUI_AdminPanel_ItemList")
    self.itemListBtn = ISButton:new(10, y, btnWid, btnHgt, title, self, EtherAdminMenu.onOptionMouseDown);
    self.itemListBtn.internal = "ITEMLIST";
    self.itemListBtn:initialise();
    self.itemListBtn:instantiate();
    self.itemListBtn.borderColor = self.buttonBorderColor;
--    self.itemListBtn.tooltip = getText("IGUI_AdminPanel_TooltipEditAdminPower");
    self:addChild(self.itemListBtn);

    y = y + btnHgt + btnGapY
    self.seeOptionsBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_SeeServerOptions"), self, EtherAdminMenu.onOptionMouseDown);
    self.seeOptionsBtn.internal = "SEEOPTIONS";
    self.seeOptionsBtn:initialise();
    self.seeOptionsBtn:instantiate();
    self.seeOptionsBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.seeOptionsBtn);

    y = y + btnHgt + btnGapY
    -- if getAccessLevel() == "admin" then
        self.nonpvpzoneBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_NonPvpZone"), self, EtherAdminMenu.onOptionMouseDown);
        self.nonpvpzoneBtn.internal = "NONPVPZONE";
        self.nonpvpzoneBtn:initialise();
        self.nonpvpzoneBtn:instantiate();
        self.nonpvpzoneBtn.borderColor = self.buttonBorderColor;
        self:addChild(self.nonpvpzoneBtn);
    y = y + btnHgt + btnGapY
    -- end

    self.seeFactionBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_SeeFaction") .. " (" .. Faction.getFactions():size() ..")", self, EtherAdminMenu.onOptionMouseDown);
    self.seeFactionBtn.internal = "SEEFACTIONS";
    self.seeFactionBtn:initialise();
    self.seeFactionBtn:instantiate();
    self.seeFactionBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.seeFactionBtn);
    y = y + btnHgt + btnGapY

    self.seeSafehousesBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_SeeSafehouses") .. " (".. SafeHouse.getSafehouseList():size() .. ")", self, EtherAdminMenu.onOptionMouseDown);
    self.seeSafehousesBtn.internal = "SEESAFEHOUSES";
    self.seeSafehousesBtn:initialise();
    self.seeSafehousesBtn:instantiate();
    self.seeSafehousesBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.seeSafehousesBtn);
    y = y + btnHgt + btnGapY

    self.safezoneBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_Safezone"), self, EtherAdminMenu.onOptionMouseDown);
    self.safezoneBtn.internal = "SAFEZONE";
    self.safezoneBtn:initialise();
    self.safezoneBtn:instantiate();
    self.safezoneBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.safezoneBtn);
    y = y + btnHgt + btnGapY

    self.seeTicketsBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_SeeTickets"), self, EtherAdminMenu.onOptionMouseDown);
    self.seeTicketsBtn.internal = "SEETICKETS";
    self.seeTicketsBtn:initialise();
    self.seeTicketsBtn:instantiate();
    self.seeTicketsBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.seeTicketsBtn);
    y = y + btnHgt + btnGapY

    self.miniScoreboardBtn = ISButton:new(10, y, btnWid, btnHgt, getText("IGUI_AdminPanel_MiniScoreboard"), self, EtherAdminMenu.onOptionMouseDown);
    self.miniScoreboardBtn.internal = "MINISCOREBOARD";
    self.miniScoreboardBtn:initialise();
    self.miniScoreboardBtn:instantiate();
    self.miniScoreboardBtn.borderColor = self.buttonBorderColor;
    self:addChild(self.miniScoreboardBtn);
    self.miniScoreboardBtn.tooltip = getText("IGUI_AdminPanel_TooltipMiniScoreboard");
    y = y + btnHgt + btnGapY

    y = 70
    self.packetCountsBtn = ISButton:new(10 + btnWid + 20, y, btnWid, btnHgt, getText("IGUI_AdminPanel_PacketCounts"), self, EtherAdminMenu.onOptionMouseDown)
    self.packetCountsBtn.internal = "PACKETCOUNTS"
    self.packetCountsBtn:initialise()
    self.packetCountsBtn:instantiate()
    self.packetCountsBtn.borderColor = self.buttonBorderColor
    self:addChild(self.packetCountsBtn)
    self.packetCountsBtn.tooltip = getText("IGUI_AdminPanel_TooltipPacketCounts")
    y = y + btnHgt + btnGapY

    self.sandboxOptionsBtn = ISButton:new(10 + btnWid + 20, y, btnWid, btnHgt, getText("IGUI_AdminPanel_SandboxOptions"), self, EtherAdminMenu.onOptionMouseDown)
    self.sandboxOptionsBtn.internal = "SANDBOX"
    self.sandboxOptionsBtn:initialise()
    self.sandboxOptionsBtn:instantiate()
    self.sandboxOptionsBtn.borderColor = self.buttonBorderColor
    self:addChild(self.sandboxOptionsBtn)
    self.sandboxOptionsBtn.tooltip = getTextOrNull("IGUI_AdminPanel_TooltipSandboxOptions")
    y = y + btnHgt + btnGapY

    self.climateOptionsBtn = ISButton:new(10 + btnWid + 20, y, btnWid, btnHgt, getText("IGUI_Adm_Weather_ClimateControl"), self, EtherAdminMenu.onOptionMouseDown)
    self.climateOptionsBtn.internal = "CLIMATE"
    self.climateOptionsBtn:initialise()
    self.climateOptionsBtn:instantiate()
    self.climateOptionsBtn.borderColor = self.buttonBorderColor
    self:addChild(self.climateOptionsBtn)
    self.climateOptionsBtn.tooltip = getTextOrNull("IGUI_AdminPanel_TooltipClimateOptions")
    y = y + btnHgt + btnGapY

    self.showStatisticsBtn = ISButton:new(10 + btnWid + 20, y, btnWid, btnHgt, getText("IGUI_AdminPanel_ShowStatistics"), self, EtherAdminMenu.onOptionMouseDown)
    self.showStatisticsBtn.internal = "STATISTICS"
    self.showStatisticsBtn:initialise()
    self.showStatisticsBtn:instantiate()
    self.showStatisticsBtn.borderColor = self.buttonBorderColor
    self:addChild(self.showStatisticsBtn)
    self.showStatisticsBtn.tooltip = getTextOrNull("IGUI_AdminPanel_TooltipShowStatistics")
    y = y + btnHgt + btnGapY

    local width = 0
    local bottom = 0
    for _,child in pairs(self:getChildren()) do
        width = math.max(width, child:getWidth())
        bottom = math.max(bottom, child:getBottom())
    end
    for _,child in pairs(self:getChildren()) do
        if child:getX() > 10 then
            child:setX(10 + width + 20)
        end
        child:setWidth(width)
    end

    self:setWidth(10 + width + 20 + width + 10)
    
    self.cancel = ISButton:new((self:getWidth() - btnWid) / 2, bottom + btnGapY, btnWid, btnHgt, getText("UI_btn_close"), self, EtherAdminMenu.onOptionMouseDown);
    self.cancel.internal = "CANCEL";
    self.cancel:initialise();
    self.cancel:instantiate();
    self.cancel.borderColor = self.buttonBorderColor;
    self:addChild(self.cancel);

    self:setHeight(self.cancel:getBottom() + btnGapY)

    self:updateButtons();
end

function EtherAdminMenu:updateButtons()
    local enabled = false;
    -- if getAccessLevel() ~= "" then
        enabled = true;
    -- else
    --     self:close();
    -- end;
    self.dbBtn.enable = enabled;
    self.checkStatsBtn.enable = enabled;
    self.seeOptionsBtn.enable = enabled;
    self.seeFactionBtn.enable = enabled;
    self.seeSafehousesBtn.enable = enabled;
    self.seeTicketsBtn.enable = enabled;
    self.miniScoreboardBtn.enable = enabled;
    self.packetCountsBtn.enable = enabled;
    self.sandboxOptionsBtn.enable = enabled;
    -- if getAccessLevel() == "observer" then
    --     self.dbBtn.enable = false;
    -- end
    self.adminPowerBtn.enable = enabled;
    self.itemListBtn.enable = enabled;
    self.climateOptionsBtn.enable = enabled;
    self.showStatisticsBtn.enable = enabled;
    if self.nonpvpzoneBtn then
        self.nonpvpzoneBtn.enable = enabled;
    end;
    -- if getAccessLevel() ~= "admin" then
        -- self.safezoneBtn.enable = false;
    -- end
end

function EtherAdminMenu:removeAdmin()
    setAdmin();
    self.beAdmin.title = getText("IGUI_AdminPanel_EnableAdminPower");
    self:updateButtons();
end

function EtherAdminMenu:onOptionMouseDown(button, x, y)
    if button.internal == "WHITELIST" then
        if ISWhitelistViewer.instance then
            ISWhitelistViewer.instance:closeSelf()
        end
        local modal = ISWhitelistViewer:new(50, 200, 1200, 650)
        modal:initialise();
        modal:addToUIManager();
    end
    if button.internal == "ADMINPOWER" then
        if ISAdminPowerUI.instance then
            ISAdminPowerUI.instance:close()
        end
        local modal = ISAdminPowerUI:new(50, 200, 480, 350, getPlayer())
        modal:initialise();
        modal:addToUIManager();
    end
    if button.internal == "ITEMLIST" then
        if ISItemsListViewer.instance then
            ISItemsListViewer.instance:close()
        end
        local modal = ISItemsListViewer:new(50, 200, 850, 650, getPlayer())
        modal:initialise();
        modal:addToUIManager();
    end
    if button.internal == "BUILDCHEAT" then
        ISBuildMenu.cheat = not ISBuildMenu.cheat;
        if ISBuildMenu.cheat then
            self.buildCheatBtn.title = getText("IGUI_AdminPanel_DisableBuildCheat");
        else
            self.buildCheatBtn.title = getText("IGUI_AdminPanel_EnableBuildCheat");
        end
    end
    if button.internal == "BEADMIN" then
        setAdmin();
        -- if getAccessLevel() == "admin" then
            self.beAdmin.title = getText("IGUI_AdminPanel_DisableAdminPower");
            self.adminModal = ISModalDialog:new(getCore():getScreenWidth() / 2 - 250,getCore():getScreenHeight() - 155, 500, 150, getText("IGUI_AdminPanel_ModalAdminPower"), false, self, EtherAdminMenu.removeAdmin);
            self.adminModal:initialise()
            self.adminModal:addToUIManager()
        -- else
        --     if self.adminModal and self.adminModal:getIsVisible() then
        --         self.adminModal:destroy();
        --     end
        --     self.beAdmin.title = getText("IGUI_AdminPanel_EnableAdminPower");
        -- end
    end
    if button.internal == "CHECKSTATS" then
        if ISPlayerStatsUI.instance then
            ISPlayerStatsUI.instance:close()
        end
        local ui = ISPlayerStatsUI:new(50,50,800,800, getPlayer(), getPlayer())
        ui:initialise();
        ui:addToUIManager();
        ui:setVisible(true);
    end
    if button.internal == "SEEOPTIONS" then
        if ISServerOptions.instance then
            ISServerOptions.instance:close()
        end
        local ui = ISServerOptions:new(50,50,600,600, getPlayer())
        ui:initialise();
        ui:addToUIManager();
    end
    if button.internal == "SEEFACTIONS" then
        if ISFactionsList.instance then
            ISFactionsList.instance:close()
        end
        local ui = ISFactionsList:new(50,50,600,600, getPlayer());
        ui:initialise();
        ui:addToUIManager();
    end
    if button.internal == "SEESAFEHOUSES" then
        if ISSafehousesList.instance then
            ISSafehousesList.instance:close()
        end
        local ui = ISSafehousesList:new(50,50,600,600, getPlayer());
        ui:initialise();
        ui:addToUIManager();
    end
    if button.internal == "NONPVPZONE" then
        if ISPvpZonePanel.instance then
            ISPvpZonePanel.instance:close()
        end
        local ui = ISPvpZonePanel:new(50,50,600,600, getPlayer());
        ui:initialise();
        ui:addToUIManager();
    end
    if button.internal == "SAFEZONE" then
        if ISAddSafeZoneUI.instance then
            ISAddSafeZoneUI.instance:close();
        end
        local ui = ISAddSafeZoneUI:new(getCore():getScreenWidth() / 2 - 210, getCore():getScreenHeight() / 2 - 200, 420, 400, getPlayer());
        ui:initialise();
        ui:addToUIManager();
    end
    if button.internal == "SEETICKETS" then
        if ISAdminTicketsUI.instance then
            ISAdminTicketsUI.instance:close()
        end
        local ui = ISAdminTicketsUI:new(50,50,600,600, getPlayer());
        ui:initialise();
        ui:addToUIManager();
    end
    if button.internal == "MINISCOREBOARD" then
        if ISMiniScoreboardUI.instance then
            ISMiniScoreboardUI.instance:close()
        end
        local ui = ISMiniScoreboardUI:new(50,50,200,300, getPlayer());
        ui:initialise();
        ui:addToUIManager();
    end
    if button.internal == "PACKETCOUNTS" then
        if ISPacketCounts.instance then
            ISPacketCounts.instance:closeSelf()
        end
        local ui = ISPacketCounts:new(50,50,1024,600, getPlayer())
        ui:initialise()
        ui:addToUIManager()
    end
    if button.internal == "SANDBOX" then
        if ISServerSandboxOptionsUI.instance then
            ISServerSandboxOptionsUI.instance:close()
        end
        local ui = ISServerSandboxOptionsUI:new(150, 150,800, 600)
        ui:initialise()
        ui:addToUIManager()
    end
    if button.internal == "CLIMATE" then
        --local ui = ISAdminWeather:new(150, 150,800, 600, getPlayer())
        --ui:initialise()
        --ui:addToUIManager()
        local ui = ISAdminWeather.OnOpenPanel();
        ui:onMadeActive();
    end
    if button.internal == "STATISTICS" then
        if ISStatisticsUI.instance then
            ISStatisticsUI.instance:close()
        end
        local ui = ISStatisticsUI:new(50, 50, getPlayer())
        ui:initialise()
        ui:addToUIManager()
    end
    if button.internal == "CANCEL" then
        self:close()
    end
    self:updateButtons();
end

function EtherAdminMenu:close()
    self:setVisible(false);
    self:removeFromUIManager();
    EtherAdminMenu.instance = nil
end

function EtherAdminMenu:new(x, y, width, height)
    local o = {};
    o = ISPanel:new(x, y, width, height);
    setmetatable(o, self);
    self.__index = self;
    o.variableColor={r=0.9, g=0.55, b=0.1, a=1};
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    o.backgroundColor = {r=0, g=0, b=0, a=0.8};
    o.buttonBorderColor = {r=0.7, g=0.7, b=0.7, a=0.5};
    o.zOffsetSmallFont = 25;
    o.moveWithMouse = true;
    EtherAdminMenu.instance = o
    return o;
end

function EtherAdminMenu.OnSafehousesChanged()
    if EtherAdminMenu.instance then
        local button = EtherAdminMenu.instance.seeSafehousesBtn
        button:setTitle( getText("IGUI_AdminPanel_SeeSafehouses") .. " (".. SafeHouse.getSafehouseList():size() .. ")" )
    end
end

Events.OnSafehousesChanged.Add(EtherAdminMenu.OnSafehousesChanged)