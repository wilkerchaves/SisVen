package gui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.persistence.criteria.Selection;

import org.hibernate.boot.jaxb.internal.stax.LocalSchema;

import application.Main;
import enums.OrderStatus;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.Order;
import model.entities.OrderItem;
import model.entities.Product;
import model.exceptions.ValidationException;
import model.services.OrderItemService;
import model.services.OrderService;
import model.services.ProductService;

public class SaleFormController implements Serializable, Initializable, DataChangeListener {

	int count = 0;

	private static final long serialVersionUID = 1L;

	List<DataChangeListener> listeners = new ArrayList<DataChangeListener>();

	private Order order;

	private ProductService productService;

	private OrderService orderService;

	private OrderItemService orderItemService;

	private ObservableList<Product> obsList;

	@FXML
	private VBox mainVBox;

	@FXML
	private HBox secundaryHBox;

	@FXML
	private VBox codeVBox;

	@FXML
	private VBox productVBox;

	@FXML
	private VBox priceVBox;

	@FXML
	private VBox quantityVBox;

	@FXML
	private VBox discountVBox;

	@FXML
	private VBox totalVBox;

	@FXML
	private TableColumn<OrderItem, String> columnCode;

	@FXML
	private TableColumn<OrderItem, String> columnName;

	@FXML
	private TableColumn<OrderItem, Double> columnUnitValue;

	@FXML
	private TableColumn<OrderItem, Integer> columnQuantity;

	@FXML
	private TableColumn<OrderItem, String> columnTotalDiscount;

	@FXML
	private TableColumn<OrderItem, String> columnTotalValue;

	@FXML
	private TableColumn<OrderItem, String> columnValueOfDiscount;

	@FXML
	private TextField codeTextField;

	@FXML
	private TextField productTextField;

	@FXML
	private TextField priceTextField;

	@FXML
	private TextField quantityTextField;

	@FXML
	private TextField discountTextField;

	@FXML
	private TextField valueOfDiscountTextField;

	@FXML
	private TextField totalTextField;

	@FXML
	private TextField totalOrderTextField;

	@FXML
	private Button btnAddItem;

	@FXML
	private Button btnSave;

	@FXML
	private TableView<OrderItem> tableView;

	public void onBtnSaveAction(ActionEvent e) {
		if (order.getItems().isEmpty()) {
			closeModal();
		}
		orderService.save(order);
		for (OrderItem item : order.getItems()) {
			orderItemService.save(item);
		}
		closeModal();

	}

	@Override
	public void onDataChange() {
		updateItemsView();

	}

	public void updateItemsView() {
		ObservableList<OrderItem> items = FXCollections.observableArrayList(order.getItems());
		tableView.setItems(items);
		totalOrderTextField.setText(Utils.getFormatedSubTotal(order.getTotal()));
		tableView.refresh();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(codeTextField);
		Constraints.setTextFieldInteger(quantityTextField);
		columnCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getCode().toString()));
		columnName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getName()));
		columnQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		columnUnitValue.setCellValueFactory(new PropertyValueFactory<>("price"));
		columnTotalDiscount.setCellValueFactory(
				c -> new SimpleStringProperty(Utils.getFormatedTextTemplateDiscount(c.getValue().getDiscount())));
		columnValueOfDiscount.setCellValueFactory(
				c -> new SimpleStringProperty(Utils.getFormatedValueOfDiscount(c.getValue().getDiscount(),
						c.getValue().getPrice(), c.getValue().getQuantity())));
		columnTotalValue.setCellValueFactory(
				c -> new SimpleStringProperty(Utils.getFormatedSubTotal(c.getValue().getSubTotal())));

		TableViewSelectionModel<OrderItem> selectionModel = tableView.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		tableView.setSelectionModel(selectionModel);

	}

	public void addEventHandler() {
		clearTextFields();
		codeTextField.requestFocus();
		discountTextField.setText("0");
		Scene mainScene = Main.getMainScene();
		mainScene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode() == KeyCode.F4) {
				closeModal();
			}

		});

		tableView.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.DELETE) {
				OrderItem item = tableView.getSelectionModel().getSelectedItem();
				removeItemFromOrder(item);
				notifyDataChange();
			}
		});

		codeTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				onKeyEnterPressed();
			}
			if (e.getCode() == KeyCode.ESCAPE) {
				addEventHandler();
			}
		});
		quantityTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				if (checkTextFieldEmpty(quantityTextField) || checkTextFieldValueIsZero(quantityTextField)) {
					Alerts.showAlert("Quantidade inválida", null, "A quantidade informada não é valida",
							AlertType.ERROR);
				} else {
					discountTextField.requestFocus();
				}
			}

		});
		discountTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				if (checkTextFieldEmpty(codeTextField)) {
					Alerts.showAlert("Campo vazio", null, "O campo código não pode ficar vazio", AlertType.ERROR);
				} else {
					valueOfDiscountTextField.setText(
							Utils.getFormatedValueOfDiscount(Utils.tryParseToDouble(discountTextField.getText()),
									Utils.tryParseToDouble(priceTextField.getText()),
									Utils.tryParseToInt(quantityTextField.getText())));
					addItemToOrder();
					btnAddItem.requestFocus();
				}
			}
		});

		btnAddItem.setOnAction(e -> {
			addEventHandler();
			notifyDataChange();
		});

	}

	private void onKeyEnterPressed() {
		if (checkTextFieldEmpty(codeTextField)) {
			count++;
			if (count == 2) {
				btnSave.requestFocus();
				count = 0;
			}
		} else {
			getCodeValue();
		}
	}

	private void getCodeValue() {
		Long code = Utils.tryParseToLong(codeTextField.getText());
		Optional<Product> p = getByCode(code);
		if (p.isEmpty()) {
			Alerts.showAlert("Produto inexistente", null, "Não existe um produto com este codigo", AlertType.ERROR);
		} else {
			productTextField.setText(p.get().getName());
			priceTextField.setText(String.valueOf(p.get().getPrice()));
			quantityTextField.requestFocus();

		}

	}

	private void clearTextFields() {
		codeTextField.clear();
		productTextField.clear();
		quantityTextField.clear();
		discountTextField.clear();
		priceTextField.clear();
		totalTextField.clear();
	}

	private boolean checkTextFieldEmpty(TextField textField) {
		if (textField.getText().trim().equals("") || textField.getText() == null) {
			return true;
		}
		return false;
	}

	private boolean checkTextFieldValueIsZero(TextField textField) {
		if (textField.getText().trim().equals("0") || Utils.tryParseToInt(textField.getText()) == 0) {
			return true;
		}
		return false;
	}

	private OrderItem getFormData() {
		OrderItem oi = new OrderItem();
		Product p = getByCode(Utils.tryParseToLong(codeTextField.getText())).get();
		oi.setOrder(order);
		oi.setProduct(p);
		oi.setQuantity(Utils.tryParseToInt(quantityTextField.getText()));
		oi.setDiscount(Utils.tryParseToDouble(discountTextField.getText()));
		oi.setPrice(p.getPrice());
		return oi;
	}

	private void addItemToOrder() {
		OrderItem oi = getFormData();
		totalTextField.setText(Utils.getFormatedSubTotal(oi.getSubTotal()));
		order.addItem(oi);
	}

	private void removeItemFromOrder(OrderItem item) {
		Optional<ButtonType> answer = Alerts.showConfirmation("Confirmar exclusão",
				"Tem certeza que deseja excluir este produto?");
		if (answer.get() == ButtonType.OK) {
			try {
				order.removeItem(item);
			} catch (IllegalStateException e) {
				Alerts.showAlert("Produto inexistente", null, "Este produto não existe na lista de compras",
						AlertType.ERROR);
			}
		}

	}

	private void closeModal() {
		try {
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			mainVBox.getChildren().clear();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			MainViewController controller = loader.getController();
			VBox vBox = (VBox) scrollPane.getContent();
			mainVBox.getChildren().addAll(vBox.getChildren());
			controller.updateListOrders();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private Optional<Product> getByCode(Long code) {
		return obsList.stream().filter(p -> p.getCode() == code).findFirst();
	}

	public ObservableList<Product> getObsList() {
		return obsList;

	}

	public void setObsList(ObservableList<Product> obsList) {
		this.obsList = obsList;

	}

	public ProductService getProductService() {
		return productService;

	}

	public void setProductService(ProductService productService) {
		this.productService = productService;

	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		listeners.add(listener);
	}

	private void notifyDataChange() {
		for (DataChangeListener listener : listeners) {
			listener.onDataChange();
		}

	}

	public void setOrderItemService(OrderItemService orderItemService) {
		this.orderItemService = orderItemService;

	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;

	}

}
