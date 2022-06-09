package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import enums.OrderStatus;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Category;
import model.entities.Order;
import model.entities.Product;
import model.services.CategoryService;
import model.services.OrderItemService;
import model.services.OrderService;
import model.services.PaymentService;
import model.services.ProductService;

public class MainViewController implements Initializable, DataChangeListener {

	private ObservableList<Category> obsListCategories;
	private ObservableList<Product> obsList;
	private ObservableList<Order> obsListOrders;
	
	
	
	@FXML
	private VBox vBox;
	
	@FXML
	private FlowPane flowPane;

	@FXML
	private MenuItem menuItemProduct;

	@FXML
	private MenuItem menuItemCategory;

	@FXML
	private MenuItem menuItemOpenCashRegister;

	@FXML
	private MenuItem menuItemCloseCashRegister;

	@FXML
	private MenuItem menuItemFindCashRegister;

	@FXML
	private MenuItem menuItemFindOrders;

	@FXML
	private Button btNewSale;

	@FXML
	public void onMenuItemProductAction() {
		loadView("/gui/ProductList.fxml", (ProductController controller) -> {
			controller.setObsList(obsList);
			controller.setObsListCategories(obsListCategories);
			controller.setProductService(new ProductService());
			controller.setCategoryService(new CategoryService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemCategoryAction() {
		loadView("/gui/CategoryList.fxml", (CategoryListController controller) -> {
			controller.setObsListCategory(obsListCategories);
			controller.setCategoryService(new CategoryService());
			controller.updateTableView();
		});
	}



	@FXML
	public void showModal() {
		loadView("/gui/SaleForm.fxml", (SaleFormController controller) -> {
			controller.addEventHandler();
			controller.setObsList(obsList);
			controller.setProductService(new ProductService());
			controller.setOrderService(new OrderService());
			controller.setOrderItemService(new OrderItemService());
			controller.setOrder(new Order(null, LocalDateTime.now(),OrderStatus.WAITING_PAYMENT));
			controller.subscribeDataChangeListener(controller);
		
		});
	}

	@FXML
	public void onMenuItemOpenCashRegisterAction() {
		System.out.println("Find a product");
	}

	@FXML
	public void onMenuItemCloseCashRegisterAction() {
		System.out.println("Find a product");
	}

	@FXML
	public void onMenuItemFindCashRegisterAction() {
		System.out.println("Find a product");
	}

	@FXML
	public void onMenuItemFindOrdersAction() {
		System.out.println("Find a product");
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializaNodes();

	}

	private void initializaNodes() {
		ProductService productService = new ProductService();
		CategoryService categoryService = new CategoryService();
		OrderService orderService = new OrderService();
		obsList = FXCollections.observableArrayList(productService.findAll());
		obsListCategories = FXCollections.observableArrayList(categoryService.findAll());
		obsListOrders = FXCollections.observableArrayList(orderService.findAll());

	}
	
	public void updateListOrders() {
		List<Order> ordersPending = getPendingOrders();
		for(Order o:ordersPending) {	
			Button btn = makeButton(o);
			btn.setOnAction(e->{
				Stage parent = Utils.currentStage(e);
				createSaleFinisheModal(o,"/gui/SaleFinish.fxml",parent);
			});
			flowPane.getChildren().add(btn);
			
		}
		
		
	}
	
	private List<Order> getPendingOrders(){
		return  obsListOrders.stream()
				.filter(o->o.getOrderStatus()==OrderStatus.WAITING_PAYMENT)
				.toList();
	}
	
	private void createSaleFinisheModal(Order o, String absolutName, Stage parent) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();
			SaleFinishController controller = loader.getController();
			controller.setOrderId(o.getId());
			controller.setObsList(obsList);
			controller.setPaymentService(new PaymentService());
			controller.setOrderService(new OrderService());
			controller.setOrderItemService(new OrderItemService());
			controller.subscribeDataChangeListener(controller);
			controller.updateListView();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Finalize o pedido");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parent);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
	}

	

	private Button makeButton(Order o) {					
		Button btn = new Button("",makeVbox(o));
		btn.setId(o.getId().toString());
		btn.setPrefSize(80, 80);
		
		return btn;
	}
	
	
	private VBox makeVbox(Order o) {
		VBox vb2 = new VBox();
		Label l1 = new Label();
		Label l2 = new Label();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		l1.setText(o.getMoment().format(formatter).toString());
		l1.setWrapText(true);
		l1.setFont(new Font(10));
		l2.setText(o.getOrderStatus().getDescrib());
		l2.setWrapText(true);
		l2.setFont(new Font(10));
		vb2.getChildren().add(l1);
		vb2.getChildren().add(l2);
		return vb2;
	}

	private synchronized <T> void loadView(String absolutName, Consumer<T> initializeAction) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			VBox vBox = loader.load();
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(vBox.getChildren());
			T controller = loader.getController();
			initializeAction.accept(controller);
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loadind view " + e.getMessage(), e.getMessage(), AlertType.ERROR);
			e.printStackTrace();
		}
	}

	@Override
	public void onDataChange() {
		System.out.println("ondatachanege mainview");
		updateListOrders();
		
	}

}
