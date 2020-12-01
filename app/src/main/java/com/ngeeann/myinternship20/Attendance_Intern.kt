package com.ngeeann.myinternship20

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.intern_attendance.*
import java.time.DayOfWeek

class Attendance_Intern : AppCompatActivity() { //TODO rework attendance storage system on database and build the attendance system
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intern_attendance)
        val userId = intent.getStringExtra("userId")

        attendanceBackArrow.setOnClickListener {
            this.finish()
        }
        if (attendanceStatus.text=="Upcoming"){
            attendanceLinear.background=null
            attendText.setTextColor(Color.parseColor("#B5B5B5"))
            attendanceAttendButton.isClickable=false
        }
    }

    private fun checkClass(userId: String, dayOfWeek: DayOfWeek) { //uses day of the week and current time to check if there's an ongoing class, returns value of class and time
        val path = database.getReference("users/$userId/Modules/$dayOfWeek").orderByKey()
        //val path2 = database.child("users").child(userId).child("Modules").child(day).orderByChild()
    }
    // attend class function, student presses this and their name will appear in the records for the class on this date
}