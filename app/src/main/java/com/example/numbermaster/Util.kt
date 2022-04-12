package com.example.numbermaster

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object Util {
    /** OpenGL 用のバッファを作成します。 */
    fun makeFloatBuffer(arr: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(arr.size * 4)  // float は 4 byte
        bb.order(ByteOrder.nativeOrder())
        return bb.asFloatBuffer().apply {
            put(arr)
            position(0)
        }
    }

    /**
     * InputActivityで入力された値のバリデーション(unit testのため、ここに置く)
     * @see "NumberMasterUtilUnitTest.validateUnitTest"
     */
    fun validate(numbers: String): Boolean {
        if (numbers.length != (9 * 9 * 6)) return false

        val zeroIndex = numbers.indexOf("0")
        val size = ((zeroIndex + 1) / 3)
        val cubeMode = numbers.substring(9 * 9 + 1, 9 * 9 + 2) != "0"

        for (zIndex in 0..5) {
            for (yIndex in 0..8) {
                for (xIndex in 0..8) {
                    val index = xIndex + (yIndex * 9) + (zIndex * 9 * 9)
                    val char = numbers.substring(index, index + 1)
                    if (cubeMode) {
                        if (yIndex < (size * 3) && xIndex < (size * 3)) {
                            if (char == "0") {
                                return false
                            }
                        }
                    } else {
                        if (zIndex == 0 && yIndex < (size * 3) && xIndex < (size * 3)) {
                            if (char == "0") {
                                return false
                            }
                        }
                    }
                }
            }
        }

        return true
    }
}