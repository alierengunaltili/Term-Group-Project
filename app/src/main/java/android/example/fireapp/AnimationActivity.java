    package android.example.fireapp;

    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.os.Handler;
    import android.view.WindowManager;

    /**
     * Animation class for the entrance of the app.
     * @date 24.05.2020
     * @author Group_g3C
     */

    public class AnimationActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent( AnimationActivity.this, LogInActivity.class));
                    finish();
                }
            }, 5000);
        }
    }
