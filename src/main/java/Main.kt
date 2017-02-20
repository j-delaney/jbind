import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import org.jnativehook.GlobalScreen
import java.nio.file.Paths
import java.util.logging.Level
import java.util.logging.Logger

private class OptionParser {
    @Parameter
    var parameters: List<String> = arrayListOf()

    @Parameter(names = arrayOf("-v", "--verbose"), description = "Whether to log debug lines")
    var verbose: Boolean = false


    @Parameter(names = arrayOf("-c", "--config"),
            description = "Config file that declares key mappings (default: ~/config/jbind/mappings.json)")
    var configFilePath: String = Paths.get(System.getenv("HOME"), "config/jbind/mappings.json").toString()

    @Parameter(names = arrayOf("-h", "--help"), help = true)
    var help: Boolean = false
}

fun main(args: Array<String>) {
    val options = OptionParser()
    JCommander(options, *args)

    if (options.verbose) {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG")
    }

    val c = ConsumeEvent()

    val globalScreenLogger = Logger.getLogger(GlobalScreen::class.java.`package`.name)
    globalScreenLogger.level = Level.WARNING

    GlobalScreen.setEventDispatcher(VoidDispatchService())
    GlobalScreen.registerNativeHook()

    GlobalScreen.addNativeKeyListener(c)
}