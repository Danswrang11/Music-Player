package com.example.myplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        runtimePermission();
    }
        public void runtimePermission()
        {
            // prompt permission for user permission (dexter library)
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            displaySongs();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    })
                    .check();
        }
    //directory for arraylist and return method for file
    public ArrayList<File> fetchSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File [] songs = file.listFiles();   //list the file
        if(songs !=null)
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".wav")){
                        arrayList.add(myFile);
                    }
                }
            }
        return arrayList;
    }
    void displaySongs(){
        //fetching songs form the external storage to arraylist
        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for( int i = 0;i<mySongs.size();i++)
        {
            items[i] = mySongs.get(i).getName().replace( ".mp3", "");
        }
        customAdapter customAdapter= new customAdapter();
        listView.setAdapter(customAdapter);

        // By clicking list of music it will play the song
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentSong = (String) listView.getItemAtPosition(position);
                startActivity(new Intent(getApplicationContext(), PlaySong.class)
                .putExtra( "songList", mySongs)
                .putExtra("currentSong", currentSong)
                .putExtra("position", position));
            }
        });
    }
    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = myView.findViewById(R.id.songname);
            textsong.setSelected(true);
            textsong.setText(items[position]);
            return  myView;
        }
    }
}
