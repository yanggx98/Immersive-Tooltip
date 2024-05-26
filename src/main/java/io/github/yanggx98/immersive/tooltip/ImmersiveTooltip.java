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
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
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
        ItemTooltipCallback.EVENT.register(new ItemTooltipCallback() {
            @Override
            public void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
                // Special component
                FoodComponent foodComponent = stack.getItem().getComponents().get(DataComponentTypes.FOOD);
                if (foodComponent != null){
                    Text hungerText = Text.translatable(identifier("tooltip.hunger").toTranslationKey(),foodComponent.nutrition());
                    Text saturationText = Text.translatable(identifier("tooltip.saturation").toTranslationKey(),(int) (foodComponent.saturation()*100))
                            .setStyle(Style.EMPTY.withFormatting(Formatting.AQUA)) .setStyle(Style.EMPTY.withFormatting(Formatting.AQUA));
                    lines.add(hungerText);
                    lines.add(saturationText);
                    for (FoodComponent.StatusEffectEntry entry: foodComponent.effects()) {
                        StatusEffectInstance statusEffect = entry.effect();
                        int c = statusEffect.getEffectType().value().getColor();
                        Text effectText = Text.empty().append("◈ ").append(Text.translatable(
                                        statusEffect.getTranslationKey()))
                                .append("(").append(StatusEffectUtil.getDurationText(statusEffect,1.0f,tooltipContext.getUpdateTickRate())).append(")")
                                .setStyle(Style.EMPTY.withColor(c));
                        lines.add(effectText);
                    }
                }
                if (stack.isDamageable()){
                    Text durabilityText = Text.translatable("item.durability",
                            stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage());
                    lines.add(durabilityText);
                }
            }
        });
        TooltipComponentAPI.EVENT.register((list, itemStack) -> {
            list.remove(0);
            list.add(0, new HeaderTooltipComponent(itemStack));
            // Background component
            Integer color = itemStack.getRarity().getFormatting().getColorValue();
            if (color == null) {
                color = 0xffffffff;
            }
            if (itemStack.getItem() instanceof ArmorItem) {
                list.add(new ModelViewerComponent(itemStack, 0xff000000 | color));
            } else {
                list.add(new ColorBorderComponent(0xff000000 | color));
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