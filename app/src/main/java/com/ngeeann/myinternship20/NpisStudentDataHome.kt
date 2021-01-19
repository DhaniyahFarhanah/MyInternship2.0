package com.ngeeann.myinternship20

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
Removed Overview and Grading. Not Priority.

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

1. Based on the same selected intern, it would retrieve an array of dates for that month according to the database format, YYYY-MM-DD.
2. The status array would already have the spacings to fit the calendar's dates called from setFirstDayOfMonth which is a custom calendar function.
3. Based on the status, "Present", "Late", "MC", would have different colors for the date. "" would mean that there is no data for that day.
4. when the user presses the specified date in the calendar, it would show the details. Entry Time, Exit Time, Maybe a view MC button.

*/
class NpisStudentDataHome : AppCompatActivity(), DatePickerDialog.OnDateSetListener { //TODO 1: Add in a overall student stat value to see all students together in one graph?
// TODO 2: avoid making redundant calls by creating a check that sees if the student selected was the same as before, if it is a different student then allow the program to carry on as usual

    private lateinit var binding: NpisStudentdatahomeBinding
    private val cal = Calendar.getInstance()
    private val customCal = Calendar.getInstance()
    private val database = Firebase.database.reference
    private val TAG = "NPISStudentDataHome"

    var chosenDay = 0
    var chosenMonth = 0
    var chosenYear = 0

    val logArrayList = arrayListOf<String>()
    val dateArrayList = arrayListOf<String>()

    val statusArrayList = arrayListOf<String>() //to be populated with the attendance status. It will have the spacing in front of it. Because it has to fit the custom calendar dates array.
    val attendanceDatesArrayList = arrayListOf<String>() //to be populated with the YYYY-MM-DD arrays for the selected month display. For database comparison.

    val customCalendarDatesArrayList = arrayListOf<String>()
    var customCalMonth = 0 //for custom calendar about months

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

                R.id.nav_log -> { //shows student log page while hiding other pages
                    logLayout.visibility=View.VISIBLE
                    attendanceLayout.visibility=View.GONE
                }

                R.id.nav_attendance -> { //shows student attendance page while hiding other pages
                    logLayout.visibility=View.GONE
                    attendanceLayout.visibility=View.VISIBLE
                    customCal.set(Calendar.MONTH,chosenMonth)
                }
            }
            true
        }//navigation between layouts

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, studentNameArray)

        binding.studentDataSpinner.adapter = adapter //using adapter, the studentDataSpinner gets populated with the names stored in studentNameArray
        binding.studentDataSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val selectedStudent: String = studentNameArray[position].toString()
                Toast.makeText(applicationContext,"$selectedStudent's Data", Toast.LENGTH_SHORT).show()

                getDateCalendar()

                binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"

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

        //***Attendance Viewing Code Here***

        binding.customCalendar.layoutManager = GridLayoutManager(applicationContext,7,LinearLayoutManager.VERTICAL,false) //makes 7 columns for each day of week
        binding.customCalendar.setHasFixedSize(true) //doesn't make the calendar deform with extra rows
        binding.customCalendar.adapter = CustomCalendarAdapter() //gets the calendar adapter. conditions of the day's

        customCalMonth= customCal.get(Calendar.MONTH)
        setFirstDayOfMonth(customCalMonth)
        binding.calendarText.text = "${changeMonthToString(customCalMonth)} ${customCal.get(Calendar.YEAR)}"

        setArrayOfDates(getLastDayOfMonth(customCalMonth))

        binding.nextMonthButton.setOnClickListener { //moves to the next month
            nextMonth()
        }

        binding.prevMonthButton.setOnClickListener { //moves to previous month
            prevMonth()
        }

        binding.npisDatabackarrow.setOnClickListener { //back to dashboard
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
        customCal.set(Calendar.MONTH,chosenMonth)
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
        customCal.set(Calendar.MONTH,chosenMonth)
        findLogByDate()
    }

    private fun nextDay() { //log function
        cal.add(Calendar.DATE,1)
        getDateCalendar()
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
        customCal.set(Calendar.MONTH,chosenMonth)
        findLogByDate()
    }

    private fun previousDay() { //log function
        cal.add(Calendar.DATE,-1)
        getDateCalendar()
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
        customCal.set(Calendar.MONTH,chosenMonth)
        findLogByDate()
    }

    /*to set the calendarDatesArray to the correct dayOfWeek also clear statusArray and adds the space to make the positioning of conditions the same
     so for example, 1st Jan is on a friday. The beginning of the list array would be ("","","","","",1,2,3..31}*/
    private fun setFirstDayOfMonth(mm: Int) {
        var firstDayOfMonth = 0

        customCalendarDatesArrayList.clear()
        statusArrayList.clear()

        customCal.set(Calendar.MONTH,mm)
        customCal.set(Calendar.DATE,1)

        firstDayOfMonth = customCal.get(Calendar.DAY_OF_WEEK)

        when(firstDayOfMonth){
            2 -> {
                customCalendarDatesArrayList.add("")
                statusArrayList.add("")
            }
            3 -> {customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
            }
            4 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
            }
            5 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
            }

            6 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
            }

            7 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
                statusArrayList.add("")
            }
        }
    }

    private fun setArrayOfDates(lastDayOfMonth: Int){ //sets the array of dates into the arraylist and inserts it into the calendar

        for(n in 1..lastDayOfMonth){
            customCalendarDatesArrayList.add(n.toString()) //this one is for the calendar
            attendanceDatesArrayList.add(conDateToStringAttendanceVer(n)) //this will have the array of YYYY-MM-DD without the spacing infront.

            //conditions of the status. FOR TESTING ONLY. Remove when you see this. This is just to test if the UI would respond accordingly to the data
            if(n<=5){
                statusArrayList.add("Late")
            }
            else if (n==6){
                statusArrayList.add("MC")
            }
            else if (n>6){
                statusArrayList.add("Present")
            }
            else{
                statusArrayList.add("")
            }
        }

    }

    private fun nextMonth(){ //goes to the next month for attendance
        customCal.add(Calendar.MONTH,1)
        customCalMonth = customCal.get(Calendar.MONTH)
        binding.calendarText.text = "${changeMonthToString(customCal.get(Calendar.MONTH))} ${customCal.get(Calendar.YEAR)}"
        setFirstDayOfMonth(customCalMonth)
        setArrayOfDates(getLastDayOfMonth(customCalMonth))
        binding.customCalendar.adapter = CustomCalendarAdapter()
    }

    private fun prevMonth(){ //goes to the prev month for attendance
        customCal.add(Calendar.MONTH,-1)
        customCalMonth = customCal.get(Calendar.MONTH)
        binding.calendarText.text = "${changeMonthToString(customCal.get(Calendar.MONTH))} ${customCal.get(Calendar.YEAR)}"
        setFirstDayOfMonth(customCalMonth)
        setArrayOfDates(getLastDayOfMonth(customCalMonth))
        binding.customCalendar.adapter = CustomCalendarAdapter()
    }

    private fun getLastDayOfMonth(mm:Int) :Int{ //to get the last day of the month for custom calendar
        customCal.set(Calendar.MONTH,mm)
        customCal.add(Calendar.MONTH,1)
        customCal.add(Calendar.DATE,-1)

        val lastDayOfMonth = customCal.get(Calendar.DATE)

        return lastDayOfMonth
    }

    private fun conDateToString(): String { //converts the currently selected date from integer values to a single combined string: "04 November 2020"
        val day = if(chosenDay < 10) { //appends a 0 in front of the day number e.g. 9 will be 09
            "0${chosenDay}"
        }
        else {
            chosenDay.toString()
        }
        return "$day ${changeMonthToString(chosenMonth)} $chosenYear"
    }

    private fun conDateToStringAttendanceVer(chosenDay: Int): String { //this one is for the attendance database. YYYY-MM-DD
        val day = if(chosenDay < 10) { //appends a 0 in front of the day number e.g. 9 will be 09
            "0${chosenDay}"
        }
        else {
            chosenDay.toString()
        }
        return "$chosenYear-${chosenMonth+1}-$day"
    }

    private fun fetchStudentLog(studId: String) { //uses studentId to query for daily logs written by the student and places the date and log entry values into arrayLists
        val path = database.child("internlogs").orderByChild("userid").equalTo(studId)

        path.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dateArrayList.clear() //clears out the arrayLists to add the date and logs of a different user TODO possibly switch the program to create another arrayList for each student to store values instead of clearing them
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
                Log.w(TAG, "Query failed.")
            }
        })
    }

    fun findLogByDate () { //scans through dateArrayList to find an entry with a date that matches the selected calendar date and displays it on screen
        val selectedIndex = dateArrayList.indexOf(conDateToString())
        if (selectedIndex == -1) { // An indexOf return value of -1 means that the element specified in the brackets was unable to be found, so this condition will notify the user of this
            studentLogDisplay.text = "Unable to find any entries for this day."
        }
        else {
            studentLogDisplay.text = logArrayList[selectedIndex]
        }
    }

    fun attendByDate () { //displays all students attendance for that day
        val date = "2020-12-10" //"$chosenYear-$chosenMonth-$chosenDay" TODO change the testing date back to an actual date when done testing
        val path = database.child("attendance").child(date).child("Internship").orderByKey() //orders the attendance records by student ID
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (studentSnapshot in snapshot.children) {
                    studentSnapshot.child("Name").getValue<String>()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Unable to query database.")
            }
        }  )
    }

    fun attendByStudent (studId: String) { //displays specific student's attendance across a week. Attendance dates use "YYYY-MM-DD" format
        val date = "$chosenYear-$chosenMonth-$chosenDay"
        val path = database.child("attendance").orderByChild("Internship/$studId/userid").equalTo(studId)
        path.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Unable to query database.")
            }
        })
    }
    inner class CustomCalendarAdapter: RecyclerView.Adapter<NpisStudentDataHome.CustomCalendarAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.calendar_date_card,parent,false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: NpisStudentDataHome.CustomCalendarAdapter.ViewHolder, position: Int) {
            holder.dateCustom.text = customCalendarDatesArrayList[position]

            holder.calendarLayout.setBackgroundColor(Color.parseColor("#FFFFFF")) //background color becomes default white
            holder.dateCustom.setTextColor(Color.parseColor("#292929")) //text color becomes default

            when(statusArrayList[position]){
                "Present" -> {holder.calendarLayout.setBackgroundColor(Color.parseColor("#2ACC4C"))
                    holder.dateCustom.setTextColor(Color.parseColor("#FFFFFF"))} //changes to green for present status

                "Late" -> {holder.calendarLayout.setBackgroundColor(Color.parseColor("#CC1010"))
                    holder.dateCustom.setTextColor(Color.parseColor("#FFFFFF"))} //changes to red for late status

                "MC" -> {holder.calendarLayout.setBackgroundColor(Color.parseColor("#5A5A5A"))
                    holder.dateCustom.setTextColor(Color.parseColor("#FFFFFF"))} //changes to light gray for absent status

                "" -> {holder.calendarLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    holder.dateCustom.setTextColor(Color.parseColor("#292929"))}//changes to default for no data/not included in the calendar list
            }
        }

        override fun getItemCount(): Int {

            return customCalendarDatesArrayList.size
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var dateCustom: TextView = itemView.findViewById(R.id.customCalendarText)
            var calendarLayout: RelativeLayout = itemView.findViewById(R.id.backgroundLayout)

            init { //when pressed, would show the details layout.
                itemView.setOnClickListener {
                    val position = adapterPosition //gets the position of the selected array in int

                    binding.details.visibility = View.VISIBLE
                    binding.detailsDateText.text = "${customCalendarDatesArrayList[position]} ${changeMonthToString(chosenMonth)} $chosenYear"
                    binding.entryTimeDetailsText.text = getEntryTime()
                    binding.leaveTimeDetailsText.text = getLeaveTime()
                }
            }
        }
    } //end of customCalendar adapter

    //public to put the text into the adapter class
    fun getEntryTime() :String{
        return "10.02"
    }

    //public to put the text into the adapter class
    fun getLeaveTime() :String{
        return "18.58"
    }

} //oof 476 lines of code very naise




