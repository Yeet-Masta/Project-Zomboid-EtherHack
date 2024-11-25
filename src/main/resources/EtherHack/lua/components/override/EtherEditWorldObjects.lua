
EtherEditWorldObjects = {};
EtherEditWorldObjects.staggerBacking = false;
EtherEditWorldObjects.stagTime = 0;
EtherEditWorldObjects.ticked = false;

EtherEditWorldObjects.doEtherContextDebugMenu = function(player, context, worldobjects, test)
	if test and ISWorldObjectContextMenu.Test then return true end

	EtherEditWorldObjects.doDebugObjectMenu(player, context, worldobjects, test)
end


function EtherEditWorldObjects.doDebugObjectMenu(player, context, worldobjects, test)
	local x = getMouseX()
	local y = getMouseY()

	local playerObj = getSpecificPlayer(player)

	local debugOption = context:addOption(getTranslate("UI_DebugObject_Title"), worldobjects, nil);
	local subMenu = ISContextMenu:getNew(context);
	context:addSubMenu(debugOption, subMenu);

	local sq = getSpecificPlayer(player):getCurrentSquare()
	if sq and sq:getBuilding() then
		if test then return ISWorldObjectContextMenu.setTest() end
		subMenu:addOption(getTranslate("UI_DebugObject_GetBuildingKey"), worldobjects, EtherEditWorldObjects.OnGetBuildingKey, player)
	end

	local window = IsoObjectPicker.Instance:PickWindow(x, y)
	if instanceof(window, "IsoWindow") then
		if test then return ISWorldObjectContextMenu.setTest() end
		subMenu:addOption(window:isLocked() and getTranslate("UI_DebugObject_WindowUnlock") or getTranslate("UI_DebugObject_WindowLock"), worldobjects, EtherEditWorldObjects.OnWindowLock, window)
		subMenu:addOption(window:isPermaLocked() and getTranslate("UI_DebugObject_WindowPermUnlock") or getTranslate("UI_DebugObject_WindowPermLock"), worldobjects, EtherEditWorldObjects.OnWindowPermLock, window)
	end

	local metalDrum = nil
	local rainBarrel = nil

	for _,obj in ipairs(worldobjects) do
		if instanceof(obj, "IsoDoor") or (instanceof(obj, "IsoThumpable") and obj:isDoor()) then
			subMenu:addOption(getTranslate("UI_DebugObject_DoorKey"), worldobjects, EtherEditWorldObjects.OnGetDoorKey, obj, player)
			subMenu:addOption(obj:isLocked() and getTranslate("UI_DebugObject_DoorUnlock") or getTranslate("UI_DebugObject_DoorLock"), worldobjects, EtherEditWorldObjects.OnDoorLock, obj)
		end
		if instanceof(obj, "IsoGenerator") then
			subMenu:addOption(getTranslate("UI_DebugObject_GeneratorSetFuel"), obj, EtherEditWorldObjects.OnGeneratorSetFuel)
		end
		if instanceof(obj, "IsoBarbecue") then
			subMenu:addOption(getTranslate("UI_DebugObject_BBQZeroFuel"), obj, EtherEditWorldObjects.OnBBQZeroFuel)
			subMenu:addOption(getTranslate("UI_DebugObject_BBQSetFuel"), obj, EtherEditWorldObjects.OnBBQSetFuel)
		end
		if instanceof(obj, "IsoFireplace") then
			subMenu:addOption(getTranslate("UI_DebugObject_FireplaceZeroFuel"), obj, EtherEditWorldObjects.OnFireplaceZeroFuel)
			subMenu:addOption(getTranslate("UI_DebugObject_FireplaceSetFuel"), obj, EtherEditWorldObjects.OnFireplaceSetFuel)
		end
		
		if CCampfireSystem.instance:isValidIsoObject(obj) then
			subMenu:addOption(getTranslate("UI_DebugObject_CampfireZeroFuel"), obj, EtherEditWorldObjects.OnCampfireZeroFuel)
			subMenu:addOption(getTranslate("UI_DebugObject_CampfireSetFuel"), obj, EtherEditWorldObjects.OnCampfireSetFuel)
		end
		if not metalDrum and CMetalDrumSystem:isValidIsoObject(obj) then
			if obj:hasModData() and not obj:getModData().haveLogs and not obj:getModData().haveCharcoal then
				subMenu:addOption(getTranslate("UI_DebugObject_MDrumZeroFuel"), obj, EtherEditWorldObjects.OnMetalDrumZeroWater)
				subMenu:addOption(getTranslate("UI_DebugObject_MDrumSetFuel"), obj, EtherEditWorldObjects.OnMetalDrumSetWater)
			end
			metalDrum = obj
		end
		if not rainBarrel and CRainBarrelSystem:isValidIsoObject(obj) then
			subMenu:addOption(getTranslate("UI_DebugObject_RBarrelZeroFuel"), obj, EtherEditWorldObjects.OnRainBarrelZeroWater)
			subMenu:addOption(getTranslate("UI_DebugObject_RBarrelSetFuel"), obj, EtherEditWorldObjects.OnRainBarrelSetWater)
			rainBarrel = obj
		end
	end

	square = EtherEditWorldObjects.pickSquare(x, y)
	if square then
		for i=1,square:getObjects():size() do
			local obj = square:getObjects():get(i-1)
			if BentFences.getInstance():isBentObject(obj) then
				subMenu:addOption(getTranslate("UI_DebugObject_UnbendFence"), worldobjects, EtherEditWorldObjects.OnUnbendFence, obj)
			end
			if BentFences.getInstance():isUnbentObject(obj) then
				subMenu:addOption(getTranslate("UI_DebugObject_BendFence"), worldobjects, EtherEditWorldObjects.OnBendFence, obj)
			end
			if BrokenFences.getInstance():isBreakableObject(obj) then
				subMenu:addOption(getTranslate("UI_DebugObject_BreakFence"), worldobjects, EtherEditWorldObjects.OnBreakFence, obj)
			end
			if instanceof(obj, "IsoCompost") then
				subMenu:addOption(getTranslate("UI_DebugObject_SetCompost"), worldobjects, EtherEditWorldObjects.OnSetCompost, obj)
			end
		end
	end

	if #subMenu.options == 0 then
		context:removeLastOption()
	end
end


function EtherEditWorldObjects.OnGetBuildingKey(worldobjects, player)
	local sq = getSpecificPlayer(player):getCurrentSquare()
	if sq and sq:getBuilding() then
		getSpecificPlayer(player):getInventory():AddItem("Base.Key1"):setKeyId(sq:getBuilding():getDef():getKeyId())
	end
end

function EtherEditWorldObjects.OnGetDoorKey(worldobjects, door, player)
	local keyID = -1
	if instanceof(door, "IsoDoor") then
		keyID = door:checkKeyId()
	elseif instanceof(door, "IsoThumpable") then
		keyID = door:getKeyId()
	end

	if keyID == -1 then
		keyID = ZombRand(100000000)
	end
	door:setKeyId(keyID)

	local doubleDoorObjects = buildUtil.getDoubleDoorObjects(door)
	for i=1,#doubleDoorObjects do
		local object = doubleDoorObjects[i]
		object:setKeyId(keyID)
	end

	local garageDoorObjects = buildUtil.getGarageDoorObjects(door)
	for i=1,#garageDoorObjects do
		local object = garageDoorObjects[i]
		object:setKeyId(keyID)
	end

	getSpecificPlayer(player):getInventory():AddItem("Base.Key1"):setKeyId(keyID)
end

function EtherEditWorldObjects.OnDoorLock(worldobjects, door)
	door:setIsLocked(not door:isLocked())
	if instanceof(door, "IsoDoor") and door:checkKeyId() ~= -1 then
		door:setLockedByKey(door:isLocked())
	end
	if instanceof(door, "IsoThumpable") and door:getKeyId() ~= -1 then
		door:setLockedByKey(door:isLocked())
	end
	getPlayer():getMapKnowledge():setKnownBlockedDoor(door, door:isLocked())

	local doubleDoorObjects = buildUtil.getDoubleDoorObjects(door)
	for i=1,#doubleDoorObjects do
		local object = doubleDoorObjects[i]
		object:setLockedByKey(door:isLocked())
	end

	local garageDoorObjects = buildUtil.getGarageDoorObjects(door)
	for i=1,#garageDoorObjects do
		local object = garageDoorObjects[i]
		object:setLockedByKey(door:isLocked())
	end
end

function EtherEditWorldObjects.OnSetDoorKeyIDBuilding(worldobjects, door)
	local sq = door:getSquare()
	local sq2 = door:getOppositeSquare()
	if sq == nil or sq2 == nil then return end
	local building = sq:getBuilding()
	local building2 = sq2:getBuilding()
	local bDef = nil
	local bDef2 = nil
	if building ~= nil then
		bDef = building:getDef()
	end
	if building2 ~= nil then
		bDef2 = building2:getDef()
	end
	if bDef ~= nil then
		door:setKeyId(bDef:getKeyId())
	elseif bDef2 ~= nil then
		door:setKeyId(bDef2:getKeyId())
	end
end

function EtherEditWorldObjects.OnSetDoorKeyIDRandom(worldobjects, door)
	local keyId = ZombRand(100000000)
	door:setKeyId(keyId)

	local doubleDoorObjects = buildUtil.getDoubleDoorObjects(door)
	for i=1,#doubleDoorObjects do
		local object = doubleDoorObjects[i]
		object:setKeyId(keyId)
	end

	local garageDoorObjects = buildUtil.getGarageDoorObjects(door)
	for i=1,#garageDoorObjects do
		local object = garageDoorObjects[i]
		object:setKeyId(keyId)
	end
end

function EtherEditWorldObjects.setForceLockDoor(worldobjects, door, player)
	if not door:getModData().CustomLock then
		door:getModData().CustomLock = true
	else
		door:getModData().CustomLock = false
	end
	door:transmitModData()
end

function EtherEditWorldObjects.OnWindowLock(worldobjects, window)
	window:setIsLocked(not window:isLocked())
end

function EtherEditWorldObjects.OnWindowPermLock(worldobjects, window)
	window:setPermaLocked(not window:isPermaLocked())
end

function EtherEditWorldObjects.OnWindowSmash(worldobjects, window)
	window:setSmashed(not window:isSmashed())
end

function EtherEditWorldObjects.OnWindowGlassRemoved(worldobjects, window)
	window:setGlassRemoved(not window:isGlassRemoved())
end

function EtherEditWorldObjects.pickSquare(x, y)
	local zoom = getCore():getZoom(0)
	local z = getSpecificPlayer(0):getSquare():getZ()
	local worldX = IsoUtils.XToIso(x * zoom, y * zoom, z)
	local worldY = IsoUtils.YToIso(x * zoom, y * zoom, z)
	return getCell():getGridSquare(worldX, worldY, z), worldX, worldY, z
end

function EtherEditWorldObjects.OnBendFence(worldobjects, fence)
	local playerObj = getSpecificPlayer(0)
	local props = fence:getProperties()
	local dir = nil
	if props:Is(IsoFlagType.collideN) and props:Is(IsoFlagType.collideW) then
		dir = (playerObj:getY() >= fence:getY()) and IsoDirections.N or IsoDirections.S
	elseif props:Is(IsoFlagType.collideN) then
		dir = (playerObj:getY() >= fence:getY()) and IsoDirections.N or IsoDirections.S
	else
		dir = (playerObj:getX() >= fence:getX()) and IsoDirections.W or IsoDirections.E
	end
	BentFences.getInstance():bendFence(fence, dir)
end

function EtherEditWorldObjects.OnUnbendFence(worldobjects, fence)
	BentFences.getInstance():unbendFence(fence)
end

function EtherEditWorldObjects.OnBreakFence(worldobjects, fence)
	local playerObj = getSpecificPlayer(0)
	local props = fence:getProperties()
	local dir = nil
	if props:Is(IsoFlagType.collideN) and props:Is(IsoFlagType.collideW) then
		dir = (playerObj:getY() >= fence:getY()) and IsoDirections.N or IsoDirections.S
	elseif props:Is(IsoFlagType.collideN) then
		dir = (playerObj:getY() >= fence:getY()) and IsoDirections.N or IsoDirections.S
	else
		dir = (playerObj:getX() >= fence:getX()) and IsoDirections.W or IsoDirections.E
	end
	fence:destroyFence(dir)
end

function EtherEditWorldObjects.OnBBQZeroFuel(obj)
	local playerObj = getSpecificPlayer(0)
	local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), fuelAmt = 0 }
	sendClientCommand(playerObj, 'bbq', 'setFuel', args)
end

local function OnBBQSetFuel2(target, button, obj)
	if button.internal == "OK" then
		local playerObj = getSpecificPlayer(0)
		local text = button.parent.entry:getText()
		if tonumber(text) then
			local fuelAmt = math.min(tonumber(text), 100.0)
			fuelAmt = math.max(fuelAmt, 0.0)
			local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), fuelAmt = fuelAmt }
			sendClientCommand(playerObj, 'bbq', 'setFuel', args)
		end
	end
end

function EtherEditWorldObjects.OnBBQSetFuel(obj)
	local modal = ISTextBox:new(0, 0, 280, 180, getTranslate("UI_DebugObject_FuelLevel"), tostring(obj:getFuelAmount()), nil, OnBBQSetFuel2, nil, obj)
	modal:initialise()
	modal:addToUIManager()
end

function EtherEditWorldObjects.OnCampfireZeroFuel(obj)
	local playerObj = getSpecificPlayer(0)
	local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), fuelAmt = 0 }
	CCampfireSystem.instance:sendCommand(playerObj, 'setFuel', args)
end

local function OnCampfireSetFuel2(target, button, obj)
	if button.internal == "OK" then
		local playerObj = getSpecificPlayer(0)
		local text = button.parent.entry:getText()
		if tonumber(text) then
			local fuelAmt = math.min(tonumber(text), 100.0)
			fuelAmt = math.max(fuelAmt, 0.0)
			local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), fuelAmt = fuelAmt }
			CCampfireSystem.instance:sendCommand(playerObj, 'setFuel', args)
		end
	end
end

function EtherEditWorldObjects.OnCampfireSetFuel(obj)
	local luaObject = CCampfireSystem.instance:getLuaObjectOnSquare(obj:getSquare())
	if not luaObject then return end
	local modal = ISTextBox:new(0, 0, 280, 180, getTranslate("UI_DebugObject_FuelLevel"), tostring(luaObject.fuelAmt), nil, OnCampfireSetFuel2, nil, obj)
	modal:initialise()
	modal:addToUIManager()
end

function EtherEditWorldObjects.OnFireplaceZeroFuel(obj)
	local playerObj = getSpecificPlayer(0)
	local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), fuelAmt = 0 }
	sendClientCommand(playerObj, 'fireplace', 'setFuel', args)
end

local function OnFireplaceSetFuel2(target, button, obj)
	if button.internal == "OK" then
		local playerObj = getSpecificPlayer(0)
		local text = button.parent.entry:getText()
		if tonumber(text) then
			local fuelAmt = math.min(tonumber(text), 100.0)
			fuelAmt = math.max(fuelAmt, 0.0)
			local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), fuelAmt = fuelAmt }
			sendClientCommand(playerObj, 'fireplace', 'setFuel', args)
		end
	end
end

function EtherEditWorldObjects.OnFireplaceSetFuel(obj)
	local modal = ISTextBox:new(0, 0, 280, 180, getTranslate("UI_DebugObject_FuelLevel"), tostring(obj:getFuelAmount()), nil, OnFireplaceSetFuel2, nil, obj)
	modal:initialise()
	modal:addToUIManager()
end


local function OnSetCompost2(target, button, obj)
	if button.internal == "OK" then
		local text = button.parent.entry:getText()
		if tonumber(text) then
			local compost = math.min(tonumber(text), 100.0)
			compost = math.max(compost, 0.0)
			obj:setCompost(compost)
		end
	end
end

function EtherEditWorldObjects.OnSetCompost(worldobjects, obj)
	local modal = ISTextBox:new(0, 0, 280, 180, getTranslate("UI_DebugObject_SetCompostLevel"), tostring(obj:getCompost()), nil, OnSetCompost2, nil, obj)
	modal:initialise()
	modal:addToUIManager()
end

local function OnGeneratorSetFuel2(target, button, obj)
	if button.internal == "OK" then
		local text = button.parent.entry:getText()
		if tonumber(text) then
			local compost = math.min(tonumber(text), 100.0)
			compost = math.max(compost, 0.0)
			obj:setFuel(compost)
		end
	end
end

function EtherEditWorldObjects.OnGeneratorSetFuel(obj)
	local modal = ISTextBox:new(0, 0, 280, 180, getTranslate("UI_DebugObject_SetFuelLevel"), tostring(obj:getFuel()), nil, OnGeneratorSetFuel2, nil, obj)
	modal:initialise()
	modal:addToUIManager()
end


function EtherEditWorldObjects.OnMetalDrumZeroWater(obj)
	local playerObj = getSpecificPlayer(0)
	local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), index = obj:getObjectIndex(), amount = 0 }
	sendClientCommand(playerObj, 'object', 'setWaterAmount', args)
end

local function OnMetalDrumSetWater2(target, button, obj)
	if button.internal == "OK" then
		local playerObj = getSpecificPlayer(0)
		local text = button.parent.entry:getText()
		if tonumber(text) then
			local waterAmt = math.min(tonumber(text), obj:getWaterMax())
			waterAmt = math.max(waterAmt, 0.0)
			local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), index = obj:getObjectIndex(), amount = waterAmt }
			sendClientCommand(playerObj, 'object', 'setWaterAmount', args)
		end
	end
end

function EtherEditWorldObjects.OnMetalDrumSetWater(obj)
	local luaObject = CMetalDrumSystem.instance:getLuaObjectOnSquare(obj:getSquare())
	if not luaObject then return end
	local modal = ISTextBox:new(0, 0, 280, 180, string.format(getTranslate("UI_DebugObject_SetWaterLevel"), obj:getWaterMax()), tostring(obj:getWaterAmount()), nil, OnMetalDrumSetWater2, nil, obj)
	modal:initialise()
	modal:addToUIManager()
end

function EtherEditWorldObjects.OnRainBarrelZeroWater(obj)
	local playerObj = getSpecificPlayer(0)
	local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), index = obj:getObjectIndex(), amount = 0 }
	sendClientCommand(playerObj, 'object', 'setWaterAmount', args)
end

local function OnRainBarrelSetWater2(target, button, obj)
	if button.internal == "OK" then
		local playerObj = getSpecificPlayer(0)
		local text = button.parent.entry:getText()
		if tonumber(text) then
			local waterAmt = math.min(tonumber(text), obj:getWaterMax())
			waterAmt = math.max(waterAmt, 0.0)
			local args = { x = obj:getX(), y = obj:getY(), z = obj:getZ(), index = obj:getObjectIndex(), amount = waterAmt }
			sendClientCommand(playerObj, 'object', 'setWaterAmount', args)
		end
	end
end

function EtherEditWorldObjects.OnRainBarrelSetWater(obj)
	local luaObject = CRainBarrelSystem.instance:getLuaObjectOnSquare(obj:getSquare())
	if not luaObject then return end
	local modal = ISTextBox:new(0, 0, 280, 180, string.format(getTranslate("UI_DebugObject_SetWaterLevel"), obj:getWaterMax()), tostring(obj:getWaterAmount()), nil, OnRainBarrelSetWater2, nil, obj)
	modal:initialise()
	modal:addToUIManager()
end

Events.OnFillWorldObjectContextMenu.Add(EtherEditWorldObjects.doEtherContextDebugMenu);
