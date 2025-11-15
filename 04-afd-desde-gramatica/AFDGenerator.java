package com.mycompany.proyecto1;

import java.io.*;
import java.util.*;

public class Proyecto1 {

    public static void main(String[] args) {

        try {
            System.out.println("=== PROYECTO 1: GRAMÁTICAS, FIRST, LL(1) y AFD ===\n");

            // 1. Cargar gramática desde archivo (0_grammar.txt)

            System.out.println("Leyendo gramática desde archivo...");
            LinkedHashMap<String, List<String>> G = readGrammar("0_grammar.txt");

            System.out.println("\n=== GRAMÁTICA ORIGINAL ===");
            printGrammar(G);

            // 2. Eliminar recursión por la izquierda

            System.out.println("\nEliminando recursión por la izquierda...");
            LinkedHashMap<String, List<String>> Gsin = EliminacionRecursion.eliminar(G);

            System.out.println("\n=== GRAMÁTICA SIN RECURSIÓN POR LA IZQUIERDA ===");
            printGrammar(Gsin);

            writeGrammarToFile(Gsin, "1_grammar_no_left_recursion.txt");


            // 3. Calcular Conjuntos FIRST

            System.out.println("\nCalculando conjunto FIRST...");
            Map<String, Set<String>> first = ConjuntoFirst.computeFirst(Gsin);

            System.out.println("\n=== FIRST ===");
            printFirst(first);

            writeFirstToFile(first, "2_first.txt");


            // 4. GENERAR TABLA LL(1)

            System.out.println("\nGenerando tabla sintáctica LL(1)...");

            Map<String, Map<String, String>> parsingTable =
                    ParsingTableGenerator.generateTable(Gsin, first);

            System.out.println("\n=== TABLA LL(1) ===");
            ParsingTableGenerator.printTable(parsingTable);

            ParsingTableGenerator.writeTable(parsingTable, "3_parsing_table.csv");


            // 5. GENERAR AFD DESDE LA GRAMÁTICA REGULAR
            // NOTA ORIGINAL DEL CÓDIGO:
            // "Aplicar algoritmo de generar un AFD a partir de una Gramática"

            System.out.println("\n=== GENERANDO AFD DESDE GRAMÁTICA ===");

            AFDGenerator.AFDResult afd =
                    AFDGenerator.generateFromGrammar(Gsin, "S");

            System.out.println("\n=== AFD FINAL ===");
            afd.print();

            // Exportar DOT para visualizar con Graphviz
            String dot = afd.toDot();
            writeText("4_afd.dot", dot);
            System.out.println("\nArchivo DOT generado: 4_afd.dot");


            System.out.println("\n=== PROCESO COMPLETADO CON ÉXITO ===");

        } catch (Exception e) {
            System.out.println("\nERROR EN EL PROGRAMA:");
            e.printStackTrace();
        }
    }

    // FUNCIONES AUXILIARES
    // (Todas conservadas y algunas mejoradas con comentarios)

    public static LinkedHashMap<String, List<String>> readGrammar(String fname) throws Exception {
        LinkedHashMap<String, List<String>> G = new LinkedHashMap<>();

        File f = new File(fname);
        if (!f.exists())
            throw new Exception("No se encontró archivo: " + fname);

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            // Formato: A -> a B | b | ...
            String[] parts = line.split("->");
            String left = parts[0].trim();
            String[] right = parts[1].split("\\|");

            List<String> prods = new ArrayList<>();
            for (String r : right)
                prods.add(r.trim());

            G.put(left, prods);
        }

        br.close();
        return G;
    }


    public static void printGrammar(Map<String, List<String>> G) {
        for (String A : G.keySet()) {
            System.out.println(A + " -> " + String.join(" | ", G.get(A)));
        }
    }


    public static void writeGrammarToFile(Map<String, List<String>> G, String fname) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(fname));
        for (String A : G.keySet()) {
            pw.println(A + " -> " + String.join(" | ", G.get(A)));
        }
        pw.close();
    }


    public static void printFirst(Map<String, Set<String>> first) {
        for (String A : first.keySet()) {
            System.out.println("FIRST(" + A + ") = " + first.get(A));
        }
    }


    public static void writeFirstToFile(Map<String, Set<String>> first, String fname) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(fname));
        for (String A : first.keySet()) {
            pw.println("FIRST(" + A + ") = " + first.get(A));
        }
        pw.close();
    }


    public static void writeText(String fname, String contenido) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(fname));
        pw.println(contenido);
        pw.close();
    }

}
