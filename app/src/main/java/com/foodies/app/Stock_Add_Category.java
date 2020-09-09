package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Stock_Add_Category extends AppCompatActivity {
    private CircleImageView add_category_main_image;
    //Materiel Edtit Text Section
    private MaterialEditText add_category_description,add_category_name;
    private CardView add_category_image_from_storage_btn;
    public static final int STOCKAGE_KEY = 177;
    //Image Upload Section
    private Uri category_image_uri;
    private DatabaseReference mCategoryRef;
    //Add Category Section
    private CardView add_category_to_database_btn;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock__add__category);
        add_category_name = findViewById(R.id.add_category_name);
        add_category_description = findViewById(R.id.add_category_description);
        //pick Image Section
        add_category_image_from_storage_btn = findViewById(R.id.add_category_image_from_storage_btn);
        add_category_main_image = findViewById(R.id.add_category_main_image);
        add_category_image_from_storage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle_image_from_stockage();
            }
        });
        //hide ActionBar
        //Add Category Section
        add_category_to_database_btn = findViewById(R.id.add_category_to_database_btn);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        mCategoryRef = FirebaseDatabase.getInstance().getReference("Menu");
        add_category_to_database_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_category_name.getText().toString().isEmpty() || category_image_uri == null
                        || add_category_description.getText().toString().isEmpty()){
                    Toast.makeText(Stock_Add_Category.this, "Veuillez remplir toutes les données", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(Stock_Add_Category.this, "Ajout d'une catégorie en cours....", Toast.LENGTH_SHORT).show();
                    final StorageReference put_categorie = storageReference.child("Menu/"+add_category_name.getText().toString());
                    put_categorie.putFile(category_image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            put_categorie.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap<String,Object> categorie_hash = new HashMap<>();
                                    categorie_hash.put("menu_name",add_category_name.getText().toString());
                                    categorie_hash.put("menu_image",uri.toString());
                                    categorie_hash.put("menu_description",add_category_description.getText().toString());
                                    categorie_hash.put("menu_added_time",System.currentTimeMillis());
                                    mCategoryRef.child(mCategoryRef.push().getKey()).setValue(categorie_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Stock_Add_Category.this, "la catégorie "+add_category_name.getText().toString()+" ajoutée avec succès", Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("error", "onFailure: "+e.getMessage());
                        }
                    });
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

    private void handle_image_from_stockage() {
        Intent pick_image = new Intent(Intent.ACTION_PICK);
        pick_image.setType("image/*");
        startActivityForResult(pick_image,STOCKAGE_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STOCKAGE_KEY && resultCode == Stock_Add_Category.this.RESULT_OK){
            category_image_uri = data.getData();
            add_category_main_image.setImageURI(category_image_uri);
        }
    }
}
