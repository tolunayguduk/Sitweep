package com.tolunayguduk.sitweep.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tolunayguduk.sitweep.R;
import com.tolunayguduk.sitweep.model.User;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button birthday,register;
    private RadioGroup rG;;
    private RadioButton rB;
    ProgressBar progressBar;
    TextInputLayout emailWrapper, passwordWrapper,nameWrapper,surnameWrapper,passwordAgainWrapper;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseDatabase database;
    DatabaseReference myRef;
    User user;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authControl();
        initComponent();
        registered();
    }
    @Override
    protected void onResume() {
        authControl();
        super.onResume();
    }
    private void authControl() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(RegisterActivity.this,TimeLineActivity.class);
            startActivity(i);
        }
    }

    private void registered() {
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);//Güncel Yılı alıyoruz
                int month = mcurrentTime.get(Calendar.MONTH);//Güncel Ayı alıyoruz
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);//Güncel Günü alıyoruz

                DatePickerDialog datePicker;//Datepicker objemiz
                datePicker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        birthday.setText( dayOfMonth + "/" + monthOfYear+ "/"+year);
                        user.setBirthday(new Date(dayOfMonth,monthOfYear,year));//Ayarla butonu tıklandığında textview'a yazdırıyoruz

                    }
                },year,month,day);//başlarken set edilcek değerlerimizi atıyoruz
                datePicker.setTitle("Doğum Tarihinizi Seçiniz");
                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

                datePicker.show();

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rG.getCheckedRadioButtonId();
                rB= (RadioButton) findViewById(selectedId);

                if (!validateEmail(emailWrapper.getEditText().getText().toString())) {
                    emailWrapper.setError("Not a valid email address!");
                } else if (!validatePassword(passwordWrapper.getEditText().getText().toString())) {
                    passwordWrapper.setError("Not a valid password!");
                } else if (!validatePasswordAgain(passwordWrapper.getEditText().getText().toString())) {
                    passwordAgainWrapper.setError("Passwords didn't match!");
                } else if (!validateNameAndSurname(nameWrapper.getEditText().getText().toString())) {
                    nameWrapper.setError("Not a valid name!");
                } else if (!validateNameAndSurname(surnameWrapper.getEditText().getText().toString())) {
                    surnameWrapper.setError("Not a valid surname!");
                }else if (rB==null || rB.getText().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Registration failed");
                    builder.setMessage("Sex can't pass null!");
                    builder.setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Tamam butonuna basılınca yapılacaklar
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                    builder.show();
                } else {
                    emailWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mAuth.createUserWithEmailAndPassword(emailWrapper.getEditText().getText().toString(), passwordWrapper.getEditText().getText().toString())
                                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    user.setName(nameWrapper.getEditText().getText().toString());
                                                    user.setSurname(surnameWrapper.getEditText().getText().toString());
                                                    user.setEmail(mAuth.getCurrentUser().getEmail());
                                                    user.setPassword(passwordWrapper.getEditText().getText().toString());
                                                    user.setSex(rB.getText().toString());
                                                    myRef.child(mAuth.getCurrentUser().getUid()).setValue(user);

                                                    Intent i = new Intent(RegisterActivity.this,TimeLineActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                    builder.setTitle("Registration failed");
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
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    private void initComponent() {
        birthday = (Button) findViewById(R.id.birthday);
        register = (Button) findViewById(R.id.register);
        rG= (RadioGroup) findViewById(R.id.radioSex);
        progressBar = findViewById(R.id.progressBar3);

        nameWrapper=  findViewById(R.id.nameWrapper);
        surnameWrapper=  findViewById(R.id.surnameWrapper);
        emailWrapper=  findViewById(R.id.emailWrapper);
        passwordWrapper=  findViewById(R.id.passwordWrapper);
        progressBar.setVisibility(View.INVISIBLE);
        passwordAgainWrapper = findViewById(R.id.passwordAgainWrapper);

        user = new User();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("kullanici");
    }

    public boolean validatePasswordAgain(String data) {
        return passwordAgainWrapper.getEditText().getText().toString().equals(data.toString());
    }
    public boolean validateNameAndSurname(String data) {
        return data.length() > 1;
    }
    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean validatePassword(String password) {
        return password.length() > 5;
    }
}
