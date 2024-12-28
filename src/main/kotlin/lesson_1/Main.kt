package lesson_1

import java.io.File

fun main() {
    val file = File("words.txt")

    file.createNewFile()
    file.appendText("""
        hello привет
        dog собака
        cat кошка
    """.trimIndent())

    val fileLines = file.readLines()

    for (line in fileLines) println(line)
}