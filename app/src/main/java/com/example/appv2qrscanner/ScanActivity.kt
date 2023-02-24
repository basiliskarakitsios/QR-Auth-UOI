package com.example.appv2qrscanner


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*
import android.R.id
import android.content.ContentValues.TAG

import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentSnapshot

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener







private const val CAMERA_REQUEST_CODE = 101

class ScanActivity : AppCompatActivity()
{
    private lateinit var codeScanner: CodeScanner

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val actionBar = supportActionBar
        actionBar!!.title = "Scan a QR code"
        actionBar.setDefaultDisplayHomeAsUpEnabled(true)

        setupPermissions()
        codeScanner()
    }

    private fun codeScanner()
    {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            val fullnameTextView = findViewById<TextView>(R.id.fullnameView)
            val schoolIDTextView = findViewById<TextView>(R.id.schoolIDView)
            val profileImage = findViewById<ImageView>(R.id.userImage)
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference
            val db = FirebaseFirestore.getInstance()

            decodeCallback = DecodeCallback {
                runOnUiThread{
                    val userID = it.text
                    val docRef = db.collection("users").document(userID)

                    docRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.data)
                                fullnameTextView.text = "Full Name: " + document.getString("fullname")
                                schoolIDTextView.text = "School ID: " + document.getString("schoolID")
                            } else {
                                Log.d(TAG, "No such document")
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.exception)
                        }
                    }

                    val profileRef: StorageReference = storageReference.child("$userID/profile.png")
                    profileRef.downloadUrl.addOnSuccessListener(OnSuccessListener {
                        Picasso.get().load(it).placeholder(R.drawable.profilepic).into(profileImage)})
                }
            }

            errorCallback = ErrorCallback{
                runOnUiThread {
                    Log.e("Main", "Camera initialization error: ${it.message}")
                }
            }
        }

        //if not continuous
//        scannerView.setOnClickListener {
//            codeScanner.startPreview()
//        }
    }

    override fun onResume()
    {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions()
    {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED)
        {
            makeRequest()
        }
    }

    private fun makeRequest()
    {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode)
        {
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "You need the camera permission to be able to use this app!", Toast.LENGTH_SHORT)
                }else{
                    //successful
                }
            }
        }
    }
//    Prwtos tropos
//    private lateinit var codeScanner: CodeScanner
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_scan)
//        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
//        setupPermissions()
//
//        codeScanner = CodeScanner(this, scannerView)
//
//        // Parameters (default values)
//        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
//        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
//        // ex. listOf(BarcodeFormat.QR_CODE)
//        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
//        codeScanner.scanMode = ScanMode.CONTINUOUS // or SINGLE or PREVIEW
//        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
//        codeScanner.isFlashEnabled = false // Whether to enable flash or not
//
//        // Callbacks
//        val textView = findViewById<TextView>(R.id.tv_textView)
//
//        codeScanner.decodeCallback = DecodeCallback {
//            runOnUiThread{
//                textView.text = it.text
//            }
//        }
//        codeScanner.errorCallback = ErrorCallback{
//            runOnUiThread {
//                Log.e("Main", "Camera initialization error: ${it.message}")
//            }
//        }
//
//        scannerView.setOnClickListener {
//            codeScanner.startPreview()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        codeScanner.startPreview()
//    }
//
//    override fun onPause() {
//        codeScanner.releaseResources()
//        super.onPause()
//    }
//
//    private fun setupPermissions()
//    {
//        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
//
//        if(permission != PackageManager.PERMISSION_GRANTED)
//        {
//            makeRequest()
//        }
//    }
//
//    private fun makeRequest()
//    {
//        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
//    }
//
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when(requestCode)
//        {
//            CAMERA_REQUEST_CODE ->{
//                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
//                {
//                    Toast.makeText(this, "You need the camera permission to be able to use this app!", Toast.LENGTH_SHORT)
//                }else{
//                    //successful
//                }
//            }
//        }
//    }
}