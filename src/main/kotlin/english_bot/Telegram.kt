package english_bot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val GET_UPDATES_DELAY_MILLISECONDS = 2000
private const val ALL_WORDS_ARE_LEARNED_MESSAGE = "Все слова в словаре выучены"
private const val BOT_START_COMMAND = "/start"

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun main(args: Array<String>) {
    var lastUpdateId = 0L

    val json = Json {
        ignoreUnknownKeys = true
    }

    val telegramBotService = TelegramBotService(botToken = args[0])
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(GET_UPDATES_DELAY_MILLISECONDS.toLong())
        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        lastUpdateId = firstUpdate.updateId + 1

        val message = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data

        if (message?.lowercase() == BOT_START_COMMAND && chatId != null) {
            try {
                telegramBotService.sendMenu(chatId, json)
            } catch (e: Exception) {
                println("Ошибка при отправке сообщения: ${e.message}")
            }
        }

        if (data?.lowercase() == BOT_STATISTIC_BUTTON_CLICKED_DATA && chatId != null) {
            try {
                val statistic = trainer.getStatistics()
                telegramBotService.sendMessage(
                    chatId = chatId,
                    message = "Выучено ${statistic.learned} из ${statistic.total} слов | ${statistic.percent}%",
                    json = json,
                )
            } catch (e: Exception) {
                println("Ошибка при отправке сообщения: ${e.message}")
            }
        }

        if (data?.lowercase() == BOT_LEARN_WORDS_BUTTON_CLICKED_DATA && chatId != null) {
            try {
                checkNextQuestionAndSend(
                    trainer = trainer,
                    telegramBotService = telegramBotService,
                    chatId = chatId,
                    json = json,
                )
            } catch (e: Exception) {
                println("Ошибка при отправке сообщения: ${e.message}")
            }
        }

        if  (data?.lowercase()?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true && chatId != null) {
            val answerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()
            val correctAnswerMessage = "Правильно!"
            val incorrectAnswerMessage = "Неправильно! ${trainer.getQuestion()?.correctAnswer?.original} - это ${trainer.getQuestion()?.correctAnswer?.translate}"

            if (trainer.checkAnswer(answerIndex)) {
                telegramBotService.sendMessage(
                    chatId = chatId,
                    message = correctAnswerMessage,
                    json = json,
                )
            } else {
                telegramBotService.sendMessage(
                    chatId = chatId,
                    message = incorrectAnswerMessage,
                    json = json,
                )
            }

            checkNextQuestionAndSend(
                trainer = trainer,
                telegramBotService = telegramBotService,
                chatId = chatId,
                json = json,
            )
        }
    }
}

fun checkNextQuestionAndSend(
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long,
    json: Json,
) {
    val nextQuestion: Question? = trainer.getNextQuestion()

    if (nextQuestion == null) {
        telegramBotService.sendMessage(chatId = chatId, message = ALL_WORDS_ARE_LEARNED_MESSAGE, json = json)

        return
    }

    telegramBotService.sendQuestion(chatId = chatId, question = nextQuestion, json = json)
}