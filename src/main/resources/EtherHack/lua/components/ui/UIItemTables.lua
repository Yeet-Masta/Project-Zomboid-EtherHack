require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
UIItemTables = ISPanel:derive("UIItemTables");

local fontHeightSmall = getTextManager():getFontHeight(UIFont.Small)

--*********************************************************
--* Обработка render
--*********************************************************
function UIItemTables:render()
    ISPanel.render(self);
    
    local y = self.datas.y + self.datas.height + 5
    self:drawText(getText("IGUI_DbViewer_TotalResult") .. self.totalResult, 0, y, 1,1,1,1,UIFont.Small)
end

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function UIItemTables:createChildren()
    ISPanel.createChildren(self);

    self.datas = ISScrollingListBox:new(0, 25, self.width, self.height - 150);
    self.datas:initialise();
    self.datas:instantiate();
    self.datas.itemheight = fontHeightSmall + 4 * 2
    self.datas.selected = 0;
    self.datas.joypadParent = self;
    self.datas.font = UIFont.NewSmall;
    self.datas.doDrawItem = self.drawDatas;
    self.datas.drawBorder = true;
    self.datas:addColumn(getTranslate("UI_ItemCreator_Title_ItemName"), 0);
    self.datas:addColumn(getTranslate("UI_ItemCreator_Title_ItemCategory"), 250)
    self:addChild(self.datas);

    self.filterByNameTitle = ISLabel:new(0, self.height - 40, 20, getTranslate("UI_ItemCreator_Title_FilterByName"), 1, 1, 1, 1, UIFont.Medium, true)
    self.filterByNameTitle:initialise()
    self.filterByNameTitle:instantiate()
    self:addChild(self.filterByNameTitle)

    self.filterByName = ISTextEntryBox:new("", 0, self.height - 20, self.width / 2 - 10, 20);
    self.filterByName.font = UIFont.Small;
    self.filterByName:initialise();
    self.filterByName:instantiate();
    self.filterByName.target = self;
    self.filterByName.itemsListFilter = self.filterName;
    self.filterByName.onTextChange = UIItemTables.onFilterChange;
    self.filterByName:setClearButton(true)
    self:addChild(self.filterByName);
    table.insert(self.filterWidgets, self.filterByName);

    self.filterByIdTitle = ISLabel:new(self.width / 2, self.height - 40, 20, getTranslate("UI_ItemCreator_Title_FilterById"), 1, 1, 1, 1, UIFont.Medium, true)
    self.filterByIdTitle:initialise()
    self.filterByIdTitle:instantiate()
    self:addChild(self.filterByIdTitle)

    self.filterById = ISTextEntryBox:new("", self.width / 2, self.height - 20, self.width / 2, 20);
    self.filterById.font = UIFont.Small;
    self.filterById:initialise();
    self.filterById:instantiate();
    self.filterById:setClearButton(true)
    self.filterById.target = self;
    self.filterById.itemsListFilter = self.filterType;
    self.filterById.onTextChange = UIItemTables.onFilterChange;
    self:addChild(self.filterById);
    table.insert(self.filterWidgets, self.filterById);

    self.addItemX1 = UIButton:new(0, self.height - 80, 100, 24, getTranslate("UI_ItemCreator_Button_AddItemX1"), 
    function() 
        local item = self.datas.items[self.datas.selected].item;
        giveItem(item:getFullName(), 1);
    end)
    self.addItemX1:initialise();
    self.addItemX1:instantiate();
    self.addItemX1:setAnchorLeft(true);
    self.addItemX1:setAnchorRight(false);
    self.addItemX1:setAnchorTop(false);
    self.addItemX1:setAnchorBottom(true);
    self.addItemX1.isOnlyInGame = true;
    self:addChild(self.addItemX1);
    table.insert(self.buttonList, self.addItemX1);

    self.addItemX2 = UIButton:new(self.addItemX1:getX() + self.addItemX1.width + 10, self.height - 80, 100, 24, getTranslate("UI_ItemCreator_Button_AddItemX2"), 
    function() 
        local item = self.datas.items[self.datas.selected].item;
        giveItem(item:getFullName(), 2);
    end)
    self.addItemX2:initialise();
    self.addItemX2:instantiate();
    self.addItemX2:setAnchorLeft(true);
    self.addItemX2:setAnchorRight(false);
    self.addItemX2:setAnchorTop(false);
    self.addItemX2:setAnchorBottom(true);
    self.addItemX2.isOnlyInGame = true;
    self:addChild(self.addItemX2);
    table.insert(self.buttonList, self.addItemX2);

    self.addItemX5 = UIButton:new(self.addItemX2:getX() + self.addItemX2.width + 10, self.height - 80, 100, 24, getTranslate("UI_ItemCreator_Button_AddItemX5"), 
    function() 
        local item = self.datas.items[self.datas.selected].item;
        giveItem(item:getFullName(), 5);
    end)
    self.addItemX5:initialise();
    self.addItemX5:instantiate();
    self.addItemX5:setAnchorLeft(true);
    self.addItemX5:setAnchorRight(false);
    self.addItemX5:setAnchorTop(false);
    self.addItemX5:setAnchorBottom(true);
    self.addItemX5.isOnlyInGame = true;
    self:addChild(self.addItemX5);
    table.insert(self.buttonList, self.addItemX5);

    self.addItemX10 = UIButton:new(self.addItemX5:getX() + self.addItemX5.width + 10, self.height - 80, 100, 24, getTranslate("UI_ItemCreator_Button_AddItemX10"), 
    function() 
        local item = self.datas.items[self.datas.selected].item;
        giveItem(item:getFullName(), 10);
    end)
    self.addItemX10:initialise();
    self.addItemX10:instantiate();
    self.addItemX10:setAnchorLeft(true);
    self.addItemX10:setAnchorRight(false);
    self.addItemX10:setAnchorTop(false);
    self.addItemX10:setAnchorBottom(true);
    self.addItemX10.isOnlyInGame = true;
    self:addChild(self.addItemX10);
    table.insert(self.buttonList, self.addItemX10);

    self:updatePanel();
end

--*********************************************************
--* Обновление панели
--*********************************************************
function UIItemTables:updatePanel()
    for i=1, #self.buttonList do
        local item = self.buttonList[i];
        if item.isOnlyInGame and getPlayer() == nil or getPlayer():isDead() then
            item:setEnable(false);
        end
    end
end


--*********************************************************
--* Инициализация списков
--*********************************************************
function UIItemTables:initList(module)
    self.totalResult = 0;
    local displayCategoryNames = {}
    local displayCategoryMap = {}
    for _, v in ipairs(module) do
        self.datas:addItem(v:getDisplayName(), v);
        if not displayCategoryMap[v:getDisplayCategory()] then
            displayCategoryMap[v:getDisplayCategory()] = true
            table.insert(displayCategoryNames, v:getDisplayCategory())
        end
        self.totalResult = self.totalResult + 1;
    end
    table.sort(self.datas.items, function(a,b) return not string.sort(a.item:getDisplayName(), b.item:getDisplayName()); end);
end

--*********************************************************
--* Обновление таблицы
--*********************************************************
function UIItemTables:update()
    self.datas.doDrawItem = self.drawDatas;
end

--*********************************************************
--* Фильтр по названию
--*********************************************************
function UIItemTables:filterName(widget, scriptItem)
    local txtToCheck = string.lower(scriptItem:getDisplayName())
    local filterTxt = string.lower(widget:getInternalText())
    return checkStringPattern(filterTxt) and string.match(txtToCheck, filterTxt)
end

--*********************************************************
--* Фильтр по ID
--*********************************************************
function UIItemTables:filterType(widget, scriptItem)
    local txtToCheck = string.lower(scriptItem:getName())
    local filterTxt = string.lower(widget:getInternalText())
    return checkStringPattern(filterTxt) and string.match(txtToCheck, filterTxt)
end

--*********************************************************
--* Применение фильтра при написании текста
--*********************************************************
function UIItemTables.onFilterChange(widget)
    local datas = widget.parent.datas;
    if not datas.fullList then datas.fullList = datas.items; end
    widget.parent.totalResult = 0;
    datas:clear();
    for i,v in ipairs(datas.fullList) do -- check every items
        local add = true;
        for j,widget in ipairs(widget.parent.filterWidgets) do -- check every filters
            if not widget.itemsListFilter(self, widget, v.item) then
                add = false
                break
            end
        end
        if add then
            datas:addItem(i, v.item);
            widget.parent.totalResult = widget.parent.totalResult + 1;
        end
    end
end

--*********************************************************
--* Отрисовка данных
--*********************************************************
function UIItemTables:drawDatas(y, item, alt)
    if y + self:getYScroll() + self.itemheight < 0 or y + self:getYScroll() >= self.height then
        return y + self.itemheight
    end
    
    local a = 0.9;

    if self.selected == item.index then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b);
    end

    if alt then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, 0.3, 0.3, 0.3);
    end

    self:drawRectBorder(0, y, self:getWidth(), self.itemheight, a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    local iconX = 4
    local iconSize = fontHeightSmall;

    local clipX = self.columns[1].size
    local clipX2 = self.columns[2].size
    local clipY = math.max(0, y + self:getYScroll())
    local clipY2 = math.min(self.height, y + self:getYScroll() + self.itemheight)
    
    self:setStencilRect(clipX, clipY, clipX2 - clipX, clipY2 - clipY)
    self:drawText(item.item:getDisplayName(), 25, y + 4, 1, 1, 1, a, self.font);
    self:clearStencilRect()

    if item.item:getDisplayCategory() ~= nil then
        self:drawText(getText("IGUI_ItemCat_" .. item.item:getDisplayCategory()), self.columns[2].size + 10, y + 4, 1, 1, 1, a, self.font);
    else
        self:drawText("<NONE>", self.columns[2].size + 10, y + 4, 1, 1, 1, a, self.font);
    end
    
    self:repaintStencilRect(0, clipY, self.width - 20, clipY2 - clipY)

    local icon = item.item:getIcon()
    if item.item:getIconsForTexture() and not item.item:getIconsForTexture():isEmpty() then
        icon = item.item:getIconsForTexture():get(0)
    end
    if icon then
        local texture = getTexture("Item_" .. icon)
        if texture then
            self:drawTextureScaledAspect2(texture, self.columns[1].size + iconX, y + (self.itemheight - iconSize) / 2, iconSize, iconSize,  1, 1, 1, 1);
        end
    end
    
    return y + self.itemheight;
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function UIItemTables:new (x, y, width, height)
    local menuTableData = ISPanel:new(x, y, width, height);
    setmetatable(menuTableData, self);
    menuTableData.listHeaderColor = {r=0.4, g=0.4, b=0.4, a=0.0};
    menuTableData.borderColor = {r=0.4, g=0.4, b=0.4, a=0};
    menuTableData.backgroundColor = {r=0, g=0, b=0, a=0};
    menuTableData.buttonBorderColor = {r=0.7, g=0.7, b=0.7, a=0.0};
    menuTableData.totalResult = 0;
    menuTableData.filterWidgets = {};
    menuTableData.buttonList = {};
    UIItemTables.instance = menuTableData;
    return menuTableData;
end