package com.ngeeann.myinternship20

import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ngeeann.myinternship20.databinding.StudentPersonaldataViewingBinding
import java.util.*

class StudentPersonalDataViewing : AppCompatActivity(), DatePickerDialog.OnDateSetListener
{
    private lateinit var binding: StudentPersonaldataViewingBinding

    private val cal = Calendar.getInstance()
    private val database = Firebase.database.reference

    var chosenDay = 0
    var chosenMonth = 0
    var chosenYear = 0

    //This group of arrays is for the schedule. The "Module" child in the student's account. Sample data
    var moduleNameListArray = arrayListOf<String>("Module 1","Module 2","Module 3","Module 4","Module 5") //Array of modules from the "module" child from student's firebase data
    var groupListArray = arrayListOf<String>("Class 1","Class 2","Class 3","Class 4","Class 5") //Array of groups from individual modules from student's firebase data
    var startAtListArray = arrayOf<String>("startTime1","startTime2","startTime3","startTime4","startTime5") //Array of class start times from individual module from student's firebase data
    var endAtListArray = arrayListOf<String>("endTime1","endTime2","endTime3","endTime4","endTime5") //Array of class end times from individual module child in student's firebase data
    var locationListArray = arrayListOf<String>("location 1","location 2","hbl","location 3","hbl","hbl") //Array of class location from individual module child in student's firebase data

    var entryTimeListArray = arrayListOf<String>("entryTime1","entryTime2","entryTime3","entryTime4","entryTime5") //Array of Student's log in time for attendance
    var statusListArray = arrayListOf<String>("Present","Late","Absent","NA","NA") //Array of the status for the from the modules

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StudentPersonaldataViewingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDateCalendar() //gets date of today

        binding.studentAttendanceRecyclerView.layoutManager = LinearLayoutManager(this) //vertical layout for recycler view

        binding.StudentDataBackArrow.setOnClickListener { //go back to dashboard
            this.finish()
        }

        binding.dateTextView.setOnClickListener { //
            pickDate()
        }

        binding.studentAttendanceRecyclerView.adapter=StudentAttendanceRecyclerAdapter() //resets recycler data shown

        binding.dayDisplayTextView.setOnClickListener { //allows user to open date picker
            pickDate()
        }

        binding.dateNextButton.setOnClickListener { //goes to the next day by 1
            nextDay()
        }

        binding.datePreviousButton.setOnClickListener { //goes to the previous day by 1
            previousDay()
        }

    } //end of activity code


    private fun changeMonthToString(monthNum: Int): String { //to change the month Int to Strings
        val monthArray = arrayOf("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December")
        return monthArray[monthNum]
    }

    private fun changeDayToString(dateNum: Int): String{ //to change the dayOfWeek Int to String
        val dateArray = arrayOf("","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
        return dateArray[dateNum]
    }

    private fun getDateCalendar() { //gets the date and sets the date into text View

        chosenDay = cal.get(Calendar.DAY_OF_MONTH)
        chosenMonth = cal.get(Calendar.MONTH)
        chosenYear = cal.get(Calendar.YEAR)

        binding.dateTextView.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
        binding.dayDisplayTextView.text = "${changeDayToString(cal.get(Calendar.DAY_OF_WEEK))}"

    }

    private fun pickDate() { //opens date picker

        getDateCalendar()
        DatePickerDialog(this,this, chosenYear, chosenMonth, chosenDay).show()

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) { //on date picker change date

        chosenDay = dayOfMonth
        chosenMonth = month
        chosenYear = year

        cal.set(Calendar.YEAR,year)
        cal.set(Calendar.MONTH,month)
        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)

        binding.dateTextView.text = "$chosenDay ${changeMonthToString(chosenMonth)} $chosenYear"
        binding.dayDisplayTextView.text = "${changeDayToString(cal.get(Calendar.DAY_OF_WEEK))}"

    }

    private fun nextDay() { //sets calendar to next day
        cal.add(Calendar.DATE,1)
        getDateCalendar()
    }

    private fun previousDay() { //sets calendar to previous day
        cal.add(Calendar.DATE,-1)
        getDateCalendar()
    }

    inner class StudentAttendanceRecyclerAdapter: RecyclerView.Adapter<StudentAttendanceRecyclerAdapter.ViewHolder>() { //the adapter for the recycler view

        //makes the items in recyclerview fill with the StaffStudentDataCard. Each item will be the card instead of "item 1" and so on. Ignore this. This is for layout purposes only
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttendanceRecyclerAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.student_attendanceviewing_card,parent,false)
            return ViewHolder(v)
        }

        /* places the data into the respective item views for student_attendanceviewing_card.xml
                moduleGroupLabel<TextView> --> "64CCADC-PB13"
                classTime<TextView> --> "13:00 - 14:00"
                status<TextView> --> "Absent"
                location<TextView> --> "Blk 5 #03-02"
                time<TextView> --> "13:05" (entry time)
                cardView<constraintLayout> --> It's the layout where all the details are in. Used to change the color.*/
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.moduleGroupLabel.text = "${moduleNameListArray[position]}-${groupListArray[position]}"
            holder.classTime.text = "${startAtListArray[position]} - ${endAtListArray[position]}"
            holder.status.text = "${statusListArray[position]}"
            holder.location.text ="${locationListArray[position]}"

            if(statusListArray[position]=="Late"){
                holder.time.visibility = View.VISIBLE
            }
            holder.time.text = "${entryTimeListArray[position]}"
            //changes color based on the status
            when(statusListArray[position]){
                "Late" -> {
                    holder.cardView.setBackgroundColor(Color.parseColor("#A22222")) //red
                }
                "Absent" -> {
                    holder.cardView.setBackgroundColor(Color.parseColor("#292929")) //gray
                }
                "Present" -> {
                    holder.cardView.setBackgroundColor(Color.parseColor("#26B357")) //green
                }
            }

        }

        override fun getItemCount(): Int {
            return groupListArray.size //this will give the total number of items in array. So 5 items will give int of 4
        }

        //initializing of what values will be placed in which textView.
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            var moduleGroupLabel: TextView = itemView.findViewById(R.id.moduleGroup_card)
            var classTime: TextView = itemView.findViewById(R.id.classTime_card)
            var status: TextView = itemView.findViewById(R.id.statusTextView)
            var location: TextView = itemView.findViewById(R.id.classLocation_card)
            var time: TextView = itemView.findViewById(R.id.entryTime_card)
            var cardView: ConstraintLayout = itemView.findViewById(R.id.studentAttendanceCardView)

        } //

    } //end of adapter

}