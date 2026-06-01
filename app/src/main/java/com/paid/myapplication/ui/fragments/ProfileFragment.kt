package com.paid.myapplication.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paid.myapplication.R
import com.paid.myapplication.databinding.FragmentProfileBinding
import com.paid.myapplication.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                binding.tvProfileName.text = profile.name
                binding.tvProfileCityDeity.text = "${profile.city} · ${profile.deity}"
                binding.tvDeityVal.text = profile.deity
            }
        }

        lifecycleScope.launch {
            viewModel.jaapStats.collect { stats ->
                binding.tvStatTotal.text = stats.totalJaap.toString()
                binding.tvStatStreak.text = stats.currentStreak.toString()
                binding.tvStatBest.text = stats.longestStreak.toString()
            }
        }

        lifecycleScope.launch {
            viewModel.theme.collect { theme ->
                binding.tvThemeLabel.text = if (theme == "dark") "Dark mode" else "Light mode"
            }
        }

        binding.btnThemeToggle.setOnClickListener {
            viewModel.toggleTheme()
        }

        binding.cardSubscription.setOnClickListener {
            findNavController().navigate(R.id.subscriptionFragment)
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Log out?")
                .setMessage("You will need to sign in again to access your spiritual journey.")
                .setPositiveButton("Log out") { _, _ ->
                    viewModel.logout()
                    findNavController().navigate(R.id.loginFragment) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
