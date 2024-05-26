package io.github.yanggx98.immersive.tooltip;

import io.github.yanggx98.immersive.tooltip.component.BaseTooltipComponent;
import io.github.yanggx98.immersive.tooltip.component.ColorBorderComponent;
import io.github.yanggx98.immersive.tooltip.component.HeaderTooltipComponent;
import io.github.yanggx98.immersive.tooltip.component.ModelViewerComponent;
import io.github.yanggx98.kaleido.render.tooltip.api.TooltipComparatorProvider;
import io.github.yanggx98.kaleido.render.tooltip.api.TooltipComponentAPI;
import io.github.yanggx98.kaleido.render.tooltip.api.TooltipDrawerProvider;
import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;


public class ImmersiveTooltip implements ClientModInitializer {

    public static final String MOD_ID = "immersive-tooltip";

    @Override
    public void onInitializeClient() {
        TooltipComparatorProvider.setComparator(Comparator.comparingInt(ImmersiveTooltip::getSerialNumber));
        TooltipComponentAPI.EVENT.register((list, itemStack) -> {
            Integer color = itemStack.getRarity().formatting.getColorValue();
            if (color == null) {
                color = 0xffffffff;
            }
            if (itemStack.getItem() instanceof ArmorItem) {
                list.add(new ModelViewerComponent(itemStack, 0xff000000 | color));
            } else {
                list.add(new ColorBorderComponent(0xff000000 | color));
            }
            list.remove(0);
            list.add(0, new HeaderTooltipComponent(itemStack));
            for (int i = 0;i<1;i++)
            {
                Text text = Text.empty().append("xxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweuixxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweuixxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweuixxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweuixxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweuixxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweuixxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweuixxxxxxxxxxxkashudiqwyeoquwoeiuqweoqweui");
                list.add(TooltipComponent.of(text.asOrderedText()));
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