package com.foodies.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodies.app.Holders.CoffeHolder;
import com.foodies.app.Holders.MenuHolder;
import com.foodies.app.Holders.MostWantedProductHolder;
import com.foodies.app.Holders.PlatHolder;
import com.foodies.app.Interfaces.Auth;
import com.foodies.app.Models.MenuModel;
import com.foodies.app.Models.ProductModel;
import com.foodies.app.Models.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.processphoenix.ProcessPhoenix;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    //most wanted Product Section
    private RecyclerView most_wanted_products_recycler;
    private FirebaseRecyclerAdapter<ProductModel, MostWantedProductHolder> most_wanted_adapter;
    private DatabaseReference mProductRef;
    //plat Section
    public static FirebaseRecyclerAdapter<ProductModel, PlatHolder> plat_adapter;
    private RecyclerView plat_recycler;
    private DatabaseReference mPlatRef;
    //Menu Section
    private DatabaseReference mMenuRef;
    private FirebaseRecyclerAdapter<MenuModel, MenuHolder> menu_adapter;
    private RecyclerView menu_recycler;
    //Coffe Section
    private DatabaseReference mCoffeRef;
    private FirebaseRecyclerAdapter<ProductModel, CoffeHolder> coffe_adapter;
    private RecyclerView coffe_recycler;
    //navigation bar section
    private BottomNavigationView bottom_navigation;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    //nav_header Section
    private LinearLayout logged_out_layout,logged_in_layout;
    private TextView header_username;
    private CircleImageView header_image;
    private ActionBarDrawerToggle drawerToggle;
    private CardView open_admin_window;
    private DatabaseReference mUserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.main_navigation_view);
        View nav_view = navigationView.getHeaderView(0);
        logged_in_layout = nav_view.findViewById(R.id.logged_in_layout);
        logged_out_layout = nav_view.findViewById(R.id.logged_out_layout);
        header_username = nav_view.findViewById(R.id.header_username);
        header_image = nav_view.findViewById(R.id.header_image);
        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
        if (Auth.mAuth.getCurrentUser() !=null){
            mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(Auth.mAuth.getCurrentUser().getUid());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.getValue(Users.class);
                    header_username.setText(users.getUsername());
                    Glide.with(getApplicationContext()).load(users.getProfile_picture()).into(header_image);
                    LinearLayout access_to_admin_btn = findViewById(R.id.access_to_admin_btn);
                    if (dataSnapshot.child("access").getValue(String.class).equals("admin")){
                        access_to_admin_btn.setVisibility(View.VISIBLE);
                    }else {
                        access_to_admin_btn.setVisibility(View.GONE);
                    }
                    access_to_admin_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getApplicationContext(),FoodiesManagment.class));
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
        }
        logged_out_layout.setVisibility(Auth.mAuth.getCurrentUser() !=null ? View.VISIBLE : View.GONE);
        logged_in_layout.setVisibility(Auth.mAuth.getCurrentUser() !=null ? View.GONE : View.VISIBLE);
        nav_view.findViewById(R.id.header_logout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.mAuth.signOut();
                drawer.closeDrawers();
                finish();
                startActivity(getIntent());
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
        nav_view.findViewById(R.id.header_login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.menu_icon);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_cart:
                        if (Auth.mAuth.getCurrentUser() !=null){
                            startActivity(new Intent(getApplicationContext(),UserCartActivity.class));
                        }else {
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        }
                        break;
                    case R.id.nav_credit:
                        startActivity(new Intent(getApplicationContext(),CreditActivity.class));
                        break;
                    case R.id.nav_profile:
                        startActivity(Auth.mAuth.getCurrentUser() !=null ? new Intent(getApplicationContext(),ProfileActivity.class) : new Intent(getApplicationContext(),LoginActivity.class));
                        break;
                }
                return true;
            }
        });
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        //Most Wanted Section
        mProductRef = FirebaseDatabase.getInstance().getReference("Products");
        Query mMostWantedQuery = mProductRef.orderByKey();
        FirebaseRecyclerOptions mMostWantedOption = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(mMostWantedQuery,ProductModel.class).build();
        most_wanted_adapter = new FirebaseRecyclerAdapter<ProductModel, MostWantedProductHolder>(mMostWantedOption) {
            @Override
            protected void onBindViewHolder(@NonNull MostWantedProductHolder holder, final int i, @NonNull ProductModel model) {
                holder.send(
                        model.getProduct_name(),model.getProduct_image(),model.getProduct_price(),model.getProduct_description()
                );
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),Product_Detaille_Activity.class).putExtra("key",most_wanted_adapter.getRef(i).getKey()));
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public MostWantedProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View most_wanted_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.row_most_wanted_product_layout
                ,parent,false);
                return new MostWantedProductHolder(most_wanted_view);
            }
        };
        most_wanted_products_recycler = findViewById(R.id.most_wanted_product_recycler);
        most_wanted_products_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                RecyclerView.HORIZONTAL,false));
        most_wanted_products_recycler.setAdapter(most_wanted_adapter);
        //Plat Section
        mPlatRef = FirebaseDatabase.getInstance().getReference("Products");
        Query mPlatQuery = mProductRef.orderByKey();
        FirebaseRecyclerOptions mPlatOption =new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(mPlatQuery,ProductModel.class).build();
        plat_adapter = new FirebaseRecyclerAdapter<ProductModel, PlatHolder>(mPlatOption) {
            @Override
            protected void onBindViewHolder(@NonNull PlatHolder holder, final int i, @NonNull ProductModel model) {
                holder.send(
                        model.getProduct_name(),model.getProduct_image(),model.getProduct_price(),model.getProduct_description()
                ,plat_adapter.getRef(i).getKey());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),Product_Detaille_Activity.class).putExtra("key",plat_adapter.getRef(i).getKey()));
                    }
                });
            }

            @NonNull
            @Override
            public PlatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View plat_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.row_plat_layout,parent,false);
                return new PlatHolder(plat_view);
            }
        };
        plat_recycler = findViewById(R.id.plat_recycler);
        plat_recycler.setAdapter(plat_adapter);
        plat_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
        //Menu Section
        mMenuRef = FirebaseDatabase.getInstance().getReference("Menu");
        Query mMenuQuery = mMenuRef.orderByKey();
        FirebaseRecyclerOptions mMenuOption = new FirebaseRecyclerOptions.Builder<MenuModel>().setQuery(mMenuQuery,MenuModel.class).build();
        menu_adapter = new FirebaseRecyclerAdapter<MenuModel, MenuHolder>(mMenuOption) {
            @Override
            protected void onBindViewHolder(@NonNull MenuHolder holder, final int  i, @NonNull final MenuModel model) {
                holder.send(model.getMenu_image(),model.getMenu_name());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),MenuItemsActivity.class).putExtra("key",menu_adapter.getRef(i).getKey()).putExtra("name",model.getMenu_name()));
                    }
                });

            }

            @NonNull
            @Override
            public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View menu_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.row_menu_layout,parent,false);
                return new MenuHolder(menu_view);
            }
        };
        menu_recycler = findViewById(R.id.menu_recycler);
        menu_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        menu_recycler.setAdapter(menu_adapter);
        //cofffe section
        mCoffeRef = FirebaseDatabase.getInstance().getReference().child("Products");
        Query mCoffeQuery = mCoffeRef.orderByKey();
        FirebaseRecyclerOptions coffe_option  =  new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(mCoffeQuery,ProductModel.class).build();
        coffe_adapter  = new FirebaseRecyclerAdapter<ProductModel, CoffeHolder>(coffe_option) {
            @Override
            protected void onBindViewHolder(@NonNull CoffeHolder holder, final int i, @NonNull ProductModel model) {
                holder.send(coffe_adapter.getRef(i).getKey(),model.getProduct_name(),
                        model.getProduct_price(),
                        model.getProduct_description(),
                        model.getProduct_image());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),Product_Detaille_Activity.class).putExtra("key",coffe_adapter.getRef(i).getKey()));
                    }
                });

            }

            @NonNull
            @Override
            public CoffeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View coffe_view =LayoutInflater.from(MainActivity.this).inflate(R.layout.row_coffe_layout,parent,false);
                return new CoffeHolder(coffe_view);
            }
        };
        coffe_recycler = findViewById(R.id.coffe_recycler);
        coffe_recycler.setAdapter(coffe_adapter);
        coffe_recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //Bottom Navigation View
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_profile:
                        if (Auth.mAuth.getCurrentUser() != null){
                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        }else
                            startActivity(new Intent(getApplicationContext(),LoginIntroductionActivity.class));
                        break;
                    case R.id.bottom_credit:
                        startActivity(new Intent(getApplicationContext(),CreditActivity.class));
                        break;
                }
                return false;
            }
        });
        //admin dialog section
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_activate_admin);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Switch enable_admin = dialog.findViewById(R.id.activate_admin_btn);
        open_admin_window = findViewById(R.id.open_admin_window);
        open_admin_window.setVisibility(Auth.mAuth !=null ? View.VISIBLE : View.GONE);
        DatabaseReference admin_ref = FirebaseDatabase.getInstance().getReference("Users");
        if (Auth.mAuth.getCurrentUser() !=null){
            admin_ref.child(Auth.mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String enabled = dataSnapshot.child("access").getValue(String.class);
                    switch (enabled){
                        case "admin":
                            enable_admin.setChecked(true);
                            break;
                        case "client":
                            enable_admin.setChecked(false);
                            break;
                        default:
                            enable_admin.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    databaseError.toException().printStackTrace();
                }
            });
        }
        findViewById(R.id.open_admin_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Auth.mAuth.getCurrentUser() != null){
                    dialog.show();
                    enable_admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            admin_ref.child(Auth.mAuth.getCurrentUser().getUid()).child("access").setValue(isChecked ? "admin" : "client").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Veuillez redémarrer l'application", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                    ProcessPhoenix.triggerRebirth(getApplicationContext(),main);
                                }
                            });
                        }
                    });
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_cart_btn:
                startActivity(new Intent(getApplicationContext(),UserCartActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        most_wanted_adapter.startListening();
        plat_adapter.startListening();
        menu_adapter.startListening();
        coffe_adapter.startListening();
    }
}
