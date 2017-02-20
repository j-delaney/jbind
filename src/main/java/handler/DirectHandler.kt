package handler

import key.SendKey
import mu.KLogging
import org.jnativehook.keyboard.NativeKeyEvent

class DirectHandler(configMapping: Map<String, String>, currentlySending: MutableSet<Int>) : Handler() {
    companion object : KLogging()

    private val mapping: Map<Int, SendKey> = configMapping.mapKeys {
        keyParser.getNativeKeyEvent(it.key)
    }.mapValues {
        keyParser.getSendKey(it.value)
    }

    private val currentlySending = currentlySending

    override fun press(nativeKeyEvent: NativeKeyEvent) {
        val keyPressed = nativeKeyEvent.keyCode

        val sendKey = mapping[keyPressed]
        if (sendKey != null) {
            val pressedKeyText = NativeKeyEvent.getKeyText(keyPressed)
            logger.info("Pressing: $pressedKeyText->${sendKey.keyText}")
            tryStoppingPress(nativeKeyEvent)
            keySimulator.tap(sendKey.keyEventToSend)
            currentlySending.add(sendKey.nativeKeyEvent)
        }
    }

    override fun release(nativeKeyEvent: NativeKeyEvent) {
        if (mapping.containsKey(nativeKeyEvent.keyCode)) {
            tryStoppingRelease(nativeKeyEvent)
        }
    }
}
