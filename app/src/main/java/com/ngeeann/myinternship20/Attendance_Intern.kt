package com.ngeeann.myinternship20

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.intern_attendance.*

class Attendance_Intern : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intern_attendance)

        attendanceBackArrow.setOnClickListener {
            startActivity(Intent(this, UIintern::class.java))
        }
        if (attendanceStatus.text=="Upcoming"){
            attendanceLinear.background=null
            attendText.setTextColor(Color.parseColor("#B5B5B5"))
            attendanceAttendButton.isClickable=false
        }
    }
}