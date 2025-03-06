package com.example.studylogs.ui.analysis
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.studylogs.data.StudyDatabase
import com.example.studylogs.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AnalysisViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = StudyDatabase.getDatabase(requireContext()).studyDao()
        viewModel = ViewModelProvider(this, AnalysisViewModelFactory(dao))[AnalysisViewModel::class.java]

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.totalStudyTime.observe(viewLifecycleOwner) { total ->
            binding.totalTimeText.text = "$total minutes"
        }

        viewModel.dailyAverage.observe(viewLifecycleOwner) { average ->
            binding.averageTimeText.text = "$average minutes"
        }

        viewModel.tagDistribution.observe(viewLifecycleOwner) { distribution ->
            val distributionText = distribution.entries.joinToString("\n") { (tag, time) ->
                "$tag: $time minutes"
            }
            binding.distributionText.text = distributionText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}