package shafin.visionlauncher;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.gesture.Gesture;
import static android.view.GestureDetector.*;

public class SMSActivity extends SettingActivity implements OnGestureListener,GestureDetector.OnDoubleTapListener  {

    View view;
    TextView textView;

    public GestureDetectorCompat gestureDetectorCompat;

    TextToSpeech textToSpeech;


    private final int GESTURE_THRESHOULD=100;
    private final int GESTURE_VELOCITY_THRESHOULD=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        textView=(TextView)findViewById(R.id.textView);
        gestureDetectorCompat=new GestureDetectorCompat(this,this);
        gestureDetectorCompat.setOnDoubleTapListener(this);
        textView.setText("Emergency Support");

        textToSpeech=new TextToSpeech(SMSActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    textToSpeech.speak("welcome to vision emergency sms support.  " +
                            "Double tap to send sms " +
                            "Swipe from right to go back to main menu" ,TextToSpeech.QUEUE_FLUSH,null);
                }
                else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();

            }
        });


    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {


        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent event1,MotionEvent event2,float velocityx,float velocityy) {
        try
        {
            float diffx=event2.getX()-event1.getX();
            float diffy=event2.getY()-event1.getY();

            if(Math.abs(diffx)>Math.abs(diffy))
            {
                if(Math.abs(diffx)>GESTURE_THRESHOULD && Math.abs(velocityx)>GESTURE_VELOCITY_THRESHOULD)
                {
                    if(diffx>0)
                    {
                        onSwipeRight();
                    }
                    else
                    {
                        onSwipeLeft();
                    }
                }
            }
            else
            {
                if(Math.abs(diffy)>GESTURE_THRESHOULD && Math.abs(velocityy)>GESTURE_VELOCITY_THRESHOULD)
                {
                    if(diffy>0)
                    {
                        onSwipeBottom();
                    }
                    else
                    {
                        onSwipeTop();
                    }
                }
            }
        }
        catch(Exception e)
        {
            // Log.d(Tag, ""+e.getMessage());
        }
        return false;
    }
    public void onSwipeRight()
    {
        textView.setText("swipe right");
        textToSpeech=new TextToSpeech(SMSActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    textToSpeech.speak("Back in main menu",TextToSpeech.QUEUE_FLUSH,null);
                }
                else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();

            }
        });
        Intent intent =new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

        // Log.i(Tag, "Right");
    }
    public void onSwipeLeft()
    {



    }

    public void onSwipeTop()
    {

    }

    public void onSwipeBottom()
    {


    }

}
