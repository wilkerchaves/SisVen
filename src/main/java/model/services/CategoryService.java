package model.services;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.impl.CategoryDAO;
import model.entities.Category;


public class CategoryService {
	private CategoryDAO categoryDAO;
	
	public CategoryService() {
		this.categoryDAO = new CategoryDAO();
	}
	
	public List<Category> findAll(){
		return categoryDAO.findAll();
	}
	public Category saveOrUpdate(Category category) {
		if(category.getId()==null) {
			return categoryDAO.save(category);
		}else {
			return categoryDAO.update(category);
		}
	}

}
