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
import kotlinx.android.synthetic.main.ui_staff_main.*

class Main_UI_Staff : AppCompatActivity() {
    private val database = Firebase.database
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_staff_main)

        userId = intent.getStringExtra("username").toString()

        fetchUserInfo(userId)

    }


    private fun fetchUserInfo(userId: String) { //checks for existing log for today using user ID & current date
        val path = database.getReference("users/$userId")
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val staff = snapshot.getValue<NPstaff>()
                staff?.let{
                    staffNameText.text = it.Name + "'s Dashboard"
                    staffEmailText.text = it.email
                    staffRoleText.text = it.group
                    staffSchoolText.text = it.school


                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext,"Unable to connect to the server.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    data class NPstaff(
        var Name: String? = "",
        var school: String? = "",
        var email: String? = "",
        var group: String? = ""
    )
}