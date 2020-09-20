package ru.spbstu.semwai.app


import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import javafx.util.Duration
import ru.spbstu.semwai.model.MsgType
import java.util.*


class IIApp : App() {

    val r = Random()
    val timeline = Timeline(KeyFrame(Duration.seconds(0.5), object : EventHandler<ActionEvent?> {
        private var i = 1
        override fun handle(event: ActionEvent?) {
            cells[r.nextInt(height)][r.nextInt(width)].click(MouseButton.PRIMARY)
            cells[r.nextInt(height)][r.nextInt(width)].style += stylebgcGreen
        }
    }))


    override val loseAction = { msgType: MsgType ->
        timeline.stop()
    }


    override fun start(primaryStage: Stage) {
        super.start(primaryStage)
        primaryStage.title = "Умный сапер (c) github.com/semwai/SmartSapper"
        root.isDisable = true

        timeline.cycleCount = 250
        timeline.play()


    }
}

fun Button.click(button: MouseButton) {
    this.fireEvent(MouseEvent(
            MouseEvent.MOUSE_CLICKED, 1.0, 1.0, 1.0, 1.0, button,
            1, true, true, true, true,
            true, true, true, true,
            true, true, null))
}