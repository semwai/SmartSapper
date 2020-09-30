package ru.spbstu.semwai.app

import javafx.application.Application
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

import tornadofx.*

class Launcher : Application() {

    private val widthProp = SimpleIntegerProperty(10)
    private val heightProp = SimpleIntegerProperty(10)

    override fun start(primaryStage: Stage) {
        val root = StackPane()
        val scene = Scene(root, 250.0, 200.0)
        root.add(VBox().apply {
            padding = Insets(100.0, 50.0, 100.0, 50.0)
            add(HBox().apply {
                add(label("Ширина").apply {
                    prefWidth = 75.0
                })
                add(TextField().apply {
                    bind(widthProp)
                    prefWidth = 75.0
                })
            })

            add(HBox().apply {
                add(label("Высота").apply {
                    prefWidth = 75.0
                })
                add(TextField().apply {
                    bind(heightProp)
                    prefWidth = 75.0
                })
            })

            add(Button("Играть самому").apply {
                prefWidth = 150.0
                spacing = 3.0
                setOnAction {
                    UserApp(widthProp.value, heightProp.value).start(Stage())
                }
            })
            add(Button("Играть ИИ").apply {
                prefWidth = 150.0
                setOnAction {
                    IIApp(widthProp.value, heightProp.value).start(Stage())
                }
            })
        })

        with(primaryStage) {
            title = "Умный сапер (c) github.com/semwai/SmartSapper"
            isResizable = false
            this.scene = scene
            show()
        }
    }

}
