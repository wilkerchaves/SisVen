package model.dao;

import model.dao.impl.CategoryDAO;
import model.dao.impl.ProductDAO;

public class DAOFactory {

	public static ProductDAO createProductDAO() {
		return new ProductDAO();
	}
	
	public static CategoryDAO createCategoryDAO() {
		return new CategoryDAO();
	}
}
