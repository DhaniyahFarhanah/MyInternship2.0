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

class Attendance_Intern : AppCompatActivity() { //TODO fix the stupid red line with some actual text
    val database = Firebase.database
    lateinit var currentLesson: Lesson

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intern_attendance)
        val userId = intent.getStringExtra("userId") //fetches userId from the main screen to use for querying database
        val userName = intent.getStringExtra("username")
        val userGroup = intent.getStringExtra("group")
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK) //gets the day of the week as a numeral e.g. 0 is Saturday
        val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) //Date format: yyyy-MM-dd
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH mm ss")) //Time format: 17:12:47

        checkClass(userId.toString(), 3, "13:00") //WHEN TESTING MAKE SURE THE HOUR PORTION HAS 2 DIGITS e.g. 8 am is 08:00

        attendanceBackArrow.setOnClickListener { //returns back to UIStudent/UIIntern main screen
            this.finish()
        }

        attendanceAttendButton.setOnClickListener { //should only be clickable when lesson has started (startTime <= localtime < endTime)
            if(userGroup == "Student") { //Student Mode
                val title = "${currentLesson.name}-${currentLesson.group}" //should only work if its student since intern's do not have a group
            }
            else {  //Intern Mode
                val title = currentLesson.name
            }
            if(/*LocalTime.now()*/LocalTime.parse("13:00")<=currentLesson.startTime.plusMinutes(5)) { //that means its "On Time" (within 5 minutes)//TODO replace "08:02" with LocalTime.now()
                currentLesson.status = "Present"
                currentLesson.userName = userName.toString()
                //currentLesson.entryTime = time
                //database.getReference("attendance/$date/$title/Present/$userId").setValue(time)
                val lessonValues = currentLesson.toInternMap()

                val attendUpdates = hashMapOf<String, Any>(
                        "$userId" to lessonValues
                )

                database.getReference("attendance/$date/${currentLesson.title}").updateChildren(attendUpdates)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Attendance submitted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            Toast.makeText(this, "Attendance was unable to submit please try again.", Toast.LENGTH_SHORT).show()
                        }
            }
            else {
                currentLesson.status = "Late"
                database.getReference("attendance/$date/$title/$userId").setValue(time)
            }
            attendButtonClicked()
        }

        attendanceMCButton.setOnClickListener { //TODO advanced MC button will involve adding in the file uploading thing and then mark all lessons for the day with MC
            if(userGroup == "Student") {
                val title = "${currentLesson.name}-${currentLesson.group}"
            }
            else {
                val title = currentLesson.name
            }
            currentLesson.status = "MC"
            database.getReference("attendance/$date/$title/mc/$userId").setValue("sick")
            attendButtonClicked()
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
                    checkAttendance(userId, "2020-12-08")
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
        val path = database.getReference("attendance/$date/${currentLesson.title}").child(userId)
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){ //allow User to press attendance button
                    val attendStat = snapshot.child("Status").getValue<String>()
                    if(snapshot.child("Status").getValue<String>() == "Absent") {
                        attendanceLateWarning.text = "Please mark your attendance within 5 minutes of class starting!"
                    }
                    else {
                        attendanceLateWarning.text = "Attendance Status: $attendStat"
                        attendanceLateWarning.setTextColor(Color.parseColor("#0ae973"))
                        attendButtonClicked()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun attendButtonClicked () {
        attendanceLinear.background=null
        attendText.setTextColor(Color.parseColor("#B5B5B5"))
        attendanceAttendButton.isClickable = false //once attendance has been submitted, both buttons will be disabled for that lesson
        mcLinear.background = null
        mcText.setTextColor(Color.parseColor("#B5B5B5"))
        attendanceMCButton.isClickable = false
        Toast.makeText(this, "Attendance has been marked, you may now close this page.", Toast.LENGTH_SHORT).show()
    }

    data class Lesson(
            var group: String? = "",
            var name: String? = "",
            var startTime: LocalTime
    ) {
        var entryTime: String = "14:00"
        var title: String = "$name-$group" //todo fix this later PLEASE
        var userName: String = ""
        var status: String = ""

        fun toInternMap(): Map<String, Any?> {
            return mapOf(
                    "EntryTime" to entryTime, //possible issue trying to convert map value of LocalTime
                    "Name" to userName,
                    "Status" to status
            )
        }

        fun toStudMap(): Map<String, Any?>{ //its the same, maybe remove?
            return mapOf(
                    "EntryTime" to entryTime,
                    "Name" to name,
                    "Status" to status
            )
        }
    }
}