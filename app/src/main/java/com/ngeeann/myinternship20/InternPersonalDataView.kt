package com.ngeeann.myinternship20

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngeeann.myinternship20.databinding.InternPersonaldataViewBinding
import kotlinx.android.synthetic.main.intern_personaldata_view.*
import java.util.*

/* Personal Data viewing for Interns

1) log is the same. Just personalized.

2) For attendance, there is a calendar. Based on the status from the statusArrayList, it would change the color of the date accordingly
   "Present" -> green
   "Late" -> red
   "Absent" -> dark gray
   default of the calendar is light gray

3) The calendar array needs spacing infront to showcase the first day of the month in the correct column. customCalendarDatesArrayList
   if it's on tuesday, the array list would be "","1"...so on til last day of the month
   so for example January 2020. 1st Jan is on friday. Therefore, this array would be {"","","","","","",1,2....,31}

   statusArrayList has the same kind of formatting. The spaces are created by the function "setFirstDayOfMonth". Inside it already clears
   the statusArrayList

   after the adding of spaces, retrieve the array of status using the customCalendarDatesArrayList. If it's possible. If no data is entered, make it null or ""

4) when the person chooses the specified date, it would showcase the date that was selected (because idk how to change color of a selected one and refresh it to the original color.
    the entry time and the leave time in a details layout. You can add more details to be added if you want.

If you want to use a function/variable inside the recyclerView, the function has to be public.
 */

class InternPersonalDataView : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: InternPersonaldataViewBinding
    private val cal = Calendar.getInstance() //calendar for log viewing
    private val customCal = Calendar.getInstance() //calendar for custom calendar (yes I need 2)

    var customCalendarDatesArrayList = arrayListOf<String>()

    //Test with working data. Description of what data set to be received from firebase
    var statusArrayList = arrayListOf<String>() //the array of status with "" same as customCalendar so that it can check the status and change the background accordingly

    var chosenDay = 0
    var chosenMonth = 0
    var chosenYear = 0

    var dateSelected = "" //date selected in string according to the attendance taking database

    var customCalMonth = 0 //custom Calendar focused on month arguments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InternPersonaldataViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Navigation between Log and Attendance
        binding.internBottomNav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.intern_log -> {
                    binding.internPersonalLogLayout.visibility = View.VISIBLE
                    binding.internPersonalAttendanceLayout.visibility = View.GONE
                }
                R.id.intern_attendance ->{
                    binding.internPersonalLogLayout.visibility = View.GONE
                    binding.internPersonalAttendanceLayout.visibility = View.VISIBLE
                }
            }
            true
        }

        //****code for Log viewing starts here****

        getDateCalendar() //called to get the day's details
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"

        binding.dateSubmittedLogText.setOnClickListener { //brings up DatePickerDialogue for log
            pickDate()
        }

        binding.dateNextButton.setOnClickListener{ //moves to next date for log
            nextDay()
        }

        binding.datePrevButton.setOnClickListener { //moves to prev date for log
            prevDay()
        }



        //**** code for Attendance viewing starts here ****

        binding.customCalendar.layoutManager = GridLayoutManager(applicationContext,7,LinearLayoutManager.VERTICAL,false) //makes 7 columns for custom calendar
        binding.customCalendar.setHasFixedSize(true) //makes it so that the calendar doesn't deform
        binding.customCalendar.adapter = CustomCalendarAdapter() //putting in the adapter

        customCalMonth= customCal.get(Calendar.MONTH)
        setFirstDayOfMonth(customCalMonth)
        binding.calendarText.text = "${changeMonthToString(customCalMonth)} $chosenYear"

        setArrayOfDates(getLastDayOfMonth(customCalMonth))

        binding.nextMonthButton.setOnClickListener { //moves to the next month
            nextMonth()
        }

        binding.prevMonthButton.setOnClickListener { //moves to previous month
            prevMonth()
        }

        binding.internPersonalDataBackArrow.setOnClickListener { //back to dashboard
            this.finish()
        }
    }

    fun changeMonthToString(monthNum: Int): String { //function to change month to a string
        val monthArray = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        return monthArray[monthNum]
    }

    private fun getDateCalendar(){ //function to get date of calendar
        chosenDay = cal.get(Calendar.DATE)
        chosenMonth = cal.get(Calendar.MONTH)
        chosenYear = cal.get(Calendar.YEAR)
    }

    private fun pickDate() {//picks date and brings up the date picker
        getDateCalendar()
        DatePickerDialog(this,this, chosenYear, chosenMonth, chosenDay).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) { //datepicker function
        chosenDay = dayOfMonth
        chosenMonth = month
        chosenYear = year
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"

        cal.set(Calendar.YEAR,year)
        cal.set(Calendar.MONTH,month)
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
    }

    private fun nextDay() { //goes to next day for log
        cal.add(Calendar.DATE,1)
        getDateCalendar()
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
    }

    private fun prevDay() { //goes to prev day for log
        cal.add(Calendar.DATE,-1)
        getDateCalendar()
        binding.dateSubmittedLogText.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
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

    private fun getLastDayOfMonth(mm:Int) :Int{ //to get the last day of the month
        customCal.set(Calendar.MONTH,mm)
        customCal.add(Calendar.MONTH,1)
        customCal.add(Calendar.DATE,-1)

        val lastDayOfMonth = customCal.get(Calendar.DATE)

        return lastDayOfMonth

    }

    //to set the calendarDatesArray to the correct dayOfWeek also clear statusArray and adds the space to make the positioning of conditions the same
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

            //conditions of the status. FOR TESTING ONLY
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

    //adapter for custom calendar
    inner class CustomCalendarAdapter: RecyclerView.Adapter<CustomCalendarAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCalendarAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.calendar_date_card,parent,false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

                "" -> holder.calendarLayout.setBackgroundColor(Color.parseColor("#CACACA")) //changes to default for no data/not included in the calendar list
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

} //333 lines nice