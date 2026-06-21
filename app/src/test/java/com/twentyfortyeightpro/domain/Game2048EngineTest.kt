package com.twentyfortyeightpro.domain

import org.junit.Test
import org.junit.Assert.*
import kotlin.random.Random

/**
 * JUnit port of the standalone EngineTestRunner verification — same 43 cases,
 * runs as a normal local unit test (`./gradlew testDebugUnitTest`), no
 * Android device/emulator needed since Game2048Engine has zero Android deps.
 */
class Game2048EngineTest {

    private val engine = Game2048Engine(4)
    private val D = Game2048Engine.Direction

    private fun boardOf(vararg rows: IntArray) = arrayOf(*rows)
    private fun boardToList(b: Array<IntArray>) = b.map { it.toList() }

    @Test
    fun `slideRowLeft merges adjacent equal pair`() {
        val r = engine.slideRowLeft(intArrayOf(2, 2, 0, 0))
        assertEquals(listOf(4, 0, 0, 0), r.row.toList())
        assertEquals(4, r.scoreGained)
        assertEquals(1, r.mergeCount)
    }

    @Test
    fun `no double merge in a single pass`() {
        val r = engine.slideRowLeft(intArrayOf(2, 2, 2, 2))
        assertEquals(listOf(4, 4, 0, 0), r.row.toList())
        assertEquals(8, r.scoreGained)
    }

    @Test
    fun `gaps are compacted before merging`() {
        val r = engine.slideRowLeft(intArrayOf(0, 2, 0, 2))
        assertEquals(listOf(4, 0, 0, 0), r.row.toList())
    }

    @Test
    fun `non-adjacent equal values separated by a different value do not merge`() {
        val r = engine.slideRowLeft(intArrayOf(2, 4, 2, 0))
        assertEquals(listOf(2, 4, 2, 0), r.row.toList())
    }

    @Test
    fun `move left on a full board produces correct result`() {
        val board = boardOf(
            intArrayOf(2, 2, 4, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(8, 0, 8, 0),
            intArrayOf(2, 4, 8, 16)
        )
        val result = engine.move(board, D.LEFT)
        assertEquals(
            listOf(listOf(4, 8, 0, 0), listOf(0, 0, 0, 0), listOf(16, 0, 0, 0), listOf(2, 4, 8, 16)),
            boardToList(result.board)
        )
        assertTrue(result.moved)
        assertEquals(4 + 8 + 16, result.scoreGained)
    }

    @Test
    fun `move right merges towards the right wall`() {
        val board = boardOf(
            intArrayOf(2, 2, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val result = engine.move(board, D.RIGHT)
        assertEquals(listOf(0, 0, 0, 4), result.board[0].toList())
    }

    @Test
    fun `move up merges a column correctly`() {
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(2, 0, 0, 0),
            intArrayOf(4, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val result = engine.move(board, D.UP)
        assertEquals(4, result.board[0][0])
        assertEquals(4, result.board[1][0])
        assertEquals(0, result.board[2][0])
    }

    @Test
    fun `move down merges a column towards the bottom`() {
        val board = boardOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 0, 0, 0),
            intArrayOf(4, 0, 0, 0)
        )
        val result = engine.move(board, D.DOWN)
        assertEquals(4, result.board[2][0])
        assertEquals(4, result.board[3][0])
    }

    @Test
    fun `moved is false when nothing changes`() {
        val board = boardOf(
            intArrayOf(2, 4, 8, 16),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val result = engine.move(board, D.LEFT)
        assertFalse(result.moved)
    }

    @Test
    fun `canMove is false on a full checkerboard with no merges possible`() {
        val board = boardOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2)
        )
        assertFalse(engine.canMove(board))
    }

    @Test
    fun `canMove is true when an adjacent merge is available`() {
        val board = boardOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 2),
            intArrayOf(4, 2, 4, 2)
        )
        assertTrue(engine.canMove(board))
    }

    @Test
    fun `canMove is true on an empty board`() {
        assertTrue(engine.canMove(engine.emptyBoard()))
    }

    @Test
    fun `highestTile finds the max value on the board`() {
        val board = boardOf(
            intArrayOf(2, 4, 0, 0),
            intArrayOf(0, 1024, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 8)
        )
        assertEquals(1024, engine.highestTile(board))
    }

    @Test
    fun `spawnTile mutates the board at an empty cell and never overwrites`() {
        val board = engine.emptyBoard()
        board[0][0] = 2; board[0][1] = 2; board[0][2] = 2
        val spawned = engine.spawnTile(board, Random(42))
        assertNotNull(spawned)
        assertEquals(spawned!!.value, board[spawned.row][spawned.col])

        val fullBoard = boardOf(
            intArrayOf(2, 4, 2, 4), intArrayOf(4, 2, 4, 2), intArrayOf(2, 4, 2, 4), intArrayOf(4, 2, 4, 2)
        )
        assertNull(engine.spawnTile(fullBoard))
    }

    @Test
    fun `comboMultiplier thresholds match the web client exactly`() {
        assertEquals(1, Game2048Engine.comboMultiplier(0))
        assertEquals(1, Game2048Engine.comboMultiplier(2))
        assertEquals(2, Game2048Engine.comboMultiplier(3))
        assertEquals(2, Game2048Engine.comboMultiplier(4))
        assertEquals(3, Game2048Engine.comboMultiplier(5))
        assertEquals(3, Game2048Engine.comboMultiplier(7))
        assertEquals(5, Game2048Engine.comboMultiplier(8))
        assertEquals(5, Game2048Engine.comboMultiplier(100))
    }

    @Test
    fun `reactionFor lookups match the web client exactly`() {
        assertEquals("Nice 😎", Game2048Engine.reactionFor(64))
        assertEquals("Legend 👑", Game2048Engine.reactionFor(2048))
        assertEquals("Impossible 🤯", Game2048Engine.reactionFor(16384))
        assertNull(Game2048Engine.reactionFor(4))
    }

    @Test
    fun `tierFor rarity mapping matches the web client exactly`() {
        assertEquals("common", Game2048Engine.tierFor(2))
        assertEquals("rare", Game2048Engine.tierFor(64))
        assertEquals("epic", Game2048Engine.tierFor(512))
        assertEquals("legendary", Game2048Engine.tierFor(2048))
        assertEquals("mythic", Game2048Engine.tierFor(4096))
        assertEquals("galaxy", Game2048Engine.tierFor(16384))
    }

    @Test
    fun `full game simulation runs to completion without crashing`() {
        var board = engine.emptyBoard()
        engine.spawnTile(board)
        engine.spawnTile(board)
        var totalScore = 0
        val dirs = listOf(D.LEFT, D.RIGHT, D.UP, D.DOWN)
        var safety = 0
        while (engine.canMove(board) && safety < 500) {
            val result = engine.move(board, dirs[safety % 4])
            if (result.moved) {
                board = result.board
                totalScore += result.scoreGained
                engine.spawnTile(board)
            }
            safety++
        }
        assertTrue(totalScore >= 0)
        assertTrue(engine.highestTile(board) >= 2)
    }
}
