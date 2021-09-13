package com.example.kotline_test1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Radapter(private val mList: ArrayList<Photo>) : RecyclerView.Adapter<Radapter.holder>() {

    class holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var view: View = itemView;
        private var mlist: Photo? = null
        val textView: TextView
        val image :ImageView;

        init {
            textView = view.findViewById(R.id.itemDate)
            image = view.findViewById(R.id.itemImage)
        }


        companion object {
            private val photo_key = "Photo"
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val inflatedView = parent.inflate(R.layout.recycler_item, false)
        return holder(inflatedView);
    }

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    override fun onBindViewHolder(holder: Radapter.holder, position: Int) {
        holder.textView.text = mList[position].text
        Glide.with(holder.image.context).load(mList[position].url).into(holder.image);
    }

    override fun getItemCount() = mList.size
}