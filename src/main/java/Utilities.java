package filecompare;

import java.util.*;
import java.io.*;

// Holds common methods

public class Utilities
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

    // Check if file is text file
    public static boolean isTextFile( File inFile )
    {
        boolean check = false;
        String extension = inFile.toString().substring( inFile.toString().indexOf(".") );
        if ( extension.equals(".txt") || extension.equals(".md") || extension.equals(".java") || extension.equals(".cs"))
            check = true;
        return check;
    }

    // Create new instance of file
    public static void initialiseFile( String inFilePath, String inInitialText )
    {
        File tempFile = new File(inFilePath);
        if ( tempFile.exists() )
        {
            tempFile.delete();
            tempFile = new File(inFilePath);
        }
        appendToFile( inFilePath, inInitialText );
    }

    // Append record to file
    public static void appendToFile( String inFilePath, String newRecord )
    {   
        try( PrintWriter writer = new PrintWriter( new FileWriter( inFilePath, true ) ) )
        {
            writer.println( newRecord );
        }
        catch( IOException e )
        {
            System.out.println( "IO Error: " + e.toString() );
        }
    }

}
