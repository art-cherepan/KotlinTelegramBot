package lesson_7

import java.net.http.HttpClient

const val GET_UPDATES_DELAY_MILLISECONDS = 2000

fun main(args: Array<String>) {
    var updateId = 0
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val chatIdRegex: Regex = "\"id\":(.+?),".toRegex()

    val client: HttpClient = HttpClient.newBuilder().build()
    val telegramBotService = TelegramBotService(
        client = client,
        botToken = args[0],
    )

    while (true) {
        Thread.sleep(GET_UPDATES_DELAY_MILLISECONDS.toLong())
        val updates: String = telegramBotService.getUpdates(updateId)

        println(updates)

        val matchUpdateIdResult: MatchResult? = updateIdRegex.find(updates)
        val updateIdMessage = (matchUpdateIdResult?.groups?.get(1)?.value) ?: continue

        updateId = updateIdMessage.toInt() + 1

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val userMessage = (matchResult?.groups?.get(1)?.value)

        val chatIdResult: MatchResult? = chatIdRegex.find(updates)
        val chatIdMessage = (chatIdResult?.groups?.get(1)?.value)

        if (chatIdMessage != null && userMessage != null) {
            try {
                telegramBotService.sendMessage(chatIdMessage, userMessage)
            } catch (e: Exception) {
                println("Ошибка при отправке сообщения: ${e.message}")
            }
        }
    }
}