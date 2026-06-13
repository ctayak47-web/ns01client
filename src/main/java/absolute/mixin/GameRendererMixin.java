package absolute.mixin;

import absolute.Client;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Миксин для GameRenderer, перехватывающий метод рендера мира.
 * Используется для вызова 3D-рендера модулей NS01.
 * Маппинги для Fabric 1.21.4.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    /**
     * Инжектируем наш код в начало метода renderWorld.
     * Это гарантирует, что наши 3D-эффекты будут отрисованы в мире,
     * до или после стандартных элементов, в зависимости от нужд.
     *
     * @param tickDelta Дельта времени для интерполяции.
     * @param limitTime Максимальное время для рендера.
     * @param matrixStack Стек матриц для трансформаций.
     * @param ci CallbackInfo для завершения инъекции.
     */
    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = false)
    private void ns01$onRenderWorld(float tickDelta, long limitTime, MatrixStack matrixStack, CallbackInfo ci) {
        // Вызываем наш глобальный метод рендера для всех 3D-модулей
        // Предполагается, что Client.modules.onRender3D() будет грамотно обрабатывать модули.
        if (Client.modules != null) {
            Client.modules.onRender3D(matrixStack, tickDelta);
        }
    }
}
        