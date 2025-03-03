package english_bot

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val BOT_STATISTIC_BUTTON_CLICKED_DATA = "statistic_clicked"
const val BOT_LEARN_WORDS_BUTTON_CLICKED_DATA = "learn_words_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
private const val API_TELEGRAM_BOT = "https://api.telegram.org/bot"

class TelegramBotService(private val botToken: String) {
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$API_TELEGRAM_BOT$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Long, message: String) {
        val encodeMessage = java.net.URLEncoder.encode(message, "utf-8") //если русский текст, то почему-то выдает Unicode
        val urlSendMessage = "$API_TELEGRAM_BOT$botToken/sendMessage?chat_id=$chatId&text=$encodeMessage"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: IOException) {
            throw Exception("HttpClient send message error: ${e.message}")
        } catch (e: InterruptedException) {
            throw Exception("HttpClient send message error: ${e.message}")
        }
    }

    fun sendMenu(chatId: Long) {
        val urlSendMessage = "$API_TELEGRAM_BOT$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "$BOT_LEARN_WORDS_BUTTON_CLICKED_DATA"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$BOT_STATISTIC_BUTTON_CLICKED_DATA"
                            }
                        ]
                    ]
                }
            } 
        """.trimIndent()

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json ")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: IOException) {
            throw Exception("HttpClient send message error: ${e.message}")
        } catch (e: InterruptedException) {
            throw Exception("HttpClient send message error: ${e.message}")
        }
    }

    fun sendQuestion(chatId: Long, question: Question?) {
        val urlSendMessage = "$API_TELEGRAM_BOT$botToken/sendMessage"
        val answers: MutableList<String> = mutableListOf()

        question?.variants?.forEachIndexed { index, variant ->
            answers.add("{\"text\":\"${variant.translate}\",\"callback_data\":\"${CALLBACK_DATA_ANSWER_PREFIX + index}\"}")
        }

        val sendQuestionBody = """
            {
                "chat_id": $chatId,
                "text": "${question?.correctAnswer?.original}",
                "reply_markup": {
                    "inline_keyboard": [
                        [${answers.joinToString(separator = ",")}]
                    ]
                }
            }
        """.trimIndent()

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json ")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestionBody))
            .build()

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: IOException) {
            throw Exception("HttpClient send message error: ${e.message}")
        } catch (e: InterruptedException) {
            throw Exception("HttpClient send message error: ${e.message}")
        }
    }
}