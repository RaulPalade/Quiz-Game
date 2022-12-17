package com.raulp.quizgame.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Game
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.databinding.FragmentGameBinding
import com.raulp.quizgame.repository.GameRepository

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: GameViewModel
    private var index = 0
    private val game = Game()
    private lateinit var questions: List<Question>

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

        viewModel.getQuestions(topic)

        viewModel.questions.observe(viewLifecycleOwner) { questionResponse ->
            when (questionResponse) {
                is Response.Success -> {
                    questions = questionResponse.data
                    startGame()
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }

        val buttons =
            listOf<Button>(binding.option1, binding.option2, binding.option3, binding.option4)
        buttons.forEach { btn ->
            run {
                btn.setOnClickListener {
                    checkAnswer(btn.text.substring(3))
                    index++
                    if (index <= 20) {
                        continueGame()
                    } else {
                        endGame()
                    }
                }
            }
        }
    }

    private fun startGame() {
        continueGame()
    }


    @SuppressLint("SetTextI18n")
    private fun continueGame() {
        binding.totalQuestions.text = "${index}/20"
        binding.points.text = "${game.points} Points"

        binding.question.text = questions[index].question
        binding.option1.text = "1) ${questions[index].correct}"
        binding.option2.text = "2) ${questions[index].wrongAnswers[1]}"
        binding.option3.text = "3) ${questions[index].wrongAnswers[2]}"
        binding.option4.text = "4) ${questions[index].wrongAnswers[3]}"
    }

    private fun checkAnswer(answer: String) {
        println(answer)
        println(questions[index].correct)
        if (answer == questions[index].correct) {
            game.points += 10
            game.correct++
            println(true)
        } else {
            game.wrong++
        }
    }

    private fun endGame() {
        println(game.toString())
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(gameRepository)
        )[GameViewModel::class.java]
    }
}