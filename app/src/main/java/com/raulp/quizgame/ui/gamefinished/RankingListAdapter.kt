package com.raulp.quizgame.ui.gamefinished

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raulp.quizgame.data.User
import com.raulp.quizgame.databinding.RankingListItemBinding

/**
 * @author Raul Palade
 * @date 18/12/2022
 * @project QuizGame
 */

class RankingListAdapter : ListAdapter<User, RankingListAdapter.RankingViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class RankingViewHolder(private var binding: RankingListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            @JvmStatic
            private var index = 1
        }

        @SuppressLint("SetTextI18n")
        fun bind(user: User) {
            binding.apply {
                playerPosition.text = index.toString()
                playerName.text = user.name
                playerPoints.text = "${user.score} Points"
                index++
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        return RankingViewHolder(RankingListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser)
    }
}