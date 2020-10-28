package com.example.myinternship20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.loginscreen.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginscreen)

        setupButton.setOnClickListener {
            var setup = LoginSetup1()

            setup.show(supportFragmentManager, "setup1")
        }

    }
}