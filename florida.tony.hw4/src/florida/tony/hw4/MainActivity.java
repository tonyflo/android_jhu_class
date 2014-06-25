package florida.tony.hw4;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.TextView;
import android.widget.Toast;
import florida.tony.hw4.Shape.Type;

/*
 * Code adapted from Scott Stanchfield
 */
public class MainActivity extends Activity {

	private Drawable square;
	private Drawable selectedSquare;
	private Drawable squareHole;
	private Drawable selectedSquareHole;
	private Drawable circle;
	private Drawable selectedCircle;
	private Drawable circleHole;
	private Drawable selectedCircleHole;

	// level variables
	private int numContainers = 1;
	private double bestAverage = 0.0;

	// timer variables
	private int count = 0;
	Timer timer;

	private int shapeSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startStopwatch();

		square = getResources().getDrawable(R.drawable.square);
		selectedSquare = getResources().getDrawable(R.drawable.selected_square);
		squareHole = getResources().getDrawable(R.drawable.square_hole);
		selectedSquareHole = getResources().getDrawable(
				R.drawable.selected_square_hole);
		circle = getResources().getDrawable(R.drawable.circle);
		selectedCircle = getResources().getDrawable(R.drawable.selected_circle);
		circleHole = getResources().getDrawable(R.drawable.circle_hole);
		selectedCircleHole = getResources().getDrawable(
				R.drawable.selected_circle_hole);

		ViewGroup main = (ViewGroup) findViewById(R.id.main);
		DrawingView drawingView = new DrawingView(this);
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1);
		drawingView.setLayoutParams(params);
		main.addView(drawingView);

		shapeSize = (int) getResources().getDimension(R.dimen.shape_size);

		Toast.makeText(getApplicationContext(),
				"Level " + numContainers + "\nClear all the shapes. Go!",
				Toast.LENGTH_SHORT).show();

	} // end onCreate

	/**
	 * Display a stopwatch and update every second
	 * http://stackoverflow.com/questions/11730902/android-simple-time-counter
	 */
	private void startStopwatch() {
		timer = new Timer();
		final TextView stopwatchText = (TextView) findViewById(R.id.stopwatch);

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// in your OnCreate() method
						stopwatchText.setText(count + "");
						count++;
					}
				});
			}
		}, 1000, 1000);
	}

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
		} // end DrawingView

		public DrawingView(Context context, AttributeSet attrs) {
			super(context, attrs);
		} // end DrawingView

		public DrawingView(Context context) {
			super(context);
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

		private int[] getRandomCoordinates() {
			// get coordinates of where to put container
			int xCoord = 0 + (int) (Math.random() * ((canvasWidth - (shapeSize)) + 1));
			int yCoord = 0 + (int) (Math.random() * ((canvasHeight - (shapeSize)) + 1));
			int[] coords = { xCoord, yCoord };
			return coords;
		}

		/*
		 * Draw the containers on the screen based on the canvas width and
		 * height
		 */
		private void drawContainers() {

			// draw random containers
			for (int i = 0; i < numContainers; i++) {
				// pick a random shape type
				Type type = (Math.random() < 0.5) ? Type.CircleHole
						: Type.SquareHole;

				int[] randCoords = getRandomCoordinates();
				int xCoord = randCoords[0];
				int yCoord = randCoords[1];

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

				clearSelections();

				// get type of hole that was clicked
				Type type = getShapeTypeOfContainerAt(event.getX(),
						event.getY());

				// if a container was touched
				if (type != null) {
					shape = new Shape(type);
					drawable = circle;

					int[] randCoords = getRandomCoordinates();
					int xCoord = randCoords[0];
					int yCoord = randCoords[1];

					shape.getBounds().set(
							xCoord - drawable.getIntrinsicWidth() / 2,
							yCoord - drawable.getIntrinsicHeight() / 2,
							xCoord + drawable.getIntrinsicWidth() / 2,
							yCoord + drawable.getIntrinsicHeight() / 2);
					shapes.add(shape);
					invalidate();

					return true;
				}

				selected = findShapeAt(event.getX(), event.getY());

				if (selected != null) {

					selected.setShapeSelected(true);

					RectF bounds = selected.getBounds();
					selectionOffsetX = event.getX() - bounds.left;
					selectionOffsetY = event.getY() - bounds.top;
				}

			case MotionEvent.ACTION_MOVE:
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
				// remove container and shape if correct shape was put into
				// container
				if (findContainerAt(event.getX(), event.getY())) {
					shapes.remove(findShapeAt(event.getX(), event.getY()));
				}

				// check if level is over
				checkDone();

				// clear selections
				clearSelections();

				break;
			} // end switch

			return true;
		} // end onTouchEvent

		private void clearSelections() {
			selected = null;

			// unselect all shapes
			for (Shape shape : shapes) {
				shape.setShapeSelected(false);
			} // end for

			// unselect all containers
			for (Shape container : containers) {
				container.setContainerSelected(false);
			} // end for
		}

		private void checkDone() {
			if (containers.size() == 0) {
				// c shapes
				shapes.clear();

				// calculate shapes cleared per second
				double shapesPerSecond = ((double) numContainers) / count;

				if (shapesPerSecond > bestAverage) {
					bestAverage = shapesPerSecond;
				}

				DecimalFormat decimalFormat = new DecimalFormat("0.00");

				// display done message
				Toast.makeText(
						getApplicationContext(),
						"You win!\nShapes per second: "
								+ decimalFormat.format(shapesPerSecond)
								+ "\nBest: "
								+ decimalFormat.format(bestAverage)
								+ "\nMoving on to Level " + numContainers++,
						Toast.LENGTH_SHORT).show();

				// go to next level
				drawContainers();

				// reset timer
				count = 0;
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

					// get "hole" type of selection
					Type currentType = selected.getType();
					Type selectedType = null;
					if (currentType == Type.SelectedCircle) {
						selectedType = Type.CircleHole;
					} else if (currentType == Type.SelectedSquare) {
						selectedType = Type.SquareHole;
					} else if (currentType == Type.Circle) {
						selectedType = Type.CircleHole;
					} else if (currentType == Type.Square) {
						selectedType = Type.SquareHole;
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

		private Type getShapeTypeOfContainerAt(float x, float y) {
			for (int i = containers.size() - 1; i >= 0; i--) {
				Shape container = containers.get(i);

				// check for a null container
				if (container == null) {
					return null;
				}

				// check if shape is above container
				if (container.getBounds().contains(x, y)) {

					// get shape type of container
					Type currentType = container.getType();
					Type newType = null;
					if (currentType == Type.CircleHole) {
						newType = Type.Circle;
					} else if (currentType == Type.SquareHole) {
						newType = Type.Square;
					}

					// indicate that the container is selected by changing its
					// type
					container.setContainerSelected(true);

					return newType;
				}
			}
			return null;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			if (paint == null) {
				// get the canvas width and height
				canvasWidth = canvas.getWidth();
				canvasHeight = canvas.getHeight();
				init();
			}

			// draw containers
			for (Shape container : containers) {
				switch (container.getType()) {
				case CircleHole:
					container.getBounds().round(rect);
					circleHole.setBounds(rect);
					circleHole.draw(canvas);
					break;
				case SquareHole:
					container.getBounds().round(rect);
					squareHole.setBounds(rect);
					squareHole.draw(canvas);
					break;
				case SelectedSquareHole:
					container.getBounds().round(rect);
					selectedSquareHole.setBounds(rect);
					selectedSquareHole.draw(canvas);
					break;
				case SelectedCircleHole:
					container.getBounds().round(rect);
					selectedCircleHole.setBounds(rect);
					selectedCircleHole.draw(canvas);
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
