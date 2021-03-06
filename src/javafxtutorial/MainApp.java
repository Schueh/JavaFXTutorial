package javafxtutorial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import com.thoughtworks.xstream.XStream;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafxtutorial.model.Person;
import javafxtutorial.util.FileUtil;

public class MainApp extends Application {

	private ObservableList<Person> personData = FXCollections.observableArrayList();
	
	public MainApp() {
		personData.add(new Person("Hans", "Muster"));
	      personData.add(new Person("Ruth", "Mueller"));
	      personData.add(new Person("Heinz", "Kurz"));
	      personData.add(new Person("Cornelia", "Meier"));
	      personData.add(new Person("Werner", "Meyer"));
	      personData.add(new Person("Lydia", "Kunz"));
	      personData.add(new Person("Anna", "Best"));
	      personData.add(new Person("Stefan", "Meier"));
	      personData.add(new Person("Martin", "Mueller"));
	}
	
	public ObservableList<Person> getPersonData() {
		return personData;
	}
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AddressApp");
		this.primaryStage.getIcons().add(new Image("file:resources/iamges/Address_Book.png"));
		
		try {
			// Load the root layout from the fxml file
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			
			primaryStage.show();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}
		
		showPersonOverview();
		
		File file = getPersonalFilePath();
		if (file != null) {
			loadPersonDataFromFile(file);
		}
	}
	
	public Stage getPrimaryStage(){
		return primaryStage;
	}

	public void showPersonOverview() {
		try {
			// Load the fxml file and set into the center of the main layout
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PersonOverview.fxml"));
			AnchorPane overviewPage = (AnchorPane) loader.load();
			rootLayout.setCenter(overviewPage);
			
			// Give the controller access to the main app
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);
			
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}
		
	}
	
	public boolean showPersonEditDialog(Person person) {
		try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PersonEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			PersonEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(person);
			
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public File getPersonalFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}
	
	public void setPersonFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());
			
			primaryStage.setTitle("AddressApp - " + file.getName());
		} else {
			prefs.remove("filePath");
			primaryStage.setTitle("AddressApp");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadPersonDataFromFile(File file) {
		XStream xstream = new XStream();
		xstream.alias("person", Person.class);
		
		try {
			String xml = FileUtil.readFile(file);
			
			ArrayList<Person> personList = (ArrayList<Person>) xstream.fromXML(xml);
			
			personData.clear();
			personData.addAll(personList);
			
			setPersonFilePath(file);
		} catch (Exception e) {
			Dialogs.showErrorDialog(primaryStage, 
					"Could not load data from file:\n" + file.getPath(),
					"Could not load data", "Error", e);
		}
	}
	
	public void savePersonDataToFile(File file) {
		XStream xstream = new XStream();
		xstream.alias("person", Person.class);
		
		ArrayList<Person> personList = new ArrayList<Person>(personData);
		
		String xml = xstream.toXML(personList);
		try {
			FileUtil.saveFile(xml, file);
			
			setPersonFilePath(file);
		} catch (Exception e) {
			Dialogs.showErrorDialog(primaryStage, "Could not save data to file:\n" + file.getPath(),
					"Could not save data", "Error", e);
		}
	}
	
	public void showBirthdayStatistics() {
		try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Birthday Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			BirthdayStatisticsController controller = loader.getController();
			controller.setPersonData(personData);
			
			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
