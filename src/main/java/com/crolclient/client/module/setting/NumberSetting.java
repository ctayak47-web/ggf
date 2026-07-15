package com.crolclient.client.module.setting;

/** Настройка типа "слайдер" — число с ограничением min/max и шагом. */
public class NumberSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double get() {
        return value;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    /** Устанавливает значение с зажимом в границы [min, max] и округлением до шага. */
    public void setClamped(double newValue) {
        double clamped = Math.max(min, Math.min(max, newValue));
        if (step > 0) {
            clamped = Math.round(clamped / step) * step;
        }
        setValue(clamped);
    }
}
