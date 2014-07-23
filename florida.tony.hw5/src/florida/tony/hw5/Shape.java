package florida.tony.hw5;

import android.R.integer;
import android.graphics.RectF;
import android.util.Log;

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
		bounds.set(bounds.left, bounds.top + pixels, bounds.right, bounds.bottom + pixels);
	}
	
	public void move(float x)
	{
		x*=10;
		bounds.set(bounds.left - x, bounds.top, bounds.right - x, bounds.bottom);
	}

	public Type getType() {
		return type;
	}

}
