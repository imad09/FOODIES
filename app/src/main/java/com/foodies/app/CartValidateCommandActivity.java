package com.foodies.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Holders.CommandeHolder;
import com.foodies.app.Interfaces.RemoveCommand;
import com.foodies.app.Models.CartModel;
import com.foodies.app.Models.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartValidateCommandActivity extends AppCompatActivity {
    private TextView command_order_number, command_user_adresse;
    public static DatabaseReference mCommandRef, mUserCommand, mUserRef, mPharmaCardReference;
    private FirebaseAuth mAuth;
    public static FirebaseRecyclerAdapter<CartModel, CommandeHolder> command_adapter;
    private RecyclerView command_product_recycler;
    private MaterialEditText command_user_phone_number;
    private LinearLayout command_adress_layout;
    private CardView command_add_adresse_btn;
    private CardView cart_validate_btn;
    public static DatabaseReference mAdminCommandRef;
    private List<CartModel> cartModelList = new ArrayList<>();
    //pharmapass payment section
    private CardView pharmapass_pay_btn;
    private BottomSheetDialog pharma_pay_dialog;
    public static String user_card_number;
    private TextView bottom_pay_card_number, bottom_pay_products, bottom_pay_total_price;
    private CardView bottom_pay_btn;
    private List<String> emails;
    public String div;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_cart_validate_command);
        command_order_number = findViewById(R.id.command_order_number);
        command_add_adresse_btn = findViewById(R.id.command_add_adresse_btn);
        command_adress_layout = findViewById(R.id.command_adress_layout);
        command_user_adresse = findViewById(R.id.command_user_adresse);
        cart_validate_btn = findViewById(R.id.cart_validate_btn);
        command_order_number.setText("Command Numero #" + getIntent().getIntExtra("order_number", 0));
        mUserCommand = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Commandes")
                .child(String.valueOf(getIntent().getIntExtra("order_number", 0))).child("Products");
        Query mCommadQuery = mUserCommand.orderByKey();
        FirebaseRecyclerOptions mCommandeOption = new FirebaseRecyclerOptions.Builder<CartModel>().setQuery(mCommadQuery, CartModel.class).build();
        command_adapter = new FirebaseRecyclerAdapter<CartModel, CommandeHolder>(mCommandeOption) {
            @Override
            protected void onBindViewHolder(@NonNull CommandeHolder holder, int i, @NonNull CartModel model) {
                holder.push(model.getKey(), model.getQuantity());
            }

            @NonNull
            @Override
            public CommandeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View command_view = LayoutInflater.from(CartValidateCommandActivity.this).inflate(R.layout.row_command_layout, parent, false);
                return new CommandeHolder(command_view);
            }
        };
        command_product_recycler = findViewById(R.id.command_product_recycler);
        command_product_recycler.setAdapter(command_adapter);
        command_product_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        command_user_phone_number = findViewById(R.id.command_user_phone_number);
        mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                command_user_phone_number.setText(users.getMobile());
                if (dataSnapshot.child("Address").getValue(String.class) != null) {
                    command_user_adresse.setText(dataSnapshot.child("Address").getValue(String.class));
                    command_add_adresse_btn.setVisibility(View.GONE);
                    command_adress_layout.setVisibility(View.VISIBLE);
                } else {
                    command_add_adresse_btn.setVisibility(View.VISIBLE);
                    command_adress_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
        command_add_adresse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileAdresseActivity.class));
            }
        });
        mAdminCommandRef = FirebaseDatabase.getInstance().getReference("Commands");
        mUserCommand = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Commandes")
                .child(String.valueOf(getIntent().getIntExtra("order_number", 0)));
        cart_validate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (command_user_phone_number.getText().toString().isEmpty() || command_add_adresse_btn.getVisibility() == View.VISIBLE) {
                    Toast.makeText(CartValidateCommandActivity.this, "Entrez Les données s'il vous plaît", Toast.LENGTH_SHORT).show();
                } else {
                    mUserCommand.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            HashMap<String, Object> command_hash = new HashMap<>();
                            command_hash.put("uid", mAuth.getCurrentUser().getUid());
                            command_hash.put("time", dataSnapshot.child("time").getValue(Long.class));
                            command_hash.put("order_number", dataSnapshot.child("order_number").getValue(Long.class));
                            command_hash.put("total_price", dataSnapshot.child("total_price").getValue(Long.class));
                            command_hash.put("mobile", command_user_phone_number.getText().toString());
                            command_hash.put("valide", false);
                            final HashMap<String, Object> product_hash = new HashMap<>();
                            for (DataSnapshot snapshot : dataSnapshot.child("Products").getChildren()) {
                                cartModelList.add(snapshot.getValue(CartModel.class));
                            }
                            mAdminCommandRef.child(String.valueOf(dataSnapshot.child("order_number").getValue(Long.class))).setValue(command_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    for (int i = 0; i < cartModelList.size(); i++) {
                                        product_hash.put("key", cartModelList.get(i).getKey());
                                        product_hash.put("quantity", cartModelList.get(i).getQuantity());
                                        product_hash.put("time", cartModelList.get(i).getTime());
                                        mAdminCommandRef.child(String.valueOf(dataSnapshot.child("order_number").getValue(Long.class))).child("Products")
                                                .child(cartModelList.get(i).getKey()).setValue(product_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                finish();
                                            }
                                        });
                                    }
                                    startActivity(new Intent(getApplicationContext(), CommandSendActivity.class).putExtra("order_number", getIntent().getIntExtra("order_number", 0)));
                                }
                            });
                            mUserRef.child("Cart").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("User Cart", "onSuccess: User Cart reset");
                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });


                }
            }
        });
        mUserCommand = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Commandes")
                .child(String.valueOf(getIntent().getIntExtra("order_number", 0)));
        DatabaseReference mProductRef = FirebaseDatabase.getInstance().getReference("Products");
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Buying Section
        getSupportActionBar().hide();
    }
    @Override
    protected void onStart() {
        super.onStart();
        command_adapter.startListening();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserCartActivity.cartModels.clear();
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RemoveCommand.remove(getIntent().getIntExtra("order_number",0));
        UserCartActivity.cartModels.clear();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserCartActivity.cartModels.clear();
        finish();
    }
}