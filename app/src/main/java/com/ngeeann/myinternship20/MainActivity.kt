package com.ngeeann.myinternship20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.loginscreen.*

class MainActivity : AppCompatActivity() {
    val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginscreen)

        setupButton.setOnClickListener {
            val setup = LoginSetup1()

            setup.show(supportFragmentManager, "setup1")
        }
        loginButton.setOnClickListener {
            if(idTextBox.text.toString() =="" || passwordTextBox.text.toString()==""){ //checks if textboxes are blank
                Toast.makeText(this,"ID or Password not entered",Toast.LENGTH_SHORT).show()
            }
            else{
                loginQuery()
            }

        }

    }

    private fun loginQuery() {
        val username = idTextBox.text   // pull info from textview
        val password = passwordTextBox.text.toString()
        val pwRef = database.getReference("users/$username/password")

        pwRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dbPassword = dataSnapshot.getValue<String>()
                if (dbPassword == password) {
                    Toast.makeText(baseContext, "Login successful.",
                        Toast.LENGTH_SHORT).show()
                    grpQuery()
                }
                else {
                    Toast.makeText(baseContext, "Login failed",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "There was an error logging in, please try again.",
                    Toast.LENGTH_SHORT).show()
                //Log.w(LoginActivity.TAG, "There was an error in the login process, please try again!", error.toException())    //error message
            }
        })
    }

    private fun grpQuery(){
        val username = idTextBox.text.toString()
        val grpRef = database.getReference("users/$username/group")
        val studIntent = Intent(this, UIStudent::class.java).putExtra("username",username)
        val intIntent = Intent(this, UIintern::class.java).putExtra("username",username)
        val staffIntent = Intent(this, UIStaff::class.java).putExtra("username",username)

        grpRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                when (dataSnapshot.getValue<String>().toString()) {
                    "student" ->  startActivity(studIntent) //start UIStudent actvity
                    "intern" -> startActivity(intIntent) //start UIintern actvity
                    else -> {
                        startActivity(staffIntent) //start UIStaff actvity
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "There was an error fetching the group value, please try again.",
                        Toast.LENGTH_SHORT).show()//ERROR MESSAGE
            }
        })
    }

    companion object {
        private const val TAG = "PostDetailActivity"
    }
}