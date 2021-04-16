package com.example.noisitapp.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noisitapp.Model.Recording
import com.example.noisitapp.R

class RecordingsAdapter(private var myDataset: ArrayList<Recording>, private val recordItemClickListener: RecordItemClickListener ) : RecyclerView.Adapter<RecordingsAdapter.MyViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val tv_name : TextView = v.findViewById(R.id.tv_recording_name)
        val tv_date : TextView = v.findViewById(R.id.tv_recording_date)
        val tv_duration : TextView = v.findViewById(R.id.tv_recording_duration)
        val tv_address : TextView = v.findViewById(R.id.tv_recording_address)
    }
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) : MyViewHolder {
        // create a new view

        return  MyViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_recordings, viewGroup, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //Log.e("AA","AAA" + myDataset.get(0))
        holder.tv_name.text = myDataset[position].name
        holder.tv_date.text =  myDataset[position].date
        holder.tv_duration.text = "Duration: "+ myDataset[position].duration
        holder.tv_address.text = "Address: " + myDataset[position].address
        holder.itemView.setOnClickListener{
            recordItemClickListener.onRecordItemClickListener(position)
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
interface RecordItemClickListener{
    fun onRecordItemClickListener(index : Int)
}

