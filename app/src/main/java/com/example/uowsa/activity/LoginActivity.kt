package com.example.uowsa.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        val signInBtn = findViewById<Button>(R.id.signInBtn)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val forgotPassword = findViewById<Button>(R.id.forgotMyPassword)

        auth = Firebase.auth

        forgotPassword.setOnClickListener {
            val intentHome = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intentHome)
        }

        registerBtn.setOnClickListener {
            val intentHome = Intent(this, SignUpActivity::class.java)
            startActivity(intentHome)
        }

        signInBtn.setOnClickListener {
            if (email.text.toString().trim().isEmpty()) {
                Toast.makeText(this@LoginActivity, "Please insert an email", Toast.LENGTH_SHORT).show()
            } else if (password.text.toString().trim().isEmpty()) {
                Toast.makeText(this@LoginActivity, "Please insert a password", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email.text.toString().trim(), password.text.toString().trim()).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this@LoginActivity, "You've signed in successfully", Toast.LENGTH_SHORT).show()
                        val intentHome = Intent(this, HomeActivity::class.java)
                        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intentHome.putExtra("user_id", auth.currentUser!!.uid)
                        val database = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid).child("connectivityStatus").setValue("Online")
                        startActivity(intentHome)
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, "Failed to Login", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}