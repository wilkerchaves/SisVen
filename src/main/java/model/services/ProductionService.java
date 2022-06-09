package model.services;

import java.util.List;

import model.dao.impl.ProductionDAO;
import model.entities.Production;


public class ProductionService {
	private ProductionDAO productionDAO;
	
	public ProductionService() {
		this.productionDAO =  new ProductionDAO();
	}
	
	public void setProductionDAO(ProductionDAO productionDAO) {
		
	}
	
	public List<Production> findAll(){
		return productionDAO.findAll();
	}
	public Production saveOrUpdate(Production production) {
		if(production.getId()==null) {
			return productionDAO.save(production);
		}else {
			return productionDAO.update(production);
		}
	}

}
