package scottm.examples.guessfour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

public class BoardView extends View {

	private static final String TAG = "BoardView";
	
	private static final float GUESS_PORTION = .75f;
	private static final float FEEDBACK_SELECT_PORTION = .25f;

	private static final float PEG_BUFFER = 8;
	private static final float GRID_LINE_SIZE = 5;
	
	private GuessFourGame game;
	private float guessWidth;
	private float feedbackWidth;
	private float rowHeight;
	private Circ[][] userGuesses;
	private Circ[][] feedback; 
	private Circ[] secretCode;
    private boolean gradient = false;

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, " in the 2 parameter constructor");
        init(context);
	}
	
    public BoardView(Context context){
        super(context);
        Log.d(TAG, " in the 1 parameter constructor");
        init(context);
    }

    private void init(Context context) {
		if(context instanceof GuessFourGame) {
			this.game = (GuessFourGame) context;
			game.setBoardView(this);
			setFocusable(true);
			setFocusableInTouchMode(true);
			buildCirclesForGuessesAndFeedback();
			buildCirclesSecretCode();
		}
		else
			Log.e(TAG, "In BoardView constructor. context is not a GuessFourGame!: " + context); 	
    }
    
    
    

	private void buildCirclesSecretCode() {
		secretCode = new Circ[game.getCodeSize()];
		for(int i = 0; i < secretCode.length; i++)
			secretCode[i] = new Circ();
	}
	
	public void spin() {
	    Log.d(TAG, "in shake! Trying to start animation!");
        startAnimation(AnimationUtils.loadAnimation(game, R.anim.spin));
	}

	public void shakeLeftRight() {
    	Log.d(TAG, "in shake! Trying to start animation!");
    	startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
    }
	
	public void shakeUpDown() {
    	Log.d(TAG, "in shake! Trying to start animation!");
    	startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake_up_down));
    }

	
	private void buildCirclesForGuessesAndFeedback() {
		userGuesses = new Circ[game.getMaxGuesses()][game.getCodeSize()];
		feedback = new Circ[game.getMaxGuesses()][game.getCodeSize()];
		for(int r = 0; r < userGuesses.length; r++)
			for(int c = 0; c < userGuesses[0].length; c++) {
				userGuesses[r][c] = new Circ();
				feedback[r][c] = new Circ();
			}
	}


	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		rowHeight = 1.0f * h / (game.getMaxGuesses() + 1);
		guessWidth = w * GUESS_PORTION;
		feedbackWidth = w * FEEDBACK_SELECT_PORTION;
		updateUserGuessLocations();
		updateFeedbackLocations();
		updateSecretCodeLocations(w);
		Log.d(TAG, "onSizeChanged in BoardView: row height: " + rowHeight + " guess width: " + guessWidth);
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private void updateSecretCodeLocations(int width) {
		float radius = (rowHeight - PEG_BUFFER) / 2;
		float totalWidth = rowHeight * secretCode.length;
		float y = rowHeight / 2;
		float x = width / 2 - totalWidth / 2 + (radius + PEG_BUFFER / 2);
		Log.d(TAG, "secret code info. width: " + width + ", rowHeight: " + rowHeight + ", radius: " + radius + ", total width: " + totalWidth);
		for(int i = 0; i < secretCode.length; i++) {
			secretCode[i].setData(x, y, radius);
			x += rowHeight;
		}
	}
	

	private void updateFeedbackLocations() {
		float heightAvailable = rowHeight / 2;
		// we will split space in half. Figure out how many feedback pegs per half.
		int pegsPerHalf = feedback[0].length / 2 + feedback[0].length % 2;
		float widthAvailable = feedbackWidth / pegsPerHalf;
		float radius = Math.min(heightAvailable, widthAvailable) / 2;	
		// similar to updateUserGuessLocations. Any way to refactor and eliminate redundancy?
		radius -= PEG_BUFFER / 4;
		float y = rowHeight + heightAvailable / 2;
		for(int r = 0; r < feedback.length; r++) {
			float x = widthAvailable / 2 + guessWidth;
			for(int c = 0; c < feedback[0].length; c++) {
				feedback[r][c].setData(x, y, radius);
				if (c == pegsPerHalf - 1) {
					// once first half of feedback done, move down and back to the left
					x = guessWidth - widthAvailable / 2;
					y += heightAvailable;
				}
				x += widthAvailable;
			}
			y += heightAvailable;
		}
	}

	private void updateUserGuessLocations() {
		float pegBoxWidth = guessWidth / game.getCodeSize();
		float radius = Math.min(rowHeight, pegBoxWidth) / 2;
		radius -= PEG_BUFFER / 2;
		float y = rowHeight + rowHeight / 2;
		for(int r = 0; r < userGuesses.length; r++) {
			float x = pegBoxWidth / 2;
			for(int c = 0; c < userGuesses[0].length; c++) {
				userGuesses[r][c].setData(x, y, radius);
				x += pegBoxWidth;
			}
			y += rowHeight;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "in onDraw");
		drawBackgroundAndGrid(canvas);
		drawUserGuesses(canvas);
		drawFeedback(canvas);
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
		lines.setStrokeWidth(GRID_LINE_SIZE);
		
		// draw the outer border
		canvas.drawRect(0, 0, getWidth(), getHeight(), lines);
	
		lines.setStrokeWidth(2);
		drawVerticalLines(canvas, lines);
		drawHorizontalLines(canvas, lines);		
		drawSecretCode(canvas);
	}


	private void drawUserGuesses(Canvas canvas) {
		int EMPTY_PEG_OUTER = getResources().getColor(R.color.empty_peg_outer);
		int EMPTY_PEG_INNER = getResources().getColor(R.color.empty_peg_inner);
		Paint pegPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Paint pegPaint = new Paint();
        Log.d(TAG, "Paint anti alias: " + pegPaint.isAntiAlias());
		for(int row = 0; row < userGuesses.length; row++) {
			for(int col = 0; col < userGuesses[0].length; col++) {
				Peg currentPeg = game.getPeg(row, col);
				Circ c = userGuesses[row][col];
				RadialGradient rg;
				if(currentPeg != null) {
						rg = new RadialGradient(c.x, c.y - 4, c.r,
								Color.WHITE, game.getPegColor(currentPeg), Shader.TileMode.CLAMP);
					pegPaint.setColor(game.getPegColor(currentPeg));
				} else {
					rg = new RadialGradient(c.x, c.y + 4, c.r,
							EMPTY_PEG_INNER, EMPTY_PEG_OUTER, Shader.TileMode.CLAMP);
					pegPaint.setColor(EMPTY_PEG_OUTER);
				}
				if (gradient) {
                    pegPaint.setShader(rg);
                }
				canvas.drawCircle(c.x, c.y, c.r, pegPaint);
			}
		}
	}

	private void drawFeedback(Canvas canvas) {
		Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);

		for (int row = 0; row < game.guessesSoFar(); row++) {
			String feedbackString = game.getFeedback(row);
			// Log.d(TAG, "row: "+ row + " feedback: " + feedbackString);
			for (int col = 0; col < feedbackString.length(); col++) {
                Paint paint = blackPaint;
				if (feedbackString.charAt(col) == 'w') {
                    paint = whitePaint;
                }
				Circ c = feedback[row][col];
				canvas.drawCircle(c.x, c.y, c.r, paint);
			}
		}
	}

	private void drawHorizontalLines(Canvas canvas, Paint lines) {
		float y = rowHeight;
		for(int i = 0; i < game.getMaxGuesses(); i++) {
			canvas.drawLine(0, y, getWidth(), y, lines);
			y += rowHeight;
		}	
	}

	
	private void drawSecretCode(Canvas canvas) {
		Log.d(TAG, "in BoardView, drawSecretCode. status of game.isActive(): " + game.isActive());
		
		// draw the text
		if(!game.isActive()) {
			Paint pegPaint = new Paint();
			for(int i = 0; i < secretCode.length; i++) {
				Circ c = secretCode[i];
				if (gradient) {
                    RadialGradient rg = new RadialGradient(c.x, c.y - 4, c.r,
                            Color.WHITE, game.getPegColor(game.getSecretPeg(i)), Shader.TileMode.CLAMP);
                    pegPaint.setShader(rg);
                } else {
                    pegPaint.setColor(game.getPegColor(game.getSecretPeg(i)));
                }
				canvas.drawCircle(c.x, c.y, c.r, pegPaint);
			}
		}
		else {
			String secret = "SECRET CODE";
			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			textPaint.setColor(Color.WHITE);
			textPaint.setTextAlign(Paint.Align.CENTER);
			textPaint.setStyle(Style.FILL);
			textPaint.setTextSize(rowHeight * .85f);
			FontMetrics fm = textPaint.getFontMetrics();
			float x = getWidth() / 2;
			float y = rowHeight / 2 - (fm.ascent + fm.descent) / 2 + GRID_LINE_SIZE / 2;
			canvas.drawText(secret, x, y, textPaint);	
		}
	}
	
	private void drawVerticalLines(Canvas canvas, Paint lines) {
		canvas.drawLine(guessWidth, rowHeight, guessWidth, getHeight(), lines);
		canvas.drawLine(guessWidth + feedbackWidth, rowHeight, guessWidth + feedbackWidth, getHeight(), lines);
	}
}
