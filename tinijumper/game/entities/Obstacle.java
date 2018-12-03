package jisaneko.tinijumper.game.entities;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.source.GameObject;
import jisaneko.tinijumper.game.source.Kitty;

public class Obstacle extends GameObject{

	
	public static final int SIZE = Kitty.SIZE_Y;
	
	public boolean trans;
	public boolean visible = true;
	
	public String type;
	
	
	public Obstacle(int pX, int pY, int lvlType,String type) {
		
		super("resources/images/obstacle/lvl" + lvlType + "/" + type, SIZE * (int)(pX / SIZE), SIZE * (int)(pY / SIZE), SIZE, SIZE);
		this.type = type;
		
		velX = 5;
		
	}
	
	
	
	public void tick(){
		
		if(type.equals("hardwall.png")){
			posX += velX;
			
			if(posX <= 0 || endX() >= Main.SCR_W) velX *= -1;
		}
		
	}
	
}
