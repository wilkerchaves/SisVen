package model.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import model.dao.EntitiesDAO;
import model.entities.Order;
import utils.HibernateUtils;

public class OrderDAO implements EntitiesDAO<Order> {
	private Session session;
	
	@Override
	public Order save(Order order) {
		System.out.println(order);
		session = HibernateUtils.getSessionFactory().openSession();
		session.save(order);
		session.close();
		return null;
	}
	
	public Order saveOrUpdate(Order order) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.saveOrUpdate(order);
		session.close();
		return null;
	}

	@Override
	public Order update(Order order) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.update(order);
		session.merge(order);
		session.flush();
		session.close();;
		return null;
	}

	@Override
	public Order find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Order> findAll() {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		List<Order> list = session.createQuery("from Order").getResultList();
		session.close();
		return list;
	}

	@Override
	public void delete(Order order) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(order);
		session.flush();
		session.close();
		
	}

}
