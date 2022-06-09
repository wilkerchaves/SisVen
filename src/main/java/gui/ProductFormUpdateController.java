package gui;

import java.io.Serializable;
import java.net.URL;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.entities.Category;
import model.entities.Product;
import model.exceptions.ValidationException;
import model.services.CategoryService;
import model.services.ProductService;

public class ProductFormUpdateController implements Serializable, Initializable {

	private static final long serialVersionUID = 1L;
	private Product product;
	private ProductService productService;
	private CategoryService categoryService;
	private ObservableList<Category> categories;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();
	List<Product> products;

	@FXML
	private GridPane gridpane;

	@FXML
	private VBox vBoxOfCheckBox;

	@FXML
	private Label labelCode;
	@FXML
	private Label labelName;
	@FXML
	private Label labelPrice;
	
	@FXML
	private Label labelQuantity;
	
	@FXML
	private Label labelCategories;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;
	@FXML
	private TextField textFieldCode;
	@FXML
	private TextField textFieldName;
	@FXML
	private TextField textFieldPrice;
	
	@FXML
	private TextField textFieldQuantity;

	public void onBtnSaveAction(ActionEvent event) {
		if (product == null) {
			throw new IllegalStateException("Product entity was null");
		}
		if (productService == null || categoryService == null) {
			throw new IllegalStateException("productService or categoryService was null");
		}
		try {
			product = getFormData();
			List<Category> categories = getCategoriesChecked(vBoxOfCheckBox);		
			product.getCategorias().addAll(categories);
			productService.update(product);
			updateCategories(categories, product);
			notifyDataChange();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorsMessages(e.getErrors());
		} catch (Exception e) {
			Alerts.showAlert("Error saving product", null, e.getMessage(), AlertType.ERROR);

		}

	}

	private Product getFormData() {
		ValidationException exception = new ValidationException("Erro de validação");
		Product p = new Product();
		p.setId(product.getId());
		if (textFieldCode.getText().trim().equals("") || textFieldCode.getText() == null){
			exception.addError("code", "O campo código não pode ficar vazio");
		}
		
		p.setCode(Utils.tryParseToLong(textFieldCode.getText()));
		if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
			exception.addError("name", "O campo nome não pode ficar vazio");
		}
		p.setName(textFieldName.getText());

		
		if (textFieldPrice.getText() == null
				|| textFieldPrice.getText().trim().equals("")
				||Utils.tryParseToDouble(textFieldPrice.getText()) < 0) {
			exception.addError("price", "Preço invalido! Verifique o preço ou se o campo está em branco");
		}
		p.setPrice(Utils.tryParseToDouble(textFieldPrice.getText()));
		
		if(textFieldQuantity.getText().trim().equals("")||textFieldQuantity.getText()==null) {
			exception.addError("quantity", "quantidade não pode ficar vazio");
		}
		
		p.setQuantity(Utils.tryParseToInt(textFieldQuantity.getText()));

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return p;
	}


	private List<Category> getCategoriesChecked(VBox vb) {
		List<CheckBox> cb = getCheckedBox(vb);
		List<String> list = cb.stream().map(cat -> cat.getText()).toList();
		List<Category> ctg = categories.stream().filter(cat -> list.contains(cat.getName())).toList();
		return ctg;
	}

	private List<CheckBox> getCheckedBox(VBox vb) {
		ValidationException exception = new ValidationException("Erro de validação");
		List<CheckBox> cb = vb.getChildren().stream().map(checkBox -> (CheckBox) checkBox).filter(item -> item.isSelected())
				.toList();
		if(cb.size()==0||cb.isEmpty()) {
			exception.addError("categories", "Selecione pelo menos uma categoria");
		}
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return cb;

	}

	private void notifyDataChange() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChange();
		}

	}

	private void updateCategories(List<Category> categories, Product product) {
		for (Category cat : categories) {
			cat.getProducts().add(product);
			categoryService.saveOrUpdate(cat);
		}

	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// TODO Auto-generated method stub
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldCode);
		Constraints.setTextFieldInteger(textFieldQuantity);
		Constraints.setTextFieldMaxLength(textFieldName, 50);
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public void setCategories(ObservableList<Category> categories) {
		this.categories = categories;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public void updateFormData() {
		if (product == null) {
			throw new IllegalStateException("Product entity was null on updateFormData");
		}

		textFieldCode.setText(String.valueOf(product.getCode()));
		textFieldName.setText(product.getName());
		textFieldPrice.setText((product.getPrice() == null) ? "" : String.valueOf(product.getPrice()));
		textFieldQuantity.setText(String.valueOf(product.getQuantity()));
		for (Category category : categories) {
			vBoxOfCheckBox.getChildren().add(new CheckBox(category.getName()));
		}
	}

	private void setErrorsMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelName.setText(errors.get("name"));

		}
		if (fields.contains("code")) {
			labelCode.setText(errors.get("code"));
		}

		if (fields.contains("price")) {
			labelPrice.setText(errors.get("price"));
		}
		if(fields.contains("categories")) {
			labelCategories.setText(errors.get("categories"));
		}
		
		if(fields.contains("quantity")) {
			labelQuantity.setText(errors.get("quantity"));
		}
	}

}
