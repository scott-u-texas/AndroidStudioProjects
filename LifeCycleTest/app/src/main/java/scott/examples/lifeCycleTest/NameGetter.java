package scott.examples.lifeCycleTest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class NameGetter extends Activity {
	
	private static final String TAG = "NAME_GETTER: ";
	public static final String NAME = "NAME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.getter);
        Log.d(TAG,"OnCreate ");
        createEditTextActionListener();
    }
    
    
    private void createEditTextActionListener() {
        EditText editText = (EditText) findViewById(R.id.enterNameEditText);
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "in on editor action");
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    done(null);
                    handled = true;
                }
                return handled;
            }
        });
    }



    public void done(View v) {
    	EditText et = (EditText)findViewById(R.id.enterNameEditText);
    	Log.d(TAG, et.getText().toString());
    	Intent result = new Intent();
    	result.putExtra(NAME, et.getText().toString());
    	setResult(LifeCycleTestActivity.GET_NAME, result);
    	finish();
    }
}
