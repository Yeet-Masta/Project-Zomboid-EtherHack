require "ISUI/ISPanel"

UICheckbox = ISPanel:derive("UICheckbox");

--************************************************************************--
--** Инициализация чекбокса
--************************************************************************--
function UICheckbox:initialise()
	ISPanel.initialise(self);
end

--************************************************************************--
--** Установка состояния чекбокса
--************************************************************************--
function UICheckbox:setCheked(isChecked)
	self.isChecked = isChecked;
end

--************************************************************************--
--** Получение состояние чекбокса
--************************************************************************--
function UICheckbox:isChecked()
	return self.isChecked;
end

--************************************************************************--
--** Отрисовка чекбокса
--************************************************************************--
function UICheckbox:render()
	if not self.isChecked then
		self:drawTextureScaled(self.uncheckedTexture, 0, 0, self.textureWidth, self.textureHeight, 1.0, 1.0, 1.0, 1.0);
	else
		self:drawTextureScaled(self.checkedTexture, 0, 0, self.textureWidth, self.textureHeight, 1.0, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b);
	end

	self:drawText(self.title, self.textureWidth + self.marginTexture, self.textureHeight / 2 - 8, 1.0, 1.0, 1.0, 1.0, self.font);
end

--************************************************************************--
--** Включение или отключение чекбокса
--************************************************************************--
function UICheckbox:setEnable(isEnable)
    self.enable = isEnable;
end

--************************************************************************--
--** Обработка клика мыши по чекбоксу
--************************************************************************--
function UICheckbox:onMouseUp(x, y)
    if self.enable and x >= 0 and y >= 0 and x <= self.width and y <= self.height then
        getSoundManager():playUISound("UIToggleTickBox");
        self.isChecked = not self.isChecked;

        if self.onCheckedMethod ~= nil then
            self.onCheckedMethod(self.isChecked);
        end
        return true;
    end

    return false;
end

--************************************************************************--
--** Создание нового чекбокса
--************************************************************************--
function UICheckbox:new (x, y, title, isChecked, onChecked)
	local uiTableData = {}
	setmetatable(uiTableData, self)
	self.__index = self
	uiTableData.x = x;
	uiTableData.y = y;
	uiTableData.checkedTexture = getExtraTexture("EtherHack/media/ui/checkbox_checked.png");
	uiTableData.uncheckedTexture = getExtraTexture("EtherHack/media/ui/checkbox_unchecked.png");
	uiTableData.textureWidth = 32;
	uiTableData.textureHeight = 16
	uiTableData.marginTexture = 10;
	uiTableData.borderColor = {r=0, g=0, b=0, a=0.0};
	uiTableData.backgroundColor = {r=0, g=0, b=0, a=0.0};
	uiTableData.anchorLeft = true;
	uiTableData.anchorRight = false;
	uiTableData.anchorTop = true;
	uiTableData.anchorBottom = false;
	uiTableData.title = title;
	uiTableData.isChecked = isChecked;
	uiTableData.font = UIFont.Small;
    uiTableData.fontHeight = getTextManager():getFontHeight(uiTableData.font);
    uiTableData.textWidth = getTextManager():MeasureStringX(uiTableData.font, uiTableData.title);
	uiTableData.onCheckedMethod = onChecked;
	uiTableData.enable = true;

	uiTableData.width = uiTableData.textureHeight + uiTableData.marginTexture + uiTableData.textWidth + 20;
	uiTableData.height = uiTableData.textureHeight;
	return uiTableData;
end

