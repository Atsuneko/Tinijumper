package jisaneko.tinijumper.game.menus;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.source.GameObject;

public class MainMenu{


	public static final int MAXPOS = Main.SCR_W - Main.TILESIZEX * 4/3;
	public static final int MAXTITLEPOS = Main.SCR_H / 25;

	String imgSource = "resources/images/menus/main";

	public MenuState state = MenuState.ENTERING;

	int tilePos = Main.SCR_W;
	public float alpha = 1f;

	boolean startGlow = false;
	boolean multiGlow = false;
	boolean controlsGlow = false;
	boolean quitGlow = false;

	/*Menu sprites - START*/
	public GameObject title = new GameObject(imgSource + "/title.png", Main.SCR_W / 2 - Main.TILESIZEX * 3 / 2, MAXTITLEPOS, Main.TILESIZEX * 3, Main.TILESIZEX * 3 * 222 / 742);
	public GameObject background = new GameObject(imgSource + "/back.png", 0, 0, Main.SCR_W * 105 / 100, Main.SCR_H * 105 / 100);

	public GameObject start = new GameObject(imgSource + "/start.png", tilePos, Main.SCR_H / 3 + Main.TILESIZEY, Main.TILESIZEX, Main.TILESIZEY);
	public GameObject multi = new GameObject(imgSource + "/multi.png", tilePos, Main.SCR_H / 3 + Main.TILESIZEY * 5/2, Main.TILESIZEX, Main.TILESIZEY);
	public GameObject controls = new GameObject(imgSource + "/controls.png", tilePos, Main.SCR_H / 3 + Main.TILESIZEY * 4, Main.TILESIZEX, Main.TILESIZEY);
	public GameObject quit = new GameObject(imgSource + "/quit.png", tilePos, Main.SCR_H / 3 + Main.TILESIZEY * 11/2, Main.TILESIZEX, Main.TILESIZEY);
	/*Menu sprites - END*/



	public void tick(){

		start.posX = tilePos; multi.posX = tilePos; controls.posX = tilePos; quit.posX = tilePos;
		if((((double)(Main.SCR_W - tilePos))/((double)(Main.SCR_W - MAXPOS))) <= 1)	alpha = (float)((((double)(Main.SCR_W - tilePos))/((double)(Main.SCR_W - MAXPOS))) * 1f);
		else alpha = 1f;
		
		if(state == MenuState.ENTERING){
			if(tilePos > MAXPOS) {
				tilePos -= 20;
			}
			if(tilePos <= MAXPOS) state = MenuState.INMENU;
		}

		if(state == MenuState.EXITING){
			if(tilePos < Main.SCR_W) {
				tilePos += 20;
			}
			if(tilePos >= Main.SCR_W) state = MenuState.EXITED;
		}

	}



	public void exit(){

		if(state == MenuState.INMENU) state = MenuState.EXITING;

	}



	public void glow(String tile){

		if(tile.equals("start")){
			if(!startGlow){
				start.setImage(imgSource + "/start_gl.png");
				startGlow = true;
			}
		}
		if(tile.equals("multi")){
			if(!multiGlow){
				multi.setImage(imgSource + "/multi_gl.png");
				multiGlow = true;
			}
		}
		if(tile.equals("controls")){
			if(!controlsGlow){
				controls.setImage(imgSource + "/controls_gl.png");
				controlsGlow = true;
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

		if(tile.equals("start")){
			if(startGlow){
				start.setImage(imgSource + "/start.png");
				startGlow = false;
			}
		}
		if(tile.equals("multi")){
			if(multiGlow){
				multi.setImage(imgSource + "/multi.png");
				multiGlow = false;
			}
		}
		if(tile.equals("controls")){
			if(controlsGlow){
				controls.setImage(imgSource + "/controls.png");
				controlsGlow = false;
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