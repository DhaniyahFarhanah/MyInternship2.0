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
import kotlinx.android.synthetic.main.uistaff.*

class UIStaff : AppCompatActivity() {
    private val database = Firebase.database
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uistaff)
        userId = intent.getStringExtra("username").toString()

        fetchUserInfo(userId)

        staffStudentData.setOnClickListener{
            startActivity(Intent(this, NPIS_StudentDataHome::class.java))
        }
    }


    private fun fetchUserInfo(userId: String) { //checks for existing log for today using user ID & current date
        val path = database.getReference("users/$userId")
        staffIdText.text = userId
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val staff = snapshot.getValue<Staff>()
                staff?.let{
                    staffNameText.text = it.Name
                    staffSchoolText.text = it.school
                    staffSubjectText.text = it.subject
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext,"Unable to connect to the server.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    data class Staff(
        var Name: String? = "",
        var school: String? = "",
        var subject: String? = ""
    )
}