package jisaneko.tinijumper.game.menus;

import javax.swing.JFrame;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.source.GameObject;

public class OnlineSelect {

	
	String imgSource = "resources/images/menus/online";
	
	boolean hostGlow = false;
	boolean joinGlow = false;
	
	JFrame hostFr = new JFrame("Host Server!");
	JFrame joinFr = new JFrame("Join Server!");
	
	int port;
	String ip;
	
	/*Objects - START*/
	public GameObject host = new GameObject(imgSource + "/host.png", Main.SCR_W / 2 - Main.TILESIZEX * 2 / 2, Main.SCR_H / 2 -  50 - Main.TILESIZEY, Main.TILESIZEX * 2, Main.TILESIZEY * 2);
	public GameObject join = new GameObject(imgSource + "/join.png", Main.SCR_W / 2 - Main.TILESIZEX * 2 / 2, Main.SCR_H * 2 / 3 - Main.TILESIZEY, Main.TILESIZEX * 2, Main.TILESIZEY * 2);
	public GameObject exit = new GameObject(imgSource + "/exit.png", Main.SCR_W - 10 - Obstacle.SIZE / 2, 10, Obstacle.SIZE / 2, Obstacle.SIZE / 2);
	public GameObject back = new GameObject(imgSource + "/back.png", 0, 0, Main.SCR_W, Main.SCR_H);
	/*Objects - END*/
	
	
	
	public OnlineSelect(){
		
		
		
	}
	
	
	
	public void glow(String tile){

		if(tile.equals("host")){
			if(!hostGlow){
				host.setImage(imgSource + "/host_gl.png");
				hostGlow = true;
			}
		}
		if(tile.equals("join")){
			if(!joinGlow){
				join.setImage(imgSource + "/join_gl.png");
				joinGlow = true;
			}
		}

	}



	public void stopGlow(String tile){

		if(tile.equals("host")){
			if(hostGlow){
				host.setImage(imgSource + "/host.png");
				hostGlow = false;
			}
		}
		if(tile.equals("join")){
			if(joinGlow){
				join.setImage(imgSource + "/join.png");
				joinGlow = false;
			}
		}

	}
	
	
	
	public void host(){
		
	}
	
}