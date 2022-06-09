package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Category;
import model.services.CategoryService;

public class CategoryListController implements Initializable, DataChangeListener {
	
	private CategoryService categoryService;
	
	private ObservableList<Category> obsListCategory;
	
	
	
	@FXML
	private TableView<Category> tableViewCategories;
	
	@FXML
	private TableColumn<Category, Long> tableColumnCode;
	
	@FXML
	private TableColumn<Category, Long> tableColumnName;
	
	@FXML
	private Button btnNew;
	
	
	public void onBtnNewAction(ActionEvent event) {		
		Stage parent = Utils.currentStage(event);
		Category category = new Category();
		createDialogFormCategory(category, "/gui/CategoryForm.fxml", parent);
		
	}

	private void createDialogFormCategory(Category category, String absolutName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
			Pane pane = loader.load();
			
			CategoryFormController controller = loader.getController();
			controller.setCategory(category);
			controller.setCategoryService(new CategoryService());
			controller.setCategories(obsListCategory);
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			
			dialogStage.setTitle("Informe os dados da nova categoria");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
	
	}
	
	public void updateTableView() {
		if(categoryService==null) {
			throw new IllegalStateException("Service was null");
		}
		obsListCategory = FXCollections.observableArrayList(categoryService.findAll());
		tableViewCategories.setItems(obsListCategory);
		tableViewCategories.refresh();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		tableColumnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewCategories.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	public void setObsListCategory(ObservableList<Category> obsListCategory) {
		this.obsListCategory = obsListCategory;
	}

	@Override
	public void onDataChange() {
		updateTableView();
		
	}
	
	

}
