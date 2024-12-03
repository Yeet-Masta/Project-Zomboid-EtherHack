-- TooltipSystem handles creating and displaying enhanced tooltips
TooltipSystem = {}
TooltipSystem.tooltips = {}

-- Tooltip panel class
local TooltipPanel = ISPanel:derive("TooltipPanel")

function TooltipPanel:new(text, x, y)
    local width = getTextManager():MeasureStringX(UIFont.Small, text) + 20
    local height = getTextManager():getFontHeight(UIFont.Small) + 10
    local o = ISPanel:new(x, y, width, height)
    setmetatable(o, self)
    self.__index = self

    o.backgroundColor = {r=0, g=0, b=0, a=0.8}
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1}
    o.text = text
    o.fadeTime = 0
    o.maxAlpha = 0.9
    o.currentAlpha = 0

    return o
end

function TooltipPanel:prerender()
    -- Fade in effect
    if self.fadeTime < 10 then
        self.fadeTime = self.fadeTime + 1
        self.currentAlpha = (self.fadeTime / 10) * self.maxAlpha
    end

    self:drawRect(0, 0, self.width, self.height, self.currentAlpha,
        self.backgroundColor.r,
        self.backgroundColor.g,
        self.backgroundColor.b)

    self:drawRectBorder(0, 0, self.width, self.height, self.currentAlpha,
        self.borderColor.r,
        self.borderColor.g,
        self.borderColor.b)
end

function TooltipPanel:render()
    local textX = 10
    local textY = (self.height - getTextManager():getFontHeight(UIFont.Small)) / 2
    self:drawText(self.text, textX, textY, 1, 1, 1, self.currentAlpha, UIFont.Small)
end

-- Main TooltipSystem functions
function TooltipSystem.addTooltip(element, text)
    if not element or not text then return end

    -- Store tooltip data
    TooltipSystem.tooltips[element] = text

    -- Add mouse handlers if they don't exist
    if not element.onMouseMoveOriginal then
        element.onMouseMoveOriginal = element.onMouseMove
        element.onMouseMove = function(self, dx, dy)
            if self.onMouseMoveOriginal then
                self:onMouseMoveOriginal(dx, dy)
            end
            TooltipSystem.showTooltip(self)
        end
    end

    if not element.onMouseMoveOutsideOriginal then
        element.onMouseMoveOutsideOriginal = element.onMouseMoveOutside
        element.onMouseMoveOutside = function(self, dx, dy)
            if self.onMouseMoveOutsideOriginal then
                self:onMouseMoveOutsideOriginal(dx, dy)
            end
            TooltipSystem.hideTooltip(self)
        end
    end
end

function TooltipSystem.showTooltip(element)
    -- Remove existing tooltip if any
    if element.activeTooltip then
        element.activeTooltip:removeFromUIManager()
        element.activeTooltip = nil
    end

    local text = TooltipSystem.tooltips[element]
    if not text then return end

    -- Calculate position (below element)
    local x = element:getAbsoluteX()
    local y = element:getAbsoluteY() + element:getHeight() + 5

    -- Create and show tooltip
    local tooltip = TooltipPanel:new(text, x, y)
    tooltip:initialise()
    tooltip:addToUIManager()
    tooltip:setAlwaysOnTop(true)

    -- Store reference to active tooltip
    element.activeTooltip = tooltip
end

function TooltipSystem.hideTooltip(element)
    if element.activeTooltip then
        element.activeTooltip:removeFromUIManager()
        element.activeTooltip = nil
    end
end

function TooltipSystem.removeTooltip(element)
    TooltipSystem.tooltips[element] = nil
    TooltipSystem.hideTooltip(element)
end

return TooltipSystem