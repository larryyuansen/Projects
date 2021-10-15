package services.models;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "product")
public class ProductBean implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ProductBean() {}
	
	private String id;
	private String name;
//	private String DESCRIPTION;
//	private int QTY;
	private double price;
//	private double MSRP;
//	private int CATID;
//	private int VENID;
	
	//get
	public String getID() {return id;}
	public String getName() {return name;}
//	public String getDescription() {return DESCRIPTION;}
//	public int getQty() {return QTY;}
	@XmlElement(name = "price")
	public double getCost() {return price;}
//	public double getMsrp() {return MSRP;}
//	public int getCatID() {return CATID;}
//	public int getVenID() {return VENID;}
	
	//set
	public void setID(String idin) { this.id = idin;}
	public void setName(String name) { this.name = name;}
//	public void setDescription(String description) { DESCRIPTION = description;}
//	public void setQty(int qty) { QTY = qty;}
	public void setCost(double cost) { this.price = cost;}
//	public void setMsrp(double msrp) { MSRP = msrp;}
//	public void setCatID(int cartid) { CATID = cartid;}
//	public void setVenID(int venid) { VENID = venid;}
//	
	
//	//toString
//	public String toString()
//	{
//		return String.format("ID in %s :\n"
//			      + "- Name = %s\n"
//			      + "- Description = %s\n"
//			      + "- Qty = %d\n"
//			      + "- Cost$ = %f\n"
//			      + "- Msrp = %f\n"
//			      + "- CartID = %d\n"
//			      + "- VenID = %d\n"
//			      , ID,NAME,DESCRIPTION,QTY,PRICE,MSRP,CATID,VENID);
//	}
	
	//toString
	public String toString()
	{
		return String.format("ID in %s :\n"
			      + "- Name = %s\n"
			      + "- Cost$ = %f\n"
			      , id,name,price);
	}

}
