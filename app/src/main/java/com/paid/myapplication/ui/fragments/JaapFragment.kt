package com.paid.myapplication.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.paid.myapplication.R
import com.paid.myapplication.databinding.FragmentJaapBinding
import com.paid.myapplication.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class JaapFragment : Fragment() {

    private var _binding: FragmentJaapBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()

    private var count = 0
    private var goal = 108
    private var timerRunning = false
    private var elapsed = 0
    private var currentMantra = "Om Namah Shivaya"

    // Only the elapsed-time timer — no auto-counting
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (timerRunning) {
                elapsed++
                updateElapsed()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJaapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        // ── Tap ring = +1 count only ──────────────────────────────────────
        binding.jaapRing.setOnClickListener {
            if (count < goal) {
                count++
                // Start timer on first tap if not already running
                if (count == 1 && !timerRunning) startTimer()
                updateUI()
                if (count >= goal) {
                    stopTimer()
                    updateTimerButton()
                    showCelebration()
                    viewModel.recordJaapSession(count, currentMantra, goal)
                }
            }
        }

        // ── Play/Pause = timer only, not counting ─────────────────────────
        binding.btnPlayPause.setOnClickListener {
            if (timerRunning) stopTimer() else startTimer()
            updateTimerButton()
        }

        binding.btnReset.setOnClickListener {
            if (count > 0) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Reset count?")
                    .setMessage("Progress and timer will reset to zero.")
                    .setPositiveButton("Reset") { _, _ -> resetSession() }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                resetSession()
            }
        }

        binding.btnChangeMantra.setOnClickListener { showMantraPicker() }

        // Goal buttons
        listOf(
            binding.goal108 to 108,
            binding.goal216 to 216,
            binding.goal540 to 540,
            binding.goal1080 to 1080,
        ).forEach { (btn, g) ->
            btn.setOnClickListener {
                goal = g
                resetSession()
                updateGoalButtons()
            }
        }

        lifecycleScope.launch {
            viewModel.jaapPrefs.collect { prefs ->
                currentMantra = prefs.savedMantras.firstOrNull() ?: "Om Namah Shivaya"
                binding.tvCurrentMantra.text = currentMantra
            }
        }

        // Feature tiles
        binding.tileReminder.setOnClickListener {
            viewModel.toggleReminder()
            val enabled = viewModel.jaapPrefs.value.reminderEnabled
            binding.tvReminderStatus.text = if (!enabled) "On ›" else "Off ›"
        }
        binding.tileStats.setOnClickListener { showStats() }
        binding.tileMantras.setOnClickListener { showMantraPicker() }
        binding.tileFocus.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Focus Mode")
                .setMessage("Tap the ring to count mantras. Timer tracks your session time.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun startTimer() {
        timerRunning = true
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        timerRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetSession() {
        stopTimer()
        count = 0
        elapsed = 0
        updateUI()
        updateTimerButton()
    }

    private fun updateUI() {
        binding.jaapRing.count = count
        binding.jaapRing.goal = goal
        binding.tvCount.text = "$count"
        binding.tvTarget.text = "$goal"
        updateElapsed()
    }

    private fun updateElapsed() {
        val m = elapsed / 60
        val s = elapsed % 60
        binding.tvElapsed.text = "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    }

    private fun updateTimerButton() {
        binding.btnPlayPause.setImageResource(if (timerRunning) R.drawable.ic_pause else R.drawable.ic_play)
        binding.tvPlayLabel.text = if (timerRunning) "Pause" else "Start"
    }

    private fun updateGoalButtons() {
        val goals = listOf(binding.goal108 to 108, binding.goal216 to 216, binding.goal540 to 540, binding.goal1080 to 1080)
        goals.forEach { (btn, g) ->
            btn.background = ContextCompat.getDrawable(requireContext(),
                if (g == goal) R.drawable.bg_goal_selected else R.drawable.bg_goal_unselected)
            btn.setTextColor(ContextCompat.getColor(requireContext(),
                if (g == goal) R.color.accent else R.color.text_muted))
        }
    }

    private fun showMantraPicker() {
        val mantras = viewModel.jaapPrefs.value.savedMantras.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Mantra")
            .setItems(mantras) { _, which ->
                currentMantra = mantras[which]
                binding.tvCurrentMantra.text = currentMantra
            }
            .show()
    }

    private fun showStats() {
        val stats = viewModel.jaapStats.value
        AlertDialog.Builder(requireContext())
            .setTitle("Your Stats")
            .setMessage("Total Jaaps: ${stats.totalJaap}\nDay Streak: ${stats.currentStreak}\nBest Streak: ${stats.longestStreak}")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showCelebration() {
        AlertDialog.Builder(requireContext())
            .setTitle("✅ Mala Complete!")
            .setMessage("$goal mantras of $currentMantra completed.")
            .setPositiveButton("New Session") { _, _ -> resetSession() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
        _binding = null
    }
}
