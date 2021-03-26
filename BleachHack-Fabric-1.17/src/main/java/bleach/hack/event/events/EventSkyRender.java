package bleach.hack.event.events;

import bleach.hack.event.Event;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.util.math.Vec3d;

public class EventSkyRender extends Event {
	
	public static class Properties extends EventSkyRender {
		
		private SkyProperties sky;
		
		public Properties(SkyProperties sky) {
			this.setSky(sky);
		}

		public SkyProperties getSky() {
			return sky;
		}

		public void setSky(SkyProperties sky) {
			this.sky = sky;
		}
	}

	public static class Color extends EventSkyRender {
	
		private float tickDelta;
		private Vec3d color = null;
	
		public Color(float tickDelta) {
			this.tickDelta = tickDelta;
		}
	
		public float getTickDelta() {
			return tickDelta;
		}
	
		public void setColor(float[] color) {
			this.color = new Vec3d(color[0], color[1], color[2]);
		}
	
		public Vec3d getColor() {
			return color;
		}
	
		public static class SkyColor extends Color {
	
			public SkyColor(float tickDelta) {
				super(tickDelta);
			}
		}
	
		public static class CloudColor extends Color {
	
			public CloudColor(float tickDelta) {
				super(tickDelta);
			}
		}
	
		public static class FogColor extends Color {
	
			public FogColor(float tickDelta) {
				super(tickDelta);
			}
		}
		
		public static class EndSkyColor extends Color {
			
			public EndSkyColor(float tickDelta) {
				super(tickDelta);
			}
		}
	}
}
