require "ISUI/ISPanel"

UIButton = ISPanel:derive("UIButton");

--************************************************************************--
--** Обработка поднятия клавиши нажатия
--************************************************************************--
function UIButton:onMouseUp(x, y)
    if not self:getIsVisible() or not self.isEnable and x >= 0 and y >= 0 and x <= self.width and y <= self.height then
		return;
	end

    local process = false;

    if self.onPressed == true then
        process = true;
    end

    self.onPressed = false;
    
	if self.onClickMethod == nil then
        return;
    end

    if process or self.allowMouseUpProcessing then
        getSoundManager():playUISound(self.activateSound)
        self.onClickMethod();
    end
end

--************************************************************************--
--** Обработка выхода мыши за границы кнопки
--************************************************************************--
function UIButton:onMouseUpOutside(x, y)
    self.onPressed = false;
end

--************************************************************************--
--** Обработка нажатия по кнопке
--************************************************************************--
function UIButton:onMouseDown(x, y)
	if not self:getIsVisible() or not self.isEnable and x >= 0 and y >= 0 and x <= self.width and y <= self.height then
		return;
	end

    self.onPressed = true;
end

--************************************************************************--
--** Обработка двойного клика
--************************************************************************--
function UIButton:onMouseDoubleClick(x, y)
	return self:onMouseDown(x, y)
end


--************************************************************************--
--** Отрисовка кнопки
--************************************************************************--
function UIButton:render()
	if self.isEnable then
		if not self.onPressed then
			self:drawRect( 0, 0, self.width, self.height, 1.0, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b)
		else
			self:drawRect( 0, 0, self.width, self.height, 0.8, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b)
		end
		self:drawTextCentre(self.title, self.width / 2, self.height / 2 - 8, 1.0, 1.0, 1.0, 1.0, self.font);
	else
		self:drawRect( 0, 0, self.width, self.height, 1.0, 0.1, 0.1, 0.1)
		self:drawTextCentre(self.title, self.width / 2, self.height / 2 - 8, 1.0, 1.0, 1.0, 0.3, self.font);
	end
end

--************************************************************************--
--** Включение или отключение кнопки
--************************************************************************--
function UIButton:setEnable(isEnable)
	self.isEnable = isEnable;
end

--************************************************************************--
--** Создание новой кнопки
--************************************************************************--
function UIButton:new (x, y, width, height, title, onClickMethod)

	local uiTableData = {}
	uiTableData = ISPanel:new(x, y, width, height);
	setmetatable(uiTableData, self)
    self.__index = self

	if width < (getTextManager():MeasureStringX(UIFont.Small, title) + 20) then
        width = getTextManager():MeasureStringX(UIFont.Small, title) + 20;
    end
	uiTableData.x = x;
	uiTableData.y = y;
	uiTableData.font = UIFont.Small;
	uiTableData.textureWidth = width;
	uiTableData.textureHeight = height;
	uiTableData.borderColor = {r=0, g=0, b=0, a=0};
	uiTableData.backgroundColor = {r=0, g=0, b=0, a=0};
    uiTableData.textColor = {r=1.0, g=1.0, b=1.0, a=1.0};
    uiTableData.width = width;
    uiTableData.height = height;
	uiTableData.anchorLeft = true;
	uiTableData.anchorRight = false;
	uiTableData.anchorTop = true;
	uiTableData.anchorBottom = false;
	uiTableData.mouseOver = false;
	uiTableData.title = title;
	uiTableData.onClickMethod = onClickMethod;
	uiTableData.isEnable = true;
	uiTableData.onPressed = false;
    uiTableData.activateSound = "UIActivateButton"
   return uiTableData
end
