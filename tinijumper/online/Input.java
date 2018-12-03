package jisaneko.tinijumper.online;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import jisaneko.tinijumper.game.entities.Obstacle;

public class Input{

	DatagramSocket sock;
	ArrayList<OnlineKitty> plyrs;
	public ArrayList<Obstacle> walls;
	byte[] receive = new byte[1024];
	
	InetAddress ip; int port;
	
	public boolean openSpace = true;
	public boolean serverExists = true;
	
	public boolean toStartGame = false;
	
	public int serverHeight = 0;

	public Input(DatagramSocket sock, ArrayList<OnlineKitty> plyrs, ArrayList<Obstacle> walls, InetAddress ip, int port){
		this.sock = sock;
		this.plyrs = plyrs;
		this.ip = ip;
		this.port = port;
		this.walls = walls;
	}

	public void run(){
		DatagramPacket receivePacket;

		receivePacket = new DatagramPacket(receive, receive.length);
		receive = new byte[1024];
		try {
			sock.setSoTimeout(100);
			sock.receive(receivePacket);
		} catch (IOException e){
			//serverExists = false;
		}
		
		String data = new String(receivePacket.getData());

		if(data.substring(0, 10).equals("serverfull")) openSpace = false;
		
		if(data.substring(0, 5).equals("start")){
			toStartGame = true;
		}
		
		if(data.substring(0, 7).equals("height,")){
			serverHeight = Integer.parseInt(data.substring(7, data.length()));
		}
		
		if(data.substring(0, 6).equals("block,")){
			int index = 6;
			String tempX = "";
			while(data.charAt(index) != ','){
				tempX += data.charAt(index);
				index++;
			}
			index++;
			String tempY = "";
			while(data.charAt(index) != ','){
				tempY += data.charAt(index);
				index++;
			}
			index++;
			String tempType = "";
			while(data.charAt(index) != ','){
				tempType += data.charAt(index);
				index++;
			}
			index++;
			String tempT = "";
			while(data.charAt(index) != ';'){
				tempT += data.charAt(index);
				index++;
			}
			
			int pX = Integer.parseInt(tempX), pY = Integer.parseInt(tempY);
			boolean trans = Boolean.parseBoolean(tempT);
			
			walls.add(new Obstacle(pX, pY, 1, tempType));
			walls.get(walls.size() - 1).trans = trans;
		}
		
		if(data.substring(0, 5).equals("plyr,")){
			int index = 5;
			String tempUN = "";
			while(data.charAt(index) != '/'){
				tempUN += data.charAt(index);
				index++;
			}
			index++;
			
			String tempIP = "";
			while(data.charAt(index) != ':'){
				tempIP += data.charAt(index);
				index++;
			}
			index++;

			String portStr = "";
			while(data.charAt(index) != ','){
				portStr += data.charAt(index);
				index++;
			}
			index++;
			int tempPort = Integer.parseInt(portStr);

			try {
				if(!(tempIP.equals(InetAddress.getLocalHost().getHostAddress()) && tempPort == sock.getLocalPort())){
					String xStr = "", yStr = "", vStr = "", botStr = "";
					while(data.charAt(index) != ','){
						xStr += data.charAt(index);
						index++;
					}
					index++;
					while(data.charAt(index) != ','){
						yStr += data.charAt(index);
						index++;
					}
					index++;
					while(data.charAt(index) != ','){
						vStr += data.charAt(index);
						index++;
					}
					index++;
					while(data.charAt(index) != ';'){
						botStr += data.charAt(index);
						index++;
					}

					int tempX = Integer.parseInt(xStr), tempY = Integer.parseInt(yStr); double tempVel = Double.parseDouble(vStr);
					boolean tempCol = Boolean.parseBoolean(botStr);

					if(plyrs.size() == 0) {
						try {
							plyrs.add(new OnlineKitty(1, tempX, tempY, InetAddress.getByName(tempIP), tempPort));
							plyrs.get(plyrs.size() - 1).velX = tempVel;
							plyrs.get(plyrs.size() - 1).botCol = tempCol;
							plyrs.get(plyrs.size() - 1).username = tempUN;
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
					}
					else{
						boolean anyNew = true;
						for(OnlineKitty k : plyrs){
							if(k.ip.getHostAddress().equals(tempIP) && k.port == tempPort) {
								k.posX = tempX; k.posY = tempY; k.velX = tempVel; k.botCol = tempCol;
								anyNew = false;
							}
						}
						if(anyNew){
							try {
								plyrs.add(new OnlineKitty(1, tempX, tempY, InetAddress.getByName(tempIP), tempPort));
								plyrs.get(plyrs.size() - 1).velX = tempVel;
								plyrs.get(plyrs.size() - 1).botCol = tempCol;
								plyrs.get(plyrs.size() - 1).username = tempUN;
							} catch (UnknownHostException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (NumberFormatException | UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}

}