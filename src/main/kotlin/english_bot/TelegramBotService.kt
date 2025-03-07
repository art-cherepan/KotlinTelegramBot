package english_bot

import kotlinx.serialization.json.Json
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

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$API_TELEGRAM_BOT$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Long, message: String, json: Json ) {
        val urlSendMessage = "$API_TELEGRAM_BOT$botToken/sendMessage"
        val encodeMessage = java.net.URLEncoder.encode(message, "utf-8") //если русский текст, то почему-то выдает Unicode
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = encodeMessage,
        )

        val requestBodyString = json.encodeToString(requestBody)
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json ")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString()) //русский текст отображается кракозябрами
        } catch (e: IOException) {
            throw Exception("HttpClient send message error: ${e.message}")
        } catch (e: InterruptedException) {
            throw Exception("HttpClient send message error: ${e.message}")
        }
    }

    fun sendMenu(chatId: Long, json: Json) {
        val urlSendMessage = "$API_TELEGRAM_BOT$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(listOf(
                    InlineKeyboard(
                        text = "Изучать слова",
                        callbackData = BOT_LEARN_WORDS_BUTTON_CLICKED_DATA,
                    ),
                    InlineKeyboard(
                        text = "Статистика",
                        callbackData = BOT_STATISTIC_BUTTON_CLICKED_DATA,
                    ),
                ))
            )
        )

        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json ")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: IOException) {
            throw Exception("HttpClient send message error: ${e.message}")
        } catch (e: InterruptedException) {
            throw Exception("HttpClient send message error: ${e.message}")
        }
    }

    fun sendQuestion(chatId: Long, question: Question?, json: Json) {
        val urlSendMessage = "$API_TELEGRAM_BOT$botToken/sendMessage"
        val answers: MutableList<String> = mutableListOf()

        question?.variants?.forEachIndexed { index, variant ->
            answers.add("{\"text\":\"${variant.translate}\",\"callback_data\":\"${CALLBACK_DATA_ANSWER_PREFIX + index}\"}")
        }

        val requestBody = question?.correctAnswer?.let {
            SendMessageRequest(
                chatId = chatId,
                text = it.original,
                replyMarkup = ReplyMarkup(
                    listOf(question.variants.mapIndexed { index, word ->
                         InlineKeyboard(
                             text = word.translate,
                             callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index",
                         )
                    })
                )
            )
        }

        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json ")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
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