package com.example.studylogs.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.studylogs.R
import com.example.studylogs.data.StudyDatabase
import com.example.studylogs.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    private val timerOptions = arrayOf(5, 10, 15, 25, 30, 45, 60, 90, 120)
    private val tagOptions = arrayOf("Study", "Work", "Reading", "Exercise", "Meditation")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = StudyDatabase.getDatabase(requireContext()).studyDao()
        viewModel = ViewModelProvider(this, HomeViewModelFactory(dao))[HomeViewModel::class.java]

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.switchTimer.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleMode()
        }

        binding.tagButton.setOnClickListener {
            showTagSelection()
        }

        binding.startButton.setOnClickListener {
            if (viewModel.isRunning.value == true) {
                viewModel.stopTimer()
            } else {
                if (viewModel.isTimerMode.value == false) {
                    showTimerSelection()
                } else {
                    viewModel.startTimer()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.timeDisplay.observe(viewLifecycleOwner) { time ->
            binding.timer.text = time
        }

        viewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.startButton.text = if (isRunning) "Stop" else "Start"
        }

        viewModel.isTimerMode.observe(viewLifecycleOwner) { isTimerMode ->
            binding.switchTimer.isChecked = !isTimerMode
            binding.stopwatchText.alpha = if (isTimerMode) 1f else 0.5f
            binding.timerText.alpha = if (isTimerMode) 0.5f else 1f
        }
    }

    private fun showTimerSelection() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Duration")
            .setItems(timerOptions.map { "$it minutes" }.toTypedArray()) { _, which ->
                viewModel.startTimer(timerOptions[which])
            }
            .show()
    }

    private fun showTagSelection() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Tag")
            .setItems(tagOptions) { _, which ->
                binding.tagButton.text = tagOptions[which]
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
