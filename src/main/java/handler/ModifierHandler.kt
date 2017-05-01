package handler

import key.SendKey
import mu.KLogging
import org.jnativehook.keyboard.NativeKeyEvent

class ModifierHandler (configMapping: Map<String, Array<String>>, currentlySending: MutableSet<Int>) : Handler() {
    companion object : KLogging()

    private val mapping: Map<Int, List<SendKey>> = configMapping.mapKeys {
        keyParser.getNativeKeyEvent(it.key)
    }.mapValues {
        it.value.map {
            keyParser.getSendKey(it)
        }
    }

    private val currentlySending = currentlySending

    override fun press(nativeKeyEvent: NativeKeyEvent) {
        val keyPressed = nativeKeyEvent.keyCode

        val sendKeyList = mapping[keyPressed]
        if (sendKeyList != null) {
            val pressedKeyText = NativeKeyEvent.getKeyText(keyPressed)

            logger.info("Modifer mapping: $pressedKeyText->$sendKeyList")
            tryStoppingPress(nativeKeyEvent)

            sendKeyList.forEach {
                keySimulator.press(it.keyEventToSend)
                currentlySending.add(it.nativeKeyEvent)
            }
            sendKeyList.reversed().forEach { keySimulator.release(it.keyEventToSend) }
        }
    }

    override fun release(nativeKeyEvent: NativeKeyEvent) {
        if (mapping.containsKey(nativeKeyEvent.keyCode)) {
            tryStoppingRelease(nativeKeyEvent)
        }
    }
}
