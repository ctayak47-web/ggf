package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

import java.util.ArrayList;
import java.util.List;

/** Подсвечивает сообщения чата, содержащие заданные ключевые слова (без скрытия остальных). */
public class ChatFilterModule extends Module {
    private final List<String> keywords = new ArrayList<>();

    public ChatFilterModule() {
        super("ChatFilter", ModuleCategory.UTILITY, "Подсвечивает сообщения чата по ключевым словам.");
    }

    public void addKeyword(String word) {
        keywords.add(word.toLowerCase());
    }

    public void removeKeyword(String word) {
        keywords.remove(word.toLowerCase());
    }

    public boolean matches(String message) {
        String lower = message.toLowerCase();
        return keywords.stream().anyMatch(lower::contains);
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
