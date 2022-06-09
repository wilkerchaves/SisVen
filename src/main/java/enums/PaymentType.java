package enums;

public enum PaymentType {
	DINHEIRO(1, "Dinheiro"),
	CR�DITO(2,"Cartao de credito"),
	D�BITO(3, "Cartao de debito");
	
	
	private int code;
	private String describ;
	
	private PaymentType(int code, String describ) {
		this.code=code;
		this.describ=describ;
	}
	
	public int getCode() {
		return code;
	}

	public String getDescrib() {
		return describ;
	}
	
	public static PaymentType toEnum(Integer code) {
		if(code == null) {
			return null;
		}
		for(PaymentType x : PaymentType.values()) {
			if(code.equals(x.getCode())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Invalid ID to PaymentType: "+code);
	}
	
	
}
