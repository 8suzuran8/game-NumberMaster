package com.example.numbermaster

import kotlin.math.floor

class NumberMasterCheckSuccess {
    var numberMasterCalculator: NumberMasterCalculator? = null
    var sizeMax: Int? = null
    var useCubeMode: Int? = null
    private var checkTable = MutableList(9) {MutableList(9) {0} }

    /**
     * @return null: un success | 1: order by number | 2: magic square | 3: mix(only cube mode)
     */
    fun checkAll(numbers: MutableList<MutableList<MutableList<Int>>>, sizeMax: Int, useCubeMode: Int): Int? {
        this.sizeMax = sizeMax
        this.useCubeMode = useCubeMode
        this.checkTable = MutableList(9) {MutableList(9) {0} }

        var cubeMax = 0
        if (this.useCubeMode == 1) cubeMax = 5

        var success: Int? = null
        for (cubeSideIndex in 0..cubeMax) {
            var cubeSideResult = false
            var number = numbers[cubeSideIndex]
            for (index in 0..3) {
                if (this.checkOrderByNumber(number)) {
                    if (success == null) {
                        success = 1
                    } else if (success == 2) {
                        success = 3
                    }

                    cubeSideResult = true
                    break
                } else if (this.checkMagicSquare(number)) {
                    if (success == null) {
                        success = 2
                    } else if (success == 1) {
                        success = 3
                    }

                    cubeSideResult = true
                    break
                }

                number = this.numberMasterCalculator!!.rotate2DList(number, this.sizeMax!!)
            }

            if (!cubeSideResult) {
                success = null
                break
            }
        }

        return success
    }

    /**
     * 15パズルの確認
     *
     * 見る方向を固定しなければならない︎
     *      →→→↙
     *      →→→↙
     *      →→→
     *
     *      ↘←←←
     *      ↘←←←
     *       ←←←
     *
     *      これの90度回転4パターンある
     *
     * 3x3のみで確認し、他の全ての3x3がそれと同じ並び。
     * 9x9の場合のみ
     *      →→→ →→→ →→→↙ パターンと
     *      3x3の全てが同じ数字として3x3ずつ移動して確認するパターンがある
     */

    /**
     * aとbが連続数か?
     */
    private fun isConsecutiveNumber(a: Int, b: Int): Boolean {
        if (a + 1 == b) return true
        if (a == 9 && b == 1) return true

        return false
    }

    private fun trueCount(list: MutableList<Boolean>): Int {
        var count = 0
        for (value in list) {
            if (value) count += 1
        }

        return count
    }

    private fun checkOrderByNumber(number: MutableList<MutableList<Int>>): Boolean {
        var result = true
        if (!this.checkOrderByNumber3x3(number)) result = false
        if (result) {
            if (this.sizeMax!! == 2) return true

            val checkPatterns = mutableListOf(3)
            if (this.sizeMax!! == 8) {
                checkPatterns.add(6)
            }

            val sizeMax = 2
            for (indexY in 0..sizeMax) {
                for (indexX in 0..sizeMax) {
                    for (checkPattern in checkPatterns) {
                        if (number[indexY][indexX] != number[indexY][indexX + checkPattern]) result = false
                        if (result && number[indexY][indexX] != number[indexY + checkPattern][indexX]) result = false
                        if (result && number[indexY][indexX] != number[indexY + checkPattern][indexX + checkPattern]) result = false

                        if (checkPattern == 6) {
                            if (result && number[indexY][indexX] != number[indexY + 3][indexX + 6]) result = false
                            if (result && number[indexY][indexX] != number[indexY + 6][indexX + 3]) result = false
                        }

                        if (!result) break
                    }

                    if (!result) break
                }

                if (!result) break
            }

            if (result) return true
        }

        if (this.sizeMax!! == 5) {
            return this.checkOrderByNumber6x6(number)
        } else if (this.sizeMax!! == 8) {
            return this.checkOrderByNumber9x9(number)
        }

        return false
    }

    private fun checkOrderByNumber3x3(number: MutableList<MutableList<Int>>): Boolean {
        // 確認方向8パターン
        var srcA: Int
        var srcB: Int
        val checkPatternResults = mutableListOf(
            true, // 左上から右へ
            true, // 左上から下へ
        )

        val sizeMax = 2
        for (indexA in 0..sizeMax) {
            if (this.trueCount(checkPatternResults) == 0) break
            for (indexB in 0..sizeMax) {
                if (indexA == 0 && indexB == 0) continue

                if (checkPatternResults[0] || checkPatternResults[1]) {
                    if (checkPatternResults[0]) {
                        // 左上から右へ
                        srcA = indexA
                        srcB = indexB - 1
                        if (indexB == 0) {
                            srcA = indexA - 1
                            srcB = this.sizeMax!!
                        }
                        checkPatternResults[0] = this.isConsecutiveNumber(
                            number[srcA][srcB],
                            number[indexA][indexB]
                        )
                    }

                    if (checkPatternResults[1]) {
                        // 左上から下へ
                        srcA = indexB - 1
                        srcB = indexA
                        if (indexB == 0) {
                            srcA = this.sizeMax!!
                            srcB = indexA - 1
                        }
                        checkPatternResults[1] = this.isConsecutiveNumber(
                            number[srcA][srcB],
                            number[indexB][indexA]
                        )
                    }
                }
            }
        }

        if (this.trueCount(checkPatternResults) > 0) return true

        return false
    }

    private fun checkOrderByNumber6x6(number: MutableList<MutableList<Int>>): Boolean {
        val patterns = mutableListOf(
            mutableListOf(
                mutableListOf(1, 1, 2, 2, 3, 3), // 0
                mutableListOf(1, 1, 2, 2, 3, 3), // 1
                mutableListOf(4, 4, 5, 5, 6, 6), // 2
                mutableListOf(4, 4, 5, 5, 6, 6), // 3
                mutableListOf(7, 7, 8, 8, 9, 9), // 4
                mutableListOf(7, 7, 8, 8, 9, 9), // 5
            ),
            mutableListOf(
                mutableListOf(3, 3, 2, 2, 1, 1), // 0
                mutableListOf(3, 3, 2, 2, 1, 1), // 1
                mutableListOf(6, 6, 5, 5, 4, 4), // 2
                mutableListOf(6, 6, 5, 5, 4, 4), // 3
                mutableListOf(9, 9, 8, 8, 7, 7), // 4
                mutableListOf(9, 9, 8, 8, 7, 7), // 5
            ),
        )

        for (pattern in patterns) {
            var checkPattern = pattern
            for (rotateIndex in 0..3) {
                var match = true
                for (yIndex in pattern.indices) {
                    for (xIndex in pattern[yIndex].indices) {
                        if (number[yIndex][xIndex] != pattern[yIndex][xIndex]) {
                            match = false
                            break
                        }
                    }
                    if (!match) break
                }

                if (match) return true

                checkPattern = this.numberMasterCalculator!!.rotate2DList(checkPattern, 5)
            }
        }
        return false
    }

    private fun checkOrderByNumber9x9(number: MutableList<MutableList<Int>>): Boolean {
        val checkPatternResults = mutableListOf(
            true, // 1ライン1..9整列&左上から右へ +1
            true, // 1ライン1..9整列&左上から右へ -1
            true, // 1ライン1..9整列&左上から下へ +1
            true, // 1ライン1..9整列&左上から下へ -1
            true, // 1ライン1..9整列&左上から右へ&一段下がると-1start +1
            true, // 1ライン1..9整列&左上から右へ&一段下がると+1start +1
            true, // 1ライン1..9整列&左上から右へ&一段下がると-1start -1
            true, // 1ライン1..9整列&左上から右へ&一段下がると+1start -1
            true, // 1ライン1..9整列&左上から下へ&一段下がると-1start +1
            true, // 1ライン1..9整列&左上から下へ&一段下がると+1start +1
            true, // 1ライン1..9整列&左上から下へ&一段下がると-1start -1
            true, // 1ライン1..9整列&左上から下へ&一段下がると+1start -1
            true, // 3x3全部同じ数字&左上から右へ
            true, // 3x3全部同じ数字&左上から下へ
        )

        var answer: Int
        val sizeMax = 8
        for (indexY in 0..sizeMax) {
            if (this.trueCount(checkPatternResults) == 0) break
            for (indexX in 0..sizeMax) {
                // 1ライン1..9整列&左上から右へ +1
                if (checkPatternResults[0]) {
                    checkPatternResults[0] =
                        number[indexY][indexX] == (indexX + 1)
                }
                // 1ライン1..9整列&左上から右へ -1
                if (checkPatternResults[1]) {
                    checkPatternResults[1] =
                        number[indexY][indexX] == ((this.sizeMax!! - indexX) + 1)
                }
                // 1ライン1..9整列&左上から下へ +1
                if (checkPatternResults[2]) {
                    checkPatternResults[2] =
                        number[indexY][indexX] == (indexY + 1)
                }
                // 1ライン1..9整列&左上から下へ -1
                if (checkPatternResults[3]) {
                    checkPatternResults[3] =
                        number[indexY][indexX] == ((this.sizeMax!! - indexY) + 1)
                }
                // 1ライン1..9整列&左上から右へ+1&一段下がると-1start
                if (checkPatternResults[4]) {
                    var index = indexX - indexY
                    if (index < 0) index += 9
                    answer = ((index) % 9) + 1
                    checkPatternResults[4] =
                        number[indexY][indexX] == answer
                }
                // 1ライン1..9整列&左上から右へ+1&一段下がると+1start
                if (checkPatternResults[5]) {
                    checkPatternResults[5] =
                        number[indexY][indexX] == ((indexX + indexY) % 9) + 1
                }
                // 1ライン1..9整列&左上から右へ-1&一段下がると-1start
                if (checkPatternResults[6]) {
                    checkPatternResults[6] =
                        number[indexY][indexX] == ((this.sizeMax!! - indexX - indexY) % 9) + 1
                }
                // 1ライン1..9整列&左上から右へ-1&一段下がると+1start
                if (checkPatternResults[7]) {
                    answer = ((this.sizeMax!! - indexX + indexY) % 9) + 1
                    checkPatternResults[7] =
                        number[indexY][indexX] == answer
                }
                // 1ライン1..9整列&左上から下へ+1&一段右は-1start
                if (checkPatternResults[8]) {
                    checkPatternResults[8] =
                        number[indexY][indexX] == (indexY - indexX) + 1
                }
                // 1ライン1..9整列&左上から下へ+1&一段右は+1start
                if (checkPatternResults[9]) {
                    answer = (indexY + indexX) + 1
                    if (answer > 9) answer -= 9
                    checkPatternResults[9] =
                        number[indexY][indexX] == answer
                }
                // 1ライン1..9整列&左上から下へ-1&一段下がると-1start
                if (checkPatternResults[10]) {
                    checkPatternResults[10] =
                        number[indexY][indexX] == ((this.sizeMax!! - indexY - indexX) % 9) + 1
                }
                // 1ライン1..9整列&左上から下へ-1&一段下がると+1start
                if (checkPatternResults[11]) {
                    answer = ((this.sizeMax!! - indexY + indexX) % 9) + 1
                    checkPatternResults[11] =
                        number[indexY][indexX] == answer
                }
                // 3x3全部同じ数字&左上から右へ
                if (checkPatternResults[12]) {
                    checkPatternResults[12] =
                        number[indexY][indexX] == ((floor(indexX / 3F) + 1) + (floor(indexY / 3F) * 3)).toInt()
                }
                // 3x3全部同じ数字&左上から下へ
                if (checkPatternResults[13]) {
                    checkPatternResults[13] =
                        number[indexY][indexX] == ((floor(indexY / 3F) + 1) + (floor(indexX / 3F) * 3)).toInt()
                }
            }
        }

        if (this.trueCount(checkPatternResults) > 0) return true

        return false
    }

    // 魔方陣の確認
    fun checkMagicSquare(number: MutableList<MutableList<Int>>): Boolean {
        var lastSum = 0

        val diagonal = mutableListOf(0, 0)
        for (indexA in 0..this.sizeMax!!) {
            var xSum = 0
            var ySum = 0
            for (indexB in 0..this.sizeMax!!) {
                // 横の合計の算出
                xSum += number[indexA][indexB]

                // 縦の合計の算出
                ySum += number[indexB][indexA]
            }

            if (xSum != ySum) return false
            if (lastSum == 0) {
                lastSum = xSum
            } else {
                if (xSum != lastSum) return false
            }

            // 斜めの合計の算出(左上から右下の)
            diagonal[0] += number[indexA][indexA]

            // 斜めの合計の算出(右上から左下の)
            diagonal[1] += number[indexA][this.sizeMax!! - indexA]
        }

        if (diagonal[0] != diagonal[1]) return false
        if (diagonal[0] != lastSum) return false

        return true
    }
}