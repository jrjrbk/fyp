package com.example.testsem7v1.adapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.EditProfileFragment
import com.example.testsem7v1.FeedbackFragment
import com.example.testsem7v1.HomeFragment
import com.example.testsem7v1.ProfileFragment
import com.example.testsem7v1.R
import com.example.testsem7v1.AboutFragment
import com.example.testsem7v1.Test
import com.example.testsem7v1.activity.admin
import com.example.testsem7v1.activity.loginSession
import com.example.testsem7v1.retrofit.systemDatabase.userID
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.analyseFeedback
import com.example.testsem7v1.editAbout
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
                if(holder.adapterPosition == 0 ){
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, EditProfileFragment()).addToBackStack(null).commit()
                }
                else if (holder.adapterPosition == 1 && !admin){
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, FeedbackFragment()).addToBackStack(null).commit()
                }
                else if (holder.adapterPosition == 2 && !admin){
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, AboutFragment()).addToBackStack(null).commit()
                }
                else if (holder.adapterPosition == 3){
                    val fragment1 = ProfileFragment()
                    val fragment2 = HomeFragment()
                    val currentFragment = activity.supportFragmentManager.findFragmentByTag(fragment1.javaClass.simpleName)
                    val passedfrag = activity.supportFragmentManager.findFragmentByTag(fragment2.javaClass.simpleName)

                    loginSession = false
                    admin = false
                    userID = userID(accountID = -1)
                    activity.supportFragmentManager.beginTransaction().detach(currentFragment!!).commit()
                    activity.recreate()
                }
                else if(holder.adapterPosition == 1 && admin){
                    // analyse feedback
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, analyseFeedback()).addToBackStack(null).commit()

                }
                else if (holder.adapterPosition == 2 && admin){
                    // edit About
                    activity.supportFragmentManager.beginTransaction().replace(R.id.profileFragment, editAbout()).addToBackStack(null).commit()
                }
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