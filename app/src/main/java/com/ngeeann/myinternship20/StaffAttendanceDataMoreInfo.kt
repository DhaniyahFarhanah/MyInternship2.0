package com.ngeeann.myinternship20

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngeeann.myinternship20.databinding.StaffAttendancedataMoreinfoBinding
import kotlinx.android.synthetic.main.staff_attendancedata_moreinfo.*
import java.util.*

class StaffAttendanceDataMoreInfo : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding:StaffAttendancedataMoreinfoBinding //using binding again

    var studentNameArray= arrayOf("") //changeable arrays based of the selection of "present", "late" or "absent" (get from firebase)
    var studentIDArray= arrayOf("") //changeable arrays based of the selection of "present", "late" or "absent" (get from firebase)
    var timeStatus= arrayOf("") //changeable arrays based of the selection of "present", "late" or "absent" (get from firebase)

    val cal= Calendar.getInstance() //calendar instance for the date

    var chosenDay = 0 //chosen day
    var chosenMonth = 0 //chosen month in int
    var chosenYear = 0 //chosen year

    var status="present" //to set the default screen as "present" on navbar and keep the UI at the kept screen when changed date. "present", "late" and "absent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= StaffAttendancedataMoreinfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewingModule=intent.getStringExtra("chosenModule").toString() //gets chosen Module from last activity
        val viewingGroup=intent.getStringExtra("chosenGroup").toString() //gets chosen Group from last activity

        binding.staffRecyclerView.layoutManager=LinearLayoutManager(this)//makes the recycler view a vertical scrollable list.

        NavBarSet(status)//set to the default upon opening

        getDateCalendar() //call to get date of today
        binding.dateChosenText.text="$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear" //display date of today

        //next date onclick
        binding.dateNextButton.setOnClickListener {
            nextDay() //to get calendar to next date
            NavBarSet(status) //to override data upon change of date
        }

        //previous date onclick
        binding.datePreviousButton.setOnClickListener {
            previousDay() //to get calendar to previous date
            NavBarSet(status) //to override data upon change of date
        }

        //picks the date from an open calendar onclick
        binding.dateChosenText.setOnClickListener {
            pickDate() //to get calendar to previous date
            NavBarSet(status) //to override data upon change of date
        }

        binding.moduleView.text=viewingModule //puts the module value in the moduleView text view
        binding.groupView.text=viewingGroup //puts the group value in the groupView text view
        binding.classTimeView.text="2.00PM - 4.00PM" //puts the timing of the class into the classTimeView text view


        //navigation between "present", "late" and "absent"

        binding.staffStudentDataBottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                //present data displayed
                R.id.nav_present -> {
                    status="present" //changes the status so it would go to present
                    NavBarSet(status)
                }

                //late data displayed
                R.id.nav_late -> {
                    status="late" //changes to status so it would go to late
                    NavBarSet(status)
                }

                //absent data displayed
                R.id.nav_absent -> {
                    status="absent" //changes to status so it would go to absent.
                    NavBarSet(status)
                }

            }
            true
        }//end of navigation between "Present", "Late" and "Absent"


        //ends the activity with the back arrow. goes back to the overview.
        binding.staffStudentDataBackArrow2.setOnClickListener {
            this.finish()
        }
    }

    private fun NavBarSet(uiStatus: String){

        binding.totalText.text="Total: ${testSetArrayOfData(uiStatus).toString()}" //called to override data with new data while returning an int and displaying the size of array
        staffRecyclerView.adapter=DataRecyclerAdapter() //puts data in respectful item slot in the recycler view
    }

    //this function is for testing only. It will receive the chosen navigation between "present", "late" and "absent. Overriding of Data
    private fun testSetArrayOfData(chosenNav: String) : Int{

        when(chosenNav){

            //data of present in an array
            "present" ->{
                studentNameArray= arrayOf("Chew Li Mien", "Brudder","Jackson Knew Me","Hoe Li Fook","Cornmit Sewer Side","Some","Names","Here")
                studentIDArray= arrayOf("S142XXXXJ","S297XXXXB","S489XXXXG","S666XXXXD","S420XXXXB","S111XXXXA","S222XXXXB","S333XXXXC")
                timeStatus=arrayOf("2:00PM","2:00PM","2:02PM","2:05PM","2:11PM","2.11PM","2.12PM","2.14PM")
                }

            //data of late in array
            "late" -> {

                studentNameArray= arrayOf("Um","Yes","Late")
                studentIDArray= arrayOf("S111XXXXA","S222XXXXB","S333XXXXC")
                timeStatus= arrayOf("3:20PM","3:50PM","4:00PM")

                }

            //data of absent
            "absent" ->{
                studentNameArray= arrayOf("I can't think of any names","Hee Haz Corona")
                studentIDArray= arrayOf("S999XXXXA","S555XXXXB")
                timeStatus= arrayOf("No", "Yes") //see if there is an MC or Not. Can open image if there is an Mc
            }

        }
        return studentNameArray.size //returns int of the number in the array

    }
    //end of testing data.

    private fun getDateCalendar(){
        chosenDay=cal.get(Calendar.DAY_OF_MONTH)
        chosenMonth=cal.get(Calendar.MONTH)
        chosenYear=cal.get(Calendar.YEAR)
    }

    private fun pickDate(){

        getDateCalendar()

        DatePickerDialog(this,this,chosenYear,chosenMonth,chosenDay).show()
    }

    private fun changeMonthToString(mm: Int): String{

        val monthArray = arrayOf("January","February","March","April","May","June","July","August","September","October","November","December")

        return monthArray[mm]

    }

    //on selected date will choose date
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        chosenDay=dayOfMonth
        chosenMonth=month
        chosenYear=year

        binding.dateChosenText.text="$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"

        cal.set(Calendar.YEAR,year)
        cal.set(Calendar.MONTH,month)
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
    }

    //chooses next day
    private fun nextDay(){

        cal.add(Calendar.DATE,1)
        getDateCalendar()

        binding.dateChosenText.text="$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
    }

    //chooses previous day
    private fun previousDay(){
        cal.add(Calendar.DATE,-1)

        getDateCalendar()

        binding.dateChosenText.text="$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
    }

    //adapter to display data of students
    inner class DataRecyclerAdapter: RecyclerView.Adapter<DataRecyclerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRecyclerAdapter.ViewHolder {
            //makes the items in recyclerview fill with the StaffStudentDataCard. Each item will be the card instead of "item 1" and so on. Ignore this. This is for layout purposes only.
            val v = LayoutInflater.from(parent.context).inflate(R.layout.staff_attendancedata_card,parent,false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.studentName.text= studentNameArray[position] //displays the Student Name in respective text view from array position into respective item slot (like a for loop system)
            holder.studentID.text= studentIDArray[position] //displays the Student ID in respective text view from array position into respective item slot
            holder.studentStatus.text= "Time: ${timeStatus[position]}" //displays the Time they logged in into respective text view from array position into respective item slot

            //if the ui is showing absent, then it would show if the student has an MC or not
            if (status=="absent"){
                holder.studentStatus.text= "MC: ${timeStatus[position]}"
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

                    //if the mc status is "yes", can open mc in another activity
                    if (timeStatus[position] == "Yes") {
                        Toast.makeText(itemView.context, "${studentNameArray[position]} has an Mc. It may open in another activity.", Toast.LENGTH_LONG).show()
                    }

                    //if the mc status is no.
                    else if (timeStatus[position]=="No"){
                        Toast.makeText((itemView.context),"${studentNameArray[position]} does not have an Mc.",Toast.LENGTH_LONG).show()
                    }

                    else {
                        Toast.makeText(itemView.context, "${studentNameArray[position]} entered class at ${timeStatus[position]}.", Toast.LENGTH_LONG).show()
                    }

                }


            }

        }

    }



}

