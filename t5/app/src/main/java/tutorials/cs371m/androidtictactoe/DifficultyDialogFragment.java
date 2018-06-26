package tutorials.cs371m.androidtictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by scottm on 6/7/2016.
 */
public class DifficultyDialogFragment extends DialogFragment {

    private static final String TAG = "Difficulty Dialog";

    private static final String DIFFICULTY_KEY = "difficulty";

    /**
     * Create a new instance of Difficulty, initialized to
     * show the current difficulty
     */
    public static DifficultyDialogFragment newInstance(int difficulty) {
        DifficultyDialogFragment result = new DifficultyDialogFragment();

        // Supply difficulty input as an argument.
        Bundle args = new Bundle();
        args.putInt(DIFFICULTY_KEY , difficulty);
        result.setArguments(args);

        return result;
    }

    public int getSelectedDifficulty() {
        return getArguments().getInt(DIFFICULTY_KEY, 0);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int currentDifficulty = getSelectedDifficulty();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.difficulty_choose)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(R.array.difficulty_levels, currentDifficulty,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // save the difficulty
                                getArguments().putInt(DIFFICULTY_KEY, which);
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // user pressed okay, so we are going to change the difficulty
                        int newDifficulty = getArguments().getInt(DIFFICULTY_KEY, 0);
                        Log.d(TAG, "User clicked okay. Changing difficulty to: " + newDifficulty);
                        ((AndroidTicTacToe)getActivity()).setDifficulty(newDifficulty);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // don't change difficulty
                        dismiss();
                    }
                });

        return builder.create();
    }
}
