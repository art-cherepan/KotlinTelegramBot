package lesson_4.task_1

import java.io.File

const val CORRECT_ANSWER_COUNT_FOR_LEARNED = 3
const val MAX_PERCENT = 100
const val LEARNED_WORDS_CHUNK_COUNT = 4

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
            1 -> {
                while (true) {
                    val questionWords = getNotLearnedChunk()
                    val questionWordsMap = parseChunkToMap(questionWords) //решил работать не со списком, а с картой
                    val randomWord = questionWordsMap.entries.shuffled().first()

                    println(randomWord.value.original)
                    questionWordsMap.forEach { println("${it.key} - ${it.value.translate}") }

                    break;
                }
            }
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

fun getNotLearnedChunk(): List<Word> {
    val dictionary = loadDictionary()
    val notLearnedList = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWER_COUNT_FOR_LEARNED }

    return notLearnedList.shuffled().take(LEARNED_WORDS_CHUNK_COUNT)
}

fun parseChunkToMap(chunk: List<Word>): Map<Int, Word> {
    val map: MutableMap<Int, Word> = mutableMapOf()
    var index = 1

    chunk.forEach {
        map[index] = it
        index++
    }

    return map
}