package lesson_2

import java.io.File

fun main() {
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

    dictionary.forEach { println("""
        -------
        original: ${it.original}
        translate: ${it.translate}
        correctAnswersCount: ${it.correctAnswersCount}
    """.trimIndent()) }
}

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int,
)