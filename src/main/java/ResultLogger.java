package filecompare;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class ResultLogger implements Runnable
{

    // ------------- //
    // - Variables - //
    // ------------- //

    String fileName = "compareFilesResults.csv";
    ComparisonResult newResult;
    
    // --------------- //
    // - Constructor - //
    // --------------- //

    public ResultLogger( ComparisonResult inNewResult )
    {
        newResult = inNewResult;
    }

    // -------------- //
    // - Load Files - //
    // -------------- //

    public void run()
    {
        String stringToWrite = newResult.getFile1() + "," + newResult.getFile2() + "," + newResult.getSimilarity();
        Utilities.appendToFile( "compareFilesResults.csv", stringToWrite);
    }

    public static void initialiseRecordFile()
    {
        String stringToWrite = "File 1,File2,Similarity";
        Utilities.initialiseFile( "compareFilesResults.csv", stringToWrite );
    }

}