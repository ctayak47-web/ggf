package com.crolclient.client.notification;

/** Одно всплывающее уведомление (Toast) в системе CrolClient. */
public class Toast {

    public enum Type {
        INFO(0x3B82F6FF),
        SUCCESS(0x22C55EFF),
        WARNING(0xF59E0BFF),
        ERROR(0xEF4444FF);

        public final int color;

        Type(int color) {
            this.color = color;
        }
    }

    public final String title;
    public final String message;
    public final Type type;
    public final long createdAtMillis;
    public final long durationMillis;

    public Toast(String title, String message, Type type, long durationMillis) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.durationMillis = durationMillis;
        this.createdAtMillis = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAtMillis > durationMillis;
    }

    /** Прогресс жизни тоста от 0.0 (только создан) до 1.0 (истёк) — для анимации исчезновения. */
    public float progress() {
        float p = (float) (System.currentTimeMillis() - createdAtMillis) / durationMillis;
        return Math.max(0f, Math.min(1f, p));
    }
}
