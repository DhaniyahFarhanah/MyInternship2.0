package com.ngeeann.myinternship20

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.intern_log.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class Log_Intern : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intern_log)
        database = Firebase.database.reference
        val username = intent.getStringExtra("username")

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val currentDate= LocalDate.now().format(formatter)
        logDateLabel.text=currentDate.toString()

        logBackArrow.setOnClickListener {
            this.finish()
        }

        logSubmitButton.setOnClickListener{
            if (username != null) {
                newLog(username, "username", currentDate, logDataEntered.text.toString())
            }
            Toast.makeText(this,"Daily Log For Today Successfully Submitted", Toast.LENGTH_SHORT).show()
        }


    }

    fun newLog(userId: String, name: String, date: String, text: String) {
        val key = database.child("internlogs").push().key
        if (key == null) {
            //Log.w(TAG, "Couldn't get push key for log")
            return
        }

        val log = Log(userId, name, date, text)
        val logValues = log.toMap()

        val childUpdates = hashMapOf<String, Any>(
                "/internlogs/$key" to logValues,
                "/users/$userId/internlogids/$key" to logValues
        )

        database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this,"Daily Log For Today Successfully Submitted", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Daily Log was unable to submit please try again.", Toast.LENGTH_SHORT).show()
                }
    }

    fun checkLog(userId: String) { //checks for existing log for today using date, user ID
        val path = database.child("users").child(userId).child("internlogids")
        //if exist, pass log text into textbox
    }

    data class Log(
            var uid: String? = "",
            var author: String? = "",
            var date: String? = "",
            var log: String? = ""
    ){
        fun toMap(): Map<String, Any?> {
            return mapOf (
                    "userid" to uid,
                    "author" to author,
                    "date" to date,
                    "log" to log
            )
        }
    }
}