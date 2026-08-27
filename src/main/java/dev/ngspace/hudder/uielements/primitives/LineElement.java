package dev.ngspace.hudder.uielements.primitives;

import org.joml.Matrix3x2fStack;

import dev.ngspace.hudder.main.HudderRenderer;
import dev.ngspace.hudder.uielements.AUIElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class LineElement extends AUIElement {
	
	private final float thickness;
	private final float x0;
	private final float y0;
	private final float x1;
	private final float y1;
	private final int color;
	
	public LineElement(float x0, float y0, float x1, float y1, int color, int thickness) {
		this.thickness = thickness;
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.color = color;
	}

	@Override
	public void renderElement(GuiGraphicsExtractor graphics, HudderRenderer renderer, DeltaTracker delta) {
		if (thickness==0)
			return;
		float gap_from_center = (thickness/2f);
		float dx = x1 - x0;
		float dy = y1 - y0;

		float length = (float) Math.sqrt(dx*dx + dy*dy);

		float offsetX = -dy / length * gap_from_center;
		float offsetY = dx / length * gap_from_center;
		renderer.renderWithVertexConsumer(graphics, vc->{
			Matrix3x2fStack matrix = graphics.pose();
		    vc.addVertexWith2DPose(matrix, x0 + offsetX, y0 + offsetY).setColor(color).setUv(0, 0);
		    vc.addVertexWith2DPose(matrix, x1 + offsetX, y1 + offsetY).setColor(color).setUv(0, 0);
		    vc.addVertexWith2DPose(matrix, x1 - offsetX, y1 - offsetY).setColor(color).setUv(0, 0);
		    vc.addVertexWith2DPose(matrix, x0 - offsetX, y0 - offsetY).setColor(color).setUv(0, 0);
		});
	}
}
