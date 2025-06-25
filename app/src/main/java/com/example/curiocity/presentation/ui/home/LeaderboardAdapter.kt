package com.example.curiocity.presentation.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.curiocity.R
import com.example.curiocity.data.local.UserLeaderboardModel

class LeaderboardAdapter(
    context: Context,
    private val entities: List<UserLeaderboardModel>
) : ArrayAdapter<UserLeaderboardModel>(context, 0, entities) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.layout_leaderboard, parent, false)

        val entity = entities[position]

        val tvPosition = view.findViewById<TextView>(R.id.leaderboard_position)
        val tvUsername = view.findViewById<TextView>(R.id.leaderboard_username)
        val tvScore = view.findViewById<TextView>(R.id.leaderboard_score)

        tvPosition.text = entity.position.toString()
        tvUsername.text = entity.username
        tvScore.text = entity.currentScore.toString()

        return view
    }
}