package gui;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import model.entities.Category;
import model.exceptions.ValidationException;
import model.services.CategoryService;

public class CategoryFormController implements Initializable {
	private CategoryService categoryService;
	private Category category;
	
	List<Category> categories;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();
	
	@FXML
	private Label labelErrorCode;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private GridPane gridPane;
	
	@FXML
	private TextField fieldCode;
	
	@FXML
	private TextField fieldName;
	
	@FXML
	private Button buttonSave;
	
	@FXML
	private Button buttonCancel;
	
	public void onBtnSaveAction(ActionEvent event) {
		if(category==null||categoryService==null) {
			throw new IllegalStateException("Category entity/service was null");
		}
		
		try {
			category = getFormData();
			categoryService.saveOrUpdate(category);
			notifyDataChange();
			Utils.currentStage(event).close();
		}catch (ValidationException e) {
			setErrorsMessages(e.getErrors());
		
		} catch (Exception e) {

			Alerts.showAlert("Error saving product", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	private Category getFormData() {
		ValidationException exception = new ValidationException("Erro de validação");
		
		Category category = new Category();
		category.setId(null);
		if(alreadyExistsCode(Utils.tryParseToLong(fieldCode.getText()))) {
			exception.addError("code", "Já existe uma categoria com este codigo!");
		}
		category.setCode(Utils.tryParseToLong(fieldCode.getText()));
		if(fieldName.getText()==null||fieldName.getText().trim().equals("")) {
			exception.addError("name", "Campo nome não pode ficar vazio!");
		}
		category.setName(fieldName.getText());
		
		if(exception.getErrors().size()>0)
			throw exception;
		return category;
	}

	private boolean alreadyExistsCode(Long code) {
		for(Category cat:categories) {
			if(cat.getCode()==code) {
				return true;
			}
		}
		return false;
	}

	public void onBtnCancelAction() {
		System.out.println("Cancel button");
	}
	
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void updateFormData() {
		if(category==null) {
			throw new IllegalStateException("Category entity was null");
		}
		fieldCode.setText(String.valueOf(category.getCode()));
		fieldName.setText(category.getName());
		
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();
		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldMaxLength(fieldName, 50);
		Constraints.setTextFieldInteger(fieldCode);
		
	}

	private void notifyDataChange() {
		for(DataChangeListener listener:dataChangeListeners) {
			listener.onDataChange();
		}
		
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	private void setErrorsMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
			
		}
		if(fields.contains("code")) {
			labelErrorCode.setText(errors.get("code"));
		}
	}
	

}
