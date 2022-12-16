package com.raulp.quizgame.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.databinding.FragmentGameBinding
import com.raulp.quizgame.repository.GameRepository

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: GameViewModel
    private var questionIndex = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.lifecycleOwner = this
        binding.gameViewModel = viewModel
        val args: GameFragmentArgs by navArgs()
        val topic = args.topic

        println(topic.toString())

        viewModel.getQuestions(topic)

        viewModel.questions.observe(viewLifecycleOwner) { questionResponse ->
            when (questionResponse) {
                is Response.Success -> {
                    startGame(questionResponse.data)
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }
    }

    private fun startGame(data: List<Question>) {
        setQuestion(questionIndex, data)
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestion(index: Int, question: List<Question>) {
        binding.totalQuestions.text = "$index/20"
        binding.question.text = question[0].question
        binding.option1.text = "1) ${question[0].wrongAnswers[0]}"
        binding.option2.text = "2) ${question[0].wrongAnswers[1]}"
        binding.option3.text = "3) ${question[0].wrongAnswers[2]}"
        binding.option4.text = "4) ${question[0].wrongAnswers[3]}"
        questionIndex++
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(gameRepository)
        )[GameViewModel::class.java]
    }
}