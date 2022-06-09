package model.dao.impl;

import java.util.List;

import org.hibernate.Session;

import model.dao.EntitiesDAO;
import model.entities.Production;
import utils.HibernateUtils;

public class ProductionDAO implements EntitiesDAO<Production> {
	private Session session;

	@Override
	public Production save(Production production) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.save(production);
		session.close();
		return null;
	}

	@Override
	public Production update(Production production) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.merge(production);
		session.flush();
		session.close();		
		return null;
	}

	@Override
	public Production find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Production> findAll() {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		List<Production> list =  session.createQuery("from Production").getResultList();
		session.close();
		return list;
	}

	@Override
	public void delete(Production production) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(production);
		session.flush();
		session.close();

	}

}
