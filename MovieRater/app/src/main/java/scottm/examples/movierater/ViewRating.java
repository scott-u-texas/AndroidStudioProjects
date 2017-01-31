package scottm.examples.movierater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

public class ViewRating extends Activity {

	private long rowID; 
	private TextView name; 
	private RatingBar rating; 
	private TextView genre;
	private TextView dateSeen; 
	private TextView tag1;
	private TextView tag2;

	// called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_view);

		name = (TextView) findViewById(R.id.nameTextView);
		rating = (RatingBar) findViewById(R.id.rating);
		genre = (TextView) findViewById(R.id.genreTextView);
		dateSeen = (TextView) findViewById(R.id.dateSeenTextView);
		tag1 = (TextView) findViewById(R.id.tag1TextView);
		tag2 = (TextView) findViewById(R.id.tag2TextView);

		// get the selected rating's row ID
		Bundle extras = getIntent().getExtras();
		rowID = extras.getLong(MovieRaterActivity.ROW_ID); 
	} 


	// called when the activity is first created
	@Override
	protected void onResume() {
		super.onResume();

		// create new LoadRatingTask and execute it 
		new LoadRatingTask().execute(rowID);
	} 


	// performs database query outside GUI thread
	private class LoadRatingTask extends AsyncTask<Long, Object, Cursor> {

		DatabaseConnector databaseConnector = 
				new DatabaseConnector(ViewRating.this);


		// perform the database access
		@Override
		protected Cursor doInBackground(Long... params) {
			databaseConnector.open();

			// get a cursor containing all data on given entry
			return databaseConnector.getOneRating(params[0]);
		}


		// use the Cursor returned from the doInBackground method
		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);

			result.moveToFirst(); 

			// get the column index for each data item
			int nameIndex = result.getColumnIndex("name");
			int ratingIndex = result.getColumnIndex("rating");
			Log.d("ViewRating", "rating column index: " + ratingIndex);
			int genreIndex = result.getColumnIndex("genre");
			int dateSeenIndex = result.getColumnIndex("dateSeen");
			int tag1Index = result.getColumnIndex("tag1");
			int tag2Index = result.getColumnIndex("tag2");

			// fill TextViews with the retrieved data
			name.setText(result.getString(nameIndex));
			rating.setRating(result.getInt(ratingIndex)  * rating.getStepSize());
			Log.d("ViewRating", "rating from table: " + result.getInt(ratingIndex));
			Log.d("View", "rating in UI component: " + rating.getRating());
			Log.d("View", "max rating in UI component: " + rating.getMax());
			Log.d("View", "step size in UI component: " + rating.getStepSize());
			genre.setText(result.getString(genreIndex));
			dateSeen.setText(result.getString(dateSeenIndex));
			tag1.setText(result.getString(tag1Index));
			tag2.setText(result.getString(tag2Index));

			result.close();
			databaseConnector.close(); 
		} 
	} // end class LoadRatingTask


	// create the Activity's menu from a menu resource XML file
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_rating_menu, menu);
		return true;
	}


	// handle choice from options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.editItem:
			// create an Intent to launch the AddEditRating Activity
			Intent addEditRating =
			    new Intent(this, AddEditRating.class);

			// pass the selected rating's data as extras with the Intent
			addEditRating.putExtra(MovieRaterActivity.ROW_ID, rowID);
			addEditRating.putExtra("name", name.getText());
			addEditRating.putExtra("rating", rating.getRating());
			addEditRating.putExtra("genre", genre.getText());
			addEditRating.putExtra("dateSeen", dateSeen.getText());
			addEditRating.putExtra("tag1", tag1.getText());
			addEditRating.putExtra("tag2", tag2.getText());
			startActivity(addEditRating); 
			return true;

		case R.id.deleteItem:
			deleteRating();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	} 


	// delete a rating
	private void deleteRating() {
		// create a new AlertDialog Builder
		AlertDialog.Builder builder = 
				new AlertDialog.Builder(ViewRating.this);

		builder.setTitle(R.string.confirmTitle); 
		builder.setMessage(R.string.confirmMessage); 

		// provide an OK button that simply dismisses the dialog
		builder.setPositiveButton(R.string.button_delete,
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {

				final DatabaseConnector databaseConnector = 
						new DatabaseConnector(ViewRating.this);

				// create an AsyncTask that deletes the rating in another 
				// thread, then calls finish after the deletion
				AsyncTask<Long, Object, Object> deleteTask =
						new AsyncTask<Long, Object, Object>() {

					@Override
					protected Object doInBackground(Long... params) {
						databaseConnector.deleteRating(params[0]); 
						return null;
					} 

					@Override
					protected void onPostExecute(Object result) {
						finish(); 
					} 
				}; 

				deleteTask.execute(new Long[] { rowID });               
			}
		}); 

		builder.setNegativeButton(R.string.button_cancel, null);
		builder.show(); 
	} 
}
