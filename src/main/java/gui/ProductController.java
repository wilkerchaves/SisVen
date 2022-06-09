package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Category;
import model.entities.Product;
import model.entities.Production;
import model.services.CategoryService;
import model.services.ProductService;
import model.services.ProductionService;

public class ProductController implements Initializable, DataChangeListener {

	private ProductService service;

	private CategoryService categoryService;

	private ObservableList<Category> obsListCategories;

	private ObservableList<Product> obsList;

	@FXML
	private TableView<Product> tableViewProducts;

	@FXML
	private TableColumn<Product, Long> tableColumnCode;

	@FXML
	private TableColumn<Product, String> tableColumnName;

	@FXML
	private TableColumn<Product, Product> tableColumnEDIT;

	@FXML
	private TableColumn<Product, Product> tableColumnREMOVE;

	@FXML
	private TableColumn<Product, Double> tableColumnPrice;

	@FXML
	private TableColumn<Product, Integer> tableColumnQnt;

	@FXML
	private TableColumn<Product, Button> tableColumnOpt;

	@FXML
	private Button btNew;

	@FXML
	private Button btAddProduction;

	@FXML
	private Button btClose;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Product product = new Product();
		createDialogFormProduct(product, "/gui/ProductForm-v2.0.fxml", parentStage);
	}

	@FXML
	public void onBtAddProductionAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Production production = new Production();
		createDialogFormProduction(production, "/gui/ProductionForm.fxml", parentStage);

	}

	public void onBtClose(ActionEvent event) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
		
		
	}

	public void initializeNodes() {
		tableColumnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		tableColumnQnt.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewProducts.prefHeightProperty().bind(stage.heightProperty());
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();

	}

	public void setProductService(ProductService service) {
		this.service = service;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setObsList(ObservableList<Product> obsList) {
		this.obsList = obsList;
	}

	public void setObsListCategories(ObservableList<Category> obsListCategories) {
		this.obsListCategories = obsListCategories;
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		obsList = FXCollections.observableArrayList(service.findAll());
		obsListCategories = FXCollections.observableArrayList(categoryService.findAll());
		tableViewProducts.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
		tableViewProducts.refresh();
	}

	public void createDialogFormProduct(Product product, String absolutName, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			ProductFormController controller = loader.getController();
			controller.setProduct(product);
			controller.setProductService(new ProductService());
			controller.setProducts(obsList);
			controller.setCategoryService(new CategoryService());
			controller.setCategories(obsListCategories);
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Informe os dados do produto");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(stage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	public void createDialogFormUpdateProduct(Product product, String absolutName, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			ProductFormUpdateController controller = loader.getController();
			controller.setProduct(product);
			controller.setProductService(new ProductService());
			controller.setProducts(obsList);
			controller.setCategoryService(new CategoryService());
			controller.setCategories(obsListCategories);
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Informe os dados do produto");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(stage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	public void createDialogFormProduction(Production production, String absolutName, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();

			ProductionFormController controller = loader.getController();
			controller.setProduction(production);
			controller.setProductService(new ProductService());
			controller.setProductionService(new ProductionService());
			controller.setProductsList(obsList);
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();

			dialogStage.setTitle("Informe a produção");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(stage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Product obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogFormUpdateProduct(obj, "/gui/ProductFormUpdateProduct.fxml",
						Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button button = new Button("Apagar");

			@Override
			protected void updateItem(Product obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Product obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
				"Tem certeza que deseja apagar este produto?");
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (Exception e) {
				Alerts.showAlert("Erro ao deletar produto", null, e.getMessage(), AlertType.ERROR);

			}
		}
	}

	@Override
	public void onDataChange() {
		updateTableView();

	}

}
