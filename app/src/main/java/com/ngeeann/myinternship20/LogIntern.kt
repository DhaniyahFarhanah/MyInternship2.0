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

class LogIntern : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intern_log)

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

}