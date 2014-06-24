package florida.tony.hw4;

import android.graphics.RectF;

public class Shape {
	private RectF bounds = new RectF();
	private Type type;
	public static enum Type {
		Square, Circle;
	}
	public Shape(Type type) {
		this.type = type;
	}
	public RectF getBounds() {
		return bounds;
	}
	public Type getType() {
		return type;
	}
}
