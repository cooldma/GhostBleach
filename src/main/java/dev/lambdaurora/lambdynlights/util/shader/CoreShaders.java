package dev.lambdaurora.lambdynlights.util.shader;

import java.io.IOException;

import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class CoreShaders {
	
	private static final Shader COLOR_OVERLAY_SHADER;
	
	public static Shader getColorOverlayShader() {
		return COLOR_OVERLAY_SHADER;
	}
	
	static {
		try {
			COLOR_OVERLAY_SHADER = ShaderLoader.load(VertexFormats.POSITION_COLOR_TEXTURE, new Identifier("lambdynlights", "color_overlay"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to initilize LambDynLights core shaders", e);
		}
	}

}
