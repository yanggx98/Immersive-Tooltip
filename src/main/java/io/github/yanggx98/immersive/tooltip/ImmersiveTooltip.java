package io.github.yanggx98.immersive.tooltip;

import com.mojang.datafixers.util.Pair;
import io.github.yanggx98.immersive.tooltip.component.BaseTooltipComponent;
import io.github.yanggx98.immersive.tooltip.component.ColorBorderComponent;
import io.github.yanggx98.immersive.tooltip.component.HeaderTooltipComponent;
import io.github.yanggx98.immersive.tooltip.component.ModelViewerComponent;
import io.github.yanggx98.kaleido.render.tooltip.api.TooltipComparatorProvider;
import io.github.yanggx98.kaleido.render.tooltip.api.TooltipComponentAPI;
import io.github.yanggx98.kaleido.render.tooltip.api.TooltipDrawerProvider;
import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;


public class ImmersiveTooltip implements ClientModInitializer {

    public static final String MOD_ID = "immersive-tooltip";

    @Override
    public void onInitializeClient() {
        TooltipComparatorProvider.setComparator(Comparator.comparingInt(ImmersiveTooltip::getSerialNumber));
        TooltipComponentAPI.EVENT.register((list, itemStack) -> {
            list.remove(0);
            list.add(0, new HeaderTooltipComponent(itemStack));
            // Background component
            Integer color = itemStack.getRarity().formatting.getColorValue();
            if (color == null) {
                color = 0xffffffff;
            }
            if (itemStack.getItem() instanceof ArmorItem) {
                list.add(new ModelViewerComponent(itemStack, 0xff000000 | color));
            } else {
                list.add(new ColorBorderComponent(0xff000000 | color));
            }
            // Special component
            FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
            if (foodComponent != null){
                Text hungerText = Text.translatable(identifier("tooltip.hunger").toTranslationKey(),foodComponent.getHunger());
                Text saturationText = Text.translatable(identifier("tooltip.saturation").toTranslationKey(),(int) (foodComponent.getSaturationModifier()*100))
                        .setStyle(Style.EMPTY.withFormatting(Formatting.AQUA)) .setStyle(Style.EMPTY.withFormatting(Formatting.AQUA));

                list.add(TooltipComponent.of(hungerText.asOrderedText()));
                list.add(TooltipComponent.of(saturationText.asOrderedText()));
                for (Pair<StatusEffectInstance, Float> statusEffect : foodComponent.getStatusEffects()) {
                    int c = statusEffect.getFirst().getEffectType().getColor();
                    Text effectText = Text.empty().append("◈ ").append(Text.translatable(
                                    statusEffect.getFirst().getTranslationKey()))
                            .append("(").append(StatusEffectUtil.getDurationText(statusEffect.getFirst(),1.0f)).append(")")
                            .setStyle(Style.EMPTY.withColor(c));
                    list.add(TooltipComponent.of(effectText.asOrderedText()));
                }
            }
            if (itemStack.isDamageable()){
                Text durabilityText = Text.translatable("item.durability",
                        itemStack.getMaxDamage() - itemStack.getDamage(), itemStack.getMaxDamage());
                list.add(TooltipComponent.of(durabilityText.asOrderedText()));
            }
        });
        TooltipDrawerProvider.setTooltipDrawerProvider(new ImmersiveTooltipDrawer());
    }

    public static Identifier identifier(String path) {
        return new Identifier(MOD_ID, path);
    }


    private static int getSerialNumber(TooltipComponent component) {
        if (component instanceof BaseTooltipComponent) {
            return 0;
        } else {
            return 1;
        }
    }
}