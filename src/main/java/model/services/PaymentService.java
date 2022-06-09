package model.services;

import java.util.List;

import model.dao.impl.PaymentDAO;
import model.entities.Payment;

public class PaymentService {
	private PaymentDAO paymentDAO;

	public PaymentService() {
		this.paymentDAO = new PaymentDAO();
	}

	public List<Payment> findAll() {
		return paymentDAO.findAll();
	}

	public Long save(Payment payment) {

		return paymentDAO.save2(payment);

	}

	public Payment update(Payment payment) {
		return paymentDAO.update(payment);
	}

}
