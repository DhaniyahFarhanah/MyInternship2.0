package com.example.myinternship20

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.loginsetup1.view.*

class LoginSetup1: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View= inflater.inflate(R.layout.loginsetup1,container)
        val intent= Intent(activity,SetupIntern::class.java)

        rootView.cancelButton.setOnClickListener{
            dismiss()
        }
        rootView.internButton.setOnClickListener {
            intent.putExtra("userType", "Intern")
            startActivity(intent)
        }
        rootView.npisButton.setOnClickListener {
            intent.putExtra("userType","NPIS")
            startActivity(intent)
        }
        rootView.studentButton.setOnClickListener {
            intent.putExtra("userType","Student")
            startActivity(intent)
        }
        rootView.npStaffButton.setOnClickListener {
            intent.putExtra("userType","NP Staff")
            startActivity(intent)
        }

        return rootView
    }
}