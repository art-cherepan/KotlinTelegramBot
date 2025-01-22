package lesson_3.task_2

import java.io.File

const val CORRECT_ANSWER_COUNT_FOR_LEARNED = 3
const val MAX_PERCENT = 100

fun main() {
    while (true) {
        println("""
            Меню: 
            1 – Учить слова
            2 – Статистика
            0 – Выход
        """.trimIndent())

        val userAnswer = readln().toIntOrNull()

        when (userAnswer) {
            1 -> println("Выбран пункт \"Учить слова\"")
            2 -> println(getStatistic())
            0 -> return
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0,
)

fun loadDictionary(): List<Word> {
    val dictionary: MutableList<Word> = mutableListOf()
    val wordsFile = File("words.txt")

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

fun getStatistic(): String {
    val dictionary = loadDictionary()
    val totalCount = dictionary.count()
    val learnedCount = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWER_COUNT_FOR_LEARNED }
    val learnedCountPercent = learnedCount.count() * MAX_PERCENT / totalCount

    return "Выучено ${learnedCount.count()} из $totalCount слов | $learnedCountPercent%"
}