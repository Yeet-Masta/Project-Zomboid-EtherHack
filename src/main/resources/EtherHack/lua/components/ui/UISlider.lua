require "ISUI/ISPanel"

UISlider = ISPanel:derive("UISlider");

--*********************************************************
--* Ограничение значений параметра
--*********************************************************
local function valueClamp(value, min, max)
    if (value < min) then
        return min;
    end
    if (value > max) then
        return max;
    end
    return value
end

--*********************************************************
--* Нажите клавиши мыши
--*********************************************************
function UISlider:onMouseDown(x, y)
    if not self.isEnable and x >= 0 and y >= 0 and x <= self.width and y <= self.height then
		return;
	end

    self.isDragging = true;

    self.currentValue = valueClamp(math.ceil((x / self.width) * (self.maxValue - self.minValue) + self.minValue), self.minValue, self.maxValue);

    if self.onChangeMethod ~= nil then
        self.onChangeMethod(self.currentValue);
    end
end

--*********************************************************
--* Движение мыши
--*********************************************************
function UISlider:onMouseMove(x, y)
    if not self.isDragging then return; end

    local absoluteX = self:getMouseX();
    self.currentValue = valueClamp(math.floor((absoluteX / self.width) * (self.maxValue - self.minValue) + self.minValue), self.minValue, self.maxValue);

    if self.onChangeMethod ~= nil then
        self.onChangeMethod(self.currentValue);
    end
end

--*********************************************************
--* Движение мыши вне слайдера
--*********************************************************
function UISlider:onMouseMoveOutside(x, y)
    self:onMouseMove(x, y);
end

--*********************************************************
--* Поднятие клавиши мыши вне слайдера
--*********************************************************
function UISlider:onMouseUpOutside(x, y)
    self.isDragging = false;
end

--*********************************************************
--* Поднятие клавиши мыши
--*********************************************************
function UISlider:onMouseUp(x, y)
    self.isDragging = false;
end

--*********************************************************
--* Отрисовка слайдера
--*********************************************************
function UISlider:render()
    ISPanel.render(self);

    -- Обновление позиции ползунка на слайдере
    local thumbPosX = (self.currentValue - self.minValue) / (self.maxValue - self.minValue) * self.width;

    if not self.isEnable then
        self.sliderThumbColor = {r = 0.3, g = 0.3, b = 0.3, a = 1.0};
    end

    self:drawRect(0, self.sliderBarByThumbOffset / 2, self.sliderBarSize.width, self.sliderBarSize.height, self.sliderBarColor.a, self.sliderBarColor.r, self.sliderBarColor.g, self.sliderBarColor.b);
    self:drawRect(thumbPosX, 0, self.sliderThumbSize.width, self.sliderThumbSize.height, self.sliderThumbColor.a, self.sliderThumbColor.r, self.sliderThumbColor.g, self.sliderThumbColor.b);
    
    
    self:drawTextRight(tostring(self.minValue), - 5, self.sliderThumbSize.height / 2 - 7, 1.0, 1.0, 1.0, 0.3, UIFont.Small);
    self:drawText(tostring(self.maxValue),self.sliderBarSize.width + 5, self.sliderThumbSize.height / 2 - 7, 1.0, 1.0, 1.0, 0.3, UIFont.Small);
	
    self:drawTextCentre(tostring(self.currentValue), thumbPosX + 3, self.sliderThumbSize.height + 5, self.sliderThumbColor.r, self.sliderThumbColor.g, self.sliderThumbColor.b, self.sliderThumbColor.a, UIFont.Small);
end


--*********************************************************
--* Создание нового экземпляра слайдера
--*********************************************************
function UISlider:new (x, y, width, height, value, minValue, maxValue, onChangeMethod)
    local uiTableData = ISPanel:new(x, y, width, height);
    setmetatable(uiTableData, self)
    self.__index = self
    uiTableData.x = x;
    uiTableData.y = y;
    uiTableData.isEnable = true;
    uiTableData.background = false;
    uiTableData.sliderBarByThumbOffset = 6;
    uiTableData.sliderBarColor = {r=1, g=1, b=1, a=0.1};
    uiTableData.sliderThumbColor = EtherMain.accentColor;
    uiTableData.sliderBarSize = {width = width, height = height - uiTableData.sliderBarByThumbOffset};
    uiTableData.sliderThumbSize = {width = 5, height = height};
    uiTableData.width = width;
    uiTableData.height = height;
    uiTableData.anchorLeft = true;
    uiTableData.anchorRight = false;
    uiTableData.anchorTop = true;
    uiTableData.anchorBottom = false;
    
    uiTableData.minValue = minValue;
    uiTableData.maxValue = maxValue;
    uiTableData.currentValue = valueClamp(value, minValue, maxValue);
    uiTableData.onChangeMethod = onChangeMethod;
    return uiTableData
end

