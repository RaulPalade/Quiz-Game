package com.raulp.quizgame.ui.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.raulp.quizgame.R
import com.raulp.quizgame.data.Game
import com.raulp.quizgame.data.Question
import com.raulp.quizgame.data.Response
import com.raulp.quizgame.databinding.FragmentGameBinding
import com.raulp.quizgame.repository.AuthRepository
import com.raulp.quizgame.repository.GameRepository
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val gameRepository = GameRepository()
    private val authRepository = AuthRepository()
    private lateinit var viewModel: GameViewModel
    private var index = 0
    private var game = Game(20)
    private lateinit var questions: List<Question>
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var photoUrl: String
    private lateinit var currentAnswers: ArrayList<String>
    private var optionButtons = listOf<Button>()
    private var bonusDoublePoints = false
    private lateinit var bonusTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
    }

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
        photoUrl = args.photoUrl

        Picasso.get().load(photoUrl).into(binding.profileImage)

        viewModel.getQuestionList(topic)
        viewModel.questionList.observe(viewLifecycleOwner) { questionResponse ->
            when (questionResponse) {
                is Response.Success -> {
                    startGame(questionResponse.data)
                }
                is Response.Failure -> {
                    println("No questions were found")
                }
            }
        }

        countDownTimer = object : CountDownTimer(1200000L, 1000) {
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

        optionButtons = listOf(binding.option1, binding.option2, binding.option3, binding.option4)

        binding.deleteBonus.setOnClickListener {
            activateBonusDelete()
            binding.deleteBonus.isEnabled = false
            binding.deleteBonus.isVisible = false
        }
        binding.doubleBonus.setOnClickListener {
            activateBonusDouble()
            binding.doubleBonus.isEnabled = false
            binding.doubleBonus.isVisible = false
        }
        binding.halfBonus.setOnClickListener {
            activateBonusHalf()
            binding.halfBonus.isEnabled = false
            binding.halfBonus.isVisible = false
        }

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

        currentAnswers = ArrayList()
        currentAnswers.add(questions[index].correct)
        questions[index].wrongAnswers.shuffle()
        currentAnswers.add(questions[index].wrongAnswers[0] as String)
        currentAnswers.add(questions[index].wrongAnswers[1] as String)
        currentAnswers.add(questions[index].wrongAnswers[2] as String)
        currentAnswers.shuffle()

        optionButtons =
            listOf<Button>(binding.option1, binding.option2, binding.option3, binding.option4)

        optionButtons.forEachIndexed { index, btn ->
            if (!btn.isEnabled) {
                btn.isEnabled = true
                btn.setBackgroundResource(R.drawable.game_answer_label)
            }
            btn.text = "${(index + 65).toChar()}) ${currentAnswers[index]}"
        }
    }

    private fun checkAnswerAndAssignPoints(answer: String) {
        if (answer == questions[index].correct) {
            if (bonusDoublePoints) {
                game.points += 20
            } else {
                game.points += 10
            }
            game.correct++
        } else {
            game.wrong++
        }
    }

    private fun endGame() {
        if (this::bonusTimer.isInitialized) {
            bonusTimer.cancel()
        }

        countDownTimer.cancel()
        val action =
            GameFragmentDirections.actionGameStartedFragmentToGameFinishedFragment(game, photoUrl)
        viewModel.updateUserScore(game.points)
        viewModel.score.observe(viewLifecycleOwner) { scoreUpdated ->
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

    private fun activateBonusDelete() {
        var correctIndex = 0
        currentAnswers.forEachIndexed { index, answer ->
            if (answer == questions[index].correct) {
                correctIndex = index
            }
        }

        var random = (0..3).random()
        while (random == correctIndex) {
            random = (0..3).random()
        }

        disableButton(random)
    }

    private fun activateBonusDouble() {
        bonusTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                bonusDoublePoints = true
            }

            override fun onFinish() {
                bonusDoublePoints = false
            }
        }.start()
    }

    private fun activateBonusHalf() {
        var correctIndex = 0
        currentAnswers.forEachIndexed { i, answer ->
            if (answer == questions[index].correct) {
                correctIndex = i
            }
        }

        var random1 = (0..3).random()
        var random2 = (0..3).random()
        while (random1 == correctIndex || random2 == correctIndex || random1 == random2) {
            random1 = (0..3).random()
            random2 = (0..3).random()
        }

        disableButton(random1)
        disableButton(random2)
    }

    private fun disableButton(random: Int) {
        optionButtons[random].isEnabled = false
        optionButtons[random].setBackgroundResource(R.drawable.game_answer_label_delete)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            GameViewModelFactory(gameRepository, authRepository)
        )[GameViewModel::class.java]
    }
}