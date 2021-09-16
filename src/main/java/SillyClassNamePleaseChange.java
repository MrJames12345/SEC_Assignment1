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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;

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
    
    // ----------- //
    // - Compare - //
    // ----------- //

    private void crossCompare(Stage stage)
    {

        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        dc.setTitle("Choose directory to compare files in.");
        File selectedDirectory = dc.showDialog(stage);

        List<File> allFiles = getAllFilesInDirectory( selectedDirectory );

        for ( File tempFile : allFiles )
            System.out.println( tempFile );
    }

    private List<File> getAllFilesInDirectory( File inDirectory )
    {
        // Subdirectories' files added after current directory finished.
        List<File> subDirectoriesToProcess = new ArrayList<File>();

        // Go through current directory
        List<File> outList = new ArrayList<File>();
        for ( File tempFile : inDirectory.listFiles() )
        {
            if ( !tempFile.isDirectory() )
                outList.add( tempFile );
            else
                subDirectoriesToProcess.add( tempFile );
        }

        // Go through subdirectories
        for ( File tempSubdirectory : subDirectoriesToProcess )
        {
            outList.addAll( getAllFilesInDirectory(tempSubdirectory) );
        }

        return outList;
    }

    // ---------------- //
    // - BUTTON: Stop - //
    // ---------------- //

    private void stopComparison()
    {
        System.out.println("Stopping comparison...");
    }
}
