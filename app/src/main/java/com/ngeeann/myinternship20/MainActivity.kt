package com.ngeeann.myinternship20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.loginscreen.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginscreen)

        setupButton.setOnClickListener {
            var setup = LoginSetup1()

            setup.show(supportFragmentManager, "setup1")
        }
        loginButton.setOnClickListener {
            if(idTextBox.text.toString() =="" || passwordTextBox.text.toString()==""){
                Toast.makeText(this,"ID or Password not entered",Toast.LENGTH_SHORT).show()
            }
            else{
                startActivity(Intent(this, SplashScreen::class.java)) //successful login
            }
        }

    }
}