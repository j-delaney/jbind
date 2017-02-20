package key

import org.jnativehook.keyboard.NativeKeyEvent
import java.awt.event.KeyEvent
import java.util.*

data class SendKey(val keyEventToSend: Int, val keyText: String, val nativeKeyEvent: Int)

// TODO: Make this a singleton
class KeyParser {
    private val textToNativeKeyEvent = HashMap<String, Int>()

    init {
        this.generateTextToNativeKeyEvent()
    }

    private fun generateTextToNativeKeyEvent() {
        for (i in 0..65406) {
            val text = NativeKeyEvent.getKeyText(i)
            if (!text.contains("Unknown keyCode")) {
                textToNativeKeyEvent.put(text, i)
            }
        }
    }

    fun getKeyEvent(s: String): Int {
        when (s) {
            "Alt", "Opt", "Option" -> return KeyEvent.VK_ALT
            "Backspace" -> return KeyEvent.VK_BACK_SPACE
            "Down" -> return KeyEvent.VK_DOWN
            "Enter" -> return KeyEvent.VK_ENTER
            "Esc", "Escape" -> return KeyEvent.VK_ESCAPE
            "Forward_Delete" -> return KeyEvent.VK_DELETE
            "Left" -> return KeyEvent.VK_LEFT
            "Meta", "Cmd", "Command" -> return KeyEvent.VK_META
            "Right" -> return KeyEvent.VK_RIGHT
            "Shift" -> return KeyEvent.VK_SHIFT
            "Space" -> return KeyEvent.VK_SPACE
            "Tab" -> return KeyEvent.VK_TAB
            "Up" -> return KeyEvent.VK_UP
            "{" -> return KeyEvent.VK_OPEN_BRACKET
            "}" -> return KeyEvent.VK_CLOSE_BRACKET
            else -> {
                if (s.length != 1) {
                    throw IllegalArgumentException("\"" + s + "\" does not have a predefined mapping to a KeyEvent")
                } else {
                    return KeyEvent.getExtendedKeyCodeForChar(s[0].toInt())
                }
            }
        }
    }

    fun getSendKey(s: String): SendKey {
        return SendKey(getKeyEvent(s), s, getNativeKeyEvent(s))
    }

    fun getNativeKeyEvent(s: String): Int {
        when (s) {
            "Alt", "Opt", "Option" -> return NativeKeyEvent.VC_ALT
            "Backspace" -> return NativeKeyEvent.VC_BACKSPACE
            "Comma", "," -> return NativeKeyEvent.VC_COMMA
            "Down" -> return NativeKeyEvent.VC_DOWN
            "Enter" -> return NativeKeyEvent.VC_ENTER
            "Escape", "Esc" -> return NativeKeyEvent.VC_ESCAPE
            "Forward_Delete" -> return NativeKeyEvent.VC_DELETE
            "Left" -> return NativeKeyEvent.VC_LEFT
            "Meta", "Cmd", "Command" -> return NativeKeyEvent.VC_META
            "Minus", "-" -> return NativeKeyEvent.VC_MINUS
            "Period", "." -> return NativeKeyEvent.VC_PERIOD
            "Right" -> return NativeKeyEvent.VC_RIGHT
            "Shift" -> return NativeKeyEvent.VC_SHIFT
            "Space" -> return NativeKeyEvent.VC_SPACE
            "Tab" -> return NativeKeyEvent.VC_TAB
            "Up" -> return NativeKeyEvent.VC_UP
            "{" -> return NativeKeyEvent.VC_OPEN_BRACKET
            "}" -> return NativeKeyEvent.VC_CLOSE_BRACKET
            else -> {
                val i = textToNativeKeyEvent[s]
                if (i == null) {
                    throw IllegalArgumentException("\"" + s + "\" does not have a mapping to NativeKeyEvent")
                } else {
                    return i
                }
            }
        }
    }
}