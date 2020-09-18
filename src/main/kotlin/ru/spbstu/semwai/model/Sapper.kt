package ru.spbstu.semwai.model

import java.lang.Exception
import java.util.*

enum class CellValue(var value: Int) {
    Bomb(-1),
    Null(0),
    One(1),
    Two(2),
    Three(3),
    Four(4),
    Five(5),
    Six(6),
    Seven(7),
    Eight(8)
}

class Cell(var value: CellValue) {
    var isOpen = false
    var marked = false
}

class Sapper(val width: Int, val height: Int) {
    private var map = MutableList(width * height) { Cell(CellValue.Null) }

    private var counter = 0

    var gameOver = false

    private fun checkArg(x: Int, y: Int) {
        if (x >= width || y >= height || x < 0 || y < 0)
            throw IllegalArgumentException("width is $width, height is $height, but x=$x, y=$y")
    }

    public fun newGame() {
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
        val direct = listOf(
                Pair(-1, -1), Pair(0, -1), Pair(1, -1),
                Pair(-1, 0), Pair(1, 0),
                Pair(-1, 1), Pair(0, 1), Pair(1, 1))
        var c = 0

        direct.forEach {
            try {
                if (getCell(x + it.first, y + it.second).value == CellValue.Bomb)
                    c++
            } catch (e: Exception) {
            }
        }

        if (getCell(x, y).value != CellValue.Bomb) {
            map[y * width + x] = Cell(CellValue.values()[c + 1])
        }
    }

    fun getCell(x: Int, y: Int): Cell {
        checkArg(x, y)
        return map[y * width + x]
    }

    fun click(x: Int, y: Int) {
        if (gameOver) return
        checkArg(x, y)
        if (map[y * width + x].isOpen) return

        if (map[y * width + x].value == CellValue.Bomb){
            closeGame()
        }
        map[y * width + x].isOpen = true
        openEmpty(x, y)
        counter++
    }

    fun mark(x: Int, y: Int) {
        if (gameOver) return
        checkArg(x, y)
        map[y * width + x].marked = !map[y * width + x].marked
    }

    fun openEmpty(x: Int, y: Int) {
        if (x >= width || y >= height || x < 0 || y < 0 || gameOver) return
        map[y * width + x].isOpen = true
        val direct = listOf(Pair(-1, 0), Pair(0, -1), Pair(1, 0), Pair(0, 1))
        direct.forEach {
            try {
                if (!getCell(x + it.first, y + it.second).isOpen && getCell(x, y).value == CellValue.Null)
                    openEmpty(x + it.first, y + it.second)
            } catch (e: Exception) {
            }
        }
    }

    private fun closeGame(){
        gameOver = true
        map.forEach {
            it.isOpen = true
        }
    }
}

