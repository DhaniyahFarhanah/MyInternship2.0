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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.uiintern.*

class UIintern : AppCompatActivity() { //TODO Transfer User's name into the Log function for the Author input, add in postal code and address later on
    val database = Firebase.database
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uiintern)
        username = intent.getStringExtra("username").toString()

        fetchUserInfo(username)
        internLog.setOnClickListener {
            startActivity(Intent(this, Log_Intern::class.java)
                    .putExtra("username", username))
                    //.putExtra("studName", intName)
        }
        internAttendance.setOnClickListener {
            startActivity(Intent(this, Attendance_Intern::class.java))
        }
    }

    private fun fetchUserInfo(username: String) {
        val namePath = database.getReference("users/$username/Name")
        val schoolPath = database.getReference("users/$username/School")
        val coursePath = database.getReference("users/$username/Course")
        val addPath = database.getReference("users/$username/address")
        val postPath = database.getReference("users/$username/postal")
        var intSchool: String?
        var intCourse: String?
        var intAddress: String?
        var intPost: String?

        internTitleText.text = "$username Dashboard"//part of the user info display process

        namePath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val intName = snapshot.getValue<String>() //Instantiates val with value of name
                internIdText.text = intName//Populates value into textview in the UI layout
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        schoolPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                intSchool = snapshot.getValue<String>()
                coursePath.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        intCourse = snapshot.getValue<String>()
                        internSchoolText.text = "$intSchool / $intCourse"
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

        addPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                intAddress = snapshot.getValue<String>()
                postPath.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        intPost = snapshot.getValue<String>()
                        internAddressText.text = "$intAddress $intPost"
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