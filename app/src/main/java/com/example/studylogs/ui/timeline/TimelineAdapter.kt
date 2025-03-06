package com.example.studylogs.ui.timeline


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studylogs.data.StudySession
import com.example.studylogs.databinding.ItemTimelineBinding
import java.text.SimpleDateFormat
import java.util.*

class TimelineAdapter : ListAdapter<StudySession, TimelineAdapter.TimelineViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TimelineViewHolder(private val binding: ItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(session: StudySession) {
            binding.apply {
                Log.e("session", session.toString())
                tagText.text = session.tag
                startTimeText.text = timeFormat.format(Date(session.startTime))

                val endTimeMillis = session.endTime ?: System.currentTimeMillis()
                endTimeText.text = timeFormat.format(Date(endTimeMillis))

                durationText.text = "${session.duration}min"
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StudySession>() {
            override fun areItemsTheSame(oldItem: StudySession, newItem: StudySession): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StudySession, newItem: StudySession): Boolean {
                return oldItem == newItem
            }
        }
    }
}