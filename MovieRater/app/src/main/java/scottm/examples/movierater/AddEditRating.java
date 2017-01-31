package scottm.examples.movierater;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;


public class AddEditRating extends Activity {
    
    private static final String TAG = "AddEditRating";
    
	private long rowID; 

	private EditText title;
	private RatingBar rating;
	private EditText genre;
	private EditText dateSeen;
	private EditText tag1;
	private EditText tag2;

	// called when the Activity is first started
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.add_rating);

		title = (EditText) findViewById(R.id.titleEditText);
		rating = (RatingBar) findViewById(R.id.ratingBar);
		dateSeen = (EditText) findViewById(R.id.dateSeenEditText);
		genre = (EditText) findViewById(R.id.genreEditText);
		tag1 = (EditText) findViewById(R.id.tag1EditText);
		tag2 = (EditText) findViewById(R.id.tag2EditText);

		Log.d(TAG, "rating bar stars: " + rating.getNumStars());
		
		Bundle extras = getIntent().getExtras();

		// if there are extras, use them to populate the EditTexts
		if (extras != null) {
			rowID = extras.getLong("row_id");
			title.setText(extras.getString("name"));  
			rating.setRating(extras.getFloat("rating"));
			Log.d(TAG, "rating from field:" + extras.getFloat("rating"));
			Log.d(TAG, "rating in UI component: " + rating.getRating());
			dateSeen.setText(extras.getString("dateSeen"));  
			genre.setText(extras.getString("genre"));  
			tag1.setText(extras.getString("tag1"));  
			tag2.setText(extras.getString("tag2"));  
		} // end if

		// set event listener for the Save Rating Button
		Button saveRatingButton = 
				(Button) findViewById(R.id.saveRatingButton);
		saveRatingButton.setOnClickListener(saveRatingButtonClicked);
	} 

	// responds to event generated when user clicks the Done Button
	OnClickListener saveRatingButtonClicked = new OnClickListener() { 
		@Override
		public void onClick(View v) {
			if (title.getText().length() != 0) {

				AsyncTask<Object, Object, Object> saveRatingTask = 
						new AsyncTask<Object, Object, Object>() {

					@Override
					protected Object doInBackground(Object... params) { 
						saveRating(); 
						return null;
					} 

					@Override
					protected void onPostExecute(Object result) { 
						finish(); 
					} 
				}; 

				// save the rating to the database using a separate thread
				saveRatingTask.execute((Object[]) null); 
			}
			else {
				// create a new AlertDialog Builder
				AlertDialog.Builder builder = 
				new AlertDialog.Builder(AddEditRating.this);

				// set dialog title & message, and provide Button to dismiss
				builder.setTitle(R.string.errorTitle); 
				builder.setMessage(R.string.errorMessage);
				builder.setPositiveButton(R.string.errorButton, null); 
				builder.show(); 
			} 
		} 
	}; 


	private void saveRating() {
		// get DatabaseConnector to interact with the SQLite database
		DatabaseConnector databaseConnector = new DatabaseConnector(this);
		
		
		Log.d(TAG, "rating inserted into DB: " + (rating.getRating() * 2));

		if (getIntent().getExtras() == null) {
			// insert the rating information into the database
			databaseConnector.insertRating(
					title.getText().toString(),
					(int) (rating.getRating() * 2),
					genre.getText().toString(), 
					dateSeen.getText().toString(), 
					tag1.getText().toString(),
					tag2.getText().toString());
		} 
		else {
			databaseConnector.updateRating(rowID,
					title.getText().toString(),
					(int) (rating.getRating() * 2),
					genre.getText().toString(), 
					dateSeen.getText().toString(), 
					tag1.getText().toString(),
					tag2.getText().toString());
		} 
	} 
} 

