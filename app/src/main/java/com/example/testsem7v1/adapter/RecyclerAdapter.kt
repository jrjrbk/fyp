package com.example.testsem7v1.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.EditProfileFragment
import com.example.testsem7v1.FeedbackFragment
import com.example.testsem7v1.R
import com.example.testsem7v1.SettingsFragment
import com.example.testsem7v1.Test
import com.google.android.material.imageview.ShapeableImageView

//Extends recyclerview
class recyclerAdapter (private val data: ArrayList<Test>): RecyclerView.Adapter<recyclerAdapter.MyViewHolder> () {

    //Refers to all the item contained in a row.
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleImage: ShapeableImageView = itemView.findViewById(R.id.title_image)
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = data[position]
        holder.titleImage.setImageResource(currentItem.titleImage)
        holder.tvHeading.text = currentItem.heading

        holder.itemView.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val activity=v!!.context as AppCompatActivity
                // if (position = ?) for specific list.
                if(holder.adapterPosition == 0){
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, EditProfileFragment()).addToBackStack(null).commit()
                }
                else if (holder.adapterPosition == 1){
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, FeedbackFragment()).addToBackStack(null).commit()
                }
                else if (holder.adapterPosition == 3){
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, SettingsFragment()).addToBackStack(null).commit()
                }
                //.addToBackStack(null)
                //disallowAddToBackStack()
            }
        })
    }

    override fun getItemCount(): Int {
        //returns the data size. Returns how many item in the recycler view.
        return data.size
    }
}

//class ViewHolder(view: View): RecyclerView.ViewHolder(view){
//        val textView : TextView
//
//        init {
//            textView = view.findViewById(R.id.textview_second)
//        }
//    }