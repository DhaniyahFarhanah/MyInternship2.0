package com.ngeeann.myinternship20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.BundleCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngeeann.myinternship20.NPISfragments.AttendanceDataFragment
import com.ngeeann.myinternship20.NPISfragments.OverviewFragment
import com.ngeeann.myinternship20.NPISfragments.logFragment
import kotlinx.android.synthetic.main.npis_studentdatahome.*

class NPIS_StudentDataHome : AppCompatActivity() {


    private var nameTest = arrayOf("Jessica", "Adams", "Why")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.npis_studentdatahome)

        val npisBottomNav= findViewById<BottomNavigationView>(R.id.npisBottomNav)

        npisBottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_overview -> {
                    overviewLayout.visibility=View.VISIBLE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.GONE
                }
                R.id.nav_log -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.VISIBLE
                    attendanceLayout.visibility=View.GONE
                }
                R.id.nav_attendance -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.VISIBLE
                }
            }
            true
        }//navigation
        val adapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,nameTest)

        studentDataSpinner.adapter=adapter

        studentDataSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStudent:String=nameTest[position]
                Toast.makeText(applicationContext,"$selectedStudent's Data",Toast.LENGTH_SHORT).show()

                overviewText.text="$selectedStudent's Overview"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        npisDatabackarrow.setOnClickListener {
            this.finish()
        }


    }
}


