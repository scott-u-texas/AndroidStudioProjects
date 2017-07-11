package edu.utexas.scottm.fragmentexample;

/**
 * Created by scottm on 4/12/2017.
 */

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * This is a secondary activity, to show what the user has selected
 * when the screen is not large enough to show it all in one activity.
 */

public class DetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            FragmentLayout.DetailsFragment details = new FragmentLayout.DetailsFragment();
            details.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
}

