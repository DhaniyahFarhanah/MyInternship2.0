package com.ngeeann.myinternship20

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.ngeeann.myinternship20.databinding.LoginscreenBinding
import kotlinx.android.synthetic.main.loginscreen.*

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DatabaseReference


class LoginActivity : AppCompatActivity(){
    lateinit var binding: LoginscreenBinding
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun loginQuery() {
        val username = idTextBox.text   // pull info from textview
        val password = passwordTextBox.text
        val pwRef = database.getReference("users/$username/password")

        pwRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dbPassword = dataSnapshot.getValue<String>()
            }

            override fun onCancelled(error: DatabaseError) {
                //error message
            }
        })
        if (dbPassword == password) {

        }
    }
}