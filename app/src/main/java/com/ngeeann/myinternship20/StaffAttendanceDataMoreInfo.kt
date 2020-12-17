package com.ngeeann.myinternship20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.staff_attendancedata_moreinfo.*

class StaffAttendanceDataMoreInfo : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager?=null
    private var adapter: RecyclerView.Adapter<StaffStudentDataRecyclerAdapter.ViewHolder>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.staff_attendancedata_moreinfo)

        val viewingModule=intent.getStringExtra("chosenModule").toString() //gets chosen Module from last activity
        val viewingGroup=intent.getStringExtra("chosenGroup").toString() //gets chosen Group from last activity

        staffRecyclerView.layoutManager=LinearLayoutManager(this) //starts the layout for the recycler view
        staffRecyclerView.adapter=StaffStudentDataRecyclerAdapter() //initializes the data from StaffStudentDataRecyclerAdapter into the recyclerView

        moduleView.text=viewingModule //puts the module value in the moduleView text view
        groupView.text=viewingGroup //puts the group value in the groupView text view

        //ends the activity with the back arrow. goes back to the overview.
        staffStudentDataBackArrow2.setOnClickListener {
            this.finish()
        }
    }
}