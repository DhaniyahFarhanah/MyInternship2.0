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
Quick Activity Structure Breakdown:
1. On Create
2. Data Viewer
3. Log Viewer
4. Attendance Viewer
5. Waits for change in student
6. Repeats steps 2-4 with newly selected student data

On Create Process (Requires: Intern Name array, Intern ID array)
1. Fetches a list of names and ID's of interns that the supervisor is in charge of
2. Initializes arrayLists with the name and ID values, studentNameArray and studIdArray
3. Initializes studentNameArray values into a data spinner. The data spinner allows the supervisor to select which inter's data they would like to view
4. Displays the first name in the name list, fetches their data with the different processes and displays the data for the currently selected tab
5. When the selected name is changed, the UI displays the selected name and data

Intern Data Viewer Process:

Intern Log Viewer Process: (Requires: Intern ID)
1. Based on the currently selected intern, the function fetchStudentLog() is called with those values
2. fetchStudentLog() fetches all logs that match the specific ID and adds the log entry and date into a set
of arrayLists. Both arrayLists object positions in the index will match with each other: the date value in
dateArrayList[3] comes from the same log "file" as the log entry value in logArrayList[3]
3. findLogByDate() function is then called, displaying the log entry for the selected intern on the selected date on the screen
4. If the user changes the selected intern, the fetchStudentLog() function is called with the new value
5. Before  adding the new set of data into the arrayLists, the arrayLists are first cleared of the previous set of data, ready to be used again

Intern Attendance Viewer Process:

*/
class NpisStudentDataHome : AppCompatActivity(), DatePickerDialog.OnDateSetListener { //TODO: Add in a overall student stat value to see all students together in one graph?

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
        setContentView(binding.root) //using binding due to many variables

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
                fetchStudentLog(studIdArray[position]) //populates logArrayList and dateArrayList with all logs from the selected student

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

    private fun fetchStudentLog(studId: String) { //uses studentId to query for daily logs written by the student and places the date and log entry values into arrayLists
        val path = database.child("internlogs").orderByChild("userid").equalTo(studId)

        path.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dateArrayList.clear() //clears out the arrayLists to add the date and logs of a different user TODO possibly switch the program to create another arrayList for each student to store values instead of cleearing them
                    logArrayList.clear()
                    for (studentSnapshot in snapshot.children) {
                        dateArrayList.add(studentSnapshot.child("date").getValue<String>().toString())
                        logArrayList.add(studentSnapshot.child("log").getValue<String>().toString())
                    }
                    findLogByDate()
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

    fun findLogByDate () { //scans through dateArrayList to find an entry with a date that matches the selected calendar date and displays it on screen
        val selectedIndex = dateArrayList.indexOf(convDateToString())
        if (selectedIndex == -1) { // An indexOf return value of -1 means that the element specified in the brackets was unable to be found, so this condition will notify the user of this
            studentLogDisplay.text = "Unable to find any entries for this day."
        }
        else {
            studentLogDisplay.text = logArrayList[selectedIndex]
        }
    }
}




