package jisaneko.tinijumper.game.menus;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.source.GameObject;

public class Controls {

	
	String imgSource = "resources/images/menus/controls";
	
	/*Objects - START*/
	public GameObject exit = new GameObject(imgSource + "/exit.png", Main.SCR_W - 10 - Obstacle.SIZE / 2, 10, Obstacle.SIZE / 2, Obstacle.SIZE / 2);
	public GameObject back = new GameObject(imgSource + "/back.png", 0, 0, Main.SCR_W, Main.SCR_H);
	/*Objects - END*/
	
}