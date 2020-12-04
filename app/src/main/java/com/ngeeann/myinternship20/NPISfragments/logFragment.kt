package com.ngeeann.myinternship20.NPISfragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.ngeeann.myinternship20.R
import kotlinx.android.synthetic.main.fragment_log.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class logFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)



        return view
    }


}