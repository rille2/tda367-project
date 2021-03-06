package view;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.Game;
import utilities.Coord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * View taht displays everything in the main gameplay screen, floors, players, stats and such
 */
public class GameplayView implements ViewInterface {
    private static final int X = 0;
    private static final int Y = 1;
    private final int width;
    private final int height;
    private final Pane rootPane;
    private AnchorPane mapPane;
    private AnchorPane statsPane;
    private AnchorPane playersPane;
    private AnchorPane inventoryPane;
    private AnchorPane floorPane;
    private int rectSize;
    private int doorButtonSize;
    private int doorButtonOffset;
    private HashMap<Integer, int[]> doorOffsetMap;


    private List<Circle> playerSprites;
    private List<Circle> playersSpritesCurrentFloor;
    private List<Text> allPlayersList;
    private Text currentPlayerIindicator;
    private Text currentplayer;
    private List<Text> currentPlayerStats;
    private List<Coord> playerCoords;
    private int currentPlayerIndex;
    private Text currentFloor;
    private Text eventEffectText;
    private Text stepsLeft;

    private HashMap<Integer, Boolean> currentTileDoors;
    private List<Button> doorButtons;
    private Button endTurnButton;
    private List<Text> itemsInInventory;
    private List<Text> stairs;

    private final Game game;
    private List<List<Rectangle>> tileViews;


    public GameplayView(Group root, int width, int height, Game game) {
        rootPane = new Pane();
        rootPane.setPrefSize(width, height);
        root.getChildren().add(rootPane);
        this.width = width;
        this.height = height;
        this.game = game;
        initPanes();
        initTileViews();
    }

    /**
     * created all its different panes to display data on
     */
    private void initPanes() {

        mapPane = new AnchorPane();
        mapPane.setPrefSize(height - 150, height - 150);
        mapPane.setLayoutY(0);
        mapPane.setLayoutX(width / 2 - (height - 150) / 2);

        statsPane = new AnchorPane();
        statsPane.setPrefSize((width - (height - 150)) / 2, height - 150);
        statsPane.setLayoutY(0);
        statsPane.setLayoutX(width - (width - (height - 150)) / 2);
        statsPane.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 5;");

        playersPane = new AnchorPane();
        playersPane.setPrefSize((width - (height - 150)) / 2, (height - 150) / 2);
        playersPane.setLayoutY(0);
        playersPane.setLayoutX(0);
        playersPane.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 5;");


        inventoryPane = new AnchorPane();
        inventoryPane.setPrefSize((width - (height - 150)) / 2, height - (height - 150) / 2);
        inventoryPane.setLayoutY((height - 150) / 2);
        inventoryPane.setLayoutX(0);
        inventoryPane.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 5;");

        floorPane = new AnchorPane();
        floorPane.setPrefSize(width - (height - 150) / 2, 150);
        floorPane.setLayoutX((height - 150) / 2);
        floorPane.setLayoutY(height - 150);
        floorPane.setStyle("-fx-background-color: black; -fx-border-color: white; -fx-border-width: 5;");

        addNode(inventoryPane);
        addNode(playersPane);
        addNode(statsPane);
        addNode(mapPane);
        addNode(floorPane);
    }

    /**
     * creates the tiles in the tilemap
     */
    private void initTileViews() {
        tileViews = new ArrayList<>();
        Rectangle currentRect;
        rectSize = (height - 150) / 6;
        for (int i = 0; i < 6; i++) {
            tileViews.add(new ArrayList<Rectangle>());
            for (int k = 0; k < 6; k++) {
                currentRect = new Rectangle(rectSize, rectSize);
                currentRect.setX(i * rectSize);
                currentRect.setY(k * rectSize);
                currentRect.setStyle(" -fx-fill: black; -fx-stroke: white; -fx-stroke-width: 5;");
                mapPane.getChildren().add(currentRect);
                tileViews.get(i).add(currentRect);
            }
        }

    }

    /**
     * gets data from model and inits everything
     */
    public void initMapData() {
        currentPlayerIndex = game.getCurrentPlayerIndex();
        initPlayerSprites();
        initPlayersPaneData();
        initStatsPane();
        initFloorPane();
        initButtons();
        initInventoryPane();
        initStair();
    }

    private void initInventoryPane() {
        itemsInInventory = new ArrayList<>();
        Text titleText = new Text("Inventory");
        titleText.setWrappingWidth((width - (height - 150)) / 2);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setLayoutX(0);
        titleText.setLayoutY(40);
        titleText.setFont(Font.font("Ink Free", 30));
        titleText.setFill(Color.WHITE);
        inventoryPane.getChildren().add(titleText);
        updateInventoryPane();
    }

    private void initPlayerSprites() {
        Circle currCircle;
        playerSprites = new ArrayList<>();
        playersSpritesCurrentFloor = new ArrayList<>();
        playerCoords = game.getPlayerPositions();
        for (int i = 0; i < game.getPlayerAmount(); i++) {
            currCircle = new Circle();
            currCircle.setCenterX(playerCoords.get(i).getX() * rectSize + rectSize / 2);
            currCircle.setCenterY(playerCoords.get(i).getY() * rectSize + rectSize / 2);
            currCircle.setRadius(15);
            currCircle.setFill(Color.WHITE);
            playerSprites.add(currCircle);
        }
        for (Integer index : game.getPlayerIndicesOnCurrentFloor()) {
            playersSpritesCurrentFloor.add(playerSprites.get(index));
            mapPane.getChildren().add(playerSprites.get(index));
        }
        playerSprites.get(currentPlayerIndex).setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 2");
    }

    private void initPlayersPaneData() {
        allPlayersList = new ArrayList<>();
        currentPlayerIindicator = new Text("->");
        currentPlayerIindicator.setFont(Font.font("Ink Free", 30));
        currentPlayerIindicator.setFill(Color.WHITE);
        playersPane.getChildren().add(currentPlayerIindicator);
        Text currText;
        for (int i = 0; i < game.getPlayerAmount(); i++) {
            currText = new Text("Player " + (i + 1));
            currText.setWrappingWidth((width - (height - 150)) / 2);
            currText.setTextAlignment(TextAlignment.CENTER);
            currText.setLayoutX(0);
            currText.setLayoutY(50 + i * ((height - 150) / 2) / game.getPlayerAmount());
            currText.setFont(Font.font("Ink Free", 30));
            currText.setFill(Color.WHITE);
            playersPane.getChildren().add(currText);
            allPlayersList.add(currText);
        }
        currentPlayerIindicator.setLayoutX(50);
        currentPlayerIindicator.setLayoutY(allPlayersList.get(currentPlayerIndex).getLayoutY());
    }

    private void initStatsPane() {
        currentplayer = new Text(game.getCurrentPlayersCharacterName());
        currentplayer.setWrappingWidth((width - (height - 150)) / 2);
        currentplayer.setTextAlignment(TextAlignment.CENTER);
        currentplayer.setLayoutX(0);
        currentplayer.setLayoutY(50);
        currentplayer.setFont(Font.font("Ink Free", 30));
        currentplayer.setFill(Color.WHITE);

        currentPlayerStats = new ArrayList<>();
        List<String> playerStatStrings = game.getCurrentPlayerStatsAsStrings();
        Text currStatText;
        for (int i = 0; i < playerStatStrings.size(); i++) {
            currStatText = new Text(playerStatStrings.get(i));
            currStatText.setWrappingWidth((width - (height - 150)) / 2);
            currStatText.setTextAlignment(TextAlignment.CENTER);
            currStatText.setLayoutX(0);
            currStatText.setLayoutY(100 + i * 50);
            currStatText.setFont(Font.font("Ink Free", 20));
            currStatText.setFill(Color.WHITE);
            currentPlayerStats.add(currStatText);
            statsPane.getChildren().add(currStatText);
        }
        statsPane.getChildren().add(currentplayer);

        stepsLeft = new Text("Steps left: " + game.getCurrentPlayerStepsLeft());
        stepsLeft.setWrappingWidth((width - (height - 150)) / 2);
        stepsLeft.setTextAlignment(TextAlignment.CENTER);
        stepsLeft.setLayoutX(0);
        stepsLeft.setLayoutY(120 + playerStatStrings.size() * 50);
        stepsLeft.setFont(Font.font("Ink Free", 30));
        stepsLeft.setFill(Color.WHITE);
        statsPane.getChildren().add(stepsLeft);
    }

    private void initFloorPane() {
        currentFloor = new Text("Showing Floor: " + (game.getCurrentFloorNumber() + 1));
        currentFloor.setWrappingWidth(height - 150);
        currentFloor.setTextAlignment(TextAlignment.CENTER);
        currentFloor.setLayoutX(0);
        currentFloor.setLayoutY(50);
        currentFloor.setFont(Font.font("Ink Free", 30));
        currentFloor.setFill(Color.WHITE);
        floorPane.getChildren().add(currentFloor);

        eventEffectText = new Text();
        eventEffectText.setWrappingWidth(height - 150);
        eventEffectText.setTextAlignment(TextAlignment.CENTER);
        eventEffectText.setLayoutX(0);
        eventEffectText.setLayoutY(100);
        eventEffectText.setFont(Font.font("Ink Free", 20));
        eventEffectText.setFill(Color.RED);
        floorPane.getChildren().add(eventEffectText);

        endTurnButton = new Button();
        endTurnButton.setText("End Turn");
        endTurnButton.setPrefSize((width - (height - 150)) / 2 - 10, 150 - 5);
        endTurnButton.setFont(Font.font("Ink Free", 30));
        endTurnButton.setLayoutX(height - 150 + 10);
        endTurnButton.setLayoutY(5);
        floorPane.getChildren().add(endTurnButton);
    }

    private void initStair() {
        stairs = new ArrayList<>();
        updateStairs();
    }

    private void updateStairs() {
        for (Text stair : stairs) {
            mapPane.getChildren().remove(stair);
        }
        stairs = new ArrayList<>();

        List<Coord> stairsUp = game.getStairsUpOnCurrentFloor();
        Text currText;
        for (Coord coord : stairsUp) {
            currText = new Text("^");
            currText.setStyle("-fx-text-fill: red; -fx-font-size: 20");
            currText.setFill(Color.RED);
            currText.setWrappingWidth(rectSize);
            currText.setTextAlignment(TextAlignment.CENTER);
            currText.setLayoutY(rectSize * coord.getY() + 40);
            currText.setLayoutX(rectSize * coord.getX());
            mapPane.getChildren().add(currText);
            stairs.add(currText);
        }

        List<Coord> stairsDown = game.getStairsDownOnCurrentFloor();
        for (Coord coord : stairsDown) {
            currText = new Text("v");
            currText.setStyle("-fx-text-fill: red; -fx-font-size: 20");
            currText.setFill(Color.RED);
            currText.setWrappingWidth(rectSize);
            currText.setTextAlignment(TextAlignment.CENTER);
            currText.setLayoutY(rectSize * coord.getY() + 40);
            currText.setLayoutX(rectSize * coord.getX());
            mapPane.getChildren().add(currText);
            stairs.add(currText);
        }
    }

    void setEventEffectText() {
        eventEffectText.setText(game.getEventEffectText());
    }


    void fadeEventText() {
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(4000));
        fade.setFromValue(10);
        fade.setToValue(0);
        fade.setNode(eventEffectText);
        fade.setAutoReverse(false);
        fade.play();
    }

    private void initButtons() {
        currentTileDoors = game.getCurrentTileDoors();
        doorButtonOffset = rectSize / 4;
        doorButtonSize = 10;
        doorOffsetMap = new HashMap<>();
        doorOffsetMap.put(0, new int[]{0, -doorButtonOffset});
        doorOffsetMap.put(1, new int[]{doorButtonOffset, 0});
        doorOffsetMap.put(2, new int[]{0, doorButtonOffset});
        doorOffsetMap.put(3, new int[]{-doorButtonOffset, 0});

        doorButtons = new ArrayList<>();
        Button currButton;
        for (int i = 0; i < 4; i++) { // 4 directions to walk
            currButton = new Button();
            currButton.setPrefSize(doorButtonSize, doorButtonSize);
            currButton.setLayoutX(getCurrentPlayerCenterX() + doorOffsetMap.get(i)[X] - doorButtonSize);
            currButton.setLayoutY(getCurrentPlayerCenterY() + doorOffsetMap.get(i)[Y] - doorButtonSize);
            currButton.setDisable(!currentTileDoors.get(i));
            doorButtons.add(currButton);
            mapPane.getChildren().add(currButton);
        }
    }

    /**
     * gets data from model and updates view
     */
    public void updateMapData() {
        currentPlayerIndex = game.getCurrentPlayerIndex();
        updatePlayerSprites();
        updatePlayersPaneData();
        updateStatsPane();
        updateFloorPane();
        updateButtons();
        updateInventoryPane();
        updateStairs();
    }

    private void updateInventoryPane() {
        for (Text itemText : itemsInInventory) {
            inventoryPane.getChildren().remove(itemText);
        }
        itemsInInventory = new ArrayList<>();
        List<String> itemStrings = game.getCurrentPlayerItemsAsText();
        Text currText;
        for (int i = 0; i < itemStrings.size(); i++) {
            currText = new Text(itemStrings.get(i));
            currText.setWrappingWidth((width - (height - 150)) / 2);
            currText.setTextAlignment(TextAlignment.CENTER);
            currText.setLayoutX(0);
            currText.setLayoutY(80 + i * 30);
            currText.setFont(Font.font("Ink Free", 20));
            currText.setFill(Color.WHITE);

            itemsInInventory.add(currText);
            inventoryPane.getChildren().add(currText);
        }
    }

    private void updateButtons() {
        currentTileDoors = game.getCurrentTileDoors();
        for (int i = 0; i < doorButtons.size(); i++) {
            doorButtons.get(i).setLayoutX(getCurrentPlayerCenterX() + doorOffsetMap.get(i)[X] - doorButtonSize);
            doorButtons.get(i).setLayoutY(getCurrentPlayerCenterY() + doorOffsetMap.get(i)[Y] - doorButtonSize);
            doorButtons.get(i).setDisable(!currentTileDoors.get(i));
        }
    }

    private void updateFloorPane() {
        currentFloor.setText("Showing Floor: " + (game.getCurrentFloorNumber() + 1));
    }

    private void updateStatsPane() {

        //updates the big character name in the statspane
        currentplayer.setText(game.getCurrentPlayersCharacterName());

        //updates stepcounter
        stepsLeft.setText("Steps left: " + game.getCurrentPlayerStepsLeft());

        //updates the stat texts
        List<String> playerStatStrings = game.getCurrentPlayerStatsAsStrings();
        for (int i = 0; i < playerStatStrings.size(); i++) {
            currentPlayerStats.get(i).setText(playerStatStrings.get(i));
        }
    }

    private void updatePlayersPaneData() {
        List<Text> deadPlayerTexts = new ArrayList<>();
        for (Integer i : game.getDeadPlayerIndices()) {
            deadPlayerTexts.add(allPlayersList.get(i));
            playersPane.getChildren().remove(allPlayersList.get(i));
        }
        for (Text deadPlayer : deadPlayerTexts) {
            allPlayersList.remove(deadPlayer);
        }
        currentPlayerIindicator.setLayoutY(allPlayersList.get(currentPlayerIndex).getLayoutY());
    }

    private void updatePlayerSprites() {
        //reset players that are shown
        for (Circle playerSprite : playerSprites) {
            mapPane.getChildren().remove(playerSprite);
        }
        initPlayerSprites();

        for (Circle playerSprite : playersSpritesCurrentFloor) {
            playerSprite.setStyle("-fx-stroke-width: none; -fx-stroke: none"); // one of these will be the last currentplayer
            mapPane.getChildren().remove(playerSprite);
        }
        playersSpritesCurrentFloor = new ArrayList<>();

        //update position of all players
        playerCoords = game.getPlayerPositions();
        for (int i = 0; i < playerSprites.size(); i++) {
            playerSprites.get(i).setCenterX(playerCoords.get(i).getX() * rectSize + rectSize / 2);
            playerSprites.get(i).setCenterY(playerCoords.get(i).getY() * rectSize + rectSize / 2);
        }

        //update currentplayer indicator
        playerSprites.get(currentPlayerIndex).setStyle("-fx-stroke: red; -fx-stroke-width: 2");

        //show players on current floor
        for (Integer index : game.getPlayerIndicesOnCurrentFloor()) {
            playersSpritesCurrentFloor.add(playerSprites.get(index));
            mapPane.getChildren().add(playerSprites.get(index));
        }
    }

    private int getCurrentPlayerCenterX() {
        return (int) playerSprites.get(currentPlayerIndex).getCenterX();
    }

    private int getCurrentPlayerCenterY() {
        return (int) playerSprites.get(currentPlayerIndex).getCenterY();
    }

    HashMap<Integer, Button> getDoorButtons() {
        HashMap<Integer, Button> buttonHashMap = new HashMap<>();
        for (int i = 0; i < doorButtons.size(); i++) {
            buttonHashMap.put(i, doorButtons.get(i));
        }
        return buttonHashMap;
    }

    Button getEndTurnButton() {
        return endTurnButton;
    }


    @Override
    public void viewToFront() {
        rootPane.toFront();
    }

    @Override
    public void addNode(Node node) {
        rootPane.getChildren().add(node);
    }


}
