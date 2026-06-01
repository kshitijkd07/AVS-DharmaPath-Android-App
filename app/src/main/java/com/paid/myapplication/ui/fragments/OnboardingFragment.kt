package com.paid.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.paid.myapplication.R
import com.paid.myapplication.databinding.FragmentOnboardingBinding
import com.paid.myapplication.viewmodel.AppViewModel

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()

    private var currentStep = 0
    private val totalSteps = 5

    private data class Step(
        val subtitle: String,
        val title: String,
        val icon: String,
        val desc: String,
        val showFeatures: Boolean = false,
        val showForm: Boolean = false,
    )

    private val steps = listOf(
        Step("Spiritual OS", "Dharma Path", "🙏", "Your daily guide for devotion, ritual, and calm."),
        Step("Philosophy", "मन से, डर से नहीं", "❤️", "Connect through love and understanding — not fear."),
        Step("Features", "Sacred Tools", "✨", "", showFeatures = true),
        Step("You", "Personalize", "📝", "", showForm = true),
        Step("Permissions", "Stay Present", "🔔", "Enable notifications for Jaap reminders and festival alerts."),
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Deity spinner
        val deities = listOf("Select", "Shiva", "Vishnu", "Krishna", "Hanuman", "Durga", "Ganesha")
        binding.spinnerDeity.adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, deities).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        renderStep()

        binding.btnContinue.setOnClickListener {
            if (currentStep < totalSteps - 1) {
                currentStep++
                renderStep()
            } else {
                // Save form data if on step 4
                val name = binding.etObName.text.toString().trim().ifEmpty { "Kshitij" }
                val city = binding.etObCity.text.toString().trim().ifEmpty { "Agra" }
                viewModel.saveProfile(name, city, "Uttar Pradesh", "Shiva")
                viewModel.completeOnboarding()
                findNavController().navigate(R.id.loginFragment) {
                    popUpTo(R.id.onboardingFragment) { inclusive = true }
                }
            }
        }
    }

    private fun renderStep() {
        val step = steps[currentStep]
        binding.tvStepSubtitle.text = step.subtitle
        binding.tvStepTitle.text = step.title
        binding.tvStepIcon.text = step.icon
        binding.tvStepDesc.text = step.desc
        binding.tvStepDesc.visibility = if (step.desc.isNotEmpty()) View.VISIBLE else View.GONE
        binding.llFeatures.visibility = if (step.showFeatures) View.VISIBLE else View.GONE
        binding.llForm.visibility = if (step.showForm) View.VISIBLE else View.GONE

        val isLast = currentStep == totalSteps - 1
        binding.btnContinue.text = if (isLast) "Enter Dharma Path  ✓" else "Continue  →"

        buildDots()
    }

    private fun buildDots() {
        binding.llDots.removeAllViews()
        for (i in 0 until totalSteps) {
            val dot = View(requireContext())
            val lp = ViewGroup.MarginLayoutParams(
                if (i == currentStep) 28.dp else 8.dp,
                4.dp
            )
            lp.marginEnd = 8.dp
            dot.layoutParams = lp
            dot.background = ContextCompat.getDrawable(
                requireContext(),
                if (i == currentStep) R.drawable.bg_pill_accent else R.drawable.bg_goal_unselected
            )
            binding.llDots.addView(dot)
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
