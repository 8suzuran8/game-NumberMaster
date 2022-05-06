package com.example.numbermaster

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NumberMasterCheckSuccessUnitTest {
    private val numberMasterCheckSuccess: NumberMasterCheckSuccess = NumberMasterCheckSuccess()
    private val checkMagicNumbers3x3 = listOf(
        mutableListOf(
            mutableListOf(8, 1, 6),
            mutableListOf(3, 5, 7),
            mutableListOf(4, 9, 2),
        ),
        mutableListOf(
            mutableListOf(4, 3, 8),
            mutableListOf(9, 5, 1),
            mutableListOf(2, 7, 6),
        ),
        mutableListOf(
            mutableListOf(2, 9, 4),
            mutableListOf(7, 5, 3),
            mutableListOf(6, 1, 8),
        ),
        mutableListOf(
            mutableListOf(6, 7, 2),
            mutableListOf(1, 5, 9),
            mutableListOf(8, 3, 4),
        ),
    )
    private val checkOrderByNumbers6x6 = listOf(
        mutableListOf( // 6x6
            mutableListOf(1, 2, 3, 1, 2, 3, 0, 0, 0), // 0
            mutableListOf(4, 5, 6, 4, 5, 6, 0, 0, 0), // 1
            mutableListOf(7, 8, 9, 7, 8, 9, 0, 0, 0), // 2
            mutableListOf(1, 2, 3, 1, 2, 3, 0, 0, 0), // 3
            mutableListOf(4, 5, 6, 4, 5, 6, 0, 0, 0), // 4
            mutableListOf(7, 8, 9, 7, 8, 9, 0, 0, 0), // 5
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 6
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 7
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 8
        ),
        mutableListOf( // 固まり
            mutableListOf(1, 1, 2, 2, 3, 3, 0, 0, 0), // 0
            mutableListOf(1, 1, 2, 2, 3, 3, 0, 0, 0), // 1
            mutableListOf(4, 4, 5, 5, 6, 6, 0, 0, 0), // 2
            mutableListOf(4, 4, 5, 5, 6, 6, 0, 0, 0), // 3
            mutableListOf(7, 7, 8, 8, 9, 9, 0, 0, 0), // 4
            mutableListOf(7, 7, 8, 8, 9, 9, 0, 0, 0), // 5
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 6
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 7
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 8
        ),
        mutableListOf( // 固まり
            mutableListOf(1, 1, 4, 4, 7, 7, 0, 0, 0), // 0
            mutableListOf(1, 1, 4, 4, 7, 7, 0, 0, 0), // 1
            mutableListOf(2, 2, 5, 5, 8, 8, 0, 0, 0), // 2
            mutableListOf(2, 2, 5, 5, 8, 8, 0, 0, 0), // 3
            mutableListOf(3, 3, 6, 6, 9, 9, 0, 0, 0), // 4
            mutableListOf(3, 3, 6, 6, 9, 9, 0, 0, 0), // 5
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 6
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 7
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0), // 8
        ),
    )
    private val checkOrderByNumbers = listOf(
        /**
         * 一段違いの逆サイドは、隣り合っているとする。
         * 9と1は連続した数とする。
         *
         * 全てのお隣さんが連続した数。
         * 又は、
         * 3x3のみの全てのお隣さんが連続した数、且つ他の全ての3x3がそれと同じ並び。
         */
        mutableListOf( // 0 パターン1
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
        ),
        mutableListOf( // 1 パターン1の回転
            mutableListOf(7, 4, 1, 7, 4, 1, 7, 4, 1),
            mutableListOf(8, 5, 2, 8, 5, 2, 8, 5, 2),
            mutableListOf(9, 6, 3, 9, 6, 3, 9, 6, 3),
            mutableListOf(7, 4, 1, 7, 4, 1, 7, 4, 1),
            mutableListOf(8, 5, 2, 8, 5, 2, 8, 5, 2),
            mutableListOf(9, 6, 3, 9, 6, 3, 9, 6, 3),
            mutableListOf(7, 4, 1, 7, 4, 1, 7, 4, 1),
            mutableListOf(8, 5, 2, 8, 5, 2, 8, 5, 2),
            mutableListOf(9, 6, 3, 9, 6, 3, 9, 6, 3),
        ),
        mutableListOf( // 2 パターン1の回転
            mutableListOf(9, 8, 7, 9, 8, 7, 9, 8, 7),
            mutableListOf(6, 5, 4, 6, 5, 4, 6, 5, 4),
            mutableListOf(3, 2, 1, 3, 2, 1, 3, 2, 1),
            mutableListOf(9, 8, 7, 9, 8, 7, 9, 8, 7),
            mutableListOf(6, 5, 4, 6, 5, 4, 6, 5, 4),
            mutableListOf(3, 2, 1, 3, 2, 1, 3, 2, 1),
            mutableListOf(9, 8, 7, 9, 8, 7, 9, 8, 7),
            mutableListOf(6, 5, 4, 6, 5, 4, 6, 5, 4),
            mutableListOf(3, 2, 1, 3, 2, 1, 3, 2, 1),
        ),
        mutableListOf( // 3 パターン1の回転
            mutableListOf(3, 6, 9, 3, 6, 9, 3, 6, 9),
            mutableListOf(2, 5, 8, 2, 5, 8, 2, 5, 8),
            mutableListOf(1, 4, 7, 1, 4, 7, 1, 4, 7),
            mutableListOf(3, 6, 9, 3, 6, 9, 3, 6, 9),
            mutableListOf(2, 5, 8, 2, 5, 8, 2, 5, 8),
            mutableListOf(1, 4, 7, 1, 4, 7, 1, 4, 7),
            mutableListOf(3, 6, 9, 3, 6, 9, 3, 6, 9),
            mutableListOf(2, 5, 8, 2, 5, 8, 2, 5, 8),
            mutableListOf(1, 4, 7, 1, 4, 7, 1, 4, 7),
        ),
        // 以下は9x9の場合のみOK
        mutableListOf( // 4 パターン2
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 1
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 2
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 3
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 4
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 5
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 6
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 7
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 8
        ),
        mutableListOf( // 5 パターン2の回転
            mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1), // 0
            mutableListOf(2, 2, 2, 2, 2, 2, 2, 2, 2), // 1
            mutableListOf(3, 3, 3, 3, 3, 3, 3, 3, 3), // 2
            mutableListOf(4, 4, 4, 4, 4, 4, 4, 4, 4), // 3
            mutableListOf(5, 5, 5, 5, 5, 5, 5, 5, 5), // 4
            mutableListOf(6, 6, 6, 6, 6, 6, 6, 6, 6), // 5
            mutableListOf(7, 7, 7, 7, 7, 7, 7, 7, 7), // 6
            mutableListOf(8, 8, 8, 8, 8, 8, 8, 8, 8), // 7
            mutableListOf(9, 9, 9, 9, 9, 9, 9, 9, 9), // 8
        ),
        mutableListOf( // 6 パターン2の回転
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 0
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 1
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 2
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 3
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 4
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 5
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 6
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 7
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 8
        ),
        mutableListOf( // 7 パターン2の回転
            mutableListOf(9, 9, 9, 9, 9, 9, 9, 9, 9), // 8
            mutableListOf(8, 8, 8, 8, 8, 8, 8, 8, 8), // 7
            mutableListOf(7, 7, 7, 7, 7, 7, 7, 7, 7), // 6
            mutableListOf(6, 6, 6, 6, 6, 6, 6, 6, 6), // 5
            mutableListOf(5, 5, 5, 5, 5, 5, 5, 5, 5), // 4
            mutableListOf(4, 4, 4, 4, 4, 4, 4, 4, 4), // 3
            mutableListOf(3, 3, 3, 3, 3, 3, 3, 3, 3), // 2
            mutableListOf(2, 2, 2, 2, 2, 2, 2, 2, 2), // 1
            mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1), // 0
        ),
        mutableListOf( // 8 パターン3
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
            mutableListOf(2, 3, 4, 5, 6, 7, 8, 9, 1), // 1
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 2
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 3
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 4
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 5
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 6
            mutableListOf(8, 9, 1, 2, 3, 4, 5, 6, 7), // 7
            mutableListOf(9, 1, 2, 3, 4, 5, 6, 7, 8), // 8
        ),
        mutableListOf( // 9 パターン3の回転
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 0
            mutableListOf(1, 9, 8, 7, 6, 5, 4, 3, 2), // 1
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 2
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 3
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 4
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 5
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 6
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 7
            mutableListOf(8, 7, 6, 5, 4, 3, 2, 1, 9), // 8
        ),
        mutableListOf( // 10 パターン3の回転
            mutableListOf(8, 7, 6, 5, 4, 3, 2, 1, 9), // 0
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 1
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 2
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 3
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 4
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 5
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 6
            mutableListOf(1, 9, 8, 7, 6, 5, 4, 3, 2), // 7
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 8
        ),
        mutableListOf( // 11 パターン3の回転
            mutableListOf(9, 1, 2, 3, 4, 5, 6, 7, 8), // 0
            mutableListOf(8, 9, 1, 2, 3, 4, 5, 6, 7), // 8
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 7
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 6
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 5
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 4
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 3
            mutableListOf(2, 3, 4, 5, 6, 7, 8, 9, 1), // 2
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 1
        ),
        mutableListOf( // 12 パターン4
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
            mutableListOf(9, 1, 2, 3, 4, 5, 6, 7, 8), // 1
            mutableListOf(8, 9, 1, 2, 3, 4, 5, 6, 7), // 2
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 3
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 4
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 5
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 6
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 7
            mutableListOf(2, 3, 4, 5, 6, 7, 8, 9, 1), // 8
        ),
        // パターン4の回転
        mutableListOf( // 13
            mutableListOf(2, 3, 4, 5, 6, 7, 8, 9, 1), // 0
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 1
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 2
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 3
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 4
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 5
            mutableListOf(8, 9, 1, 2, 3, 4, 5, 6, 7), // 6
            mutableListOf(9, 1, 2, 3, 4, 5, 6, 7, 8), // 7
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 8
        ),
        // パターン4の回転
        mutableListOf( // 14
            mutableListOf(1, 9, 8, 7, 6, 5, 4, 3, 2), // 0
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 1
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 2
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 3
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 4
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 5
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 6
            mutableListOf(8, 7, 6, 5, 4, 3, 2, 1, 9), // 7
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 8
        ),
        // パターン4の回転
        mutableListOf( // 15
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 0
            mutableListOf(8, 7, 6, 5, 4, 3, 2, 1, 9), // 1
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 2
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 3
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 4
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 5
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 6
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 7
            mutableListOf(1, 9, 8, 7, 6, 5, 4, 3, 2), // 8
        ),
        mutableListOf( // 16 パターン5
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 0
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 8
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 7
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 6
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 5
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 4
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 3
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 2
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 1
        ),
        mutableListOf( // 17 パターン5の回転
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 0
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 8
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 7
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 6
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 5
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 4
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 3
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 2
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 1
        ),
        mutableListOf( // 18 パターン5の回転
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 0
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 8
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 7
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 6
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 5
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 4
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 3
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 2
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 1
        ),
        mutableListOf( // 19 パターン5の回転
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 0
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 8
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 7
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 6
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 5
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 4
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 3
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 2
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 1
        ),
        mutableListOf( // 20 パターン6
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 0
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 8
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 7
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 6
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 5
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 4
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 3
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 2
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 1
        ),
        mutableListOf( // 21 パターン6の回転
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 0
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 8
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 7
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 6
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 5
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 4
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 3
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 2
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 1
        ),
        mutableListOf( // 22 パターン6の回転
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 0
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 8
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 7
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 6
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 5
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 4
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 3
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 2
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 1
        ),
        mutableListOf( // 23 パターン6の回転
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 0
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 8
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 7
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 6
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 5
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 4
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 3
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 2
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 1
        ),
    )
    private val checkNgOrderByNumbers = listOf(
        /**
         * 一段違いの逆サイドは、隣り合っているとする。
         * 9と1は連続した数とする。
         *
         * 全てのお隣さんが連続した数。
         * 又は、
         * 3x3のみの全てのお隣さんが連続した数、且つ他の全ての3x3がそれと同じ並び。
         */
        mutableListOf( // 0 パターン1
            mutableListOf(3, 2, 1, 1, 2, 3, 1, 2, 3), // 0
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
        ),
        mutableListOf( // 1 パターン1の回転
            mutableListOf(1, 4, 7, 7, 4, 1, 7, 4, 1),
            mutableListOf(8, 5, 2, 8, 5, 2, 8, 5, 2),
            mutableListOf(9, 6, 3, 9, 6, 3, 9, 6, 3),
            mutableListOf(7, 4, 1, 7, 4, 1, 7, 4, 1),
            mutableListOf(8, 5, 2, 8, 5, 2, 8, 5, 2),
            mutableListOf(9, 6, 3, 9, 6, 3, 9, 6, 3),
            mutableListOf(7, 4, 1, 7, 4, 1, 7, 4, 1),
            mutableListOf(8, 5, 2, 8, 5, 2, 8, 5, 2),
            mutableListOf(9, 6, 3, 9, 6, 3, 9, 6, 3),
        ),
        mutableListOf( // 2 パターン1の回転
            mutableListOf(7, 8, 9, 9, 8, 7, 9, 8, 7),
            mutableListOf(6, 5, 4, 6, 5, 4, 6, 5, 4),
            mutableListOf(3, 2, 1, 3, 2, 1, 3, 2, 1),
            mutableListOf(9, 8, 7, 9, 8, 7, 9, 8, 7),
            mutableListOf(6, 5, 4, 6, 5, 4, 6, 5, 4),
            mutableListOf(3, 2, 1, 3, 2, 1, 3, 2, 1),
            mutableListOf(9, 8, 7, 9, 8, 7, 9, 8, 7),
            mutableListOf(6, 5, 4, 6, 5, 4, 6, 5, 4),
            mutableListOf(3, 2, 1, 3, 2, 1, 3, 2, 1),
        ),
        mutableListOf( // 3 パターン1の回転
            mutableListOf(9, 6, 3, 3, 6, 9, 3, 6, 9),
            mutableListOf(2, 5, 8, 2, 5, 8, 2, 5, 8),
            mutableListOf(1, 4, 7, 1, 4, 7, 1, 4, 7),
            mutableListOf(3, 6, 9, 3, 6, 9, 3, 6, 9),
            mutableListOf(2, 5, 8, 2, 5, 8, 2, 5, 8),
            mutableListOf(1, 4, 7, 1, 4, 7, 1, 4, 7),
            mutableListOf(3, 6, 9, 3, 6, 9, 3, 6, 9),
            mutableListOf(2, 5, 8, 2, 5, 8, 2, 5, 8),
            mutableListOf(1, 4, 7, 1, 4, 7, 1, 4, 7),
        ),
        // 以下は9x9の場合のみOK
        mutableListOf( // 4 パターン2
            mutableListOf(3, 2, 1, 4, 5, 6, 7, 8, 9), // 0
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 1
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 2
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 3
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 4
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 5
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 6
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 7
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 8
        ),
        mutableListOf( // 5 パターン2の回転
            mutableListOf(1, 2, 1, 1, 1, 1, 1, 1, 1), // 0
            mutableListOf(2, 1, 2, 2, 2, 2, 2, 2, 2), // 1
            mutableListOf(3, 3, 3, 3, 3, 3, 3, 3, 3), // 2
            mutableListOf(4, 4, 4, 4, 4, 4, 4, 4, 4), // 3
            mutableListOf(5, 5, 5, 5, 5, 5, 5, 5, 5), // 4
            mutableListOf(6, 6, 6, 6, 6, 6, 6, 6, 6), // 5
            mutableListOf(7, 7, 7, 7, 7, 7, 7, 7, 7), // 6
            mutableListOf(8, 8, 8, 8, 8, 8, 8, 8, 8), // 7
            mutableListOf(9, 9, 9, 9, 9, 9, 9, 9, 9), // 8
        ),
        mutableListOf( // 6 パターン2の回転
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 0
            mutableListOf(8, 9, 7, 6, 5, 4, 3, 2, 1), // 1
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 2
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 3
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 4
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 5
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 6
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 7
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 8
        ),
        mutableListOf( // 7 パターン2の回転
            mutableListOf(9, 8, 9, 9, 9, 9, 9, 9, 9), // 8
            mutableListOf(8, 9, 8, 8, 8, 8, 8, 8, 8), // 7
            mutableListOf(7, 7, 7, 7, 7, 7, 7, 7, 7), // 6
            mutableListOf(6, 6, 6, 6, 6, 6, 6, 6, 6), // 5
            mutableListOf(5, 5, 5, 5, 5, 5, 5, 5, 5), // 4
            mutableListOf(4, 4, 4, 4, 4, 4, 4, 4, 4), // 3
            mutableListOf(3, 3, 3, 3, 3, 3, 3, 3, 3), // 2
            mutableListOf(2, 2, 2, 2, 2, 2, 2, 2, 2), // 1
            mutableListOf(1, 1, 1, 1, 1, 1, 1, 1, 1), // 0
        ),
        mutableListOf( // 8 パターン3
            mutableListOf(3, 2, 1, 4, 5, 6, 7, 8, 9), // 0
            mutableListOf(2, 3, 4, 5, 6, 7, 8, 9, 1), // 1
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 2
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 3
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 4
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 5
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 6
            mutableListOf(8, 9, 1, 2, 3, 4, 5, 6, 7), // 7
            mutableListOf(9, 1, 2, 3, 4, 5, 6, 7, 8), // 8
        ),
        mutableListOf( // 9 パターン3の回転
            mutableListOf(7, 8, 9, 6, 5, 4, 3, 2, 1), // 0
            mutableListOf(1, 9, 8, 7, 6, 5, 4, 3, 2), // 1
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 2
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 3
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 4
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 5
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 6
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 7
            mutableListOf(8, 7, 6, 5, 4, 3, 2, 1, 9), // 8
        ),
        mutableListOf( // 10 パターン3の回転
            mutableListOf(6, 7, 8, 5, 4, 3, 2, 1, 9), // 0
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 1
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 2
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 3
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 4
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 5
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 6
            mutableListOf(1, 9, 8, 7, 6, 5, 4, 3, 2), // 7
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 8
        ),
        mutableListOf( // 11 パターン3の回転
            mutableListOf(9, 9, 2, 3, 4, 5, 6, 7, 8), // 0
            mutableListOf(8, 1, 1, 2, 3, 4, 5, 6, 7), // 8
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 7
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 6
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 5
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 4
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 3
            mutableListOf(2, 3, 4, 5, 6, 7, 8, 9, 1), // 2
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 1
        ),
        mutableListOf( // 12 パターン4
            mutableListOf(3, 2, 1, 4, 5, 6, 7, 8, 9), // 0
            mutableListOf(9, 1, 2, 3, 4, 5, 6, 7, 8), // 1
            mutableListOf(8, 9, 1, 2, 3, 4, 5, 6, 7), // 2
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 3
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 4
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 5
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 6
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 7
            mutableListOf(2, 3, 4, 5, 6, 7, 8, 9, 1), // 8
        ),
        // パターン4の回転
        mutableListOf( // 13
            mutableListOf(4, 3, 2, 5, 6, 7, 8, 9, 1), // 0
            mutableListOf(3, 4, 5, 6, 7, 8, 9, 1, 2), // 1
            mutableListOf(4, 5, 6, 7, 8, 9, 1, 2, 3), // 2
            mutableListOf(5, 6, 7, 8, 9, 1, 2, 3, 4), // 3
            mutableListOf(6, 7, 8, 9, 1, 2, 3, 4, 5), // 4
            mutableListOf(7, 8, 9, 1, 2, 3, 4, 5, 6), // 5
            mutableListOf(8, 9, 1, 2, 3, 4, 5, 6, 7), // 6
            mutableListOf(9, 1, 2, 3, 4, 5, 6, 7, 8), // 7
            mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 8
        ),
        // パターン4の回転
        mutableListOf( // 14
            mutableListOf(8, 9, 1, 7, 6, 5, 4, 3, 2), // 0
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 1
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 2
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 3
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 4
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 5
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 6
            mutableListOf(8, 7, 6, 5, 4, 3, 2, 1, 9), // 7
            mutableListOf(9, 8, 7, 6, 5, 4, 3, 2, 1), // 8
        ),
        // パターン4の回転
        mutableListOf( // 15
            mutableListOf(8, 9, 7, 6, 5, 4, 3, 2, 1), // 0
            mutableListOf(8, 7, 6, 5, 4, 3, 2, 1, 9), // 1
            mutableListOf(7, 6, 5, 4, 3, 2, 1, 9, 8), // 2
            mutableListOf(6, 5, 4, 3, 2, 1, 9, 8, 7), // 3
            mutableListOf(5, 4, 3, 2, 1, 9, 8, 7, 6), // 4
            mutableListOf(4, 3, 2, 1, 9, 8, 7, 6, 5), // 5
            mutableListOf(3, 2, 1, 9, 8, 7, 6, 5, 4), // 6
            mutableListOf(2, 1, 9, 8, 7, 6, 5, 4, 3), // 7
            mutableListOf(1, 9, 8, 7, 6, 5, 4, 3, 2), // 8
        ),
        mutableListOf( // 16 パターン5
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 0
            mutableListOf(1, 1, 2, 1, 2, 2, 3, 3, 3), // 8
            mutableListOf(1, 1, 1, 2, 2, 2, 3, 3, 3), // 7
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 6
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 5
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 4
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 3
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 2
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 1
        ),
        mutableListOf( // 17 パターン5の回転
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 0
            mutableListOf(7, 7, 4, 7, 4, 4, 1, 1, 1), // 8
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 7
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 6
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 5
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 4
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 3
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 2
            mutableListOf(9, 9, 9, 6, 6, 6, 3, 3, 3), // 1
        ),
        mutableListOf( // 18 パターン5の回転
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 0
            mutableListOf(9, 9, 8, 9, 8, 8, 7, 7, 7), // 8
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 7
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 6
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 5
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 4
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 3
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 2
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 1
        ),
        mutableListOf( // 19 パターン5の回転
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 0
            mutableListOf(3, 3, 6, 3, 6, 6, 9, 9, 9), // 8
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 7
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 6
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 5
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 4
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 3
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 2
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 1
        ),
        mutableListOf( // 20 パターン6
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 0
            mutableListOf(3, 3, 2, 3, 2, 2, 1, 1, 1), // 8
            mutableListOf(3, 3, 3, 2, 2, 2, 1, 1, 1), // 7
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 6
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 5
            mutableListOf(6, 6, 6, 5, 5, 5, 4, 4, 4), // 4
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 3
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 2
            mutableListOf(9, 9, 9, 8, 8, 8, 7, 7, 7), // 1
        ),
        mutableListOf( // 21 パターン6の回転
            mutableListOf(6, 6, 6, 9, 9, 9, 3, 3, 3), // 0
            mutableListOf(6, 6, 6, 9, 9, 9, 3, 3, 3), // 8
            mutableListOf(6, 6, 6, 9, 9, 9, 3, 3, 3), // 7
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 6
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 5
            mutableListOf(8, 8, 8, 5, 5, 5, 2, 2, 2), // 4
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 3
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 2
            mutableListOf(7, 7, 7, 4, 4, 4, 1, 1, 1), // 1
        ),
        mutableListOf( // 22 パターン6の回転
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 0
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 8
            mutableListOf(7, 7, 7, 8, 8, 8, 9, 9, 9), // 7
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 6
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 5
            mutableListOf(4, 4, 4, 5, 5, 5, 6, 6, 6), // 4
            mutableListOf(2, 2, 2, 1, 1, 1, 3, 3, 3), // 3
            mutableListOf(2, 2, 2, 1, 1, 1, 3, 3, 3), // 2
            mutableListOf(2, 2, 2, 1, 1, 1, 3, 3, 3), // 1
        ),
        mutableListOf( // 23 パターン6の回転
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 0
            mutableListOf(1, 1, 4, 1, 4, 4, 7, 7, 7), // 8
            mutableListOf(1, 1, 1, 4, 4, 4, 7, 7, 7), // 7
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 6
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 5
            mutableListOf(2, 2, 2, 5, 5, 5, 8, 8, 8), // 4
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 3
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 2
            mutableListOf(3, 3, 3, 6, 6, 6, 9, 9, 9), // 1
        ),
        mutableListOf( // 24
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
            mutableListOf(4, 5, 6, 4, 5, 9, 4, 5, 6), // 7
            mutableListOf(7, 8, 9, 7, 8, 6, 7, 8, 9), // 8
        ),
        mutableListOf( // 25
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
            mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
            mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
            mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
            mutableListOf(1, 2, 3, 1, 2, 9, 1, 2, 3), // 6
            mutableListOf(4, 5, 6, 4, 5, 3, 4, 5, 6), // 7
            mutableListOf(7, 8, 9, 7, 8, 6, 7, 8, 9), // 8
        ),
    )
    private val checkCubeMode3x3 = listOf(
        mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            ),
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            ),
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            ),
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            ),
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            ),
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            ),
        ),
        mutableListOf(
            mutableListOf(
                mutableListOf(8, 1, 6),
                mutableListOf(3, 5, 7),
                mutableListOf(4, 9, 2),
            ),
            mutableListOf(
                mutableListOf(8, 1, 6),
                mutableListOf(3, 5, 7),
                mutableListOf(4, 9, 2),
            ),
            mutableListOf(
                mutableListOf(8, 1, 6),
                mutableListOf(3, 5, 7),
                mutableListOf(4, 9, 2),
            ),
            mutableListOf(
                mutableListOf(8, 1, 6),
                mutableListOf(3, 5, 7),
                mutableListOf(4, 9, 2),
            ),
            mutableListOf(
                mutableListOf(8, 1, 6),
                mutableListOf(3, 5, 7),
                mutableListOf(4, 9, 2),
            ),
            mutableListOf(
                mutableListOf(8, 1, 6),
                mutableListOf(3, 5, 7),
                mutableListOf(4, 9, 2),
            ),
        ),
        mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3),
                mutableListOf(4, 5, 6),
                mutableListOf(7, 8, 9),
            ),
            mutableListOf(
                mutableListOf(1, 4, 7),
                mutableListOf(2, 5, 8),
                mutableListOf(3, 6, 9),
            ),
            mutableListOf(
                mutableListOf(8, 1, 6),
                mutableListOf(3, 5, 7),
                mutableListOf(4, 9, 2),
            ),
            mutableListOf(
                mutableListOf(4, 3, 8),
                mutableListOf(9, 5, 1),
                mutableListOf(2, 7, 6),
            ),
            mutableListOf(
                mutableListOf(2, 9, 4),
                mutableListOf(7, 5, 3),
                mutableListOf(6, 1, 8),
            ),
            mutableListOf(
                mutableListOf(6, 7, 2),
                mutableListOf(1, 5, 9),
                mutableListOf(8, 3, 4),
            ),
        ),
    )
    private val checkCubeMode = listOf(
        mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
        ),
        mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
                mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 9), // 0
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
        ),
    )
    private val checkNgCubeMode = listOf(
        mutableListOf(
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
            mutableListOf(
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 0
                mutableListOf(4, 5, 4, 6, 5, 6, 4, 5, 6), // 1
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 2
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 3
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 4
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 5
                mutableListOf(1, 2, 3, 1, 2, 3, 1, 2, 3), // 6
                mutableListOf(4, 5, 6, 4, 5, 6, 4, 5, 6), // 7
                mutableListOf(7, 8, 9, 7, 8, 9, 7, 8, 9), // 8
            ),
        ),
    )

    @Test
    fun addition_isCorrect() {
        this.numberMasterCheckSuccess.numberMasterCalculator = NumberMasterCalculator()
        this.checkOrderByNumberUnitTest()
        this.checkMagicSquareUnitTest()
    }

    private fun checkOrderByNumberUnitTest() {
        this.checkOrderByNumberOkUnitTest()
        this.checkOrderByNumberNgUnitTest()
        this.checkCubeModeOkUnitTest()
        this.checkCubeModeNgUnitTest()
    }

    private fun checkOrderByNumberOkUnitTest() {
        this.numberMasterCheckSuccess.useCubeMode = 0

        for (sizeMax in listOf(2, 5, 8)) {
            this.numberMasterCheckSuccess.sizeMax = sizeMax

            for ((index, element) in this.checkOrderByNumbers.withIndex()) {
                if (sizeMax < 8 && index >= 4) break
                val result = this.numberMasterCheckSuccess.checkAll(mutableListOf(element), sizeMax, 0)
                assertEquals(1, result)
            }
            // 6x6専用
            if (sizeMax == 5) {
                for (element in this.checkOrderByNumbers6x6) {
                    val result = this.numberMasterCheckSuccess.checkAll(mutableListOf(element), sizeMax, 0)
                    assertEquals(1, result)
                }
            }
        }
    }

    private fun checkOrderByNumberNgUnitTest() {
        this.numberMasterCheckSuccess.useCubeMode = 0

        for (sizeMax in listOf(2, 5, 8)) {
            this.numberMasterCheckSuccess.sizeMax = sizeMax

            for ((index, element) in this.checkNgOrderByNumbers.withIndex()) {
                if (sizeMax < 8 && index >= 4) break
                val result = this.numberMasterCheckSuccess.checkAll(mutableListOf(element), sizeMax, 0)
                if (result != null) {
                    assertEquals(24, index)
                    assertEquals(8, sizeMax)
                }
                assertEquals(null, result)
            }
        }
    }

    private fun checkCubeModeNgUnitTest() {
        this.numberMasterCheckSuccess.useCubeMode = 0

        for (sizeMax in listOf(2, 5, 8)) {
            this.numberMasterCheckSuccess.sizeMax = sizeMax

            for (element in this.checkNgCubeMode) {
                val result = this.numberMasterCheckSuccess.checkAll(element, sizeMax, 1)
                assertEquals(null, result)
            }
        }
    }

    private fun checkCubeModeOkUnitTest() {
        this.numberMasterCheckSuccess.useCubeMode = 1

        for (element in this.checkCubeMode) {
            val result = this.numberMasterCheckSuccess.checkAll(element, 8, 1)
            assertEquals(1, result)
        }

        for ((index, element) in this.checkCubeMode3x3.withIndex()) {
            val result = this.numberMasterCheckSuccess.checkAll(element, 2, 1)
            assertEquals(index + 1, result)
        }
    }

    private fun checkMagicSquareUnitTest() {
        this.numberMasterCheckSuccess.useCubeMode = 0
        this.numberMasterCheckSuccess.sizeMax = 2

        for (checkMagicNumber in checkMagicNumbers3x3) {
            assertTrue(this.numberMasterCheckSuccess.checkMagicSquare(checkMagicNumber))
        }
    }
}