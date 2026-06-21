package com.twentyfortyeightpro.domain

import kotlin.random.Random

/**
 * Pure 2048 game engine. Zero Android dependencies so it's testable on the
 * plain JVM. Ported directly from the JS engine (static/js/game-engine.js)
 * used by the Django web version — same merge rules, same combo thresholds,
 * same rarity tiers, so behavior is identical across web and native clients.
 *
 * Board convention: size x size grid of Int, 0 = empty, board[row][col].
 */
class Game2048Engine(private val size: Int = 4) {

    fun emptyBoard(): Array<IntArray> = Array(size) { IntArray(size) }

    fun cloneBoard(board: Array<IntArray>): Array<IntArray> = Array(size) { r -> board[r].copyOf() }

    fun emptyCells(board: Array<IntArray>): List<Pair<Int, Int>> {
        val cells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (board[r][c] == 0) cells.add(r to c)
            }
        }
        return cells
    }

    data class SpawnResult(val row: Int, val col: Int, val value: Int)

    /** Spawns a random tile (90% -> 2, 10% -> 4) in an empty cell. Mutates board. */
    fun spawnTile(board: Array<IntArray>, random: Random = Random.Default): SpawnResult? {
        val empties = emptyCells(board)
        if (empties.isEmpty()) return null
        val (r, c) = empties[random.nextInt(empties.size)]
        val value = if (random.nextDouble() < 0.9) 2 else 4
        board[r][c] = value
        return SpawnResult(r, c, value)
    }

    data class SlideResult(val row: IntArray, val scoreGained: Int, val mergeCount: Int, val mergedValues: List<Int>)

    /** Slide + merge a single row to the LEFT. */
    fun slideRowLeft(row: IntArray): SlideResult {
        val compact = row.filter { it != 0 }
        val merged = mutableListOf<Int>()
        var scoreGained = 0
        var mergeCount = 0
        val mergedValues = mutableListOf<Int>()

        var i = 0
        while (i < compact.size) {
            if (i + 1 < compact.size && compact[i] == compact[i + 1]) {
                val newVal = compact[i] * 2
                merged.add(newVal)
                scoreGained += newVal
                mergeCount += 1
                mergedValues.add(newVal)
                i += 2
            } else {
                merged.add(compact[i])
                i += 1
            }
        }
        while (merged.size < row.size) merged.add(0)

        return SlideResult(merged.toIntArray(), scoreGained, mergeCount, mergedValues)
    }

    fun transpose(board: Array<IntArray>): Array<IntArray> {
        val t = emptyBoard()
        for (r in 0 until size) for (c in 0 until size) t[c][r] = board[r][c]
        return t
    }

    fun reverseRows(board: Array<IntArray>): Array<IntArray> =
        Array(size) { r -> board[r].reversedArray() }

    enum class Direction { LEFT, RIGHT, UP, DOWN }

    data class MoveResult(
        val board: Array<IntArray>,
        val moved: Boolean,
        val scoreGained: Int,
        val mergeCount: Int,
        val mergedValues: List<Int>
    )

    /** Executes a move. Does NOT mutate input board. Does NOT spawn a new tile. */
    fun move(board: Array<IntArray>, direction: Direction): MoveResult {
        var working = cloneBoard(board)
        var scoreGained = 0
        var mergeCount = 0
        val mergedValues = mutableListOf<Int>()

        fun processRows(b: Array<IntArray>): Array<IntArray> {
            return Array(size) { r ->
                val result = slideRowLeft(b[r])
                scoreGained += result.scoreGained
                mergeCount += result.mergeCount
                mergedValues.addAll(result.mergedValues)
                result.row
            }
        }

        working = when (direction) {
            Direction.LEFT -> processRows(working)
            Direction.RIGHT -> reverseRows(processRows(reverseRows(working)))
            Direction.UP -> transpose(processRows(transpose(working)))
            Direction.DOWN -> transpose(reverseRows(processRows(reverseRows(transpose(working)))))
        }

        val moved = !boardsEqual(board, working)
        return MoveResult(working, moved, scoreGained, mergeCount, mergedValues)
    }

    fun boardsEqual(a: Array<IntArray>, b: Array<IntArray>): Boolean {
        for (r in 0 until size) for (c in 0 until size) if (a[r][c] != b[r][c]) return false
        return true
    }

    fun highestTile(board: Array<IntArray>): Int {
        var max = 0
        for (row in board) for (v in row) if (v > max) max = v
        return max
    }

    /** True if there's an empty cell OR any adjacent pair (horiz/vert) is equal. */
    fun canMove(board: Array<IntArray>): Boolean {
        if (emptyCells(board).isNotEmpty()) return true
        for (r in 0 until size) {
            for (c in 0 until size) {
                val v = board[r][c]
                if (c + 1 < size && board[r][c + 1] == v) return true
                if (r + 1 < size && board[r + 1][c] == v) return true
            }
        }
        return false
    }

    companion object {
        /** Combo multiplier given the current consecutive-merge streak count. */
        fun comboMultiplier(comboCount: Int): Int = when {
            comboCount >= 8 -> 5
            comboCount >= 5 -> 3
            comboCount >= 3 -> 2
            else -> 1
        }

        /** Reaction text lookup for a merged tile value. */
        fun reactionFor(value: Int): String? = when (value) {
            64 -> "Nice 😎"
            128 -> "Brooo 😹💥"
            256 -> "Madd 🥶🚀"
            512 -> "Insane 🔥"
            1024 -> "Crazy 😳"
            2048 -> "Legend 👑"
            4096 -> "Monster 😈"
            8192 -> "God Mode ⚡"
            16384 -> "Impossible 🤯"
            else -> null
        }

        /** Rarity tier for a tile value -> drives visual styling (matches Achievement.rarity). */
        fun tierFor(value: Int): String = when {
            value >= 16384 -> "galaxy"
            value >= 4096 -> "mythic"
            value >= 2048 -> "legendary"
            value >= 512 -> "epic"
            value >= 64 -> "rare"
            else -> "common"
        }
    }
}
