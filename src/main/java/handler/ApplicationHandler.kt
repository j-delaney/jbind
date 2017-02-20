package handler

import mu.KLogging
import org.jnativehook.keyboard.NativeKeyEvent
import java.awt.Desktop
import java.io.File
import java.io.IOException

class ApplicationHandler(configMapping: Map<String, String>) : Handler() {
    companion object : KLogging()

    private val mapping: Map<Int, String> = configMapping.mapKeys {
        keyParser.getNativeKeyEvent(it.key)
    }

    override fun press(nativeKeyEvent: NativeKeyEvent) {
        val keyPressed = nativeKeyEvent.keyCode

        val application = mapping[keyPressed]
        if (application != null) {
            val pressedKeyText = NativeKeyEvent.getKeyText(keyPressed)

            logger.info("App mapping: $pressedKeyText->$application")
            tryStoppingPress(nativeKeyEvent)

            try {
                Desktop.getDesktop().open(File(application))
                // TODO: What about `Runtime.exec("open /path/to/Whichever.app");`
                // TODO: What about `osascript`?
                // osascript -e "tell application \"System Events\" to tell process \"Chrome\"" -e "set frontmost to true" -e "end tell"
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
        }
    }

    override fun release(nativeKeyEvent: NativeKeyEvent) {
        if (mapping.containsKey(nativeKeyEvent.keyCode)) {
            tryStoppingRelease(nativeKeyEvent)
        }
    }
}
