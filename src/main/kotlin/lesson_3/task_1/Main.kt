package lesson_3.task_1

import java.io.File

fun main() {
    val dictionary = loadDictionary()

    while (true) {
        println("""
            Меню: 
            1 – Учить слова
            2 – Статистика
            0 – Выход
        """.trimIndent())

        val userAnswer = readln().toIntOrNull()

        val output = when (userAnswer) {
            1 -> "Выбран пункт \"Учить слова\""
            2 -> "Выбран пункт \"Статистика\""
            0 -> "exit"
            else -> "Введите число 1, 2 или 0"
        }

        if (output == "exit") break

        println(output)
    }
}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int,
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