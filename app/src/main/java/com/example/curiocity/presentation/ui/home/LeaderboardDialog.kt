package com.example.curiocity.presentation.ui.home

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.curiocity.data.local.UserLeaderboardModel
import com.example.curiocity.databinding.DialogLeaderboardBinding

class LeaderboardDialog(
    private val entities: List<UserLeaderboardModel>
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogLeaderboardBinding.inflate(layoutInflater)

        val adapter = LeaderboardAdapter(requireContext(), entities)
        binding.leaderboardList.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()
        return dialog
    }
}