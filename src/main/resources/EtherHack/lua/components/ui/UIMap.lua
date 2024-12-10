UIMap = ISWorldMap:derive("UIMap")

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function UIMap:createChildren() end

--*********************************************************
--* Восстановление настроек
--*********************************************************
function UIMap:restoreSettings()
	if not MainScreen.instance or not MainScreen.instance.inGame then return end
	local settings = WorldMapSettings.getInstance()
	if settings:getFileVersion() ~= 1 then return end
	local centerX = settings:getDouble("WorldMap.CenterX", 0.0)
	local centerY = settings:getDouble("WorldMap.CenterY", 0.0)
	local zoom = settings:getDouble("WorldMap.Zoom", 0.0)
	if zoom == 0.0 then return end -- ISMiniMap loaded settings for the first time
	local isometric = settings:getBoolean("WorldMap.Isometric")

	if self.localPlayer ~= nil then
		centerX = self.localPlayer:getX()
		centerY = self.localPlayer:getY()
		zoom = 18;
	end

	self.mapAPI:centerOn(centerX, centerY)
	self.mapAPI:setZoom(zoom)
	self.mapAPI:setBoolean("Isometric", isometric)
end

--*********************************************************
--* Отрисовка символов
--*********************************************************
function UIMap:onToggleSymbols()

end
--*********************************************************
--* Ограничение значений
--*********************************************************
local function clamp(val, lower, upper)
    if lower > upper then lower, upper = upper, lower end
    return math.max(lower, math.min(upper, val))
end

function UIMap:prerender()
	if self.centerByPlayer then
		if self.dragging then return end
		local playerObj = self.localPlayer;
		if not playerObj then return end
		local vehicle = playerObj:getVehicle();
		if vehicle then
			self.mapAPI:centerOn(vehicle:getX(), vehicle:getY())
		else
			self.mapAPI:centerOn(playerObj:getX(), playerObj:getY())
		end
	end
end
--*********************************************************
--* Отрисовка
--*********************************************************
function UIMap:render()

	self:suspendStencil()
    self:clampStencilRectToParent(0, 0, self:getWidth(), self:getHeight() )

	-- Отрисовка зомби
	if isMapDrawZombies() then
		local zombies = getCell():getZombieList()
		for i=1,zombies:size() do
			local zombie = zombies:get(i-1)

			local x = self.mapAPI:worldToUIX(zombie:getX(), zombie:getY());
			local y = self.mapAPI:worldToUIY(zombie:getX(), zombie:getY());

			local size = 125 / self.mapAPI:getWorldScale()
			size = clamp(size, 2, 5)

			self:drawRect(x - size, y - size, size * 2 - 1, size * 2 - 1, self.zombieColor.a, self.zombieColor.r, self.zombieColor.g, self.zombieColor.b);
			self:drawRectBorder(x - size, y - size, size * 2, size * 2, 1, 0, 0, 0);
		end
	end

	-- Отрисовка машин
	if isMapDrawVehicles() then
		local vehicles = getCell():getVehicles()
		for i=1,vehicles:size() do
			local vehicle = vehicles:get(i-1)

			local x = self.mapAPI:worldToUIX(vehicle:getX(), vehicle:getY());
			local y = self.mapAPI:worldToUIY(vehicle:getX(), vehicle:getY());

			local size = 125 / self.mapAPI:getWorldScale()
			size = clamp(size, 2, 5)

			self:drawRect(x - size, y - size, size * 2 - 1, size * 2 - 1, self.vehicleColor.a, self.vehicleColor.r, self.vehicleColor.g, self.vehicleColor.b);
			self:drawRectBorder(x - size, y - size, size * 2, size * 2, 1, 0, 0, 0);
			if self.mapAPI:getWorldScale() > 5 then
			self:drawTextCentre(vehicle:getScriptName(), x + 1, y + 6, 0.0, 0.0, 0.0, 1.0, UIFont.Small);
			self:drawTextCentre(vehicle:getScriptName(), x, y + 5, 1.0, 1.0, 1.0, 1.0, UIFont.Small);
		end
		end
	end

	-- Отрисовка других игроков
	if isMapDrawAllPlayers() then
        local players = getOnlinePlayers()
        if players ~= nil then
            for i=1,players:size() do
                local player = players:get(i-1)
                if player ~= self.localPlayer then
                    local x = self.mapAPI:worldToUIX(player:getX(), player:getY());
                    local y = self.mapAPI:worldToUIY(player:getX(), player:getY());

                    local size = 125 / self.mapAPI:getWorldScale()
                    size = clamp(size, 2, 5)

                    self:drawRect(x - size, y - size, size * 2 - 1, size * 2 - 1, self.playerColor.a, self.playerColor.r, self.playerColor.g, self.playerColor.b);
                    self:drawRectBorder(x - size, y - size, size * 2, size * 2, 1, 0, 0, 0);
                    if self.mapAPI:getWorldScale() > 1 then
                        self:drawTextCentre(player:getUsername(), x + 1, y + 6, 0.0, 0.0, 0.0, 1.0, UIFont.Small);
                        self:drawTextCentre(player:getUsername(), x, y + 5, 1.0, 1.0, 1.0, 1.0, UIFont.Small);
                    end
                end
            end
        end
    end

	-- Отрисовка локального игрока
	if isMapDrawLocalPlayer() then
		local player = self.localPlayer;

		local x = self.mapAPI:worldToUIX(player:getX(), player:getY());
		local y = self.mapAPI:worldToUIY(player:getX(), player:getY());

		local size = 125 / self.mapAPI:getWorldScale()
		size = clamp(size, 2, 5)

		self:drawRect(x - size, y - size, size * 2 - 1, size * 2 - 1, self.localPlayerColor.a, self.localPlayerColor.r, self.localPlayerColor.g, self.localPlayerColor.b);
		self:drawRectBorder(x - size, y - size, size * 2, size * 2, 1, 0, 0, 0);
		if self.mapAPI:getWorldScale() > 1 then
			self:drawTextCentre(player:getUsername(), x + 1, y + 6, 0.0, 0.0, 0.0, 1.0, UIFont.Small);
			self:drawTextCentre(player:getUsername(), x, y + 5, 1.0, 1.0, 1.0, 1.0, UIFont.Small);
		end
	end

	self:clearStencilRect()
    self:resumeStencil()
end

--*********************************************************
--* Нажатие джойстиком
--*********************************************************
function UIMap:onJoypadDown()

end

--*********************************************************
--* ЛКМ - нажатие клавиши
--*********************************************************
function UIMap:onMouseDown(x, y)
	self.dragging = true
	self.dragMoved = false
	self.dragStartX = x
	self.dragStartY = y
	self.dragStartCX = self.mapAPI:getCenterWorldX()
	self.dragStartCY = self.mapAPI:getCenterWorldY()
	self.dragStartZoomF = self.mapAPI:getZoomF()
	self.dragStartWorldX = self.mapAPI:uiToWorldX(x, y)
	self.dragStartWorldY = self.mapAPI:uiToWorldY(x, y)
	return true
end

--*********************************************************
--* Движение мыши
--*********************************************************
function UIMap:onMouseMove(dx, dy)
	if self.dragging then
		local mouseX = self:getMouseX()
		local mouseY = self:getMouseY()
		if not self.dragMoved and math.abs(mouseX - self.dragStartX) <= 4 and math.abs(mouseY - self.dragStartY) <= 4 then
			return
		end
		self.dragMoved = true
		local worldX = self.mapAPI:uiToWorldX(mouseX, mouseY, self.dragStartZoomF, self.dragStartCX, self.dragStartCY)
		local worldY = self.mapAPI:uiToWorldY(mouseX, mouseY, self.dragStartZoomF, self.dragStartCX, self.dragStartCY)
		self.mapAPI:centerOn(self.dragStartCX + self.dragStartWorldX - worldX, self.dragStartCY + self.dragStartWorldY - worldY)
	end
	return true
end

--*********************************************************
--* Движение мыши вне карты
--*********************************************************
function UIMap:onMouseMoveOutside(dx, dy)
	return self:onMouseMove(dx, dy)
end

--*********************************************************
--* ЛКМ - поднятие клавиши
--*********************************************************
function UIMap:onMouseUp(x, y)
	self.dragging = false
	return true
end

--*********************************************************
--* ЛКМ - поднятие клавиши мыши вне карты
--*********************************************************
function UIMap:onMouseUpOutside(x, y)
	self.dragging = false
	return true
end

--*********************************************************
--* Движение колесика мыши
--*********************************************************
function UIMap:onMouseWheel(del)
	self.mapAPI:zoomAt(self:getMouseX(), self:getMouseY(), del)
	return true
end

--*********************************************************
--* ПКМ - нажатие клавиши
--*********************************************************
function UIMap:onRightMouseDown(x, y)
	return false
end

--*********************************************************
--* ПКМ - поднятие клавиши
--*********************************************************
function UIMap:onRightMouseUp(x, y)
	local context = ISContextMenu.get(0, x + self:getAbsoluteX(), y + self:getAbsoluteY())

	local player = self.localPlayer;
	local worldX = self.mapAPI:uiToWorldX(x, y)
	local worldY = self.mapAPI:uiToWorldY(x, y)

	local maxDistance = 100;

	if math.abs(worldX - player:getX() ) > maxDistance or math.abs(worldY - player:getY()) > maxDistance then
		return;
	end
	if getWorld():getMetaGrid():isValidChunk(worldX / 10, worldY / 10) then
		local option = context:addOption(getTranslate("UI_Map_TeleportContext"), self, self.onTeleport, worldX, worldY)
	end
end

--*********************************************************
--* Безопасная телепортация
--*********************************************************
function UIMap:onTeleport(x, y)
	if isPlayerInSafeTeleported() then
		return
	end

	safePlayerTeleport(x, y);
end
--*********************************************************
--* Создание нового экземпляра
--*********************************************************
function UIMap:new(x, y, width, height)
	local uiTableData = {}

	uiTableData = ISWorldMap:new(x, y, width, height)
	setmetatable(uiTableData, self)
	self.__index = self

	uiTableData.localPlayer = getPlayer();
	uiTableData.localPlayerColor = {r = 0.5, g = 1.0, b = 0.5, a = 1.0}
	uiTableData.playerColor = {r = 1.0, g = 0.2, b = 0.2, a = 1.0}
	uiTableData.vehicleColor = {r = 0.2, g = 0.2, b = 1.0, a = 1.0}
	uiTableData.zombieColor = {r = 1.0, g = 0.5, b = 0.3, a = 1.0}
	uiTableData.centerByPlayer = false;


	return uiTableData
end