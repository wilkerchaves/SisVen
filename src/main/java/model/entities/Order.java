package model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import enums.OrderStatus;

@Entity
@Table(name = "tb_order")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NaturalId
	private LocalDateTime moment;
	
	private Integer status;
	
	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY,optional = true)
	@JoinColumn(name = "payment_id")
	private Payment payment;
	
	@OneToMany(mappedBy = "id.order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<OrderItem> items = new HashSet<>();
	
	public Order() {
		
	}
	public Order(Long id, LocalDateTime moment, OrderStatus orderStatus) {
		
		this.id = id;
		this.moment = moment;
		setOrderStatus(orderStatus);
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getMoment() {
		return moment;
	}
	public void setMoment(LocalDateTime moment) {
		this.moment = moment;
	}
	
	public OrderStatus getOrderStatus() {
		return OrderStatus.toEnum(status);
	}
	public void setOrderStatus(OrderStatus status) {
		if(status!=null) {
			this.status = status.getCode();
		}
	}
	
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	public Set<OrderItem> getItems() {
		return items;
	}
	
	public OrderItem addItem(OrderItem item) {
		if(items.contains(item)) {
			OrderItem oi = items.stream().filter(i->i.getProduct().getCode()==item.getProduct().getCode()).findFirst().get();
			oi.setQuantity(oi.getQuantity()+item.getQuantity());
			System.out.println(item.getDiscount());
			oi.setDiscount(item.getDiscount()+oi.getDiscount());
			System.out.println(oi.getDiscount());
			items.add(oi);
			return oi;
			
		}else {
			items.add(item);
		}
		
		return item;
	}
	
	public void removeItem(OrderItem item) {
		if(items.contains(item)) {
			items.remove(item);
		}else {
			throw new IllegalStateException();
		}
		
	}
	
	
	public Double getTotal() {
		Double sum = 0.0;
		for(OrderItem item:items) {
			sum+=item.getSubTotal();
		}
		return sum;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}
	@Override
	public String toString() {
		return "Order [id=" + id + ", moment=" + moment + ", status=" + status + "";
	}
	
	
	
	
	
	
}
