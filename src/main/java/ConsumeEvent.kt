import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.JsonReader
import handler.*
import key.KeyParser
import mu.KLogging
import org.jnativehook.NativeHookException
import org.jnativehook.NativeInputEvent
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.awt.AWTException
import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

class ConsumeEvent @Throws(NativeHookException::class, AWTException::class)
constructor(configFilePath: String) : NativeKeyListener {
    companion object : KLogging()

    private val configFilePath = configFilePath

    private var hyperDown = false
    private val currentlySending: MutableSet<Int> = mutableSetOf()
    private val keyParser = KeyParser()

    // TODO: Migrate to own class
    private inner class Config {
        internal var hyper: String? = null
        internal var direct: Map<String, String>? = null
        internal var modifiers: Map<String, Array<String>>? = null
        internal var applications: Map<String, String>? = null
        internal var scripts: Map<String, Array<String>>? = null
    }

    private val handlers: MutableList<Handler> = ArrayList()

    private val hyper: Int

    init {
        val config = loadConfigFile()

        if (config.hyper == null) {
            throw IllegalArgumentException("You must specify a hyper key")
        } else {
            hyper = keyParser.getNativeKeyEvent(config.hyper!!)
        }

        if (config.direct != null) {
            handlers.add(DirectHandler(config.direct!!, currentlySending))
        }

        if (config.scripts != null) {
            handlers.add(ScriptHandler(config.scripts!!))
        }

        if (config.modifiers != null) {
            handlers.add(ModifierHandler(config.modifiers!!, currentlySending))
        }

        if (config.applications != null) {
            handlers.add(ApplicationHandler(config.applications!!))
        }
    }

    private fun loadConfigFile(): Config {
        val gson = Gson()

        val fileReader = try {
            FileReader(this.configFilePath)
        } catch(e: FileNotFoundException) {
            logger.error("Could not open config file located at ${this.configFilePath}")
            System.exit(1)
            FileReader("") // Unreachable, but needed so fileReader is non-nullable.
        }

        val jsonReader = JsonReader(fileReader)

        try {
            return gson.fromJson<Config>(jsonReader, Config::class.java)
        } catch (e: Exception) {
            if (e is JsonIOException || e is JsonSyntaxException) {
                logger.error("Could not parse config file")
            }
            throw e
        }
    }

    private fun tryStopping(e: NativeKeyEvent, event: String) {
        logger.debug { "Attempting to stop event $event, ${NativeKeyEvent.getKeyText(e.keyCode)}" }
        try {
            val f = NativeInputEvent::class.java.getDeclaredField("reserved")
            f.isAccessible = true
            f.setShort(e, 0x01.toShort())
        } catch (ex: Exception) {
            logger.warn("Unable to stop $event, ${NativeKeyEvent.getKeyText(e.keyCode)}")
            ex.printStackTrace()
        }
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) {
        fun tryStopping(e: NativeKeyEvent) = tryStopping(e, "keyPressed")

        // e.getKeyCode() uses same number as NativeKeyEvent
        // KeyEvent is its own thing.
        val keyText = NativeKeyEvent.getKeyText(e.keyCode)
        logger.debug { "$keyText (${e.keyCode}) pressed" }
        logger.debug { currentlySending }
        if (currentlySending.remove(e.keyCode)) {
            logger.info { "Not capturing $keyText" }
        } else if (e.keyCode == hyper) {
            hyperDown = true
            logger.debug("Hyper pressed")
            tryStopping(e)
        } else if (hyperDown) {
            handlers.forEach {
                it.press(e)
            }
        }
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) {
        fun tryStopping(e: NativeKeyEvent) = tryStopping(e, "keyReleased")

        if (e.keyCode == hyper) {
            hyperDown = false
            logger.debug("Hyper released")
            tryStopping(e)
        } else if (hyperDown) {
            handlers.forEach {
                it.release(e)
            }
        }
    }

    override fun nativeKeyTyped(e: NativeKeyEvent) { /* Unimplemented */
    }
}