package ai

import PREVIEW
import PROCESSING_ZONE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import model.G2Image
import model.G2ModelInfo
import model.G2Point
import org.bytedeco.opencv.global.opencv_dnn
import org.bytedeco.opencv.global.opencv_dnn.readNetFromDarknet
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.RectVector
import org.bytedeco.opencv.opencv_core.Scalar
import org.bytedeco.opencv.opencv_core.Size
import org.bytedeco.opencv.opencv_dnn.DetectionModel

private const val DEBUGGABLE = false

class G2Ai {
    private val imageDst = Mat()
    private val model: DetectionModel

    val detectionsFlow: MutableSharedFlow<List<G2Point>> = MutableSharedFlow()
    val imageFlow: MutableSharedFlow<Mat> = MutableSharedFlow()

    init {
        println("Loading G2Ai")
        if (DEBUGGABLE) System.setProperty("org.bytedeco.javacpp.logger.debug", "true")
        val net = readNetFromDarknet("yolov4/valo.cfg", "yolov4/valo.weights").apply {
            setPreferableBackend(opencv_dnn.DNN_BACKEND_CUDA)
            setPreferableTarget(opencv_dnn.DNN_TARGET_CUDA)
        }
        model = DetectionModel(net).apply {
            setInputParams(1.0 / 255, Size(PROCESSING_ZONE_SIZE, PROCESSING_ZONE_SIZE), Scalar(), false, false)
        }
        println("G2Ai Loaded!")
    }

    suspend fun findEnemies(image: G2Image) = withContext(Dispatchers.Default) {
        cvtColor(image.mat, imageDst, COLOR_RGBA2RGB)

        val classIdsRef = IntArray(256)
        val boxesRef = RectVector()

        model.detect(imageDst, classIdsRef, FloatArray(0), boxesRef, 0.5f, 0.4f)

        val boxes = boxesRef.get()
        val classIds = classIdsRef.sliceArray(boxes.indices)

        if (PREVIEW) {
            boxes.forEach {
                rectangle(imageDst, it, Scalar(0.0, 255.0, 0.0, 255.0))
            }
            imageFlow.emit(imageDst)
        }

        detectionsFlow.emit(
            boxes.mapIndexed { index, box ->
                val clazz = classIds[index]
                val (x1, x2) = box.tl().x() to box.br().x()
                val (y1, y2) = box.tl().y() to box.br().y()

                G2Point(
                    info = when (clazz) {
                        0 -> G2ModelInfo.BODY
                        1 -> G2ModelInfo.HEAD
                        else -> G2ModelInfo.NONE
                    },
                    x = (x1 + x2) / 2,
                    y = (y1 + y2) / 2
                )
            }
        )
    }
}