package com.example.appv2qrscanner

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class RegisterActivity : AppCompatActivity() {
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var globalURI: Uri
    private lateinit var uploadImageButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        //leitourgikothta upload image
        uploadImageButton = findViewById<ImageView>(R.id.uploadImageBtn)
        uploadImageButton.setOnClickListener{
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1000)
        }

        val registerButton = findViewById<Button>(R.id.registerBtn)
        val emailInput = findViewById<TextView>(R.id.emailInput)
        val passwordInput = findViewById<TextView>(R.id.passwordInput)
        val nameInput = findViewById<TextView>(R.id.nameInput)
        val idInput = findViewById<TextView>(R.id.idInput)
        registerButton.setOnClickListener {
            when{
                TextUtils.isEmpty(emailInput.text.toString().trim {it <= ' '}) ->  {
                    Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(passwordInput.text.toString().trim {it <= ' '}) ->  {
                    Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(nameInput.text.toString().trim {it <= ' '}) ->  {
                    Toast.makeText(this, "Please enter a full name", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(idInput.text.toString().trim {it <= ' '}) ->  {
                    Toast.makeText(this, "Please enter a school ID", Toast.LENGTH_SHORT).show()
                }else -> {
                    val email = emailInput.text.toString()
                    val password = passwordInput.text.toString()
                    val fullName = nameInput.text.toString()
                    val schoolID = idInput.text.toString()

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(
                                OnCompleteListener { task ->
                                    //if registration is successful
                                    if(task.isSuccessful){
                                        //Firebase registered user
                                        firebaseUser = task.result!!.user!!
                                        uploadImageToFirebase(globalURI)                //to uri dhmiourgeitai apo to image button

                                        //pros8hkh onomatos kai schoolID sthn vash me kleidi to uid
                                        val db = FirebaseFirestore.getInstance()
                                        val userDocument = hashMapOf("fullname" to fullName, "schoolID" to schoolID)
                                        db.collection("users").document(firebaseUser.uid).set(userDocument)

//                                        Toast.makeText(this, "You are registered successfully", Toast.LENGTH_SHORT).show()

                                        //send verification email
//                                        firebaseUser.sendEmailVerification().addOnCompleteListener(
//                                            OnCompleteListener { task ->
//                                                if(task.isSuccessful){
//                                                    Toast.makeText(this, "You are registered successfully. A verification " +
//                                                            "has been sent to your email address", Toast.LENGTH_SHORT).show()
//                                                }else{
//                                                    Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
//                                                }
//                                            }
//                                        )




                                        val docRef = db.collection("users").document(firebaseUser.uid)
                                        docRef.get().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val document = task.result
                                                if (document.exists()) {
                                                    Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.data)
                                                    val profileUpdates = userProfileChangeRequest {
                                                        displayName = document.getString("fullname")
                                                    }
                                                    firebaseUser.updateProfile(profileUpdates)
                                                    firebaseUser.reload()
                                                } else {
                                                    Log.d(ContentValues.TAG, "No such document")
                                                }
                                            } else {
                                                Log.d(ContentValues.TAG, "get failed with ", task.exception)
                                            }
                                        }
                                        Toast.makeText(this, "You are registered successfully.", Toast.LENGTH_SHORT).show()





                                        //redirect to profile activity
//                                        val intent = Intent(this, MyProfileActivity::class.java)
//                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                        intent.putExtra("userID", firebaseUser.uid)
//                                        intent.putExtra("username", fullName)
//                                        intent.putExtra("email", email)
//                                        intent.putExtra("schoolID", schoolID)
//                                        startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && resultCode == RESULT_OK)
        {
            val imageURI = data?.data
            if (imageURI != null) {
                globalURI = imageURI
                uploadImageButton.setImageURI(globalURI)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri)
    {
        var fileRef: StorageReference = storageReference.child(firebaseUser.uid + "/profile.png")
        fileRef.putFile(imageUri)
//            .addOnSuccessListener{
//                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
//            }
            .addOnFailureListener{
                Toast.makeText(this, "Image not uploaded. Please contact an administrator", Toast.LENGTH_SHORT).show()
            }
    }
}