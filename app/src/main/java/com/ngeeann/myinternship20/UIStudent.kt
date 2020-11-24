package com.ngeeann.myinternship20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.uiintern.*
import kotlinx.android.synthetic.main.uistudent.*

class UIStudent : AppCompatActivity() {
    val database = Firebase.database
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uistudent)
        username = intent.getStringExtra("username").toString()
        fetchUserInfo(username)

        Toast.makeText(this,"Welcome", Toast.LENGTH_SHORT).show()


    }

    private fun fetchUserInfo(username: String) {
        val namePath = database.getReference("users/$username/Name")
        val schoolPath = database.getReference("users/$username/School")
        val coursePath = database.getReference("users/$username/Course")
        var studSchool: String?
        var studCourse: String?

        studentTitleText.text = username

        namePath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val intName = snapshot.getValue<String>() //Instantiates val with value of name
                studentIdText.text = intName//Populates value into textview in the UI layout
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        schoolPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studSchool = snapshot.getValue<String>()
                coursePath.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        studCourse = snapshot.getValue<String>()
                        studentSchoolText.text = "$studSchool / $studCourse"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}