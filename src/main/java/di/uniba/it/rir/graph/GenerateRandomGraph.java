/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.rir.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author pierpaolo
 */
public class GenerateRandomGraph {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            int v = Integer.parseInt(args[0]);
            int e = Integer.parseInt(args[1]);
            System.out.println("Vertices " + v + ", edges " + e);
            BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
            Random rnd = new Random();
            int i = 0;
            while (i < e) {
                int v1 = rnd.nextInt(v);
                int v2 = rnd.nextInt(v);
                if (v1 != v2) {
                    writer.append(String.valueOf(v1)).append(" ").append(String.valueOf(v2));
                    writer.newLine();
                    i++;
                }
            }
            writer.close();
        }
    }

}
