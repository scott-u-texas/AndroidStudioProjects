package scottm.examples;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by scottm on 3/27/2017.
 */
public class BadAnimationActivity extends Activity {

    private TextView count;
    private TextView time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bad_anim);
        count = (TextView) findViewById(R.id.num_redraw);
        time = (TextView) findViewById(R.id.system_time);
    }

    public static class FakeView extends View {

        private static int onDrawCount;

        public FakeView (Context context, AttributeSet attrs) {
            super(context, attrs);
        }


        public void onDraw(Canvas c) {
            onDrawCount++;
        }

    }
}
