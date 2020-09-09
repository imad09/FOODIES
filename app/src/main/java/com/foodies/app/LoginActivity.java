package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    //Login Section
    private MaterialEditText login_email,login_password;
    private FirebaseAuth mAuth;
    private CardView login_click_btn;
    private DatabaseReference mLoginDateRef;
    public Pattern pattern;
    public Matcher matcher;
    public String pattern_list = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);
        //register btn
        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_click_btn = findViewById(R.id.login_click_btn);
        login_click_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_email.getText().toString().isEmpty() || login_password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, "Veuillez entrer les détails de connexion", Toast.LENGTH_LONG).show();
                }else {
                    pattern = Pattern.compile(pattern_list);
                    matcher = pattern.matcher(login_email.getText().toString());
                    if(matcher.matches()){
                        mAuth.signInWithEmailAndPassword(login_email.getText().toString(),
                                login_password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                finish();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Il y a un problème de connexion", Toast.LENGTH_LONG).show();
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                Toast.makeText(LoginActivity.this, "Processus de connexion annulé", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else
                        Toast.makeText(LoginActivity.this, "Veuillez saisir un e-mail valide", Toast.LENGTH_LONG).show();


                }
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().hide();
    }
}
