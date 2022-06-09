package gui.utils;

import java.text.NumberFormat;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	static NumberFormat nf = NumberFormat.getCurrencyInstance();

	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Long tryParseToLong(String str) {
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double tryParseToDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static String getFormatedSubTotal(Double subTotal) {
		return nf.format(subTotal);
	}

	public static String getFormatedValueOfDiscount(Double discount, Double value, Integer quantity) {
		return nf.format((value * discount / 100) * quantity);

	}
	
	public static String getFormatedTextTemplateDiscount(Double discount) {
		return ""+discount +"%";
	}
}
