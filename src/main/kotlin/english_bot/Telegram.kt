package english_bot

import java.net.http.HttpClient

const val GET_UPDATES_DELAY_MILLISECONDS = 2000
const val BOT_START_COMMAND = "/start"
const val BOT_STATISTIC_BUTTON_CLICKED_DATA = "statistic_clicked"
const val BOT_LEARN_WORDS_BUTTON_CLICKED_DATA = "learn_words_clicked"

fun main(args: Array<String>) {
    var updateId = 0
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val client: HttpClient = HttpClient.newBuilder().build()
    val telegramBotService = TelegramBotService(
        client = client,
        botToken = args[0],
    )

   // val trainer: LearnWordsTrainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(GET_UPDATES_DELAY_MILLISECONDS.toLong())
        val updates: String = telegramBotService.getUpdates(updateId)

        println(updates)

        val matchUpdateIdResult: MatchResult? = updateIdRegex.find(updates)
        val updateIdMessage = (matchUpdateIdResult?.groups?.get(1)?.value) ?: continue

        updateId = updateIdMessage.toInt() + 1

        val matchMessageTextResult: MatchResult? = messageTextRegex.find(updates)
        val userMessage = (matchMessageTextResult?.groups?.get(1)?.value)

        val natchChatIdResult: MatchResult? = chatIdRegex.find(updates)
        val chatIdMessage = (natchChatIdResult?.groups?.get(1)?.value)

        val matchDataResult: MatchResult? = dataRegex.find(updates)
        val dataMessage = (matchDataResult?.groups?.get(1)?.value)

        if (chatIdMessage != null) {
            if (userMessage?.lowercase() == BOT_START_COMMAND) {
                try {
                    telegramBotService.sendMenu(chatIdMessage)
                } catch (e: Exception) {
                    println("Ошибка при отправке сообщения: ${e.message}")
                }
            }

            if (dataMessage?.lowercase() == BOT_STATISTIC_BUTTON_CLICKED_DATA) {
                try {
                    telegramBotService.sendMessage(chatIdMessage, "clicked statistic button")
                } catch (e: Exception) {
                    println("Ошибка при отправке сообщения: ${e.message}")
                }
            }
        }
    }
}