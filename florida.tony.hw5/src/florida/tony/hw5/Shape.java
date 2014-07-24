package florida.tony.hw5;

import android.graphics.RectF;

/*
 * Code adapted from Scott Stanchfield
 */
public class Shape {
	private RectF bounds = new RectF();
	private Type type;

	public static enum Type {
		Square, Circle, SquareHole, CircleHole;
	}

	public Shape(Type type) {
		this.type = type;
	}

	public RectF getBounds() {
		return bounds;
	}

	public void moveDown(int pixels) {
		bounds.set(bounds.left, bounds.top + pixels, bounds.right,
				bounds.bottom + pixels);
	}

	public void move(float x, float y) {
		//move left right
		x = x * 10;
		
		//y value controls speed
		x *= Math.abs(y/5);
		
		bounds.set(bounds.left - x, bounds.top, bounds.right - x, bounds.bottom);
	}

	public Type getType() {
		return type;
	}

}
