package io.github.yanggx98.immersive.tooltip.component;

import io.github.yanggx98.kaleido.tooltip.components.ColorTooltipBorderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ModelTooltipComponent extends ColorTooltipBorderComponent {
    private static final float ROTATE_COEFFICIENT = 0.4f;
    private static float rotateValue = 0;
    private final Item item;
    private static final int ENTITY_SIZE = 30;
    private static final int SPACING = 12;

    public ModelTooltipComponent(Item item, int color) {
        super(color);
        this.item = item;
    }

    @Override
    public void drawBorder(DrawContext context, int x, int y, int width, int height, int z, int page) {
        super.drawBorder(context, x, y, width, height, z, page);
        if (page != 0) {
            return;
        }
        if (item instanceof ArmorItem armorItem) {
            rotateValue+=ROTATE_COEFFICIENT;
            if(rotateValue % 360 == 0){
                rotateValue = 0;
            }
            ArmorStandEntity entity = new ArmorStandEntity(EntityType.ARMOR_STAND, MinecraftClient.getInstance().world);
            entity.equipStack(armorItem.getSlotType(), item.getDefaultStack());
            int offset = ENTITY_SIZE + SPACING - 10;
            super.drawBorder(context, x - offset - 25, y, ENTITY_SIZE + 10, ENTITY_SIZE + 30 + 10, z, page);
            drawEntity(context, x - ENTITY_SIZE / 2 - SPACING - 10, y + ENTITY_SIZE + 30 + 5, ENTITY_SIZE, rotateValue, -45, entity);
        }
    }


    public static void drawEntity(DrawContext context, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float) Math.atan((double) (mouseX / 40.0F));
        float g = (float) Math.atan((double) (mouseY / 40.0F));
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(g * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        entity.bodyYaw = mouseX;
        entity.setYaw(180.0F + f * 40.0F);
        entity.setPitch(-g * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        drawEntity(context, x, y, size, quaternionf, quaternionf2, entity);
    }

    public static void drawEntity(DrawContext context, int x, int y, int size, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, Entity entity) {
        context.getMatrices().push();
        context.getMatrices().translate((double) x, (double) y, 450);
        context.getMatrices().multiplyPositionMatrix((new Matrix4f()).scaling((float) size, (float) size, (float) (-size)));
        context.getMatrices().multiply(quaternionf);
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            quaternionf2.conjugate();
            entityRenderDispatcher.setRotation(quaternionf2);
        }

        entityRenderDispatcher.setRenderShadows(false);
//        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, context.getMatrices(), context.getVertexConsumers(), 15728880);
//        });
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

}
