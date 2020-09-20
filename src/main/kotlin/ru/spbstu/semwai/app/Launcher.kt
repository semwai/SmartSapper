package ru.spbstu.semwai.app

import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

import tornadofx.*

class Launcher : Application() {
    override fun start(primaryStage: Stage) {
        val root = StackPane()
        val scene = Scene(root, 250.0, 200.0)
        root.add(VBox().apply {
            padding = Insets(100.0,50.0,100.0,50.0)
            add(Button("Играть самому").apply {
                prefWidth = 150.0
                spacing = 15.0
                setOnAction {
                    UserApp().start(Stage())
                }
            })
            add(Button("Играть ИИ").apply {
                prefWidth = 150.0
                setOnAction {
                    IIApp().start(Stage())
                }
            })
        })
        primaryStage.title = "SmartSapper (c) github.com/semwai/SmartSapper"
        primaryStage.isResizable = false
        primaryStage.scene = scene
        primaryStage.show()
    }

}
