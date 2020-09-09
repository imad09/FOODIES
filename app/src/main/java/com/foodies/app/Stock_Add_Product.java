package com.foodies.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.foodies.app.Models.MenuModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.foodies.app.FoodiesBase.foodies_admin_channel;

public class Stock_Add_Product extends AppCompatActivity {
    //add Product Image Section
    private CardView add_product_image;
    private LinearLayout selected_product_image_layout;
    private final int IMG_KEY = 154;
    public Uri image_uri;
    //prodcut id Section
    private CardView add_product_id_qr_btn;
    private MaterialEditText add_product_id;
    //Product Detaille Section
    private MaterialEditText add_product_name,add_product_description;
    private Spinner add_product_categories_spinner;
    private DatabaseReference mCategorieRef;
    private List<MenuModel> categoriesModels_liste = new ArrayList<>();
    private List<String> categories_name = new ArrayList<>();
    //price Section
    private MaterialEditText add_product_buying_price,add_product_selling_price;
    //add Product To Database
    private LinearLayout add_product_to_database_btn;
    private DatabaseReference mProductRef,mAdderRef;
    //Storage Reference
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    public NotificationCompat.Builder notification_builder;
    public static final String CHANNEL_ADMIN = "ADMIN_PHMA_NOTI_CHANNEL";
    public NotificationManager notificationManager;
    public int progress_download =0;
    //Product Quantity
    public TextView add_product_quantity;
    //Keys
    public static final int STORAGE_KEY = 05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock__add__product);
        //Image Section
        add_product_image = findViewById(R.id.add_product_image);
        selected_product_image_layout = findViewById(R.id.selected_product_image_layout);
        add_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"),IMG_KEY);
            }
        });
        //product id Section
        add_product_id_qr_btn = findViewById(R.id.add_product_id_qr_btn);
        add_product_id = findViewById(R.id.add_product_id);
        add_product_id_qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Stock_Add_Product.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Scanner le Qr du produit");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
        //Add Product Detaille Section
        add_product_name = findViewById(R.id.add_product_name);
        add_product_description = findViewById(R.id.add_product_description);
        mCategorieRef = FirebaseDatabase.getInstance().getReference("Menu");
        add_product_categories_spinner = findViewById(R.id.add_product_categories_spinner);
        mCategorieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    MenuModel categoriesModels = snapshot.getValue(MenuModel.class);
                    categoriesModels_liste.add(snapshot.getValue(MenuModel.class));
                    categories_name.add(categoriesModels.getMenu_name());
                }
                ArrayAdapter categories_adapter = new ArrayAdapter(Stock_Add_Product.this,android.R.layout.simple_spinner_item,
                        categories_name);
                categories_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                add_product_categories_spinner.setAdapter(categories_adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Quantity Section
        add_product_quantity = findViewById(R.id.add_product_quantity);
        //Payment Section
        add_product_buying_price = findViewById(R.id.add_product_buying_price);
        //Storage Refre
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        //Add Product To Database
        add_product_to_database_btn = findViewById(R.id.add_product_to_database_btn);
        add_product_to_database_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lets Make SImple Check of ou data
                if (add_product_name.getText().toString().isEmpty() || add_product_description.getText().toString().isEmpty()
                        || add_product_buying_price.getText().toString().isEmpty()
                        || add_product_id.getText().toString().isEmpty()  || add_product_quantity.getText().toString().isEmpty() || image_uri == null) {
                    Toast.makeText(Stock_Add_Product.this, "Vous devez entrer tous les détails du produit", Toast.LENGTH_LONG).show();
                }else {
                    HashMap<String,Object> product_hash = new HashMap<>();
                    product_hash.put("product_name",add_product_name.getText().toString());
                    product_hash.put("product_description",add_product_description.getText().toString());
                    product_hash.put("product_quantity",Long.parseLong(add_product_quantity.getText().toString()));
                    product_hash.put("product_price",Long.parseLong(add_product_buying_price.getText().toString()));
                    product_hash.put("product_id",Long.parseLong(add_product_id.getText().toString()));
                    product_hash.put("product_added_time",System.currentTimeMillis());
                    product_hash.put("product_category",categories_name.get(add_product_categories_spinner.getSelectedItemPosition()));
                    mProductRef = FirebaseDatabase.getInstance().getReference("Products");
                    mAdderRef = mProductRef.push();
                    mProductRef.child(mAdderRef.getKey()).setValue(product_hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mProductRef.child(mAdderRef.getKey()).child("product_key").setValue(mAdderRef.getKey());
                            Toast.makeText(Stock_Add_Product.this, "Le produit a été ajouté à la base de données avec succès", Toast.LENGTH_LONG).show();
                            new ImageUploadProcess().execute();
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_KEY && resultCode == Stock_Add_Product.RESULT_OK){
            try {
                image_uri = data.getData();
                selected_product_image_layout.setVisibility(View.VISIBLE);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        IntentResult intentResult= IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult !=null){
            if(intentResult.getContents() !=null){
                add_product_id.setText(intentResult.getContents().toString());
            }

        }
    }

    public class ImageUploadProcess extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            start_notification();
            @SuppressLint("WrongThread") final StorageReference image_storage = storageReference.child("Product_Images/"+add_product_name.getText().toString()+"/"+"Images/"
                    + UUID.randomUUID().toString());
            if (image_uri !=null){
                image_storage.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        image_storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mProductRef.child(mAdderRef.getKey()).child("product_image").setValue(uri.toString());
                                finish();
                                startActivity(getIntent());
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull final UploadTask.TaskSnapshot taskSnapshot) {
                        progress_download = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                        notification_builder.setProgress(100,progress_download,false);
                        notificationManager.notify(1,notification_builder.build());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Product Image Upload", "onFailure: "+e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        notificationManager.cancel(1);
                        Toast.makeText(Stock_Add_Product.this, "l'image est téléchargée vers le stockage", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
        public void start_notification(){
            notification_builder = new NotificationCompat.Builder(Stock_Add_Product.this,foodies_admin_channel)
                    .setSmallIcon(R.drawable.ic_check_black_24dp)
                    .setChannelId(foodies_admin_channel)
                    .setColor(Color.parseColor("#FC4041"))
                    .setChannelId(CHANNEL_ADMIN)
                    .setContentTitle("Le téléchargement de l'image du produit").
                            setProgress(100,progress_download,false);
            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel admin_channel = new NotificationChannel(CHANNEL_ADMIN,"Pharmmacy_admin_channel",
                        NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(admin_channel);
                notificationManager.notify(1,notification_builder.build());
            }else {
                notificationManager.notify(1,notification_builder.build());
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_KEY && grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.i("Storage Permission", "Permission Granted");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
