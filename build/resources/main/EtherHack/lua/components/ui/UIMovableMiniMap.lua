require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
UIMovableMiniMap = ISPanel:derive("UIMovableMiniMap"); -- Наследование от ISPanel
UIMovableMiniMap.instance = nil;

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function UIMovableMiniMap:createChildren()
    ISPanel.createChildren(self);

    self.map = UIMap:new(10, 30, self.width - 20, self.height - 40)
    self.map:initialise()
    self.map:instantiate()
    self.map:initDataAndStyle()
    self.map.mapAPI:resetView()
    self.map:restoreSettings()
    self.map.centerByPlayer = true
    self:addChild(self.map)

    self.closeButton = ISButton:new(3, 0, 20, 20, "", self, function(self, button) self:close() end);
	self.closeButton:initialise();
	self.closeButton.borderColor.a = 0.0;
	self.closeButton.backgroundColor.a = 0;
	self.closeButton.backgroundColorMouseOver.a = 0;
	self.closeButton:setImage(self.closeTexture);
	self:addChild(self.closeButton);

    self.resizeWidgetCorner = ISResizeWidget:new(self.width-10, self.height-10, 10, 10, self);
	self.resizeWidgetCorner:initialise();
	self.resizeWidgetCorner:setVisible(true)
	self:addChild(self.resizeWidgetCorner);

end

--************************************************************************--
--** Prerender карты
--************************************************************************--
function UIMovableMiniMap:prerender()
    ISPanel.prerender(self)

	self:drawRect( 0, 0, self.width, 20, 1.0, 0, 0, 0, 0.5)
	self:drawTextCentre(self.title, self:getWidth() / 2, 1, 1, 1, 1, 1, UIFont.Small);
	
end

--************************************************************************--
--** Render карты
--************************************************************************--
function UIMovableMiniMap:render()
    ISPanel.render(self)
	
    self:drawTexture(self.resizeimage, self.width-10, self.height - 10, 1, 1, 1, 1);
end

--*********************************************************
--* Закрытие миникарты
--*********************************************************
function UIMovableMiniMap:close()
    UIMovableMiniMap.instance:setVisible(false);
    UIMovableMiniMap.instance:removeFromUIManager();
    UIMovableMiniMap.instance = nil;
end

--*********************************************************
--* Логика открытия миникарты
--*********************************************************
function UIMovableMiniMap.openPanel()
    -- Если панель уже существует, закрываем окно
    if UIMovableMiniMap.instance ~= nil then
        UIMovableMiniMap.instance:setVisible(false);
        UIMovableMiniMap.instance:removeFromUIManager();
        UIMovableMiniMap.instance = nil;
        return
    end

    -- Создаем новую панель
    UIMovableMiniMap.instance = UIMovableMiniMap:new();
    UIMovableMiniMap.instance:initialise();
    UIMovableMiniMap.instance:instantiate();
    UIMovableMiniMap.instance:addToUIManager();
    UIMovableMiniMap.instance:setVisible(true);
    UIMovableMiniMap.instance:setAlwaysOnTop(false);
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function UIMovableMiniMap:new()
    local menuTableData = {};

    local width = 256;
    local height = 256;

    local positionX = getCore():getScreenWidth() - width - 15;
    local positionY = getCore():getScreenHeight() - height - 15;

    menuTableData = ISPanel:new(positionX, positionY, width, height);
    setmetatable(menuTableData, self);
	menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    menuTableData.title = getTranslate("UI_Map_MiniMapTitle");
    menuTableData.moveWithMouse = true;
    menuTableData.localPlayer = getPlayer();
    menuTableData.closeTexture = getTexture("media/ui/Dialog_Titlebar_CloseIcon.png");
	menuTableData.resizeimage = getTexture("media/ui/Panel_StatusBar_Resize.png");
    self.__index = self;

    return menuTableData;
end