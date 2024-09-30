/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package antcolony;

import static antcolony.Ant.m;
import java.util.ArrayList;

/**
 *
 * @author claud
 */
public class Mapa {

    //esta clase hara de monitor con 2 metodos sincronizados para que las hormigas
    //directamente pongan sus feromonas en tiempo real, despues se evaporan
    int[][] coords;
    double[][] distances;
    double[][] pheromones;

    //arreglos para optimizar calculos
    double[][] onedvddtc;
    double[][] dtcpowtobeta;
    double[][] pheromonespowtoalfa;

    public Mapa(int cantNodos, boolean lab, double alfa, double beta) {

        this.coords = new int[cantNodos][2];
        this.distances = new double[cantNodos][cantNodos];
        this.pheromones = new double[cantNodos][cantNodos];
        this.onedvddtc = new double[cantNodos][cantNodos];
        this.dtcpowtobeta = new double[cantNodos][cantNodos];
        this.pheromonespowtoalfa = new double[cantNodos][cantNodos];

        //elegir nodos
        if (!lab) {
            for (int i = 0; i < cantNodos; i++) {
                coords[i][0] = (int) (Math.random() * 1000);
                coords[i][1] = (int) (Math.random() * 1000);
            }
        } else {
            ArregloValores arr = new ArregloValores();
            this.coords = arr.coords;
        }

        //inicializar arreglos
        for (int i = 0; i < cantNodos; i++) {
            for (int j = 0; j < cantNodos; j++) {
                if (i < j) {
                    distances[i][j] = calcdtc(coords[i][0], coords[i][1], coords[j][0], coords[j][1]);
                    distances[j][i] = calcdtc(coords[i][0], coords[i][1], coords[j][0], coords[j][1]);

                    pheromones[i][j] = 1/distances[i][j];
                    pheromones[j][i] = 1/distances[j][i];

                    double dtc = 1 / distances[i][j];
                    onedvddtc[i][j] = dtc;
                    onedvddtc[j][i] = dtc;

                    dtcpowtobeta[i][j] = Math.pow(dtc, beta);
                    dtcpowtobeta[j][i] = Math.pow(dtc, beta);

                    pheromonespowtoalfa[i][j] = Math.pow(pheromones[i][j], alfa);
                    pheromonespowtoalfa[j][i] = Math.pow(pheromones[i][j], alfa);
                }
            }
        }

    }

    public double calcdtc(int x, int y, int x1, int y1) {
        return Math.sqrt(Math.pow((x - x1), 2) + Math.pow((y - y1), 2));
    }

    public void printM() {

        for (int i = 0; i < coords.length; i++) {
            System.out.println("Nodo "+i+" "+coords[i][0] + ";" + coords[i][1]);
        }

        System.out.println("\n\n");

        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords.length; j++) {

                System.out.print(distances[i][j] + "  ");
            }
            System.out.println("");
        }

        System.out.println("\n\n");

        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords.length; j++) {

                System.out.print(pheromones[i][j] + "  ");
            }
            System.out.println("");
        }
        System.out.println("\n\n");

    }
    
    
    
    public void printNodos(){
        for (int i = 0; i < coords.length; i++) {
            System.out.println("Nodo "+i+": ("+coords[i][0]+";"+coords[i][1]+") ,");
        }
    
    }

}
