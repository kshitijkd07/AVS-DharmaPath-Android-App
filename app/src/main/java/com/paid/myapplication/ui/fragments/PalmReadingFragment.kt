package com.paid.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paid.myapplication.databinding.FragmentPalmReadingBinding

class PalmReadingFragment : Fragment() {

    private var _binding: FragmentPalmReadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPalmReadingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardUpload.setOnClickListener {
            // In a real app: launch image picker
            binding.tvResult.text = "🔮 Your palm shows strong life line indicating vitality and determination. The heart line suggests deep emotional connections. The head line reveals analytical thinking and creativity. This is a premium feature — upgrade to get full AI-powered reading."
            binding.cardResult.visibility = View.VISIBLE
        }

        binding.btnAnalyze.setOnClickListener {
            binding.tvResult.text = "🔮 Your palm shows strong life line indicating vitality and determination. The heart line suggests deep emotional connections. The head line reveals analytical thinking and creativity."
            binding.cardResult.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
