package enums;

public enum PaymentStatus {

	FINISHED(1,"Finalizado"),
	PENDENT(2,"Pendente"),
	CANCELED(3,"Cancelado");
	
	private int code;
	private String describ;
	
	private PaymentStatus(int code, String describ) {
		this.code=code;
		this.describ=describ;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getDescrib() {
		return describ;
	}
	
	public static PaymentStatus toEnum(Integer code) {
		if(code==null) {
			return null;
		}
		for(PaymentStatus x : PaymentStatus.values()) {
			if(code.equals(x.getCode())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Invalid ID to PaymentStatus");
	}
}
