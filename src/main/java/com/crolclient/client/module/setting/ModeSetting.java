package com.crolclient.client.module.setting;

/** Настройка типа "режим" — выбор одного значения из списка строк. */
public class ModeSetting extends Setting<String> {

    private final String[] options;

    public ModeSetting(String name, String defaultValue, String... options) {
        super(name, defaultValue);
        this.options = options;
    }

    public String[] getOptions() {
        return options;
    }

    /** Переключает на следующий вариант по кругу — используется по клику в ClickGUI. */
    public void cycleNext() {
        int idx = indexOf(value);
        int next = (idx + 1) % options.length;
        setValue(options[next]);
    }

    private int indexOf(String v) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(v)) return i;
        }
        return 0;
    }
}
