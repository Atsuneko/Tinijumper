package jisaneko.tinijumper.game.entities;

import jisaneko.tinijumper.game.source.GameObject;

public class Coin extends GameObject{

	
	public static final int SIZE = Obstacle.SIZE;
	public static final int PLUS = 20;
	
	
	public Coin(int pX, int pY) {
		
		super("resources/images/envir/coin.gif", SIZE * (int)(pX / SIZE), SIZE * (int)(pY / SIZE), SIZE, SIZE);
		
	}
	
}
