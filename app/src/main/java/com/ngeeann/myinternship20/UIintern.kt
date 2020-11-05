package com.ngeeann.myinternship20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.uiintern.*

class UIintern : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uiintern)

        internLog.setOnClickListener {
            startActivity(Intent(this, Log_Intern::class.java))
        }
    }


}