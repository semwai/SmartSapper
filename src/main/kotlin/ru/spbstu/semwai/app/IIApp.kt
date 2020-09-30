package ru.spbstu.semwai.app


import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import javafx.util.Duration
import javafx.util.converter.NumberStringConverter
import ru.spbstu.semwai.model.MsgType
import ru.spbstu.semwai.model.Solver
import tornadofx.*
import java.util.*


class IIApp(width: Int, height: Int) : App(width, height) {

    lateinit var solver: Solver

    private val interval = SimpleDoubleProperty(0.5)
    private var totalWin = 0
    private var totalGame = 0
    private val scoreStr = SimpleStringProperty("Это первая игра")

    private val timer = Timeline(KeyFrame(Duration.seconds(0.5), object : EventHandler<ActionEvent?> {
        private var i = 1
        override fun handle(event: ActionEvent?) {
            solver.nextStep()
        }
    }))


    override val message = { msgType: MsgType ->
        timer.stop()
        Alert(Alert.AlertType.INFORMATION).show()

        if (msgType == MsgType.WIN)
            totalWin++
        totalGame++
        scoreStr.value = "$totalWin побед из $totalGame игр"

        newGame()
        timer.playFromStart()
    }


    override fun start(primaryStage: Stage) {
        super.start(primaryStage)
        root.add(Label().apply { textProperty().bind(scoreStr) })
        solver = Solver(model, cells)

        cells.forEach { it.forEach { btn -> btn.isDisable = true } }
        timer.cycleCount = Int.MAX_VALUE
        timer.play()

        with(primaryStage) {
            setOnCloseRequest {
                timer.stop()
            }
        }
    }
}

