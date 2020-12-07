package com.ngeeann.myinternship20

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ngeeann.myinternship20.databinding.NpisStudentdatahomeBinding
import kotlinx.android.synthetic.main.npis_studentdatahome.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class NPIS_StudentDataHome : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: NpisStudentdatahomeBinding
    private var nameTest = arrayOf("Jessica", "Adams", "Why")//testing array for the names in the spinner for the NPIS staff to choose
    val cal=Calendar.getInstance()

    var day = 0
    var month = 0
    var year = 0

    var chosenDay = 0
    var chosenMonth = 0
    var chosenYear = 0

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= NpisStudentdatahomeBinding.inflate(layoutInflater)
        setContentView(binding.root)//using binding due to many variables

        binding.npisBottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_overview -> {
                    overviewLayout.visibility=View.VISIBLE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.GONE
                    gradeLayout.visibility=View.GONE
                }
                R.id.nav_log -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.VISIBLE
                    attendanceLayout.visibility=View.GONE
                    gradeLayout.visibility=View.GONE
                }
                R.id.nav_attendance -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.VISIBLE
                    gradeLayout.visibility=View.GONE
                }
                R.id.nav_grading -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.GONE
                    gradeLayout.visibility=View.VISIBLE
                }
            }
            true
        }//navigation between frames (You can ignore this

        val adapter= ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,nameTest)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val currentDate= LocalDate.now().format(formatter)

        binding.studentDataSpinner.adapter=adapter
        //here is the spinner for the names
        binding.studentDataSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedStudent:String=nameTest[position]
                Toast.makeText(applicationContext,"$selectedStudent's Data",Toast.LENGTH_SHORT).show()

                getDateCalendar()
                binding.overviewText.text="$selectedStudent's Overview"
                binding.dateSubmittedLogText.text="$day/$month/$year"

                binding.dateSubmittedLogText.setOnClickListener {
                    pickDate()
                }

                binding.dateleftbutton.setOnClickListener {

                }
                binding.daterightbutton.setOnClickListener {

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        npisDatabackarrow.setOnClickListener {
            this.finish()
        }

    }

    private fun getDateCalendar(){
        day=cal.get(Calendar.DAY_OF_MONTH)
        month=cal.get(Calendar.MONTH)
        year=cal.get(Calendar.YEAR)
    }

    private fun pickDate(){

            getDateCalendar()

            DatePickerDialog(this,this,year,month,day).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        chosenDay=dayOfMonth
        chosenMonth=month
        chosenYear=year

        binding.dateSubmittedLogText.text="$chosenDay / $chosenMonth / $chosenYear"
    }

}




