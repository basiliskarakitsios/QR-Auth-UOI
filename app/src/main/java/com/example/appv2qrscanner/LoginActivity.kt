package com.example.appv2qrscanner

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val firebaseInstance = FirebaseAuth.getInstance()

        val registerButton = findViewById<TextView>(R.id.registerBtn)
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


        val emailInput = findViewById<TextView>(R.id.emailInput)
        val passwordInput = findViewById<TextView>(R.id.passwordInput)

        //reset password
        val resetPassword = findViewById<TextView>(R.id.resetPasswordView)
        resetPassword.setOnClickListener{
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        //log in
        val loginButton = findViewById<Button>(R.id.loginBtn)
        loginButton.setOnClickListener {
            when{
                TextUtils.isEmpty(emailInput.text.toString().trim {it <= ' '}) ->  {
                    Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(passwordInput.text.toString().trim {it <= ' '}) ->  {
                    Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val email = emailInput.text.toString()
                    val password = passwordInput.text.toString()

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).
                    addOnCompleteListener(
                        OnCompleteListener { task ->
                            //if login is successful
                            if(task.isSuccessful){
                                //check if email is verified
                                //send verification email
//                                if(firebaseInstance.currentUser!!.isEmailVerified){
//                                    //redirect to profile activity
//                                    Toast.makeText(this, "You are logged in successfully", Toast.LENGTH_SHORT).show()
//                                    val intent = Intent(this, MyProfileActivity::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    startActivity(intent)
//                                    finish()
//                                }else{
//                                    Toast.makeText(this, "Please verify your email address first", Toast.LENGTH_SHORT).show()
//                                }


                                //redirect to profile activity
                                Toast.makeText(this, "You are logged in successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MyProfileActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }else{
                                Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

    }
}