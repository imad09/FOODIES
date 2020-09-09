package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Models.CartModel;
import com.foodies.app.Models.CommandModel;
import com.foodies.app.Models.ProductModel;
import com.foodies.app.Models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserCommandesListActivity extends AppCompatActivity {
    private RecyclerView user_command_recycler;
    private DatabaseReference command_ref;
    private FirebaseRecyclerAdapter<CommandModel, ClientCommandHolder> command_adapter;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_user_commandes_list);
        command_ref = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Commandes");
        Query command_query = command_ref.orderByKey();
        FirebaseRecyclerOptions mOption = new FirebaseRecyclerOptions.Builder<CommandModel>().setQuery(command_query,CommandModel.class).build();
        command_adapter = new FirebaseRecyclerAdapter<CommandModel, ClientCommandHolder>(mOption) {
            @Override
            protected void onBindViewHolder(@NonNull ClientCommandHolder commandAdminHolder, int i, @NonNull CommandModel model) {
                commandAdminHolder.put(model.isValide(),model.getOrder_number(),model.getTime(),model.getUid(),model.getMobile(),model.getTotal_price());
            }

            @NonNull
            @Override
            public ClientCommandHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View command_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_command_client,parent,false);
                return new ClientCommandHolder(command_view);
            }
        };
        user_command_recycler = findViewById(R.id.user_command_recycler);
        user_command_recycler.setAdapter(command_adapter);
        user_command_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        command_adapter.startListening();
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().hide();
    }
    public class ClientCommandHolder  extends RecyclerView.ViewHolder{
        private TextView pharmapass_request_date,command_full_name,command_address,command_phone_number,command_total_price,command_order_number,command_status_text;
        private RecyclerView command_product_recycler;
        private DatabaseReference mUserRref,mProductRef;
        private FirebaseRecyclerAdapter<CartModel, CommandClientProductHolder> command__client_adapter;

        public ClientCommandHolder(@NonNull View itemView) {
            super(itemView);
            command_total_price = itemView.findViewById(R.id.command_total_price);
            command_full_name = itemView.findViewById(R.id.command_full_name);
            command_phone_number = itemView.findViewById(R.id.command_phone_number);
            command_address = itemView.findViewById(R.id.command_address);
            command_total_price = itemView.findViewById(R.id.command_total_price);
            command_order_number = itemView.findViewById(R.id.command_order_number);
            command_product_recycler = itemView.findViewById(R.id.command_product_recycler);
            command_status_text = itemView.findViewById(R.id.command_status_text);
            pharmapass_request_date = itemView.findViewById(R.id.pharmapass_request_date);
        }

        public void put(boolean valide, long order_number, long time, String uid, String mobile, long total_price) {
            mUserRref = FirebaseDatabase.getInstance().getReference("Users");
            command_order_number.setText("#"+order_number);
            mUserRref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    command_full_name.setText(users.getUsername());
                    command_address.setText(users.getAddress());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
            command_phone_number.setText(mobile);
            command_total_price.setText(total_price+"DA");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd | hh:mm");
            Date get_order_time = new Date(time);
            pharmapass_request_date.setText(simpleDateFormat.format(get_order_time));
            mProductRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid()).child("Commandes").child(String.valueOf(order_number)).child("Products");
            Query product_query = mProductRef.orderByKey();
            FirebaseRecyclerOptions mOption = new FirebaseRecyclerOptions.Builder<CartModel>().setQuery(product_query,CartModel.class).build();
            command__client_adapter = new FirebaseRecyclerAdapter<CartModel, CommandClientProductHolder>(mOption) {
                @Override
                protected void onBindViewHolder(@NonNull CommandClientProductHolder commandProductHolder, int i, @NonNull CartModel model) {
                    commandProductHolder.push(model.getKey(),model.getQuantity());
                }

                @NonNull
                @Override
                public CommandClientProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View order_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_command_product,parent,false);
                    return new CommandClientProductHolder(order_view);
                }
            };
            command_product_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
            command_product_recycler.setAdapter(command__client_adapter);
            command__client_adapter.startListening();
        }
        public class CommandClientProductHolder extends RecyclerView.ViewHolder{
            private TextView command_product_name,command_product_quantity;
            private ImageView command_product_image;
            private DatabaseReference product_ref;

            public CommandClientProductHolder(@NonNull View itemView) {
                super(itemView);
                command_product_image = itemView.findViewById(R.id.command_product_image);
                command_product_name = itemView.findViewById(R.id.command_product_names);
                command_product_quantity = itemView.findViewById(R.id.command_product_quantity);
                product_ref = FirebaseDatabase.getInstance().getReference("Products");
            }

            public void push(String key, int quantity) {
                command_product_quantity.setText("x"+quantity);
                product_ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                            command_product_name.setText(productModel.getProduct_name());
                            Glide.with(getApplicationContext()).load(productModel.getProduct_image()).into(command_product_image);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
            }
        }
    }
}
