package scottm.cs378.example.intentexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class GetName extends Activity {

    private static final String TAG = "Get Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_name);
        createEditTextActionListener();
    }

    private void createEditTextActionListener() {
        EditText editText = (EditText) findViewById(R.id.typeName);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    public void done(View view) {
        EditText et = (EditText) this.findViewById(R.id.typeName);
        Intent result = new Intent();
        Log.d(TAG, et.getText().toString());
        result.putExtra(IntentExample.NAME_DATA, et.getText().toString());
        setResult(RESULT_OK, result);
        finish();
    }
}
