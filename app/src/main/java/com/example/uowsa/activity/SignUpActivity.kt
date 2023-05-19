package com.example.uowsa.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uowsa.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val conPassword = findViewById<EditText>(R.id.conPassword)
        val username = findViewById<EditText>(R.id.username)

        val signUpBtn = findViewById<Button>(R.id.signUpBtn)
        val logInBtn = findViewById<Button>(R.id.loginBtn)

        auth = Firebase.auth

        logInBtn.setOnClickListener {
            val intentHome = Intent(this, LoginActivity::class.java)
            startActivity(intentHome)
        }

        signUpBtn.setOnClickListener {
            if (email.text.toString().trim().isEmpty()) {
                Toast.makeText(this@SignUpActivity, "Please insert an email", Toast.LENGTH_SHORT)
                    .show()
            }
            //email validation unnecessary, as the input type is set to email in the xml layout file.
            val emailTxt = email.text.toString()
            val passwordTxt = password.text.toString()
            val usernameTxt = username.text.toString()
            val conPasswordTxt = conPassword.text.toString()
            val usernameLowerCase = username.text.toString().lowercase()

            if (email.text.toString().trim().isEmpty()) {
                Toast.makeText(this@SignUpActivity, "Please insert an email.", Toast.LENGTH_SHORT).show()
            }else if(!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
                Toast.makeText(this@SignUpActivity, "The email inserted is not a defined email address.", Toast.LENGTH_SHORT).show()
            }else if(usernameTxt.isEmpty()){
                Toast.makeText(this@SignUpActivity, "Please insert a username", Toast.LENGTH_SHORT).show()
            }else if(usernameLowerCase.contains("admin")){
                Toast.makeText(this@SignUpActivity, "The username $usernameTxt is reserved, please enter a new username.", Toast.LENGTH_SHORT).show()
            }else if(usernameTxt.length < 4 || usernameTxt.length > 12) {
                Toast.makeText(this@SignUpActivity, "Please enter a username between 4 - 12 characters.", Toast.LENGTH_SHORT).show()
            }else if (passwordTxt.isEmpty()) {
                Toast.makeText(this@SignUpActivity, "Please insert a Password", Toast.LENGTH_SHORT).show()
            }else if (passwordTxt.length < 8 || passwordTxt.length > 16){
                Toast.makeText(this@SignUpActivity, "Please insert a Password between 8 - 16 characters.", Toast.LENGTH_SHORT).show()
            }else if (conPasswordTxt.isEmpty()){
                Toast.makeText(this@SignUpActivity, "The confirm password is empty.", Toast.LENGTH_SHORT).show()
            }else if(passwordTxt != conPassword.text.toString().trim()){
                Toast.makeText(this@SignUpActivity, "The confirm password inserted is incorrect.", Toast.LENGTH_SHORT).show()
            } else{
                registerUser(usernameTxt, emailTxt, passwordTxt)
            }
        }
    }

    private fun registerUser(username: String, email: String, Password:String){
        auth.createUserWithEmailAndPassword(email, Password).addOnCompleteListener(this) { it ->
            if (it.isSuccessful){
                val user: FirebaseUser? = auth.currentUser
                val userId: String = user!!.uid

                Log.d("tag", userId)

                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                val userData:HashMap<String,String> = HashMap()
                userData["userId"] = userId
                userData["userName"] = username
                userData["profileImage"] = ""
                userData["status"] = "Hey, UoWsa is the future!"
                userData["studyCourse"] = ""
                userData["campusLocation"] = ""
                userData["connectivityStatus"] = "Offline"

                databaseReference.setValue(userData).addOnCompleteListener(this){
                    if (it.isSuccessful){
                        Toast.makeText(this@SignUpActivity, "You're registered successfully", Toast.LENGTH_SHORT).show()
                        val intentHome = Intent(this@SignUpActivity, LoginActivity::class.java)
                        //Clears all previous activities in the background and ensure the application runs smoothly.
                        intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intentHome)
                        finish()
                    }else{
                        Toast.makeText(this@SignUpActivity, "Failed to Register", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}