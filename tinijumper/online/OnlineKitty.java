package jisaneko.tinijumper.online;

import java.net.InetAddress;

import jisaneko.tinijumper.game.source.Kitty;

public class OnlineKitty extends Kitty{
	
	InetAddress ip;
	int port;
	
	public String username;
	
	public OnlineKitty(int playerType, int pX, int pY, InetAddress ip, int port) {
		super(playerType, pX, pY);
		this.ip = ip;
		this.port = port;
	}
	
	
	
	public void manage(){
		manageImage();
		if(velX < 0) facing = 'l';
		if(velX > 0) facing = 'r';
	}

}
