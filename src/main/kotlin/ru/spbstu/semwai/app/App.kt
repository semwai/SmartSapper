package ru.spbstu.semwai.app

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.spbstu.semwai.model.CellValue
import ru.spbstu.semwai.model.MsgType
import ru.spbstu.semwai.model.Sapper
import tornadofx.*


const val stylebgr0 = "-fx-background-radius: 0px;"
const val stylebgcRed = "-fx-background-color: #FF0000;"
const val stylebgcGray = "-fx-background-color: #AAAAAA;"
const val styleFontSize18 = "-fx-font-size: 18;"
const val stylebgcGreen = "-fx-background-color: #00FF00;"

abstract class App(private val width: Int = 6, private val height: Int = 5) : Application() {


    val cells = mutableListOf<MutableList<Button>>()
    val root = VBox()

    abstract val message: (MsgType) -> Unit

    val model: Sapper by lazy { Sapper(width, height, message) }

    private fun create() {
        for (i in 0 until height) {
            val box = HBox()
            root.children.add(box)
            cells.add(mutableListOf())
            for (j in 0 until width) {
                val btn = Button()
                btn.prefWidth = 40.0
                btn.prefHeight = 40.0
                btn.style = stylebgr0 //+ styleFontSize18
                btn.setOnMouseClicked {
                    if (it.button == MouseButton.PRIMARY) {
                        model.click(j, i)
                    } else {
                        model.mark(j, i)
                    }
                    update()
                }
                cells[i].add(btn)
                box.add(btn)
            }
        }
        update()
    }

    private fun update() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                val cell = model.getCell(j, i)

                if (cell.marked) {
                    cells[i][j].text = "X"
                    continue
                }
                if (!cell.isOpen) {
                    cells[i][j].text = " "
                    continue
                }
                if (cell.value == CellValue.Null)
                    cells[i][j].style = stylebgcGray + stylebgr0
                if (cell.value == CellValue.Bomb)
                    cells[i][j].style = stylebgcRed + stylebgr0
                cells[i][j].text = when (cell.value) {
                    CellValue.Bomb -> "*"
                    CellValue.Null -> " "
                    CellValue.Undefined -> " "
                    else -> cell.value.value.toString()
                }
            }
        }
    }

    override fun start(primaryStage: Stage) {
        model.newGame()
        create()
        val scene = Scene(root, 39.0 * width, 39.0 * height + 40)

        with(primaryStage) {
            title = "Умный сапер (c) github.com/semwai/SmartSapper"
            isResizable = false
            this.scene = scene
            show()
        }
    }

    fun newGame() {
        println("\n\n\nNEW GAME\n\n")
        model.newGame()
        cells.forEach { it.forEach { btn -> btn.style = stylebgr0 } }
        update()
    }
}


fun Button.click(button: MouseButton) {
    this.fireEvent(MouseEvent(
            MouseEvent.MOUSE_CLICKED, 1.0, 1.0, 1.0, 1.0, button,
            1, true, true, true, true,
            true, true, true, true,
            true, true, null))
}
