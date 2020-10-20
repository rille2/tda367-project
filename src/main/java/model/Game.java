package model;

import controller.EventObserver;
import controller.GameObserver;
import utilities.Coord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game implements ControllerObservable {

    private static Game gameInstance;
    private static final boolean isInstanciated = false;
    private GameObserver observer;
    private List<Player> playerList;

    private List<Kharacter> characterList = KharacterFactory.getCharacters();
    private List<GameState> listOfHaunts = new ArrayList<>();
    GameState insanityHaunt;

    private Board board;


    private int playerAmount;
    private GameState gameState;

    private int currentPlayerIndex;
    private int eventCounter;
    private Random random = new Random();



    //SingeltonPattern








    private void runCharacterSelectScreen() {
        //Temporary character select
        for (int i = 0; i < playerAmount; i++) {
            playerList.get(i).setCharacter(characterList.get(i));
        }
    }





    public void moveCurrentPlayer(int dx, int dy) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.getStepsLeft() > 0) {
            currentPlayer.playerMove(dx, dy);
            board.tryActivateEventOnPlayerPos(currentPlayer);
        }
        notifyGameData();
    }




   /*
    public boolean roomContainsInsanePlayer(){
        return roomContainsInsanePlayer();  //Why does it return itself??
    }

    */

    private void runGameOverScreen() {

    }
    //Must it be private?

    public List<String> getCharacterNames() {
        List<String> characterNames = new ArrayList<>();
        for (Kharacter a : characterList) {
            characterNames.add(a.getName());
        }
        return characterNames;
    }

    public List<HashMap<Stat, Integer>> getCharacterStats() {
        List<HashMap<Stat, Integer>> characterStats = new ArrayList<>();
        for (Kharacter a : characterList) {
            characterStats.add(a.getStats());
        }
        return characterStats;
    }


    public List<Kharacter> getCharacterList() {
        return characterList;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    void turn(Player activePlayer, Event event) {
    }

    public Player getCurrentPlayer() {
        return playerList.get(currentPlayerIndex);
    }

    public void updateCurrentPlayer() {
        getCurrentPlayer().resetSteps();
        currentPlayerIndex++;
        if (currentPlayerIndex == playerAmount + 1) { //TODO se om vi kan göra detta lite vackrare xD
            currentPlayerIndex--;
        }
        observer.updateCurrentPlayer();
        currentPlayerIndex = currentPlayerIndex % playerAmount;

        if (currentPlayerIndex == 0)
            notifyNewTurn();
    }



    @Override
    public void setObserver(GameObserver observer) {
        this.observer = observer;
    }

    @Override
    public void notifyNewTurn() { //TODO change to only one observer not list
        observer.updateTurn();
    }

    @Override
    public void notifyGameData() {
        observer.updateMapData();
    }

    @Override
    public void notifyGameStart() {
        observer.initMapData();
    }

    public String getEventEffectText(){
        return board.getEventEffectText(getCurrentPlayer());
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean checkAllPlayersHaveChars() {
        for (Player player : playerList) {
            if (!player.getHasCharacter())
                return false;
        }
        return true;
    }

    public List<Coord> getPlayerPositions() {
        List<Coord> playerPositions = new ArrayList<>();
        for (Player player : playerList)
            playerPositions.add(player.getPos());
        return playerPositions;
    }

    public String getCurrentPlayersCharacterName() {
        return getCurrentPlayer().getCharacterName();
    }

    public List<String> getCurrentPlayerStatsAsStrings() {
        return getCurrentPlayer().getCharacterStatsAsStrings();
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentFloorNumber() {
        return getCurrentPlayer().getFloor();
    }

    public List<Integer> getPlayerIndicesOnCurrentFloor() {
        List<Integer> playerIndicesOnCurrentFloor = new ArrayList<>();
        for (int i = 0; i < playerAmount; i++) {
            if (playerList.get(i).getFloor() == getCurrentFloorNumber()) {
                playerIndicesOnCurrentFloor.add(i);
            }
        }
        return playerIndicesOnCurrentFloor;
    }

    public HashMap<Integer, Boolean> getCurrentTileDoors() {
        return board.getCurrentPlayerTileDoors(getPlayerPositions().get(getCurrentPlayerIndex()));
    }

    public int getCurrentPlayerStepsLeft() {
        return getCurrentPlayer().getStepsLeft();
    }

    public void endTurn() {
        updateCurrentPlayer();
        notifyGameData();
        //removeDeadPlayersFromGame(); TODO fix this, gives nullpointer exeption
    }

    public List<String> getCurrentPlayerItemsAsText() {
        return getCurrentPlayer().getItemNames();
    }

    public boolean currentPlayerHasCharacter() {
        return getCurrentPlayer().getHasCharacter();
    }

    public void setCurrentPlayersCharacter(int index) {
        getCurrentPlayer().setCharacter(characterList.get(index));
    }

    /**
     * singleton pattern
     * @return instance of game
     */
    public static Game getInstance() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }

    /**
     * registers event observer
     * @param eventObserver
     */
    public void registerEventObserver(EventObserver eventObserver) {
        for (Event e : board.getEvents()) {
            e.setObserver(eventObserver);
        }
    }





    public void handleEvent() {
        Player currentPlayer = getCurrentPlayer();
        board.handleEvent(currentPlayer);
        observer.updateMapData();
    }

    /**
     * Getter for the buttonText on the eventButton
     * @return String
     */
    public String getEventButtonText(){
        return board.getEventButtonText(getCurrentPlayer());
    }

    public void removeDeadPlayersFromGame() {
        for (Player p : playerList) {
            if (p.isPlayerDead()) {
                playerList.remove(p);
                playerAmount--;
            }
        }
    }

    public Tile getPlayerTile(Player player) {
        return board.getFloor(player.getFloor()).getTile(player.getX(), player.getY());
    }

    public boolean roomContainsInsanePlayer() {
        return false;
    }


    void initHaunt() {
        listOfHaunts.get(0).init();
    }

    /**
     * starts haunt if event counter reaches threshold
     */
    private void eventTriggered() {
        eventCounter++;
        if (eventCounter == 8) {
            gameState = getRandomHaunt();
        }
    }

    private GameState getRandomHaunt() {
        return listOfHaunts.get(random.nextInt(listOfHaunts.size()));
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    public void setPlayerAmount(int playerAmount) {
        this.playerAmount = playerAmount;
        createPlayers(playerAmount);
    }

    int nonHauntedPlayersLeft(){
        int count = 0;
        for (Player player: playerList){
            if (!player.isHaunted())
                count++;
        }
        return count;
    }

    //Must it be private?
    void createPlayers(int amountPlayers) {
        Player currPlayer;
        playerList = new ArrayList<>();
        for (int i = 0; i < amountPlayers; i++) {
            currPlayer = new Player();
            currPlayer.setPos(new Coord(i, 0, 1));
            playerList.add(currPlayer);
        }
    }

    private Game() {
        board = new Board();
        listOfHaunts.add(new InsanityHauntState());
    }



}
