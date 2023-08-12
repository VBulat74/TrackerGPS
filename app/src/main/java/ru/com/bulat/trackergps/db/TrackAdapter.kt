package ru.com.bulat.trackergps.db

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.com.bulat.trackergps.R
import ru.com.bulat.trackergps.databinding.TrackItemBinding

class TrackAdapter (private val listener : Listener) : ListAdapter<TrackItem, TrackAdapter.ViewHolder>(Comparator()) {
    class ViewHolder(view : View, private val listener: Listener) : RecyclerView.ViewHolder(view), OnClickListener {
        val binding = TrackItemBinding.bind(view)
        private var trackTemp : TrackItem? = null
        init {
            binding.ibDelete.setOnClickListener (this)
            binding.item.setOnClickListener(this)
        }

        fun bind (trackItem: TrackItem) = with(binding) {
            tvTime.text = trackItem.time
            tvDate.text = trackItem.date
            tvDistance.text = trackItem.distance
            tvAvrVelocity.text = trackItem.velocity
            trackTemp = trackItem
        }

        override fun onClick(v: View?) {
            val type = when(v?.id) {
                R.id.ibDelete -> ClickType.DELETE
                R.id.item -> ClickType.OPEN
                else -> {ClickType.OPEN}
            }
            trackTemp?.let { listener.onClick(track = it, type = type) }

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
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(track : TrackItem, type: ClickType)
    }

    enum class ClickType {
        DELETE,
        OPEN,
    }
}