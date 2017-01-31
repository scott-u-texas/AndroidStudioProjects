package scottm.examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
        // in onCreate for Activity
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
}