package com.ngeeann.myinternship20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Spinner
import kotlinx.android.synthetic.main.npis_studentdatahome.*

class NPIS_StudentDataHome : AppCompatActivity() {

    lateinit var spinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.npis_studentdatahome)

        npisDatabackarrow.setOnClickListener {
            this.finish()
        }

    }
}