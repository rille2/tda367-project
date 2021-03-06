package model;

import utilities.Coord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Class representing the board on which the game is played on. Holds a number of floors.
 */
public class Board {

    private final Random rand = new Random();
    private final List<Floor> floors;
    private final int numberOfFloors = 3;
    private final int eventPerFloor = 5;
    private final List<Event> events;
    private List<Integer> indexList;

    /**
     * A board consists of three floors
     * Receives a list of events from EventFactory
     */
    Board() {
        floors = new ArrayList<>();
        events = EventFactory.createEventList();
        for (int i = 0; i < numberOfFloors; i++) {
            floors.add(new Floor(generateEventList(i), i));
        }
    }

    /**
     * Gets events
     *
     * @return list of events
     */
    List<Event> getEvents() {
        return events;
    }

    /**
     * @param i index in list of floors
     * @return floor number i
     */
    public Floor getFloor(int i) {
        return floors.get(i);
    }

    /**
     * @return list of random indexes equal to the length of list of events
     */
    private List<Integer> randomIndexList() {
        indexList = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {

            indexList.add(i);
        }
        return indexList;
    }

    /**
     * @return shuffled list of events for a single floor
     */
    private List<Event> generateEventList(int floorIndex) {
        int index;
        indexList = randomIndexList();
        List<Event> floorEventList = new ArrayList<>();
        for (int i = 0; i < eventPerFloor; i++) {
            index = rand.nextInt(indexList.size());
            while (!checkEligibleForFloor(events.get(indexList.get(index)), floorIndex)) {
                index ++;
                index %= indexList.size();
            }
            floorEventList.add(events.get(indexList.get(index)));
            indexList.remove(index);
        }
        return floorEventList;
    }

    /**
     * @param coord coordinate of current active player
     * @return hashmap of active doors around current active player
     */
    HashMap<Integer, Boolean> getCurrentPlayerTileDoors(Coord coord) {
        Floor currentFloor = floors.get(coord.getFloor());
        return currentFloor.getDoorsOnTile(coord.getX(), coord.getY());
    }

    /**
     * @param player current active player
     *               Activates event on current active player if there is an event on their position
     * @return boolean, true if there was an event was activated, flase if not
     */
    boolean tryActivateEventOnPlayerPos(Player player) {
        if (player.isHaunted()) {
            return false;
        }
        Floor floor = floors.get(player.getFloor());
        return floor.tryActivateEventOnTile(player);
    }

    /**
     * chains handleEvent method down to event
     *
     * @param currentPlayer current active player
     */
    public void handleEvent(Player currentPlayer) {
        Floor floor = floors.get(currentPlayer.getFloor());
        floor.handleEvent(currentPlayer);
    }

    /**
     * Chaining of a getter down to Event
     *
     * @param currentPlayer current active player
     * @return String effectText
     */
    public String getEventEffectText(Player currentPlayer) {
        Floor floor = floors.get(currentPlayer.getFloor());
        return floor.getEventEffectText(currentPlayer);
    }

    /**
     * Chaining of a getter down to Event
     *
     * @param currentPlayer current Active player
     * @return String of text to be displayed on Button.
     */
    String getEventButtonText(Player currentPlayer) {
        Floor floor = floors.get(currentPlayer.getFloor());
        return floor.getEventButtonText(currentPlayer);
    }

    List<Coord> getStairsUpOnCurrentFloor(int floor) {
        return floors.get(floor).getStairsUp(floor);
    }

    List<Coord> getStairsDownOnCurrentFloor(int floor) {
        return floors.get(floor).getStairsDown(floor);
    }

    /**
     * checks so that no move events, elevators and such, can be added to top or bottom floor
     * @param event event to be added
     * @param floor floor index
     * @return
     */
    private boolean checkEligibleForFloor(Event event, int floor) {
        boolean moveEvent = event.getEventType() == -3;
        boolean eligible = !(moveEvent && floor != 1);
        return eligible;
    }
}
