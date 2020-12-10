package com.ngeeann.myinternship20

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.intern_log.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.log

class Intern_Log : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var existKey: String
    private var logExists = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intern_log)
        database = Firebase.database.reference
        val userId = intent.getStringExtra("userId") //receives userId and username info from UIintern activity
        val username = intent.getStringExtra("username")
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy") //fetches current date to be use for log activity
        val currentDate= LocalDate.now().format(formatter)

        logDateLabel.text = currentDate.toString() //Displays current date at the top of the screen
        fetchKey(userId.toString(), currentDate) //Checks if there is an existing log for the day

        logBackArrow.setOnClickListener {
            this.finish()
        }

        logSubmitButton.setOnClickListener{ //log submission function called will depend on whether this is a new log or an existing log
            if(logExists) {
                amendLog(existKey) //changes text for existing log
            }
            else {
                if (userId != null) {
                    newLog(userId, username.toString(), currentDate, logDataEntered.text.toString()) //creates a new log for the day
                }
            }
        }
    }

    private fun newLog(userId: String, name: String, date: String, text: String) {
        val key = database.child("internlogs").push().key
        if (key == null) {
            Toast.makeText(this, "Daily log was unable to submit please try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val log = Log(userId, name, date, text)
        val logValues = log.toMap()

        val childUpdates = hashMapOf<String, Any>(
                "/internlogs/$key" to logValues
        )

        database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Daily log for today successfully submitted", Toast.LENGTH_SHORT).show()
                    logExists = true
                    existKey = key
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Daily log was unable to submit please try again.", Toast.LENGTH_SHORT).show()
                }
        database.child("users").child(userId).child("internlogids").child(date).setValue(key)
    }

    private fun amendLog(key: String) {
        val text = logDataEntered.text.toString()
        database.child("internlogs").child(key).child("log").setValue(text)
                .addOnSuccessListener {
                    Toast.makeText(this, "Daily log for today has been successfully amended.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Daily log was unable to be amended please try again.", Toast.LENGTH_SHORT).show()
                }
    }

    private fun fetchKey(userId: String, date: String) { //fetches ID key from user profile to access the intern logs with
        val path: DatabaseReference = database.child("users").child(userId).child("internlogids").child(date)
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    logExists = true
                    existKey = snapshot.getValue<String>().toString()
                    checkLog(existKey)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Unable to fetch intern log key.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun checkLog(key: String) { //checks for existing log for today using user ID & current date
        val path = database.child("internlogs").child(key)
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val todayLog = snapshot.getValue<Log>()
                todayLog?.let{
                    logDataEntered.setText(it.log) //displays existing log for the day
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext,"Unable to fetch post. Please check your internet connection.",Toast.LENGTH_SHORT).show()
            }
        })
    }

    data class Log(
            var uid: String? = "",
            var author: String? = "",
            var date: String? = "",
            var log: String? = ""
    ){
        fun toMap(): Map<String, Any?> {
            return mapOf(
                    "userid" to uid,
                    "author" to author,
                    "date" to date,
                    "log" to log
            )
        }
    }
}