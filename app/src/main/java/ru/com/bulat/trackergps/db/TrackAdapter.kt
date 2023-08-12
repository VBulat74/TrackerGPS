package ru.com.bulat.trackergps.db

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.com.bulat.trackergps.R
import ru.com.bulat.trackergps.databinding.TrackItemBinding

class TrackAdapter : ListAdapter<TrackItem, TrackAdapter.ViewHolder>(Comparator()) {
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val binding = TrackItemBinding.bind(view)
        fun bind (trackItem: TrackItem) = with(binding) {
            tvTime.text = trackItem.time
            tvDate.text = trackItem.date
            tvDistance.text = trackItem.distance
            tvAvrVelocity.text = trackItem.velocity

        }
    }

    class Comparator : DiffUtil.ItemCallback<TrackItem> () {
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}