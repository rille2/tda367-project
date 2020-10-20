package model;


import java.util.HashMap;

public class Tile {
    private Event event;
    private boolean illuminated = false;
    private boolean doorUp = true;
    private boolean doorDown = true;
    private boolean doorLeft = true;
    private boolean doorRight = true;
    private boolean hasPlayer;
    private boolean hasEvent;

    public Tile(int i, int j, Tile[][] tiles) {
        hasEvent = false;
        setDoors(i, j, tiles);
    }

    private void setDoors(int col, int row, Tile[][] tiles) {
        if (row == 0) doorUp = false;
        if (col == 0) doorLeft = false;
        if (row == tiles.length - 1) doorDown = false;
        if (col == tiles[0].length - 1) doorRight = false;

    }


    boolean hasPlayer() {
        return hasPlayer;
    }

    public void setHasPlayer(boolean hasPlayer) {
        this.hasPlayer = hasPlayer;
    }

    public boolean hasEvent() {
        return hasEvent;
    }

    public void toggleIllumination() {
        illuminated = !illuminated;
    }

    public void setEvent(Event event) {
        hasEvent = true;
        this.event = event;
    }

    /**
     * HashMap so that doors is easily accessible through an int
     * @return
     */
    HashMap<Integer, Boolean> getDoors() {
        HashMap<Integer, Boolean> doors = new HashMap<>();
        doors.put(0, doorUp);
        doors.put(1, doorRight);
        doors.put(2, doorDown);
        doors.put(3, doorLeft);
        return doors;
    }

    boolean tryActivateEvent() {
        if (hasEvent) {
            event.activate();
            return true;
        }
        return false;
    }

    /**
     * Handles an event with the player as a parameter, if it is not a permanent event it  will disappear
     * @param currentPlayer player that enters the tile
     */

    public void handleEvent(Player currentPlayer) {
        event.handleEvent(currentPlayer);
        if (!event.isPermanent()){
            event = null;
            hasEvent = false;
        }
    }

    /**
     * Getter for the event's buttonText.
     * @return String to be displayed on eventView.
     */
    String getEventButtonText(){
        return event.getEventButtonText();
    }

    /**
     * Getter for the event's outcome text
     * @return String to be displayed on mainGameView.
     */
    String getEventEffectText(){
        return event.getEventEffectText();
    }
}
