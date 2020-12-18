package com.ngeeann.myinternship20

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//this is the adapter to pass the data from an array into the cardviews item by item. scrollable.
class StaffPresentDataRecyclerAdapter: RecyclerView.Adapter<StaffPresentDataRecyclerAdapter.ViewHolder>() {



    //testing data only. 5 in each.
    private var studentNameArray= arrayOf("Chew Li Mien", "Brudder","Jackson Knew Me","Hoe Li Fook","Cornmit Sewer Side")
    private var studentIDArray= arrayOf("S142XXXXJ","S297XXXXB","S489XXXXG","S666XXXXD","S420XXXXB")
    private var timeStatus=arrayOf("3.00PM","3.00PM","3.02PM","3.05PM","3.11PM")



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffPresentDataRecyclerAdapter.ViewHolder {
        //makes the items in recyclerview fill with the StaffStudentDataCard. Each item will be the card instead of "item 1" and so on. Ignore this. This is for layout purposes only.
        val v = LayoutInflater.from(parent.context).inflate(R.layout.staff_attendancedata_card,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //puts the arrays of the
        holder.studentName.text= studentNameArray[position]
        holder.studentID.text= studentIDArray[position]
        holder.studentStatus.text= timeStatus[position]
    }

    override fun getItemCount(): Int {
        return studentNameArray.size //this will give the total number of items in array. So 5 items will give int of 4
    }

    //initializing of what values will be placed in which textview. Ignore this
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var studentName: TextView = itemView.findViewById(R.id.studentNameText_card)
        var studentID:TextView = itemView.findViewById(R.id.studentIdText_card)
        var studentStatus:TextView = itemView.findViewById(R.id.timeText_card)

    }


}