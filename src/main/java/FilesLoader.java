package filecompare;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;

public class FilesLoader implements Runnable
{

    // ------------- //
    // - Variables - //
    // ------------- //

    // Directory to load files from
    File directory;
    // Injected list of files (initially empty)
    List<File> listOfFiles;
    Object mutex;
    
    // --------------- //
    // - Constructor - //
    // --------------- //

    public FilesLoader( File inDirectory, List<File> inListOfFiles )
    {
        directory = inDirectory;
        listOfFiles = inListOfFiles;
    }

    // -------------- //
    // - Load Files - //
    // -------------- //

    @Override
    public void run()
    {
        getAllNonEmptyTextFiles( directory );
    }

    // Get all non-empty text files in directory (recursively process subdirectories)
    private void getAllNonEmptyTextFiles( File inDirectory )
    {
        // List of subdirectories so can process after current directory finished.
        List<File> subDirectoriesToProcess = new ArrayList<File>();

        // Go through current directory
        for ( File tempFile : inDirectory.listFiles() )
        {
            // IF not subfolder, is non-empty and is text file, process now
            if ( !tempFile.isDirectory() && tempFile.length() > 0 && Utilities.isTextFile(tempFile) )
                listOfFiles.add( tempFile );
            // ELSE IF subdirectory, process later
            else if ( tempFile.isDirectory() )
                subDirectoriesToProcess.add( tempFile );
            // ELSE ignore file
        }

        // Go through subdirectories
        for ( File tempSubdirectory : subDirectoriesToProcess )
            getAllNonEmptyTextFiles(tempSubdirectory);
    }

}