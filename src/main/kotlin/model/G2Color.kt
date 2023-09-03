package model

import java.awt.Color
import kotlin.math.abs

data class G2Color(
    private val rgb: Int
) {
    private val color = Color(rgb)

    fun isColored(): Boolean {
        with(color) {
            if (green >= 170) return false

            if (green >= 120) return abs(red - blue) <= 8 &&
                    red - green >= 50 &&
                    blue - green >= 50 &&
                    red >= 105 &&
                    blue >= 105

            return abs(red - blue) <= 13 &&
                    red - green >= 60 &&
                    blue - green >= 60 &&
                    red >= 110 &&
                    blue >= 100
        }
    }
}