package sillypackagenamepleasechange;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class SillyClassNamePleaseChange extends Application
{

    // Launch application
    public static void main(String[] args)
    {
        Application.launch(args);
    }
    
    // ------------- //
    // - Variables - //
    // ------------- //

    private TableView<ComparisonResult> resultTable = new TableView<>();  
    private ProgressBar progressBar = new ProgressBar();
    
    // -------- //
    // - Main - //
    // -------- //

    @Override
    public void start(Stage stage)
    {

        // - Layout - //

        // Title, min width
        stage.setTitle("SillyWindowTitlePleaseChange");
        stage.setMinWidth(600);

        // Toolbar
        Button compareButton = new Button("Compare");
        Button stopButton = new Button("Stop");
        ToolBar toolBar = new ToolBar(compareButton, stopButton);
        
        // Initialise progressbar
        progressBar.setProgress(0.0);
        
        // Create columns, set widths, add to table
        TableColumn<ComparisonResult,String> file1Col = new TableColumn<>("File 1");
        TableColumn<ComparisonResult,String> file2Col = new TableColumn<>("File 2");
        TableColumn<ComparisonResult,String> similarityCol = new TableColumn<>("Similarity");
        file1Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        file2Col.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.40));
        similarityCol.prefWidthProperty().bind(resultTable.widthProperty().multiply(0.20));
        resultTable.getColumns().add(file1Col);
        resultTable.getColumns().add(file2Col);
        resultTable.getColumns().add(similarityCol);

        // Add main parts to window
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolBar);
        mainBox.setCenter(resultTable);
        mainBox.setBottom(progressBar);
        Scene scene = new Scene(mainBox);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        // - Functions - //
        
        // Toolbar buttons
        compareButton.setOnAction(event -> crossCompare(stage));
        stopButton.setOnAction(event -> stopComparison());
        
        // How to extract ComparisonResult and put into the three columns
        file1Col.setCellValueFactory(      (cell) -> new SimpleStringProperty( cell.getValue().getFile1() ) );
        file2Col.setCellValueFactory(      (cell) -> new SimpleStringProperty( cell.getValue().getFile2() ) );
        similarityCol.setCellValueFactory( (cell) -> new SimpleStringProperty( String.format("%.1f%%", cell.getValue().getSimilarity() * 100.0)) );

    }
    
    // ------------------------- //
    // - BUTTON: Cross Compare - //
    // ------------------------- //

    private void crossCompare(Stage stage)
    {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        dc.setTitle("Choose directory");
        File directory = dc.showDialog(stage);
        
        System.out.println("Comparing files within " + directory + "...");
        
        // Extremely fake way of demonstrating how to use the progress bar (noting that it can 
        // actually only be set to one value, from 0-1, at a time.)
        try
        {
            Thread.sleep(1000);
            progressBar.setProgress(0.25);
            Thread.sleep(1000);
            progressBar.setProgress(0.5);
            Thread.sleep(1000);
            progressBar.setProgress(0.6);
            Thread.sleep(1000);
            progressBar.setProgress(0.85);
            Thread.sleep(1000);
            progressBar.setProgress(1.0);
        }
        catch( InterruptedException e )
        {
            System.out.println( e.toString() );
        }

        // Extremely fake way of demonstrating how to update the table (noting that this shouldn't
        // just happen once at the end, but progressively as each result is obtained.)
        List<ComparisonResult> newResults = new ArrayList<>();
        newResults.add(new ComparisonResult("Example File 1", "Example File 2", 0.75));
        newResults.add(new ComparisonResult("Example File 1", "Example File 3", 0.31));
        newResults.add(new ComparisonResult("Example File 2", "Example File 3", 0.45));
        
        resultTable.getItems().setAll(newResults);        
        
        // progressBar.setProgress(0.0); // Reset progress bar after successful comparison?
    }

    // ---------------- //
    // - BUTTON: Stop - //
    // ---------------- //

    private void stopComparison()
    {
        System.out.println("Stopping comparison...");
    }
}
