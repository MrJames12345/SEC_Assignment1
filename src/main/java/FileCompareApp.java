package filecompare;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.*;
import java.io.*;

public class FileCompareApp extends Application
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
    private File selectedDirectory;
    
    // -------- //
    // - Main - //
    // -------- //

    @Override
    public void start(Stage stage)
    {

        // - Layout - //

        // Title, min width
        stage.setTitle("Files Compare");
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
        compareButton.setOnAction(event -> fullCompareProcess(stage));
        stopButton.setOnAction(event -> stopComparison());
        
        // How to extract ComparisonResult and put into the three columns
        file1Col.setCellValueFactory(      (cell) -> new SimpleStringProperty( cell.getValue().getFile1().substring( selectedDirectory.toString().length() + 1 ) ) );
        file2Col.setCellValueFactory(      (cell) -> new SimpleStringProperty( cell.getValue().getFile2().substring( selectedDirectory.toString().length() + 1 ) ) );
        similarityCol.setCellValueFactory( (cell) -> new SimpleStringProperty( String.format("%.1f%%", cell.getValue().getSimilarity() * 100.0)) );

    }
    
    // ----------- //
    // - Compare - //
    // ----------- //

    private void fullCompareProcess(Stage stage)
    {
        // List of all files
        List<File> listOfFiles = new ArrayList<File>();
        // Blocking queue for completed comparisons
        BlockingQueue<ComparisonResult> listOfComparisons = new ArrayBlockingQueue<ComparisonResult>(10);
        // Thread pool for comparing files, updating UI table and writing to csv
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );

        progressBar.setProgress( 0.0 );

        // Choose directory
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("C:\\Users\\James\\Desktop\\TEST"));
        dc.setTitle("Choose directory to compare files in.");
        selectedDirectory = dc.showDialog(stage);

        // Use separate thread to load files
        CompletableFuture.runAsync( new FilesLoader( selectedDirectory, listOfFiles ) )
        // Then add file comparing process to thread pool
        .thenRun( () -> threadPool.submit( new FilesComparer( listOfFiles, listOfComparisons ) ) );

        // Add results processing to thread pool
        threadPool.submit( () -> {
            // Innitialise csv file
            ResultLogger.initialiseRecordFile();
            // Completed comparisons counter
            int completedComparisons = 0;
            // Constantly process new results
            while(true)
            {
                ComparisonResult tempResult;
                // Take the next comparison result (blocking/waiting if empty)
                tempResult = listOfComparisons.take();
                // Add csv writing process to thread pool
                threadPool.submit( new ResultLogger( tempResult ) );
                // Add UI table updating process to thread pool
                threadPool.submit( () -> {
                    if ( tempResult.getSimilarity() > 0.5 )
                        resultTable.getItems().add(tempResult);
                } );
                // Add progress bar updating to thread pool
                completedComparisons++;
                double totalComparisons = (listOfFiles.size()*listOfFiles.size() - listOfFiles.size()) * 0.5;
                final double progress = (double)(completedComparisons/totalComparisons);
                threadPool.submit( () -> {
                    progressBar.setProgress( progress );
                    if ( Double.compare(progress, 1.0) == 0 )
                        progressBar.setStyle("-fx-accent: orange");
                });
            }
        });

    }

    // -------- //
    // - Stop - //
    // -------- //

    private void stopComparison()
    {
        System.out.println("Stopping comparison...");
    }
}
