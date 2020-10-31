package com.example.myinternship20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.loginsetup1.*
import kotlinx.android.synthetic.main.setup_intern.*

class SetupIntern : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_intern)

        val type = intent.getStringExtra("userType").toString()

        setupUserType.text="$type setup"
        setupUserEmail.hint="$type E-mail"
        setupUserID.hint="$type ID"

        if(type=="Intern"){
            setupIntern.visibility=View.VISIBLE
        }
        registerButton.setOnClickListener {
            if(setupUserEmail.text.toString()==""||setupUserID.text.toString()==""||setupPassword.text.toString()==""){
                Toast.makeText(this,"Fill in empty blanks",Toast.LENGTH_SHORT).show()
            }
            else{//successful creation
                startActivity(Intent(this,MainActivity::class.java))//successful registeration
                Toast.makeText(this,"Successfully created $type User",Toast.LENGTH_SHORT).show()
            }
        }
        setupCancelButton.setOnClickListener{
            finish()
        }

    }
}