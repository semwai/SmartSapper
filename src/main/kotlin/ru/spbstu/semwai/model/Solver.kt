package ru.spbstu.semwai.model

import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import ru.spbstu.semwai.app.click
import java.util.*

class Solver(private val model: Sapper, private val buttons: MutableList<MutableList<Button>>) {

    private val r = Random()

    fun nextStep() {
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

        buttons[r.nextInt(model.height)][r.nextInt(model.width)].click(MouseButton.PRIMARY)
    }
}