package io.github.yanggx98.immersive.tooltip.api;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public interface ItemDisplayNameProvider {
    Text getDisplayName(ItemStack stack);
}
