package scottm.examples.guessfour;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;

public class GuessFourGame extends Activity {

	private static final String TAG = "GuessFourGame";
	
	public static final String KEY_DIFFICULTY = "scottm.examples.guessfour";
	private static final String PREF_GAME = "guessFourGame";
	
	public static final int DIFFICULTY_CONTINUE_GAME = -1;
	public static final int DIFFICULY_EASY = 0;
	public static final int DIFFICULY_TRADITIONAL = 1;
	public static final int DIFFICULY_HARD = 2;
	
	private static final int MAX_GUESSES = 12;
	
	// row 0 is code size, row 1 is number of colors
	// columns are difficulties, easy to hard
	private static int[][] pegsAndColors = {{3, 4, 5}, 
											{4, 6, 8}};
	
	private HashMap<Peg, Integer> pegColors;		
	private int numColors;
	private Button[] colorButtons;
	private Board gameBoard;
	private BoardView boardView;
	private boolean active;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate 1");
		
		int difficulty = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULY_TRADITIONAL);
		createBoard(difficulty);
		
		buildPegColors();
		setContentView(R.layout.game);
		addColorButtons();
		
		// allow volume control:
	    setVolumeControlStream(AudioManager.STREAM_MUSIC);
	    
	    // if this activity is restarted need to know to continue!
	    getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE_GAME);
	}
	
	private void createBoard(int difficulty) {
		Log.d(TAG, "In createBoard. difficulty: " + difficulty);
		
		if(difficulty == DIFFICULTY_CONTINUE_GAME) {
			// retrieve the old codes
			String oldCodes = getPreferences(MODE_PRIVATE).getString(PREF_GAME, "");
			Log.d(TAG, "In createBoard. old board: " + oldCodes);
			rebuildBoard(oldCodes);
		}
		else {
			active = true;
			int codeSize = pegsAndColors[0][difficulty];
			numColors = pegsAndColors[1][difficulty];
			Code secretCode = new Code(codeSize, numColors);
			gameBoard = new Board(MAX_GUESSES, secretCode);			
		}
	}
	
	private void rebuildBoard(String oldCodes) {
		int indexOfNewLine = oldCodes.indexOf("\n");
		String[] data = oldCodes.substring(0,indexOfNewLine).split("\\s+");
		active = data[0].equals("true");
		numColors = Integer.parseInt(data[1]);
		Log.d(TAG, Arrays.toString(data) + " active? " + active);
		gameBoard = new Board(MAX_GUESSES, oldCodes.substring(indexOfNewLine + 1));
	}

	private void addColorButtons() {
		Log.d(TAG, "In addButtons.");
		
		colorButtons = new Button[numColors];
		LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
		float weight = (float) (1.0 / numColors);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT, weight);
		for (int buttonNum = 0; buttonNum < numColors; buttonNum++) {
			final Button btn = new Button(this);
			final Peg p = Peg.values()[buttonNum];
			btn.setText(p.toString());
			btn.getBackground().setColorFilter(pegColors.get(p), PorterDuff.Mode.MULTIPLY);
			btn.setTextSize(16);
			btn.setSingleLine();
			btn.setLayoutParams(param);
            Log.d(TAG, "BUTTON PARAMETERS: " + ((LinearLayout.LayoutParams) btn.getLayoutParams()).weight);
			btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (active) {
                        setNextPeg(p);
                        Log.d(TAG, "color button clicked:" + btn.getText());
                        boardView.invalidate();
                    } else {
                        newGame();
                    }
                }
            });
			buttonContainer.addView(btn);
			colorButtons[buttonNum] = btn;
		}
	}
	
	public void newGame() {
		// remind to start new game!!!!!
	}
	
	public void setBoardView(BoardView bv) {
		boardView = bv;
	}
	
	private void setNextPeg(Peg p) {
		if(gameBoard.currentGuessFull()) 
			showError(R.string.code_full_message);
		else {
			gameBoard.addPeg(p);
			Sounds.play(this, R.raw.bing2);
		}
	}

	private void buildPegColors() {
		int[] colors = getResources().getIntArray(R.array.peg_colors);
		Peg[] pegs = Peg.values();
		if(colors.length != pegs.length)
			Log.e(TAG, "colors array in xml file and number of pegs do not match");
		int numPairs = Math.min(colors.length, pegs.length);
		pegColors = new HashMap<Peg, Integer>();
		for(int i = 0; i < numPairs; i++) {
			pegColors.put(pegs[i], colors[i]);
		}
	}
	
	public int getPegColor(Peg p) {
		return pegColors.get(p);
	}
	
	public int getMaxGuesses() {
		return MAX_GUESSES;
	}
	
	public int getCodeSize() {
		return gameBoard.codeSize();
	}
	
	public int getNumColors() {
		return numColors;
	}
	
	public int guessesSoFar() {
		return gameBoard.guessesSoFar();
	}
	
    // pre: 0 <= guessNum < maxGuesses, 0 <= pegNum < codeSize()
	public Peg getPeg(int guessNum, int pegNum) {
		return gameBoard.getPeg(guessNum, pegNum);
	}


    public void clearPeg(View v) {
        if(active)
            clearPeg();
    }

    public void makeGuess(View v) {
        if(active)
            makeGuess();
    }

    public void newGame(View v) {
        Log.d(TAG, "new game button pressed");
        checkNewGame();
    }

	
	
	private void checkNewGame() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to start a new game?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                GuessFourGame.this.finish();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();	
		Log.d(TAG, "in check new game. alert: " + alert);
		alert.show();
	}

	public String getFeedback(int guessNum) {
		return gameBoard.getFeedback(guessNum);
	}

	private void makeGuess() {
		if(!gameBoard.currentGuessFull())
			showError(R.string.code_not_full_error);
		else {
			gameBoard.addGuess();
			if(gameBoard.solved()) {
				active = false;
				gameOver(R.string.win);
				Sounds.play(this, R.raw.f);
			}
			else if(gameBoard.guessesSoFar() == gameBoard.maxAllowedGuesses()) {
				active = false;
				gameOver(R.string.lost);
			}
			else
				Sounds.play(this, R.raw.bing);
			boardView.invalidate();
		}	
	}

	private void clearPeg() {
		if(gameBoard.currentGuessEmpty())
			showError(R.string.code_empty_message);
		else {
			Sounds.play(this, R.raw.tick);
			gameBoard.removeLastPeg();
			boardView.invalidate();
		}
	}

	private void showError(int id) {
		Sounds.play(this, R.raw.bong);
		boardView.shakeLeftRight();
		Toast errorToast = Toast.makeText(this, id, Toast.LENGTH_SHORT);
		errorToast.setGravity(Gravity.CENTER, 0, 0);
		errorToast.show();			
	}
	
	private void gameOver(int id) {
	    if(id == R.string.win)
	        boardView.spin();
	    else
	        boardView.shakeUpDown();
		Toast gameOverToast = Toast.makeText(this, id, Toast.LENGTH_LONG);
		gameOverToast.setGravity(Gravity.CENTER, 0, 0);
		gameOverToast.show();
	}
	
	public Peg getSecretPeg(int num) {
		return gameBoard.getSecretPeg(num);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "in onPause");
		
		// save current board. We will reconstruct feedback if necessary
		String codeInfo = gameBoard.toStringCodes();
		// add the number of colors as first piece of data
		codeInfo = active + " " + numColors + "\n" + codeInfo;
		getPreferences(MODE_PRIVATE).edit().putString(PREF_GAME, codeInfo).commit();
	}
	
	public boolean isActive() {
		return active;
	}
}
