package com.example.appv2qrscanner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.appv2qrscanner.databinding.ActivityMyProfileBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener

import com.google.firebase.auth.UserProfileChangeRequest

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import android.graphics.BitmapFactory

import com.google.firebase.storage.FileDownloadTask

import android.R.id
import android.content.ContentValues
import com.google.firebase.storage.UploadTask
import java.io.File
import java.lang.Exception
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class MyProfileActivity : AppCompatActivity() {

    private lateinit var usernameField: TextView
    private lateinit var emailField: TextView
    private lateinit var schoolIDField: TextView
    private lateinit var profileImageField: ImageView
    private lateinit var qrCode: ImageView

    private val db = FirebaseFirestore.getInstance()

    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference

    private lateinit var binding: ActivityMyProfileBinding
    private val auth = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)
        if(auth == null){
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            createUI()
        }

        //leitourgikothta tou logout
        val logoutButton = findViewById<Button>(R.id.logoutBtn)
        logoutButton.setOnClickListener{
            AuthUI.getInstance().signOut(this).addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "You're logged out", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun createUI(){
        auth?.let {
            usernameField = findViewById<TextView>(R.id.textName)
//            emailField = findViewById<TextView>(R.id.textEmail)
            schoolIDField = findViewById<TextView>(R.id.textSchoolID)
            profileImageField = findViewById<ImageView>(R.id.userImage)
            qrCode = findViewById<ImageView>(R.id.userQR)

            //gia na parw to schoolID kai fullname
            val docRef = db.collection("users").document(auth.uid)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                        usernameField.text = "Full Name: " + auth.displayName
//                        emailField.text = auth.email
                        schoolIDField.text = "School ID: " + document.getString("schoolID")
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                    }
                } else {
                    Log.d(ContentValues.TAG, "get failed with ", task.exception)
                }
            }


            val profileRef: StorageReference = storageReference.child(auth?.uid + "/profile.png")
            profileRef.downloadUrl.addOnSuccessListener(OnSuccessListener {
                Picasso.get().load(it).placeholder(R.drawable.profilepic).into(profileImageField)})

            //olo to block einai gia th dhmiourgia tou QR
            val content = auth.uid                  //to qr ginetai vasei tou monadikou ID tou xrhsth
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 700, 700)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            qrCode.setImageBitmap(bitmap)
        }
    }


    override fun onResume() {
        super.onResume()
        if(auth != null)
        {
            createUI()
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }
}

