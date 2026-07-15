package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Хранит заранее заданные фразы-макросы, отправляемые в чат ТОЛЬКО по
 * явному нажатию хоткея игроком (никакой автоматической отправки без
 * участия пользователя).
 */
public class ChatMacrosModule extends Module {
    private final Map<Integer, String> macros = new LinkedHashMap<>();

    public ChatMacrosModule() {
        super("ChatMacros", ModuleCategory.UTILITY, "Отправляет заранее заданные фразы в чат по хоткею.");
    }

    public void setMacro(int keyCode, String message) {
        macros.put(keyCode, message);
    }

    /** Вызывается из обработчика клавиш при совпадении keyCode с одним из макросов. */
    public void handleKey(int keyCode) {
        if (!isEnabled()) return;
        String message = macros.get(keyCode);
        if (message != null && mc.player != null && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendChatMessage(message);
        }
    }

    public Map<Integer, String> getMacros() {
        return macros;
    }
}
