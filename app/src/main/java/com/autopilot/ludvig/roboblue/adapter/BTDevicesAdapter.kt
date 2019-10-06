package com.autopilot.ludvig.roboblue.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.autopilot.ludvig.roboblue.R

class BTDevicesAdapter(val BTList: ArrayList<String>): RecyclerView.Adapter<BTDevicesAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.txtName?.text = BTList[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.bt_device, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return BTList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName = itemView.findViewById<TextView>(R.id.tv_bt_device)

    }

}
