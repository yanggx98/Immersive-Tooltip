package io.github.yanggx98.immersive.tooltip;

import io.github.yanggx98.immersive.tooltip.api.ItemRarityNameProvider;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.yanggx98.immersive.tooltip.ImmersiveTooltip.MOD_ID;
import static io.github.yanggx98.immersive.tooltip.ImmersiveTooltip.identifier;


public class TooltipHelper {
    static final String ITEM_MARK_KEY = MOD_ID + ".mark.item";
    static final String FOOD_COMPONENT_MARK_KEY = MOD_ID + ".mark.food";

    static ItemRarityNameProvider rarityNameProvider = new DefaultRarityNameProvider();

    public static Text createItemMark(ItemStack stack) {
        return Text.translatable(ITEM_MARK_KEY, Registries.ITEM.getId(stack.getItem()).toString());
    }

    public static Text createRarityMark(ItemStack stack) {
        return rarityNameProvider.getRarityName(stack);
    }

    public static void setRarityNameProvider(ItemRarityNameProvider provider) {
        if (provider != null) {
            rarityNameProvider = provider;
        }
    }

    @Nullable
    public static Item asItem(Text text) {
        if (text.getContent() instanceof TranslatableTextContent content) {
            if (Objects.equals(content.getKey(), ITEM_MARK_KEY)) {
                return Registries.ITEM.get(Identifier.of(content.getArg(0).getString()));
            }
        }
        return null;
    }

    public static Text createFoodComponentMark(FoodComponent foodComponent) {
        return Text.literal(FOOD_COMPONENT_MARK_KEY)
                .append(Text.translatable(identifier("tooltip.hunger").toTranslationKey(),foodComponent.nutrition()))
                .append(Text.translatable(identifier("tooltip.saturation").toTranslationKey(),foodComponent.saturation()));
    }


    private static class DefaultRarityNameProvider implements ItemRarityNameProvider {

        @Override
        public Text getRarityName(ItemStack stack) {
            String markKey = MOD_ID + ".rarity." + stack.getRarity().name().toLowerCase();
            return Text.translatable(markKey).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
        }
    }
}
