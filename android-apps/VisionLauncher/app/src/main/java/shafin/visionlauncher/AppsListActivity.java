package shafin.visionlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppsListActivity extends AppCompatActivity {


    private PackageManager manager;
    private List<Item>apps;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);
        loadApps();
        loadListView();
        addClickListener();
    }
    private void loadApps(){

       manager=getPackageManager();
        apps=new ArrayList<>();
        Intent i=new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivties=manager.queryIntentActivities(i,0);
        for(ResolveInfo r1: availableActivties){
            Item app=new Item();
            app.label=r1.activityInfo.packageName;
            app.name=r1.loadLabel(manager);
            app.icon=r1.loadIcon(manager);
            apps.add(app);
        }
    }

    private void loadListView(){
        list=(ListView)findViewById(R.id.list);
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this,R.layout.item,apps){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView==null){
                    convertView=getLayoutInflater().inflate(R.layout.item,null);
                }
                ImageView appIcon=(ImageView)convertView.findViewById(R.id.icon);
                appIcon.setImageDrawable(apps.get(position).icon);

                TextView appName=(TextView)convertView.findViewById(R.id.name);
                appName.setText(apps.get(position).name );

                return convertView;
            }
        };

        list.setAdapter(adapter);

    }
    private void  addClickListener  (){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i =manager.getLaunchIntentForPackage(apps.get(position).label.toString());
                startActivity(i);
            }
        });
    }
}
