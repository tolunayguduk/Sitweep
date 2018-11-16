package com.tolunayguduk.sitweep.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tolunayguduk.sitweep.R;

import java.sql.Time;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextView kayitOl,forgotPassword;
    Button login;
    TextInputLayout emailWrapper, passwordWrapper;
    ProgressBar progressBar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authControl();
        initComponent();
        regestered();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    @Override
    protected void onResume() {
        authControl();
        super.onResume();
    }

    private void initComponent() {
        kayitOl = (TextView) findViewById(R.id.kayitOl);
        forgotPassword= (TextView) findViewById(R.id.forgotPassword);
        login = (Button) findViewById(R.id.login);

        emailWrapper =  findViewById(R.id.emailWrapper);
        passwordWrapper =  findViewById(R.id.passwordWrapper);

        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void authControl() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(LoginActivity.this,TimeLineActivity.class);
            startActivity(i);
        }
    }

    private void regestered() {
        kayitOl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail(emailWrapper.getEditText().getText().toString())) {
                    emailWrapper.setError("Not a valid email address!");
                } else if (!validatePassword(passwordWrapper.getEditText().getText().toString())) {
                    passwordWrapper.setError("Not a valid password!");
                } else {
                    emailWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);


                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                mAuth.signInWithEmailAndPassword(emailWrapper.getEditText().getText().toString(), passwordWrapper.getEditText().getText().toString())
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Intent i = new Intent(LoginActivity.this,TimeLineActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                                    builder.setTitle("Authentication failed");
                                                    builder.setMessage(task.getException().getMessage());
                                                    builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            //Tamam butonuna basılınca yapılacaklar
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            }
                                        });

                            }catch (Exception e) {

                            }
                        }
                    });
                    thread.start();




                }
            }
        });
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean validatePassword(String password) {
        return password.length() > 5;
    }
}
