package scottm.examples.guessfour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

import static android.R.attr.lines;
import static scottm.examples.guessfour.R.layout.game;

public class BoardView extends View {

    private static final String TAG = "BoardView";

    private static final float GUESS_PORTION = .75f;
    private static final float FEEDBACK_SELECT_PORTION = .25f;

    private static final float PEG_BUFFER = 16;
    private static final float GRID_LINE_SIZE = 5;

    private Board logicalBoard;
    private GuessFourGame game;
    private float guessWidth;
    private float feedbackWidth;
    private float rowHeight;
    private Circ[][] userGuesses;
    private Circ[][] feedback;
    private Circ[] secretCode;
    private boolean gradient = false;
    private RectF[] highlightRects;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, " in the 2 parameter constructor");
        init(context);
    }

    public BoardView(Context context) {
        super(context);
        Log.d(TAG, " in the 1 parameter constructor");
        init(context);
    }

    private void init(Context context) {
        if (context instanceof GuessFourGame) {
            this.game = (GuessFourGame) context;
            logicalBoard = game.gameBoard;
            setFocusable(true);
            setFocusableInTouchMode(true);
            buildhighlightRects();
            buildCirclesForGuessesAndFeedback();
            buildCirclesSecretCode();
        } else
            Log.e(TAG, "In BoardView constructor. context is not a GuessFourGame!: " + context);
    }

    public void setBoard(Board b) {
        logicalBoard = b;
    }

    private void buildCirclesSecretCode() {
        secretCode = new Circ[logicalBoard.codeSize()];
        for (int i = 0; i < secretCode.length; i++) {
            secretCode[i] = new Circ();
        }
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
        for (int r = 0; r < userGuesses.length; r++) {
            for (int c = 0; c < userGuesses[0].length; c++) {
                userGuesses[r][c] = new Circ();
                feedback[r][c] = new Circ();
            }
        }
    }

    private void buildhighlightRects() {
        highlightRects = new RectF[game.getMaxGuesses()];
        for (int i = 0; i < highlightRects.length; i++) {
            highlightRects[i] = new RectF();
        }
    }

    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        rowHeight = 1.0f * newHeight / (game.getMaxGuesses() + 1);
        guessWidth = newWidth * GUESS_PORTION;
        feedbackWidth = newWidth * FEEDBACK_SELECT_PORTION;
        updateUserGuessLocations();
        updateFeedbackCircleLocations();
        updateHighlightRectLocations();
        updateSecretCodeLocations(newWidth);
        Log.d(TAG, "onSizeChanged in BoardView: row height: " + rowHeight + " guess width: " + guessWidth);
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
    }

    private void updateSecretCodeLocations(int width) {
        float radius = (rowHeight - PEG_BUFFER) / 2;
        float totalWidth = rowHeight * secretCode.length;
        float y = rowHeight / 2;
        float x = width / 2 - totalWidth / 2 + (radius + PEG_BUFFER / 2);
        Log.d(TAG, "secret code info. width: " + width + ", rowHeight: " + rowHeight + ", radius: " + radius + ", total width: " + totalWidth);
        for (Circ aSecretCode : secretCode) {
            aSecretCode.setData(x, y, radius);
            x = x + rowHeight;
        }
    }


    private void updateFeedbackCircleLocations() {
        float heightAvailable = rowHeight / 2;
        // we will split space in half. Figure out how many feedback pegs per half.
        int pegsPerHalf = feedback[0].length / 2 + feedback[0].length % 2;
        float widthAvailable = feedbackWidth / pegsPerHalf;
        float radius = Math.min(heightAvailable, widthAvailable) / 2;
        // similar to updateUserGuessLocations. Any way to refactor and eliminate redundancy?
        radius -= PEG_BUFFER / 4;
        float y = rowHeight + heightAvailable / 2;
        for (Circ[] aFeedback : feedback) {
            float x = widthAvailable / 2 + guessWidth;
            for (int c = 0; c < feedback[0].length; c++) {
                aFeedback[c].setData(x, y, radius);
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


    private void updateHighlightRectLocations() {
        final float LEFT = 0;
        final float RIGHT = guessWidth;
        float y = rowHeight;
        for (RectF r : highlightRects) {
            r.left = LEFT;
            r.right = RIGHT;
            r.top = y;
            y += rowHeight;
            r.bottom = y;
            Log.d(TAG, "feedback rect: " + r);
        }
    }

    private void updateUserGuessLocations() {
        float pegBoxWidth = guessWidth / game.getCodeSize();
        float radius = Math.min(rowHeight, pegBoxWidth) / 2;
        radius -= PEG_BUFFER / 2;
        float y = rowHeight + rowHeight / 2;
        for (Circ[] userGuess : userGuesses) {
            float x = pegBoxWidth / 2;
            for (int c = 0; c < userGuesses[0].length; c++) {
                userGuess[c].setData(x, y, radius);
                x += pegBoxWidth;
            }
            y = y + rowHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "in onDraw");
        drawBackgroundAndGrid(canvas);
        drawSecretCode(canvas);
        drawUserGuesses(canvas);
        drawFeedback(canvas);
    }


    private void drawBackgroundAndGrid(Canvas canvas) {
        // draw the board background
        Paint board = new Paint();
        board.setColor(getResources().getColor(R.color.board));
        canvas.drawRect(0, 0, getWidth(), getHeight(), board);

        // highlight the current row if game is active
        if (game.isActive()) {
            Paint row = new Paint();
            row.setColor(getResources().getColor(R.color.current_row));
            canvas.drawRect(highlightRects[game.guessesSoFar()], row);

        }

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
    }


    private void drawUserGuesses(Canvas canvas) {
        int EMPTY_PEG_OUTER = getResources().getColor(R.color.empty_peg_outer);
        int EMPTY_PEG_INNER = getResources().getColor(R.color.empty_peg_inner);
        Paint pegPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Paint pegPaint = new Paint();
        Log.d(TAG, "Paint anti alias: " + pegPaint.isAntiAlias());
        for (int row = 0; row < userGuesses.length; row++) {
            for (int col = 0; col < userGuesses[0].length; col++) {
                Peg currentPeg = game.getPeg(row, col);
                Circ c = userGuesses[row][col];
                RadialGradient rg;
                if (currentPeg != null) {
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
        Paint grayPaint = new Paint();
        grayPaint.setColor(Color.GRAY);
        for (int row = 0; row < game.guessesSoFar(); row++) {
            String feedbackString = game.getFeedback(row);
            Log.d(TAG, "row: " + row + " feedback: " + feedbackString);
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
        for (int i = 0; i < game.getMaxGuesses(); i++) {
            canvas.drawLine(0, y, getWidth(), y, lines);
            y += rowHeight;
        }
    }


    private void drawSecretCode(Canvas canvas) {
        Log.d(TAG, "in BoardView, drawSecretCode. status of game.isActive(): " + game.isActive());

        if (!game.isActive()) {
            Paint pegPaint = new Paint();
            for (int i = 0; i < secretCode.length; i++) {
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
        } else {
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
