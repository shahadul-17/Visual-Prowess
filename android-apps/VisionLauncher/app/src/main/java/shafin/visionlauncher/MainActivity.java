package shafin.visionlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.gesture.Gesture;
import java.util.Calendar;
import java.util.Date;
import static android.view.GestureDetector.*;



public class MainActivity extends AppCompatActivity implements OnGestureListener,GestureDetector.OnDoubleTapListener {
    TextView textView,textView2,textView3;
    GestureDetectorCompat gestureDetectorCompat;
    TextToSpeech textToSpeech,tts;
    int counter = 0;
    String batterylevel;
    private String[] menuList = {"Music", "Services", "Main Menu","Setting", "Status","Call","Emergency SMS", "Video Call"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        textView=(TextView)findViewById(R.id.textView);
        textView2=(TextView)findViewById(R.id.textView2);
        textView3=(TextView)findViewById(R.id.textView3);
        textView.setText("Welcome to Vision Launcher. \n Swipe up and down to choose menu and double tap to select item.");
        gestureDetectorCompat=new GestureDetectorCompat(this,this);
        gestureDetectorCompat.setOnDoubleTapListener(this);

        Intent bindIntent = new Intent(this,MyService.class);//screen on of service
        startService(bindIntent);


        // Intent LocationIntent = new Intent(this,LocationService.class);//screen on of service
        //startService(LocationIntent);








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

        if (counter==0) {

            Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
            startActivity(intent);
        }
        else if (counter==1){
          //  Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
         //   startActivity(intent);
        }
        else if (counter==2){
           Intent intent = new Intent(getApplicationContext(), MainActivity.class);
           startActivity(intent);
        }
        else if (counter==3){

            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);

        }
        else if (counter==4){

            status();

        }
        else if (counter==5){

            Intent intent = new Intent(getApplicationContext(), CallActivity.class);
            startActivity(intent);

        }
        else if (counter==6){

            Intent intent = new Intent(getApplicationContext(), SMSActivity.class);
            startActivity(intent);

        }
        else if (counter==7){

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        }

        return false;

    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
       // textView.setText("onDoubleTapEvent");
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
       // textView.setText("onSingleTapUp");


        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
       // textView.setText("onScroll");

        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

        //textView.setText("onLongPress");
        Intent intent=new Intent(getApplicationContext(),AppsListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        textView2.setText("");
        textView3.setText("");
        counter++;

        if (counter >= menuList.length)
            counter = 0;
        textView.setText(menuList[counter]);
        textToSpeech=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    textToSpeech.speak(menuList[counter],TextToSpeech.QUEUE_FLUSH,null);
                }
                else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();

            }
        });


        return false;
    }




    public static class PowerUtil {
        public static boolean isConnected(Context context) {
            Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
        }
    }
    public static String batteryLevel(Context context)
    {
        Intent intent  = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int    level   = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int    scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int    percent = (level*100)/scale;
        return String.valueOf(percent) + "%";
    }
    public void battery(){

        batterylevel=batteryLevel(this);
        PowerUtil recevier= new PowerUtil();

        if (recevier.isConnected(this)==true) {

            textToSpeech=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i==TextToSpeech.SUCCESS){
                        Date currentTime = Calendar.getInstance().getTime();
                        textToSpeech.speak("Battery level is"+batterylevel+"and charger is connected",TextToSpeech.QUEUE_FLUSH,null);
                        textView3.setText("Battery level is"+batterylevel+"and charger is connected");
                    }
                    else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();

                }
            });
        }
        else {

            textToSpeech=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i==TextToSpeech.SUCCESS){
                        Date currentTime = Calendar.getInstance().getTime();
                        textToSpeech.speak("Battery level is"+batterylevel+"and charger is not connected",TextToSpeech.QUEUE_FLUSH,null);
                        textView3.setText("Battery level is"+batterylevel+"and charger is not connected");

                    }
                    else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();

                }
            });

        }

    }

    public void status(){
        textToSpeech=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    Date currentTime = Calendar.getInstance().getTime();
                    textToSpeech.speak("Today's date is :"+currentTime.toString(),TextToSpeech.QUEUE_FLUSH,null);
                    textView2.setText("Today's date is :"+currentTime.toString());
                   //Toast.makeText(getApplicationContext(),currentTime.toString(),Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();
                battery();
            }
        });
    }
}
