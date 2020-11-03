package com.ngeeann.myinternship20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class UIStudent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uistudent)

        Toast.makeText(this,"Welcome", Toast.LENGTH_SHORT).show()
    }
}