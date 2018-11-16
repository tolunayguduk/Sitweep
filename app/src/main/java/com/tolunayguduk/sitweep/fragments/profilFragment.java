package com.tolunayguduk.sitweep.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tolunayguduk.sitweep.R;
import com.tolunayguduk.sitweep.functions.AudioAdapter;
import com.tolunayguduk.sitweep.model.Audio;
import com.tolunayguduk.sitweep.model.Photo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import de.hdodenhof.circleimageview.CircleImageView;
import es.claucookie.miniequalizerlibrary.EqualizerView;

public class profilFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Query recyclerQuery;
    //END FIREBASE VARIABLES ######################################################
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout refresh;
    private CircleImageView circleImageView;
    View view;
    //END VIEW VARIABLES ######################################################
    private ArrayList<Audio> arrayList;
    private AudioAdapter adapter;
    private ListView recycler;
    private TextView postCount;
    EqualizerView equalizer;
    Audio audio;
    private static int RESULT_LOAD_IMG = 1;

    private void initComponent(View view) {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("audio");
        myRef = database.getReference("audio");
        recyclerQuery = myRef.orderByChild("singer").equalTo(mAuth.getUid());
        //END FIREBASE CONFIG ######################################################
        circleImageView = view.findViewById(R.id.profile_image);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        postCount = view.findViewById(R.id.postCount);
        refresh = view.findViewById(R.id.refresh);
        recyclerBinder(view);
        //END VIEW CONFIG ######################################################
        audio = new Audio(getContext());
        equalizer = (EqualizerView) view.findViewById(R.id.equalizer_view);
        equalizer.stopBars();
    }

    private void recyclerBinder(View view) {
        refresh.setRefreshing(true);
        recycler = view.findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();
        recyclerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Audio audio = snapshot.getValue(Audio.class);
                    arrayList.add(audio);
                }
                adapter = new AudioAdapter(getContext(), R.layout.play_list_item_layout, arrayList);
                recycler.setAdapter(adapter);
                postCount.setText(String.valueOf(arrayList.size()));
                refresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                refresh.setRefreshing(false);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void registered() {
        floatingActionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        equalizer.animateBars();
                        Thread recordThread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                audio.recording = true;
                                audio.startRecord(44100,new File(Environment.getExternalStorageDirectory(), "rec.pcm"));
                            }
                        });
                        recordThread.start();
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        equalizer.stopBars();
                        audio.recording = false;

                        final Snackbar snackbar = Snackbar.make(getView().getRootView(), "Kaydı dinle?", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Dinle", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                audio.playRecord(44100,new File(Environment.getExternalStorageDirectory(), "rec.pcm"));
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("UYARI!!");
                        builder.setMessage("Sesi kaydetmek istediğinize emin msiniz?");
                        builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Tamam butonuna basılınca yapılacaklar
                                final Date date = new Date();
                                final String child = mAuth.getCurrentUser().getUid() + "-" + date.getTime();
                                final StorageReference riversRef = mStorageRef.child(child);
                                riversRef.putFile(Uri.fromFile(audio.returnAudioFile()))
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Beklenmedik Bir Hata Oluştu");
                                                builder.setMessage(exception.getMessage());
                                                builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //Tamam butonuna basılınca yapılacaklar
                                                        equalizer.animateBars();
                                                    }
                                                });
                                                builder.show();
                                            }
                                        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }

                                        // Continue with the task to get the download URL
                                        return riversRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            equalizer.stopBars();
                                            Audio audio = new Audio();
                                            audio.setDownloadURL(task.getResult().toString());
                                            audio.setName("Yorum");
                                            audio.setDate(date.getTime());
                                            audio.setSinger(mAuth.getUid());
                                            myRef.child(child).setValue(audio);

                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Beklenmedik Bir Hata Oluştu");
                                            builder.setMessage("Lütfen yayımcıya bu hatayı bildiriniz");
                                            builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Tamam butonuna basılınca yapılacaklar
                                                    equalizer.stopBars();
                                                }
                                            });
                                            builder.show();
                                        }
                                    }
                                });
                            }
                        });
                        builder.show();
                        break;
                    }
                }
                return true;
            }
        });
    }
    //############################################################################################################
    public profilFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profil_fragment, container, false);
        // Inflate the layout for this fragment
        initComponent(view);
        registered();
        return view;
    }
    @Override
    public void onRefresh() {

    }
}
