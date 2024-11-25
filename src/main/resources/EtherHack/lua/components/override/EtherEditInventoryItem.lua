local function EtherEditInventoryItem(player, context, items)
    local container = nil
    local resItems = {}
    for i,v in ipairs(items) do
        if not instanceof(v, "InventoryItem") then
            for _, it in ipairs(v.items) do
                resItems[it] = true
            end
            container = v.items[1]:getContainer()
        else
            resItems[v] = true
            container = v:getContainer()
        end
    end

    local listItems = {}
    for v, _ in pairs(resItems) do
        table.insert(listItems, v)
    end

    context:addOption(getText("ContextMenu_EditItem"), items, function ()
        local ui = ISItemEditorUI:new(50,50,600,600, getPlayer(), listItems[1]);
        ui:initialise();
        ui:addToUIManager();
    end, getPlayer());

    local removeOption = context:addOption(getTranslate("UI_DeleteItemTitle"))
    local subMenuRemove = ISContextMenu:getNew(context)
    context:addSubMenu(removeOption, subMenuRemove)

    subMenuRemove:addOption(getTranslate("UI_DeleteItemOne"), listItems[1], ISRemoveItemTool.removeItem, player)
    subMenuRemove:addOption(getTranslate("UI_DeleteItemSelected"), listItems, ISRemoveItemTool.removeItems, player)
end
Events.OnFillInventoryObjectContextMenu.Add(EtherEditInventoryItem)
