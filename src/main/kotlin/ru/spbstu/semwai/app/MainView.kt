package ru.spbstu.semwai.app

import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import ru.spbstu.semwai.model.CellValue
import ru.spbstu.semwai.model.Sapper
import tornadofx.*


class App : Application() {
    private val width = 30
    private val height = 15
    private val model = Sapper(width, height)
    private val cells = mutableListOf<MutableList<Button>>()
    private val root = VBox()

    private val stylebgr0 = "-fx-background-radius: 0px;"
    private val stylebgcRed = "-fx-background-color: #FF0000;"
    private val stylebgcGray = "-fx-background-color: #AAAAAA;"
    private val styleFontSize18 = "-fx-font-size: 18;"
    fun create() {
        for (i in 0 until height) {
            val box = HBox()
            root.children.add(box)
            cells.add(mutableListOf())
            for (j in 0 until width) {
                val btn = Button()
                btn.prefWidth = 40.0
                btn.prefHeight = 40.0
                //btn.font = Font.font(18.0)
                btn.style = stylebgr0 + styleFontSize18
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

    fun update() {
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
                    else -> cell.value.value.toString()
                }
            }
        }
    }

    override fun start(primaryStage: Stage) {
        model.newGame()
        create()
        val scene = Scene(root, 39.0 * width, 39.0 * height + 40)
        root.add(VBox().apply {
            add(Button("Сначала").apply {
                style += styleFontSize18
                setOnAction {
                    model.newGame()
                    cells.forEach { it.forEach { btn -> btn.style = stylebgr0 } }
                    update()
                }
            })
        })
        primaryStage.title = "Hello World!"
        primaryStage.isResizable = false
        primaryStage.scene = scene
        primaryStage.show()

    }

}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}