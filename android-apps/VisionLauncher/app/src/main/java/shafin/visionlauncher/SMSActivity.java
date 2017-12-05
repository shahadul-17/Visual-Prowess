package shafin.visionlauncher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
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

import java.util.List;
import java.util.Locale;

import static android.view.GestureDetector.*;

public class SMSActivity extends SettingActivity implements OnGestureListener,GestureDetector.OnDoubleTapListener,LocationListener {

    View view;
    TextView textView,textView2;
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;

    TextView locationText;
    double l;
    double n;
    String a;
    LocationManager locationManager;
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
        textView2=(TextView)findViewById(R.id.textView2);
        gestureDetectorCompat=new GestureDetectorCompat(this,this);
        gestureDetectorCompat.setOnDoubleTapListener(this);
       textView.setText("Emergency SMS Support");
       textView2.setText("welcome to vision emergency sms support.  \n" +
               "Double tap to send sms. \n" +
               "Swipe from right to go back to main menu.  \n");

        textToSpeech=new TextToSpeech(SMSActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS){
                    textToSpeech.speak(
                            "welcome to vision emergency sms support.  " +
                            "Double tap to send sms " +
                            "Swipe from right to go back to main menu" ,TextToSpeech.QUEUE_FLUSH,null);
                }
                else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();

            }
        });

        locationText = (TextView)findViewById(R.id.locationText);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        getLocation();






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



            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);


                String message = "My Location is : ("+"http://www.google.com/maps/place/" + l +","+ n+")";
                String phoneNo = getPhone();
                //Toast.makeText(getApplicationContext(), "This is my Toast message!\n"+a, Toast.LENGTH_LONG).show();

                if(!TextUtils.isEmpty(message) && !TextUtils.isEmpty(phoneNo)) {

                    if(checkPermission(Manifest.permission.SEND_SMS)) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, getName()+","+getSms()+"\n"+message, null, null);
                        Toast.makeText(SMSActivity.this, "SMS sent", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(SMSActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }







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


    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                   // mSendMessageBtn.setEnabled(true);
                }
                return;
            }
        }
    }






    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        l=location.getLatitude();
        n=location.getLongitude();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
            a=locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2);
        }catch(Exception e)
        {

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(SMSActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
