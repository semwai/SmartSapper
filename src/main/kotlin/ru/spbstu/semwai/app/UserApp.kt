package ru.spbstu.semwai.app

import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import ru.spbstu.semwai.model.MsgType
import tornadofx.*

class UserApp : App() {

    override val loseAction = { type: MsgType ->
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Сообщение"
        alert.headerText = ""
        alert.contentText = when (type) {
            MsgType.WIN -> "Ты победил"
            MsgType.LOSE -> "Ты проиграл"
        }
        alert.show()
    }

    override fun start(primaryStage: Stage) {
        super.start(primaryStage)
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
        primaryStage.title = "Не самый умный сапер (c) github.com/semwai/SmartSapper"
    }

}