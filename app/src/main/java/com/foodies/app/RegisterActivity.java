package com.foodies.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private MaterialEditText register_email,register_password,register_username,register_phone;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersRef;
    private CardView register_btn;
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
        setContentView(R.layout.activity_regsiter);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_username = findViewById(R.id.register_username);
        register_phone = findViewById(R.id.register_phone);
        register_btn = findViewById(R.id.register_btn);
        mUsersRef = FirebaseDatabase.getInstance().getReference("Users");
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (register_email.getText().toString().isEmpty() ||
                register_password.getText().toString().isEmpty()||
                register_phone.getText().toString().isEmpty()||
                register_username.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Veuillez entrer les détails de connexion", Toast.LENGTH_LONG).show();
                }else {
                    pattern = Pattern.compile(pattern_list);
                    matcher = pattern.matcher(register_email.getText().toString());
                    if (matcher.matches()){
                        mAuth.createUserWithEmailAndPassword(register_email.getText().toString(),
                                register_password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                HashMap<String,Object> register_hash = new HashMap<>();
                                register_hash.put("email",register_email.getText().toString());
                                register_hash.put("password",register_password.getText().toString());
                                register_hash.put("username",register_username.getText().toString());
                                register_hash.put("mobile",register_phone.getText().toString());
                                register_hash.put("time",System.currentTimeMillis());
                                register_hash.put("uid",mAuth.getCurrentUser().getUid());
                                register_hash.put("access","client");
                                register_hash.put("profile_picture","https://ui-avatars.com/api/?name="+register_username.getText().toString()+"&color=FC4041&background=fff&size=1024");
                                mUsersRef.child(mAuth.getCurrentUser().getUid()).setValue(register_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        Toast.makeText(RegisterActivity.this, "Compte créé Vous pouvez accéder aux fonctionnalités maintenant", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }else {
                        Toast.makeText(RegisterActivity.this, "Veuillez saisir un e-mail valide", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //hide actionbar
        getSupportActionBar().hide();
    }
}
