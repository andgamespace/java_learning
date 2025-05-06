// src/main/java/com/TodoList/TodoApp.java
package com.TodoList;

import com.TodoList.dao.DatabaseManager;
import com.TodoList.view.TodoView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TodoApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Create main view
        TodoView todoView = new TodoView();

        // Create the scene with our TodoView as the root node
        Scene scene = new Scene(todoView, 720, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/modern.css").toExternalForm());

        // Set up the stage
        primaryStage.setTitle("Todo List Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Clean up resources when the application closes
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}