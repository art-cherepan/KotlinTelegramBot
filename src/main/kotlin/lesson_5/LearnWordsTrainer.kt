package lesson_5

import java.io.File

const val WORDS_FILE_NAME = "words.txt"

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

class LearnWordsTrainer (
    private val learnedAnswerCount: Int = 3,
    private val countOfQuestionWords: Int = 4,
) {
    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val learned = dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.size
        val total = dictionary.size
        val percent = learned * 100 / total

        return Statistics(learned, total, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < learnedAnswerCount }

        if (notLearnedList.isEmpty()) return null

        val questionWords = if (notLearnedList.size < countOfQuestionWords) {
            val learnedList = dictionary.filter { it.correctAnswersCount >= learnedAnswerCount }.shuffled()
            notLearnedList.shuffled().take(countOfQuestionWords) +
                    learnedList.take(countOfQuestionWords - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(countOfQuestionWords)
        }.shuffled()

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
        try {
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
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary(dictionary: List<Word>) {
        File(WORDS_FILE_NAME).bufferedWriter().use { writer ->
            dictionary.forEach {
                writer.write("${it.original}|${it.translate}|${it.correctAnswersCount}\n")
            }
        }
    }
}