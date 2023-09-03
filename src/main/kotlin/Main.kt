import ai.G2Ai
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.G2Point
import org.bytedeco.opencv.global.opencv_highgui.*
import java.awt.Robot
import kotlin.system.exitProcess

val CloseOperations = MutableStateFlow(false)

fun main(): Unit = runBlocking {
    val robot = Robot()
    val g2Ai = G2Ai()
    val imageProcessing = G2ImageProcessing(robot, g2Ai)
    val g2Mouse = G2Mouse()

    // G2 Core loop
    launch {
        imageProcessing.target
            .collectLatest { target ->
                if (!target.isEmpty() && isKeyPressed(1)) {
                    g2Mouse.move(target)
                }
            }
    }

    // Show preview if enabled
    launch {
        g2Ai.imageFlow
            .collectLatest { image ->
                if (waitKey(1) < 0)
                    imshow("frame", image)
            }
    }

    // Watcher for closure
    launch {
        CloseOperations.collectLatest {
            if (it) {
                println("Exiting...")
                if (PREVIEW) destroyAllWindows()
                g2Mouse.closeMouse()
                exitProcess(0)
            }
        }
    }

    // Started
    if (g2Mouse.startMouse()) {
        println("Ready!")
        imageProcessing.start()
        while (!CloseOperations.value) {
            delay(1)
            if (isKeyPressed(34)) closeProcess()
        }
    } else {
        println("Can't open mouse serial port. Probably you have to update the COM port number")
        closeProcess()
    }
}

fun CoroutineScope.closeProcess() {
    launch { CloseOperations.emit(true) }
}

fun <T> performance(action: () -> T): T {
    val start = System.currentTimeMillis()
    val result = action()
    val end = System.currentTimeMillis()
    println("Execution time: ${end - start}")
    return result
}