// 各サイズや座標を算出する

package com.example.numbermaster

class NumberMasterCalculator {
    var numberPanelSize: Int? = null

    /**
     * Loop end value according to size
     * @see "NumberMasterCalculatorUnitTest.getSizeMaxUnitTest"
     */
    fun getSizeMax(size: Int): Int {
        return size * 3 - 1
    }

    /**
     * @see "NumberMasterCalculatorUnitTest.getUseNumbersUnitTest"
     */
    fun getUseNumbers(useCubeMode: Int, size: Int): MutableList<Int> {
        var useNumbers: MutableList<Int> = mutableListOf()
        for (i in 0 until size * size) {
            useNumbers += (1..9).toMutableList()
        }

        if (useCubeMode == 1) {
            useNumbers = (useNumbers + useNumbers + useNumbers + useNumbers + useNumbers + useNumbers).toMutableList()
        }

        return useNumbers
    }

    /**
     * 常に9x9x6作るため、unit testは無し
     */
    fun shuffleNumbers(useCubeMode: Int, size: Int): MutableList<MutableList<MutableList<Int>>> {
        val useNumbers = this.getUseNumbers(useCubeMode, size).shuffled().toMutableList()
        var i = 0
        val sizeMax = this.getSizeMax(size)

        var cubeSideMax = 0
        if (useCubeMode == 1) cubeSideMax = 5

        val numbers = MutableList(6) {MutableList(9) {MutableList(9) {0} } }
        for (cubeSideNumber in 0..cubeSideMax) {
            for (yIndex in 0..sizeMax) {
                for (xIndex in 0..sizeMax) {
                    numbers[cubeSideNumber][yIndex][xIndex] = useNumbers[i]
                    i += 1
                }
            }
        }

        return numbers
    }

    /**
     * @see "NumberMasterCalculatorUnitTest.getRandom9PositionUnitTest"
     */
    fun getRandom9Position(useCubeMode: Int, size: Int, numbers: MutableList<MutableList<MutableList<Int>>>): MutableMap<String, Int> {
        val sizeMax = this.getSizeMax(size)

        var cubeSideMax = 0
        if (useCubeMode == 1) cubeSideMax = 5

        val list9: MutableList<MutableMap<String, Int>> = mutableListOf()

        for (cubeSideNumber in 0..cubeSideMax) {
            for (yIndex in 0..sizeMax) {
                for (xIndex in 0..sizeMax) {
                    if (numbers[cubeSideNumber][yIndex][xIndex] == 9) {
                        list9.add(mutableMapOf(
                            "cubeSideNumber" to cubeSideNumber,
                            "y" to yIndex,
                            "x" to xIndex,
                        ))
                    }
                }
            }
        }

        val randomIndex = (0 until list9.size).random()

        return list9[randomIndex]
    }

    /**
     * 1, 2, 3  3, 6, 9
     * 4, 5, 6 →2, 5, 8
     * 7, 8, 9  1, 4, 7
     * @see "NumberMasterCalculatorUnitTest.rotateListUnitTest"
     */
    fun rotate2DList(target: MutableList<MutableList<Int>>, sizeMax: Int): MutableList<MutableList<Int>> {
        val rowCount = target.size
        val colCount = target[0].size
        val result = MutableList(rowCount) {MutableList(colCount) {0} }

        for (indexRow in 0..sizeMax) {
            for (indexCol in 0..sizeMax) {
                result[indexCol][indexRow] = target[indexRow][sizeMax - indexCol]
            }
        }

        return result
    }

    /**
     * @param target List<Any>
     * @param step Int +1 is 1, 2, 3 -> 3, 1, 2 | -1 is 1, 2, 3 -> 2, 3, 1
     * @see "NumberMasterCalculatorUnitTest.rollListUnitTest"
     */
    fun rollList(target: List<Int?>, step: Int): MutableList<Int?> {
        val result: MutableList<Int?> = mutableListOf()

        if (step == 1) {
            // target.size - 1(last)
            result.add(target[target.size - 1]!!.toInt())

            // 0(first)..target.size - 2(last - 1)
            for (i in 0..(target.size - 2)) {
                result.add(target[i]!!.toInt())
            }
        } else {
            // 1(second)..target.size - 1(last)
            for (i in 1 until target.size) {
                result.add(target[i]!!.toInt())
            }

            // 0(first)
            result.add(target[0]!!.toInt())
        }

        return result
    }

    fun getNextCubeNet(mouseDirection: String, cubeNet: MutableList<MutableList<Int?>>): MutableList<MutableList<Int?>> {
        val result: MutableList<MutableList<Int?>> = cubeNet

        when (mouseDirection) {
            "top" -> {
                // 配列のx = 1を一つ上にずらす
                result[1] = this.rollList(cubeNet[1].toMutableList(), -1)

                result[0][1] = result[1][1]
                result[0][3] = result[1][3]
            }
            "right" -> {
                // 配列のx = 1を一つ右にずらす
                result[0] = this.rollList(cubeNet[0].toMutableList(), +1)

                result[1][1] = result[0][1]
                result[1][3] = result[0][3]
            }
            "bottom" -> {
                // 配列のx = 1を一つ下にずらす
                result[1] = this.rollList(cubeNet[1].toMutableList(), +1)

                result[0][1] = result[1][1]
                result[0][3] = result[1][3]
            }
            "left" -> {
                // 配列のx = 1を一つ左にずらす
                result[0] = this.rollList(cubeNet[0].toMutableList(), -1)

                result[1][1] = result[0][1]
                result[1][3] = result[0][3]
            }
        }

        return result
    }

    // mouseDirection is swipe direction
    fun getTargetCubeSideNumber(mouseDirection: String, cubeNet: MutableList<MutableList<Int?>>): Int {
        when (mouseDirection) {
            "top" -> {
                return cubeNet[1][2]!!
            }
            "right" -> {
                return cubeNet[0][0]!!
            }
            "bottom" -> {
                return cubeNet[1][0]!!
            }
            "left" -> {
                return cubeNet[0][2]!!
            }
        }

        return 0
    }

    fun copy(numbers: MutableList<MutableList<MutableList<Int>>>): MutableList<MutableList<MutableList<Int>>> {
        val newNumbers = MutableList(6) {MutableList(9) {MutableList(9) {0} } }
        for (cubeSideNumber in 0 until numbers.size) {
            for (yIndex in 0 until numbers[cubeSideNumber].size) {
                for (xIndex in 0 until numbers[cubeSideNumber][yIndex].size) {
                    newNumbers[cubeSideNumber][yIndex][xIndex] = numbers[cubeSideNumber][yIndex][xIndex]
                }
            }
        }

        return newNumbers
    }
}