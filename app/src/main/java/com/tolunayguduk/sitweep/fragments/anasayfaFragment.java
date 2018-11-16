package com.tolunayguduk.sitweep.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tolunayguduk.sitweep.R;
import com.tolunayguduk.sitweep.functions.AudioAdapter;
import com.tolunayguduk.sitweep.model.Audio;

import java.util.ArrayList;

public class anasayfaFragment extends Fragment {

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    //END FIREBASE VARIABLES ######################################################
    private SwipeRefreshLayout refresh;
    private ArrayList<Audio> arrayList;
    private AudioAdapter adapter;
    private ListView recycler;

    private void initComponent(View view) {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("audio");
        myRef = database.getReference("audio");
        //END FIREBASE CONFIG ######################################################
        refresh = view.findViewById(R.id.refreshGlobal);
        recyclerBinder(view);
        //END VIEW CONFIG ######################################################
    }
    private void recyclerBinder(View view) {
        refresh.setRefreshing(true);
        recycler = view.findViewById(R.id.recyclerView);
        arrayList = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Audio audio = snapshot.getValue(Audio.class);
                    arrayList.add(audio);
                }
                adapter = new AudioAdapter(getContext(), R.layout.anonymus_play_list_item_layout, arrayList);
                recycler.setAdapter(adapter);
                refresh.setRefreshing(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                refresh.setRefreshing(false);
            }
        });
    }


    public anasayfaFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.anasayfa_fragment, container, false);
        initComponent(view);
        return view;
    }



}