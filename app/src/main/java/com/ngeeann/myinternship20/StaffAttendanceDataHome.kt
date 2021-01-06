package com.ngeeann.myinternship20

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ngeeann.myinternship20.databinding.StaffAttendancedatahomeBinding
import kotlinx.android.synthetic.main.staff_attendancedatahome.*
import kotlinx.android.synthetic.main.staff_attendancedatahome.view.*
import kotlinx.android.synthetic.main.staff_attendanceoverview_card.*
/*
1. Receives array of modules that the staff is in charge of
2. Populates module array adapter with names of modules to select from
3. Launches query of module groups when loaded, display groups in the box below
4. When a different module is selected, run step 3 again
5. When user presses group, start MoreInfo activity and send module, group, timing and day info to activity
 */

class StaffAttendanceDataHome : AppCompatActivity() {

    private lateinit var binding: StaffAttendancedatahomeBinding//used binding again
    private var testModule = arrayOf("64LNXSR","64BASRS") //testing array for the Module

    private var groupArray = arrayOf("TB11","TB12")//test array for groups to see layout
    private var studentTotalArray = arrayOf("28", "26")
    private val database = Firebase.database.reference
    private var dayArray = arrayOf("Tuesday", "Wednesday")//test array for day of the week to see the layout
    private var timeArray = arrayOf("10.00 - 12.00","14.00 - 15.00")//test array for timing to see the layout
    private var chosenModule = "Module 1"//string to get chosen module
    private var groupArrayList = arrayListOf("Group 1", "Group 2", "Group 3")
    private var dayArrayList = arrayListOf("Day 1","Day 2","Day 3")
    private var timeArrayList = arrayListOf("Time slot 1","Time slot 2","Time slot 3")

    override fun onCreate(savedInstanceState: Bundle?) {
        val userId = intent.getStringExtra("userId")
        val moduleArray = intent.getStringArrayListExtra("moduleArray")
            ?: arrayListOf("Module 1", "Module 2", "Module 3")
        val groupArray = intent.getStringArrayListExtra("classArray") //the module group array e.g. CCADC LB12
            ?: arrayListOf("Group 1", "Group 2", "Group 3") //todo should be removable when new layout arrives
        super.onCreate(savedInstanceState)
        binding= StaffAttendancedatahomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.overviewRecyclerView.layoutManager=LinearLayoutManager(this)//makes the recycler view a vertical scrollable list.
        binding.overviewRecyclerView.adapter=OverviewRecyclerAdapter()//attached the adapter class for the recycler view

        groupQuery(userId.toString(), chosenModule) //checks for groups for selected module

        binding.staffStudentDataBackArrow.setOnClickListener {
            this.finish()
        }

        binding.staffModuleSpinner.adapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, moduleArray)

        binding.staffModuleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenModule = testModule[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        } //initialize chosenModule with the selected item in the array for Module drop box


    }

    //adapter to display data of students
    inner class OverviewRecyclerAdapter: RecyclerView.Adapter<OverviewRecyclerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewRecyclerAdapter.ViewHolder {
            //makes the items in recyclerview fill with the StaffStudentDataCard. Each item will be the card instead of "item 1" and so on. Ignore this. This is for layout purposes only.
            val v = LayoutInflater.from(parent.context).inflate(R.layout.staff_attendanceoverview_card,parent,false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.groupTextView.text= groupArray[position] //displays the group into the designated text view
            holder.studentTotalTextView.text= "${studentTotalArray[position]} students" //displays the total number of students into the designated text view
            holder.dayTextView.text= dayArray[position] //displays the day of the week into the designated text view
            holder.timingTextView.text= timeArray[position] //displays the timing of the class into the designated text view

        }

        override fun getItemCount(): Int {
            return groupArray.size //this is to make sure the recycler view will display the all items in the array

        }

        //initializing of what values will be placed in which text view. Ignore this
        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

            var groupTextView: TextView = itemView.findViewById(R.id.groupTextView)
            var studentTotalTextView: TextView = itemView.findViewById(R.id.studentTotalTextView)
            var dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
            var timingTextView: TextView = itemView.findViewById(R.id.timingTextView)


            //when pressed, will open the activity with correct info
            init {
                itemView.setOnClickListener {
                    val position = adapterPosition //gets the position of the selected array in int

                    //start the next activity and transfers the data over
                    startActivity(Intent(this@StaffAttendanceDataHome,StaffAttendanceDataMoreInfo::class.java)
                            //extra data to transfer to the next activity
                            .putExtra("chosenModule",chosenModule)
                            .putExtra("chosenGroup",groupArray[position])
                            .putExtra("chosenStudentTotal",studentTotalArray[position])
                            .putExtra("chosenDay",dayArray[position])
                            .putExtra("chosenTime",timeArray[position]))
                }

            }

        }

    }
    private fun groupQuery(userId: String, module: String) {
        val path = database.child("users/$userId/Modules/$module")
        path.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupArrayList.clear()
                dayArrayList.clear()
                timeArrayList.clear()
                for (modSnapshot in snapshot.children){
                    groupArrayList.add(modSnapshot.key.toString())
                    dayArrayList.add(modSnapshot.child("Day").value.toString())
                    timeArrayList.add(modSnapshot.child("Time").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}