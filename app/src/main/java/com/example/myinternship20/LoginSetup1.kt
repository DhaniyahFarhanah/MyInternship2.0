package com.example.myinternship20

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

        rootView.cancelButton.setOnClickListener{
            dismiss()
        }

        return rootView
    }
}