package scotm.examples;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class ContentProviderExampleActivity extends ListActivity {
	
	private static final String TAG = "ImageContent";

    private EditText inputEditText;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        populateLisView(0);
        inputEditText = (EditText) findViewById(R.id.minSize);
        showContentProviders();
    }

    // from: http://stackoverflow.com/questions/2001590/get-a-list-of-available-content-providers
    private void showContentProviders() {
        int count = 0;
        for (PackageInfo pack
                : getPackageManager()
                .getInstalledPackages(PackageManager.GET_PROVIDERS)) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {

                count += providers.length;
                for (ProviderInfo provider : providers) {
                    Log.d(TAG, "provider: " + provider.authority);
                }
            }
        }
        Log.d(TAG, "Number of providers on device: " + count);
    }
    
    public void tryCalendarIntent(View v) {
//		Log.d(TAG, "Trying Calendar Intent");
//    	Calendar beginTime = Calendar.getInstance();
//    	beginTime.set(2016, Calendar.APRIL, 4, 8, 00);
//    	Calendar endTime = Calendar.getInstance();
//    	endTime.set(2016, Calendar.APRIL, 4, 19, 00);
//    	Intent intent = new Intent(Intent.ACTION_INSERT)
//    	        .setData(Events.CONTENT_URI)
//    	        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
//    	        			beginTime.getTimeInMillis())
//    	        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
//    	        			endTime.getTimeInMillis())
//    	        .putExtra(Events.TITLE, "ALPHA RELEASE")
//    	        .putExtra(Events.DESCRIPTION, "Major assignment " +
//    	        						"is due in CS371m!!!!")
//    	        .putExtra(Intent.EXTRA_EMAIL, "scottm@cs.utexas.edu");
//    	startActivity(intent);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Log.d(TAG, "in onResume");
        showImageDataInLog();
    }

	private void populateLisView(int size) {
		
		String[] columns = {MediaStore.Images.Media.DATE_TAKEN, 
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media._ID,
                MediaStore.Images.Media._ID};
		
		int[] listViewIDs = {R.id.date_taken,
				R.id.size, R.id.data, R.id.id, R.id.thumb};
		
		String selectionClause
                = MediaStore.Images.Media.SIZE + " > ?";
		
		String[] selectionArgs
                = {Integer.toString(size)};
		
		Cursor imageData = getContentResolver().query(
			    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			    columns,
			    selectionClause,        
			    selectionArgs,
                MediaStore.Images.Media.DATE_TAKEN + " DESC");
		
		
		ListAdapter adapter = new MyAdapter(this,
				R.layout.list_item_view,
				imageData, columns, listViewIDs);
		
		Log.d(TAG, "Number of images: " + adapter.getCount());
		
		setListAdapter(adapter);
	}
//
//    private void createOnItemClickListener() {
//
//        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            public void onItemClick(AdapterView<?> parent,
//                                    View v, int position, long id) {
//
//                Log.d(TAG, "Selected view: " + v);
//
//                String country = countries.get(position);
//
//                String toastString = "position: " + position +
//                        ", id: " + id + "\ndata: "
//                        + country;
//
//                // example if creating and showing a Toast. Cheers!
//                Toast.makeText(ContentProviderExampleActivity.this,
//                        toastString,
//                        Toast.LENGTH_LONG).show();
//
////                // remove item selected from arraylist
////                countries.remove(position);
////                //
////                adapter.notifyDataSetChanged();
//
//                //needed?
//                // view.invalidateViews();
//
//                // if we want to perform web search for country
//                // searchWeb(country);
//            }
//        });
//    }

    public void filterBySize(View v) {
		String rawSize = inputEditText.getText().toString();
		if (rawSize.length() > 0 && onlyDigits(rawSize)) {
			int size = Integer.parseInt(rawSize);
            Log.d(TAG, "filtering size: " + size);
			populateLisView(size);
		}
    }


    private boolean onlyDigits(String str) {
        str = str.trim();
        for (int i = 0; i < str.length(); i++) {
            int value = str.charAt(i) - '0';
            if (value < 0 || value > 9) {
                return false;
            }
        }
        return true;
	}

	private static class MyAdapter 
		extends SimpleCursorAdapter {
		
		static String format = "MM/dd/yyyy hh:mm a";

        private Context context;
		
		private MyAdapter(Context c, int layout, 
				Cursor cur, String[] from, int[] to) {
			super(c,layout, cur, from, to);
            context = c;
		}
		
		public void setViewText(TextView v, String text) {
		    if (v.getId() == R.id.date_taken) {
		        text = getDate(Long.parseLong(text), format);
		    } else if (v.getId() == R.id.data) {
                text = "Path: " + text;
            } else if (v.getId() == R.id.id) {
                text = "Image ID: " + text;
            } else if (v.getId() == R.id.size) {
                text = "Image Size: " + text;
            }
		    v.setText(text);
		}

        public void setViewImage (ImageView v,
                           String value) {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    context.getContentResolver(), Integer.parseInt(value),
                    MediaStore.Images.Thumbnails.MICRO_KIND, null
            );
            if (bitmap != null) {
                v.setImageBitmap(bitmap);
            }
        }

	}

	private void showImageDataInLog() {
		Cursor cursor = getContentResolver().query(
				/* The content URI of the image table*/
			    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			    /* String[] projection, The columns to return for each row 
			     * if null, get them all*/
			    null,    
			    /*  String selection criteria, return rows that match this
			     * if null return all rows  */
			    null,        
			    /* String[] selectionArgs. ?s from selection
			     * ?s replaced by this parameter.*/
			    null,   
			    /* String sortOrder, how to sort row, null unsorted */
			    null);                        
		
		Log.d(TAG, "Image count: " + cursor.getCount());
		Log.d(TAG, "Columns: "  + cursor.getColumnCount());
		String[] columns = cursor.getColumnNames();
		
		Log.d(TAG, "Columns: " + Arrays.toString(columns));
		
		String[] projection = {MediaStore.Images.Media.DATE_TAKEN, 
				MediaStore.Images.Media.SIZE,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.LATITUDE,
				MediaStore.Images.Media.LONGITUDE,
				MediaStore.Images.Media._ID};
		
		cursor = getContentResolver().query(
			    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
			    projection,
			    null,        
			    null,   
			    MediaStore.Images.Media.DATE_TAKEN);
		
		// get column indices, refactor to array of ints using projection String[]
		int size 
			= cursor.getColumnIndex(MediaStore.Images.Media.SIZE);
		int dateTaken 
			= cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
		int bucketDisplayName 
			= cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
		int latitude
            = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
		Log.d(TAG, "column num for latitude: " + latitude);
		int longitude
            = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
		Log.d(TAG, "column num for longitude: " + longitude);
		
		int id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
		    String imageData = "size: " + cursor.getInt(size) + ", ";
			String sDate = format.format(cursor.getLong(dateTaken));
			imageData += "date taken: " + sDate + ", ";
			imageData += "bucket display name: " + cursor.getString(bucketDisplayName) + ", ";
			imageData += "latitude: " + cursor.getDouble(latitude) + ", ";
			imageData += "longitude: " + cursor.getDouble(longitude) + ", ";
			imageData += "_id: " + cursor.getInt(id);
			Log.d(TAG, imageData);
			cursor.moveToNext();
		}
		
		cursor.moveToFirst();
		int idFirst = cursor.getInt(id);
		trySingleRowWithID(idFirst);
	}
	
	private void trySingleRowWithID(int id) {
	    Log.d(TAG, "Getting single row with id");
	    // know id on 
	    Uri singleUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  id);
	    Cursor cursor = getContentResolver().query(singleUri, null, null, null, null);
	    Log.d(TAG, "column count: " + cursor.getColumnCount());
	    Log.d(TAG, "row count: " + cursor.getCount());
    }

    // from http://stackoverflow.com/questions/7953725/how-to-convert-milliseconds-to-date-format-in-android
	public static String getDate(long milliSeconds, String dateFormat) {

	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}

}