package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;
import net.minecraft.client.option.GameOptions;

/**
 * Визуально повышает гамму (яркость) вместо переопределения освещения мира —
 * это тот же самый механизм, что и стандартный ползунок "Яркость" в настройках
 * игры, только доступный по хоткею. Не даёт видимости сквозь блоки и не
 * раскрывает информацию, недоступную обычному игроку с максимальной яркостью.
 */
public class FullBrightModule extends Module {

    private final NumberSetting gammaSetting = new NumberSetting("Яркость", 16.0, 1.0, 16.0, 1.0);
    private double savedGamma;

    public FullBrightModule() {
        super("FullBright", ModuleCategory.VISUAL, "Устанавливает максимальную игровую гамму (аналог ползунка яркости).");
        addSetting(gammaSetting);
    }

    @Override
    protected void onEnable() {
        GameOptions options = mc.options;
        if (options != null) {
            savedGamma = options.getGamma().getValue();
            options.getGamma().setValue(gammaSetting.get());
        }
    }

    @Override
    protected void onDisable() {
        GameOptions options = mc.options;
        if (options != null) {
            options.getGamma().setValue(savedGamma);
        }
    }
}
