package com.paid.myapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paid.myapplication.R
import com.paid.myapplication.data.PUJA_DETAILS
import com.paid.myapplication.data.PujaDetail
import com.paid.myapplication.data.PujaStep
import com.paid.myapplication.data.SamagriItem
import com.paid.myapplication.databinding.FragmentPujaDetailBinding

class PujaDetailFragment : Fragment() {

    private var _binding: FragmentPujaDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var puja: PujaDetail
    private var samagriChecked = mutableListOf<Boolean>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPujaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pujaId = arguments?.getInt("pujaId", 1) ?: 1
        puja = PUJA_DETAILS[pujaId] ?: PujaDetail(
            title = "Puja Vidhi", deity = "Various", duration = "30 mins", rating = 4.5, verified = false,
            overview = "Instructions coming soon.",
            samagri = listOf(SamagriItem(1, "Basic Puja Kit")),
            steps = listOf(PujaStep(1, "Prepare", "Clean area and gather materials.")),
        )
        samagriChecked = MutableList(puja.samagri.size) { false }

        binding.tvTitle.text = puja.title
        binding.tvDeity.text = puja.deity
        binding.tvOverview.text = puja.overview

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.tabOverview.setOnClickListener { switchTab("overview") }
        binding.tabSamagri.setOnClickListener { switchTab("samagri") }
        binding.tabVidhi.setOnClickListener { switchTab("vidhi") }

        binding.btnToSamagri.setOnClickListener { switchTab("samagri") }
        binding.btnToVidhi.setOnClickListener { switchTab("vidhi") }
        binding.btnComplete.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        buildSamagriList()
        buildVidhiList()
        switchTab("overview")
    }

    private fun switchTab(tab: String) {
        binding.panelOverview.visibility = if (tab == "overview") View.VISIBLE else View.GONE
        binding.panelSamagri.visibility = if (tab == "samagri") View.VISIBLE else View.GONE
        binding.panelVidhi.visibility = if (tab == "vidhi") View.VISIBLE else View.GONE

        val selected = R.drawable.bg_tab_selected
        val unselected = R.drawable.bg_tab_unselected
        val accentColor = ContextCompat.getColor(requireContext(), R.color.accent)
        val mutedColor = ContextCompat.getColor(requireContext(), R.color.text_muted)

        binding.tabOverview.background = ContextCompat.getDrawable(requireContext(), if (tab == "overview") selected else unselected)
        binding.tabSamagri.background = ContextCompat.getDrawable(requireContext(), if (tab == "samagri") selected else unselected)
        binding.tabVidhi.background = ContextCompat.getDrawable(requireContext(), if (tab == "vidhi") selected else unselected)
        binding.tabOverview.setTextColor(if (tab == "overview") accentColor else mutedColor)
        binding.tabSamagri.setTextColor(if (tab == "samagri") accentColor else mutedColor)
        binding.tabVidhi.setTextColor(if (tab == "vidhi") accentColor else mutedColor)
    }

    private fun buildSamagriList() {
        val container = binding.llSamagriItems
        container.removeAllViews()
        puja.samagri.forEachIndexed { i, item ->
            val row = LayoutInflater.from(requireContext()).inflate(R.layout.item_samagri, container, false)
            val tvName = row.findViewById<TextView>(R.id.tv_samagri_name)
            val tvCheck = row.findViewById<TextView>(R.id.tv_check)
            tvName.text = item.name
            tvCheck.text = if (samagriChecked[i]) "✅" else "⬜"
            row.setOnClickListener {
                samagriChecked[i] = !samagriChecked[i]
                tvCheck.text = if (samagriChecked[i]) "✅" else "⬜"
                tvName.alpha = if (samagriChecked[i]) 0.5f else 1f
            }
            container.addView(row)
        }
    }

    private fun buildVidhiList() {
        val container = binding.llVidhiSteps
        container.removeAllViews()
        puja.steps.forEach { step ->
            val row = LayoutInflater.from(requireContext()).inflate(R.layout.item_vidhi_step, container, false)
            row.findViewById<TextView>(R.id.tv_step_num).text = "${step.step}"
            row.findViewById<TextView>(R.id.tv_step_title).text = step.title
            row.findViewById<TextView>(R.id.tv_step_instruction).text = step.instruction
            val tvMantra = row.findViewById<TextView>(R.id.tv_step_mantra)
            if (step.mantra != null) {
                tvMantra.visibility = View.VISIBLE
                tvMantra.text = "▶ ${step.mantra}"
            } else {
                tvMantra.visibility = View.GONE
            }
            container.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
