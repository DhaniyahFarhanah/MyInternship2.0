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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ngeeann.myinternship20.databinding.NpisStudentdatahomeBinding
import kotlinx.android.synthetic.main.npis_studentdatahome.*
import java.util.*
import kotlin.collections.ArrayList
/*
Students: Adam, Diana Hol, Jessica
On Create Process
1. 
Log Viewer Process:
1. Using the list of names from step 1,
2. fetches all logs linked to the specific ID
3 .populates logArrayList and dateArrayList with all logs from the selected student
*/
class NpisStudentDataHome : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: NpisStudentdatahomeBinding
    private val cal = Calendar.getInstance()
    private val database = Firebase.database.reference
    var chosenDay = 0
    var chosenMonth = 0
    var chosenYear = 0
    val logArrayList = arrayListOf<String>()
    val dateArrayList = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        val userId = intent.getStringExtra("userId")
        val studentNameArray: ArrayList<String> = intent.getStringArrayListExtra("studNameArr") //initializes an array with the Name values of students under the SV from the Main UI
                ?: arrayListOf("Student 1 Name", "Student 2 Name", "Student 3 Name") //if the program is unable to fetch the data from the previous activity, the system will produce default values
        val studIdArray: ArrayList<String> = intent.getStringArrayListExtra("studIdArr") //initializes a matching array with the ID values of interns,
        // the position of objects in the array are the same for both arrays e.g. studentNameArray[1] = Diana Hol, studIdArray[1] = S102XXXXXX (Diana Hol's student ID)
                ?: arrayListOf("Student 1 ID", "Student 2 ID", "Student 3 ID")
        super.onCreate(savedInstanceState)
        binding = NpisStudentdatahomeBinding.inflate(layoutInflater)
        setContentView(binding.root)//using binding due to many variables

        binding.npisBottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_overview -> {
                    overviewLayout.visibility=View.VISIBLE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.GONE
                    gradeLayout.visibility=View.GONE
                } //shows student stat page while hiding other pages
                R.id.nav_log -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.VISIBLE
                    attendanceLayout.visibility=View.GONE
                    gradeLayout.visibility=View.GONE
                } //shows student log page while hiding other pages
                R.id.nav_attendance -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.VISIBLE
                    gradeLayout.visibility=View.GONE
                } //shows student attendance page while hiding other pages
                R.id.nav_grading -> {
                    overviewLayout.visibility=View.GONE
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.GONE
                    gradeLayout.visibility=View.VISIBLE
                } //shows student grading page while hiding other pages
            }
            true
        }//navigation between frames

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, studentNameArray)

        binding.studentDataSpinner.adapter = adapter //using adapter, the studentDataSpinner gets populated with the names stored in studentNameArray
        binding.studentDataSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedStudent: String = studentNameArray[position].toString()
                Toast.makeText(applicationContext,"$selectedStudent's Data", Toast.LENGTH_SHORT).show()

                getDateCalendar()

                binding.overviewText.text = "$selectedStudent's Overview Data"
                binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
                binding.attendanceText.text = "$selectedStudent's Attendance Data"
                binding.gradeText.text = "$selectedStudent's Ready For Grading"

                //log coding here
                fetchStudentLog(studIdArray[position], convDateToString()) //populates logArrayList and dateArrayList with all logs from the selected student
                findLogByDate() //scans through dateArrayList to find an entry with a date that matches the selected calendar date and displays it on screen

                binding.dateSubmittedLogText.setOnClickListener {
                    pickDate()
                }

                binding.datenextbutton.setOnClickListener {
                    nextDay()
                }
                binding.datepreviousbutton.setOnClickListener {
                    previousDay()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //nothing here
            }

        }

        npisDatabackarrow.setOnClickListener {
            this.finish()
        }

    }

    private fun changeMonthToString(monthNum: Int): String {
        val monthArray = arrayOf("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December")
        return monthArray[monthNum]
    }

    private fun getDateCalendar() {
        chosenDay = cal.get(Calendar.DAY_OF_MONTH)
        chosenMonth = cal.get(Calendar.MONTH)
        chosenYear = cal.get(Calendar.YEAR)
    }

    private fun pickDate() {
            getDateCalendar()
            DatePickerDialog(this,this, chosenYear, chosenMonth, chosenDay).show()
            findLogByDate()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        chosenDay = dayOfMonth
        chosenMonth = month
        chosenYear = year
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"

        cal.set(Calendar.YEAR,year)
        cal.set(Calendar.MONTH,month)
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        findLogByDate()
    }

    private fun nextDay() {
        cal.add(Calendar.DATE,1)
        getDateCalendar()
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
        findLogByDate()
    }

    private fun previousDay() {
        cal.add(Calendar.DATE,-1)
        getDateCalendar()
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
        findLogByDate()
    }

    private fun convDateToString(): String { //converts the currently selected date from integer values to a single combined string
        val day = if(chosenDay < 10) { //appends a 0 in front of the day number e.g. 9 will be 09
            "0${chosenDay}"
        }
        else {
            chosenDay.toString()
        }
        return "$day ${changeMonthToString(chosenMonth)} $chosenYear"
    }

    private fun fetchStudentLog(studId: String, date: String) { //uses Supervisor's ID to query for student's that they are in charge of to display on the page
        val path = database.child("internlogs").orderByChild("userid").equalTo(studId)

        path.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for ((n, studentSnapshot) in snapshot.children.withIndex()) {
                        dateArrayList.add(studentSnapshot.child("date").getValue<String>().toString())
                        logArrayList.add(studentSnapshot.child("log").getValue<String>().toString())
                    }
                }
                else {
                    studentLogDisplay.text = "Unable to find any entries for this student."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun findLogByDate () {
        val selectedIndex = dateArrayList.indexOf(convDateToString())
        if (selectedIndex == -1) { // An indexOf return value of -1 means that the element specified in the brackets was unable to be found, so this condition will notify the user of this
            studentLogDisplay.text = "Unable to find any entries for this day."
        }
        else {
            studentLogDisplay.text = logArrayList[selectedIndex]
        }
    }
}




