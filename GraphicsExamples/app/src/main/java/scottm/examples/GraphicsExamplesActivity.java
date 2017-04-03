package scottm.examples;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class GraphicsExamplesActivity extends Activity implements View.OnClickListener {

    // private GraphicsView gv;
    private BalloonView bv;
    private AnimationLoop animator;
    private final int FPS = 50;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bv = (BalloonView) findViewById(R.id.graphics_view);
        animator = new AnimationLoop(bv, FPS);
        Log.d("GraphicsView", "gv object: " + bv);
        bv.setOnClickListener(this);
    }

    public void onResume() {
        super.onResume();
        if (!animator.isRunning()) {
            animator.start();
        }
    }

    @Override
    public void onClick(View view) {
        // need to respond to clicks, not touches??
        Log.d("GraphicsView", "onClick called.");
        Log.d("GraphicsView", "animator is running: " + animator.isRunning());
        if (animator.isRunning()) {
            animator.stop();
        } else {
            animator.start();
        }
    }

    public void onPause() {
        super.onPause();
        if (animator.isRunning()) {
            animator.stop();
        }
    }

    public void onStop() {
        super.onStop();
        if (animator.isRunning()) {
            animator.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.bad_animation_activity:
                Intent intent = new Intent(this, BadAnimationActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}