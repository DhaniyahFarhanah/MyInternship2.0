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
import kotlinx.android.synthetic.main.ui_intern_main.*

class Main_UI_Intern : AppCompatActivity() {
    private val database = Firebase.database
    lateinit var userId: String
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_intern_main)
        userId = intent.getStringExtra("username").toString()

        fetchUserInfo(userId)
        internLog.setOnClickListener {
            startActivity(Intent(this, Intern_Log::class.java)
                    .putExtra("userId", userId)
                    .putExtra("username", username))
        }
        internAttendance.setOnClickListener {
            startActivity(Intent(this, Student_Attendance::class.java)
                    .putExtra("userId", userId)
                    .putExtra("username", username)
                    .putExtra("group", "Intern"))
        }
    }

    private fun fetchUserInfo(userId: String) { //checks for existing log for today using user ID & current date
        val path = database.getReference("users/$userId")

        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val intern = snapshot.getValue<Intern>()
                intern?.let{
                    internTitleText.text= it.Name + "'s Dashboard"
                    username = it.Name.toString()
                    internIdText.text = it.StudID
                    internSchoolText.text = it.Course + " / " + it.School
                    internAddressText.text = it.address + " / " + it.postal
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext,"Unable to connect to the server.",Toast.LENGTH_SHORT).show()
            }
        })
    }

    data class Intern(
        var Course: String? = "",
        var Name: String? = "",
        var StudID: String?= "",
        var School: String? = "",
        var address: String? = "",
        var postal: String? = ""
    ){
        fun toMap(): Map<String, Any?> {
            return mapOf(
                "Course" to Course,
                "Name" to Name,
                "StudID" to StudID,
                "School" to School,
                "address" to address,
                "postal" to postal
            )
        }
    }
}