package com.tolunayguduk.sitweep.functions;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tolunayguduk.sitweep.R;
import com.tolunayguduk.sitweep.model.Audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;

public class AudioAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Audio> arrayList;
    Audio audio;
    private Boolean flag = true;
    Thread thread;

    public AudioAdapter(Context context, int layout, ArrayList<Audio> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
        audio = new Audio(context);
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder {
        TextView txtName, txtDate;
        ImageView ivPlay, ivStop;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layout, null);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            viewHolder.ivPlay = (ImageView) convertView.findViewById(R.id.play);
            viewHolder.ivStop = (ImageView) convertView.findViewById(R.id.pause);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Audio music = arrayList.get(position);
        viewHolder.txtName.setText(music.getName());
        viewHolder.txtDate.setText(String.valueOf(music.getDate()));
        // play music
        viewHolder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(flag){
                            viewHolder.ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                            try{
                                URL url = new URL(music.getDownloadURL());
                                URLConnection connection = url.openConnection();
                                InputStream in = connection.getInputStream();
                                FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "rec.pcm"));
                                byte[] buf = new byte[512];
                                while (true) {
                                    int len = in.read(buf);
                                    if (len == -1) {
                                        break;
                                    }
                                    fos.write(buf, 0, len);
                                }
                                in.close();
                                fos.flush();
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            audio.playRecord(44100,new File(Environment.getExternalStorageDirectory(), "rec.pcm"));
                            flag = true;
                            viewHolder.ivPlay.setImageResource(android.R.drawable.ic_media_play);
                        }
                        if(!flag) {
                            flag = true;
                            viewHolder.ivPlay.setImageResource(android.R.drawable.ic_media_play);
                        }
                    }
                });
                thread.start();

            }
        });
        // stop
        viewHolder.ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    //mediaPlayer.stop();
                    //mediaPlayer.release();
                    flag = true;
                }
                viewHolder.ivPlay.setImageResource(android.R.drawable.ic_media_play);
            }
        });
        return convertView;
    }
}
