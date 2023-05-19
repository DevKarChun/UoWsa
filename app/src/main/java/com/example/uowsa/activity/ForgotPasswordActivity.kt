package com.example.uowsa.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val email = findViewById<EditText>(R.id.email)
        val resetBtn = findViewById<Button>(R.id.resetBtn)

        auth = Firebase.auth

        resetBtn.setOnClickListener {
            if (email.text.toString().trim().isEmpty()){
                Toast.makeText(this@ForgotPasswordActivity, "Please insert an email", Toast.LENGTH_SHORT).show()
            }else{
                auth.sendPasswordResetEmail(email.text.toString().trim()).addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity, "Email sent successfully to reset your password", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this@ForgotPasswordActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}