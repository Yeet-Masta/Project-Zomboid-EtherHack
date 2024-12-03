require "ISUI/ISPanel"

--*********************************************************
--* Подключение модулей
--*********************************************************
local etherModules = {
    "EtherHack/lua/components/override/EtherAdminMenu.lua",
    "EtherHack/lua/components/override/EtherDebugMenu.lua",
    "EtherHack/lua/components/override/EtherEditInventoryItem.lua",
    "EtherHack/lua/components/override/EtherEditWorldObjects.lua",
    "EtherHack/lua/components/ui/UIButtonsPanel.lua",
    "EtherHack/lua/components/ui/UICheckbox.lua",
    "EtherHack/lua/components/ui/UIButton.lua",
    "EtherHack/lua/components/ui/UISlider.lua",
    "EtherHack/lua/components/ui/UIMechanics.lua",
    "EtherHack/lua/components/ui/UIModalAddXP.lua",
    "EtherHack/lua/components/ui/UIMovableMiniMap.lua",
    "EtherHack/lua/components/ui/UIModalAddTrait.lua",
    "EtherHack/lua/components/ui/UIHealth.lua",
    "EtherHack/lua/components/ui/UIItemTables.lua",
    "EtherHack/lua/components/ui/UIMap.lua",
    "EtherHack/lua/components/ui/UISkillTable.lua",
    "EtherHack/lua/components/ui/UITraitsTable.lua",
    "EtherHack/lua/components/panels/EtherInfoPanel.lua",
    "EtherHack/lua/components/panels/EtherCharacterPanel.lua",
    "EtherHack/lua/components/panels/EtherItemCreator.lua",
    "EtherHack/lua/components/panels/EtherPlayerEditor.lua",
    "EtherHack/lua/components/panels/EtherVisualsPanel.lua",
    "EtherHack/lua/components/panels/EtherMapPanel.lua",
    "EtherHack/lua/components/panels/EtherExploitPanel.lua",
    "EtherHack/lua/components/panels/EtherSettingsPanel.lua",
    "EtherHack/lua/components/panels/AnimatedPanel.lua",
    "EtherHack/lua/components/panels/TooltipPanel.lua",
    "EtherHack/lua/components/panels/NotificationPanel.lua"
}

for _, module in ipairs(etherModules) do
    requireExtra(module);
end

EtherMain = ISPanel:derive("EtherMain")
EtherMain.instance = nil
EtherMain.menuKeyID = 210
EtherMain.defaultWidth = 510
EtherMain.defaultHeight = 500
EtherMain.currentTabID = 1
EtherMain.accentColor = {r = getAccentUIColor():getR(), g = getAccentUIColor():getG(), b = getAccentUIColor():getB(), a = 1.0}

function EtherMain:close()

    -- Animate panel closing
    if self.buttonsPanel.currentPanel then
        self.buttonsPanel.currentPanel:hidePanel()
    end
    self:hidePanel()

    -- Show notification
    -- NotificationSystem.info("EtherHack menu closed")

    -- Remove from UI manager after animation
    Events.OnTick.Add(function()
        if self.animState == AnimatedPanel.STATE_HIDDEN then
            Events.OnTick.Remove(self)
            EtherMain.instance:setVisible(false)
            EtherMain.instance:removeFromUIManager()
            EtherMain.instance = nil
        end
    end)
end

function EtherMain:createChildren()
    AnimatedPanel.createChildren(self)

    self.buttonsPanel = UIButtonsPanel:new(0, 0, 50, self.height, self, EtherMain.accentColor)
    self.buttonsPanel:initialise()
    self.buttonsPanel:instantiate()
    self.buttonsPanel:setVisible(true)
    self:addChild(self.buttonsPanel)

    -- Add tooltips to buttons
    self.buttonsPanel:addButton("EtherHack/media/ui/info.png", EtherInfoPanel)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Information Panel")

    self.buttonsPanel:addButton("EtherHack/media/ui/character.png", EtherCharacterPanel)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Character Settings")

    self.buttonsPanel:addButton("EtherHack/media/ui/itemCreator.png", EtherItemCreator)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Item Creator")

    self.buttonsPanel:addButton("EtherHack/media/ui/playerEditor.png", EtherPlayerEditor)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Player Editor")

    self.buttonsPanel:addButton("EtherHack/media/ui/visuals.png", EtherVisualsPanel)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Visual Settings")

    self.buttonsPanel:addButton("EtherHack/media/ui/teleport.png", EtherMapPanel)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Map & Teleport")

    self.buttonsPanel:addButton("EtherHack/media/ui/exploit.png", EtherExploitPanel)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Exploit Options")

    self.buttonsPanel:addButton("EtherHack/media/ui/settings.png", EtherSettingsPanel)
    TooltipSystem.addTooltip(self.buttonsPanel.buttons[#self.buttonsPanel.buttons], "Settings")

    self.buttonsPanel:openPanel(EtherMain.currentTabID)
end

function EtherMain.OnOpenPanel(key)
    if key == EtherMain.menuKeyID then
        if EtherMain.instance ~= nil then
            EtherMain.instance:setVisible(false);
            EtherMain.instance:removeFromUIManager();
            EtherMain.instance = nil;
            return
        end

        -- Create new instance
        EtherMain.instance = EtherMain:new()
        EtherMain.instance:initialise()
        EtherMain.instance:instantiate()
        EtherMain.instance:addToUIManager()
        EtherMain.instance:setVisible(true)
        EtherMain.instance:setAlwaysOnTop(false)

        -- Show opening animation
        --EtherMain.instance:showPanel()

        -- Play open sound
        -- SoundSystem.onWindowOpen()

        -- Show welcome notification
        -- NotificationSystem.success("EtherHack menu opened")
    end
end

function EtherMain:new()
    local posX = getCore():getScreenWidth() / 2 - EtherMain.defaultWidth / 2
    local posY = getCore():getScreenHeight() / 2 - EtherMain.defaultHeight / 2

    local o = AnimatedPanel:new(posX, posY, EtherMain.defaultWidth, EtherMain.defaultHeight)
    setmetatable(o, self)
    o.background = true
    o.backgroundColor = {r=0.05, g=0.05, b=0.05, a=1}
    o.borderColor = {r=0, g=0, b=0, a=0}
    o.moveWithMouse = true
    o.savePositionKey = "etherhack_main_window" -- Save window position between sessions
    self.__index = self

    return o
end

Events.OnKeyPressed.Add(EtherMain.OnOpenPanel)