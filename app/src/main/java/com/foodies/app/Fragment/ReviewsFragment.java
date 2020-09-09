package com.foodies.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Holders.UserRatingHolder;
import com.foodies.app.LoginActivity;
import com.foodies.app.Models.RatingBarModel;
import com.foodies.app.Product_Detaille_Activity;
import com.foodies.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class ReviewsFragment extends Fragment {
    private RatingBar product_rating_bar;
    private MaterialEditText product_rating_edittext;
    private CardView product_rating_send_rating;
    private FirebaseAuth mAuth;
    private DatabaseReference mProductRef,mUserRef;
    private RecyclerView user_ratings_recycler;
    private FirebaseRecyclerAdapter<RatingBarModel, UserRatingHolder> user_rating_adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View review_view =  inflater.inflate(R.layout.fragment_reviews,container,false);
        mAuth = FirebaseAuth.getInstance();
        product_rating_send_rating = review_view.findViewById(R.id.product_rating_send_rating);
        product_rating_bar = review_view.findViewById(R.id.product_rating_bar);
        product_rating_edittext = review_view.findViewById(R.id.product_rating_edittext);
        mProductRef = FirebaseDatabase.getInstance().getReference("Products");
        product_rating_send_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product_rating_bar.getRating() == 0.0f || product_rating_edittext.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Mettre les détails de l'évaluation", Toast.LENGTH_SHORT).show();
                }else  {
                    if (mAuth.getCurrentUser() !=null){
                        mProductRef.child(Product_Detaille_Activity.key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("Rating").child("Users").hasChild(mAuth.getCurrentUser().getUid())){
                                    Toast.makeText(getActivity(), "Merci mais vous avez déjà voté", Toast.LENGTH_LONG).show();
                                }else {
                                    if (dataSnapshot.child("Rating").child("rating").getValue(Float.class) == null){
                                        mProductRef.child(Product_Detaille_Activity.key).child("Rating").child("rating").setValue(0.0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                HashMap<String,Object> rating_hash = new HashMap<>();
                                                rating_hash.put("uid",mAuth.getCurrentUser().getUid());
                                                rating_hash.put("time",System.currentTimeMillis());
                                                rating_hash.put("message",product_rating_edittext.getText().toString().trim());
                                                rating_hash.put("rating",product_rating_bar.getRating());
                                                mProductRef.child(Product_Detaille_Activity.key).child("Rating").child("Users").child(mAuth.getCurrentUser().getUid()).setValue(rating_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getActivity(), "Merci pour l'évaluation", Toast.LENGTH_LONG).show();
                                                        mProductRef.child(Product_Detaille_Activity.key).child("Rating").child("rating").setValue(product_rating_bar.getRating());
                                                    }
                                                });
                                            }
                                        });
                                    }else {
                                        HashMap<String,Object> rating_hash = new HashMap<>();
                                        rating_hash.put("uid",mAuth.getCurrentUser().getUid());
                                        rating_hash.put("time",System.currentTimeMillis());
                                        rating_hash.put("message",product_rating_edittext.getText().toString().trim());
                                        rating_hash.put("rating",product_rating_bar.getRating());
                                        mProductRef.child(Product_Detaille_Activity.key).child("Rating").child("Users").child(mAuth.getCurrentUser().getUid()).setValue(rating_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Merci pour l'évaluation", Toast.LENGTH_LONG).show();
                                                float bar_rating = (product_rating_bar.getRating())+(dataSnapshot.child("Rating").child("rating").getValue(Float.class));
                                                mProductRef.child(Product_Detaille_Activity.key).child("Rating").child("rating").setValue(bar_rating);
                                            }
                                        });
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }else {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }

                }
            }
        });
        //Load Users Rating
        mUserRef = FirebaseDatabase.getInstance().getReference("Products").child(Product_Detaille_Activity.key).child("Rating").child("Users");
        Query user_query = mUserRef.orderByKey();
        FirebaseRecyclerOptions mOption = new FirebaseRecyclerOptions.Builder<RatingBarModel>().setQuery(user_query,RatingBarModel.class).build();
        user_rating_adapter = new FirebaseRecyclerAdapter<RatingBarModel, UserRatingHolder>(mOption) {
            @Override
            protected void onBindViewHolder(@NonNull UserRatingHolder userRatingHolder, int i, @NonNull RatingBarModel ratingBarModel) {
                userRatingHolder.push(ratingBarModel.getMessage(),ratingBarModel.getTime(),ratingBarModel.getRating(),ratingBarModel.getUid());
            }

            @NonNull
            @Override
            public UserRatingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View user_view = LayoutInflater.from(getActivity()).inflate(R.layout.row_user_rating_bar,parent,false);
                return new UserRatingHolder(user_view);
            }
        };
        user_ratings_recycler = review_view.findViewById(R.id.user_ratings_recycler);
        user_ratings_recycler.setAdapter(user_rating_adapter);
        user_ratings_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        return review_view;
    }

    @Override
    public void onStart() {
        super.onStart();
        user_rating_adapter.startListening();
    }
}
