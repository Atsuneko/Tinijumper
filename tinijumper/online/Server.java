package jisaneko.tinijumper.online;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Server implements Runnable{

	public static final int MAXPL = 4;

	DatagramSocket sock;
	ArrayList<User> users = new ArrayList<User>();
	byte[] receive = new byte[1024];
	byte[] send = new byte[1024];

	//int port;

	int minPlayers;
	boolean started = false;

	public Server(int port, int min){
		//this.port = port;

		if(min >= 2){
			minPlayers = min;
		} else{
			minPlayers = 2;
		}

		try {
			sock = new DatagramSocket(port);
			try {
				System.out.println(InetAddress.getLocalHost().getHostAddress() + " " + sock.getLocalPort());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		} catch (SocketException e){
			JOptionPane.showMessageDialog(null, "SERVER COULD NOT BE CONSTRUCTED", "Join denied", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
		}
	}

	public void run(){
		DatagramPacket receivePacket;
		DatagramPacket sendPacket;

		String data = null;

		while(true){
			receivePacket = new DatagramPacket(receive, receive.length);
			receive = new byte[1024];
			try {
				sock.receive(receivePacket);
				data = new String(receivePacket.getData());
			} catch (IOException e) {
				e.printStackTrace();
			}

			boolean allowed = false;
			for(User u : users){
				if(receivePacket.getAddress().equals(u.ip) && receivePacket.getPort() == u.port) allowed = true;
			}

			if(data.substring(0, 3).equals("new") && users.size() < MAXPL){
				System.out.println("Connected!");
				users.add(new User(sock, users, receivePacket.getAddress(), receivePacket.getPort()));
			} else if (data.substring(0, 3).equals("new") && users.size() >= MAXPL){
				send = "serverfull".getBytes();
				sendPacket = new DatagramPacket(send, send.length, receivePacket.getAddress(), receivePacket.getPort());
				send = new byte[1024];
				try {
					sock.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(allowed){
				for(User u : users){
					send = data.getBytes();
					sendPacket = new DatagramPacket(send, send.length, u.ip, u.port);
					send = new byte[1024];

					try {
						sock.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}



			if(users.size() == minPlayers && !started){
				for(User u : users){
					send = "start".getBytes();
					sendPacket = new DatagramPacket(send, send.length, u.ip, u.port);
					send = new byte[1024];

					try {
						sock.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				started = true;
			}
		}
	}

} class User{

	DatagramSocket sock;
	ArrayList<User> users;
	byte[] receive = new byte[1024];
	byte[] send = new byte[1024];

	InetAddress ip;
	int port;

	public User(DatagramSocket sock, ArrayList<User> users, InetAddress ip, int port){
		this.sock = sock;
		this.users = users;
		this.ip = ip;
		this.port = port;
	}
}
