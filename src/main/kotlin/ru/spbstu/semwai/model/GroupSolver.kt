package ru.spbstu.semwai.model

import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import ru.spbstu.semwai.app.click
import java.util.*

typealias Coord = Pair<Int, Int>

//count - количество оставшихся попыток маркирования бомбы
class GroupSolverCollection(private val model: Sapper, input: Array<Array<Cell>>, private val mask: Array<IntArray>, private val count: Int) {

    //Координата ячейки, её значение, undefined?, marked?
    private val map = mutableMapOf<Coord, Pair<Int, Boolean>>()

    private val groups = mutableMapOf<List<Coord>, Int>()

    init {
        //добавляем только ячейки с известным значением и те, которые мы до этого пометили
        input.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                if (cell.value != CellValue.Undefined)
                    map[j to i] = Pair(cell.value.value, cell.marked)
            }
        }
        //Образовываем группы
        map.forEach { (xy, vm) ->
            /*находим вокруг ячейки координаты неизвестных и немаркированных ячеек, а затем присваиваем этому списку значение
            ячейки по центру (образовываем группу), но только если значение этой ячейки с учетом маски(рядом разгаданных бомб) > 0*/
            val value = vm.first + mask[xy.second][xy.first]
            if (value >= 0)
                groups[
                        aroundOffset.map {
                            //находим соседние ячейки (размножаем её координату на 8 координат вокруг прибавляя каждый раз разные смещения)
                            it.first + xy.first to it.second + xy.second
                        }.filter {
                            //важно чтобы ячейка не выходила за карту и была неизвестной
                            it.first in 0 until model.width && it.second in 0 until model.height
                                    && model.getCell(it.first, it.second).value == CellValue.Undefined
                                    && !model.getCell(it.first, it.second).marked
                        }] = value
        }
    }

    fun bombs(): List<Coord> {
        val out = mutableListOf<Coord>()
        groups.filter { it.key.size == it.value }.forEach {
            it.key.forEach { coord ->
                if (!model.getCell(coord.first, coord.second).marked)
                    out.add(coord)
            }
        }
        return out
    }

    fun free(): List<Coord> {
        val out = mutableListOf<Coord>()
        groups.filter { it.value == 0 }.forEach {
            it.key.forEach { coord ->
                if (!model.getCell(coord.first, coord.second).marked)
                    out.add(coord)
            }
        }
        return out
    }

    private fun validateGroups() = groups.filter { it.value > 0 && it.key.isNotEmpty() }

    fun crossFree(): List<Coord> {
        val validatedGroups = validateGroups()
        val steps = mutableListOf<Coord>()
        validatedGroups.forEach { first ->
            validatedGroups.forEach { second ->
                if (first.key != second.key && (first.key - second.key).isEmpty() && first.value - second.value == 0) {
                    val free = second.key - first.key
                    steps.addAll(free)
                    println("first $first second $second free is $free")
                }
            }
        }
        return steps
    }

    fun crossBombs(): List<Coord> {
        val validatedGroups = validateGroups()
        val steps = mutableListOf<Coord>()
        validatedGroups.forEach { first ->
            validatedGroups.forEach { second ->
                if (first.key != second.key && (first.key - second.key).size == first.value - second.value) {
                    val bombs = second.key - first.key
                    steps.addAll(bombs)
                    println("first $first second $second bombs is $bombs")
                }
            }
        }
        return steps
    }


    fun lastHope(): List<Coord> {
        val validatedGroups = validateGroups()
        validatedGroups.forEach { first ->
            validatedGroups.forEach { second ->
                return if (count == 1)
                    first.key.intersect(second.key).toList()
                else
                    first.key + second.key - first.key.intersect(second.key)
            }
        }
        return emptyList()
    }

    fun goRandom(): Coord {
        val r = Random()
        var x: Int
        var y: Int
        do {
            x = r.nextInt(model.width)
            y = r.nextInt(model.width)
        } while (model.getCell(x, y).value != CellValue.Undefined)
        return x to y
    }

    fun print() {
        println(groups.filter { it.value > 0 })
    }
}

class GroupSolver(private val model: Sapper, private val buttons: MutableList<MutableList<Button>>) {

    private val mask = Array(model.height) { IntArray(model.width) { 0 } }

    //сколько раз мы маркировали бомбу
    private var count = 0
    private var maxCount = model.bombCount()

    fun nextStep() {
        val map = model.getAllCells()
        log(map)
        val c = GroupSolverCollection(model, map, mask, maxCount - count)
        val free = c.free()
        val bombs = c.bombs()
        println("free - $free\nbombs - $bombs\n")
        free.forEach {
            open(it)
        }
        bombs.forEach {
            mark(it)
        }
        if (bombs.isEmpty() && free.isEmpty()) {
            val crossFree = c.crossFree()
            val crossBombs = c.crossBombs()
            crossFree.forEach {
                open(it)
            }
            crossBombs.forEach {
                mark(it)
            }
            if (crossFree.isEmpty() && crossBombs.isEmpty()) {
                c.print()
                println("Осталось ${maxCount - count} бомб")
                val lastHope = c.lastHope()
                lastHope.forEach {
                    //mark(it)
                }
                open(c.goRandom())
                if (lastHope.isEmpty()) {

                }
            }
        }
    }

    private fun log(map: Array<Array<Cell>>) {
        print("   ")
        (0 until model.width).forEach { print(String.format("%3s", it).replace(' ', '-')) }
        println()
        map.forEachIndexed { j, row ->
            print(String.format("%3s", j))
            row.forEach {
                val v = when {
                    it.value == CellValue.Bomb || it.marked -> "*"
                    it.value == CellValue.Undefined -> "?"
                    it.value == CellValue.Null -> " "
                    else -> it.value.value.toString()
                }
                print(String.format("%3s", v))
            }
            println()
        }
        mask.forEach { row ->
            print("   ")
            row.forEach {
                print(String.format("%3s", it))
            }
            println()
        }
    }

    private fun open(c: Coord) {
        buttons[c.second][c.first].click(MouseButton.PRIMARY)
    }

    private fun mark(c: Coord) {
        if (model.getCell(c.first, c.second).marked)
            return
        buttons[c.second][c.first].click(MouseButton.SECONDARY)
        aroundOffset.forEach {
            val cx = c.first + it.first
            val cy = c.second + it.second
            if (cy in 0 until model.height && cx in 0 until model.width) {
                mask[cy][cx]--
            }
        }
        count++
    }

}