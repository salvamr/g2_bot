import ai.G2Ai
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.G2Image
import model.G2ModelInfo
import model.G2Point
import java.awt.Rectangle
import java.awt.Robot
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import kotlin.math.hypot

class G2ImageProcessing(
    private val robot: Robot,
    private val g2Ai: G2Ai,
    private val rectangle: Rectangle = Rectangle(
        (SCREEN_SIZE_X / 2) - (PROCESSING_ZONE_SIZE / 2),
        (SCREEN_SIZE_Y / 2) - (PROCESSING_ZONE_SIZE / 2),
        PROCESSING_ZONE_SIZE,
        PROCESSING_ZONE_SIZE
    )
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    val target: MutableSharedFlow<G2Point> = MutableSharedFlow()

    init {
        launch {
            g2Ai.detectionsFlow.collectLatest { detections ->
                if (detections.isNotEmpty()) {
                    val enemyMap = detections.groupBy { it.info }
                    val enemyHeads = enemyMap[G2ModelInfo.HEAD]
                    val enemyBodies = enemyMap[G2ModelInfo.BODY]

                    when {
                        enemyHeads?.isNotEmpty() == true -> {
                            target.emit(enemyHeads.closestValueToCenter())
                        }
                        enemyBodies?.isNotEmpty() == true -> {
                            target.emit(enemyBodies.closestValueToCenter())
                        }
                    }
                }
            }
        }
    }

    fun start() {
        launch {
            while (!CloseOperations.value) {
                val image = G2Image(robot.createScreenCapture(rectangle))
                g2Ai.findEnemies(image)
                delay(1)
            }
        }
    }

    private fun List<G2Point>.closestValueToCenter(): G2Point {
        val halfScreen = PROCESSING_ZONE_SIZE / 2.0

        return minByOrNull {
            abs(hypot(it.x - halfScreen, it.y - halfScreen))
        } ?: G2Point()
    }
}

