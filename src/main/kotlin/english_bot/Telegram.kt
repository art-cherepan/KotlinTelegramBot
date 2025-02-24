package english_bot

const val GET_UPDATES_DELAY_MILLISECONDS = 2000
const val BOT_START_COMMAND = "/start"

fun main(args: Array<String>) {
    var updateId = 0
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val updateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val telegramBotService = TelegramBotService(botToken = args[0])

    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(GET_UPDATES_DELAY_MILLISECONDS.toLong())
        val updates: String = telegramBotService.getUpdates(updateId)

        println(updates)

        val matchUpdateIdResult: MatchResult? = updateIdRegex.find(updates)
        val updateIdMessage = (matchUpdateIdResult?.groups?.get(1)?.value?.toIntOrNull()) ?: continue

        updateId = updateIdMessage + 1

        val matchMessageTextResult: MatchResult? = messageTextRegex.find(updates)
        val userMessage = (matchMessageTextResult?.groups?.get(1)?.value)

        val matchChatIdResult: MatchResult? = chatIdRegex.find(updates)
        val chatIdMessage = (matchChatIdResult?.groups?.get(1)?.value?.toLongOrNull()) ?: continue

        if (userMessage?.lowercase() == BOT_START_COMMAND) {
            try {
                telegramBotService.sendMenu(chatIdMessage)
            } catch (e: Exception) {
                println("Ошибка при отправке сообщения: ${e.message}")
            }
        }

        val matchDataResult: MatchResult? = dataRegex.find(updates)
        val dataMessage = (matchDataResult?.groups?.get(1)?.value) ?: continue

        if (dataMessage.lowercase() == BOT_STATISTIC_BUTTON_CLICKED_DATA) {
            try {
                val statistic = trainer.getStatistics()
                telegramBotService.sendMessage(chatIdMessage, "Выучено ${statistic.learned} из ${statistic.total} слов | ${statistic.percent}%")
            } catch (e: Exception) {
                println("Ошибка при отправке сообщения: ${e.message}")
            }
        }

        if (dataMessage.lowercase() == BOT_LEARN_WORDS_BUTTON_CLICKED_DATA) {
            try {
                telegramBotService.sendMessage(chatIdMessage, "clicked learn words button")
            } catch (e: Exception) {
                println("Ошибка при отправке сообщения: ${e.message}")
            }
        }
    }
}