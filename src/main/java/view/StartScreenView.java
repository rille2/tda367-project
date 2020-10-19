package view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StartScreenView implements ViewInterface {
    private Pane pane;
    private Button confirmButton;
    private Spinner<Integer> intInput;

    public StartScreenView(Group root, int width, int height) {
        pane = new Pane();
        pane.setPrefSize(width, height);
        pane.setStyle("-fx-background-color: white");
        root.getChildren().add(pane);

        confirmButton = new Button();
        confirmButton.setText("Confirm");
        confirmButton.setFont(Font.font("Ink Free"));
        confirmButton.setLayoutX(width / 2 + confirmButton.getWidth() / 2);
        confirmButton.setLayoutY(height / 2);

        intInput = new Spinner<>(1,4,1,1);
        intInput.setLayoutX(confirmButton.getLayoutX() - 200);
        intInput.setLayoutY(height / 2);

        Text titleText = new Text("Choose the amount of players");
        titleText.setStyle("-fx-font-size: 25");
        titleText.setFont(Font.font("Ink Free"));
        titleText.setWrappingWidth(width);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setLayoutX(0);
        titleText.setLayoutY(100);

        addNode(confirmButton);
        addNode(intInput);
        addNode(titleText);
    }


    @Override
    public void viewToFront() {
        pane.toFront();
    }

    @Override
    public void addNode(Node node) {
        pane.getChildren().add(node);
    }



    public Button getButton() {
        return confirmButton;
    }

    public Spinner<Integer> getIntInput() {
        return intInput;
    }
}