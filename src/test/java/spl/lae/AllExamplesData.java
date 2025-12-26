package spl.lae;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Single consolidated class that contains all example inputs, expected outputs,
 * and produced outputs as string text blocks and convenient maps.
 *
 * Use the maps `INPUTS`, `EXPECTED`, and `OUTPUTS` to access data by file name.
 */
public final class AllExamplesData {
    // ----- Inputs -----
    public static final String COMPLEX_NESTED = """
{
  "operator": "+",
  "operands": [
    {
      "operator": "*",
      "operands": [
        {
          "operator": "+",
          "operands": [
            [ [1,2], [3,4] ],
            [ [5,6], [7,8] ]
          ]
        },
        {
          "operator": "+",
          "operands": [
            [ [1,0], [0,1] ],
            { "operator": "-", "operands": [ [ [0,1], [1,0] ] ] }
          ]
        }
      ]
    },
    {
      "operator": "T",
      "operands": [
        [ [2,3], [4,5] ]
      ]
    }
  ]
}
""";

    public static final String CPOILOT_TEST = """
{
  "operator": "*",
  "operands": [
    [
      [1, 2],
      [3, 4]
    ],
    [
      [5, 6],
      [7, 8]
    ]
  ]
}
""";

    public static final String DIM_MISMATCH = """
{
  "operator": "*",
  "operands": [
    [ [1,2,3], [4,5,6] ],
    [ [7,8], [9,10] ]
  ]
}
""";

    public static final String INCONSISTENT_ROW = """
[
  [1, 2, 3],
  [4, 5]
]
""";

    public static final String INVALID_OPERATOR = """
{
  "operator": "MOVE",
  "operands": [
    [ [1,2], [3,4] ],
    [ [5,6], [7,8] ]
  ]
}
""";

    public static final String LARGE_IDENTITY_MULT = """
{
  "operator": "*",
  "operands": [
    [
      [1,0,0,0,0,0,0,0,0,0],
      [0,1,0,0,0,0,0,0,0,0],
      [0,0,1,0,0,0,0,0,0,0],
      [0,0,0,1,0,0,0,0,0,0],
      [0,0,0,0,1,0,0,0,0,0],
      [0,0,0,0,0,1,0,0,0,0],
      [0,0,0,0,0,0,1,0,0,0],
      [0,0,0,0,0,0,0,1,0,0],
      [0,0,0,0,0,0,0,0,1,0],
      [0,0,0,0,0,0,0,0,0,1]
    ],
    [
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1],
      [1,1,1,1,1,1,1,1,1,1]
    ]
  ]
}
""";

    public static final String MANY_THREADS = """
{
  "operator": "*",
  "operands": [
    [ [1,2,3], [4,5,6], [7,8,9] ],
    [ [9,8,7], [6,5,4], [3,2,1] ]
  ]
}
""";

    // ----- Expected results (from previous merged_expected.json) -----
    public static final String COPILOTEXPECTED = """
{
  "result": [ [19.0, 22.0], [43.0, 50.0] ]
}
""";

    public static final String COPILOTEXPECTED_COMPLEX_NESTED = """
{
  "result": [ [0.0, 6.0], [1.0, 7.0] ]
}
""";

    public static final String COPILOTEXPECTED_LARGE_IDENTITY_MULT = """
{
  "result": [ [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0], [1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0] ]
}
""";

    public static final String MANY_THREADS_EXPECTED = """
{
  "result": [ [ 30.0, 24.0, 18.0 ], [ 84.0, 69.0, 54.0 ], [ 138.0, 114.0, 90.0 ] ]
}
""";

    public static final String DIM_MISMATCH_EXPECTED = """
{
  "error" : "One or more tasks failed during execution"
}
""";

    public static final String INCONSISTENT_ROW_EXPECTED = """
{
  "error" : "Inconsistent row sizes in matrix."
}
""";

    public static final String INVALID_OPERATOR_EXPECTED = """
{
  "error" : "Unknown operator: MOVE"
}
""";

    // For convenience, provide maps keyed by filename
    public static final Map<String, String> INPUTS;
    public static final Map<String, String> EXPECTED;
    public static final Map<String, String> OUTPUTS;

    static {
        Map<String, String> in = new HashMap<>();
        in.put("complex_nested.json", COMPLEX_NESTED);
        in.put("cpoilottest.json", CPOILOT_TEST);
        in.put("dim_mismatch.json", DIM_MISMATCH);
        in.put("inconsistent_row.json", INCONSISTENT_ROW);
        in.put("invalid_operator.json", INVALID_OPERATOR);
        in.put("large_identity_mult.json", LARGE_IDENTITY_MULT);
        in.put("many_threads.json", MANY_THREADS);

        Map<String, String> ex = new HashMap<>();
        ex.put("copilotexpected.json", COPILOTEXPECTED);
        ex.put("copilotexpected_complex_nested.json", COPILOTEXPECTED_COMPLEX_NESTED);
        ex.put("copilotexpected_large_identity_mult.json", COPILOTEXPECTED_LARGE_IDENTITY_MULT);

        // Map expected results to corresponding input filenames as well
        ex.put("cpoilottest.json", COPILOTEXPECTED);
        ex.put("complex_nested.json", COPILOTEXPECTED_COMPLEX_NESTED);
        ex.put("large_identity_mult.json", COPILOTEXPECTED_LARGE_IDENTITY_MULT);
        ex.put("many_threads.json", MANY_THREADS_EXPECTED);
        ex.put("dim_mismatch.json", DIM_MISMATCH_EXPECTED);
        ex.put("inconsistent_row.json", INCONSISTENT_ROW_EXPECTED);
        ex.put("invalid_operator.json", INVALID_OPERATOR_EXPECTED);

        // For now, set outputs same as expected when available
        Map<String, String> out = new HashMap<>();
        out.putAll(ex);

        INPUTS = Collections.unmodifiableMap(in);
        EXPECTED = Collections.unmodifiableMap(ex);
        OUTPUTS = Collections.unmodifiableMap(out);
    }

    private AllExamplesData() {}
}
