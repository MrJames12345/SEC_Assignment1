package sillypackagenamepleasechange;

public class ComparisonResult
{

    // - Variables - //

    private final String file1;
    private final String file2;
    private final double similarity;

    // - Constructor - //
    
    public ComparisonResult(String inFile1, String inFile2, double inSimilarity)
    {
        this.file1 = inFile1;
        this.file2 = inFile2;
        this.similarity = inSimilarity;
    }

    // - Accessors - //
    
    public String getFile1() { return file1; }
    public String getFile2() { return file2; }
    public double getSimilarity() { return similarity; }

}
