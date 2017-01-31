package scottm.cs378.example.intentexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class IntentExample extends Activity {

    public static final String NAME_DATA = "NAME_DATA";
    public static final int GET_NAME_REQUEST = 812029;

    private static final String TAG = "INTENT EXAMPLE";
    private static final String HINT = "hint";
    private static final int TAKE_PICTURE_CODE = 526719;
    private Uri outputFileUri;
    private String fileName;
    private String extension;
    private int imageNumber;
    private String pictureLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_intent_example);
        fileName = Environment.getExternalStorageDirectory() + "/intentExamplePhotos/test";
        extension = ".jpg";
        imageNumber = readImageNumber();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "in onPause");
    }

    private int readImageNumber() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(getString(R.string.image_number_tag), 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "in onStop");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.image_number_tag), imageNumber);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        Log.d(TAG, "request code: " + requestCode);
        Log.d(TAG, "result code: " + resultCode);


        if (requestCode == TAKE_PICTURE_CODE) {
            handlePicture(resultCode);
        } else if (requestCode == GET_NAME_REQUEST) {
            showName(resultCode, data);
        }

    }

    private void handlePicture(int resultCode) {
        ImageView img = (ImageView) this.findViewById(R.id.imageView1);

        if (resultCode == RESULT_OK) {
            // change picture in ImageView to image just taken

            // reduce size of image
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bmp = BitmapFactory.decodeFile(pictureLocation, options);
            img.setImageBitmap(bmp);

            Toast.makeText(this, "Photo saved to: "
                    + outputFileUri.toString(), Toast.LENGTH_LONG).show();

            Log.d(TAG, "Photo saved to: " + outputFileUri.toString());

            // increment the picture number, so next picture saved as different file
            imageNumber++;
        } else if (resultCode == RESULT_CANCELED) {
            Bitmap onPictureImage
                    = BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_picture);
            img.setImageBitmap(onPictureImage);
        }

        //		File file = new File(fileName);
//		if(file.exists())
//			Log.d(TAG, "file exists: " + file);
//		else
//			Log.d(TAG, "file does not exist: " + file);
    }

    private void showName(int resultCode, Intent intent) {
        String name = intent.getStringExtra(NAME_DATA);
        Log.d(TAG, "name is " + name);
        if(resultCode == RESULT_OK) {
            Toast.makeText(this, intent.getStringExtra(NAME_DATA)
                    + " just typed in their name!", Toast.LENGTH_LONG).show();
        }
    }


    public void takePhoto(View v) {
        // create directory if necessary
        File photoDir
                = new File(Environment.getExternalStorageDirectory()
                + "/intentExamplePhotos/");

        if (photoDir.mkdirs())
            Log.d(TAG, "mkdirs returned true: " + photoDir);
        else
            Log.d(TAG, "mkdirs returned false: " + photoDir);

        // create Intent to take picture via cameras and specify location
        // to store image so we can retrieve easily
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pictureLocation = fileName + imageNumber + extension;
        File file = new File(pictureLocation);
        outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {

            Toast.makeText(this,
                    getString(R.string.taking_picture_toast_message),
                    Toast.LENGTH_LONG).show();

            startActivityForResult(intent, TAKE_PICTURE_CODE);
        } else {
            Toast.makeText(this,
                    getString(R.string.no_camera_toast_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void getName(View view) {
        Intent intent = new Intent(this, GetName.class);
        intent.putExtra(HINT, "Type Your Name Please!");
        startActivityForResult(intent, GET_NAME_REQUEST);
    }
}