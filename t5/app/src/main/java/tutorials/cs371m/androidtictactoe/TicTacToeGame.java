package tutorials.cs371m.androidtictactoe;

/* TicTacToeConsole.java
 * By Frank McCown (Harding University)
 *
 * This is a tic-tac-toe game that runs in the console window.  The human
 * is X and the computer is O.
 */

import android.util.Log;
import java.util.Random;

public class TicTacToeGame {

    // The computer's difficulty levels
    // Easy difficulty -> always random move.
    // Harder difficulty -> winning move if available otherwise random
    // Expert difficultly -> winning move if available, otherwise block possible
    //      winning move, otherwise random. (Can still lose.)
    public enum DifficultyLevel {Easy, Harder, Expert}


    private static final String TAG = "TicTacToeGame";
    public final static int BOARD_SIZE = 9;

    // Characters used to represent the human, computer, and open spots
    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';


    private Random mRand;
    private char mBoard[];
    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();
        mBoard = new char[BOARD_SIZE];
        clearBoard();
    }

    /**
     * Get the current difficulty level.
     * @return The current difficulty level for this game.
     */
    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    /**
     * Set the difficulty level for this game.
     * @param difficultyLevel The new difficulty level.
     */
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard() {
        for (int i = 0; i < mBoard.length; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    /** Set the given player at the given location on the game board.
     *  The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public void  setMove(char player, int location){
        mBoard[location] = player;
    }


    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    public char getBoardOccupant(int cell) {
        return mBoard[cell];
    }

    public int getComputerMove() {
        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.Expert) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        return move;
    }

    private int getRandomMove() {
        int move;
        // Generate random move
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        Log.d(TAG, "Computer is moving to " + move + ", a random move");
        return move;
    }

    // Check if winning move on board exists.
    // If so, return that value, otherwise return -1
    //      to indicate no winning move.
    private int getWinningMove() {
        // First see if there's a move O can make to win
        int space = 0;
        int spaceToMoveTo = -1;
        while (space < mBoard.length && spaceToMoveTo == -1) {
            if (mBoard[space] == OPEN_SPOT) {
                // temporarily place computer piece in spot
                mBoard[space] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    Log.d(TAG, "Computer is moving to " + space + " in order to win.");
                    spaceToMoveTo = space;
                }
                mBoard[space] = OPEN_SPOT;
            }
            space++;
        }
        return spaceToMoveTo;
    }

    // Check if other player has 2 in a row for use to block.
    // If so, return that value, otherwise return -1
    //      to indicate no winning move.
    private int getBlockingMove() {
        // See if there's a move O can make to block X from winning
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];   // Save the current number
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER;
                    Log.d(TAG, "Computer is moving to " + i + " in order to block human win.");
                    return i;
                }
                else
                    mBoard[i] = curr;
            }
        }
        return -1; // no blocking moves
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mBoard.length; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                result.append("-");
            }
            else {
                result.append(mBoard[i]);
            }
            if ((i + 1) % 3 == 0) {
                result.append('\n');
            }
        }
        return result.toString();
    }
}
