package scottm.examples.guessfour;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class GuessFour extends Activity implements OnClickListener {
	
	private static int[] ids = {R.id.about_button, R.id.continue_button, R.id.exit_button, R.id.new_button};
	private static final String TAG = "GuessFour";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // set up click listeners
        View[] buttons = new View[ids.length];
        for(int i = 0; i < buttons.length; i++) {
        	buttons[i] = findViewById(ids[i]);
        	buttons[i].setOnClickListener(this);
        }      	
    }
    

    public void onClick(View v) {
		Log.d(TAG, "in onClick! View is: " + v);
		Sounds.play(this, R.raw.bing2);
    	switch (v.getId()) {
    		case R.id.about_button:
    			Intent i = new Intent(this, About.class);
    			startActivity(i);
    			break;
    		case R.id.new_button:
    			openNewGameDialog();
    			break;
    		case R.id.exit_button:
    			finish();
    			break;
    		case R.id.continue_button:
    			startGame(GuessFourGame.DIFFICULTY_CONTINUE_GAME);
    			break;
    	}
    }
	
	private void openNewGameDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.new_game_title);
		adb.setItems(R.array.difficulty, 
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int difficulty) {
						startGame(difficulty);	// callback!			
					}
				});
		adb.show();
		
	}
	
	private void startGame(int difficulty) {
		Log.d(TAG, "clicked on " + difficulty);
		Intent intent = new Intent(this, GuessFourGame.class);
		intent.putExtra(GuessFourGame.KEY_DIFFICULTY, difficulty);
		startActivity(intent);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	// no real preferences. I don't have background music or hints unlike Sudoko example
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//			case R.id.settings:
//				startActivity(new Intent(this, Prefs.class));
//				return true;
//			// More items go here (if any) ...
//		}
//		return false;
//	}
}