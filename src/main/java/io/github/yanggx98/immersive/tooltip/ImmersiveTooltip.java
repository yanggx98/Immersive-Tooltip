package io.github.yanggx98.immersive.tooltip;

import com.mojang.datafixers.util.Pair;
import io.github.yanggx98.immersive.tooltip.component.HeaderTooltipComponent;
import io.github.yanggx98.immersive.tooltip.component.ModelTooltipComponent;
import io.github.yanggx98.kaleido.tooltip.api.ITooltipBorderComponentProviderCallback;
import io.github.yanggx98.kaleido.tooltip.api.ITooltipComponentProviderCallback;
import io.github.yanggx98.kaleido.tooltip.components.KaleidoTooltipComponent;
import io.github.yanggx98.kaleido.tooltip.components.TooltipComparatorProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

import static io.github.yanggx98.immersive.tooltip.TooltipHelper.asItem;
import static io.github.yanggx98.immersive.tooltip.TooltipHelper.createRarityMark;

public class ImmersiveTooltip implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "immersive-tooltip";
	private static int getSerialNumber(TooltipComponent component) {
		if (component instanceof KaleidoTooltipComponent) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register(new ItemTooltipCallback() {
			@Override
			public void getTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
				if (lines.get(0) instanceof MutableText mutableText) {
					mutableText.append(TooltipHelper.createItemMark(stack)).append(createRarityMark(stack));
				}
				FoodComponent foodComponent = stack.getItem().getFoodComponent();
				if (foodComponent != null) {
					lines.add(
							Text.translatable(identifier("tooltip.hunger").toTranslationKey(),foodComponent.getHunger())
									.setStyle(Style.EMPTY.withFormatting(Formatting.AQUA))
					);
					lines.add(Text.translatable(identifier("tooltip.saturation").toTranslationKey(),(int) (foodComponent.getSaturationModifier()*100))
							.setStyle(Style.EMPTY.withFormatting(Formatting.AQUA))
					);
					for (Pair<StatusEffectInstance, Float> statusEffect : foodComponent.getStatusEffects()) {
						int color = statusEffect.getFirst().getEffectType().getColor();
						lines.add(Text.empty().append("◈ ").append(Text.translatable(
										statusEffect.getFirst().getTranslationKey()))
								.append("(").append(StatusEffectUtil.getDurationText(statusEffect.getFirst(),1.0f)).append(")")
								.setStyle(Style.EMPTY.withColor(color))
						);
					}
				}
				if (stack.isDamageable()){
					lines.add(Text.translatable("item.durability", new Object[]{stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()}));
				}
			}
		});


		TooltipComparatorProvider.setComparator(Comparator.comparingInt(ImmersiveTooltip::getSerialNumber));

		ITooltipComponentProviderCallback.EVENT.register(new ITooltipComponentProviderCallback() {
			@Override
			public TooltipComponent getTooltipComponent(Text text, int i) {
				if (text instanceof MutableText mutableText) {
					for (Text childText : mutableText.getSiblings()) {
						Item item = asItem(childText);
						if (item != null) {
							OrderedText name = mutableText.getSiblings().get(0).copy().setStyle((mutableText.getStyle())).asOrderedText();
							OrderedText rarity = mutableText.getSiblings().get(2).asOrderedText();
							return new HeaderTooltipComponent(item, name, rarity);
						}
					}
				}
				return TooltipComponent.of(text.asOrderedText());
			}
		});
		ITooltipBorderComponentProviderCallback.EVENT.register(list -> {
			for (Text text : list) {
				if (text instanceof MutableText mutableText) {
					for (Text childText : mutableText.getSiblings()) {
						Item item = asItem(childText);
						if (item != null) {
							Integer colorValue = item.getDefaultStack().getRarity().formatting.getColorValue();
							int color = colorValue != null ? colorValue : -1;
							return new ModelTooltipComponent(item, 0xff000000 | color);
						}
					}
				}
			}
			return null;
		});
	}

	private static String secondFormatting(int duration){
		if (duration<=0){
			return "00:00";
		}
		int m = duration/60;
		int s = duration%60;
		return String.format("%02d:%02d", m,s);
	}

	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}
}