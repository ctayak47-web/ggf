package com.crolclient.client.mixin;

import com.crolclient.client.module.ModuleManager;
import com.crolclient.client.module.impl.utility.ChatFilterModule;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Подсвечивает входящие сообщения чата, содержащие ключевые слова из
 * ChatFilterModule. Сообщение НЕ скрывается и НЕ модифицируется по
 * содержанию — добавляется только цветовой префикс-маркер для видимости.
 */
@Mixin(ChatHud.class)
public abstract class MixinChatHud {

    // В 1.21.4 публичный метод addMessage(Text) остался как overload;
    // он вызывает внутренний addMessage с доп. параметрами.
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void crolclient$onAddMessage(Text message, CallbackInfo ci) {
        var module = ModuleManager.getInstance().getByName("ChatFilter");
        if (module instanceof ChatFilterModule filter && filter.isEnabled()) {
            if (filter.matches(message.getString())) {
                // Подсветка реализуется через параллельное уведомление в системе тостов
                // CrolClient (см. ToastManager), не изменяя сам объект Text сообщения —
                // это безопаснее, чем мутация текста чата на лету.
                com.crolclient.client.notification.ToastManager.getInstance().push(
                        "ChatFilter", "Совпадение по ключевому слову", com.crolclient.client.notification.Toast.Type.INFO);
            }
        }
    }
}
