package com.raulp.quizgame.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.raulp.quizgame.Response
import com.raulp.quizgame.data.Game
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.databinding.FragmentGameBinding
import com.raulp.quizgame.repository.GameRepository
import java.util.concurrent.TimeUnit


class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val gameRepository = GameRepository()
    private lateinit var viewModel: GameViewModel
    private var index = 0
    private var game = Game(5)
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
                    startGame(questionResponse.data)
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }

        object : CountDownTimer(6000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                val format = String.format(
                    "%02d:%02d",
                    (seconds % 3600) / 60, (seconds % 60)
                )
                binding.timer.text = format
            }

            override fun onFinish() {
                game.notAnswered = game.totalQuestions - index
                endGame()
            }
        }.start()

        val optionButtons =
            listOf<Button>(binding.option1, binding.option2, binding.option3, binding.option4)

        optionButtons.forEach { btn ->
            btn.setOnClickListener {
                val answer = btn.text.substring(3)
                checkAnswerAndAssignPoints(answer)
                if (index < game.totalQuestions - 1) {
                    index++
                    setNextQuestion()
                } else {
                    endGame()
                }
            }
        }
    }

    private fun startGame(data: List<Question>) {
        questions = data
        setNextQuestion()
    }

    @SuppressLint("SetTextI18n")
    private fun setNextQuestion() {
        binding.totalQuestions.text = "${index + 1}/${game.totalQuestions}"
        binding.points.text = "${game.points} Points"
        binding.question.text = questions[index].question

        val answers = ArrayList<String>()
        answers.add(questions[index].correct)
        questions[index].wrongAnswers.shuffle()
        answers.add(questions[index].wrongAnswers[0] as String)
        answers.add(questions[index].wrongAnswers[1] as String)
        answers.add(questions[index].wrongAnswers[2] as String)
        answers.shuffle()

        val optionButtons =
            listOf<Button>(binding.option1, binding.option2, binding.option3, binding.option4)

        optionButtons.forEachIndexed { index, btn ->
            btn.text = "${(index + 65).toChar()}) ${answers[index]}"
        }
    }

    private fun checkAnswerAndAssignPoints(answer: String) {
        if (answer == questions[index].correct) {
            game.points += 10
            game.correct++
        } else {
            game.wrong++
        }
    }

    private fun endGame() {
        val action = GameFragmentDirections.actionGameStartedFragmentToGameFinishedFragment(game)
        viewModel.updateUserScore(game.points)
        viewModel.scoreUpdated.observe(viewLifecycleOwner) { scoreUpdated ->
            when (scoreUpdated) {
                is Response.Success -> {
                    this.findNavController().navigate(action)
                }
                is Response.Failure -> {
                    println("Impossible to update user score")
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(gameRepository)
        )[GameViewModel::class.java]
    }
}