package scottm.examples.movierater;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MovieRaterActivity extends ListActivity {

	public static final String ROW_ID = "row_id"; // Intent extra key
	private ListView ratingListView; 
	private CursorAdapter ratingAdapter; 


	@Override
	public void onCreate(Bundle savedInstanceState) { 

		super.onCreate(savedInstanceState);
		ratingListView = getListView(); 
		ratingListView.setOnItemClickListener(viewRatingListener);      

		// map each ratings's name to a TextView
		// in the ListView layout
		String[] from = new String[] { "name" };
		int[] to = new int[] { R.id.ratingTextView };
		ratingAdapter = new SimpleCursorAdapter(
				MovieRaterActivity.this, 
				R.layout.rating_list_item, null, 
				from, to);
		// public SimpleCursorAdapter (Context context, 
		// int layout, Cursor c, 
		// String[] from, int[] to)

		setListAdapter(ratingAdapter); 
	} 


	@Override
	protected void onResume() {
		super.onResume(); 

		// create new GetRatingsTask and execute it 
		new GetRatingsTask().execute((Object[]) null);
	} 


	@Override
	protected void onStop() {
		Cursor cursor = ratingAdapter.getCursor(); 

		if (cursor != null) 
			cursor.deactivate(); // deactivate it

		ratingAdapter.changeCursor(null); // adapter now has no Cursor
		super.onStop();
	} 


	// performs database query outside GUI thread
	private class GetRatingsTask extends AsyncTask<Object, Object, Cursor> {
		DatabaseConnector databaseConnector = 
				new DatabaseConnector(MovieRaterActivity.this);

		// perform the database access
		@Override
		protected Cursor doInBackground(Object... params) {
			databaseConnector.open();

			return databaseConnector.getAllRatings(); 
		} 

		// use the Cursor returned from the doInBackground method
		@Override
		protected void onPostExecute(Cursor result) {
			ratingAdapter.changeCursor(result); 
			databaseConnector.close();
		} 
	} // end class GetContactsTask


	// create the Activity's menu from a menu resource XML file
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.movie_rating_menu, menu);
		return true;
	} 


	// handle choice from options menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// create a new Intent to launch
		Intent addNewContact = 
				new Intent(this, AddEditRating.class);
		startActivity(addNewContact); 
		return super.onOptionsItemSelected(item); 
	} 


	// event listener that responds to the user touching a contact's name
	// in the ListView
	OnItemClickListener viewRatingListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Log.d("MoiveRater", "postion: " + position + ", id: " + id);
			// create an Intent to launch the ViewRating Activity
			Intent viewContact = 
					new Intent(MovieRaterActivity.this, ViewRating.class);

			// pass the selected contact's row ID as an extra with the Intent
			viewContact.putExtra(ROW_ID, id);
			startActivity(viewContact);
		} 
	}; 
}
