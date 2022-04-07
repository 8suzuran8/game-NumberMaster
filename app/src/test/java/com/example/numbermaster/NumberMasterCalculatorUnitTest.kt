package com.example.numbermaster

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NumberMasterCalculatorUnitTest {
    @Test
    fun addition_isCorrect() {
        val numberMasterCalculator = NumberMasterCalculator()

        this.rollListUnitTest(numberMasterCalculator)
        this.getRandom9PositionUnitTest(numberMasterCalculator)
        this.getSizeMaxUnitTest(numberMasterCalculator)
        this.getUseNumbersUnitTest(numberMasterCalculator)
        this.rotateListUnitTest(numberMasterCalculator)
    }

    private fun rollListUnitTest(numberMasterCalculator: NumberMasterCalculator) {
        var target: MutableList<Int> = mutableListOf(0, 1, 2, 3, 4)
        var result = numberMasterCalculator.rollList(target, 1)
        assertEquals("4,0,1,2,3", result.joinToString(","))
        result = numberMasterCalculator.rollList(target, -1)
        assertEquals("1,2,3,4,0", result.joinToString(","))

        target = mutableListOf(0, 5, 2, 4, 1)
        result = numberMasterCalculator.rollList(target, 1)
        assertEquals("1,0,5,2,4", result.joinToString(","))
        result = numberMasterCalculator.rollList(target, -1)
        assertEquals("5,2,4,1,0", result.joinToString(","))
    }

    private fun getRandom9PositionUnitTest(numberMasterCalculator: NumberMasterCalculator) {
        var useCubeMode = 0
        var size = 1
        var numbers = mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            )
        )
        var position9: MutableMap<String, Int>

        for (i in 0..5) {
            position9 = numberMasterCalculator.getRandom9Position(useCubeMode, size, numbers)
            assertEquals(0, position9["cubeSideNumber"])
            assertEquals(2, position9["y"])
            assertEquals(2, position9["x"])
        }

        numbers = mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 9),
                mutableListOf(7, 8, 6),
            )
        )
        for (i in 0..5) {
            position9 = numberMasterCalculator.getRandom9Position(useCubeMode, size, numbers)
            assertEquals(0, position9["cubeSideNumber"])
            assertEquals(1, position9["y"])
            assertEquals(2, position9["x"])
        }

        size = 2
        numbers = mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3),
                mutableListOf(4, 5, 9, 4, 5, 9),
                mutableListOf(7, 8, 6, 7, 8, 6),
                mutableListOf(1, 2, 3, 1, 2, 3),
                mutableListOf(4, 9, 5, 9, 5, 4),
                mutableListOf(7, 8, 6, 7, 8, 6),
            )
        )
        for (i in 0..5) {
            position9 = numberMasterCalculator.getRandom9Position(useCubeMode, size, numbers)
            assertEquals(0, position9["cubeSideNumber"])
            assertEquals(true, listOf(1, 4).contains(position9["y"]))
            if (position9["y"] == 1) {
                assertEquals(true, listOf(2, 5).contains(position9["x"]))
            } else {
                assertEquals(true, listOf(1, 3).contains(position9["x"]))
            }
        }

        numbers = mutableListOf(
            mutableListOf(
                mutableListOf(1, 9, 3, 1, 2, 3),
                mutableListOf(4, 5, 2, 4, 5, 9),
                mutableListOf(7, 8, 6, 7, 9, 6),
                mutableListOf(1, 2, 3, 1, 2, 3),
                mutableListOf(4, 9, 5, 8, 5, 4),
                mutableListOf(7, 8, 6, 7, 8, 6),
            )
        )
        for (i in 0..5) {
            position9 = numberMasterCalculator.getRandom9Position(useCubeMode, size, numbers)
            assertEquals(0, position9["cubeSideNumber"])
            assertEquals(true, listOf(0, 1, 2, 4).contains(position9["y"]))
            when {
                position9["y"] == 0 -> {
                    assertEquals(1, position9["x"])
                }
                position9["y"] == 1 -> {
                    assertEquals(5, position9["x"])
                }
                position9["y"] == 2 -> {
                    assertEquals(4, position9["x"])
                }
                position9["y"] == 4 -> {
                    assertEquals(1, position9["x"])
                }
            }
        }

        size = 3
        numbers = mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3),
                mutableListOf(4, 5, 9, 4, 5, 9, 4, 5, 9),
                mutableListOf(7, 8, 6, 7, 8, 6, 7, 8, 6),
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3),
                mutableListOf(4, 9, 5, 9, 5, 4, 4, 9, 5),
                mutableListOf(7, 8, 6, 7, 8, 6, 7, 8, 6),
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3),
                mutableListOf(4, 9, 5, 9, 5, 4, 4, 9, 5),
                mutableListOf(7, 8, 6, 7, 8, 6, 7, 8, 6),
            )
        )
        for (i in 0..5) {
            position9 = numberMasterCalculator.getRandom9Position(useCubeMode, size, numbers)
            assertEquals(0, position9["cubeSideNumber"])
            assertEquals(true, listOf(1, 4, 7).contains(position9["y"]))
            when {
                position9["y"] == 1 -> {
                    assertEquals(true, listOf(2, 5, 8).contains(position9["x"]))
                }
                position9["y"] == 4 -> {
                    assertEquals(true, listOf(1, 3, 7).contains(position9["x"]))
                }
                position9["y"] == 7 -> {
                    assertEquals(true, listOf(1, 3, 7).contains(position9["x"]))
                }
            }
        }

        useCubeMode = 1
        size = 3
        numbers = mutableListOf(
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 9, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
        )
        for (i in 0..5) {
            position9 = numberMasterCalculator.getRandom9Position(useCubeMode, size, numbers)
            assertEquals(1, position9["cubeSideNumber"])
            assertEquals(1, position9["y"])
            assertEquals(2, position9["x"])
        }

        numbers = mutableListOf(
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 9, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
            mutableListOf(
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
                mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
            ),
        )
        for (i in 0..5) {
            position9 = numberMasterCalculator.getRandom9Position(useCubeMode, size, numbers)
            assertEquals(3, position9["cubeSideNumber"])
            assertEquals(3, position9["y"])
            assertEquals(6, position9["x"])
        }
    }

    private fun getSizeMaxUnitTest(numberMasterCalculator: NumberMasterCalculator) {
        var sizeMax = numberMasterCalculator.getSizeMax(1)
        assertEquals(2, sizeMax)

        sizeMax = numberMasterCalculator.getSizeMax(2)
        assertEquals(5, sizeMax)

        sizeMax = numberMasterCalculator.getSizeMax(3)
        assertEquals(8, sizeMax)
    }

    private fun getUseNumbersUnitTest(numberMasterCalculator: NumberMasterCalculator) {
        var useNumbers = numberMasterCalculator.getUseNumbers(0, 1)
        assertEquals(9, useNumbers.size) // 9個あるか？
        var all = (1..9).toMutableList()
        for (i in 0 until useNumbers.size) {
            assertNotEquals(0, all.size) // 0でも、まだ繰り返していたらアウト
            for (value in all) {
                if (value == useNumbers[i]) {
                    all.remove(value)
                    break
                }
            }
        }
        assertEquals(0, all.size) // 全て終わって0出なければアウト

        useNumbers = numberMasterCalculator.getUseNumbers(0, 2)
        assertEquals(9 * 4, useNumbers.size) // 9個あるか？
        all = ((1..9) + (1..9) + (1..9) + (1..9)).toMutableList()
        for (i in 0 until useNumbers.size) {
            assertNotEquals(0, all.size) // 0でも、まだ繰り返していたらアウト
            for (value in all) {
                if (value == useNumbers[i]) {
                    all.remove(value)
                    break
                }
            }
        }
        assertEquals(0, all.size) // 全て終わって0出なければアウト

        useNumbers = numberMasterCalculator.getUseNumbers(0, 3)
        assertEquals(9 * 9, useNumbers.size) // 9個あるか？
        all = ((1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9)).toMutableList()
        for (i in 0 until useNumbers.size) {
            assertNotEquals(0, all.size) // 0でも、まだ繰り返していたらアウト
            for (value in all) {
                if (value == useNumbers[i]) {
                    all.remove(value)
                    break
                }
            }
        }
        assertEquals(0, all.size) // 全て終わって0出なければアウト

        useNumbers = numberMasterCalculator.getUseNumbers(1, 3)
        assertEquals(9 * 9 * 6, useNumbers.size) // 9個あるか？
        all = (
                (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) +
                (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) +
                (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) +
                (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) +
                (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) +
                (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9) + (1..9)
                ).toMutableList()
        for (i in 0 until useNumbers.size) {
            assertNotEquals(0, all.size) // 0でも、まだ繰り返していたらアウト
            for (value in all) {
                if (value == useNumbers[i]) {
                    all.remove(value)
                    break
                }
            }
        }
        assertEquals(0, all.size) // 全て終わって0出なければアウト
    }

    private fun rotateListUnitTest(numberMasterCalculator: NumberMasterCalculator) {
        val inputList1 = mutableListOf(
            mutableListOf(1, 2, 3),
            mutableListOf(4, 5, 6),
            mutableListOf(7, 8, 9),
        )
        val inputList1Results = listOf(
            mutableListOf(
                mutableListOf(3, 6, 9),
                mutableListOf(2, 5, 8),
                mutableListOf(1, 4, 7),
            ),
            mutableListOf(
                mutableListOf(9, 8, 7),
                mutableListOf(6, 5, 4),
                mutableListOf(3, 2, 1),
            ),
        )
/*
        val inputList2 = mutableListOf(
            mutableListOf(1, 2, 3, 4, 5, 6),
            mutableListOf(7, 8, 9, 10, 11, 12),
            mutableListOf(13, 14, 15, 16, 17, 18),
            mutableListOf(19, 20, 21, 22, 23, 24),
            mutableListOf(25, 26, 27, 28, 29, 30),
            mutableListOf(31, 32, 33, 34, 35 36),
        )
 */

        var result = numberMasterCalculator.rotate2DList(inputList1, 2)
        assertTrue(result.size == inputList1Results[0].size)
        assertTrue(result[0].size == inputList1Results[0][0].size)
        for (indexA in 0 until result.size) {
            for (indexB in 0 until result[0].size) {
                assertTrue(result[indexA][indexB] == inputList1Results[0][indexA][indexB])
            }
        }

        result = numberMasterCalculator.rotate2DList(result, 2)
        for (indexA in 0 until result.size) for (indexB in 0 until result[0].size) {
            assertTrue(result[indexA][indexB] == inputList1Results[1][indexA][indexB])
        }

        // result = numberMasterCalculator.rotate2DList(inputList2)
    }
}