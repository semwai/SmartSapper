package ru.spbstu.semwai.model

import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import ru.spbstu.semwai.app.click
import java.util.*

/*
data class Group(private val elements: Set<Pair<Int, Int>>, val value: Int, val center: Pair<Int, Int>) {
    /**
     * Если одна группа содержит другую, то вычитаем из большей меньшую.
     * То есть было две группы (5678,2) и (5,1), стало (678,1) и (5,1);
     * (2345,3) и (5,1) → (234,2) и (5,1)
     */
    operator fun minus(other: Group): Group {
        return when {
            other.elements.subtract(this.elements).isEmpty() -> {
                Group(this.elements.subtract(other.elements), this.value - other.value, this.center)
            }
            this.elements.subtract(other.elements).isEmpty() -> {
                Group(other.elements.subtract(this.elements), other.value - this.value, other.center)
            }
            else -> {
                this
            }
        }

    }
}

class GroupManager() {
    var elements = mutableSetOf<Group>()

    fun calc() {

    }

    fun next(): Pair<Int, Int> {
        return elements.firstOrNull { it.value == 1 }?.center ?: elements.first { it.value == 2 }.center
    }
}

class SapperCollection(private val model: Sapper) {
    private val grid: MutableMap<Pair<Int, Int>, Cell> = mutableMapOf()

    fun add(x: Int, y: Int, cell: Cell): Boolean {
        if (x < 0 || x > model.width - 1 || y < 0 || y > model.height - 1)
            return false

        grid.putIfAbsent(Pair(x, y), cell)
        return true
    }

    fun pushAllNew() {
        for (i in 0 until model.height)
            for (j in 0 until model.width) {
                val cell = model.getCell(j, i)
                if (cell.value.value > 0)
                    add(j, i, cell)
            }

    }

    fun isEmpty() = grid.isEmpty()

    fun iterator() = grid.iterator()
    //fun pop() = grid.pop()
}

class Solver(private val model: Sapper, private val buttons: MutableList<MutableList<Button>>) {


    //Хранит множество кандидатов на ход
    private val stack = SapperCollection(model)

    fun nextStep() {
        //если нам некуда идти
        if (stack.isEmpty()) {
            val r = Random()
            val (x, y) = arrayOf(r.nextInt(model.width), r.nextInt(model.height))
            open(x, y)
            println()
        }
        val groups = GroupManager()
        val iterator = stack.iterator()
        while (iterator.hasNext()) {
            val c = iterator.next()
            val coords = mutableSetOf<Pair<Int,Int>>()
            val x = c.key.first
            val y = c.key.second
            val cell = model.getCell(x,y)
            if (cell.value >= CellValue.One)
                for (i in -1..1)
                    for (j in -1..1) {
                        if (i == j) continue
                        val xx = c.key.first + j
                        val yy = c.key.second + i
                        if (model.getCell(xx, yy).value >= CellValue.One)
                            coords.add(Pair(xx,yy))
                    }
            groups.elements.add(Group(coords, cell.value.value,Pair(x,y)))
        }
        groups.calc()
        val (x,y) = groups.next()
        open(x,y)

    }

    private fun open(x: Int, y: Int) {
        buttons[y][x].click(MouseButton.PRIMARY)
        stack.pushAllNew()
    }

    private fun mark(x: Int, y: Int) = buttons[y][x].click(MouseButton.SECONDARY)

    private fun log() {
        println("---".repeat(model.width))
        for (i in 0 until model.height) {
            for (j in 0 until model.width) {
                val v = when (val value = model.getCell(j, i).value.value) {
                    -1 -> "*"
                    -2 -> "?"
                    0 -> " "
                    else -> value.toString()
                }
                print(String.format("%3s", v))
            }
            println()
        }
    }
}

 */

class SolverCollection(private val model: Sapper, input: Array<Array<Cell>>) {

    private val map = mutableMapOf<Pair<Int, Int>, Cell>()

    init {
        input.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                map[j to i] = cell
            }
        }
    }

    //лучший кандидат на выбор пустой клетки вокруг него
    private fun bestCandidate(): Pair<Int, Int>? =
            //map + class + enum = value.value.value....
            map.filter { it.value.value.value in 1..8 }.minBy { cell ->
                var c = 0 //количество пустых соседей у клетки
                aroundOffset.forEach {
                    val x = cell.key.first + it.first
                    val y = cell.key.second + it.second
                    if (x in 0 until model.width && y in 0 until model.height) {
                        if (model.getCell(x, y).value == CellValue.Undefined)
                            c++
                    }
                }
                c
            }?.key

    //лучшие возможные хода. Массив потому-что в некоторых случаях приходится случайно выбрать одну пустую клетку
    fun bestMove(): Array<Pair<Int, Int>> {
        val center = bestCandidate() ?: return arrayOf()
        val out = mutableListOf<Pair<Int, Int>>()
        aroundOffset.forEach {
            val x = center.first + it.first
            val y = center.second + it.second
            if (x in 0 until model.width && y in 0 until model.height) {
                val candidate = model.getCell(x, y)
                if ((candidate.value == CellValue.Undefined) && !candidate.marked)
                    out.add(x to y)
            }
        }
        return out.toTypedArray()
    }

    //Просто генерируем координаты для следующего хода. Даем следующую неоткрытую и немаркированную ячейку
    fun nextUndefined(): Pair<Int, Int> =
            map.filter { it.value.value == CellValue.Undefined && !it.value.marked }.keys.first()

}

class Solver(private val model: Sapper, private val buttons: MutableList<MutableList<Button>>) {

    private val mask = Array(model.width) {IntArray(model.height) {0} }

    fun nextStep() {
        val map = model.getAllCells()
        for (i in 0 until model.height)
            for (j in 0 until model.width)
                map[i][j]--
        log(map)
        if (map.all { it.all { c -> !c.isOpen } }) {
            val r = Random()
            val (x, y) = arrayOf(r.nextInt(model.width), r.nextInt(model.height))
            open(x, y)
        } else {
            val c = SolverCollection(model, map)
            /*Координаты лучшей клетки, вокруг которой бомба. В идеальном случае её значение = 1 и вокруг нее всего одна
             пустая ячейка. В случае, если значение = 1, а клеток 2, придется выбрать случайно.
            */
            val bombs = c.bestMove()
            val bomb = bombs.firstOrNull()
            if (bomb != null){
                if (bombs.size > 4) {
                    println("!Иду на $bomb")
                    open(bomb.first, bomb.second)
                    return
                }
                else
                    mark(bomb.first, bomb.second)
            }
            if (bombs.isEmpty()) {
                val next = c.nextUndefined()
                println("Иду на $next")
                open(next.first, next.second)
            }

        }

    }

    private fun log(map: Array<Array<Cell>>) {
        println("---".repeat(model.width))
        map.forEach { row ->
            row.forEach {
                val v = when (it.value) {
                    CellValue.Bomb -> "*"
                    CellValue.Undefined -> "?"
                    CellValue.Null -> " "
                    else -> it.value.value.toString()
                }
                print(String.format("%3s", v))
            }
            println()
        }
    }

    private fun open(x: Int, y: Int) {
        buttons[y][x].click(MouseButton.PRIMARY)
    }

    private fun mark(x: Int, y: Int){
        buttons[y][x].click(MouseButton.SECONDARY)
        aroundOffset.forEach {
            val cx = x + it.first
            val cy = y + it.second
            if (cy in 0 until model.height && cx in 0 until model.width){
                mask[cy][cx]--
            }
        }
    }

}