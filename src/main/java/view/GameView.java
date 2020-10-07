package view;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.scene.*;

import model.Game;
import sun.applet.Main;

import javax.swing.text.View;
import java.awt.*;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;

public class GameView {
	public static final int WINDOW_H = 800;
	public static final int WINDOW_W = 1300;
	private Group root;
	private CharacterSelectView characterSelectView;
	private StartScreenView startScreenView;
	private MainGameView mainGameView;

	public GameView(Game game) {
		root = new Group();
		characterSelectView = new CharacterSelectView(root, WINDOW_W, WINDOW_H, game.getCharacterNames(),game.getCharacterStats());
		startScreenView = new StartScreenView(root, WINDOW_W, WINDOW_H);
		mainGameView = new MainGameView(root, WINDOW_W, WINDOW_H, game);
		startScreenView.viewToFront();

	}

	public Group getRoot() {
		return root;
	}

	public ViewInterface getCharacterSelectView(){
		return characterSelectView;
	}

	public ViewInterface getStartScreenView() {
		return startScreenView;
	}

	public ViewInterface getMainGameView() {
		return mainGameView;
	}

	public void updateCurrentPlayerIndex(int index) {
		characterSelectView.setCurrentPlayerText(index);
	}


	public HashMap<Integer, Button> getCharSelectButtons(){
		return characterSelectView.getButtonMap();
	}

	public List<Text> getCharSelectTexts(){
		return characterSelectView.getTexts();
	}

	public void updateMainGameViewMapData() {
		mainGameView.updateMapData();
	}


}
