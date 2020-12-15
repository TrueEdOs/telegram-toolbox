package org.telegram.toolbox.toolbox.runners;

import java.io.*;

public class PythonRunner {
    private final static String codeFile = "./code.py";
    private final static String inputFile = "./in";

    public String execute(final String code, final String input) throws Exception {
        PrintWriter writerCode = new PrintWriter(codeFile, "UTF-8");
        writerCode.write(code);
        writerCode.close();
        PrintWriter writerInput = new PrintWriter(inputFile, "UTF-8");
        writerInput.write(input);
        writerInput.close();
        final ProcessBuilder processBuilder = new ProcessBuilder("python", codeFile);

        final Process process = processBuilder.start();
        final StringBuilder output = new StringBuilder();
        final StringBuilder errorOutput = new StringBuilder();

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        final BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line);
        }

        if (errorOutput.length() != 0) {
            throw new Exception("Python package failed with error: " + errorOutput.toString());
        }

        return output.toString();
    }
}
