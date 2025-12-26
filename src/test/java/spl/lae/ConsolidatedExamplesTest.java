package spl.lae;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import parser.InputParser;
import parser.OutputWriter;
import parser.ComputationNode;
import memory.SharedVector;
import memory.SharedMatrix;
import memory.VectorOrientation;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class ConsolidatedExamplesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode runFromInputString(String inputContent, int threads, String outFileName) throws Exception {
        File tmp = File.createTempFile("conex", ".json");
        Files.writeString(tmp.toPath(), inputContent, StandardCharsets.UTF_8);
        Path outDir = Path.of("script_output");
        Files.createDirectories(outDir);
        Path outPath = outDir.resolve(outFileName);
        try {
            InputParser parser = new InputParser();
            ComputationNode root = parser.parse(tmp.getAbsolutePath());
            LinearAlgebraEngine engine = new LinearAlgebraEngine(threads);
            ComputationNode result = engine.run(root);
            OutputWriter.write(result.getMatrix(), outPath.toString());
        } catch (ParseException | IllegalArgumentException e) {
            // parse-time or validation errors
            OutputWriter.write(e.getMessage(), outPath.toString());
        } catch (Exception e) {
            // runtime errors from engine
            OutputWriter.write(e.getMessage(), outPath.toString());
        } finally {
            tmp.delete();
        }
        return mapper.readTree(outPath.toFile());
    }

    @Test
    public void copilotExampleProducesExpectedOutput() throws Exception {
        JsonNode produced = runFromInputString(AllExamplesData.INPUTS.get("cpoilottest.json"), 2, "consolidated_cpoilotout.json");
        JsonNode expected = mapper.readTree(AllExamplesData.EXPECTED.get("copilotexpected.json"));
        assertEquals(expected, produced);
    }

    @Test
    public void invalidOperatorProducesError() throws Exception {
        JsonNode node = runFromInputString(AllExamplesData.INPUTS.get("invalid_operator.json"), 2, "consolidated_invalid_op.json");
        assertTrue(node.has("error"));
    }

    @Test
    public void inconsistentRowProducesError() throws Exception {
        JsonNode node = runFromInputString(AllExamplesData.INPUTS.get("inconsistent_row.json"), 2, "consolidated_inconsistent.json");
        assertTrue(node.has("error"));
    }

    @Test
    public void dimMismatchProducesError() throws Exception {
        JsonNode node = runFromInputString(AllExamplesData.INPUTS.get("dim_mismatch.json"), 2, "consolidated_dim_mismatch.json");
        assertTrue(node.has("error"));
    }

    @Test
    public void complexNestedProducesExpected() throws Exception {
        JsonNode produced = runFromInputString(AllExamplesData.INPUTS.get("complex_nested.json"), 4, "consolidated_complex.json");
        JsonNode expected = mapper.readTree(AllExamplesData.COPILOTEXPECTED_COMPLEX_NESTED);
        assertEquals(expected, produced);
    }

    @Test
    public void largeIdentityProducesExpectedWithManyThreads() throws Exception {
        JsonNode produced = runFromInputString(AllExamplesData.INPUTS.get("large_identity_mult.json"), 16, "consolidated_large.json");
        JsonNode expected = mapper.readTree(AllExamplesData.COPILOTEXPECTED_LARGE_IDENTITY_MULT);
        assertEquals(expected, produced);
    }

    @Test
    public void manyThreadsDeterminism() throws Exception {
        String input = AllExamplesData.INPUTS.get("many_threads.json");
        JsonNode ref = runFromInputString(input, 1, "consolidated_det_t1.json");
        for (int t : new int[]{2,4,8,16}) {
            JsonNode produced = runFromInputString(input, t, "consolidated_det_t" + t + ".json");
            assertEquals(ref, produced, "Mismatch for threads=" + t);
        }
    }

    @Test
    public void allInputsMatchExpected() throws Exception {
        for (String name : AllExamplesData.INPUTS.keySet()) {
            if (!AllExamplesData.EXPECTED.containsKey(name)) continue;
            String input = AllExamplesData.INPUTS.get(name);
            JsonNode produced = runFromInputString(input, 4, "consolidated_check_" + name.replaceAll("\\W+","_") + ".json");
            JsonNode expected = mapper.readTree(AllExamplesData.EXPECTED.get(name));
            // If expected is an error object, only assert that produced has an error field
            if (expected.has("error")) {
                assertTrue(produced.has("error"), "Expected an error for " + name);
            } else {
                assertEquals(expected, produced, "Mismatch for " + name);
            }
        }
    }

    @Test
    public void allInputsAcrossThreadCounts() throws Exception {
        int[] threadCounts = new int[] {1,2,4,8,16,32,64};
        for (int threads : threadCounts) {
            for (String name : AllExamplesData.INPUTS.keySet()) {
                String input = AllExamplesData.INPUTS.get(name);
                JsonNode produced = runFromInputString(input, threads, "consolidated_check_t" + threads + "_" + name.replaceAll("\\W+","_") + ".json");
                if (AllExamplesData.EXPECTED.containsKey(name)) {
                    JsonNode expected = mapper.readTree(AllExamplesData.EXPECTED.get(name));
                    if (expected.has("error")) {
                        assertTrue(produced.has("error"), "Expected error for " + name + " with threads=" + threads);
                    } else {
                        assertEquals(expected, produced, "Mismatch for " + name + " with threads=" + threads);
                    }
                } else {
                    // No expected known: at least ensure it's either result or error
                    assertTrue(produced.has("result") || produced.has("error"), "Unexpected output for " + name + " with threads=" + threads);
                }
            }
        }
    }

    @Test
    public void zeroThreadsThrows() {
        assertThrows(IllegalArgumentException.class, () -> new LinearAlgebraEngine(0));
        assertThrows(IllegalArgumentException.class, () -> new LinearAlgebraEngine(-2));
    }

    @Test
    public void vecMatMulDimensionMismatchThrows() {
        SharedVector v = new SharedVector(new double[] {1,2,3}, VectorOrientation.ROW_MAJOR);
        SharedMatrix m = new SharedMatrix();
        m.loadRowMajor(new double[][] { {1,2}, {3,4} });
        assertThrows(IllegalArgumentException.class, () -> v.vecMatMul(m));
    }
}
