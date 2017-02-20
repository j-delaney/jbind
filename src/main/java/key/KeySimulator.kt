package key

import mu.KLogging
import java.awt.Robot
import java.awt.event.KeyEvent

class KeySimulator {
    companion object : KLogging()

    private val robot: Robot = Robot()

    fun press(keyEvent: Int) {
        logger.debug {"Pressing KeyEvent $keyEvent (${KeyEvent.getKeyText(keyEvent)})"}
        robot.keyPress(keyEvent)
    }

    fun release(keyEvent: Int) {
        logger.debug {"Releasing KeyEvent $keyEvent (${KeyEvent.getKeyText(keyEvent)})"}
        robot.keyRelease(keyEvent)
    }

    fun tap(keyEvent: Int) {
        this.press(keyEvent)
        this.release(keyEvent)
    }
}