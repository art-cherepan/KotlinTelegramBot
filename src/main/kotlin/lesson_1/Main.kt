package lesson_1

import java.io.File

fun main() {
    val file = File("words.txt")
    file.forEachLine { println(it) }
}