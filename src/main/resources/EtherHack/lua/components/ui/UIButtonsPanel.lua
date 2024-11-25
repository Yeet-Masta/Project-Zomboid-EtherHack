require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
UIButtonsPanel = ISPanel:derive("UIButtonsPanel"); -- Наследование от ISPanel

--*********************************************************
--* Prerender
--*********************************************************
function UIButtonsPanel:prerender()
    ISPanel.prerender(self);
    
    for id = 1, #self.buttons do
        local button = self.buttons[id]
        if (button.id == self.currentTabID) then
            local buttonHighlightPosY = (id - 1) * 50;
            self:drawRect(0, buttonHighlightPosY, 5, 50, 1.0, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b);
            button:setTextureRGBA(EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b, 1.0)
        else
            button:setTextureRGBA(1.0, 1.0, 1.0, 1.0)
        end
    end
end


--*********************************************************
--* Открытие вкладки по ID
--*********************************************************
function UIButtonsPanel:openPanel(id)
    if #self.buttons <= 0 then return end

    local panelById = self.buttons[id or 1].panelTag;
    self.currentTabID = id;

    if self.currentPanel ~= nil then
        self.currentPanel:setVisible(false);
        EtherMain.instance:removeChild(self.currentPanel)
    end

    local panel = panelById:new(self.width, 0, self.parent.width - self.width, self.parent.height);
    panel:initialise();
    panel:instantiate();
    panel:setVisible(true);
    self.parent:addChild(panel); 
    
    self.currentPanel = panel;
        
    EtherMain.currentTabID = id;
end

--*********************************************************
--* Обработка нажатий кнопки
--*********************************************************
function UIButtonsPanel:onButtonClick(button)
    self:openPanel(button.id);
end


--*********************************************************
--* Добавление кнопки на панель
--*********************************************************
function UIButtonsPanel:addButton(iconPath, panelTag)
    local amountButtons = #self.buttons;
    local id = #self.buttons + 1;
    local posY = 10 + amountButtons * 50;

    local button = ISButton:new(10, posY, self.buttonSize.width, self.buttonSize.width, "", self, self.onButtonClick);
    button.anchorRight = true;
    button.anchorLeft = false;
    button:initialise();
    button.borderColor.a = 0.0;
    button.backgroundColor.a = 0;
    button.backgroundColorMouseOver.a = 0;
    button.id = id;
    button.panelTag = panelTag;
    button:setImage(getExtraTexture(iconPath));
    self:addChild(button);
    button:setVisible(true);

    table.insert( self.buttons, button );
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function UIButtonsPanel:new(posX, posY, width, height, parent, accentColor)
    local menuTableData = {};

    menuTableData = ISPanel:new(posX, posY, width, height);
    setmetatable(menuTableData, self);
    menuTableData.background = true;
	menuTableData.backgroundColor = {r=0.1, g=0.1, b=0.1, a=1.0};
	menuTableData.borderColor = {r=0, g=0, b=0, a=0};
    menuTableData.moveWithMouse = true;
    self.__index = self;

    self.parent = parent;

    self.buttons = {};
    self.buttonSize = {width = 32, height = 32};

    self.currentTabID = 1;

    self.currentPanel = nil;

    return menuTableData;
end