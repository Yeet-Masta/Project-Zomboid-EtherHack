require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
EtherInfoPanel = ISPanel:derive("EtherInfoPanel"); -- Наследование от ISPanel

--*********************************************************
--* Метод рисования текста по середние
--*********************************************************
function EtherInfoPanel:drawTextCentered(text, y, r, g, b, a, font)
    local x = self.width / 2 - (getTextManager():MeasureStringX(font, text) / 2)
    self:drawText(text, x, y, r, g, b, a, font)
end

--*********************************************************
--* Отрисовка текста
--*********************************************************
function EtherInfoPanel:render()
    local y = 30
    local marginTitle = 25;
    local marginText = 15;
    self:drawTextCentered(getTranslate("UI_InformationPanel_General_Title"), y, 1, 1, 1, 1, UIFont.Medium)

    y = y + marginTitle

    local generalTexts = {
        "UI_InformationPanel_General_Text1",
        "UI_InformationPanel_General_Text2",
        "UI_InformationPanel_General_Text3",
        "UI_InformationPanel_General_Text4",
        "UI_InformationPanel_General_Text5",
    }
    for i, textKey in ipairs(generalTexts) do
        self:drawTextCentered(getTranslate(textKey), y, 1, 1, 1, 1, UIFont.Small)
        y = y + marginText
    end

    y = y + marginText

    self:drawTextCentered(getTranslate("UI_InformationPanel_Disclaimer_Title"), y, 1, 1, 1, 1, UIFont.Medium)

    y = y + marginTitle

    local disclaimerTexts = {
        "UI_InformationPanel_Disclaimer_Text1",
        "UI_InformationPanel_Disclaimer_Text2",
        "UI_InformationPanel_Disclaimer_Text3",
        "UI_InformationPanel_Disclaimer_Text4",
        "UI_InformationPanel_Disclaimer_Text5",
    }
    for i, textKey in ipairs(disclaimerTexts) do
        self:drawTextCentered(getTranslate(textKey), y, 1, 1, 1, 1, UIFont.Small)
        y = y + marginText
    end

    y = y + marginText

    self:drawTextCentered(getTranslate("UI_InformationPanel_AntiCheatStatus_Title"), y, 1, 1, 1, 1, UIFont.Medium)

    y = y + marginTitle

    local antiCheatStatusTexts = {
        "UI_InformationPanel_AntiCheatStatus_Text1",
        "UI_InformationPanel_AntiCheatStatus_Text2",
        "UI_InformationPanel_AntiCheatStatus_BikiniTools",
        "UI_InformationPanel_AntiCheatStatus_CustomLogger"
    }

    local customLogger = PARP ~= nil or LogExtenderClient ~= nil or LogExtenderServer ~= nil or AVCS ~= nil;
    local bikinitools = BTSE ~= nil or PARP ~= nil or Bikinitools ~= nil;

    local antiCheatStatus = {
        getAntiCheat12Status(),
        getAntiCheat8Status(),
        bikinitools,
        customLogger
    }

    for i, textKey in ipairs(antiCheatStatusTexts) do
        local statusText = antiCheatStatus[i] 
                        and getTranslate("UI_InformationPanel_AntiCheatStatus_Enable")
                        or getTranslate("UI_InformationPanel_AntiCheatStatus_Disable")

        if self.localPlayer == nil then statusText = getTranslate("UI_InformationPanel_AntiCheatStatus_Disable") end
        
        self:drawTextCentered(getTranslate(textKey) .. statusText, y, 1, 1, 1, 1, UIFont.Small)
        y = y + marginText
    end


    y = y + marginText
    
    self:drawTextCentered(getTranslate("UI_InformationPanel_Contacts_Title"), y, 1, 1, 1, 1, UIFont.Medium)

    y = y + marginTitle

    local contactTexts = {
        "GitHub: https://github.com/Quzile",
        "YouTube: https://www.youtube.com/@quzile",
        "Discord: quzile (Quzile#9580)",
        "Email: rubranny@gmail.com",
        getTranslate("UI_InformationPanel_Contacts_Donation") .. "https://www.donationalerts.com/r/quzile",
    }
    for i, text in ipairs(contactTexts) do
        self:drawTextCentered(text, y, 1, 1, 1, 1, UIFont.Small)
        y = y + marginText
    end
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EtherInfoPanel:new(posX, posY, width, height)
    local menuTableData = {};

    menuTableData = ISPanel:new(posX, posY, width, height);
    setmetatable(menuTableData, self);
    menuTableData.background = true;
	menuTableData.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.0};
	menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    menuTableData.moveWithMouse = true;
    menuTableData.localPlayer = getPlayer();
    self.__index = self;

    return menuTableData;
end