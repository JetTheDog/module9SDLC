package application;
	
import static java.util.stream.Collectors.toMap;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javafx.beans.binding.Bindings;
import java.util.Scanner;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.collections.FXCollections;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * This program reads in content from a file and outputs the words and word count.
 * Further information on the details of this process can be found in the documentation for evaluatePoem.
 * Interaction can be used to change the way in which the table of words and occurrences is displayed.
 * 
 * @author Jordan Bender
 */

public class Main extends Application {
	
	// Word/Count data list
	static Map<String, Integer> wordFrequency;
	// Inital array of words
	static ArrayList<String> poemWords = new ArrayList<String>();
	
	@Override
	public void start(Stage primaryStage) {
		
		VBox pageVert = new VBox();
		pageVert.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.80));
		
		Scene pageScene = new Scene(pageVert,700,600);
		primaryStage.setScene(pageScene);
		Label pageLabel = new Label("Poem total words: " + poemWords.size() + ", Unique words: " + wordFrequency.size());
		TableView tableView = new TableView();

		// Create class to manage columns, pass it word list/count
		MapTable mapTable = new MapTable(wordFrequency);
		tableView = mapTable.getTableView();
		
		tableView.setFixedCellSize(25);
	    tableView.prefHeightProperty().bind(tableView.fixedCellSizeProperty().multiply(Bindings.size(tableView.getItems()).add(0.9)));
		// Show it
		pageLabel.setFont(new Font("Calibri", 24));
		primaryStage.setScene(pageScene);
		primaryStage.show();
		
		pageVert.getChildren().add(pageLabel);
		pageVert.getChildren().add(tableView);	
	}
	
	public static void main(String[] args) throws IOException {	
		Boolean ret = evaluatePoem("ravenPoem.html");
		
		// Creates UI
		if (ret)
			launch(args);
	}
		
	public static Boolean evaluatePoem(String poemFileName) throws IOException  {
		/**
		 * This method handels the initial evaluation of the poem's file.
		 * The entire poem is first put into a string where unwanted contents are removed.
		 * Next scanner is used to add each word of the string into an ArrayList.
		 * The words are then counted and mapped to their amount in the poem.
		 *
		 * @param String takes the file name of poem that you want to evaluate.
		 * @return This method is a boolean that returns 'retVal'.
		 * A value of true will mean the program successfully parsed and mapped the contents.
		 * @exception IOException is thrown if an issue occurs when finding/interacting with the file.
		 */
		
		// Local data
		File poemFile = new File(poemFileName);
		FileInputStream fis = new FileInputStream(poemFile);
		byte[] data = new byte[(int) poemFile.length()];
		Boolean retVal = false;
		
		try {
			// Read file
			fis.read(data);
			fis.close();
			String entirePoem = new String(data, "UTF-8");
			int firstSpot = entirePoem.indexOf("<h1>"); // Start here
			if (firstSpot == -1)
				firstSpot = 0;
			int lastSpot = entirePoem.indexOf("<!--end chapter-->"); // End here
			if (lastSpot == -1)
				lastSpot = entirePoem.length() - 1;
			entirePoem = entirePoem.substring(firstSpot, lastSpot); // Shortens the page to just the poem
			
			// Remove unwanted chars
			entirePoem = entirePoem.replaceAll("\\<.*?>",""); // Removes HTML tags
			entirePoem = entirePoem.replaceAll("\\.",""); // Removes period
			entirePoem = entirePoem.replaceAll("!"," "); // Removes !
			entirePoem = entirePoem.replaceAll(","," "); // Removes ,
			entirePoem = entirePoem.replaceAll(";"," "); // Removes ;
			entirePoem = entirePoem.replaceAll("\\?"," "); // Removes ?
			entirePoem = entirePoem.replaceAll("\\n"," "); // Removes \n
			entirePoem = entirePoem.replaceAll("\\r"," "); // Removes \r
			entirePoem = entirePoem.replaceAll("[^\\p{ASCII}]", " "); // Removes non-ASII
			
			// Collect just words
			Scanner scanPoem = new Scanner(entirePoem);
			scanPoem.useDelimiter(" |\\n|-"); // White space
			while(scanPoem.hasNext()) {
				String word = scanPoem.next();
				if(word.length() > 0)
					poemWords.add(word);
			}
			scanPoem.close();
			
			// Create count array
			wordFrequency = poemWords.stream().collect(toMap(s -> s, s -> 1, Integer::sum)); // Maps words and counts occurrences
			
			retVal = true;

			// Debugging
			System.out.println("Sorted words:");
			System.out.println(wordFrequency);
		   
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
}