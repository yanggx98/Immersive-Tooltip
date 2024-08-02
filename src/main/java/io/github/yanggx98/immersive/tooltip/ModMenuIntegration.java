package io.github.yanggx98.immersive.tooltip;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.text.Text;

import static io.github.yanggx98.immersive.tooltip.ImmersiveTooltip.option;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Text.of("Used for narration. Could be used to render a title in the future."))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Rendering Options"))
                        .option(option)
                        .build())
                .build().generateScreen(parentScreen);
    }
}