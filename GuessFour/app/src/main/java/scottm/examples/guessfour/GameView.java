package scottm.examples.guessfour;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

	private static final String TAG = "GuessFour";
	
	private static final float GUESS_PORTION = .56f;
	private static final float FEEDBACK_SELECT_PORTION = .20f;
	private static final int PEG_BUFFER = 10;

	private GuessFourGame game;
	private float guessWidth;
	private float feedbackWidth;
	private float colorSelectWidth;
	private float rowHeight;
	private int selectedColor;
	private HashMap<Peg, Integer> pegColors;
	private Circ[][] userGuesses;
	private Circ[] colorSelections;
	
	public GameView(Context context) {
		super(context);
		if(context instanceof GuessFourGame) {
			this.game = (GuessFourGame) context;
			setFocusable(true);
			setFocusableInTouchMode(true);
			buildPegColors();
			buildUserGuesses();
			buildColorSelections();
		}
		else
			Log.e(TAG, "In GameView constructor. context is not a GuessFourGame!: " + context);
	}
	
	private void buildColorSelections() {
		colorSelections = new Circ[game.getNumColors()];
		for(int i = 0; i < colorSelections.length; i++)
			colorSelections[i] = new Circ();
	}

	private void buildUserGuesses() {
		userGuesses = new Circ[game.getMaxGuesses()][game.getCodeSize()];
		for(int r = 0; r < userGuesses.length; r++)
			for(int c = 0; c < userGuesses[0].length; c++)
				userGuesses[r][c] = new Circ();
	}

	private void buildPegColors() {
		int[] colors = getResources().getIntArray(R.array.peg_colors);
		Peg[] pegs = Peg.values();
		if(colors.length != pegs.length)
			Log.e(TAG, "colors array in xml file and number of pegs do not match");
		int numPairs = Math.min(colors.length, pegs.length);
		pegColors = new HashMap<Peg, Integer>();
		for(int i = 0; i < numPairs; i++)
			pegColors.put(pegs[i], colors[i]);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		rowHeight = 1.0f * h / (game.getMaxGuesses() + 1);
		
		guessWidth = w * GUESS_PORTION;
		feedbackWidth = w * FEEDBACK_SELECT_PORTION;
		colorSelectWidth = w - guessWidth - feedbackWidth;
		updateUserGuessLocations();
		updateColorSelectionInfo();
		Log.d(TAG, "onSizeChanged: row height: " + rowHeight + " guess width: " + guessWidth);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	
	private void updateColorSelectionInfo() {
		float maxWidth = (colorSelectWidth);
		float maxHeight = ((getHeight() - (rowHeight * 2)) / game.getNumColors());
		Log.d(TAG, "trying to place color select pegs. maxWidth = " + maxWidth + ", maxHeight = " + maxHeight);
		float pegRadius = (Math.min(maxWidth, maxHeight) - PEG_BUFFER) / 2;
		float x = (getWidth() - colorSelectWidth / 2);
		float y = rowHeight * 5 / 2;
		for(int i = 0; i < colorSelections.length; i++) {
			colorSelections[i].setData(x, y, pegRadius);
			y += pegRadius * 2 + PEG_BUFFER;
		}
	}

	private void updateUserGuessLocations() {
		float pegBoxWidth = guessWidth / game.getCodeSize();
		float newRadius = Math.min(rowHeight, pegBoxWidth) / 2;
		newRadius -= PEG_BUFFER / 2;
		float y = rowHeight + rowHeight / 2;
		for(int r = 0; r < userGuesses.length; r++) {
			float x = pegBoxWidth / 2;
			for(int c = 0; c < userGuesses[0].length; c++) {
				userGuesses[r][c].setData(x, y, newRadius);
				x += pegBoxWidth;
			}
			y += rowHeight;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawBackgroundAndGrid(canvas);
		drawUserGuesses(canvas);
		drawColorSelectPegs(canvas);
		drawSelectionCircle(canvas);
	}
	
	private void drawSelectionCircle(Canvas canvas) {
		Paint highlightPaint = new Paint();
		highlightPaint.setStyle(Paint.Style.STROKE);
		highlightPaint.setStrokeWidth(10);
		highlightPaint.setColor(Color.WHITE);
		Circ c = colorSelections[selectedColor];
		canvas.drawCircle(c.x, c.y, c.r, highlightPaint);
	}

	private void drawBackgroundAndGrid(Canvas canvas) {
		// draw the board background
		Paint board = new Paint();
		board.setColor(getResources().getColor(R.color.board));
		canvas.drawRect(0, 0, getWidth(), getHeight(), board);
		
		
		// create the paint for the grid lines
		Paint lines = new Paint();
		lines.setStyle(Paint.Style.STROKE);
		lines.setColor(getResources().getColor(R.color.gridline));
		lines.setStrokeWidth(5);
		
		// draw the outer border
		canvas.drawRect(0, 0, getWidth(), getHeight(), lines);
	
		lines.setStrokeWidth(2);
		drawVerticalLines(canvas, lines);
		drawHorizontalLines(canvas, lines);		
	}

	private void drawColorSelectPegs(Canvas canvas) {
		// draw the text
		String pick = "PICK";
		String color = "COLOR";
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setStyle(Style.FILL);
		float width = Math.min(rowHeight, Math.max(textPaint.measureText(pick), textPaint.measureText(color))) + PEG_BUFFER;
		textPaint.setTextSize(textPaint.getTextSize() * (colorSelectWidth / width));
		Rect textBounds = new Rect();
		textPaint.getTextBounds(color, 0, color.length(), textBounds);
		float x = getWidth() - colorSelectWidth / 2;
		float y = textBounds.height() + PEG_BUFFER;
		canvas.drawText(pick, x, y, textPaint);
		canvas.drawText(color, x, y * 2, textPaint);
		
		// draw the Color Select Pegs
		Paint pegPaint = new Paint();
		for(int i = 0; i < colorSelections.length; i++) {
			Peg currentPeg = Peg.values()[i];
			pegPaint.setColor(pegColors.get(currentPeg));
			Circ c = colorSelections[i];
			canvas.drawCircle(c.x, c.y, c.r, pegPaint);
		}
		
	}

	private void drawUserGuesses(Canvas canvas) {
		Paint pegPaint = new Paint();
		
		for(int row = 0; row < userGuesses.length; row++) {
			for(int col = 0; col < userGuesses[0].length; col++) {
				if(row < game.guessesSoFar()) {
						Peg currentPeg = game.getPeg(row, col);
						pegPaint.setColor(pegColors.get(currentPeg));
				}
				else
					pegPaint.setColor(getResources().getColor(R.color.empty_peg_outer));
				Circ c = userGuesses[row][col];
				canvas.drawCircle(c.x, c.y, c.r, pegPaint);
			}
		}
	}

	private void drawHorizontalLines(Canvas canvas, Paint lines) {
		float y = rowHeight;
		for(int i = 0; i < game.getMaxGuesses(); i++) {
			canvas.drawLine(0, y, getWidth() - colorSelectWidth, y, lines);
			y += rowHeight;
		}	
	}

	
	private void drawVerticalLines(Canvas canvas, Paint lines) {
		canvas.drawLine(guessWidth, rowHeight, guessWidth, getHeight(), lines);
		canvas.drawLine(guessWidth + feedbackWidth, rowHeight, guessWidth + feedbackWidth, getHeight(), lines);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "inside method onKeyDown. keyCode: " + keyCode + ", keyEvent: " + event);
		switch(keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP :
				selectColor(-1);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN :
				selectColor(1);
				break;
//			default :
//				return super.onKeyDown(keyCode, event);
		}
		return true;
	}
	
	
	private void selectColor(int change) {
		Rect r = new Rect((int) (getWidth() - colorSelectWidth), 0, getWidth(), getHeight());
		selectedColor += change;
		if(selectedColor < 0)
			selectedColor = 0;
		else if( selectedColor >= game.getNumColors())
			selectedColor = game.getNumColors() - 1;
		invalidate(r);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		float x = event.getX();
		float y = event.getY();
		int index = 0;
		boolean found = false;
		while(!found && index < colorSelections.length) {
			if(colorSelections[index].contains(x, y))
				found = true;
			else
				index++;	
		}
		if(found)
			selectColor(index - selectedColor);
		
		return true;
	}


	private static class Circ {
		float x;
		float y;
		float r;
		
		private void setData(float x, float y, float r) {
			this.x = x;
			this.y = y;
			this.r = r;
		}
		
		private boolean contains(float x, float y) {
			float xDist = this.x - x;
			float yDist = this.y - y;
			float distance = FloatMath.sqrt(xDist * xDist + yDist * yDist);
			return distance <= r;
		}
	}
}
