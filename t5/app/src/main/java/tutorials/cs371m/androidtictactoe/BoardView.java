package tutorials.cs371m.androidtictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by scottm on 6/8/2016.
 */
public class BoardView extends View {

    // Width of the board grid lines
    public static final int GRID_LINE_WIDTH = 6;

    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;
    private Paint mPaint;
    private TicTacToeGame mGame;


    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setGame(TicTacToeGame game) {
        mGame = game;
    }

    public void initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Make thick, light gray lines
        mPaint.setColor(Color.LTGRAY);
        mPaint.setStrokeWidth(GRID_LINE_WIDTH);
    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        drawLines(canvas, boardWidth, boardHeight);
        drawXsAndOs(canvas, boardWidth, boardHeight);
    }

    private void drawXsAndOs(Canvas canvas, int boardWidth, int boardHeight) {
        int oneThirdWidth = boardWidth / 3;
        int oneThirdHeight = boardHeight / 3;
        Rect drawingRect = new Rect();
        // Draw all the X and O images
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;

            // Define the boundaries of a destination rectangle for the image
            drawingRect.left = col * oneThirdWidth; // x coordinate of left side of rect
            drawingRect.top = row * oneThirdHeight; // y coordinate of top of rect
            drawingRect.right = drawingRect.left + oneThirdWidth; // x coordinate of right side of rect
            drawingRect.bottom = drawingRect.top + oneThirdHeight; // y coordinate of bottom of rect

            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap, null, drawingRect, null);
            }
            else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap, null, drawingRect, null);
            }
        }
    }

    private void drawLines(Canvas canvas, int boardWidth, int boardHeight) {
        int oneThirdWidth = boardWidth / 3;
        int oneThirdHeight = boardHeight / 3;
        canvas.drawLine(oneThirdWidth, 0, oneThirdWidth, boardHeight, mPaint);
        canvas.drawLine(oneThirdWidth * 2, 0, oneThirdWidth * 2, boardHeight, mPaint);
        canvas.drawLine(0, oneThirdHeight, boardWidth, oneThirdHeight, mPaint);
        canvas.drawLine(0, oneThirdHeight * 2, boardWidth, oneThirdHeight * 2, mPaint);
    }


    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }
}
