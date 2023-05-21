package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TaskController {
  private VBox container;
  private VBox todoParentContainer;
  private VBox inprogressParentContainer;
  private VBox doneParentContainer;
  private VBox parentContainer;
  private Label heading;
  private Label errorMessage;
  private Task task;
  private Backend backend;

  public TaskController(
      VBox todoParentContainer,
      VBox inprogressParentContainer,
      VBox doneParentContainer,
      Label errorMessage,
      Backend backend,
      Task task)
      throws MyException {

    this.todoParentContainer = todoParentContainer;
    this.inprogressParentContainer = inprogressParentContainer;
    this.doneParentContainer = doneParentContainer;
    this.errorMessage = errorMessage;
    this.backend = backend;
    this.task = task;
    switch (task.getStatus()) {
      case TODO:
        parentContainer = todoParentContainer;
        break;
      case INPROGRESS:
        parentContainer = inprogressParentContainer;
        break;
      case DONE:
        parentContainer = doneParentContainer;
        break;
    }
    heading = new Label("#" + task.getID() + " " + task.getTitle());
    heading.setWrapText(true);
    Button button = new Button(">");
    button.setStyle("-fx-font-size: 8;");
    button.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent e) {
            switchPane();
          }
        });
    container = new VBox(heading, button);
    container.setSpacing(5);
    container.setPadding(new Insets(5));
    container.setStyle("-fx-background-color: #FFFFFF;");
    parentContainer.getChildren().add(container);
  }

  public void switchPane() {
    parentContainer.getChildren().remove(container);
    TaskStatus nextStatus;
    if (parentContainer == todoParentContainer) {
      nextStatus = TaskStatus.INPROGRESS;
    } else if (parentContainer == inprogressParentContainer) {
      nextStatus = TaskStatus.DONE;
    } else {
      nextStatus = TaskStatus.TODO;
    }
    try {
      backend.updateTaskStatus(task.getID(), nextStatus.toString());
    } catch (MyException e) {
      System.out.println("update task table: " + e);
      errorMessage.setText("Failed to update database");
      return;
    }
    if (parentContainer == todoParentContainer) {
      parentContainer = inprogressParentContainer;
    } else if (parentContainer == inprogressParentContainer) {
      parentContainer = doneParentContainer;
    } else {
      parentContainer = todoParentContainer;
    }
    parentContainer.getChildren().add(container);
    task.setStatus(nextStatus);
    errorMessage.setText("");
  }
}
