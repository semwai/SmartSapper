package ru.spbstu.semwai.model

import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import ru.spbstu.semwai.app.click
import java.util.*


class SolverCollection(private val model: Sapper, input: Array<Array<Cell>>, private val mask: Array<IntArray>) {

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
            map.filter { it.value.value.value + mask[it.key.second][it.key.first] in 1..8 }
                    .minBy { cell ->
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
        println("Самая подходящая ячейка = $center")
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
        println(out.toTypedArray().joinToString(","))
        return out.toTypedArray()
    }

    //Просто генерируем координаты для следующего хода. Даем следующую неоткрытую и немаркированную ячейку
    fun nextUndefined(): Pair<Int, Int>? =
            map.filter {
                it.value.value == CellValue.Undefined &&
                        !it.value.marked
                       /*&& (it.value.value.value + mask[it.key.second][it.key.first] < 1)*/
            }.keys.firstOrNull()

}

class Solver(private val model: Sapper, private val buttons: MutableList<MutableList<Button>>) {

    private val mask = Array(model.width) { IntArray(model.height) { 0 } }
    private var first = true

    fun nextStep() {
        val map = model.getAllCells()
        log(map)
        if (first) {
            //когда мы не знаем куда идти или вообще нет подходящих кандидатов, мы идем в случайную точку.
            first = false
            val r = Random()
            val (x, y) = arrayOf(r.nextInt(model.width), r.nextInt(model.height))
            println("?Иду на $x $y\n\n")
            open(x, y)
        } else {
            val c = SolverCollection(model, map, mask)
            /*Координаты лучшей клетки, вокруг которой бомба. В идеальном случае её значение = 1 и вокруг нее всего одна
             пустая ячейка. В случае, если значение = 1, а клеток 2, придется выбрать случайно.
            */
            val candidate = c.bestMove() //массив из возможных ходов вокруг выбранной ячейки
            val bomb = candidate.firstOrNull() //если у нас только один кандидат, то это гарантированно бомба.
            if (bomb != null) {
                if (candidate.size > 4) { //однако вокруг 1 может быть 8 неоткрытых ячеек. Если их > 4,
                    //то просто идем, понимая что можем подорваться
                    println("!Иду на $bomb\n\n")
                    open(bomb.first, bomb.second)
                } else {
                    /*если вокруг лучшей ячейки мало возможных кандидатов
                    если мы учитываем, что рядом были раскрыты бомбы, то надо за каждое раскрытие отнимать 1 у соседних с бомбой
                    ячеек. Если рядом было раскрыта 1 бомба и значение текущей ячейки = 1, то 1-1 = 0 (в этой ячейке значение теперь
                    равно 0 и бомбы точно нет)*/
                    if (model.getCell(bomb.first, bomb.second).value.value + mask[bomb.second][bomb.first] < 1) {
                        println("Баллов для бомбы недостаточно - $bomb\n\n")
                        open(bomb.first, bomb.second)
                    } else {
                        /*Если все-таки у нас очень большое значение ячейки и даже количество
                        соседних раскрытых бомб меньше значения текущей ячейки*/
                        println("Помечаю бомбу на $bomb\n\n")
                        mark(bomb.first, bomb.second)
                    }
                }
            } else {
                //У нас не оказалось кандидата на бомбу. Просим следующую неизвестную ячейку.
                val next = c.nextUndefined()
                if (next == null) {
                    //Не оказалось вообще вариантов с подходящей неизвестной ячейкой (
                    first = true
                } else {
                    println("Иду на $next\n\n")
                    open(next.first, next.second)
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

    private fun open(x: Int, y: Int) {
        buttons[y][x].click(MouseButton.PRIMARY)
    }

    private fun mark(x: Int, y: Int) {
        buttons[y][x].click(MouseButton.SECONDARY)
        aroundOffset.forEach {
            val cx = x + it.first
            val cy = y + it.second
            if (cy in 0 until model.height && cx in 0 until model.width) {
                mask[cy][cx]--
            }
        }
    }

}