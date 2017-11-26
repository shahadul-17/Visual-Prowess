package shafin.vision;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MapsStartPage extends Setting {
    public static final String EXTRA_MESSAGE ="shafin.vision" ;
    Button button;
    EditText editText;
    String markerName;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_start_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        button=(Button)findViewById(R.id.button);
        editText=(EditText)findViewById(R.id.editText);
        name=(TextView)findViewById(R.id.nameview);
       // Toast.makeText(getApplication(), getName(), Toast.LENGTH_LONG).show();
        if (getName().length()!=0){
        name.setText(getName());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });
    }

    public void sendMessage(){

        if (getName().length()!=0){
            Intent intent=new Intent(this,MapsActivity.class);
            markerName=getName();
            intent.putExtra(EXTRA_MESSAGE, markerName);
            startActivity(intent);
        }
        else {
            Intent intent=new Intent(this,MapsActivity.class);
            markerName=editText.getText().toString();
            if (markerName.length()==0){
                Toast.makeText(getApplication(), "Please enter a name", Toast.LENGTH_LONG).show();
            }
            else {
                intent.putExtra(EXTRA_MESSAGE, markerName);
                startActivity(intent);
            }
        }




    }





}

