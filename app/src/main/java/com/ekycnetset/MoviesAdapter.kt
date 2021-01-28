package com.ekycnetset

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.library.ekycnetset.model.Data

internal class MoviesAdapter(private var moviesList: ArrayList<Data>) : RecyclerView.Adapter<MoviesAdapter.MyViewHolder>() {

   internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
      var name: TextView = view.findViewById(R.id.nameTxt)
      var switchButton: SwitchCompat = view.findViewById(R.id.switchC)

   }

   @NonNull
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val itemView = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_admin, parent, false)
      return MyViewHolder(itemView)
   }

   override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

      val movie = moviesList[position]
      holder.name.text = movie.name
      holder.switchButton.isChecked = movie.value

      holder.switchButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{

         override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {


            movie.value = p0!!.isChecked

         }

      })


   }
   override fun getItemCount(): Int {
      return moviesList.size
   }
}