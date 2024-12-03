-- NotificationSystem handles creating and displaying popup notifications
NotificationSystem = {}
NotificationSystem.notifications = {}
NotificationSystem.maxNotifications = 5
NotificationSystem.NOTIFICATION_LIFETIME = 3000 -- 3 seconds

-- Constants for notification types
NotificationSystem.TYPE_INFO = "info"
NotificationSystem.TYPE_SUCCESS = "success"
NotificationSystem.TYPE_WARNING = "warning"
NotificationSystem.TYPE_ERROR = "error"

-- Colors for different notification types
NotificationSystem.COLORS = {
    info = {r=0.2, g=0.6, b=1.0},
    success = {r=0.2, g=0.8, b=0.2},
    warning = {r=1.0, g=0.6, b=0.2},
    error = {r=1.0, g=0.2, b=0.2}
}

-- Sound effects for different notification types
NotificationSystem.SOUNDS = {
    info = "UISelect",
    success = "UISelectPositive",
    warning = "UISelectNegative",
    error = "UISelectBad"
}

-- Notification panel class
local NotificationPanel = ISPanel:derive("NotificationPanel")

function NotificationPanel:new(text, notificationType, x, y)
    local width = math.max(300, getTextManager():MeasureStringX(UIFont.Medium, text) + 40)
    local height = 50
    local o = ISPanel:new(x, y, width, height)
    setmetatable(o, self)
    self.__index = self

    o.text = text
    o.type = notificationType
    o.typeColor = NotificationSystem.COLORS[notificationType]
    o.backgroundColor = {r=0, g=0, b=0, a=0.8}
    o.borderColor = o.typeColor
    o.lifetime = NotificationSystem.NOTIFICATION_LIFETIME
    o.alpha = 0
    o.targetY = y
    o.moving = false
    o.startTime = getTimestampMs()

    return o
end

function NotificationPanel:initialise()
    ISPanel.initialise(self)
    -- Play sound based on notification type
    -- getSoundManager():playUISound(NotificationSystem.SOUNDS[self.type])
end

function NotificationPanel:prerender()
    -- Fade in/out and movement animations
    if self.lifetime > 500 then
        -- Fade in
        self.alpha = math.min(1, self.alpha + 0.1)
    else
        -- Fade out
        self.alpha = math.max(0, self.alpha - 0.1)
    end

    -- Smooth movement to target position
    if self.moving then
        local currentY = self:getY()
        local diff = self.targetY - currentY
        if math.abs(diff) > 0.5 then
            self:setY(currentY + (diff * 0.2))
        else
            self:setY(self.targetY)
            self.moving = false
        end
    end

    -- Draw background
    self:drawRect(0, 0, self.width, self.height, self.alpha * 0.8,
        self.backgroundColor.r,
        self.backgroundColor.g,
        self.backgroundColor.b)

    -- Draw colored border based on type
    self:drawRectBorder(0, 0, self.width, self.height, self.alpha,
        self.borderColor.r,
        self.borderColor.g,
        self.borderColor.b)

    -- Draw type indicator bar
    self:drawRect(0, 0, 4, self.height, self.alpha,
        self.typeColor.r,
        self.typeColor.g,
        self.typeColor.b)
end

function NotificationPanel:render()
    -- Draw text
    local textX = 20
    local textY = (self.height - getTextManager():getFontHeight(UIFont.Medium)) / 2
    self:drawText(self.text, textX, textY, 1, 1, 1, self.alpha, UIFont.Medium)

    -- Update lifetime
    self.lifetime = math.max(0, NotificationSystem.NOTIFICATION_LIFETIME - (getTimestampMs() - self.startTime))

    -- Remove when lifetime expires
    if self.lifetime <= 0 and self.alpha <= 0 then
        self:removeFromUIManager()
        NotificationSystem.removeNotification(self)
    end
end

-- Main NotificationSystem functions
function NotificationSystem.show(text, notificationType)
    -- Remove oldest notification if at max capacity
    if #NotificationSystem.notifications >= NotificationSystem.maxNotifications then
        local oldest = NotificationSystem.notifications[1]
        oldest:removeFromUIManager()
        table.remove(NotificationSystem.notifications, 1)
    end

    -- Calculate position for new notification
    local x = getCore():getScreenWidth() - 320
    local y = 20 + (#NotificationSystem.notifications * 60)

    -- Create and show notification
    local notification = NotificationPanel:new(text, notificationType or NotificationSystem.TYPE_INFO, x, y)
    notification:initialise()
    notification:addToUIManager()

    -- Add to active notifications
    table.insert(NotificationSystem.notifications, notification)

    return notification
end

function NotificationSystem.removeNotification(notification)
    for i, n in ipairs(NotificationSystem.notifications) do
        if n == notification then
            table.remove(NotificationSystem.notifications, i)
            -- Update positions of remaining notifications
            NotificationSystem.updatePositions()
            break
        end
    end
end

function NotificationSystem.updatePositions()
    for i, notification in ipairs(NotificationSystem.notifications) do
        notification.targetY = 20 + ((i-1) * 60)
        notification.moving = true
    end
end

-- Convenience methods for different notification types
function NotificationSystem.info(text)
    return NotificationSystem.show(text, NotificationSystem.TYPE_INFO)
end

function NotificationSystem.success(text)
    return NotificationSystem.show(text, NotificationSystem.TYPE_SUCCESS)
end

function NotificationSystem.warning(text)
    return NotificationSystem.show(text, NotificationSystem.TYPE_WARNING)
end

function NotificationSystem.error(text)
    return NotificationSystem.show(text, NotificationSystem.TYPE_ERROR)
end

return NotificationSystem