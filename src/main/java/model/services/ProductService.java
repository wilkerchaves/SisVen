package model.services;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.impl.ProductDAO;
import model.entities.Product;


public class ProductService {
	private ProductDAO productDAO;
	
	public ProductService() {
		this.productDAO =  new ProductDAO();
	}
	
	public void setProductDAO(ProductDAO productDAO) {
		
	}
	
	public List<Product> findAll(){
		return productDAO.findAll();
	}
	public Product saveOrUpdate(Product product) {
		if(product.getId()==null) {
			return productDAO.save(product);
		}else {
			return productDAO.update(product);
		}
	}
	public Product update(Product product) {
		return productDAO.update(product);
	}
	
	public void remove(Product product) {
		productDAO.delete(product);
	}

}
