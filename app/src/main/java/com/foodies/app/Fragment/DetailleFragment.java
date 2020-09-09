package com.foodies.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodies.app.Product_Detaille_Activity;
import com.foodies.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailleFragment extends Fragment {
    private DatabaseReference mProductRef;
    private TextView product_description;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View detaille_view =  inflater.inflate(R.layout.fragment_detaille,container,false);
        product_description = detaille_view.findViewById(R.id.product_description);
        mProductRef = FirebaseDatabase.getInstance().getReference("Products");
        mProductRef.child(Product_Detaille_Activity.key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                product_description.setText(dataSnapshot.child("product_description").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
        return detaille_view;
    }
}
