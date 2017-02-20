package handler

import key.KeyParser
import key.KeySimulator
import mu.KLogging
import org.jnativehook.NativeInputEvent
import org.jnativehook.keyboard.NativeKeyEvent

abstract class Handler {
    companion object : KLogging()

    protected val keySimulator = KeySimulator()
    protected val keyParser = KeyParser()

    protected fun tryStopping(e: NativeKeyEvent, event: String) {
        DirectHandler.logger.debug { "Attempting to stop event $event, ${NativeKeyEvent.getKeyText(e.keyCode)}" }
        try {
            val f = NativeInputEvent::class.java.getDeclaredField("reserved")
            f.isAccessible = true
            f.setShort(e, 0x01.toShort())
        } catch (ex: Exception) {
            DirectHandler.logger.warn("Unable to stop $event, ${NativeKeyEvent.getKeyText(e.keyCode)}")
            ex.printStackTrace()
        }
    }

    protected fun tryStoppingPress(e: NativeKeyEvent) = tryStopping(e, "keyPress")
    protected fun tryStoppingRelease(e: NativeKeyEvent) = tryStopping(e, "keyRelease")

    abstract fun press(nativeKeyEvent: NativeKeyEvent)
    abstract fun release(nativeKeyEvent: NativeKeyEvent)
}