import java.awt.Color;
import java.awt.Rectangle;

@SuppressWarnings("serial")
public class Brick extends Rectangle{
 Color brickColor;
 double HP;
 int R;
 int G;
 int B;
 
 public Brick(int newX, int newY, int newWidth, int newHeight){
  super(newX, newY, newWidth, newHeight);
  //brickColor = new Color(0, 128, 255);
 }

 public Brick(int newX, int newY){
  this(newX, newY, 10, 10);
 }

 public Brick(){
  this(0,0,10,10);
 }

 public void Hit(){
	 this.brickColor=new Color(R,G,B,(int)(HP)+20);
 }
 public void setColor(int x, int y, int z){
	 this.R=x;
	 this.G=y;
	 this.B=z;
	 this.brickColor=new Color(R,G,B);
 }
 public Color getColor(){ return brickColor; }

}