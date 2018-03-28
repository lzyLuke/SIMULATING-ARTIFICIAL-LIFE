package application;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import simulate.Hex;
import web.displayHex;

import java.lang.Double;
/**
 * polyHex extends from Polygon, it can be shown on our scrollPane. Given a column number and a row number,
 * it will calculate the 6 points' coordinates and construct a polygon object using them.
 * Furthermore, it also set the appearance of polygon, their color, their width and so on.
 * @author luke
 *
 */
public class PolyHex extends Polygon {
	int row;
	int column;
	int strokeWidth=3;
	static double radius=30;
	static double initialx=50;
	static double initialy=50;
	static PolyHex lastClicked=null; // a static variable to record the last clicked PolyHex
	double locationx;
	double locationy;
	Polygon face[]=new Polygon[6];
	Double []sixPoints;
	Hex hex;
	public displayHex displayhex;
	public PolyHex(int c,int r){
		this.row=r;
		this.column=c;
		sixPoints=new Double[12];
		locationx=initialx+Math.sqrt(3)*radius*column;	// the central coordinates of the location
		locationy=initialy+radius*2*row-column*radius;
		
		sixPoints[0]=locationx-radius/2;
		sixPoints[1]=locationy+radius*Math.sqrt(3)/2;
		
		sixPoints[2]=locationx+radius/2;
		sixPoints[3]=locationy+radius*Math.sqrt(3)/2;
		
		sixPoints[4]=locationx+radius;
		sixPoints[5]=locationy;
		
		sixPoints[6]=locationx+radius/2;
		sixPoints[7]=locationy-radius*Math.sqrt(3)/2;
		
		sixPoints[8]=locationx-radius/2;
		sixPoints[9]=locationy-radius*Math.sqrt(3)/2;
		
		sixPoints[10]=locationx-radius;
		sixPoints[11]=locationy;
		
		getPoints().addAll(sixPoints);
		setFill(Color.TRANSPARENT);
		this.setStrokeWidth(strokeWidth);
		setStroke(Color.web("#e4d6d6"));
		
		
		for(int i=0;i<6;i++)
			face[i]=direction(i);
	}
	
	public int getColumn(){
		return column;
	}
	public int getRow(){
		return row;
	}
	public double getLocationX()
	{
		return locationx;
	}
	public double getLocationY()
	{
		return locationy;
	}
	public void setInfo(Hex hex)
	{
		this.hex=hex;
	}
	public Hex getInfo(Hex hex)
	{
		return hex;
	}
	public Double[] getAllPoints()
	{
		return sixPoints;
	}
	/**
	 *  If this polygon represents a critter, then it will have its face direction,
	 *  we use a polygon to represent the direction.
	 * @param face the facing direction (0~5)
	 * @return a polygon representing the facing direction on this PolyHex
	 */
	private Polygon direction(int face)
	{
		Polygon triangle = new Polygon();
		Double threePoints[] = new Double[6];
		Double origin[] = new Double[6];
			origin[0]=locationx;			
			origin[1]=locationy+0.7*radius;
			
			origin[2]=locationx-radius*0.4;
			origin[3]=locationy+radius*0.5;
			
			origin[4]=locationx+radius*0.4;
			origin[5]=locationy+radius*0.5;
			
			// we use a formula to represent a rotation around a certain point. 
			threePoints[0]=(origin[0]-locationx)*Math.cos(Math.PI/3 * face) + (origin[1]-locationy)*Math.sin(Math.PI/3*face)+locationx;
			threePoints[1]=-(origin[0]-locationx)*Math.sin(Math.PI/3 * face) + (origin[1]-locationy)*Math.cos(Math.PI/3*face)+locationy;
				
			threePoints[2]=(origin[2]-locationx)*Math.cos(Math.PI/3 * face) + (origin[3]-locationy)*Math.sin(Math.PI/3*face)+locationx;
			threePoints[3]=-(origin[2]-locationx)*Math.sin(Math.PI/3 * face) + (origin[3]-locationy)*Math.cos(Math.PI/3*face)+locationy;
				
			threePoints[4]=(origin[4]-locationx)*Math.cos(Math.PI/3 * face) + (origin[5]-locationy)*Math.sin(Math.PI/3*face)+locationx;
			threePoints[5]=-(origin[4]-locationx)*Math.sin(Math.PI/3 * face) + (origin[5]-locationy)*Math.cos(Math.PI/3*face)+locationy;
	
		triangle.getPoints().addAll(threePoints);
		triangle.setStroke(Color.web("#e4d6d6"));
		triangle.setFill(Color.web("#e4d6d6"));
		triangle.setVisible(false);
		triangle.setStrokeWidth(strokeWidth*0.5);
		
		return triangle;
	}
	/**
	 * show the facing direction on the GUI.
	 */
	public void setFace(int dir)
	{
		dir=(dir%6+6)%6;
		for(int i=0;i<6;i++)
			face[i].setVisible(false);
		face[dir].setVisible(true);
	}
	/**
	 * disable all face polygon on the graph.
	 */
	public void blockFace()
	{
		for(int i=0;i<6;i++)
			face[i].setVisible(false);
	}

}
