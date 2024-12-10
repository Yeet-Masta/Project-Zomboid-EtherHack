require "ISUI/ISPanel"

UIStatsEditor = ISPanel:derive("UIStatsEditor")
UIStatsEditor.instance = nil

function UIStatsEditor:createChildren()
    ISPanel.createChildren(self)

    -- Zombie Kills Input
    self.killsLabel = ISLabel:new(10, 20, 25, "Zombie Kills:", 1, 1, 1, 1, UIFont.Medium, true)
    self:addChild(self.killsLabel)

    self.killsEntry = ISTextEntryBox:new(tostring(getZombieKills()), 120, 20, 100, 25)
    self.killsEntry:initialise()
    self.killsEntry:instantiate()
    self.killsEntry:setOnlyNumbers(true)
    self:addChild(self.killsEntry)

    -- Hours Survived Input  
    self.hoursLabel = ISLabel:new(10, 60, 25, "Hours Survived:", 1, 1, 1, 1, UIFont.Medium, true)
    self:addChild(self.hoursLabel)

    self.hoursEntry = ISTextEntryBox:new(tostring(getHoursAlive()), 120, 60, 100, 25)
    self.hoursEntry:initialise()
    self.hoursEntry:instantiate()
    self.hoursEntry:setOnlyNumbers(true)
    self:addChild(self.hoursEntry)

    -- Save Button
    self.saveButton = ISButton:new(10, 100, 100, 25, "Save Changes", self, UIStatsEditor.onSaveButton)
    self.saveButton:initialise()
    self.saveButton:instantiate()
    self:addChild(self.saveButton)

    -- Close Button
    self.closeButton = ISButton:new(120, 100, 100, 25, "Close", self, UIStatsEditor.onCloseButton)
    self.closeButton:initialise()
    self.closeButton:instantiate()
    self:addChild(self.closeButton)
end

function UIStatsEditor:onSaveButton()
    local kills = tonumber(self.killsEntry:getText())
    local hours = tonumber(self.hoursEntry:getText())

    if kills then
        setZombieKills(kills)
    end

    if hours then
        setHoursAlive(hours)
    end

    -- Refresh any UI that shows these stats
    if ISPlayerStatsUI.instance then
        ISPlayerStatsUI.instance:updateStats()
    end
end

function UIStatsEditor:onCloseButton()
    self:close()
end

function UIStatsEditor:close()
    self:setVisible(false)
    self:removeFromUIManager()
    UIStatsEditor.instance = nil
end

function UIStatsEditor:new()
    local width = 230
    local height = 140
    local x = getCore():getScreenWidth() / 2 - width / 2
    local y = getCore():getScreenHeight() / 2 - height / 2
    local o = ISPanel:new(x, y, width, height)
    setmetatable(o, self)
    self.__index = self
    o.variableColor = {r=0.9, g=0.55, b=0.1, a=1}
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1}
    o.backgroundColor = {r=0, g=0, b=0, a=0.8}
    o.moveWithMouse = true
    return o
end

function UIStatsEditor.OnOpenPanel()
    if UIStatsEditor.instance then
        UIStatsEditor.instance:close()
    end

    UIStatsEditor.instance = UIStatsEditor:new()
    UIStatsEditor.instance:initialise()
    UIStatsEditor.instance:instantiate()
    UIStatsEditor.instance:addToUIManager()
    return UIStatsEditor.instance
end