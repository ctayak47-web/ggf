package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.notification.Toast;
import com.crolclient.client.notification.ToastManager;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

/** Предупреждает уведомлением, если игрок приближается вплотную к кактусу (только оповещение). */
public class AntiCactusModule extends Module {
    private boolean warned = false;

    public AntiCactusModule() {
        super("AntiCactus", ModuleCategory.UTILITY, "Уведомляет, если игрок вплотную приближается к кактусу.");
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        BlockPos pos = mc.player.getBlockPos();
        boolean nearCactus = false;
        for (BlockPos neighbor : BlockPos.iterateOutwards(pos, 1, 1, 1)) {
            if (mc.world.getBlockState(neighbor).isOf(Blocks.CACTUS)) {
                nearCactus = true;
                break;
            }
        }
        if (nearCactus && !warned) {
            ToastManager.getInstance().push("AntiCactus", "Рядом кактус!", Toast.Type.WARNING);
            warned = true;
        } else if (!nearCactus) {
            warned = false;
        }
    }
}
