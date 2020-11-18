package com.ngeeann.myinternship20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.uiintern.*

class UIintern : AppCompatActivity() {
    val database = Firebase.database
    val username = intent.getStringExtra("Name")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uiintern)

        val username = intent.getStringExtra("Name")

        internLog.setOnClickListener {
            startActivity(Intent(this, Log_Intern::class.java))
        }
        internAttendance.setOnClickListener {
            startActivity(Intent(this, Attendance_Intern::class.java))
        }


    }

    fun populateScreen() {
        val path = database.getReference("users/$username")
    }
}