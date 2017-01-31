package scottm.examples.guessfour;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class About extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		View doneButton = findViewById(R.id.about_done_button);
		doneButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		finish();
	}
	
}
