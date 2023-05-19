package com.example.uowsa.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//         ...
//         Initialize Firebase Auth
        auth = Firebase.auth

        val login = findViewById<Button>(R.id.loginBtn)
        val signUp = findViewById<Button>(R.id.signUpBtn)
        val tos = findViewById<Button>(R.id.toS)

        login.setOnClickListener {
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        }

        signUp.setOnClickListener {
            val intentSignUp = Intent(this, SignUpActivity::class.java)
            startActivity(intentSignUp)
        }

        tos.setOnClickListener {
            val intentSignUp = Intent(this, TermsOfServiceActivity::class.java)
            startActivity(intentSignUp)
        }
    }
}