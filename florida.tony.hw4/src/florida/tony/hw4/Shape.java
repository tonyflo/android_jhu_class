package florida.tony.hw4;

import android.graphics.RectF;

/*
 * Code adapted from Scott Stanchfield
 */
public class Shape {
	private RectF bounds = new RectF();
	private Type type;

	public static enum Type {
		Square, Circle, SelectedSquare, SelectedCircle, SquareHole, CircleHole, SelectedSquareHole, SelectedCircleHole;
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

	public void setShapeSelected(boolean selected) {
		if (selected) {
			// selected
			if (this.type == Type.Square) {
				this.type = Type.SelectedSquare;
			} else if (this.type == Type.Circle) {
				this.type = Type.SelectedCircle;
			}
		} else {
			// unselected
			if (this.type == Type.SelectedSquare) {
				this.type = Type.Square;
			} else if (this.type == Type.SelectedCircle) {
				this.type = Type.Circle;
			}
		}
	}
	
	public void setContainerSelected(boolean selected) {
		if (selected) {
			// selected
			if (this.type == Type.SquareHole) {
				this.type = Type.SelectedSquareHole;
			} else if (this.type == Type.CircleHole) {
				this.type = Type.SelectedCircleHole;
			}
		} else {
			// unselected
			if (this.type == Type.SelectedSquareHole) {
				this.type = Type.SquareHole;
			} else if (this.type == Type.SelectedCircleHole) {
				this.type = Type.CircleHole;
			}
		}
	}
}
