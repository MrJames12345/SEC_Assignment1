package sillypackagenamepleasechange;

import java.util.*;
import java.io.*;

// Holds common methods

public class Utils
{
    
    // Get char array from text file
    public static char[] textFileToCharArray( File inFile )
    {
        String fileString = "";

        try
        {
            BufferedReader fileReader = new BufferedReader( ( new FileReader(inFile) ) );
            String tempLine = null;
    
            while ( (tempLine = fileReader.readLine()) != null )
                fileString += tempLine;
            
            fileReader.close();
        }
        catch( IOException e )
        {
            System.out.println( e.toString() );
        }

        return fileString.toCharArray();
    }

    // Get all non-empty text files
    public static List<File> getAllNonEmptyTextFiles( File inDirectory )
    {
        // Subdirectories' files added after current directory finished.
        List<File> subDirectoriesToProcess = new ArrayList<File>();

        // Go through current directory
        List<File> outList = new ArrayList<File>();
        for ( File tempFile : inDirectory.listFiles() )
        {

            // IF not subfolder, is non-empty and is text file, process now
            if ( !tempFile.isDirectory() && tempFile.length() > 0 && isTextFile(tempFile) )
                outList.add( tempFile );
            // ELSE IF subdirectory, process later
            else if ( tempFile.isDirectory() )
                subDirectoriesToProcess.add( tempFile );
            // ELSE ignore file

        }

        // Go through subdirectories
        for ( File tempSubdirectory : subDirectoriesToProcess )
        {
            outList.addAll( getAllNonEmptyTextFiles(tempSubdirectory) );
        }

        return outList;
    }

    // Check if file is text file
    public static boolean isTextFile( File inFile )
    {
        boolean check = false;
        String extension = inFile.toString().substring( inFile.toString().indexOf(".") );
        if ( extension.equals(".txt") || extension.equals(".md") || extension.equals(".java") || extension.equals(".cs"))
            check = true;
        return check;
    }

}
