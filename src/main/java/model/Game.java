package model;

import utilities.Coord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game implements ControllerObservable {

    private static Game gameInstance;
    private GameObserver observer;
    private List<Player> playerList;
    private List<Integer> deadPlayerIndices;

    private final List<Kharacter> characterList = KharacterFactory.getCharacters();
    private final List<GameState> listOfHaunts = new ArrayList<>();
    private HashMap<String, Integer> staminaNameMap = new HashMap<>();

    private final Board board;


    private int playerAmount;
    private GameState gameState;

    private int currentPlayerIndex;
    private int eventCounter;
    private final int hauntTrigger = 8;
    private final Random random = new Random();


    private Game() {
        board = new Board();
        listOfHaunts.add(new InsanityHauntState());
        deadPlayerIndices = new ArrayList<>();
    }

    /**
     * singleton pattern
     *
     * @return instance of game
     */
    public static Game getInstance() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }

    /**
     * Moves currentPlayer and does sanityChecks for the move
     *
     * @param dx delta to move in X
     * @param dy delta to move in Y
     */
    public void moveCurrentPlayer(int dx, int dy) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.getStepsLeft() > 0) {
            currentPlayer.playerMove(dx, dy, 0);
            if (getPlayerTile(currentPlayer).hasStairDown()) {
                currentPlayer.addCoord(new Coord(0, 0, -1));
            } else if (getPlayerTile(currentPlayer).hasStairUp()) {
                currentPlayer.addCoord(new Coord(0, 0, 1));
            }

            board.tryActivateEventOnPlayerPos(currentPlayer);
        }
        removeDeadPlayersFromGame();
        hauntCheck();
        notifyGameData();

    }

    public void updateCurrentPlayer() {
        getCurrentPlayer().resetSteps();
        currentPlayerIndex++;
        if (currentPlayerIndex == playerAmount + 1) {
            currentPlayerIndex--;
        }
        observer.updateCurrentPlayer();
        currentPlayerIndex = currentPlayerIndex % playerAmount;
    }

    public boolean checkAllPlayersHaveChars() {
        for (Player player : playerList) {
            if (!player.getHasCharacter())
                return false;
        }
        return true;
    }

    public void endTurn() {
        removeDeadPlayersFromGame();
        checkForHauntInit();
        updateCurrentPlayer();
        notifyGameData();
    }

    /**
     * Creates the amount of players that the game is supposed to have.
     *
     * @param amountPlayers The amount of players needed.
     */
    private void createPlayers(int amountPlayers) {
        Player currPlayer;
        currentPlayerIndex=0;
        playerList = new ArrayList<>();
        for (int i = 0; i < amountPlayers; i++) {
            currPlayer = new Player();
            currPlayer.setPos(new Coord(i, 0, 1));
            playerList.add(currPlayer);
        }
    }

    /**
     * Creates a list of all the players that are currently on the same Tile
     *
     * @return List of players
     */
    List<Player> createListOfPlayersInSameRoom() {
        List<Player> listOfPlayersInTheSameRoom = new ArrayList<>();
        for (Player p : getPlayerList()) {
            if (p.getPos().equals(getCurrentPlayer().getPos())) {
                listOfPlayersInTheSameRoom.add(p);
            }
        }
        return listOfPlayersInTheSameRoom;
    }

    /**
     * Checks if haunt is activated
     */
    private void hauntCheck() {
        checkForHauntInit();
        if (gameState != null) {
            gameState.turn(getCurrentPlayer());
        }

    }

    public List<String> getCharacterNames() {
        List<String> characterNames = new ArrayList<>();
        for (Kharacter a : characterList) {
            characterNames.add(a.getName());
        }
        return characterNames;
    }

    public String getHauntText() {
        return gameState.getHauntText();
    }

    public List<List<String>> getCharacterStats() {
        List<List<String>> characterStats = new ArrayList<>();
        for (Kharacter a : characterList) {
            characterStats.add(a.getStatsAsStrings());
        }


        return characterStats;
    }

    public List<Player> getPlayerList() {
        List<Player> players = new ArrayList<>(playerList);
        return players;
    }

    public Player getCurrentPlayer() {
        return playerList.get(currentPlayerIndex);
    }

    public List<String> getHauntedNamesInSameRoom() {
        List<String> hauntedNameList = new ArrayList<>();
        for (Player p : createListOfPlayersInSameRoom()) {
            if (p.isHaunted()) {
                hauntedNameList.add(p.getCharacterName());
            }
        }
        return hauntedNameList;
    }

    public List<String> getNonHauntedNamesList() {
        List<String> nonHauntedNames = new ArrayList<>();
        for (Player p : createListOfPlayersInSameRoom()) {
            if (!p.isHaunted()) {
                nonHauntedNames.add(p.getCharacterName());
            }
        }
        return nonHauntedNames;
    }

    /**
     * get a hashmap of damage taken after battle
     *
     * @return hashmap with name and stat change after battle
     */
    public HashMap<String, Integer> getDamageMap() {
        HashMap<String, Integer> damageMap = new HashMap<>();
        int damage;
        if (!staminaNameMap.isEmpty()) {
            for (Player p : createListOfPlayersInSameRoom()) {
                damage = Math.abs(staminaNameMap.get(p.getCharacterName()) - p.getCharacter().getStat(Stat.STAMINA));
                damageMap.put(p.getCharacterName(), damage);
            }
        }
        return damageMap;
    }

    public HashMap<String, Integer> getStaminaNameMap() {
        HashMap<String, Integer> staminaNameMap = new HashMap<>();
        for (Player p : createListOfPlayersInSameRoom()) {
            staminaNameMap.put(p.getCharacterName(), p.getCharacter().getStat(Stat.STAMINA));
        }
        return staminaNameMap;
    }

    void saveOldStaminaMap() {
        staminaNameMap = getStaminaNameMap();
    }

    @Override
    public void setObserver(GameObserver observer) {
        this.observer = observer;
    }

    @Override
    public void notifyGameData() {
        observer.updateMapData();
    }

    @Override
    public void notifyGameStart() {
        observer.initMapData();

    }

    @Override
    public void notifyHaunt() {
        observer.initHauntView();
    }

    @Override
    public void notifyCombat() {
        observer.initCombatScreen();
    }

    @Override
    public void notifyGameOver() {
        observer.initGameOverView();

    }

    public String getEventEffectText() {
        return board.getEventEffectText(getCurrentPlayer());
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
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

    Board getBoard() {
        return board;
    }

    public int getCurrentFloorNumber() {
        return getCurrentPlayer().getFloor();
    }

    /**
     *
     * @return returns which floor (index) each player is on, information for the view so it knows which floor to show.
     */
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

    public List<String> getCurrentPlayerItemsAsText() {
        return getCurrentPlayer().getItemNames();
    }

    public void setCurrentPlayersCharacter(int index) {
        getCurrentPlayer().setCharacter(characterList.get(index));
    }

    /**
     * registers event observer
     *
     * @param eventObserver
     */
    public void registerEventObserver(EventObserver eventObserver) {
        for (Event e : board.getEvents()) {
            e.setObserver(eventObserver);
        }
    }

    /**
     * method chaining->asks board to handle event. Notifies observer to update afterwards.
     */
    public void handleEvent() {
        Player currentPlayer = getCurrentPlayer();
        board.handleEvent(currentPlayer);
        eventTriggered();
        observer.updateMapData();
    }

    /**
     * Getter for the buttonText on the eventButton
     *
     * @return String
     */
    public String getEventButtonText() {
        return board.getEventButtonText(getCurrentPlayer());
    }

    public void removeDeadPlayersFromGame() {
        deadPlayerIndices = new ArrayList<>();
        List<Player> deadPlayers = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).isPlayerDead()) {
                deadPlayers.add(playerList.get(i));
                deadPlayerIndices.add(i);
            }
        }
        for (int i = 0; i < deadPlayers.size(); i++) {
            playerList.remove(deadPlayers.get(i));
            playerAmount--;
            if (currentPlayerIndex >= playerAmount) currentPlayerIndex--;

        }

    }

    public Tile getPlayerTile(Player player) {
        return board.getFloor(player.getFloor()).getTile(player.getX(), player.getY());
    }

    private void initHaunt() {
        gameState.init();
    }

    /**
     * starts haunt if event counter reaches threshold
     */
    private void eventTriggered() {
        eventCounter++;
    }

    /**
     * Checks if haunt should be triggered. Will be random when more haunts is added.
     */
    private void checkForHauntInit() {
        if (eventCounter == hauntTrigger && gameState == null) {
            gameState = getRandomHaunt();
            initHaunt();
            notifyHaunt();
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

    public List<Coord> getStairsDownOnCurrentFloor() {
        return board.getStairsDownOnCurrentFloor(getCurrentFloorNumber());
    }

    public List<Coord> getStairsUpOnCurrentFloor() {
        return board.getStairsUpOnCurrentFloor(getCurrentFloorNumber());
    }

    public List<Integer> getDeadPlayerIndices() {
        List<Integer> copy = new ArrayList<>(deadPlayerIndices);
        deadPlayerIndices.clear();
        return copy;
    }

    int getHauntTrigger(){
        return hauntTrigger;
    }
}
