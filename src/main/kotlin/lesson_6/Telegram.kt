package lesson_6

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val UPDATES_SEPARATOR_LENGTH = 11
const val GET_UPDATES_DELAY_MILLISECONDS = 2000

fun main(args: Array<String>) {
    var updateId = 0

    while (true) {
        Thread.sleep(GET_UPDATES_DELAY_MILLISECONDS.toLong())
        val updates: String = getUpdates(args[0], updateId)

        println(updates)

        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")

        if (startUpdateId == -1 || endUpdateId == -1) continue

        val updateIdString = updates.substring(startUpdateId + UPDATES_SEPARATOR_LENGTH, endUpdateId)
        updateId = updateIdString.toInt() + 1
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val client: HttpClient = HttpClient.newBuilder().build()
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}