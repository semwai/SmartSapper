package ru.spbstu.semwai.model

import java.lang.Exception
import java.util.*

enum class CellValue(var value: Int) {
    Bomb(100),
    Null(0),
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8),
    Undefined(1000)
}

data class Cell(var value: CellValue) {
    var isOpen = false
    var marked = false

    /*operator fun dec():Cell {
        val newValue = when (value) {
            //CellValue.One -> CellValue.Null
            CellValue.Two -> CellValue.One
            CellValue.Three -> CellValue.Two
            CellValue.Four -> CellValue.Three
            CellValue.Five -> CellValue.Four
            CellValue.Six -> CellValue.Five
            CellValue.Seven -> CellValue.Six
            CellValue.Eight -> CellValue.Seven
            else -> value
        }
        val newInstance = Cell(newValue)
        newInstance.isOpen = this.isOpen
        newInstance.marked = this.marked
        return newInstance
    }*/
}

val aroundOffset = listOf(
        Pair(-1, -1), Pair(0, -1), Pair(1, -1),
        Pair(-1, 0), Pair(1, 0),
        Pair(-1, 1), Pair(0, 1), Pair(1, 1))

class Sapper(val width: Int, val height: Int, val loseHandler: (MsgType) -> Unit) {
    private var map = MutableList(width * height) { Cell(CellValue.Null) }

    private var counter = 0

    var gameOver = false

    private fun checkArg(x: Int, y: Int) {
        if (x >= width || y >= height || x < 0 || y < 0)
            throw IllegalArgumentException("width is $width, height is $height, but x=$x, y=$y")
    }

    fun newGame() {
        counter = 0
        gameOver = false
        map = MutableList(width * height) { Cell(CellValue.Null) }
        val r = Random()
        for (i in 1..(width * height / 13)) {
            map[r.nextInt(width * height - 1)] = Cell(CellValue.Bomb)
        }
        for (i in 0 until height) {
            for (j in 0 until width) {
                calculate(j, i)
            }
        }

    }

    private fun calculate(x: Int, y: Int) {
        var c = 0
        aroundOffset.forEach {
            try {
                if (getRealCell(x + it.first, y + it.second).value == CellValue.Bomb)
                    c++
            } catch (e: Exception) {
            }
        }
        if (getRealCell(x, y).value != CellValue.Bomb) {
            map[y * width + x] = Cell(CellValue.values()[c + 1])
        }
    }

    private fun getRealCell(x: Int, y: Int): Cell {
        checkArg(x, y)
        return map[y * width + x]
    }

    /*То, что получает игрок, а именно для неоткрытой клетки выдаем ему клетку с типом "неизвестно",
    чтобы нельзя было обмануть игру и узнать координаты всех мин*/
    fun getCell(x: Int, y: Int): Cell {
        checkArg(x, y)
        val c = map[y * width + x]
        return when (c.isOpen) {
            true -> c
            false -> c.copy(value = CellValue.Undefined).apply { marked = c.marked }
        }

    }

    fun click(x: Int, y: Int) {
        if (gameOver) return
        checkArg(x, y)
        if (map[y * width + x].isOpen || map[y * width + x].marked) return
        counter++
        if (map[y * width + x].value == CellValue.Bomb) {
            gameOver()
        }
        if (map.filter { it.value == CellValue.Bomb }.all { it.marked })
            gameWin()

        openEmpty(x, y)
    }

    fun mark(x: Int, y: Int) {
        if (gameOver) return
        checkArg(x, y)
        map[y * width + x].marked = !map[y * width + x].marked
        //Если помечены только все бомбы
        if (map.filter { it.value == CellValue.Bomb }.all { it.marked } &&
                map.filter { it.value != CellValue.Bomb }.all { !it.marked })
            gameWin()
    }

    //Вынес за функцию чтобы не создавать массив для каждой клетки

    private fun openEmpty(x: Int, y: Int) {
        val directsForOpenEmpty = listOf(Pair(-1, 0), Pair(0, -1), Pair(1, 0), Pair(0, 1))
        if (x >= width || y >= height || x < 0 || y < 0 || gameOver) return
        map[y * width + x].isOpen = true
        directsForOpenEmpty.forEach {
            //обернул в try, т.к. в граничных клетках возможен вызов за карту.
            try {
                val cell = getRealCell(x, y)
                if ((!getRealCell(x + it.first, y + it.second).isOpen) && cell.value == CellValue.Null)
                    openEmpty(x + it.first, y + it.second)

            } catch (e: Exception) {
            }

        }

    }

    private fun gameOver() {
        gameOver = true
        map.forEach {
            it.isOpen = true
        }
        loseHandler(MsgType.LOSE)
    }

    private fun gameWin() {
        gameOver = true
        map.forEach {
            it.isOpen = true
        }
        loseHandler(MsgType.WIN)
    }

    fun getAllCells() = Array(height) { i -> Array(width) { j -> getCell(j, i) } }
}

enum class MsgType {
    WIN,
    LOSE
}
