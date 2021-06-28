package com.example.favoriteplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.favoriteplaces.R
import com.example.favoriteplaces.activities.AddFavoritePlaceActivity
import com.example.favoriteplaces.activities.MainActivity
import com.example.favoriteplaces.models.FavoritePlaceModel
import de.hdodenhof.circleimageview.CircleImageView

open class FavoritePlaceAdapter(
    private val context: Context,
    private var list: ArrayList<FavoritePlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_favorite_place,
                parent,
                false)
        )
    }

    /**
     * On bind create a new variable and get the item from the list,
     * take necessary variables from the created variable and assign it to the holder
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.findViewById<CircleImageView>(R.id.circleImageViewPlaceImage).setImageURI(Uri.parse(model.image))
            holder.itemView.findViewById<TextView>(R.id.textViewTitle).text = model.title
            holder.itemView.findViewById<TextView>(R.id.textViewDescription).text =
                model.description

            /**
             * Bind each list element onClick
             */
            holder.itemView.setOnClickListener{
                if(onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddFavoritePlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * Function that returns the number of elements
     */
    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: FavoritePlaceModel) {

        }
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}