package com.example.studylogs.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studylogs.data.StudyDatabase
import com.example.studylogs.databinding.FragmentTimelineBinding

class TimelineFragment : Fragment() {
    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TimelineViewModel
    private lateinit var adapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = StudyDatabase.getDatabase(requireContext()).studyDao()
        viewModel = ViewModelProvider(this, TimelineViewModelFactory(dao))[TimelineViewModel::class.java]

        setupRecyclerView()
        setupDateNavigation()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TimelineAdapter()
        binding.timelineRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TimelineFragment.adapter
        }
    }

    private fun setupDateNavigation() {
        binding.previousDayButton.setOnClickListener {
            viewModel.navigateDate(false)
        }
        binding.nextDayButton.setOnClickListener {
            viewModel.navigateDate(true)
        }
    }

    private fun observeViewModel() {
        viewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            adapter.submitList(sessions)
        }

        viewModel.currentDate.observe(viewLifecycleOwner) { date ->
            binding.dateText.text = viewModel.formatDate(date)
            binding.nextDayButton.isEnabled = !viewModel.isCurrentDate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}