package sillypackagenamepleasechange;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.io.*;

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
        compareButton.setOnAction(event -> fullCompareProcess(stage));
        stopButton.setOnAction(event -> stopComparison());
        
        // How to extract ComparisonResult and put into the three columns
        file1Col.setCellValueFactory(      (cell) -> new SimpleStringProperty( cell.getValue().getFile1() ) );
        file2Col.setCellValueFactory(      (cell) -> new SimpleStringProperty( cell.getValue().getFile2() ) );
        similarityCol.setCellValueFactory( (cell) -> new SimpleStringProperty( String.format("%.1f%%", cell.getValue().getSimilarity() * 100.0)) );

    }
    
    // ----------- //
    // - Compare - //
    // ----------- //

    private void fullCompareProcess(Stage stage)
    {

        List<ComparisonResult> listOfComparisons = new ArrayList<ComparisonResult>();

        // Choose directory
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File("."));
        dc.setTitle("Choose directory to compare files in.");
        File selectedDirectory = dc.showDialog(stage);

        // Get all non-empty text files
        List<File> allFiles = Utils.getAllNonEmptyTextFiles( selectedDirectory );

        // Compare all files
        for ( int i = 0; i < allFiles.size(); i++ )
        {
            for ( int j = i + 1; j < allFiles.size(); j++ )
            {
                listOfComparisons.add( compareFiles( allFiles.get(i), allFiles.get(j) ) );
            }
        }

        for ( ComparisonResult tempComparison : listOfComparisons )
        {
            System.out.println( tempComparison.getFile1() );
            System.out.println( tempComparison.getFile2() );
            System.out.println( tempComparison.getSimilarity() );
        }
    }

    // Compare two files
    private ComparisonResult compareFiles( File inFile1, File inFile2 )
    {
        double similarity = 0;
        char[] file1Chars = Utils.textFileToCharArray( inFile1 );
        char[] file2Chars = Utils.textFileToCharArray( inFile2 );

        int[][] subsolutions =  new int[file1Chars.length + 1][file2Chars.length + 1];
        boolean[][] directionsLeft = new boolean[file1Chars.length + 1][file2Chars.length + 1];
        
        // Fill first row and first column of subsolutions with zeros
        for ( int i = 0; i < file1Chars.length + 1; i++ )
            subsolutions[i][0] = 0;
        for ( int i = 0; i < file2Chars.length + 1; i++ )
            subsolutions[0][i] = 0;

        for ( int i = 1; i <= file1Chars.length; i++ )
        {
            for ( int j = 1; j <= file2Chars.length; j++ )
            {
                if ( file1Chars[i - 1] == file2Chars[j - 1] )
                {
                    subsolutions[i][j] = subsolutions[i - 1][j - 1] + 1;
                }
                else if ( subsolutions[i - 1][j] > subsolutions[i][j - 1] )
                {
                    subsolutions[i][j] = subsolutions[i - 1][j];
                    directionsLeft[i][j] = true;
                }
                else
                {
                    subsolutions[i][j] = subsolutions[i][j - 1];
                    directionsLeft[i][j] = false;
                }
            }
        }

        int matches = 0;
        int i = file1Chars.length;
        int j = file2Chars.length;

        while ( i > 0 && j > 0 )
        {
            if ( file1Chars[i - 1] == file2Chars[j - 1] )
            {
                matches += 1;
                i -= 1;
                j -= 1;
            }
            else if ( directionsLeft[i][j] )
                i -= 1;
            else        
                j -= 1;
        }

        similarity =  (double)(matches * 2) / (double)(file1Chars.length + file2Chars.length);

        return new ComparisonResult( inFile1.toString(), inFile2.toString(), similarity );
    }

    // ---------------- //
    // - BUTTON: Stop - //
    // ---------------- //

    private void stopComparison()
    {
        System.out.println("Stopping comparison...");
    }
}
