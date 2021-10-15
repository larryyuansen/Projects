package services.models;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "products")
public class ProductCollection implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	public ProductCollection() {}
	

	private List<ProductBean> PRODUCTS;
	
	@XmlElement(name="product")
	public List<ProductBean> getProducts() {return PRODUCTS;}

	public void setProducts(List<ProductBean> products) {PRODUCTS = products;}

}
