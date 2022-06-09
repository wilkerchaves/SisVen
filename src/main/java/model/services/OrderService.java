package model.services;

import java.util.List;

import model.dao.impl.OrderDAO;
import model.entities.Order;

public class OrderService {
	private OrderDAO orderDAO;

	public OrderService() {
		this.orderDAO = new OrderDAO();
	}

	public List<Order> findAll() {
		return orderDAO.findAll();
	}

	public Order save(Order order) {
		return orderDAO.save(order);

	}
	
	public Order saveOrUpdate(Order order) {
		return orderDAO.saveOrUpdate(order);

	}

	public Order update(Order order) {
		return orderDAO.update(order);
	}

}
