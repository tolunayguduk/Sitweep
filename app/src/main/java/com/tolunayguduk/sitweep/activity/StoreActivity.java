package com.tolunayguduk.sitweep.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tolunayguduk.sitweep.R;

public class StoreActivity extends AppCompatActivity {

    Button button;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        authControl();
        initComponent();
        registered();


    }

    private void authControl() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent i = new Intent(StoreActivity.this,LoginActivity.class);
            startActivity(i);
        }
    }

    private void initComponent() {
        button = findViewById(R.id.button2);
    }

    private void registered() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent i = new Intent(StoreActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
}
