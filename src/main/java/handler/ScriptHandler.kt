package handler

import mu.KLogging
import org.jnativehook.keyboard.NativeKeyEvent
import java.io.IOException

class ScriptHandler(configMapping: Map<String, Array<String>>) : Handler() {
    companion object : KLogging()

    private val mapping: Map<Int, Array<String>> = configMapping.mapKeys {
        keyParser.getNativeKeyEvent(it.key)
    }

    override fun press(nativeKeyEvent: NativeKeyEvent) {
        val keyPressed = nativeKeyEvent.keyCode

        val args = mapping[keyPressed]
        if (args != null) {
            val pressedKeyText = NativeKeyEvent.getKeyText(keyPressed)

            logger.info("Script mapping: $pressedKeyText->${args.asList()}")
            tryStoppingPress(nativeKeyEvent)
            val runtime = Runtime.getRuntime()
            try {
                val process = runtime.exec(args)
                // TODO: Check exit code using process.waitFor().
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