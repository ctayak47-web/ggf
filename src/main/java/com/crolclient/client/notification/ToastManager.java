package com.crolclient.client.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Управляет очередью Toast-уведомлений CrolClient.
 * Рендерится модулем NotificationsModule (см. module.impl.hud.NotificationsModule),
 * но само хранилище вынесено сюда, чтобы любой другой модуль (Item Notify,
 * Anti-Cactus, Auction Helper и т.д.) мог отправлять уведомления через
 * простой статический вызов ToastManager.getInstance().push(...).
 */
public class ToastManager {

    private static ToastManager instance;

    private final List<Toast> activeToasts = new ArrayList<>();
    private static final int MAX_VISIBLE = 5;

    public static ToastManager getInstance() {
        if (instance == null) {
            instance = new ToastManager();
        }
        return instance;
    }

    public void push(String title, String message, Toast.Type type) {
        push(title, message, type, 4000);
    }

    public void push(String title, String message, Toast.Type type, long durationMillis) {
        activeToasts.add(0, new Toast(title, message, type, durationMillis));
        while (activeToasts.size() > MAX_VISIBLE) {
            activeToasts.remove(activeToasts.size() - 1);
        }
    }

    /** Удаляет истёкшие уведомления — вызывается каждый тик из NotificationsModule. */
    public void tick() {
        Iterator<Toast> it = activeToasts.iterator();
        while (it.hasNext()) {
            if (it.next().isExpired()) {
                it.remove();
            }
        }
    }

    public List<Toast> getActiveToasts() {
        return activeToasts;
    }
}
