package com.ngeeann.myinternship20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.uiintern.*
import kotlinx.android.synthetic.main.uistaff.*

class UIStaff : AppCompatActivity() {
    val database = Firebase.database
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uistaff)
        username = intent.getStringExtra("username").toString()

        fetchUserInfo(username)
    }

    private fun fetchUserInfo(username: String) {
        val namePath = database.getReference("users/$username/Name")
        val schoolPath = database.getReference("users/$username/school")
        val subPath = database.getReference("users/$username/subject")
        var staffSchool: String?
        var staffSubject: String?

        staffIdText.text = username

        namePath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val staffName = snapshot.getValue<String>() //Instantiates val with value of name
                staffNameText.text = staffName//Populates value into textview in the UI layout
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        schoolPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                staffSchool = snapshot.getValue<String>()
                staffSchoolText.text = staffSchool
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        subPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                staffSubject = snapshot.getValue<String>()
                staffSubjectText.text = staffSubject
            }

            override fun onCancelled(error: DatabaseError) {
                staffSubjectText.text = ""
            }
        })
    }
}