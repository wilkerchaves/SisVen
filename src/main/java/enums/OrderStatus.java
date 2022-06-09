package enums;

public enum OrderStatus {

	WAITING_PAYMENT(1,"Aguardando pagamento"),
	PAID(2,"Pago"),
	CANCELED(3,"Cancelado");
	
	private int code;
	private String describ;
	
	private OrderStatus(int code, String describ) {
		this.code=code;
		this.describ=describ;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescrib() {
		return describ;
	}
	
	public static OrderStatus toEnum(Integer code) {
		if(code==null) {
			return null;
		}
		for(OrderStatus x : OrderStatus.values()) {
			if(code.equals(x.getCode())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Invalid ID to PaymentStatus");
	}
}
