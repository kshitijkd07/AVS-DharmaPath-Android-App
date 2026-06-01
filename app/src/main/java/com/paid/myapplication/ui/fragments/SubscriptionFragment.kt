package com.paid.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paid.myapplication.R
import com.paid.myapplication.databinding.FragmentSubscriptionBinding

class SubscriptionFragment : Fragment() {

    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!
    private var isAnnual = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSubscribe.setOnClickListener { findNavController().popBackStack() }

        binding.btnAnnual.setOnClickListener { setBilling(true) }
        binding.btnMonthly.setOnClickListener { setBilling(false) }

        updatePrices()
    }

    private fun setBilling(annual: Boolean) {
        isAnnual = annual
        val selected = R.drawable.bg_goal_selected
        val unselected = R.drawable.bg_goal_unselected
        val accent = ContextCompat.getColor(requireContext(), R.color.accent)
        val muted = ContextCompat.getColor(requireContext(), R.color.text_muted)

        binding.btnAnnual.background = ContextCompat.getDrawable(requireContext(), if (annual) selected else unselected)
        binding.btnAnnual.setTextColor(if (annual) accent else muted)
        binding.btnMonthly.background = ContextCompat.getDrawable(requireContext(), if (!annual) selected else unselected)
        binding.btnMonthly.setTextColor(if (!annual) accent else muted)

        updatePrices()
    }

    private fun updatePrices() {
        binding.tvDevoteePrice.text = if (isAnnual) "₹1,000/yr" else "₹101/mo"
        binding.tvSadhakPrice.text = if (isAnnual) "₹2,500/yr" else "₹251/mo"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
