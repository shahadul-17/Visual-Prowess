package shafin.vision;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    TextView name ;
    TextView ipAddress;
    TextView raspberryPiId;


    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String IpAddress = "ipKey";
    public static final String RaspberryPiId = "raspberryPiIdKey";
    public static String host;


    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        name = (TextView) findViewById(R.id.editTextName);
        ipAddress = (TextView) findViewById(R.id.IpAddress);
        raspberryPiId = (TextView) findViewById(R.id.RaspberryPiID);
        //sms = (TextView) findViewById(R.id.editTextSms);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name))
        {
            name.setText(sharedpreferences.getString(Name, ""));

        }
        if (sharedpreferences.contains(IpAddress))
        {
            ipAddress.setText(sharedpreferences.getString(IpAddress, ""));

        }
        if (sharedpreferences.contains(RaspberryPiId))
        {
            raspberryPiId.setText(sharedpreferences.getString(RaspberryPiId, ""));

        }


        String ph  = ipAddress.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(IpAddress, ph);
        host=ph;

    }

    public void run(View view){
        String n  = name.getText().toString();
        String ph  = ipAddress.getText().toString();
        String e  = raspberryPiId.getText().toString();


        Editor editor = sharedpreferences.edit();
        editor.putString(Name, n);
        editor.putString(IpAddress, ph);
        editor.putString(RaspberryPiId, e);


        editor.commit();

    }

    public String getName(){
        String n  = name.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(Name, n);
        return n;

    }
    public String getIpAddress(){
        String ph  = ipAddress.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(IpAddress, ph);
        return ph;

    }
    public String getEmail(){
        String e  = raspberryPiId.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(RaspberryPiId, e);
        return e;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



}


