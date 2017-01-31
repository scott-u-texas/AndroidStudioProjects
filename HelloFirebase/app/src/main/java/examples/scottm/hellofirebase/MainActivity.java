package examples.scottm.hellofirebase;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testFirebase();
    }

    private void testFirebase() {
        // Write a message to the database
        FirebaseDatabase database
                    = FirebaseDatabase.getInstance();
        DatabaseReference myRef
                = database.getReference("Class message");

        myRef.setValue("Hello, Firebase AGAIN!!!!!!!!");
    }
}
