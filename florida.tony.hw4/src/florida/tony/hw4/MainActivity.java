package florida.tony.hw4;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import florida.tony.hw4.Shape.Type;

/*
 * Code adapted from Scott Stanchfield
 */
public class MainActivity extends Activity {

	private static enum Mode {
		DrawCircle, DrawSquare, Select
	} // end Mode

	private Mode mode = Mode.Select;

	private Drawable square;
	private Drawable selectedSquare;
	private Drawable circle;
	private Drawable selectedCircle;
	private int numContainers = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		square = getResources().getDrawable(R.drawable.square);
		selectedSquare = getResources().getDrawable(R.drawable.selected_square);
		circle = getResources().getDrawable(R.drawable.circle);
		selectedCircle = getResources().getDrawable(R.drawable.selected_circle);

		ViewGroup main = (ViewGroup) findViewById(R.id.main);
		DrawingView drawingView = new DrawingView(this);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1);
		drawingView.setLayoutParams(params);
		main.addView(drawingView);

		Toast.makeText(getApplicationContext(),
				"Level " + numContainers + "\nClear all the shapes. Go!",
				Toast.LENGTH_LONG).show();
	} // end onCreate

	public void squarePressed(View view) {
		mode = Mode.DrawSquare;
	} // end squarePressed

	public void circlePressed(View view) {
		mode = Mode.DrawCircle;
	} // end circlePressed

	public void selectPressed(View view) {
		mode = Mode.Select;
	} // end circlePressed

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} // end onCreateOptionsMenu

	public class DrawingView extends View {

		private Shape selected;

		private float selectionOffsetX = 0;
		private float selectionOffsetY = 0;

		public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
			super(context, attrs, defStyleAttr);
			// TODO Auto-generated constructor stub
		} // end DrawingView

		public DrawingView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		} // end DrawingView

		public DrawingView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		} // end DrawingView

		private List<Shape> shapes = new ArrayList<Shape>();
		private List<Shape> containers = new ArrayList<Shape>();
		private Paint paint;
		private Rect rect = new Rect();

		private int canvasWidth = 0;
		private int canvasHeight = 0;

		private void init() {
			paint = new Paint();
			paint.setColor(Color.DKGRAY);

			drawContainers();
		} // end init

		/*
		 * Draw the containers on the screen based on the canvas width and
		 * height
		 */
		private void drawContainers() {
			/**
			 * Get shape size from dimens.xml http://stackoverflow.com/questions
			 * /2238883/what-is-the-correct-way
			 * -to-specify-dimensions-in-dip-from-java-code
			 */
			int shapeSize = (int) getResources().getDimension(
					R.dimen.shape_size);

			// draw random containers
			for (int i = 0; i < numContainers; i++) {
				// pick a random shape type
				Type type = (Math.random() < 0.5) ? Type.Circle : Type.Square;

				// get coordinates of where to put container
				int xCoord = 0 + (int) (Math.random() * ((canvasWidth - (shapeSize)) + 1));
				int yCoord = 0 + (int) (Math.random() * ((canvasHeight - (shapeSize)) + 1));
				Log.d("hw4", "hw4 i=" + i + " (" + xCoord + ", " + yCoord + ")");

				// draw shape
				Shape shape = new Shape(type);
				shape.getBounds().set(xCoord, yCoord, xCoord + shapeSize,
						yCoord + shapeSize);
				containers.add(shape);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			Shape shape = null;
			Drawable drawable = null;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d("hw4", "hw4 down");
				switch (mode) {
				case DrawCircle:
					Log.d("hw4", "hw4 circle");
					shape = new Shape(Type.Circle);
					drawable = circle;
					break;
				case DrawSquare:
					Log.d("hw4", "hw4 square");
					shape = new Shape(Type.Square);
					drawable = square;
					break;
				case Select:
					Log.d("hw4", "hw4 select");
					selected = findShapeAt(event.getX(), event.getY());

					if (selected != null) {

						selected.setSelected(true);

						RectF bounds = selected.getBounds();
						selectionOffsetX = event.getX() - bounds.left;
						selectionOffsetY = event.getY() - bounds.top;
					}
					break;
				} // end switch
			case MotionEvent.ACTION_MOVE:
				Log.d("hw4", "hw4 move");
				if (selected != null) {
					RectF bounds = selected.getBounds();
					float width = bounds.right - bounds.left;
					float height = bounds.bottom - bounds.top;
					bounds.set(event.getX() - selectionOffsetX, event.getY()
							- selectionOffsetY, event.getX() - selectionOffsetX
							+ width, event.getY() - selectionOffsetY + height);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				Log.d("hw4", "hw4 up");
				if (findContainerAt(event.getX(), event.getY())) {
					shapes.remove(findShapeAt(event.getX(), event.getY()));
				}

				// check if level is over
				checkDone();

				// clear selections
				clearSelections();

				break;
			} // end switch

			if (drawable != null) {
				shape.getBounds().set(
						event.getX() - drawable.getIntrinsicWidth() / 2,
						event.getY() - drawable.getIntrinsicHeight() / 2,
						event.getX() + drawable.getIntrinsicWidth() / 2,
						event.getY() + drawable.getIntrinsicHeight() / 2);
				shapes.add(shape);
				invalidate();
				return true;
			} // end if

			return true;
		} // end onTouchEvent

		private void clearSelections() {
			selected = null;

			// unselect all shapes
			for (Shape shape : shapes) {
				shape.setSelected(false);
			} // end for
		}

		private void checkDone() {
			if (containers.size() == 0) {
				// c shapes
				shapes.clear();

				// go to next level
				numContainers++;
				drawContainers();
				
				// display done message
				Toast.makeText(getApplicationContext(), "You win!\nMoving on to Level " + numContainers,
						Toast.LENGTH_LONG).show();
			}
		}

		private Shape findShapeAt(float x, float y) {
			for (int i = shapes.size() - 1; i >= 0; i--) {
				Shape shape = shapes.get(i);
				if (shape.getBounds().contains(x, y)) {
					return shape;
				}
			}
			return null;
		}

		private boolean findContainerAt(float x, float y) {
			for (int i = containers.size() - 1; i >= 0; i--) {
				Shape container = containers.get(i);

				// check for a null container
				if (container == null) {
					return false;
				}

				// check if shape is above container
				if (container.getBounds().contains(x, y)) {

					// check for a null selection
					if (selected == null) {
						return false;
					}

					// get opposite type of selection
					Type currentType = selected.getType();
					Type selectedType = null;
					if (currentType == Type.SelectedCircle) {
						selectedType = Type.Circle;
					} else if (currentType == Type.SelectedSquare) {
						selectedType = Type.Square;
					} else if (currentType == Type.Circle) {
						selectedType = Type.SelectedCircle;
					} else if (currentType == Type.Square) {
						selectedType = Type.SelectedSquare;
					}

					// check if shape is the same type as containers
					if (container.getType() == selectedType
							|| container.getType() == currentType) {
						// remove container if shape is above it
						containers.remove(container);
						invalidate();
						return true;
					}
				}
			}
			return false;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (paint == null) {
				// pass the canvas width and height to init
				canvasWidth = canvas.getWidth();
				canvasHeight = canvas.getHeight();
				init();
			}

			// draw containers
			for (Shape container : containers) {
				switch (container.getType()) {
				case Circle:
					container.getBounds().round(rect);
					circle.setBounds(rect);
					circle.draw(canvas);
					break;
				case Square:
					container.getBounds().round(rect);
					square.setBounds(rect);
					square.draw(canvas);
					break;
				default:
					break;
				} // end switch
			} // end for

			// draw shapes
			for (Shape shape : shapes) {
				switch (shape.getType()) {
				case Circle:
					shape.getBounds().round(rect);
					circle.setBounds(rect);
					circle.draw(canvas);
					break;
				case Square:
					shape.getBounds().round(rect);
					square.setBounds(rect);
					square.draw(canvas);
					break;
				case SelectedSquare:
					shape.getBounds().round(rect);
					selectedSquare.setBounds(rect);
					selectedSquare.draw(canvas);
					break;
				case SelectedCircle:
					shape.getBounds().round(rect);
					selectedCircle.setBounds(rect);
					selectedCircle.draw(canvas);
					break;
				default:
					break;
				} // end switch
			} // end for
		} // end onDraw
	} // end DrawingView
} // end MainActivity
