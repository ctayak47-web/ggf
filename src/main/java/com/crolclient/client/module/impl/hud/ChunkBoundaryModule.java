package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

/**
 * Флаг-переключатель для отрисовки границ текущего чанка (аналог debug
 * F3+G) — читается миксином WorldRenderer, чисто информационная сетка.
 */
public class ChunkBoundaryModule extends Module {
    public ChunkBoundaryModule() {
        super("ChunkBoundary", ModuleCategory.HUD, "Отображает границы текущего чанка (аналог F3+G).");
    }
}
