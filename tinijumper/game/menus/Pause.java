package jisaneko.tinijumper.game.menus;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.source.GameObject;

public class Pause{


	String imgSource = "resources/images/menus/pause";

	boolean mainGlow = false;
	boolean quitGlow = false;

	/*Pause sprites - START*/
	public GameObject background = new GameObject(imgSource + "/back.png", 0, 0, Main.SCR_W, Main.SCR_H);

	public GameObject main = new GameObject(imgSource + "/menu.png", Main.SCR_W / 2 - Main.TILESIZEX / 2, Main.SCR_H / 2, Main.TILESIZEX, Main.TILESIZEY);
	public GameObject quit = new GameObject(imgSource + "/quit.png", Main.SCR_W / 2 - Main.TILESIZEX / 2, Main.SCR_H / 2 + (Main.TILESIZEY * 3) / 2, Main.TILESIZEX, Main.TILESIZEY);
	/*Pause sprites - END*/


	public void glow(String tile){

		if(tile.equals("main")){
			if(!mainGlow){
				main.setImage(imgSource + "/menu_gl.png");
				mainGlow = true;
			}
		}
		if(tile.equals("quit")){
			if(!quitGlow){
				quit.setImage(imgSource + "/quit_gl.png");
				quitGlow = true;
			}
		}

	}



	public void stopGlow(String tile){

		if(tile.equals("main")){
			if(mainGlow){
				main.setImage(imgSource + "/menu.png");
				mainGlow = false;
			}
		}
		if(tile.equals("quit")){
			if(quitGlow){
				quit.setImage(imgSource + "/quit.png");
				quitGlow = false;
			}
		}

	}

}
