package com.ngeeann.myinternship20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.ngeeann.myinternship20.databinding.StaffAttendancedatahomeBinding
import kotlinx.android.synthetic.main.staff_attendancedatahome.*


class StaffAttendanceDataHome : AppCompatActivity() {

    private lateinit var binding: StaffAttendancedatahomeBinding//used binding again
    private var testSubject = arrayOf("64LNXSR","64BASRS") //testing array for the Subjects
    private var testClass = arrayOf("LB11","LB12","LB13") //testing array for the Class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= StaffAttendancedatahomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var chosenModule=""
        var chosenGroup=""

        binding.staffStudentDataBackArrow.setOnClickListener {
            this.finish()
        }

        binding.staffSubjectSpinner.adapter=ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,testSubject)
        binding.staffGroupSpinner.adapter=ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,testClass)


        //initialize chosenModule with the selected item in the array for Module drop box
        binding.staffSubjectSpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenModule = testSubject[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        //initialize chosenGroup with the selected item in the array for Group drop box
        }
        binding.staffGroupSpinner.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenGroup = testClass[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
        //on click of the more details
        moreDetailsButton.setOnClickListener {
            startActivity(Intent(this,StaffAttendanceDataMoreInfo::class.java).
                putExtra("chosenModule",chosenModule).putExtra("chosenGroup",chosenGroup))//transfer the chosenModule and Group to the next Activity
        }

    }
}