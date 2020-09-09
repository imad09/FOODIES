package com.foodies.app.Holders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodies.app.Fragment.ProductsListFragment;
import com.foodies.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductListHolder extends RecyclerView.ViewHolder {
    private TextView product_name,product_id,product_quantity,product_category,product_buying_price,product_added_date;
    private LinearLayout product_section_names_layout;
    private CardView delete_product_btn,edit_product_btn;
    private Context context;
    private DatabaseReference mDeleteRef,mProductUpdateRef;
    //bottom section
    private BottomSheetDialog bottom_update_quantity_dialog;
    private MaterialEditText bottom_old_quantity,bottom_new_quantity;
    private LinearLayout bottom_update_quantity;
    public ProductListHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        product_name = itemView.findViewById(R.id.product_name);
        product_id = itemView.findViewById(R.id.product_id);
        product_quantity = itemView.findViewById(R.id.product_quantity);
        product_category = itemView.findViewById(R.id.product_category);
        product_buying_price = itemView.findViewById(R.id.product_buying_price);
        product_section_names_layout = itemView.findViewById(R.id.product_section_names_layout);
        delete_product_btn = itemView.findViewById(R.id.delete_product_btn);
        edit_product_btn = itemView.findViewById(R.id.edit_product_btn);
        product_added_date = itemView.findViewById(R.id.product_added_date);
        //Bottom Update Qauntity Section
        bottom_update_quantity_dialog = new BottomSheetDialog(context);
        View bottom_view = LayoutInflater.from(context).inflate(R.layout.bottom_update_quantity,null);
        bottom_update_quantity_dialog.setContentView(bottom_view);
        bottom_new_quantity = bottom_update_quantity_dialog.findViewById(R.id.bottom_new_quantity);
        bottom_old_quantity = bottom_update_quantity_dialog.findViewById(R.id.bottom_old_quantity);
        bottom_update_quantity = bottom_update_quantity_dialog.findViewById(R.id.bottom_update_quantity);
    }

    public void send(final String product_key, final String product_names, long product_ids, long quantity, long product_buying_prices, ProductListHolder holder, String product_categorys, long product_added_time) {
        mDeleteRef = FirebaseDatabase.getInstance().getReference().child("Products");
        edit_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_update_quantity_dialog.show();
            }
        });
        bottom_old_quantity.setText(""+quantity);
/*        mProductUpdateRef = FirebaseDatabase.getInstance().getReference("Products").child(product_key);
        bottom_update_quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottom_new_quantity.getText().toString().isEmpty()){
                    Toast.makeText(context, "Veuillez entrer une nouvelle quantité", Toast.LENGTH_SHORT).show();
                }else {
                    long calcul = (Long.parseLong(bottom_old_quantity.getText().toString())+(Long.parseLong(bottom_new_quantity.getText().toString())));
                    mProductUpdateRef.child("product_quantity").setValue(calcul).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Quantité mise à jour avec succès", Toast.LENGTH_SHORT).show();
                            bottom_update_quantity_dialog.dismiss();
                        }
                    });
                }
            }
        });*/
        if (holder.getAdapterPosition() == 0){
            product_section_names_layout.setVisibility(View.VISIBLE);
            product_name.setText(product_names);
            product_category.setText(product_categorys);
            product_buying_price.setText(product_buying_prices+" DA");
            product_quantity.setText(""+quantity);
            product_id.setText("#"+product_ids);
            Date data = new Date(product_added_time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm");
            product_added_date.setText(simpleDateFormat.format(data));
            delete_product_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeleteRef.child(product_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, product_names+" A été supprimée", Toast.LENGTH_SHORT).show();
                            ProductsListFragment.product_fb_adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }else {
            product_section_names_layout.setVisibility(View.GONE);
            product_name.setText(product_names);
            product_category.setText(product_categorys);
            product_id.setText("#"+product_ids);
            product_buying_price.setText(product_buying_prices+" DA");
            product_quantity.setText(""+quantity);
            Date data = new Date(product_added_time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm");
            product_added_date.setText(simpleDateFormat.format(data));
            delete_product_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeleteRef.child(product_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, product_names+" A été supprimée", Toast.LENGTH_SHORT).show();
                            ProductsListFragment.product_fb_adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }
}
