package gui;

import java.io.Serializable;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.Utils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Product;
import model.entities.Production;
import model.exceptions.ValidationException;
import model.services.ProductService;
import model.services.ProductionService;

public class ProductionFormController implements Initializable, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private ProductService productService;
	private ProductionService productionService;
	private Production production;
	private List<Product> products;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	@FXML
	private TextField textFieldProductCode;
	@FXML
	private TextField textFieldQuantity;
	@FXML
	private Label labelCode;
	@FXML
	private Label labelQuantity;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;

	public void onBtnSaveAction(ActionEvent event) {
		if (production == null) {
			throw new IllegalStateException("Production entity was null");
		}
		if (products.isEmpty()) {
			throw new IllegalStateException("List of product is empty");
		}
		try {
			production = getFormData();
			Product product = new Product();
			product = production.getProduct();
			product.getProductions().add(production);
			productionService.saveOrUpdate(production);
			notifyDataChange();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorsMessages(e.getErrors());
		} catch (Exception e) {
			Alerts.showAlert("Erro ao adicionar produção", null, e.getMessage(), AlertType.ERROR);

		}

	}

	private Production getFormData() {
		ValidationException exception = new ValidationException("Erro de validação");
		Production production = new Production();
		production.setId(null);
		Product product = new Product();
		if (textFieldProductCode.getText() == null || textFieldProductCode.getText().trim().equals("")) {
			exception.addError("ProductCode", "O campo código não pode ficar vazio");
		} else {
			if (existsCodeOnList(Utils.tryParseToLong(textFieldProductCode.getText()))) {
				product = findProdOnListByCode(Utils.tryParseToLong(textFieldProductCode.getText()));
				production.setProduct(product);

			} else {

				exception.addError("Product", "Produto não encontrado");

			}
		}
		if (textFieldQuantity.getText() == null || textFieldQuantity.getText().trim().equals("")) {
			exception.addError("quantity", "O campo quantidade não pode ficar vazio");
		}
		production.setQuantity(Utils.tryParseToInt(textFieldQuantity.getText()));
		production.setMoment(LocalDateTime.now(ZoneId.systemDefault()));
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return production;
	}

	private boolean existsCodeOnList(Long code) {
		for (Product p : products) {
			if (p.getCode().equals(code)) {
				return true;
			}
		}
		return false;
	}

	private Product findProdOnListByCode(Long code) {
		for (Product p : products) {
			if (p.getCode().equals(code))
				return p;
		}
		return null;
	}
	
//	private void showList() {
//		for(Product p : products) {
//			System.out.println(p);
//		}
//	}

	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private void notifyDataChange() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}

	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// TODO Auto-generated method stub
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldQuantity);
		Constraints.setTextFieldMaxLength(textFieldProductCode, 10);
		
	}

	public void updateFormData() {
		if (production == null) {
			throw new IllegalStateException("Production entity was null on updateFormData");
		}

		textFieldQuantity.setText(String.valueOf(production.getQuantity()));
		textFieldProductCode
				.setText(String.valueOf((production.getProduct() == null) ? "" : production.getProduct().getCode()));
	}

	private void setErrorsMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("ProductCode")) {
			labelCode.setText(errors.get("ProductCode"));

		}
		if (fields.contains("quantity")) {
			labelQuantity.setText(errors.get("quantity"));
		}

		if (fields.contains("Product")) {
			Alerts.showAlert("Produto não encontrado", null, "Não existe um produto para o codigo informado",
					AlertType.ERROR);
		}
	}

	public void setProduction(Production production) {
		this.production = production;

	}

	public void setProductsList(ObservableList<Product> products) {
		this.products = products;

	}

	public ProductionService getProductionService() {
		return productionService;

	}

	public void setProductionService(ProductionService productionService) {
		this.productionService = productionService;

	}

	public ProductService getProductService() {
		return productService;
		
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
		
	}

}
