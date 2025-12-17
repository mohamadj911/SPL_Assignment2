package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      // Added by us 
      // While args handles the inputs number, check for correct number of arguments
      // the user has to provide numThreads, inputPath and outputPath.
      if (args.length < 3) {
            System.err.println("Usage: <numThreads> <inputPath> <outputPath>");
            return;
        }

        // Converts the first command-line argument from a String to an int/
        // Thats the number of TiredThread workers to create
        int numThreads = Integer.parseInt(args[0]);

        // Stores the location of the JSON file containing the matrices and operations.
        // THats the path to input file
        String inputPath = args[1];

        // Stores the location where the final JSON result or error message must be saved.
        // Thats the path to output file
        String outputPath = args[2];

        try {
            // 1. Parse the input JSON into a tree
            InputParser parser = new InputParser();
            // Reads the JSON and builds the initial ComputationNode tree.
            ComputationNode root = parser.parse(inputPath);

            // 2. Starts the orchestration loop—finding resolvable nodes, loading M1,M2. 
            // Initialize and run the Engine
            LinearAlgebraEngine lae = new LinearAlgebraEngine(numThreads);
            ComputationNode resultNode = lae.run(root);

            // 3. Get the final result matrix from the resolved root node
            // and write it to the output file.
            OutputWriter.write(resultNode.getMatrix(), outputPath);

        } catch (Exception exception) {
            // 4. Handle errors (like dimension mismatches) by writing an error object
            try {
                OutputWriter.write(exception.getMessage(), outputPath);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    
    }
    // Adding end
} 
