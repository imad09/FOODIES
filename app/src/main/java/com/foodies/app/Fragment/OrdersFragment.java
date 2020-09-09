package com.foodies.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Models.CartModel;
import com.foodies.app.Models.CommandModel;
import com.foodies.app.Models.ProductModel;
import com.foodies.app.Models.Users;
import com.foodies.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdersFragment extends Fragment {
    private DatabaseReference command_ref;
    private FirebaseRecyclerAdapter<CommandModel,CommandAdminHolder> command_adapter;
    private RecyclerView command_fragment_recycler;
    private FloatingActionButton foodies_font_1;
    private int count = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View order_view = inflater.inflate(R.layout.fragment_orders,container,false);
        command_fragment_recycler = order_view.findViewById(R.id.command_fragment_recycler);
        foodies_font_1 = order_view.findViewById(R.id.foodies_font_1);
        CommandAdapterMeth(false);
        foodies_font_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                switch (count){
                    case 1:
                        CommandAdapterMeth(false);
                        break;
                    case 2:
                        CommandAdapterMeth(true);
                        count=0;
                        break;
                }

            }
        });
        return order_view;
    }

    private void CommandAdapterMeth(boolean status) {
        command_ref = FirebaseDatabase.getInstance().getReference("Commands");
        Query command_query = command_ref.orderByChild("valide").equalTo(status);
        FirebaseRecyclerOptions mOption = new FirebaseRecyclerOptions.Builder<CommandModel>().setQuery(command_query,CommandModel.class).build();
        command_adapter = new FirebaseRecyclerAdapter<CommandModel, CommandAdminHolder>(mOption) {
            @Override
            protected void onBindViewHolder(@NonNull CommandAdminHolder commandAdminHolder, int i, @NonNull CommandModel model) {
                commandAdminHolder.put(model.isValide(),model.getOrder_number(),model.getTime(),model.getUid(),model.getMobile(),model.getTotal_price());
            }

            @NonNull
            @Override
            public CommandAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View command_view = LayoutInflater.from(getActivity()).inflate(R.layout.row_command_admin,parent,false);
                return new CommandAdminHolder(command_view);
            }
        };
        LinearLayoutManager command_manager= new LinearLayoutManager(getActivity());
        command_manager.setStackFromEnd(true);
        command_manager.setReverseLayout(true);
        command_fragment_recycler.setAdapter(command_adapter);
        command_fragment_recycler.setLayoutManager(command_manager);
        command_adapter.startListening();
    }

    public class CommandAdminHolder extends RecyclerView.ViewHolder{
        private DatabaseReference mUserRref,mProductRef;
        private TextView pharmapass_request_date,command_full_name,command_address,command_phone_number,command_total_price,command_order_number,command_status_text;
        private RecyclerView command_product_recycler;
        private FirebaseRecyclerAdapter<CartModel,CommandProductHolder> command_product_adapter;
        private CardView command_delete_btn,command_valide_btn;
        private LinearLayout command_status_layout;
        public CommandAdminHolder(@NonNull View itemView) {
            super(itemView);
            command_total_price = itemView.findViewById(R.id.command_total_price);
            command_full_name = itemView.findViewById(R.id.command_full_name);
            command_phone_number = itemView.findViewById(R.id.command_phone_number);
            command_address = itemView.findViewById(R.id.command_address);
            command_total_price = itemView.findViewById(R.id.command_total_price);
            command_order_number = itemView.findViewById(R.id.command_order_number);
            command_product_recycler = itemView.findViewById(R.id.command_product_recycler);
            command_delete_btn = itemView.findViewById(R.id.command_delete_btn);
            command_valide_btn = itemView.findViewById(R.id.command_valide_btn);
            command_status_layout = itemView.findViewById(R.id.command_status_layout);
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
            mProductRef = FirebaseDatabase.getInstance().getReference("Commands").child(String.valueOf(order_number)).child("Products");
            Query product_query = mProductRef.orderByKey();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd | hh:mm");
            Date get_order_time = new Date(time);
            pharmapass_request_date.setText(simpleDateFormat.format(get_order_time));
            FirebaseRecyclerOptions mOption = new FirebaseRecyclerOptions.Builder<CartModel>().setQuery(product_query,CartModel.class).build();
            command_product_adapter = new FirebaseRecyclerAdapter<CartModel, CommandProductHolder>(mOption) {
                @Override
                protected void onBindViewHolder(@NonNull CommandProductHolder commandProductHolder, int i, @NonNull CartModel model) {
                    commandProductHolder.push(model.getKey(),model.getQuantity());
                }

                @NonNull
                @Override
                public CommandProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View order_view = LayoutInflater.from(getActivity()).inflate(R.layout.row_command_product,parent,false);
                    return new CommandProductHolder(order_view);
                }
            };
            command_product_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
            command_product_recycler.setAdapter(command_product_adapter);
            command_product_adapter.startListening();
            DatabaseReference mCommandeReference = FirebaseDatabase.getInstance().getReference("Commands");
            command_delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommandeReference.child(String.valueOf(order_number)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "la commande #"+order_number+" supprimée avec succès", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            command_valide_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommandeReference.child(String.valueOf(order_number)).child("valide").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Commande Validé", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            command_status_text.setText(valide? "Commande Valide" : "Command invalide");
            command_status_layout.setBackgroundColor(valide? getResources().getColor(R.color.green): getResources().getColor(R.color.colorPrimaryDark));
        }
        public class CommandProductHolder extends RecyclerView.ViewHolder{
            private TextView command_product_name,command_product_quantity;
            private ImageView command_product_image;
            private DatabaseReference product_ref;
            public CommandProductHolder(@NonNull View itemView) {
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
                            Glide.with(getActivity()).load(productModel.getProduct_image()).into(command_product_image);
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
