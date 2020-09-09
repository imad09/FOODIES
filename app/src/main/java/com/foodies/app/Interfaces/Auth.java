package com.foodies.app.Interfaces;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public interface Auth {
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
}
