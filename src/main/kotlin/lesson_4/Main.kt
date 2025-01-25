package lesson_4


import java.io.File

const val CORRECT_ANSWER_COUNT_FOR_LEARNED = 3
const val MAX_PERCENT = 100
const val LEARNED_WORDS_CHUNK_COUNT = 4
const val WORDS_FILE_NAME = "words.txt"

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

        when (userAnswer) {
            1 -> {
                while (true) {
                    val questionWords = getNotLearnedChunk(dictionary)
                    val questionWordsMap = parseChunkToMap(questionWords) //решил работать не со списком, а с картой
                    val randomWord = questionWordsMap.entries.shuffled().first()

                    println(randomWord.value.original)
                    questionWordsMap.forEach { println("${it.key} - ${it.value.translate}") }

                    println("----------")
                    println("0 - Меню")

                    val userTranslateAnswer = readln().toIntOrNull()

                    if (userTranslateAnswer == randomWord.key) {
                        dictionary.forEach {
                            if (it.original == randomWord.value.original) {
                                it.correctAnswersCount++
                            }
                        }

                        saveDictionary(dictionary)

                        println("Правильно!")
                    } else if (userTranslateAnswer == 0) {
                        break;
                    }
                    else {
                        println("Неправильно! ${randomWord.value.original} - это ${randomWord.value.translate}")
                    }
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
    var correctAnswersCount: Int = 0,
)

fun loadDictionary(): List<Word> {
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

fun getStatistic(): String {
    val dictionary = loadDictionary()
    val totalCount = dictionary.count()
    val learnedCount = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWER_COUNT_FOR_LEARNED }
    val learnedCountPercent = learnedCount.count() * MAX_PERCENT / totalCount

    return "Выучено ${learnedCount.count()} из $totalCount слов | $learnedCountPercent%"
}

fun getNotLearnedChunk(dictionary: List<Word>): List<Word> {
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

fun saveDictionary(dictionary: List<Word>) {
    File(WORDS_FILE_NAME).bufferedWriter().use {
        writer -> dictionary.forEach {
            writer.write("${it.original}|${it.translate}|${it.correctAnswersCount}\n")
        }
    }
}