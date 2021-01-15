package com.ngeeann.myinternship20

import android.app.DatePickerDialog
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

class InternPersonalDataView : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: InternPersonaldataViewBinding
    private val cal = Calendar.getInstance() //calendar for log viewing
    private val customCal = Calendar.getInstance() //calendar for custom calendar (yes I need 2)

    /*this array is to get the dates for the custom calendar. There will be "" for empty days that are not in the first week.
      if it's on tuesday, the array list would be "","1"...so on til last day of the month

      so for example January 2020. 1st Jan is on friday. Therefore, this array would be {"","","","","","",1,2....,31}*/

    var customCalendarDatesArrayList = arrayListOf<String>()

    var arrayOfDates = arrayListOf<Int>() //this one will hold all the dates for the month without the "". It will also be an Int

    var chosenDay = 0
    var chosenMonth = 0
    var chosenYear = 0

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

        binding.nextMonthButton.setOnClickListener {
            nextMonth()
        }

        binding.prevMonthButton.setOnClickListener {
            prevMonth()
        }

        binding.internPersonalDataBackArrow.setOnClickListener { //back to dashboard
            this.finish()
        }
    }

    private fun changeMonthToString(monthNum: Int): String {
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

    private fun nextMonth(){ //goes to the next month
        customCal.add(Calendar.MONTH,1)
        customCalMonth = customCal.get(Calendar.MONTH)
        binding.calendarText.text = "${changeMonthToString(customCal.get(Calendar.MONTH))} ${customCal.get(Calendar.YEAR)}"
        setFirstDayOfMonth(customCalMonth)
        setArrayOfDates(getLastDayOfMonth(customCalMonth))
        binding.customCalendar.adapter = CustomCalendarAdapter()
    }

    private fun prevMonth(){ //goes to the prev month
        customCal.add(Calendar.MONTH,-1)
        customCalMonth = customCal.get(Calendar.MONTH)
        binding.calendarText.text = "${changeMonthToString(customCal.get(Calendar.MONTH))} ${customCal.get(Calendar.YEAR)}"
        setFirstDayOfMonth(customCalMonth)
        setArrayOfDates(getLastDayOfMonth(customCalMonth))
        binding.customCalendar.adapter = CustomCalendarAdapter()
    }

    private fun getLastDayOfMonth(mm:Int) :Int{ //to get the last day of

        customCal.set(Calendar.MONTH,mm)
        customCal.add(Calendar.MONTH,1)
        customCal.add(Calendar.DATE,-1)

        val lastDayOfMonth = customCal.get(Calendar.DATE)

        return lastDayOfMonth

    }

    private fun setFirstDayOfMonth(mm: Int) {

        var firstDayOfMonth = 0

        customCalendarDatesArrayList.clear()

        customCal.set(Calendar.MONTH,mm)
        customCal.set(Calendar.DATE,1)

        firstDayOfMonth = customCal.get(Calendar.DAY_OF_WEEK)

        when(firstDayOfMonth){
            2 -> {
                customCalendarDatesArrayList.add("")
            }
            3 -> {customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
            }
            4 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
            }
            5 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
            }

            6 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
            }

            7 -> {
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
                customCalendarDatesArrayList.add("")
            }
        }
    }

    private fun setArrayOfDates(lastDayOfMonth: Int){ //sets the array of dates into the arraylist and inserts it into the calendar

        for(n in 1..lastDayOfMonth){
            arrayOfDates.add(n) //this one is for checking database
            customCalendarDatesArrayList.add(n.toString()) //this one is for the calendar
        }

    }

    //adapter for custom calendar
    inner class CustomCalendarAdapter: RecyclerView.Adapter<CustomCalendarAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCalendarAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.calendar_date_card,parent,false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.monthAndYear.text=customCalendarDatesArrayList[position]
        }

        override fun getItemCount(): Int {

            return customCalendarDatesArrayList.size
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

            var monthAndYear: TextView = itemView.findViewById(R.id.customCalendarText)
            var calendarLayout: RelativeLayout = itemView.findViewById(R.id.backgroundLayout)

        }

    }
}