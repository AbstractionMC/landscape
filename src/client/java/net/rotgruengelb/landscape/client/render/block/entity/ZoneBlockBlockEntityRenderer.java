package net.rotgruengelb.landscape.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.rotgruengelb.landscape.block.entity.ZoneBlockBlockEntity;
import net.rotgruengelb.landscape.util.math.BlockZone;

@Environment(EnvType.CLIENT)
public class ZoneBlockBlockEntityRenderer implements BlockEntityRenderer<ZoneBlockBlockEntity> {
	public ZoneBlockBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	@Override
	public void render(ZoneBlockBlockEntity zoneBlockBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		if (!MinecraftClient.getInstance().player.isCreativeLevelTwoOp() && !MinecraftClient.getInstance().player.isSpectator()) {
			return;
		}
		if (zoneBlockBlockEntity.shouldShowZones() && !zoneBlockBlockEntity.getZones().isEmpty()) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
			for (BlockZone zone : zoneBlockBlockEntity.getZones(false)) {
				BlockPos pos1 = zone.pos1(true);
				BlockPos pos2 = zone.pos2(true);
				WorldRenderer.drawBox(matrixStack, vertexConsumer, pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), 1.0f, 1.0f, 0.0f, 1.0f);
			}
		}
	}

	@Override
	public boolean rendersOutsideBoundingBox(ZoneBlockBlockEntity structureBlockBlockEntity) {
		return true;
	}

	@Override
	public int getRenderDistance() {
		return 96;
	}
}
