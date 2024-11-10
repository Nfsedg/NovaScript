/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.novascript;
import java.util.*;
import java.util.regex.*;
import java.io.*;
/**
 *
 * @author edgar
 */
public class NovaScript {
        
    // Expresión regular para detectar variables, operadores y comandos de salida
    private static final Pattern showPrintPattern = Pattern.compile("(SHOW|PRINT)\\(\"(.*?)\"\\)");
    private static final Pattern variablePattern = Pattern.compile("(INT|STR|BOOL|FLOAT)\\s+(\\w+)\\s*=\\s*(.+);?");
    
    // Mapa para almacenar variables y sus valores
    private static final Map<String, Object> variables = new HashMap<>();

    public static void compile(String code) {
        execute(code);
    }

    private static void execute(String code) {
        System.out.println("Ejecutando código NovaScript:");

        // Analiza cada línea del código
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();

            // Manejar comandos SHOW y PRINT con mensajes entre comillas
            Matcher showPrintMatcher = showPrintPattern.matcher(line);
            if (showPrintMatcher.find()) {
                String command = showPrintMatcher.group(1); // SHOW o PRINT
                String message = showPrintMatcher.group(2); // Mensaje entre comillas
                System.out.println(message);
                continue;
            }

            // Manejar declaración de variables (INT, STR, BOOL, FLOAT)
            Matcher variableMatcher = variablePattern.matcher(line);
            if (variableMatcher.find()) {
                String type = variableMatcher.group(1);
                String varName = variableMatcher.group(2);
                String value = variableMatcher.group(3).replace("\"", "");

                switch (type) {
                    case "INT":
                        variables.put(varName, Integer.parseInt(value));
                        break;
                    case "FLOAT":
                        variables.put(varName, Float.parseFloat(value));
                        break;
                    case "STR":
                        variables.put(varName, value);
                        break;
                    case "BOOL":
                        variables.put(varName, Boolean.parseBoolean(value.toUpperCase()));
                        break;
                    default:
                        System.out.println("Tipo desconocido: " + type);
                        break;
                }
                continue;
            }

            // Si es una llamada SHOW o PRINT con una variable
            if (line.startsWith("SHOW") || line.startsWith("PRINT")) {
                int start = line.indexOf("(");
                int end = line.lastIndexOf(")");
                if (start != -1 && end != -1) {
                    String varName = line.substring(start + 1, end).trim();
                    if (variables.containsKey(varName)) {
                        System.out.println(variables.get(varName));
                    } else {
                        System.out.println("Error: Variable no definida - " + varName);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java NovaScriptCompiler <nombre_del_archivo.txt>");
            return;
        }

        String fileName = args[0];
        StringBuilder code = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        compile(code.toString());
    }
}
