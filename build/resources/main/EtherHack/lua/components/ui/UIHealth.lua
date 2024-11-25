require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
UIHealth = ISPanel:derive("UIHealth"); -- Наследование от ISPanel
UIHealth.instance = nil;

local fontHeightSmall = getTextManager():getFontHeight(UIFont.Small)


--*********************************************************
--* Создание метки
--*********************************************************
function UIHealth:addLabel(posX, posY, title)
    local label = ISLabel:new(posX, posY + 3, getTextManager():getFontHeight(UIFont.Small), title, 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
    return label
end

--*********************************************************
--* Создание кнопки
--*********************************************************
function UIHealth:addButton(posX, posY, width, height, buttonTitle, onClick, isRequireBodyPart)
    local button = UIButton:new(posX, posY, width, height, buttonTitle, onClick)
    button:initialise();
    button:instantiate();
    button:setAnchorLeft(true);
    button:setAnchorRight(false);
    button:setAnchorTop(false);
    button:setAnchorBottom(true);
    button.isRequireBodyPart = isRequireBodyPart;
    self:addChild(button);
    table.insert(self.buttonList, button);
    return button
end

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function UIHealth:createChildren()
    ISPanel.createChildren(self);

    self.closeButton = ISButton:new(3, 0, 20, 20, "", self, function(self, button) self:close() end);
	self.closeButton:initialise();
	self.closeButton.borderColor.a = 0.0;
	self.closeButton.backgroundColor.a = 0;
	self.closeButton.backgroundColorMouseOver.a = 0;
	self.closeButton:setImage(self.closeTexture);
	self:addChild(self.closeButton);

    self.datas = ISScrollingListBox:new(10, 80, self.width - 20, self.height - 210);
    self.datas:initialise();
    self.datas:instantiate();
    self.datas.itemheight = fontHeightSmall + 4 * 2
    self.datas.selected = 0;
    self.datas.joypadParent = self;
    self.datas.font = UIFont.NewSmall;
    self.datas.doDrawItem = self.drawDatas;
    self.datas.drawBorder = true;
    self.datas.backgroundColor = {r=0, g=0, b=0, a=1.0};
    self.datas:addColumn(getTranslate("UI_Medic_PartsTableName"), 0);
    self.datas:addColumn(getTranslate("UI_Medic_PartsTableHealth"), 150)
    self.datas:addColumn(getTranslate("UI_Medic_PartsTableBandaged"), 250)
    self.datas:addColumn(getTranslate("UI_Medic_PartsTableDamaged"), 360)
    self:addChild(self.datas);

    local healBody = self:addButton(10, self.datas.y + self.datas.height + 10, 100, 20, getTranslate("UI_Medic_ButtonHealBody"), function ()
        local player = self.localPlayer;
        local bodyParts = player:getBodyDamage():getBodyParts()
        for i=1, bodyParts:size() do
            local bodyPart = bodyParts:get(i-1)
            bodyPart:RestoreToFullHealth();

            if bodyPart:getStiffness() > 0 then
                bodyPart:setStiffness(0)
                player:getFitness():removeStiffnessValue(BodyPartType.ToString(bodyPart:getType()))
            end
        end
    end, false)

    local healPart = self:addButton(healBody.x + healBody.width + 10, self.datas.y + self.datas.height + 10, 100, 20, getTranslate("UI_Medic_ButtonHeal"), function ()
        local player = self.localPlayer;
        local bodyPart = self.datas.items[self.datas.selected].item
        bodyPart:RestoreToFullHealth();

        if bodyPart:getStiffness() > 0 then
            bodyPart:setStiffness(0)
            player:getFitness():removeStiffnessValue(BodyPartType.ToString(bodyPart:getType()))
        end
    end, true)

    local bleeding = self:addButton(healPart.x + healPart.width + 10, self.datas.y + self.datas.height + 10, 100, 20, getTranslate("UI_Medic_ButtonBleeding"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        bodyPart:setBleedingTime((bodyPart:getBleedingTime() > 0) and 0 or 10)
    end, true)

    local bullet = self:addButton(bleeding.x + bleeding.width + 10, self.datas.y + self.datas.height + 10, 100, 20, getTranslate("UI_Medic_ButtonBullet"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:haveBullet() then
            local deepWound = bodyPart:isDeepWounded()
            local deepWoundTime = bodyPart:getDeepWoundTime()
            local bleedTime = bodyPart:getBleedingTime()
            bodyPart:setHaveBullet(false, 0)
            bodyPart:setDeepWoundTime(deepWoundTime)
            bodyPart:setDeepWounded(deepWound)
            bodyPart:setBleedingTime(bleedTime)
        else
            bodyPart:setHaveBullet(true, 0)
        end
    end, true)

    local burnNeedsWash = self:addButton(10, bleeding.y + bleeding.height + 10, 100, 20, getTranslate("UI_Medic_ButtonBurnWash"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:getBurnTime() > 0 then
            bodyPart:setBurnTime(0)
            bodyPart:setNeedBurnWash(not bodyPart:isNeedBurnWash())
        else
            bodyPart:setBurnTime(50)
        end
    end, true)

    local deepWound = self:addButton(burnNeedsWash.x + burnNeedsWash.width + 10, bleeding.y + bleeding.height + 10, 100, 20, getTranslate("UI_Medic_ButtonDeepWound"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:getDeepWoundTime() > 0 then
            bodyPart:setDeepWoundTime(0)
            bodyPart:setDeepWounded(false)
            bodyPart:setBleedingTime(0)
        else
            bodyPart:generateDeepWound();
        end
    end, true)

    local fracture = self:addButton(deepWound.x + deepWound.width + 10, bleeding.y + bleeding.height + 10, 100, 20, getTranslate("UI_Medic_ButtonFracture"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:getFractureTime() > 0 then
            bodyPart:setFractureTime(0)
        else
            bodyPart:setFractureTime(21)
        end
    end, true)

    local glassShards = self:addButton(fracture.x + fracture.width + 10, bleeding.y + bleeding.height + 10, 100, 20, getTranslate("UI_Medic_ButtonGlass"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        bodyPart:generateDeepShardWound();
    end, true)

    local infected = self:addButton(10, glassShards.y + glassShards.height + 10, 100, 20, getTranslate("UI_Medic_ButtonInfected"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:isInfectedWound() then
            bodyPart:setWoundInfectionLevel(-1)
        else
            bodyPart:setWoundInfectionLevel(10)
        end
    end, true)

    local scratched = self:addButton(infected.x + infected.width + 10, glassShards.y + glassShards.height + 10, 100, 20, getTranslate("UI_Medic_ButtonScratch"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:getScratchTime() > 0 then
            bodyPart:setScratched(false, true)
            bodyPart:setScratchTime(0)
        else
            bodyPart:setScratched(true, false);
        end
    end, true)

    local laceration = self:addButton(scratched.x + scratched.width + 10, glassShards.y + glassShards.height + 10, 100, 20, getTranslate("UI_Medic_ButtonLaceration"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:isCut() then
            bodyPart:setCut(false)
            bodyPart:setCutTime(0)
        else
            bodyPart:setCut(true)
        end
    end, true)

    local bite = self:addButton(laceration.x + laceration.width + 10, glassShards.y + glassShards.height + 10, 100, 20, getTranslate("UI_Medic_ButtonBite"), function ()
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:bitten() then
            bodyPart:SetBitten(false);
            bodyPart:SetInfected(false);
            bodyPart:SetFakeInfected(false);
        else
            bodyPart:SetBitten(true);
        end
    end, true)

    local exerciseFatigue = self:addButton(10, bite.y + bite.height + 10, 100, 20, getTranslate("UI_Medic_ButtonFatigue"), function ()
        local player = self.localPlayer;
        local bodyPart = self.datas.items[self.datas.selected].item
        if bodyPart:getStiffness() > 0 then
            bodyPart:setStiffness(0)
            player:getFitness():removeStiffnessValue(BodyPartType.ToString(bodyPart:getType()))
        else
            bodyPart:setStiffness(100)
        end
    end, true)

    self:loadBodyParts();
end

--*********************************************************
--* Отрисовка данных
--*********************************************************
function UIHealth:drawDatas(y, item, alt)
    if y + self:getYScroll() + self.itemheight < 0 or y + self:getYScroll() >= self.height then
        return y + self.itemheight
    end

    if self.selected == item.index then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b);
    end

    if alt then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, 0.3, 0.3, 0.3);
    end
    self:drawRectBorder(0, y, self:getWidth(), self.itemheight, 0.5, 1, 1, 1);
    self:drawRectBorder(self.columns[1].size, y, self.columns[2].size, self.itemheight, 0.5, 1, 1, 1);

    local clipX = self.columns[1].size
    local clipX2 = self.columns[2].size
    local clipY = math.max(0, y + self:getYScroll())
    local clipY2 = math.min(self.height, y + self:getYScroll() + self.itemheight)

    local textPartColor = {r = 1, g = 1, b = 1, a = 1}
    local partHealth = round(item.item:getHealth())

    textPartColor = {r = 1 - partHealth / 100, g = partHealth / 100, b = 0, a = 1}

    local isBandaged = item.item:bandaged()
    local isDamaged= item.item:haveBullet() or item.item:isInfectedWound() or item.item:stitched() or item.item:haveGlass()
    or item.item:getBurnTime() > 0 or item.item:getSplintFactor() > 0 or (item.item:getFractureTime() > 0 and item.item:getSplintFactor() == 0)
    or item.item:bleeding() or item.item:getStiffness() > 5 or item.item:getAdditionalPain() > 10 or item.item:bitten() or item.item:deepWounded()
    or item.item:isCut() or item.item:scratched() or item.item:getGarlicFactor() > 0 or item.item:getComfreyFactor() > 0 or item.item:getComfreyFactor() > 0;

    -- Устанавливаем маску для первого столбца
    self:suspendStencil()
    self:setStencilRect(clipX, clipY, clipX2 - clipX, clipY2 - clipY)
    local name = BodyPartType.getDisplayName(item.item:getType())
    self:drawText(name, 5, y + 4, textPartColor.r, textPartColor.g, textPartColor.b, textPartColor.a, UIFont.Small);
    -- Удаляем маску
    self:clearStencilRect()
    self:resumeStencil()

    if isBandaged then
        isBandaged = getTranslate("UI_Medic_YesTitle")
        self:drawRect(self.columns[3].size, y, self.columns[4].size - self.columns[3].size, self.itemheight, 0.3, 0.5, 1.0, 0.5);
    else
        isBandaged = "-"
    end

    if isDamaged then
        isDamaged = getTranslate("UI_Medic_YesTitle")
        self:drawRect(self.columns[4].size, y, self.columns[4].size - self.columns[3].size, self.itemheight, 0.3, 1.0, 0.5, 0.5);
    else
        isDamaged = "-"
    end
    
    self:drawText(tostring(partHealth).."%", self.columns[2].size + 10, y + 4, textPartColor.r, textPartColor.g, textPartColor.b, textPartColor.a, UIFont.Small);
    self:drawText(tostring(isBandaged), self.columns[3].size + 10, y + 4, 1, 1, 1, 1, UIFont.Small);
    self:drawText(tostring(isDamaged), self.columns[4].size + 10, y + 4, 1, 1, 1, 1, UIFont.Small);

    return y + self.itemheight;
end
--************************************************************************--
--** Prerender медика
--************************************************************************--
function UIHealth:prerender()
    ISPanel.prerender(self)

	self:drawRect( 0, 0, self.width, 20, 1.0, 0, 0, 0, 0.5)
	self:drawTextCentre(self.title, self:getWidth() / 2, 1, 1, 1, 1, 1, UIFont.Small);
	
end

--************************************************************************--
--** Render медика
--************************************************************************--
function UIHealth:render()
    ISPanel.render(self)

	local player = self.localPlayer;

    self:drawTexture(self.resizeimage, self.width-10, self.height - 10, 1, 1, 1, 1);
    self:drawText(getTranslate("UI_Medic_TotalHealth")..tostring(round(player:getBodyDamage():getHealth())).. "%", 10, 30, 1, 1, 1, 1, UIFont.Small);
end

--************************************************************************--
--** Обновление меню
--************************************************************************--
function UIHealth:update()
    for _, button in pairs(self.buttonList) do
        if button.isRequireBodyPart and self.datas.items[self.datas.selected] == nil then
            button.isEnable = false
        else
            button.isEnable = true;
        end
    end
end

--*********************************************************
--* Получение частей тела
--*********************************************************
function UIHealth:loadBodyParts()
    self.lastSelectedIndex = self.datas.selected or 0;

    self.datas:clear();

    local player = self.localPlayer;
    
    local bodyParts = player:getBodyDamage():getBodyParts()

    for i=1,bodyParts:size() do
        local bodyPart = bodyParts:get(i-1)
        self.datas:addItem("Body Part", bodyPart)
    end

    self.datas.selected = self.lastSelectedIndex;
end

--*********************************************************
--* Получение пациента
--*********************************************************
function UIHealth:getPatient()
    return self.otherPlayer or self.localPlayer
end

--*********************************************************
--* Закрытие меню медика
--*********************************************************
function UIHealth:close()
    UIHealth.instance:setVisible(false);
    UIHealth.instance:removeFromUIManager();
    UIHealth.instance = nil;
end

--*********************************************************
--* Логика открытия меню медика
--*********************************************************
function UIHealth.openPanel()
    -- Если панель уже существует, закрываем окно
    if UIHealth.instance ~= nil then
        UIHealth.instance:setVisible(false);
        UIHealth.instance:removeFromUIManager();
        UIHealth.instance = nil;
        return
    end

    -- Создаем новую панель
    UIHealth.instance = UIHealth:new();
    UIHealth.instance:initialise();
    UIHealth.instance:instantiate();
    UIHealth.instance:addToUIManager();
    UIHealth.instance:setVisible(true);
    UIHealth.instance:setAlwaysOnTop(true);
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function UIHealth:new()
    local menuTableData = {};

    local width = 480;
    local height = 500;

    local positionX = getCore():getScreenWidth() / 2 - width / 2;
    local positionY = getCore():getScreenHeight() / 2 - height / 2;

    menuTableData = ISPanel:new(positionX, positionY, width, height);
    setmetatable(menuTableData, self);
	menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
	menuTableData.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.8};
    menuTableData.title = getTranslate("UI_Medic_Title");
    menuTableData.moveWithMouse = true;
    menuTableData.isPartsLoaded = false;
    menuTableData.localPlayer = getPlayer();
    menuTableData.otherPlayer = nil;
    menuTableData.buttonList = {}
    menuTableData.closeTexture = getTexture("media/ui/Dialog_Titlebar_CloseIcon.png");
    self.__index = self;
    return menuTableData;
end