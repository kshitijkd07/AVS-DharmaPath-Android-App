package com.paid.myapplication.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.paid.myapplication.R
import com.paid.myapplication.databinding.FragmentLoginBinding
import com.paid.myapplication.viewmodel.AppViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendOtp.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            if (phone.length < 10) {
                Toast.makeText(requireContext(), "Enter valid 10-digit number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.btnSendOtp.isEnabled = false
            binding.btnSendOtp.text = "Sending..."
            Handler(Looper.getMainLooper()).postDelayed({
                binding.stepPhone.visibility = View.GONE
                binding.stepOtp.visibility = View.VISIBLE
                binding.btnSendOtp.isEnabled = true
                binding.btnSendOtp.text = "Send OTP  →"
            }, 800)
        }

        binding.tvEditPhone.setOnClickListener {
            binding.stepOtp.visibility = View.GONE
            binding.stepPhone.visibility = View.VISIBLE
        }

        binding.btnVerify.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()
            if (otp.length < 6) {
                Toast.makeText(requireContext(), "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.btnVerify.isEnabled = false
            binding.btnVerify.text = "Verifying..."
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.login()
                findNavController().navigate(R.id.homeFragment) {
                    popUpTo(R.id.loginFragment) { inclusive = true }
                }
            }, 800)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
