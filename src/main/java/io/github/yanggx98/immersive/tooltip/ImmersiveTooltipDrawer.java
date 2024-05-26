package io.github.yanggx98.immersive.tooltip;

import io.github.yanggx98.immersive.tooltip.component.TooltipBackgroundComponent;
import io.github.yanggx98.kaleido.render.tooltip.api.TooltipDrawerProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;

public class ImmersiveTooltipDrawer implements TooltipDrawerProvider.ITooltipDrawer {
    private static final int EDGE_SPACING = 32;
    private static final int PAGE_SPACING = 12;

    private static int getLimitMaxHeight() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight() - EDGE_SPACING * 2;
    }

    @Override
    public void drawTooltip(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {
        MatrixStack matrices = context.getMatrices();
        List<TooltipPage> pageList = new ArrayList<>();
        TooltipBackgroundComponent backgroundComponent = getBackgroundComponent(components);
        if (components.isEmpty()) {
            return;
        }
        if (backgroundComponent != null) {
            components.remove(backgroundComponent);
        }

        int pageWidth = 0;
        int pageHeight = -2;
        int maxHeight = getLimitMaxHeight();
        int totalWidth = 0;

        // 如果有两个以上的项目则需要给第一个空出空间
        int spacing = components.size() > 1 ? 4 : 0;
        pageHeight += spacing;


        TooltipPage page = new TooltipPage();

        for (int j = 0; j < components.size(); j++) {
            TooltipComponent tooltipComponent = components.get(j);
            int width = tooltipComponent.getWidth(textRenderer);
            if (width > pageWidth) {
                pageWidth = width;
            }
            pageHeight += tooltipComponent.getHeight();
            if (pageHeight > maxHeight) {
                pageList.add(page);
                totalWidth += page.width;
                page = new TooltipPage();
                page.components.add(tooltipComponent);
                page.height = tooltipComponent.getHeight();
//                pageWidth = tooltipComponent.getWidth(textRenderer);
                pageHeight = tooltipComponent.getHeight();
                if (j == components.size() - 1) {
                    page.width = tooltipComponent.getWidth(textRenderer);
                    pageList.add(page);
                    totalWidth += page.width;
                }
            }else if (j == components.size() - 1) {
                page.height = pageHeight;
                page.width = pageWidth;
                page.components.add(tooltipComponent);
                pageList.add(page);
                totalWidth += page.width;
            } else {
                page.height = pageHeight;
                page.width = pageWidth;
                page.components.add(tooltipComponent);
            }
        }

        Vector2ic vector2ic = positioner.getPosition(context.getScaledWindowWidth(), context.getScaledWindowHeight(), x, y, totalWidth, pageList.get(0).height);
        int n = vector2ic.x();
        int o = vector2ic.y();
        for (TooltipPage tooltipPage : pageList) {
            tooltipPage.x = n;
            // 6 是距离底部的补偿
            tooltipPage.y = (pageList.size() > 1) ? o - EDGE_SPACING : o - 6;
            n += tooltipPage.width + PAGE_SPACING;
        }

        matrices.push();
        // render background component
        for (TooltipPage p : pageList) {
            if (backgroundComponent == null) {
                context.draw(() -> TooltipBackgroundRenderer.render(context, p.x, p.y, p.width, p.height, 400));
            } else {
                TooltipBackgroundComponent finalBorderComponent = backgroundComponent;
                context.draw(() -> finalBorderComponent.render(context, p.x, p.y, p.width, p.height, 400, pageList.indexOf(p)))
                ;
            }
        }
        // render component
        matrices.translate(0.0f, 0.0f, 400.0f);
        for (TooltipPage p : pageList) {
            int cx = p.x;
            int cy = p.y;
            for (TooltipComponent component : p.components) {
                component.drawText(textRenderer, cx, cy, matrices.peek().getPositionMatrix(), context.getVertexConsumers());
                component.drawItems(textRenderer, cx, cy, context);
                cy += component.getHeight();
                if (p == pageList.get(0) && component == p.components.get(0)) {
                    cy += spacing;
                }
            }
        }
        matrices.pop();
    }

    @Nullable
    private TooltipBackgroundComponent getBackgroundComponent(List<TooltipComponent> components) {
        for (TooltipComponent component : components) {
            if (component instanceof TooltipBackgroundComponent) {
                return (TooltipBackgroundComponent) component;
            }
        }
        return null;
    }

    private static class TooltipPage {
        private int x;
        private int y;
        private int width;
        private int height;
        private List<TooltipComponent> components;

        private TooltipPage() {
            this(0, 0, 0, 0, new ArrayList<>());
        }

        private TooltipPage(int x, int y, int width, int height, List<TooltipComponent> components) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.components = components;
        }
    }
}
