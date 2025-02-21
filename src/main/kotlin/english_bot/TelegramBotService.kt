package english_bot

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val API_TELEGRAM_BOT = "https://api.telegram.org/bot"

class TelegramBotService(
    private val client: HttpClient,
    private val botToken: String,
) {
    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$API_TELEGRAM_BOT$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: String, message: String) {
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

    fun sendMenu(chatId: String) {
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
                                "callback_data": "learn_words_clicked"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "statistic_clicked"
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
}