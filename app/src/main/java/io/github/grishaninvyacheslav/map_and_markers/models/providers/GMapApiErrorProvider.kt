package io.github.grishaninvyacheslav.map_and_markers.models.providers

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeEmitter
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @param updatePeriod - период в милисекундах, через который проверяется наличие ошибки API Google Maps
 */
class GMapApiErrorProvider(private val updatePeriod: Long) {
    val errorTag = "Google Maps Android API"

    var apiErrorLog: Maybe<String> = Maybe.create { emitter: MaybeEmitter<String> ->
        Thread {
            try {
                do {
                    val errorLog = getApiErrorLog()
                    if (errorLog.isNotEmpty()) {
                        emitter.onSuccess(errorLog)
                    }
                    Thread.sleep(updatePeriod)
                } while (errorLog.isEmpty())
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.start()
    }

    private fun getApiErrorLog(): String {
        Runtime.getRuntime().exec("logcat -c")
        val process = Runtime.getRuntime().exec("logcat -d *:E")
        val bufferedReader = BufferedReader(
            InputStreamReader(process.inputStream)
        )
        val log = StringBuilder()
        var line: String? = ""
        while (bufferedReader.readLine().also { line = it } != null) {
            if (line?.contains("E $errorTag:") == true) {
                log.append(line)
                log.append("\n")
            }
        }
        return log.toString()
    }
}