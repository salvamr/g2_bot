package ai

import PROCESSING_ZONE_SIZE
import SCREEN_SIZE_X
import SCREEN_SIZE_Y
import User32
import isKeyPressed
import java.awt.Rectangle
import java.awt.Robot
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

fun main() {
    val robot = Robot()
    var close = false
    var counter = 0
    val rectangle = Rectangle(
        (SCREEN_SIZE_X / 2) - (PROCESSING_ZONE_SIZE / 2),
        (SCREEN_SIZE_Y / 2) - (PROCESSING_ZONE_SIZE / 2),
        PROCESSING_ZONE_SIZE,
        PROCESSING_ZONE_SIZE
    )

    while (!close) {

        if (isKeyPressed(1)) {
            val image = robot.createScreenCapture(rectangle)
            ImageIO.write(image, "jpg", Files.newOutputStream(Paths.get("results/image-${++counter}.jpg")))
            Thread.sleep(1000)
        }

        if (isKeyPressed(34)) {
            close = true
            continue
        }
    }
}