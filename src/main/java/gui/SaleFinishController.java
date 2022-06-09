package gui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.persistence.PersistenceException;

import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.util.Util;

import org.hibernate.exception.GenericJDBCException;

import application.Main;
import enums.OrderStatus;
import enums.PaymentType;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import model.entities.Order;
import model.entities.OrderItem;
import model.entities.Payment;
import model.entities.Product;
import model.services.OrderItemService;
import model.services.OrderService;
import model.services.PaymentService;

public class SaleFinishController implements Serializable, Initializable, DataChangeListener {

	List<DataChangeListener> listeners = new ArrayList<DataChangeListener>();
	private ObservableList<Product> obsList;

	private Long orderId;

	private Order order;

	private OrderService orderService;

	private PaymentService paymentService;

	private OrderItemService orderItemService;

	private static final long serialVersionUID = 1L;

	@FXML
	private VBox vBox;

	@FXML
	private TableView<OrderItem> tableView;

	@FXML
	private TableColumn<OrderItem, String> tableColumnProduct;

	@FXML
	private TableColumn<OrderItem, Integer> tableColumnQtd;

	@FXML
	private TableColumn<OrderItem, String> tableColumnUnitPrice;

	@FXML
	private TableColumn<OrderItem, Integer> tableColumnDiscount;

	@FXML
	private TableColumn<OrderItem, String> tableColumnTotalPrice;

	@FXML
	private TextField codeTextField;

	@FXML
	private TextField nameTextField;

	@FXML
	private TextField quantityTextField;

	@FXML
	private TextField discountTextField;

	@FXML
	private TextField UnitValueTextField;

	@FXML
	private TextField totalValueTextField;

	@FXML
	private TextField totalTextField;

	@FXML
	private TextField subTotalTextField;

	@FXML
	private TextField codePaymentTextField;

	@FXML
	private TextField moneyChangeTextField;

	@FXML
	private ChoiceBox<PaymentType> describPaymentTextField;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private Button btAddItem;

	public void onBtnAddItemAction(ActionEvent e) {

			OrderItem oi = order.addItem(getFormData());
			try {
				orderItemService.update(oi);
			} catch (RuntimeException e1) {
				Throwable t = e1.getCause().getCause(); 
				if(t instanceof SQLException) {
					Alerts.showAlert("Erro ao atualizar lista de compras", null, "Erro ao atualizar lista de compras: "+t.getMessage(), AlertType.WARNING);
				}
			
				
			}
			notifyDataChange();

	}

	public void onBtnSaveAction(ActionEvent e) {
		if (tableView.getItems().isEmpty()) {
			Alerts.showAlert("Lista de produtos vazia", null, "Lista de produtos vazia não pode ficar vazia",
					AlertType.ERROR);
		}
		Payment payment = new Payment(null, PaymentType.toEnum(Utils.tryParseToInt(codePaymentTextField.getText())),
				order);
		paymentService.save(payment);
		order.setPayment(payment);
		order.setOrderStatus(OrderStatus.PAID);
		System.out.println(order.getItems());
		System.out.println(order.getPayment());
		System.out.println(order.getOrderStatus());
		orderService.update(order);
		Utils.currentStage(e).close();
		refreshHome();

	}

	private void clearTextFields() {
		codeTextField.clear();
		quantityTextField.clear();
		discountTextField.clear();
		nameTextField.clear();
		totalValueTextField.clear();
		UnitValueTextField.clear();

	}

	private OrderItem getFormData() {
		OrderItem oi = new OrderItem();
		Long code = Utils.tryParseToLong(codeTextField.getText());
		Optional<Product> product = obsList.stream().filter(p -> p.getCode() == code).findFirst();
		oi.setProduct(product.get());
		oi.setQuantity(Utils.tryParseToInt(quantityTextField.getText()));
		oi.setDiscount(Utils.tryParseToDouble(discountTextField.getText()));
		oi.setPrice(product.get().getPrice());
		oi.setOrder(order);

		return oi;
	}

	private boolean checkTextFieldEmpty(TextField textField) {
		if (textField.getText().trim().equals("") || textField.getText() == null) {
			return true;
		}
		return false;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		listeners.add(listener);
	}

	public void updateListView() {
		clearTextFields();
		codeTextField.requestFocus();
		discountTextField.setText("0");
		order = orderService.findAll().stream().filter(o -> o.getId() == orderId).findFirst().get();
		totalTextField.setText(Utils.getFormatedSubTotal(order.getTotal()));
		setEventsOfTextsFields();
		ObservableList<OrderItem> items = FXCollections.observableArrayList(order.getItems());
		tableView.setItems(items);
		tableView.refresh();

	}

	private void notifyDataChange() {
		for (DataChangeListener listener : listeners) {
			listener.onDataChange();
		}

	}

	@Override
	public void onDataChange() {
		updateListView();

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();

	}

	private void initializeNodes() {
		tableColumnProduct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduct().getName()));
		tableColumnQtd.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		tableColumnDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
		tableColumnUnitPrice
				.setCellValueFactory(c -> new SimpleStringProperty(Utils.getFormatedSubTotal(c.getValue().getPrice())));
		tableColumnTotalPrice.setCellValueFactory(
				c -> new SimpleStringProperty(Utils.getFormatedSubTotal(c.getValue().getSubTotal())));
		describPaymentTextField.setItems(FXCollections.observableArrayList(PaymentType.values()));
		Constraints.setTextFieldInteger(discountTextField);
		Constraints.setTextFieldInteger(quantityTextField);
		Constraints.setTextFieldDouble(subTotalTextField);

	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;

	}

	public void setOrderItemService(OrderItemService orderItemService) {
		this.orderItemService = orderItemService;

	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;

	}

	public void setObsList(ObservableList<Product> obsList) {
		this.obsList = obsList;

	}

	private Product getProduct(Long code) {
		Optional<Product> product = obsList.stream().filter(p -> p.getCode() == code).findFirst();
		return product.get();

	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void setEventsOfTextsFields() {
		codeTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				codeTextFieldOnKeyEnterPressed();
			}
		});

		quantityTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				quantityTextFieldOnKeyEnterPressed();

			}
		});

		discountTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				discountTextFieldOnKeyEnterPressed();

			}
		});
		codePaymentTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				codePaymentTextFieldOnKeyEnterPressed();
			}
		});
		subTotalTextField.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				subTotalTextFieldOnKeyEnterPressed();

			}

		});
	}

	private void subTotalTextFieldOnKeyEnterPressed() {
		Double value = Utils.tryParseToDouble(subTotalTextField.getText());
		Double moneyChange = value - order.getTotal();
		if (moneyChange < 0) {
			Alerts.showAlert("Valor informado inválido", null,
					"O valor recebido não pode ser menor que o total: " + Utils.getFormatedSubTotal(order.getTotal()),
					AlertType.WARNING);
		} else {
			moneyChangeTextField.setText(Utils.getFormatedSubTotal(moneyChange));
			btSave.requestFocus();
		}

	}

	private void codePaymentTextFieldOnKeyEnterPressed() {
		if (checkTextFieldEmpty(codePaymentTextField)) {
			Alerts.showAlert("Campo vazio", null, "Este campo não pode ficar vazio", AlertType.ERROR);
		} else {
			try {
				PaymentType payType = getPaymentType(Utils.tryParseToInt(codePaymentTextField.getText()));
				describPaymentTextField.setValue(payType);
				subTotalTextField.setText(String.valueOf(order.getTotal()));
				subTotalTextField.requestFocus();
			} catch (IllegalArgumentException excep) {
				Alerts.showAlert("Código de Pagamento Invalido", null, "Código de pagamento inexistente",
						AlertType.ERROR);
			}
		}

	}

	private void discountTextFieldOnKeyEnterPressed() {
		if (checkTextFieldEmpty(discountTextField)) {
			Alerts.showAlert("Campo vazio", null, "Este campo não pode ficar vazio", AlertType.ERROR);
		} else {
			Double discount = Utils.tryParseToDouble(discountTextField.getText());
			Integer qtd = Utils.tryParseToInt(quantityTextField.getText());
			Product p = getProduct(Utils.tryParseToLong(codeTextField.getText()));
			UnitValueTextField.setText(String.valueOf(p.getPrice()));
			totalValueTextField.setText(String.valueOf((p.getPrice() - (p.getPrice() * discount / 100)) * qtd));
			btAddItem.requestFocus();
		}

	}

	private void quantityTextFieldOnKeyEnterPressed() {
		if (checkTextFieldEmpty(quantityTextField)) {
			Alerts.showAlert("Campo vazio", null, "Este campo não pode ficar vazio", AlertType.ERROR);
		} else {
			discountTextField.requestFocus();
		}

	}

	private void codeTextFieldOnKeyEnterPressed() {
		if (checkTextFieldEmpty(codeTextField)) {
			codePaymentTextField.requestFocus();
		} else {
			try {
				Product p = getProduct(Utils.tryParseToLong(codeTextField.getText()));
				nameTextField.setText(p.getName());

				quantityTextField.requestFocus();
			} catch (NoSuchElementException exception) {
				Alerts.showAlert("Produto não encontrado", null, "Não existe um produto com este codigo",
						AlertType.ERROR);
			}
		}

	}

	private void refreshHome() {
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

	private PaymentType getPaymentType(Integer code) {
		PaymentType payType = PaymentType.toEnum(code);
		return payType;
	}

}
