package shafin.visionlauncher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    TextView name ;
    TextView phone;
    TextView email;
    TextView sms;

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Phone = "phoneKey";
    public static final String Email = "emailKey";
    public static final String Sms = "smsKey";


    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        name = (TextView) findViewById(R.id.editTextName);
        phone = (TextView) findViewById(R.id.editTextPhone);
        email = (TextView) findViewById(R.id.editTextEmail);
        sms = (TextView) findViewById(R.id.editTextSms);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(Name))
        {
            name.setText(sharedpreferences.getString(Name, ""));

        }
        if (sharedpreferences.contains(Phone))
        {
            phone.setText(sharedpreferences.getString(Phone, ""));

        }
        if (sharedpreferences.contains(Email))
        {
            email.setText(sharedpreferences.getString(Email, ""));

        }
        if (sharedpreferences.contains(Sms))
        {
            sms.setText(sharedpreferences.getString(Sms, ""));

        }



    }

    public void run(View view){
        String n  = name.getText().toString();
        String ph  = phone.getText().toString();
        String e  = email.getText().toString();
        String s  = sms.getText().toString();

        Editor editor = sharedpreferences.edit();
        editor.putString(Name, n);
        editor.putString(Phone, ph);
        editor.putString(Email, e);
        editor.putString(Sms, s);

        editor.commit();

    }

    public String getName(){
        String n  = name.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(Name, n);
        return n;

    }
    public String getPhone(){
        String ph  = phone.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(Phone, ph);
        return ph;

    }
    public String getEmail(){
        String e  = email.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(Email, e);
        return e;

    }
    public String getSms(){
        String s  = sms.getText().toString();
        Editor editor = sharedpreferences.edit();
        editor.putString(Sms, s);
        return s;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}


