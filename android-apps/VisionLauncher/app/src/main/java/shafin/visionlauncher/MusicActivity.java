package shafin.visionlauncher;



import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.gesture.Gesture;
import static android.view.GestureDetector.*;

public class MusicActivity extends AppCompatActivity implements OnGestureListener,GestureDetector.OnDoubleTapListener {
    TextView textView,textView2;

    public GestureDetectorCompat gestureDetectorCompat;

    TextToSpeech textToSpeech;


    private final int GESTURE_THRESHOULD=100;
    private final int GESTURE_VELOCITY_THRESHOULD=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

       // Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();

        textView=(TextView)findViewById(R.id.textView);
        textView2=(TextView)findViewById(R.id.textView2);
        gestureDetectorCompat=new GestureDetectorCompat(this,this);
        gestureDetectorCompat.setOnDoubleTapListener(this);
        textView.setText("Music Support");
        textView2.setText("welcome to vision music support.   \n" +
                "Tap the screen to play or pause music. Swipe Top for next music. \n" +
                "Swipe down for previous music. \n" +
                "Swipe from right to go back to main menu. \n");



       textToSpeech=new TextToSpeech(MusicActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    textToSpeech.speak("welcome to vision music support.   " +
                            "Tap the screen to play or pause music. Swipe Top for next music." +
                            "Swipe down for previous music." +
                            "Swipe from right to go back to main menu.",TextToSpeech.QUEUE_FLUSH,null);
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
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
       // textView.setText("onSingleTapConfirmed");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
       // textView.setText("onDoubleTap");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        //textView.setText("onDoubleTapEvent");
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
       // textView.setText("onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

        //textView.setText("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        textView.setText("Play/Pause");

        long eventtime = SystemClock.uptimeMillis();
        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        sendOrderedBroadcast(downIntent, null);

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
       // textView.setText("onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        textView.setText("onLongPress");
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
        textToSpeech=new TextToSpeech(MusicActivity.this, new TextToSpeech.OnInitListener() {
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
       // Log.i(TAG, "Left");
       // textView.setText("swipe left");


    }

    public void onSwipeTop()
    {
        textView.setText("Next");

        long eventtime = SystemClock.uptimeMillis();
        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        sendOrderedBroadcast(downIntent, null);
    }

    public void onSwipeBottom()
    {
        textView.setText("Previous");

        long eventtime = SystemClock.uptimeMillis();
        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN,   KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        sendOrderedBroadcast(downIntent, null);

    }


}
