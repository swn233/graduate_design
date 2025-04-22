package com.example.backend.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class PythonExecuteService {

    public String executePythonCode(String code) {
        try {
            ProcessBuilder pb = new ProcessBuilder("/opt/homebrew/anaconda3/bin/python", "-c", code);
            System.out.println("后端python执行");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            return "Exit Code: " + exitCode + "\nOutput:\n" + output.toString();
        } catch (Exception e) {
            return "Execution error: " + e.getMessage();
        }
    }
}