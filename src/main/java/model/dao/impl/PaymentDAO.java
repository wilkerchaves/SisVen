package model.dao.impl;

import java.util.List;

import org.hibernate.Session;

import model.dao.EntitiesDAO;
import model.entities.Payment;
import utils.HibernateUtils;

public class PaymentDAO implements EntitiesDAO<Payment> {
	private Session session;
	
	
	public Long save2(Payment payment) {
		session = HibernateUtils.getSessionFactory().openSession();
		Long id = (Long) session.save(payment);
		session.close();
		return id;
	}
	
	@Override
	public Payment save(Payment payment) {
		session = HibernateUtils.getSessionFactory().openSession();
		Long id = (Long) session.save(payment);
		session.close();
		return null;
	}

	@Override
	public Payment update(Payment payment) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.merge(payment);
		session.flush();
		session.close();;
		return null;
	}

	@Override
	public Payment find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Payment> findAll() {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		List<Payment> list = session.createQuery("from Payment").getResultList();
		session.close();
		return list;
	}

	@Override
	public void delete(Payment payment) {
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(payment);
		session.flush();
		session.close();
		
	}

}
