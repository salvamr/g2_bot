import com.fazecast.jSerialComm.SerialPort
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import model.G2Point
import kotlin.coroutines.CoroutineContext

private const val COM = "COM9"
private const val THRESHOLD = 5

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class G2Mouse(
    private val serialPort: SerialPort = SerialPort.getCommPort(COM).apply { baudRate = 115200 },
    private val mouseInputs: MutableStateFlow<G2Point> = MutableStateFlow(G2Point())
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    init {

        launch {
            mouseInputs
                .filterNot { it.isEmpty() }
                .distinctUntilChanged { old, new ->
                    new.x in (old.x - THRESHOLD)..(old.x + THRESHOLD) && new.y in (old.y - THRESHOLD)..(old.y + THRESHOLD)
                }
                .collectLatest {
                    val moveX = limitMouseCoordinates(it.x)
                    val moveY = limitMouseCoordinates(it.y)
                    serialPort.writeBytes(byteArrayOf(moveX.toByte(), moveY.toByte()), 2L)
                }
        }
    }

    fun startMouse() = serialPort.openPort()

    suspend fun move(point: G2Point) = withContext(Dispatchers.Default) {
        mouseInputs.update { normalizeG2Point(point) }
    }

    fun closeMouse() = serialPort.closePort()

    private fun limitMouseCoordinates(target: Int, limit: Int = MAX_MOUSE_COORDS): Int {
        if (target !in -limit..limit) {
            return if (target < 0) -limit else limit
        }
        return target
    }

    private fun normalizeG2Point(point: G2Point): G2Point {
        val halfScreen = PROCESSING_ZONE_SIZE / 2

        val destinationX = (point.x - halfScreen) * AIM_SPEED
        val destinationY = if (IGNORE_Y) 0 else (point.y - halfScreen) * AIM_SPEED

        return G2Point(x = destinationX.toInt(), y = destinationY.toInt())
    }
}