package jisaneko.tinijumper.online;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.source.GameObject;
import jisaneko.tinijumper.game.source.Kitty;

public class Client implements Runnable{

	String username;
	
	InetAddress ip;
	int port;
	DatagramSocket sock;
	DatagramPacket sendPacket;
	byte[] send;
	
	Random rand = new Random();

	public Input input;

	public int levelType;
	public int startX = 0, startY = 0;
	
	/*Game Objects - START*/
	public Kitty oplyr;
	public ArrayList<OnlineKitty> plyrs = new ArrayList<OnlineKitty>();
	
	public GameObject levelBack;
	public ArrayList<Obstacle> walls = new ArrayList<Obstacle>();
	/*Game Objects - END*/

	public boolean gameStarted = false;
	
	public String disp = "";

	public int levHeight = 0;
	public int genKey = 0;
	public int serverHeight = 0;

	public Client(String address, int port, int type, String username){
		try {
			this.username = username;
			
			sock = new DatagramSocket();
			ip = InetAddress.getByName(address);
			this.port = port;

			levelType = type;

			send = "new".getBytes();
			sendPacket = new DatagramPacket(send, send.length, ip, port);
			send = new byte[1024];
			sock.send(sendPacket);

			setUp();

			input = new Input(sock, plyrs, walls, ip, this.port);
		} catch (IOException e) {
			System.exit(1);
		}
	}

	public void run(){
		while(true){
			String data = "";

			try {
				data = "plyr," + username + "/" + InetAddress.getLocalHost().getHostAddress() + ":" + sock.getLocalPort() + "," + oplyr.posX + "," + oplyr.posY + "," + oplyr.velX + "," + oplyr.botCol + ";";
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			send = data.getBytes();
			sendPacket = new DatagramPacket(send, send.length, ip, port);
			send = new byte[1024];
			try {
				if(input.openSpace && input.serverExists) sock.send(sendPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(levHeight > serverHeight){
				data = "height," + levHeight;
				send = data.getBytes();
				sendPacket = new DatagramPacket(send, send.length, ip, port);
				send = new byte[1024];
				try {
					if(input.openSpace && input.serverExists) sock.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			serverHeight = input.serverHeight;

			input.run();
			
			if(input.toStartGame){
				startGame();
			}
			
			if(gameStarted && !disp.equals("JUMP! JUMP! JUMP!")) disp = "JUMP! JUMP! JUMP!";
			if(!gameStarted && !disp.equals("Waiting for Players")) disp = "Waiting for Players";
		}
	}
	
	
	public void startGame(){
		oplyr.posX = startX; oplyr.posY = startY;
		gameStarted = true;
		input.toStartGame = false;
	}


	public void setUp(){
		BufferedReader levelFile = null;

		try {
			levelFile = new BufferedReader(new FileReader(new File("data/levels/lvl" + levelType + ".txt")));
		} catch (FileNotFoundException e) {
			//Error Message
			System.exit(1);
		}

		String line = null;
		try {
			levelFile.readLine();
			while((line = levelFile.readLine()) != null){
				if(line.equals("")) continue;
				if(line.equals("next")) break;
				walls.add(new Obstacle(Integer.parseInt(line.substring(0, 2)) * Obstacle.SIZE, Integer.parseInt(line.substring(2, 4)) * Obstacle.SIZE, levelType, line.substring(4, line.length())));
			}
			
			line = levelFile.readLine();
			
			startX = Integer.parseInt(line.substring(0,2)) * Obstacle.SIZE; startY = Integer.parseInt(line.substring(2,4)) * Obstacle.SIZE;
			oplyr = new Kitty(1, Integer.parseInt(line.substring(0,2)) * Obstacle.SIZE, Integer.parseInt(line.substring(2,4)) * Obstacle.SIZE);
			
			levelBack = new GameObject("resources/images/backgrounds/lvl" + levelType + ".png", 0, Main.SCR_H - (Main.SCR_W * 10), Main.SCR_W, Main.SCR_W * 10);
			
		} catch (IOException e) {
			//Error Message
			System.exit(1);
		}
	}



	public void generateBlocksOnline(int height, int width){

		int fromHeight = height + levHeight;
		int numWalls = walls.size();
		for(int i = 0; i < numWalls; i++){
			if(walls.get(i).posY < fromHeight) fromHeight = walls.get(i).posY;
		}

		ArrayList<Obstacle> highWalls = new ArrayList<Obstacle>();
		int newNumWalls = walls.size();
		for(int i = 0; i < newNumWalls; i++){
			if(walls.get(i).posY == fromHeight) highWalls.add(walls.get(i));
		}

		int nextHeight = fromHeight - ((rand.nextInt(2) + 3) * Obstacle.SIZE);

		if(rand.nextDouble() < 0.2){
			walls.add(new Obstacle(rand.nextInt(9 * Obstacle.SIZE), nextHeight, levelType, "hardwall.png"));
		} else {
			int[] nextPosX = new int[rand.nextInt(3) + 3];

			for(int i = 0; i < nextPosX.length; i++) nextPosX[i] = -1;

			for(int i = 0; i < nextPosX.length; i++){
				nextPosX[i] = rand.nextInt(width / Obstacle.SIZE);

				boolean doSkip = false;

				for(Obstacle o : highWalls){
					if(nextPosX[i] == o.posX / Obstacle.SIZE) {
						i--;
						doSkip = true;
						break;
					}
				}
				if(doSkip) continue;

				for(Obstacle o : highWalls){
					if(nextPosX[i] + 1 == o.posX / Obstacle.SIZE || nextPosX[i] - 1 == o.posX / Obstacle.SIZE) {
						i--;
						break;
					}
				}
			}

			Arrays.sort(nextPosX);

			for(int i = 0; i < nextPosX.length; i++){
				String blType = "";
				for(int j = 0; j < nextPosX.length; j++){
					if(j == i) continue;
					if(nextPosX[i] == nextPosX[j]) continue;

					if(nextPosX[i] == nextPosX[j] + 1) blType = "_ol";
					if(nextPosX[i] == nextPosX[j] - 1){
						if(blType.equals("")) blType = "_or";
						if(blType.equals("_ol")) blType = "_ph";
					}
				}
				if(rand.nextDouble() <= 0.01) {
					walls.add(new Obstacle(nextPosX[i] * Obstacle.SIZE, nextHeight, levelType, "wall" + blType + ".png"));
					walls.get(walls.size() - 1).trans = true;
					String data = "block," + walls.get(walls.size() - 1).posX + "," + walls.get(walls.size() - 1).posY + ",wall" + blType + ".png," + walls.get(walls.size() - 1).trans + ";"; 
					send = data.getBytes();
					sendPacket = new DatagramPacket(send, send.length, ip, port);
					send = new byte[1024];
					try {
						sock.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					walls.add(new Obstacle(nextPosX[i] * Obstacle.SIZE, nextHeight, levelType, "wall" + blType + ".png"));
					String data = "block," + walls.get(walls.size() - 1).posX + "," + walls.get(walls.size() - 1).posY + "," + walls.get(walls.size() - 1).velX +",wall" + blType + ".png," + walls.get(walls.size() - 1).trans + ";";
					send = data.getBytes();
					sendPacket = new DatagramPacket(send, send.length, ip, port);
					send = new byte[1024];
					try {
						sock.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
