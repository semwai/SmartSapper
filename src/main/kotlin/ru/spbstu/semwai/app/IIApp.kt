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
import javafx.scene.control.ButtonType
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

class ScoreTable(private var Win: Int = 0, private var Lose: Int = 0) {
    fun lose() {
        Lose++
        updateStr()
    }

    fun win() {
        Win++
        updateStr()
    }

    private fun updateStr() {
        scoreStr.value = "$Win побед из ${Win + Lose} игр"
    }

    val scoreStr = SimpleStringProperty("Это первая игра")
}

class IIApp(width: Int, height: Int) : App(width, height) {

    val solver: Solver by lazy { Solver(model, cells) }

    private val score = ScoreTable()

    private val timer = Timeline(KeyFrame(Duration.seconds(1.0), object : EventHandler<ActionEvent?> {
        private var i = 1
        override fun handle(event: ActionEvent?) {
            solver.nextStep()
        }
    }))


    override val message = { msgType: MsgType ->
        when (msgType) {
            MsgType.WIN -> score.win()
            MsgType.LOSE -> score.lose()
        }
        with(Alert(Alert.AlertType.INFORMATION, "Haha", ButtonType.NEXT)) {
            timer.stop()
            show()
            setOnCloseRequest {
                newGame()
                timer.playFromStart()
            }
        }
    }


    override fun start(primaryStage: Stage) {
        super.start(primaryStage)
        root.add(Label().apply { textProperty().bind(score.scoreStr) })
        cells.forEach { it.forEach { btn -> btn.isDisable = true } }
        timer.cycleCount = Int.MAX_VALUE
        timer.play()
        primaryStage.setOnCloseRequest {
            timer.stop()
        }
    }
}

