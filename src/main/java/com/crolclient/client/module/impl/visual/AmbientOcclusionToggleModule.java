package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import net.minecraft.client.option.AoOption;

/** Быстрый переключатель встроенной опции сглаженного освещения (Ambient Occlusion). */
public class AmbientOcclusionToggleModule extends Module {
    private AoOption savedValue;

    public AmbientOcclusionToggleModule() {
        super("AmbientOcclusion", ModuleCategory.VISUAL, "Быстро отключает/включает сглаженное освещение блоков.");
    }

    @Override
    protected void onEnable() {
        savedValue = mc.options.getAo().getValue();
        mc.options.getAo().setValue(AoOption.OFF);
    }

    @Override
    protected void onDisable() {
        if (savedValue != null) {
            mc.options.getAo().setValue(savedValue);
        }
    }
}
