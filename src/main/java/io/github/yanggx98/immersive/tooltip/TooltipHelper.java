package io.github.yanggx98.immersive.tooltip;

import io.github.yanggx98.immersive.tooltip.api.ItemBorderColorProvider;
import io.github.yanggx98.immersive.tooltip.api.ItemDisplayNameProvider;
import io.github.yanggx98.immersive.tooltip.api.ItemRarityNameProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static io.github.yanggx98.immersive.tooltip.ImmersiveTooltip.MOD_ID;


public class TooltipHelper {
    static final String ITEM_MARK_KEY = MOD_ID + ".mark.item";
    static final String FOOD_COMPONENT_MARK_KEY = MOD_ID + ".mark.food";

    static ItemRarityNameProvider rarityNameProvider = new DefaultItemRarityNameProvider();
    static ItemDisplayNameProvider displayNameProvider = new DefaultItemDisplayNameProvider();
    static ItemBorderColorProvider borderColorProvider = new DefaultItemBorderColorProvider();

    public static Text getRarityName(ItemStack stack) {
        return rarityNameProvider.getRarityName(stack);
    }

    public static Text getDisplayName(ItemStack stack) {
        return displayNameProvider.getDisplayName(stack);
    }

    public static void setRarityNameProvider(ItemRarityNameProvider provider) {
        if (provider != null) {
            rarityNameProvider = provider;
        }
    }

    public static void setDisplayNameProvider(ItemDisplayNameProvider provider) {
        if (provider != null) {
            displayNameProvider = provider;
        }
    }
    public static void setBorderColorProvider(ItemBorderColorProvider provider) {
        if (provider != null) {
            borderColorProvider = provider;
        }
    }

    private static class DefaultItemRarityNameProvider implements ItemRarityNameProvider {

        @Override
        public Text getRarityName(ItemStack stack) {
            String markKey = MOD_ID + ".rarity." + stack.getRarity().name().toLowerCase();
            return Text.translatable(markKey).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
        }
    }

    private static class DefaultItemDisplayNameProvider implements ItemDisplayNameProvider {

        @Override
        public Text getDisplayName(ItemStack stack) {
            return Text.empty().append(stack.getName()).formatted(stack.getRarity().formatting);
        }
    }

    private static class DefaultItemBorderColorProvider implements ItemBorderColorProvider {
        @Override
        public int getItemBorderColor(ItemStack itemStack) {
            Integer color = itemStack.getRarity().formatting.getColorValue();
            if (color == null) {
                color = 0xffffffff;
            }
            return color;
        }
    }
}
