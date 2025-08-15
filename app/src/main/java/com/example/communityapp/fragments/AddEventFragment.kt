package com.example.communityapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.communityapp.databinding.FragmentAddEventsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEventFragment : Fragment() {
    private lateinit var binding: FragmentAddEventsBinding

    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddEventsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can set up CalendarView, button click etc. here
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.timeInMillis
        }

        binding.createButton.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()
            val desc = binding.editDescription.text.toString().trim()

            if (title.isEmpty() && desc.isEmpty()) {
                binding.editTitle.error = "Title can't be empty"
                binding.editDescription.error = "Description can't be empty"
                return@setOnClickListener
            }

            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDate))

            Toast.makeText(requireContext(), "Event added", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack() // go back to previous screen
        }
    }
}