package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;
import org.lwjgl.glfw.GLFW;

/**
 * Классический зум камеры (сужение FOV, пока модуль включён по хоткею) —
 * чисто визуальный эффект, аналогичный биноклям/подзорным трубам в других
 * модах. Не даёт информации, недоступной через обычное приближение экрана,
 * не меняет хитбоксы и не подсвечивает игроков/мобов.
 */
public class ZoomModule extends Module {

    private final NumberSetting zoomFactorSetting = new NumberSetting("Множитель", 4.0, 2.0, 10.0, 0.5);
    private double savedFov;

    public ZoomModule() {
        super("Zoom", ModuleCategory.VISUAL, "Сужает поле зрения, пока модуль включён (нажмите хоткей ещё раз, чтобы выключить).", GLFW.GLFW_KEY_C);
        addSetting(zoomFactorSetting);
    }

    @Override
    protected void onEnable() {
        savedFov = mc.options.getFov().getValue();
    }

    @Override
    public void onTick() {
        int targetFov = (int) Math.max(1, savedFov / zoomFactorSetting.get());
        mc.options.getFov().setValue(targetFov);
    }

    @Override
    protected void onDisable() {
        mc.options.getFov().setValue((int) savedFov);
    }
}
