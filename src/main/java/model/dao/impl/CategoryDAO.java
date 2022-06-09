package model.dao.impl;

import java.util.List;

import org.hibernate.Session;

import model.dao.EntitiesDAO;
import model.entities.Category;
import utils.HibernateUtils;

public class CategoryDAO implements EntitiesDAO<Category> {
	private Session session;
	
	@Override
	public Category save(Category category) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.save(category);
		session.close();
		return null;
	}

	@Override
	public Category update(Category category) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.getTransaction().begin();
		session.merge(category);
		session.flush();
		session.close();
		return null;
	}

	@Override
	public Category find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> findAll() {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		List<Category> list = session.createQuery("from Category").getResultList();
		session.close();
		return list;
	}

	@Override
	public void delete(Category category) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(category);
		session.flush();
		session.close();
		
	}

}
