package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CommandSendActivity extends AppCompatActivity {
    private ImageView command_qr_code_image;
    private TextView command_order_number,card_order_number,command_order_date
            ,command_total_price;
    private DatabaseReference mCommandRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_command_send);
        /*        command_send_map = findViewById(R.id.command_send_map);*/
        command_qr_code_image = findViewById(R.id.command_qr_code_image);
/*        command_map_distance = findViewById(R.id.command_map_distance);
        command_map_duration = findViewById(R.id.command_map_duration);*/
        command_order_number = findViewById(R.id.command_order_number);
        card_order_number = findViewById(R.id.card_order_number);
        //lets Collect Order
        command_order_date = findViewById(R.id.command_order_date);
        command_total_price = findViewById(R.id.command_total_price);
        mCommandRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid())
                .child("Commandes");
        mCommandRef.child(String.valueOf(getIntent().getIntExtra("order_number",0))).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                command_order_number.setText("#"+dataSnapshot.child("order_number").getValue(Long.class));
                card_order_number.setText("#"+dataSnapshot.child("order_number").getValue(Long.class));
                card_order_number.setText("#"+dataSnapshot.child("order_number").getValue(Long.class));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date get_order_time = new Date(dataSnapshot.child("time").getValue(Long.class));
                command_order_date.setText(simpleDateFormat.format(get_order_time));
                command_total_price.setText(dataSnapshot.child("total_price").getValue(Long.class)+"DA");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Create Qr Code
        long code_number = getIntent().getIntExtra("order_number",0);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(String.valueOf(code_number),
                    BarcodeFormat.QR_CODE,100,100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap code = barcodeEncoder.createBitmap(bitMatrix);
            command_qr_code_image.setImageBitmap(code);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}