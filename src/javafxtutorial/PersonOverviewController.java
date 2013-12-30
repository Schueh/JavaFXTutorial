package javafxtutorial;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafxtutorial.model.Person;
import javafxtutorial.util.CalendarUtil;

public class PersonOverviewController {
      @FXML
	  private TableView<Person> personTable;
	  @FXML
	  private TableColumn<Person, String> firstNameColumn;
	  @FXML
	  private TableColumn<Person, String> lastNameColumn;
	  
	  @FXML
	  private Label firstNameLabel;
	  @FXML
	  private Label lastNameLabel;
	  @FXML
	  private Label streetLabel;
	  @FXML
	  private Label postalCodeLabel;
	  @FXML
	  private Label cityLabel;
	  @FXML
	  private Label birthdayLabel;
	  
	  // Reference to the main application
	  private MainApp mainApp;
	  
	  /**
	  * The constructor.
	  * The constructor is called before the initialize() method.
	  */
	  public PersonOverviewController() {
	  }
	  
	  /**
	   * Initializes the controller class. This method is automatically called
	   * after the fxml file has been loaded.
	   */
	   @FXML
	   private void initialize() {
		   // Initialize the person table
	       firstNameColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
	       lastNameColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
	       
	       personTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	       
	       ClearLabels();
	       
	       personTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Person>(){
	    	   
	    	   @Override
	    	   public void changed(ObservableValue<? extends Person> observable, Person oldValue, Person newValue) {
	    		   showPersonDetails(newValue);
	    	   }
	       });
	   }

	   /**
	   * Is called by the main application to give a reference back to itself.
	   * 
	   * @param mainApp
	   */
	   public void setMainApp(MainApp mainApp) {
	       this.mainApp = mainApp;
	       
	       // Add observable list data to the table
	       personTable.setItems(mainApp.getPersonData());
	   }
	   
	   private void showPersonDetails(Person person) {
		   if (person == null) {
			   ClearLabels();
		   } else {
			   this.birthdayLabel.setText(CalendarUtil.format(person.getBirthday()));
			   this.cityLabel.setText(person.getCity());
			   this.firstNameLabel.setText(person.getFirstName());
			   this.lastNameLabel.setText(person.getLastName());
			   this.postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
			   this.streetLabel.setText(person.getStreet());
		   }
	   }

	private void ClearLabels() {
		this.birthdayLabel.setText("");
		this.cityLabel.setText("");
		this.firstNameLabel.setText("");
		this.lastNameLabel.setText("");
		this.postalCodeLabel.setText("");
		this.streetLabel.setText("");
	}
	
	@FXML
	private void handleDeletePerson() {
		int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			personTable.getItems().remove(selectedIndex);
		} else {
			Dialogs.showWarningDialog(mainApp.getPrimaryStage(), "Please select a person in the table.", "No Person selected", "No Selection");
		}
		
	}
	
	@FXML
	private void handleNewPerson() {
		Person tempPerson = new Person();
		boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
		if (okClicked) {
			mainApp.getPersonData().add(tempPerson);
		}
	}
	
	@FXML
	private void handleEditPerson() {
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		if (selectedPerson != null) {
			boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
			if (okClicked) {
				refreshPersonTable();
				showPersonDetails(selectedPerson);
			} else {
				Dialogs.showWarningDialog(mainApp.getPrimaryStage(), "Please select a person in table.",
						"No Person selected", "No Selection");
			}
		}
	}

	private void refreshPersonTable() {
		int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
		personTable.setItems(null);
		personTable.layout();
		personTable.setItems(mainApp.getPersonData());
		personTable.getSelectionModel().select(selectedIndex);
	}
}
