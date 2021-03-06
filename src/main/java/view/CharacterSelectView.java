package view;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CharacterSelectView prompt the player to pick a character
 * and displays the stats of the characters
 */

public class CharacterSelectView implements ViewInterface {
    private final Pane pane;

    private final int width;
    private final int height;
    private final int buttonSpacing;

    private Text text;
    private Text playerText;
    private List<Text> textsPlayer;
    private List<Text> textsStats;
    private List<List<String>> statList;
    private HashMap<Integer, Button> buttonMap;
    private Button startButton;

    public CharacterSelectView(Group root, int width, int height) {
        this.width = width;
        this.height = height;
        buttonSpacing = 70;

        pane = new Pane();
        pane.setPrefSize(width, height);
        pane.setStyle("-fx-background-color: Black");
        root.getChildren().add(pane);
    }

    /**
     * creates buttons and everything about them except the action event
     * @param characterNames
     */
    public void initButton(List<String> characterNames) {
        buttonMap = new HashMap<>();

        startButton = new Button();
        startButton.setLayoutX(width / 2 - 180);
        startButton.setLayoutY(height / 2 + characterNames.size() * buttonSpacing);
        startButton.setPrefSize(400, 50);
        startButton.setText("Start Game");
        startButton.setFont(Font.font("Ink Free", 20));
        addNode(startButton);

        Button currButton;
        for (int i = 0; i < characterNames.size(); i++) {
            currButton = new Button();
            currButton.setLayoutX(width / 2);
            currButton.setLayoutY(height / 2 + i * buttonSpacing);
            currButton.setPrefSize(200, 30);
            currButton.setText(characterNames.get(i));
            currButton.setFont(Font.font("Ink Free", 20));
            addNode(currButton);
            buttonMap.put(i, currButton);
        }

    }

    /**
     *  creatied text elements
     * @param statList List of String list, one string list for every character, representing their stats as text
     */
    public void initText(List<List<String>> statList) {
        this.statList = statList;

        playerText = new Text();
        playerText.setText("It's Player 1:s turn!");
        playerText.setFont(Font.font("Ink Free", 25));
        playerText.setFill(Color.WHITE);
        playerText.setWrappingWidth(300);
        playerText.setTextAlignment(TextAlignment.CENTER);
        playerText.setLayoutX(width / 2 - playerText.getWrappingWidth() / 2);
        playerText.setLayoutY(150);
        addNode(playerText);

        text = new Text("Choose your character");
        text.setFont(Font.font("Ink Free", 50));
        text.setFill(Color.WHITE);
        text.setWrappingWidth(300);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setLayoutX(width / 2 - text.getWrappingWidth() / 2);
        text.setLayoutY(50);
        addNode(text);

        textsPlayer = new ArrayList<>();
        textsStats = new ArrayList<>();

        Text currPlayerText;
        Text currStatText;
        int refButtonX;
        int refButtonY;
        for (int i = 0; i < buttonMap.size(); i++) {
            refButtonX = (int) buttonMap.get(i).getLayoutX();
            refButtonY = (int) buttonMap.get(i).getLayoutY();


            currPlayerText = new Text();
            currPlayerText.setWrappingWidth(200);
            currPlayerText.setTextAlignment(TextAlignment.CENTER);
            currPlayerText.setLayoutX(refButtonX + 200);
            currPlayerText.setLayoutY(refButtonY + 20);
            currPlayerText.setFont(Font.font("Ink Free", 20));
            currPlayerText.setFill(Color.WHITE);
            addNode(currPlayerText);
            textsPlayer.add(currPlayerText);

            currStatText = new Text();
            currStatText.setWrappingWidth(180);
            currStatText.setTextAlignment(TextAlignment.CENTER);
            currStatText.setLayoutX(refButtonX - 180);
            currStatText.setLayoutY(refButtonY + 20);
            currStatText.setText(getAllStatsAsString(i));
            currStatText.setFont(Font.font("Ink Free", 15));
            currStatText.setFill(Color.WHITE);
            addNode(currStatText);
            textsStats.add(currStatText);
        }
    }


    /**
     * converst a list of strings to a string (stats)
     * @param statListIndex index of the statList in question
     * @return a string that can be directly displayed as character stats
     */
    private String getAllStatsAsString(int statListIndex) {
        String allStats = "";
        for (String stat : statList.get(statListIndex)) {
            allStats += stat;
            allStats += " ";
        }
        return allStats;
    }


    @Override
    public void viewToFront() {
        pane.toFront();
    }

    /**
     * changes the top text in the character select screen
     * @param index current player index
     * @param amountPlayers
     */
    public void setPlayerTexts(int index, int amountPlayers) {
        playerText.setText("It's Player " + (index + 1) + ":s turn!");
        if (index == amountPlayers) {
            playerText.setText("All players have chosen a character");
        }
    }

    @Override
    public void addNode(Node node) {
        pane.getChildren().add(node);
    }

    public HashMap<Integer, Button> getButtonMap() {
        return buttonMap;
    }

    public Button getStartButton() {
        return startButton;
    }

    public List<Text> getTexts() {
        return textsPlayer;
    }

    public void setText() {
        text.setText("All players have chosen a character");
    }

}
