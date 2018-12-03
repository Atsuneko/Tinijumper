package jisaneko.tinijumper.game.source;
import java.awt.Color;

import jisaneko.tinijumper.game.Main;

public class HUD {

	
	public final static int BOOSTSIZEY = Main.SCR_H / 15;
	public final static double MAXBOOST = 50;
	public int boostPosX = 20, boostPosY = Main.SCR_H - BOOSTSIZEY * 2;
	public double boost = MAXBOOST / 2;
	public int boostSizeX = (int)(boost * 10);
	public boolean inBoost = false;
	public Color boostCol = new Color(199, 50, 133);
	
	int hearts = 6;
	
	public void tick(){
		
		if(boost < 0) boost = 0;
		if(boost > MAXBOOST) boost = MAXBOOST;
		
		boostSizeX = (int)(boost * 10);
		
		if(inBoost && boost <= 0){
			inBoost = false;
		}
		
		if(inBoost){
			boostCol = new Color(255, 182, 193);
			boost--;
		}if(boost >= 0.9 * MAXBOOST){
			if(boostCol.equals(new Color(199, 50, 133))) boostCol = Color.RED;
			else if(boostCol.equals(Color.RED)) boostCol = Color.BLUE;
			else if(boostCol.equals(Color.BLUE)) boostCol = Color.GREEN;
			else if(boostCol.equals(Color.GREEN)) boostCol = Color.RED;
		} else {
			boostCol = new Color(199, 50, 133);
		}
		
	}
	
	
	
	public void addToBoost(int amnt){
		
		if(boost + amnt < MAXBOOST){
			boost += amnt;
		}
		
	}
	
}
