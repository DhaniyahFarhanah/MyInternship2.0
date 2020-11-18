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

        var formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val currentDate= LocalDate.now().format(formatter)
        logDateLabel.text=currentDate.toString()

        logBackArrow.setOnClickListener {
            startActivity(Intent(this, UIintern::class.java))
        }

        logSubmitButton.setOnClickListener{
            startActivity(Intent(this, UIintern::class.java))
            Toast.makeText(this,"Daily Log For Today Successfully Submitted", Toast.LENGTH_SHORT).show()
        }


    }

    fun newLog(userId: String, username: String, title: String, body: String) {
        val key = database.child("internlogs").push().key
        if (key == null) {
            //Log.w(TAG, "Couldn't get push key for log")
            return
        }

        val log = Log(userId, username, title, body)
        val logValues = log.toMap()

        val childUpdates = hashMapOf<String, Any>(
                "/internlogs/$key" to logValues,
                "/user-posts/$userId/$key" to logValues
        )

        database.updateChildren(childUpdates)
    }

    data class Log(
            var uid: String? = "",
            var author: String? = "",
            var title: String? = "",
            var body: String? = ""
    ){
        fun toMap(): Map<String, Any?> {
            return mapOf (
                    "userid" to uid,
                    "author" to author,
                    "title" to title,
                    "body" to body
            )
        }
    }
}