package dev.lambdaurora.lambdynlights.shadow.nightconfig;

import java.io.IOException;

import dev.lambdaurora.lambdynlights.eventbus.Subscribe;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerSlider;
import dev.lambdaurora.lambdynlights.event.events.EventRenderBlockOutline;
import dev.lambdaurora.lambdynlights.event.events.EventWorldRender;
import dev.lambdaurora.lambdynlights.shadow.NightConfig;
import dev.lambdaurora.lambdynlights.shadow.NightConfigCategory;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerColor;
import dev.lambdaurora.lambdynlights.api.item.DynamicLightsInitializerMode;
import dev.lambdaurora.lambdynlights.util.render.Renderer;
import dev.lambdaurora.lambdynlights.util.render.color.QuadColor;
import dev.lambdaurora.lambdynlights.util.shader.CoreShaders;
import dev.lambdaurora.lambdynlights.util.shader.ColorVertexConsumerProvider;
import dev.lambdaurora.lambdynlights.util.shader.ShaderEffectWrapper;

import com.google.gson.JsonSyntaxException;

import dev.lambdaurora.lambdynlights.util.shader.ShaderLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class ConfigSpecDumbSupplier extends NightConfig {

	private ShaderEffectWrapper shader;
	private ColorVertexConsumerProvider colorVertexer;

	public ConfigSpecDumbSupplier() {
		super("BlockHighlight", KEY_UNBOUND, NightConfigCategory.RENDER, "Highlights blocks that you're looking at.",
				new DynamicLightsInitializerMode("Render", "Shader", "Box").withDesc("The Render mode."),
				new DynamicLightsInitializerSlider("ShaderFill", 1, 255, 50, 0).withDesc("How opaque the fill on shader mode should be."),
				new DynamicLightsInitializerSlider("Box", 0, 5, 2, 1).withDesc("How thick the box outline should be."),
				new DynamicLightsInitializerSlider("BoxFill", 0, 255, 50, 0).withDesc("How opaque the fill on box mode should be."),
				new DynamicLightsInitializerColor("Color", 0, 128, 128).withDesc("The color of the highlight."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		try {
			shader = new ShaderEffectWrapper(
					ShaderLoader.loadEffect(mc.getFramebuffer(), new Identifier("lambdynlights", "shaders/post/entity_outline.json")));

			colorVertexer = new ColorVertexConsumerProvider(shader.getFramebuffer("main"), CoreShaders::getColorOverlayShader);
		} catch (JsonSyntaxException | IOException e) {
			e.printStackTrace();
			setEnabled(false);
		}
	}

	@Subscribe
	public void onRenderBlockOutline(EventRenderBlockOutline event) {
		event.setCancelled(true);
	}

	@Subscribe
	public void onWorldRender(EventWorldRender.Post event) {
		int mode = getSetting(0).asMode().getMode();

		if (!(mc.crosshairTarget instanceof BlockHitResult))
			return;

		BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
		BlockState state = mc.world.getBlockState(pos);

		if (state.getMaterial() == Material.AIR || !mc.world.getWorldBorder().contains(pos))
			return;

		int[] color = this.getSetting(4).asColor().getRGBArray();
		if (mode == 0) {
			shader.prepare();
			shader.clearFramebuffer("main");

			Vec3d offset = state.getModelOffset(mc.world, pos);
			MatrixStack matrices = Renderer.matrixFrom(pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z);

			BlockEntity be = mc.world.getBlockEntity(pos);
			BlockEntityRenderer<BlockEntity> renderer = be != null ? mc.getBlockEntityRenderDispatcher().get(be) : null;
			if (renderer != null) {
				renderer.render(be, mc.getTickDelta(), matrices,
						colorVertexer.createSingleProvider(mc.getBufferBuilders().getEntityVertexConsumers(), color[0], color[1], color[2], getSetting(1).asSlider().getValueInt()),
						LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
			} else {
				mc.getBlockRenderManager().getModelRenderer().renderFlat(
						mc.world, mc.getBlockRenderManager().getModel(state), state, pos, matrices,
						colorVertexer.createSingleProvider(mc.getBufferBuilders().getEntityVertexConsumers(), color[0], color[1], color[2], getSetting(1).asSlider().getValueInt()).getBuffer(RenderLayers.getMovingBlockLayer(state)),
						false, Random.create(0), 0L, OverlayTexture.DEFAULT_UV);
			}

			colorVertexer.draw();
			shader.render();
			shader.drawFramebufferToMain("main");
		} else {
			Box box = state.getOutlineShape(mc.world, pos).getBoundingBox().offset(pos);
			float width = getSetting(2).asSlider().getValueFloat();
			int fill = getSetting(3).asSlider().getValueInt();

			if (width != 0)
				Renderer.drawBoxOutline(box, QuadColor.single(color[0], color[1], color[2], 255), width);

			if (fill != 0)
				Renderer.drawBoxFill(box, QuadColor.single(color[0], color[1], color[2], fill));
		}
	}
}