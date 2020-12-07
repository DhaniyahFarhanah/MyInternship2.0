package com.ngeeann.myinternship20

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.intern_attendance.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class Attendance_Intern : AppCompatActivity() { //TODO rework attendance storage system on database and build the attendance system
    val database = Firebase.database
    lateinit var currentLesson: Lesson

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intern_attendance)
        val userId = intent.getStringExtra("userId") //fetches userId from the main screen to use for querying database
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK) //gets the day of the week as a numeral e.g. 0 is Saturday
        val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) //Date format: yyyy-MM-dd
        val time = LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) //Time format: hh:MM:ss.SSS

        checkClass(userId.toString(), 2, "11:03") //WHEN TESTING MAKE SURE THE HOUR PORTION HAS 2 DIGITS e.g. 8 am is 08:00
        checkAttendance(date)

        attendanceBackArrow.setOnClickListener { //returns back to UIStudent/UIIntern main screen
            this.finish()
        }

        attendanceAttendButton.setOnClickListener { //should only be clickable when lesson has started (startTime <= localtime < endTime)
            val title = "${currentLesson.name}-${currentLesson.group}"
            if(LocalTime.parse("11:03")/*LocalTime.now()*/<=currentLesson.startTime.plusMinutes(5)) { //that means its "On Time" (within 5 minutes)//TODO replace "08:02" with LocalTime.now()
                database.getReference("attendance/$date/$title/present/$userId").setValue(time)
            }
            else {
                database.getReference("attendance/$date/$title/late/$userId").setValue(time)
            }
            attendanceLinear.background=null
            attendText.setTextColor(Color.parseColor("#B5B5B5"))
            attendanceAttendButton.isClickable = false
            Toast.makeText(this, "Attendance has been marked, you may now close this page.", Toast.LENGTH_SHORT).show()
        }

        attendanceMCButton.setOnClickListener { //TODO advanced MC button will involve adding in the file uploading thing and then mark all lessons for the day with MC
            val title = "${currentLesson.name}-${currentLesson.group}"
            database.getReference("attendance/$date/$title/mc/$userId").setValue("sick")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkClass(userId: String, dayNum: Int, time: String) { //uses day of the week and current time to check if there's an ongoing class, returns value of class and time
        val weekArray = arrayOf<String>("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday") //array to convert numeral value of
        // day of the week to the string name
        val dayOfWeek = weekArray[dayNum] //query path will not take an array element directly so I initialized another value just for it
        val path = database.getReference("users/$userId/Modules/$dayOfWeek").orderByChild("EndAt").startAt(time) //checks for any classes that
        // match the given time
        val localTime = LocalTime.parse(time) //converts the Time string value into a Time LocalTime unit value

        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    var currLess: Int = 0 //0 is the default value as it is also the first lesson in the day
                    val nameArr = arrayOfNulls<String>(snapshot.childrenCount.toInt())
                    val locArr = arrayOfNulls<String>(snapshot.childrenCount.toInt())
                    val grpArr = arrayOfNulls<String>(snapshot.childrenCount.toInt())

                    for ((n, classSnapshot) in snapshot.children.withIndex()) {
                        val startAt = classSnapshot.child("StartAt").getValue<String>() //can merge into the LocalTime Parser reducing the amount of memory consumed
                        val endAt = classSnapshot.child("EndAt").getValue<String>()
                        val startTime = LocalTime.parse(startAt)
                        val endTime = LocalTime.parse(endAt)

                        classSnapshot.child("Name").getValue<String>().also { nameArr[n] = it.toString() }
                        classSnapshot.child("Location").getValue<String>().also { locArr[n] = it.toString() }
                        classSnapshot.child("Group").getValue<String>().also { grpArr[n] = it.toString() }

                        if (localTime >= startTime.minusMinutes(15) && localTime < startTime) { //checks if there is an upcoming lesson within 15 minutes
                            val diffTime = Duration.between(localTime, startTime).toMinutes()
                            attendanceStatus.text = "UPCOMING IN ${diffTime.toString()} MINUTES"
                            currLess = n
                            attendanceLinear.background=null
                            attendText.setTextColor(Color.parseColor("#B5B5B5"))
                            attendanceAttendButton.isClickable=false
                        } else if (localTime in startTime..endTime) { //checks if current time is in the middle of a lesson
                            attendanceStatus.text = "CURRENTLY ATTENDING"
                            currLess = n
                            currentLesson = Lesson(grpArr[currLess], nameArr[currLess], startTime)
                        }
                    }
                    attendanceSet.text = nameArr[currLess]
                    attendanceLocation.text = locArr[currLess]
                }
                else { //a third state that only appears when there are no lessons found within the query parameters e.g. lessons have ended for the day or no lessons
                    attendanceSet.text = "See you tomorrow!"
                    attendanceLocation.text = ""
                    attendanceAttendButton.isVisible = false
                    attendanceLateWarning.text = ""
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun checkAttendance(userId: String, date: String) {
        val path = database.getReference("attendance/$date/${currentLesson.title}").orderByChild(userId).equalTo()
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(userId).exists()){

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    data class Lesson(
            var group: String? = "",
            var name: String? = "",
            var startTime: LocalTime
    ) {
        var title = "$name-$group"
    }
}