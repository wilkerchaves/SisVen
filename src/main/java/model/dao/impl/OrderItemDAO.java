package model.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.mysql.cj.jdbc.exceptions.SQLError;

import org.hibernate.Session;

import model.dao.EntitiesDAO;
import model.entities.OrderItem;
import utils.HibernateUtils;

public class OrderItemDAO implements EntitiesDAO<OrderItem> {
	private Session session;

	@Override
	public OrderItem save(OrderItem orderItem) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(orderItem);
		session.flush();
		session.close();
		return null;
	}

	public Object saveAll(Collection<OrderItem> items) {
		session = HibernateUtils.getSessionFactory().openSession();
		for (OrderItem item : items) {
			session.save(item);
		}
		session.close();
		return null;

	}

	@Override
	public OrderItem update(OrderItem orderItem){
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.merge(orderItem);
		session.flush();
		session.close();
		return null;
	}

	@Override
	public OrderItem find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<OrderItem> findAll() {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		List<OrderItem> list = session.createQuery("from OrderItem").getResultList();
		session.close();
		return list;
	}

	@Override
	public void delete(OrderItem orderItem) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(orderItem);
		session.flush();
		session.close();

	}

}
