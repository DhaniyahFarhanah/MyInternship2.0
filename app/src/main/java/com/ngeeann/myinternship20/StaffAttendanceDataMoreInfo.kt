package com.ngeeann.myinternship20

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ngeeann.myinternship20.databinding.StaffAttendancedataMoreinfoBinding
import kotlinx.android.synthetic.main.staff_attendancedata_card.*
import kotlinx.android.synthetic.main.staff_attendancedata_moreinfo.*
import kotlinx.android.synthetic.main.ui_intern_main.*
import java.time.format.DateTimeFormatter
import java.util.*
/*
1. Receives specified module, group, lesson time slot and day e.g. CCADC, LB12, 10:00 - 12:00 & Tuesday
2. Displays module, group, lesson time slot in their respective textviews
3. Displayed date will be the latest occurrence of that day (Tuesday) relative to current time e.g. If
    current day is Monday, display the previous week's Tuesday entries. If current day is Wednesday, display the current week's Tuesday Date and entry
4. Queries database with date, module-group value "attendance/$selectedDate/$modGroup and initializing studentNameArray
5. Entry display works by attempting to initialize 3 arrays with the values of Students based on their status. If no students in that array are found, display "No students in this list"
    5.1. OrderByChild "Name" value
    5.2. Checks what is their status, enters their current nameArray position into one of the arrays e.g. Students 1,2,4,5 are present and Student 3 is late, presentArray = {1,2,4,5}, lateArray = {3}
    OR
    5.2. Fills status into statusArray, statusArray = {P,P,P,L,P}
    5.3 Listing values in each tab will be done as nameArray[presentArray[n]] OR if (statusArray[n] == "P") {...}
6. When the forward or backward buttons are pressed, selectedDate is +/- 7 days and queried again

 */

class StaffAttendanceDataMoreInfo : AppCompatActivity()/*, DatePickerDialog.OnDateSetListener*/ {

    private lateinit var binding: StaffAttendancedataMoreinfoBinding //using binding again
    private val database = Firebase.database.reference

    //public changeable arrays based of the selection of "present", "late" or "absent" (for testing of layout)
    var studentNameArray = arrayOf("")
    var studentIDArray = arrayOf("")
    var timeStatus = arrayOf("")
    var studentNameArrayList = arrayListOf<String>()
    var studentIdArrayList = arrayListOf<String>()
    var studentDetail = arrayListOf<String>()
    var studentStatusArrayList = arrayListOf<String>()

    val cal = Calendar.getInstance() //calendar instance for the date

    var chosenDate = 0 //chosen day
    var chosenMonth = 0 //chosen month in int
    var chosenYear = 0 //chosen year

    var status = "present" //to set the default screen as "present" on navbar and keep the UI at the kept screen when changed date. "present", "late" and "absent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StaffAttendancedataMoreinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chosenModule = intent.getStringExtra("chosenModule").toString()
        val chosenGroup = intent.getStringExtra("chosenGroup").toString()
        val chosenStudentTotal = intent.getStringExtra("chosenStudentTotal").toString()
        val chosenDayOfWeek = intent.getStringExtra("chosenDay").toString()
        val chosenTiming = intent.getStringExtra("chosenTime").toString() //gets chosen time slot from previous activity

        binding.staffRecyclerView.layoutManager = LinearLayoutManager(this)//makes the recycler view a vertical scrollable list.

        binding.dateNextButton.visibility=View.GONE
        navBarSet(status,chosenStudentTotal)//set to the default upon opening
        val defaultDateString=revertDateCalendar(changeDayToInt(chosenDayOfWeek)) //called to get the correct date based on schedule
        binding.dateChosenText.text = "$chosenDate ${changeMonthToString(chosenMonth)} $chosenYear"//sets date into the textview

        binding.dateNextButton.setOnClickListener { //next date onclick
            nextDay(defaultDateString) //to get calendar to next date
            navBarSet(status,chosenStudentTotal) //to override data upon change of date
        }

        binding.datePreviousButton.setOnClickListener { //previous date onclick
            previousDay() //to get calendar to previous date
            navBarSet(status,chosenStudentTotal) //to override data upon change of date
        }

        binding.dateChosenText.setOnClickListener { //picks the date from an open calendar onclick
            pickDate() //to get calendar to previous date
            navBarSet(status,chosenStudentTotal) //to override data upon change of date
        }

        binding.moduleView.text = chosenModule //puts the module value in the moduleView text view
        binding.groupView.text = chosenGroup //puts the group value in the groupView text view
        binding.classTimeView.text = chosenTiming //puts the timing of the class into the classTimeView text view
        binding.dayOfWeekTextView.text = chosenDayOfWeek//puts the day of the week into the dayOfWeekTextView text view

        binding.staffStudentDataBottomNav.setOnNavigationItemSelectedListener { item -> //navigation between "present", "late" and "absent"
            when (item.itemId) {
                //present data displayed
                R.id.nav_present -> {
                    status = "present" //changes the status so it would go to present
                    navBarSet(status,chosenStudentTotal)
                    queryLesson(chosenModule, chosenGroup, "Present")
                }
                //late data displayed
                R.id.nav_late -> {
                    status = "late" //changes to status so it would go to late
                    navBarSet(status,chosenStudentTotal)
                    queryLesson(chosenModule, chosenGroup, "Late")
                }
                //absent data displayed
                R.id.nav_absent -> {
                    status = "absent" //changes to status so it would go to absent.
                    navBarSet(status,chosenStudentTotal)
                    queryLesson(chosenModule, chosenGroup, "MC")
                }
            }
            true
        }//end of navigation between "Present", "Late" and "Absent"

        binding.staffStudentDataBackArrow2.setOnClickListener { //ends the activity with the back arrow. goes back to the overview.
            this.finish()
        }
    }

    private fun navBarSet(uiStatus: String,studentsTotal:String){
        testSetArrayOfData(uiStatus)//called to override data with new data while returning an int and displaying the size of array
        staffRecyclerView.adapter = DataRecyclerAdapter() //puts data in respective item slot in the recycler view
        binding.totalDataTextView.text = "Total: ${studentNameArray.size} / $studentsTotal" //puts total of the list into the total text view
    }

    private fun testSetArrayOfData(chosenNav: String) : Int{ //this function is for testing only. It will receive the chosen navigation between "present", "late" and "absent. Overriding of Data
        when(chosenNav){
            "present" ->{
                studentNameArray = arrayOf("Chew Li Mien", "Brudder","Jackson Knew Me","Hoe Li Fook","Cornmit Sewer Side","Some","Names","Here")
                studentIDArray = arrayOf("S142XXXXJ","S297XXXXB","S489XXXXG","S666XXXXD","S420XXXXB","S111XXXXA","S222XXXXB","S333XXXXC")
                timeStatus = arrayOf("2:00PM","2:00PM","2:02PM","2:05PM","2:11PM","2.11PM","2.12PM","2.14PM")
                } //data of present in an array
            "late" -> {
                studentNameArray = arrayOf("Um","Yes","Late")
                studentIDArray = arrayOf("S111XXXXA","S222XXXXB","S333XXXXC")
                timeStatus = arrayOf("3:20PM","3:50PM","4:00PM")
                }//data of late in array
            "absent" ->{
                studentNameArray = arrayOf("I can't think of any names","Hee Haz Corona")
                studentIDArray = arrayOf("S999XXXXA","S555XXXXB")
                timeStatus = arrayOf("No", "Yes") //see if there is an MC or Not. Can open image if there is an Mc
            }//data of absent
        }
        return studentNameArray.size //returns int of the number in the array
    }//end of testing data.

    private fun getDateCalendar(){
        chosenDate = cal.get(Calendar.DATE)
        chosenMonth = cal.get(Calendar.MONTH)
        chosenYear = cal.get(Calendar.YEAR)
    }

    private fun revertDateCalendar(dd:Int):String{
        if(dd>cal.get(Calendar.DAY_OF_WEEK)){
            cal.set(Calendar.DAY_OF_WEEK,dd)
            cal.add(Calendar.DATE,-7)
        }
        else{
            cal.set(Calendar.DAY_OF_WEEK,dd)
        }

        getDateCalendar()
        return "$chosenDate ${changeMonthToString(chosenMonth)} $chosenYear"
    }

    private fun pickDate(){
        getDateCalendar()
        // DatePickerDialog(this,this,chosenYear,chosenMonth,chosenDay).show()
    }

    private fun changeDayToInt(dd: String): Int {
        when(dd){
            "Sunday" -> return 1
            "Monday" -> return 2
            "Tuesday" -> return 3
            "Wednesday" -> return 4
            "Thursday" -> return 5
            "Friday" -> return 6
            "Saturday" -> return 7
        }
        return 0
    }

    private fun changeMonthToString(mm: Int): String{
        val monthArray = arrayOf("January","February","March","April","May","June",
            "July", "August","September","October","November","December")
        return monthArray[mm]
    }

    /*override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) { //on selected date will choose date
        chosenDay = dayOfMonth
        chosenMonth = month
        chosenYear = year

        binding.dateChosenText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"

        cal.set(Calendar.YEAR,year)
        cal.set(Calendar.MONTH,month)
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
    }*/

    private fun nextDay(default:String){  //chooses next day
        cal.add(Calendar.DATE,7)
        getDateCalendar()
        binding.dateChosenText.text = "$chosenDate ${changeMonthToString(chosenMonth)} $chosenYear"
        if (binding.dateChosenText.text==default){
            binding.dateNextButton.visibility=View.GONE
        }
        else{
            binding.dateNextButton.visibility=View.VISIBLE
        }
    }

    private fun previousDay(){ //chooses previous day
        cal.add(Calendar.DATE,-7)
        getDateCalendar()
        binding.dateChosenText.text = "$chosenDate ${changeMonthToString(chosenMonth)} $chosenYear"
        binding.dateNextButton.visibility=View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun conDateToString(): String{ //Format of YYYY-MM-DD
        return Calendar.DATE.toString()
    }

    //adapter to display data of students
    inner class DataRecyclerAdapter: RecyclerView.Adapter<DataRecyclerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRecyclerAdapter.ViewHolder { //makes the items in recyclerview fill with the StaffStudentDataCard. Each item will be the card instead of "item 1" and so on. Ignore this. This is for layout purposes only.
            val v = LayoutInflater.from(parent.context).inflate(R.layout.staff_attendancedata_card,parent,false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.studentName.text = studentNameArray[position] //displays the Student Name in respective text view from array position into respective item slot (like a for loop system)
            holder.studentID.text = studentIDArray[position] //displays the Student ID in respective text view from array position into respective item slot
            holder.studentStatus.text = "Time: ${timeStatus[position]}" //displays the Time they logged in into respective text view from array position into respective item slot

            //if the ui is showing absent, then it would show if the student has an MC or not
            if (status == "absent"){
                holder.studentStatus.text = "MC: ${timeStatus[position]}"

            }
        }

        override fun getItemCount(): Int {
            return studentNameArray.size //this is to make sure the recycler view will display the all items in the array
        }

        //initializing of what values will be placed in which text view. Ignore this
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            var studentName: TextView = itemView.findViewById(R.id.studentNameText_card)
            var studentID: TextView = itemView.findViewById(R.id.studentIdText_card)
            var studentStatus: TextView = itemView.findViewById(R.id.timeText_card)

            //when pressed, will bring up a toast for details
            init {
                itemView.setOnClickListener {
                    val position = adapterPosition //gets the position of the selected array in int
                    when {
                        timeStatus[position] == "Yes" -> {
                            Toast.makeText(itemView.context, "${studentNameArray[position]} has an Mc. It may open in another activity.", Toast.LENGTH_SHORT).show()
                        } //if the mc status is "yes", can open mc in another activity
                        timeStatus[position] == "No" -> {
                            Toast.makeText((itemView.context),"${studentNameArray[position]} does not have an Mc.",Toast.LENGTH_SHORT).show()
                        }//if the mc status is no.
                        else -> {
                            Toast.makeText(itemView.context, "${studentNameArray[position]} entered class at ${timeStatus[position]}.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun queryLesson(module: String, group: String, status: String) { //initializes 4 arraylists with values from the lesson entry
        val path = database.child("attendance").child("2012-12-22").child("64CCADC-LB12").orderByChild("Status").equalTo(status) //todo revert to original set later
        path.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    studentNameArrayList.clear()
                    studentIdArrayList.clear()
                    studentDetail.clear()
                    studentStatusArrayList.clear()
                    for ((n,lessonSnapshot) in snapshot.children.withIndex()){
                        studentNameArrayList.add(lessonSnapshot.child("Name").getValue<String>().toString())
                        studentNameArray[n] = lessonSnapshot.child("Name").getValue<String>().toString()
                        studentIdArrayList.add(lessonSnapshot.key.toString())
                        if(lessonSnapshot.child("EntryTime").exists()) { //checks if student is on MC or not as present students have an entry time while sick ones have an MC link in their entry
                            studentDetail.add(lessonSnapshot.child("EntryTime").getValue<String>().toString())
                        }
                        else {
                            studentDetail.add(lessonSnapshot.child("MC Link").getValue<String>().toString())
                        }
                        studentStatusArrayList.add(lessonSnapshot.child("Status").getValue<String>().toString())
                    }
                    //studentNameArray = studentNameArrayList.toArray() as Array<String>
                    staffRecyclerView.adapter = DataRecyclerAdapter()
                }
                else {
                    Toast.makeText(this@StaffAttendanceDataMoreInfo, "No lesson records on this date found.", Toast.LENGTH_SHORT)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@StaffAttendanceDataMoreInfo, "Failed to query, please try again.", Toast.LENGTH_SHORT)
            }
        })
    }

}

