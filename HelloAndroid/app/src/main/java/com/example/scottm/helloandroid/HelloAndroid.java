package com.example.scottm.helloandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HelloAndroid extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_android);
    }

    public void changeMessage(View v) {
        Log.d("Hello Android", "The view object is " + v);
        TextView textView = this.findViewById(R.id.tv1);
        textView.setText("Are we done?????");
    }
}
