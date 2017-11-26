package shafin.visionlauncher;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class MyService extends Service {
    TextToSpeech textToSpeech;
    @Override
    public void onCreate() {
        super.onCreate();
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    public class MyReceiver extends BroadcastReceiver {
        private boolean screenOff;
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
           // Log.i("[BroadcastReceiver]", "MyReceiver");

            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                screenOff = false;

                try {
                    textToSpeech = new TextToSpeech(MyService.this, new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i == TextToSpeech.SUCCESS) {
                                textToSpeech.speak("Screen is on", TextToSpeech.QUEUE_FLUSH, null);
                            } else
                                Toast.makeText(getApplicationContext(), "Feature not supported by your device", Toast.LENGTH_LONG).show();

                        }
                    });

                }
                catch (Exception e){}


            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){

                screenOff = true;
                textToSpeech=new TextToSpeech(MyService.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i==TextToSpeech.SUCCESS){
                            textToSpeech.speak("Screen is off",TextToSpeech.QUEUE_FLUSH,null);
                        }
                        else Toast.makeText(getApplicationContext(),"Feature not supported by your device",Toast.LENGTH_LONG).show();

                    }
                });


            }
            Intent i = new Intent(context, MyService.class);
            i.putExtra("screen_state", screenOff);

            context.startService(i);
        }
    }}