package kit.ce.ash.mobileproject;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 성현 on 2016-06-12.
 */

public class splashActivity extends Activity {

    Timer time = new Timer();

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Intent intent = new Intent(splashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        GifView view = (GifView)findViewById(R.id.gifView);

        view.loadGIFResource(this, R.drawable.sp);

        time.schedule(task, 2000);

    }
}