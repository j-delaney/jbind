import java.util.*
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

class VoidDispatchService : AbstractExecutorService() {
    private var running = false

    init {
        running = true
    }

    override fun shutdown() {
        running = false
    }

    override fun shutdownNow(): List<Runnable> {
        running = false
        return ArrayList(0)
    }

    override fun isShutdown(): Boolean {
        return !running
    }

    override fun isTerminated(): Boolean {
        return !running
    }

    @Throws(InterruptedException::class)
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return true
    }

    override fun execute(r: Runnable) {
        r.run()
    }
}