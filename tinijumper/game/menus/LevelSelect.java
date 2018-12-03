package jisaneko.tinijumper.game.menus;

import java.util.ArrayList;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.source.GameObject;

public class LevelSelect {

	public static final int MAXTITLEPOS = Main.SCR_H / 25;

	String imgSource = "resources/images/menus/lvls";

	int tilePos = Main.SCR_W;

	/*Menu sprites - START*/
	public GameObject title = new GameObject(imgSource + "/title.png", Main.SCR_W / 2 - Main.TILESIZEX * 3 / 2, MAXTITLEPOS, Main.TILESIZEX * 3, Main.TILESIZEX * 3 * 222 / 742);
	public GameObject background = new GameObject(imgSource + "/back.png", 0, 0, Main.SCR_W, Main.SCR_H);
	public GameObject exit = new GameObject(imgSource + "/exit.png", Main.SCR_W - 10 - Obstacle.SIZE / 2, 10, Obstacle.SIZE / 2, Obstacle.SIZE / 2);

	public ArrayList<GameObject> levels = new ArrayList<GameObject>();
	/*Menu sprites - END*/
	
	public LevelSelect(){
		
		levels.add(new GameObject(imgSource + "/lvl1.png", Main.SCR_W / 2 - Main.TILESIZEX * 2, Main.SCR_H / 2 - Main.TILESIZEY * 2, Main.TILESIZEX, Main.TILESIZEY));
		levels.add(new GameObject(imgSource + "/lvl2.png", Main.SCR_W / 2 - Main.TILESIZEX * 2, Main.SCR_H / 2, Main.TILESIZEX, Main.TILESIZEY));

	}

}