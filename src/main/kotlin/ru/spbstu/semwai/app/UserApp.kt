package ru.spbstu.semwai.app

import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.spbstu.semwai.model.MsgType
import tornadofx.*

class UserApp(width: Int, height: Int) : App(width, height) {

    override val message = { type: MsgType ->
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Сообщение"
        alert.headerText = ""
        alert.contentText = when (type) {
            MsgType.WIN -> "Ты победил"
            MsgType.LOSE -> "Ты проиграл"
            MsgType.FIRST_STEP_LOSE -> "Проигрыш при первом ходе не считается"
        }
        alert.show()
    }

    override fun start(primaryStage: Stage) {
        super.start(primaryStage)
        root.add(VBox().apply {
            add(Button("Сначала").apply {
                style += styleFontSize18
                setOnAction {
                    newGame()
                }
            })
        })
        primaryStage.title = "Не самый умный сапер (c) github.com/semwai/SmartSapper"
    }

}