package lesson_5

import java.io.File

const val WORDS_FILE_NAME = "words.txt"
const val CORRECT_ANSWER_COUNT_FOR_LEARNED = 3
const val LEARNED_WORDS_CHUNK_COUNT = 4

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {
    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val learned = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWER_COUNT_FOR_LEARNED }.size
        val total = dictionary.size
        val percent = learned * 100 / total

        return Statistics(learned, total, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWER_COUNT_FOR_LEARNED }

        if (notLearnedList.isEmpty()) return null

        val questionWords = notLearnedList.take(LEARNED_WORDS_CHUNK_COUNT).shuffled()
        val correctAnswer = questionWords.random()
        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )

        return Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)

            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)

                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val dictionary: MutableList<Word> = mutableListOf()
        val wordsFile = File(WORDS_FILE_NAME)

        wordsFile.forEachLine {
            val line = it.split("|")

            dictionary.add(
                Word(
                    original = line[0],
                    translate = line[1],
                    correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0,
                ),
            )
        }

        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        File(WORDS_FILE_NAME).bufferedWriter().use { writer ->
            dictionary.forEach {
                writer.write("${it.original}|${it.translate}|${it.correctAnswersCount}\n")
            }
        }
    }
}