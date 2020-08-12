package com.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    private static final String[] PERMISSIONS= {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PERMISSIONS= 12345;
    private static final int PERMISSION_COUNT=1;

    @SuppressLint("NewApi")
    private boolean arePermissionDenied() {
        for (int i = 0; i < PERMISSION_COUNT; i++) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(arePermissionDenied()){
            ((ActivityManager)(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            recreate();
        }else{
            onResume();
        }
    }

    private boolean isMusicPlayerInit;
    private List<String> musicFileList;

    private void addMusicFilesFrom(String dirPath){
        final File musicDir= new File(dirPath);
        if(!musicDir.exists()){
            musicDir.mkdir();
            return;
        }
        final File[] files= musicDir.listFiles();
        for (File file: files){
            final String path= file.getAbsolutePath();
            if(path.endsWith(".mp3")){
                musicFileList.add(path);
            }
        }
    }

    private void fileMusicList(){
        musicFileList.clear();
        addMusicFilesFrom(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment
                        .DIRECTORY_MUSIC)));

        addMusicFilesFrom(String.valueOf(Environment.
                getExternalStoragePublicDirectory(Environment
                .DIRECTORY_DOWNLOADS)));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && arePermissionDenied()){
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        }
        if(!isMusicPlayerInit){
            final ListView listView= findViewById(R.id.listView);
            final TextAdapter textAdapter= new TextAdapter();
            musicFileList= new ArrayList<>();
            fileMusicList();
            textAdapter.setData(musicFileList);
            listView.setAdapter(textAdapter);

            isMusicPlayerInit=true;
        }
    }

    class TextAdapter extends BaseAdapter{
        private List<String> data= new ArrayList<>();
        void setData(List<String> mData){
            data.clear();
            data.addAll(mData);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView== null){
                convertView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.model, parent, false);
                convertView.setTag(new ViewHolder((TextView)convertView.findViewById(R.id.Item)));
            }
            ViewHolder holder= (ViewHolder)convertView.getTag();
            final String item= data.get(position);
            holder.info.setText(item.substring(item.lastIndexOf('/')+1));
            return convertView;
        }
    }

    class ViewHolder{
        TextView info;
        ViewHolder(TextView mInfo){
            info= mInfo;
        }
    }
}