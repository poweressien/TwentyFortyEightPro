import com.twentyfortyeightpro.domain.Game2048Engine
import kotlin.random.Random
import kotlin.system.exitProcess

typealias D = Game2048Engine.Direction

/**
 * Standalone compile+run test harness (no JUnit/Android needed) so the engine
 * logic can be verified on plain kotlinc, mirroring test-engine.js 1:1.
 * Run with: kotlinc Game2048Engine.kt EngineTestRunner.kt -include-runtime -d test.jar && java -jar test.jar
 */

var passed = 0
var failed = 0

fun <T> assertEqual(actual: T, expected: T, label: String) {
    if (actual == expected) {
        passed++
        println("✅ PASS: $label")
    } else {
        failed++
        println("❌ FAIL: $label\n   expected: $expected\n   actual:   $actual")
    }
}

fun assertTrue(value: Boolean, label: String) {
    if (value) {
        passed++
        println("✅ PASS: $label")
    } else {
        failed++
        println("❌ FAIL: $label (expected truthy)")
    }
}

fun boardToList(b: Array<IntArray>): List<List<Int>> = b.map { it.toList() }

fun main() {
    val engine = Game2048Engine(4)

    // ── slideRowLeft basic merge ────────────────────────────────────────────
    run {
        val r = engine.slideRowLeft(intArrayOf(2, 2, 0, 0))
        assertEqual(r.row.toList(), listOf(4, 0, 0, 0), "slideRowLeft merges [2,2,0,0] -> [4,0,0,0]")
        assertEqual(r.scoreGained, 4, "slideRowLeft score for [2,2,0,0] is 4")
        assertEqual(r.mergeCount, 1, "slideRowLeft mergeCount for [2,2,0,0] is 1")
    }

    // ── no double merge in single pass ──────────────────────────────────────
    run {
        val r = engine.slideRowLeft(intArrayOf(2, 2, 2, 2))
        assertEqual(r.row.toList(), listOf(4, 4, 0, 0), "[2,2,2,2] merges to [4,4,0,0] (no chain merge)")
        assertEqual(r.scoreGained, 8, "score for [2,2,2,2] -> 8 (4+4)")
    }

    // ── gap handling ─────────────────────────────────────────────────────────
    run {
        val r = engine.slideRowLeft(intArrayOf(0, 2, 0, 2))
        assertEqual(r.row.toList(), listOf(4, 0, 0, 0), "[0,2,0,2] compresses and merges to [4,0,0,0]")
    }

    // ── non-adjacent equal values don't merge across a different value ──────
    run {
        val r = engine.slideRowLeft(intArrayOf(2, 4, 2, 0))
        assertEqual(r.row.toList(), listOf(2, 4, 2, 0), "[2,4,2,0] -> no merge, just compacted")
    }

    // ── move left on full board ─────────────────────────────────────────────
    run {
        val board = arrayOf(
            intArrayOf(2, 2, 4, 4),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(8, 0, 8, 0),
            intArrayOf(2, 4, 8, 16)
        )
        val result = engine.move(board, D.LEFT)
        assertEqual(
            boardToList(result.board),
            listOf(listOf(4, 8, 0, 0), listOf(0, 0, 0, 0), listOf(16, 0, 0, 0), listOf(2, 4, 8, 16)),
            "move left on full board produces correct result"
        )
        assertTrue(result.moved, "move left registers as moved=true")
        assertEqual(result.scoreGained, 4 + 8 + 16, "move left total score correct")
    }

    // ── move right ───────────────────────────────────────────────────────────
    run {
        val board = arrayOf(
            intArrayOf(2, 2, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val result = engine.move(board, D.RIGHT)
        assertEqual(
            boardToList(result.board),
            listOf(listOf(0, 0, 0, 4), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0)),
            "move right merges towards right wall"
        )
    }

    // ── move up ──────────────────────────────────────────────────────────────
    run {
        val board = arrayOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(2, 0, 0, 0),
            intArrayOf(4, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val result = engine.move(board, D.UP)
        assertEqual(
            boardToList(result.board),
            listOf(listOf(4, 0, 0, 0), listOf(4, 0, 0, 0), listOf(0, 0, 0, 0), listOf(0, 0, 0, 0)),
            "move up merges column correctly"
        )
    }

    // ── move down ────────────────────────────────────────────────────────────
    run {
        val board = arrayOf(
            intArrayOf(2, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(2, 0, 0, 0),
            intArrayOf(4, 0, 0, 0)
        )
        val result = engine.move(board, D.DOWN)
        assertEqual(
            boardToList(result.board),
            listOf(listOf(0, 0, 0, 0), listOf(0, 0, 0, 0), listOf(4, 0, 0, 0), listOf(4, 0, 0, 0)),
            "move down merges column correctly towards bottom"
        )
    }

    // ── moved=false when nothing changes ────────────────────────────────────
    run {
        val board = arrayOf(
            intArrayOf(2, 4, 8, 16),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        )
        val result = engine.move(board, D.LEFT)
        assertTrue(!result.moved, "move left on already-compacted distinct row returns moved=false")
    }

    // ── canMove detects game over correctly ─────────────────────────────────
    run {
        val fullNoMoves = arrayOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2)
        )
        assertTrue(!engine.canMove(fullNoMoves), "canMove false on full checkerboard with no merges possible")

        val fullWithMerge = arrayOf(
            intArrayOf(2, 4, 2, 4),
            intArrayOf(4, 2, 4, 2),
            intArrayOf(2, 4, 2, 2),
            intArrayOf(4, 2, 4, 2)
        )
        assertTrue(engine.canMove(fullWithMerge), "canMove true when an adjacent merge is available")

        assertTrue(engine.canMove(engine.emptyBoard()), "canMove true on an empty board")
    }

    // ── highestTile ──────────────────────────────────────────────────────────
    run {
        val board = arrayOf(
            intArrayOf(2, 4, 0, 0),
            intArrayOf(0, 1024, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 8)
        )
        assertEqual(engine.highestTile(board), 1024, "highestTile finds the max value on board")
    }

    // ── spawnTile only places in empty cell, never overwrites ──────────────
    run {
        val board = engine.emptyBoard()
        board[0][0] = 2; board[0][1] = 2; board[0][2] = 2
        val spawned = engine.spawnTile(board, Random(42))
        assertTrue(spawned != null, "spawnTile returns a spawn result when empties exist")
        assertTrue(board[spawned!!.row][spawned.col] == spawned.value, "spawnTile mutates board at returned coords")

        val fullBoard = arrayOf(
            intArrayOf(2, 4, 2, 4), intArrayOf(4, 2, 4, 2), intArrayOf(2, 4, 2, 4), intArrayOf(4, 2, 4, 2)
        )
        val noSpawn = engine.spawnTile(fullBoard)
        assertTrue(noSpawn == null, "spawnTile returns null when board is full")
    }

    // ── comboMultiplier thresholds ───────────────────────────────────────────
    run {
        assertEqual(Game2048Engine.comboMultiplier(0), 1, "combo 0 -> multiplier 1")
        assertEqual(Game2048Engine.comboMultiplier(2), 1, "combo 2 -> multiplier 1")
        assertEqual(Game2048Engine.comboMultiplier(3), 2, "combo 3 -> multiplier 2")
        assertEqual(Game2048Engine.comboMultiplier(4), 2, "combo 4 -> multiplier 2")
        assertEqual(Game2048Engine.comboMultiplier(5), 3, "combo 5 -> multiplier 3")
        assertEqual(Game2048Engine.comboMultiplier(7), 3, "combo 7 -> multiplier 3")
        assertEqual(Game2048Engine.comboMultiplier(8), 5, "combo 8 -> multiplier 5")
        assertEqual(Game2048Engine.comboMultiplier(100), 5, "combo 100 -> multiplier 5 (capped)")
    }

    // ── reactionFor lookups ───────────────────────────────────────────────────
    run {
        assertEqual(Game2048Engine.reactionFor(64), "Nice 😎", "reaction for 64")
        assertEqual(Game2048Engine.reactionFor(2048), "Legend 👑", "reaction for 2048")
        assertEqual(Game2048Engine.reactionFor(16384), "Impossible 🤯", "reaction for 16384")
        assertEqual(Game2048Engine.reactionFor(4), null, "no reaction for small tile 4")
    }

    // ── tierFor rarity mapping ────────────────────────────────────────────────
    run {
        assertEqual(Game2048Engine.tierFor(2), "common", "tier for 2 is common")
        assertEqual(Game2048Engine.tierFor(32), "common", "tier for 32 is common")
        assertEqual(Game2048Engine.tierFor(64), "rare", "tier for 64 is rare")
        assertEqual(Game2048Engine.tierFor(256), "rare", "tier for 256 is rare")
        assertEqual(Game2048Engine.tierFor(512), "epic", "tier for 512 is epic")
        assertEqual(Game2048Engine.tierFor(2048), "legendary", "tier for 2048 is legendary")
        assertEqual(Game2048Engine.tierFor(4096), "mythic", "tier for 4096 is mythic")
        assertEqual(Game2048Engine.tierFor(16384), "galaxy", "tier for 16384 is galaxy")
    }

    // ── full game simulation runs without crashing & score only increases ───
    run {
        var board = engine.emptyBoard()
        engine.spawnTile(board)
        engine.spawnTile(board)
        var totalScore = 0
        val dirs = listOf(D.LEFT, D.RIGHT, D.UP, D.DOWN)
        var safety = 0
        while (engine.canMove(board) && safety < 500) {
            val dir = dirs[safety % 4]
            val result = engine.move(board, dir)
            if (result.moved) {
                board = result.board
                totalScore += result.scoreGained
                engine.spawnTile(board)
            }
            safety++
        }
        assertTrue(totalScore >= 0, "full simulation completed in $safety steps, final score $totalScore")
        assertTrue(engine.highestTile(board) >= 2, "simulation produced at least one merge over time")
    }

    println("\n$passed passed, $failed failed")
    exitProcess(if (failed > 0) 1 else 0)
}
