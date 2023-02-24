package com.example.appv2qrscanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private var notTwice = true
    private lateinit var closeAppToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get reference to button SCAN
        val scanButton = findViewById<Button>(R.id.scanBtn)
        // set on-click listener
        scanButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ScanActivity::class.java))
        }

        // get reference to button LOGIN
        val loginButton = findViewById<Button>(R.id.loginBtn)
        // set on-click listener
        loginButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // get reference to button PROFILE
        val profileButton = findViewById<Button>(R.id.profileBtn)
        // set on-click listener
        profileButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MyProfileActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            closeAppToast.cancel()
            super.onBackPressed()
            finishAffinity()
        }
        doubleBackToExitPressedOnce = true
        closeAppToast = Toast.makeText(this, "Tap back button again in order to exit", Toast.LENGTH_SHORT)
        if(doubleBackToExitPressedOnce && notTwice){
            notTwice = false
            closeAppToast.show()
        }
        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}