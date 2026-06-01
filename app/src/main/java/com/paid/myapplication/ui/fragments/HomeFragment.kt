package com.paid.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paid.myapplication.R
import com.paid.myapplication.databinding.FragmentHomeBinding
import com.paid.myapplication.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Suprabhat"
            hour < 17 -> "Shubh Dophar"
            else      -> "Shubh Sandhya"
        }
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, d MMM"))
        binding.tvGreetingDate.text = "$greeting · $date"

        lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                binding.tvUserName.text = profile.name
                binding.tvUserLocation.text = "📍 ${profile.location}"
            }
        }

        lifecycleScope.launch {
            viewModel.jaapStats.collect { stats ->
                binding.tvStreakVal.text = "${stats.currentStreak}"
                binding.tvTotalJaapVal.text = if (stats.totalJaap >= 1000)
                    "${stats.totalJaap / 1000}k" else "${stats.totalJaap}"
            }
        }

        binding.btnBeginJaap.setOnClickListener {
            findNavController().navigate(R.id.jaapFragment)
        }

        binding.orbMantra.setOnClickListener {
            findNavController().navigate(R.id.jaapFragment)
        }
        binding.orbPuja.setOnClickListener {
            findNavController().navigate(R.id.pujaFragment)
        }
        binding.orbCalendar.setOnClickListener { }
        binding.orbAstro.setOnClickListener {
            findNavController().navigate(R.id.palmReadingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
