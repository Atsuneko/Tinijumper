package jisaneko.tinijumper.game.source;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import javax.swing.ImageIcon;

public class GameObject{


	public int posX, posY, sizeX, sizeY;
	public double velX, velY;
	public boolean added = true;

	public Image image;

	AffineTransformOp op = null;


	public GameObject(String src, int pX, int pY, int sX, int sY){

		sizeX = sX; sizeY = sY;

		try {
			image = new ImageIcon(src).getImage();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		posX = pX; posY = pY;

	}



	public double endX(){

		return posX + sizeX;

	}



	public double endY(){

		return posY + sizeY;

	}



	public double midX(){

		return posX + sizeX/2;

	}



	public double midY(){

		return posY + sizeY/2;

	}



	public void setLocation(int x, int y){

		posX = x; posY = y;

	}



	public void setImage(String src){

		try {
			image = new ImageIcon(src).getImage();
		} catch (Exception e) {
			//ERROR MESSAGE
			System.exit(1);
		}

	}
	
	
	
	public void setImage(Image img){

		try {
			image = img;
		} catch (Exception e) {
			//ERROR MESSAGE
			System.exit(1);
		}

	}



	public void rotate(double angle){

		AffineTransform tx = AffineTransform.getRotateInstance(angle, midX(), midY());
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

	}



	public boolean contains(int pX, int pY){

		return (pX > posX && pX < endX() && pY > posY && pY < endY());

	}



	public boolean collides(GameObject o){

		return (endX() > o.posX && o.endX() > posX && endY() > o.posY && o.endY() > posY);

	}


	public boolean collides(int pX, int eX, int pY, int eY){

		return (endX() > pX && eX > posX && endY() > pY && eY > posY);

	}



	public boolean collidesBotCol(GameObject o){

		return (endX() > o.posX && o.endX() > posX && endY() >= o.posY && o.endY() >= posY);

	}

}
