require "ISUI/ISPanel"
require "ISUI/ISUIElement"

AnimatedPanel = ISPanel:derive("AnimatedPanel")

-- Animation properties
AnimatedPanel.STATE_HIDDEN = 0
AnimatedPanel.STATE_SHOWING = 1
AnimatedPanel.STATE_SHOWN = 2
AnimatedPanel.STATE_HIDING = 3

-- Animation configuration
AnimatedPanel.ANIMATION_DURATION = 200 -- ms
AnimatedPanel.EASING_FUNCTION = "easeOutQuad" -- can be: linear, easeInQuad, easeOutQuad, easeInOutQuad

function AnimatedPanel:new(x, y, width, height)
    local o = ISPanel:new(x, y, width, height)
    setmetatable(o, self)
    self.__index = self

    o.animState = self.STATE_HIDDEN
    o.animProgress = 0
    o.lastFrameTime = 0
    o.dragable = true
    o.startAlpha = 0
    o.targetAlpha = 1
    o.savePositionKey = nil -- For saving position between game sessions
    
    -- Save initial position
    o.originalX = x
    o.originalY = y
    
    -- Restore saved position if exists
    if o.savePositionKey then
        local savedX = getGameTime():getModData()[o.savePositionKey .. "_x"]
        local savedY = getGameTime():getModData()[o.savePositionKey .. "_y"]
        if savedX and savedY then
            o.originalX = savedX
            o.originalY = savedY
            o:setX(savedX)
            o:setY(savedY)
        end
    end

    return o
end

-- Animation easing functions
function AnimatedPanel:ease(t, b, c, d)
    t = t / d
    
    if self.EASING_FUNCTION == "linear" then
        return c * t + b
    elseif self.EASING_FUNCTION == "easeInQuad" then
        return c * t * t + b
    elseif self.EASING_FUNCTION == "easeOutQuad" then
        return -c * t * (t - 2) + b
    elseif self.EASING_FUNCTION == "easeInOutQuad" then
        t = t * 2
        if t < 1 then
            return c / 2 * t * t + b
        else
            t = t - 1
            return -c / 2 * (t * (t - 2) - 1) + b
        end
    end
end

function AnimatedPanel:showPanel()
    if self.animState == self.STATE_HIDDEN then
        self.animState = self.STATE_SHOWING
        self.animProgress = 0
        self.lastFrameTime = getTimeInMillis()
        self:setVisible(true)
        self:setAlpha(0)
    end
end

function AnimatedPanel:hidePanel()
    if self.animState == self.STATE_SHOWN then
        self.animState = self.STATE_HIDING
        self.animProgress = 0
        self.lastFrameTime = getTimeInMillis()
    end
end

function AnimatedPanel:prerender()
    -- Handle animations
    if self.animState == self.STATE_SHOWING or self.animState == self.STATE_HIDING then
        local currentTime = getTimeInMillis()
        local deltaTime = currentTime - self.lastFrameTime
        self.lastFrameTime = currentTime
        
        self.animProgress = self.animProgress + deltaTime
        
        if self.animProgress >= self.ANIMATION_DURATION then
            if self.animState == self.STATE_SHOWING then
                self.animState = self.STATE_SHOWN
                self:setAlpha(self.targetAlpha)
            else
                self.animState = self.STATE_HIDDEN
                self:setVisible(false)
            end
        else
            local progress = self:ease(self.animProgress, 0, 1, self.ANIMATION_DURATION)
            if self.animState == self.STATE_SHOWING then
                self:setAlpha(progress * self.targetAlpha)
            else
                self:setAlpha((1 - progress) * self.targetAlpha)
            end
        end
    end
    
    ISPanel.prerender(self)
end

-- Handle dragging
function AnimatedPanel:onMouseDown(x, y)
    if self.dragable then
        self.dragging = true
        self.dragX = x
        self.dragY = y
    end
end

function AnimatedPanel:onMouseUp(x, y)
    self.dragging = false
    -- Save position if needed
    if self.savePositionKey then
        getGameTime():getModData()[self.savePositionKey .. "_x"] = self:getX()
        getGameTime():getModData()[self.savePositionKey .. "_y"] = self:getY()
    end
end

function AnimatedPanel:onMouseMove(dx, dy)
    if self.dragging then
        self:setX(self:getX() + dx)
        self:setY(self:getY() + dy)
        return true
    end
    return false
end

function AnimatedPanel:onMouseMoveOutside(dx, dy)
    return self:onMouseMove(dx, dy)
end

-- Reset position
function AnimatedPanel:resetPosition()
    self:setX(self.originalX)
    self:setY(self.originalY)
    if self.savePositionKey then
        getGameTime():getModData()[self.savePositionKey .. "_x"] = self.originalX
        getGameTime():getModData()[self.savePositionKey .. "_y"] = self.originalY
    end
end

return AnimatedPanel