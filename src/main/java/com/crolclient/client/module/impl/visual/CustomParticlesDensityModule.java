package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;
import net.minecraft.client.option.ParticlesOption;

/** Даёт более гибкий (не только 3 ступени) слайдер плотности частиц. */
public class CustomParticlesDensityModule extends Module {
    private final NumberSetting densitySetting = new NumberSetting("Плотность", 1.0, 0.0, 1.0, 0.05);

    public CustomParticlesDensityModule() {
        super("ParticlesDensity", ModuleCategory.VISUAL, "Точный слайдер плотности частиц (вместо 3 стандартных ступеней).");
        addSetting(densitySetting);
    }

    @Override
    protected void onEnable() {
        applyDensity();
    }

    @Override
    public void onTick() {
        applyDensity();
    }

    private void applyDensity() {
        double d = densitySetting.get();
        ParticlesOption option = d > 0.66 ? ParticlesOption.ALL : d > 0.33 ? ParticlesOption.DECREASED : ParticlesOption.MINIMAL;
        mc.options.getParticles().setValue(option);
    }
}
