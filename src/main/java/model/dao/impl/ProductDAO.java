package model.dao.impl;

import java.util.List;

import org.hibernate.Session;

import model.dao.EntitiesDAO;
import model.entities.Product;
import utils.HibernateUtils;

public class ProductDAO implements EntitiesDAO<Product> {
	private Session session;

	@Override
	public Product save(Product product) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.save(product);
		session.close();
		return null;
	}

	@Override
	public Product update(Product product) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.merge(product);
		session.flush();
		session.close();		
		return null;
	}

	@Override
	public Product find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Product> findAll() {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		List<Product> list =  session.createQuery("from Product").getResultList();
		session.close();
		return list;
	}

	@Override
	public void delete(Product product) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(product);
		session.flush();
		session.close();
	}

	

}
