package com.tolunayguduk.sitweep.model;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Audio {
    private String name;
    private String singer;
    private String downloadURL;
    private Uri song;
    private long date;
    private Context context;
    public Boolean recording=true;
    //END OTHER VARIABLES ######################################################
    public Audio(Context context){
        this.context = context;
    }
    public Audio(String name, String singer, Uri song) {
        this.name = name;
        this.singer = singer;
        this.song = song;
    }
    public Audio(){}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSinger() {
        return singer;
    }
    public void setSinger(String singer) {
        this.singer = singer;
    }
    public Uri getSong() {
        return song;
    }
    public void setSong(Uri song) {
        this.song = song;
    }
    public String getDownloadURL() {
        return downloadURL;
    }
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }
    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }

    public void startRecord(int freq, File file){

        try {
            file.createNewFile();
            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(freq,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            short[] audioData = new short[minBufferSize];
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    freq,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);
            audioRecord.startRecording();
            while(recording){
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numberOfShort; i++){
                    dataOutputStream.writeShort(audioData[i]);
                }
            }

            audioRecord.stop();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void playRecord(int freq, File file){
        int shortSizeInBytes = Short.SIZE/Byte.SIZE;
        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            int i = 0;
            while(dataInputStream.available() > 0){
                audioData[i] = dataInputStream.readShort();
                i++;
            }
            dataInputStream.close();
            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    freq,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public File returnAudioFile(){
        return new File(Environment.getExternalStorageDirectory(), "rec.pcm");
    }



}
