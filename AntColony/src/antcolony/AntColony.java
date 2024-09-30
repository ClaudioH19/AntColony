/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package antcolony;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claud
 */
public class AntColony {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here

        //variables de control
        int cantNodos = 20;
        boolean lab = true;
        boolean random = false;
        int cantHormigas = 20;
        double decreaseRatio = 0.9;
        int iteration = 50;
        double alfa = 1.5;//2.5
        double beta = 3;//1.5
        boolean verbose = false;
        int Q = 1;

        //encontrar argumentos
        for (int i = 0; i < args.length; i++) {

            if (args[i].toLowerCase().equals("-v")) {
                verbose = true;
            }

            if ((args[i].toLowerCase().equals("--help") || args[i].toLowerCase().equals("/?")) && args.length == 1) {
                System.out.println("Argumentos permitidos:");
                System.out.println("    -v: Activa modo verbosa");
                System.out.println("    -r: Asigna las hormigas aleatoriamente");
                System.out.println("    -a X: Define X como alfa");
                System.out.println("    -b X: Define X como beta");
                System.out.println("    -d X: X ratio de evaporación");
                System.out.println("    -i X: X número de iteraciones");
                System.out.println("    -ants X: X número de iteraciones");
                System.out.println("");
                return;
            }

            if (args[i].toLowerCase().equals("-r")) {
                random = true;
            }

            if (args[i].toLowerCase().equals("-i")) {
                iteration = Integer.parseInt(args[i + 1]);
            }

            if (args[i].toLowerCase().equals("-a")) {
                alfa = Double.parseDouble(args[i + 1]);
            }

            if (args[i].toLowerCase().equals("-b")) {
                beta = Double.parseDouble(args[i + 1]);
            }

            if (args[i].toLowerCase().equals("-ants")) {
                cantHormigas = Integer.parseInt(args[i + 1]);
            }

            if (args[i].toLowerCase().equals("-d")) {
                decreaseRatio = Double.parseDouble(args[i + 1]);
            }
        }

        if (lab) {
            cantNodos = 280;
        }

        ArrayList<ArrayList> allTours = new ArrayList();
        ArrayList<Double> allW = new ArrayList();
        Mapa m = new Mapa(cantNodos, lab, alfa, beta);

        //tomar el tiempo
        long startTime = System.currentTimeMillis();

        Ant[] ants = new Ant[cantHormigas];
        if (verbose) {
            m.printM();
        }

        //--------------ALGORITMO-----------------------------------------
        for (int k = 0; k < iteration; k++) {
            System.out.println("Iteration: " + (k + 1) + "/" + iteration);
            //crear hormigas
            for (int i = 0; i < cantHormigas; i++) {
                ants[i] = new Ant(cantNodos, m, decreaseRatio, alfa, beta, random, iteration);

            }

            //parte paralela para hacer el tour
            for (int i = 0; i < cantHormigas; i++) {
                ants[i].start();
            }

            for (int i = 0; i < cantHormigas; i++) {
                try {
                    ants[i].join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AntColony.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //disminuir feromonas una vez terminados los tours
            for (int i = 0; i < cantNodos; i++) {
                for (int j = i; j < cantNodos; j++) {

                    m.pheromones[i][j] *= (1 - decreaseRatio);
                    m.pheromones[j][i] *= (1 - decreaseRatio);

                }
            }

            //depositar feromonas
            for (int i = 0; i < cantHormigas; i++) {
                for (int j = 0; j < ants[i].visitados.size() - 1; j++) {

                    m.pheromones[ants[i].visitados.get(j)][ants[i].visitados.get(j + 1)] += Q / ants[i].w;
                    m.pheromones[ants[i].visitados.get(j + 1)][ants[i].visitados.get(j)] += Q / ants[i].w;

                }
            }

            //actualizar matrixferoaalfa
            for (int i = 0; i < cantNodos; i++) {
                for (int j = 0; j < cantNodos; j++) {
                    m.pheromonespowtoalfa[i][j] = Math.pow(m.pheromones[i][j], alfa);
                    m.pheromonespowtoalfa[j][i] = Math.pow(m.pheromones[i][j], alfa);
                }
            }

            if (verbose) {
                //busqueda del mejor tour para verbose
                double menor = Double.MAX_VALUE;
                int idx = -1;
                for (int i = 0; i < cantHormigas; i++) {
                    if (ants[i].w < menor) {
                        menor = ants[i].w;
                        idx = i;
                    }
                }

                System.out.println("Mejor tour, costo: " + ants[idx].w);
                System.out.println(ants[idx].tour);
                System.out.println("");
            }
            //guardamos el tour de cada una y su costo
            for (int i = 0;
                    i < cantHormigas;
                    i++) {
                allTours.add(ants[i].tour);
                allW.add(ants[i].w);

            }          

        }

        double menor = Double.MAX_VALUE;
        int idx = -1;
        for (int i = 0; i < allTours.size(); i++) {
            if (allW.get(i) < menor) {
                menor = allW.get(i);
                idx = i;
            }
        }

        if (verbose) {
            m.printM();
        }
        long endTime = System.currentTimeMillis() - startTime;
        m.printNodos();
        System.out.println("\n\nTour:" + allTours.get(idx) + "\n\n");
        //System.out.println("Tour: " + ants[hormiga].visitados);
        System.out.println("Costo: " + menor);
        System.out.println("Tiempo de Ejecucion: " + endTime + " ms");

        String c0 = (random ? " -r" : "");
        String c1 = (verbose ? " -v" : "");

        System.out.println("Parametros ejecutados:");
        System.out.println("-ants " + cantHormigas + " -i " + iteration + " -a " + alfa + " -b " + beta + " -d " + decreaseRatio + c0 + c1);

    }

}
