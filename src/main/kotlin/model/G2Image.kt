package model

import org.bytedeco.opencv.global.opencv_core.CV_8UC3
import org.bytedeco.opencv.opencv_core.Mat
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte


data class G2Image(val bufferedImage: BufferedImage) {

    val mat: Mat
        get() {
            val image = convertTo3ByteBGRType(bufferedImage)
            val finalMat = Mat(image.height, image.width, CV_8UC3)
            val bytes = (image.data.dataBuffer as DataBufferByte).data
            finalMat.data().put(*bytes)
            return finalMat
        }

    private fun convertTo3ByteBGRType(image: BufferedImage): BufferedImage {
        val convertedImage = BufferedImage(
            image.width, image.height,
            BufferedImage.TYPE_3BYTE_BGR
        )
        convertedImage.graphics.drawImage(image, 0, 0, null)
        return convertedImage
    }
}
