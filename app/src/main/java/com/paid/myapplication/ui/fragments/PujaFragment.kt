package com.paid.myapplication.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paid.myapplication.R
import com.paid.myapplication.data.PUJA_LIST
import com.paid.myapplication.data.PujaItem
import com.paid.myapplication.databinding.FragmentPujaBinding
import com.paid.myapplication.databinding.ItemPujaBinding

class PujaFragment : Fragment() {

    private var _binding: FragmentPujaBinding? = null
    private val binding get() = _binding!!

    private val categories = listOf("All", "Daily", "Deity", "Occasion", "Festival")
    private var activeCategory = "All"
    private var searchQuery = ""
    private lateinit var adapter: PujaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPujaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Category chips
        buildCategoryChips()

        // RecyclerView
        adapter = PujaAdapter(getFiltered()) { puja ->
            findNavController().navigate(R.id.pujaDetailFragment, bundleOf("pujaId" to puja.id))
        }
        binding.rvPujaList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPujaList.adapter = adapter

        // Search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                adapter.updateList(getFiltered())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun getFiltered() = PUJA_LIST.filter { puja ->
        (activeCategory == "All" || puja.category == activeCategory) &&
            puja.title.contains(searchQuery, ignoreCase = true)
    }

    private fun buildCategoryChips() {
        binding.llCategories.removeAllViews()
        categories.forEach { cat ->
            val chip = TextView(requireContext()).apply {
                text = cat
                textSize = 13f
                setPadding(40, 20, 40, 20)
                background = ContextCompat.getDrawable(
                    requireContext(),
                    if (cat == activeCategory) R.drawable.bg_goal_selected else R.drawable.bg_goal_unselected
                )
                setTextColor(ContextCompat.getColor(
                    requireContext(),
                    if (cat == activeCategory) R.color.accent else R.color.text_muted
                ))
                val lp = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.marginEnd = 16
                layoutParams = lp
                setOnClickListener {
                    activeCategory = cat
                    buildCategoryChips()
                    adapter.updateList(getFiltered())
                }
            }
            binding.llCategories.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PujaAdapter(
    private var items: List<PujaItem>,
    private val onClick: (PujaItem) -> Unit,
) : RecyclerView.Adapter<PujaAdapter.VH>() {

    inner class VH(val binding: ItemPujaBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(newItems: List<PujaItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPujaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvPujaTitle.text = item.title
        holder.binding.tvPujaCategory.text = item.category
        holder.binding.tvPujaDeity.text = "Deity · ${item.deity}"
        holder.binding.tvPujaDuration.text = "⏱ ${item.duration}"
        holder.binding.tvPujaRating.text = "★ ${item.rating}"
        holder.binding.tvPujaVerified.visibility = if (item.verified) View.VISIBLE else View.GONE
        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
