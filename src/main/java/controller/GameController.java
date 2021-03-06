package controller;


import model.EventObserver;
import model.Game;
import model.GameObserver;
import view.GameView;
import view.ViewInterface;

/**
 * Controller for GameView and holder for all other controllers
 * Inits all controllers and their views
 * Also sets and handles buttons for GameView
 */

public class GameController implements GameObserver, EventObserver {
    private final Game game;
    private final GameView view;
    private ViewInterface gameOverView;
    private ViewInterface characterSelectView;
    private ViewInterface startScreenView;
    private ViewInterface mainGameView;
    private ViewInterface combatView;
    private CharacterSelectController characterSelectController;
    private StartScreenController startScreenController;
    private GameOverController gameOverController;
    private GameplayViewController gameplayViewController;
    private EventController eventController;
    private CombatController combatController;


    public GameController(GameView view, Game game) {
        this.view = view;
        this.game = game;
        initViews();
        initControllers();
        initEvent();
    }

    private void initEvent() {
        game.registerEventObserver(this);
    }

    private void initViews() {
        startScreenView = view.getStartScreenView();
        characterSelectView = view.getCharacterSelectView();
        mainGameView = view.getGamePlayView();
        combatView = view.getCombatView();
        gameOverView = view.getGameOverView();
    }

    private void initControllers() {
        startScreenController = new StartScreenController(game);
        startScreenController.setNextView(characterSelectView);
        startScreenController.setIntInput(view.getStartScreenIntInput());
        startScreenController.setConfirmButton(view.getStartScreenConfirmButton());

        characterSelectController = new CharacterSelectController(game);
        characterSelectController.setStartGameButton(view.getStartGameButton());
        characterSelectController.setButtonMap(view.getCharSelectButtons());
        characterSelectController.setTextList(view.getCharSelectTexts());
        characterSelectController.setNextView(mainGameView);

        gameplayViewController = new GameplayViewController(game);

        eventController = new EventController(game);
        eventController.setEventButtonMap(view.getEventButtons());
        eventController.setMainGameView(mainGameView);

        combatController = new CombatController(game, combatView);
        combatController.setButton(view.getCombatButton());
        combatController.setNextView(mainGameView);

        gameOverController = new GameOverController(game);
        gameOverController.setCloseGameButton(view.getCloseGameButton());


    }

    @Override
    public void updateCurrentPlayer() {
        view.updateCurrentPlayerIndex(game.getCurrentPlayerIndex());
    }

    @Override
    public void updateMapData() {
        view.updateGameplayViewMapData();
    }

    @Override
    public void initMapData() {
        view.initMapData();
        gameplayViewController.setDoorButtons(view.getMainGameViewDoorButtons());
        gameplayViewController.setEndTurnButton(view.getMainGameViewEndTurnButton());
    }

    @Override
    public void initHauntView() {
        view.initHauntView();
    }

    @Override
    public void initCombatScreen() {
        view.initCombatScreen();

    }

    @Override
    public void initGameOverView() {
        view.initGameOverView();


    }

    @Override
    public void updateEventEffect() {
        view.updateEventEffect();
    }

    @Override
    public void updateEventView(int eventType, String eventText) {
        view.updateEventView(eventType, eventText);
        view.updateGameplayViewMapData();

    }
}