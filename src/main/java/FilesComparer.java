package filecompare;

import java.util.*;
import java.util.concurrent.*;

import java.io.*;

public class FilesComparer implements Runnable
{

    // ------------- //
    // - Variables - //
    // ------------- //

    // Injected list of files
    List<File> listOfFiles;
    // Injected list of completed comparisons (initially empty)
    BlockingQueue<ComparisonResult> listOfComparisons;
    Object mutex;

    // --------------- //
    // - Constructor - //
    // --------------- //

    public FilesComparer( List<File> inListOfFiles, BlockingQueue<ComparisonResult> inListOfComparisons )
    {
        listOfFiles = inListOfFiles;
        listOfComparisons = inListOfComparisons;
    }

    // ----------------- //
    // - Compare Files - //
    // ----------------- //

    public void run()
    {
        File tempFile;
        ComparisonResult newResult;

         // Compare all files
         for ( int i = 0; i < listOfFiles.size(); i++ )
         {
             for ( int j = i + 1; j < listOfFiles.size(); j++ )
             {
                 try
                 {
                    listOfComparisons.put( compareFiles( listOfFiles.get(i), listOfFiles.get(j) ) );
                 }
                 catch( InterruptedException e )
                 {
                     System.out.println( e.toString() );
                 }
             }
         }
    }

    // Compare two files
    private ComparisonResult compareFiles( File inFile1, File inFile2 )
    {
        double similarity = 0;
        char[] file1Chars = Utilities.textFileToCharArray( inFile1 );
        char[] file2Chars = Utilities.textFileToCharArray( inFile2 );

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


    // ---------------------------- //
    // - CLASS: Compare Processor - //
    // ---------------------------- //

    private class CompareProcessor implements Runnable
    {

        public void run()
        {

        }
    }

}