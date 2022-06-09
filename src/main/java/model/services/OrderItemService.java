package model.services;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.mysql.cj.jdbc.exceptions.SQLError;

import model.dao.impl.OrderItemDAO;
import model.entities.OrderItem;

public class OrderItemService {
	private OrderItemDAO orderItemDAO;

	public OrderItemService() {
		this.orderItemDAO = new OrderItemDAO();
	}

	public void setOrderItemDAO(OrderItemDAO orderItemDAO) {

	}
	
	public Object saveAll(Collection<OrderItem> items) {
		return orderItemDAO.saveAll(items);
	}

	public List<OrderItem> findAll() {
		return orderItemDAO.findAll();
	}

	public OrderItem save(OrderItem orderItem) {
		return orderItemDAO.save(orderItem);
	}
	
	public OrderItem update(OrderItem orderItem) {
		return orderItemDAO.update(orderItem);
	}

}
