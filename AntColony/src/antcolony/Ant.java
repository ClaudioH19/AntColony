/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package antcolony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author claud
 */
public class Ant extends Thread {

    static Mapa m;
    ArrayList<Integer> tour;
    HashMap<Integer, Integer> visitados = new HashMap();

    double w;
    int estaEn;
    int cantNodos;
    double alfa;
    double beta;
    int iteration;
    double decreaseRatio;
    boolean random;

    public Ant(int cantNodos, Mapa m, double d, double alfa, double beta, boolean random, int iteration) {
        this.tour = new ArrayList();
        this.visitados = new HashMap();
        this.cantNodos = cantNodos;
        this.m = m;
        this.decreaseRatio = d;
        this.alfa = alfa;
        this.beta = beta;
        this.random = random;
        this.iteration = iteration;
    }

    @Override
    public void run() {

        hacerTour();

    }

    public void hacerTour() {
        this.tour = new ArrayList();
        this.visitados = new HashMap();

        //activarlo random favorece la exploracion temprana
        if (random) {
            this.estaEn = (int) (Math.random() * this.cantNodos);
        } else {
            this.estaEn = 0;
        }

        this.tour.add(estaEn);
        visitados.put(estaEn, estaEn);
        double w = 0;
        int inicio = this.estaEn;
        int cantVisitados = 1;

        //elegir un nodo
        //while no queden nodos por visitar
        while (cantVisitados < cantNodos) {
            //calcular probabilidades de posibles nodos a visitar
            double[] probabilidades = new double[cantNodos];
            double total = 0;
            for (int i = 0; i < cantNodos; i++) {

                probabilidades[i] = probaNodo(estaEn, i);
                total += probabilidades[i];
                //System.out.println(total);
            }

            if (total > 0) {
                // Normalizar las probabilidades: dividir cada una por el total
                for (int i = 0; i < cantNodos; i++) {
                        probabilidades[i] /= total;
                }
            }

            //elegir un num
            double num = Math.random();

            //simular la rutela
            double sum = 0;
            int select = -1; //nodo a visitar
            for (int i = 0; i < cantNodos && select == -1; i++) {
                sum += probabilidades[i];
                //System.out.println("SUM: " + sum);
                if (num <= sum && i != estaEn && !visitados.containsKey(i)) {
                    //selecionamos nodo i si el num mayor a acumulado suma
                    select = i;
                    break;
                }
            }
            //System.out.println("sum: " + sum);
            //System.out.println(num);
            //si no sale alguien, por salazar
            if (select == -1) {
                do {
                    int proximo = (int) (Math.random() * cantNodos);
                    if (!visitados.containsKey(proximo)) {
                        select = proximo;
                    }
                } while (select == -1);
            }

            //visitar nodo
            //antes calcular el peso de ir a otro nodo
            w += m.distances[estaEn][select];
            //viajar
            tour.add(select);
            visitados.put(select, select);
            estaEn = select;
            cantVisitados++;

        }
        //completar tour ir al origen
        visitados.put(inicio, inicio);
        tour.add(inicio);
        w += m.distances[estaEn][inicio];
        //System.out.println(visitados);
        this.w = w;
    }

    public double probaNodo(int i, int j) {
        if (i == j || visitados.containsKey(j)) {
            return 0;
        }

        //                  tau                 no es una n pero se parece
        double heuristica = m.dtcpowtobeta[i][j] * m.pheromonespowtoalfa[i][j];
        //double heuristica = Math.pow((1 / m.distances[i][j]), beta) * Math.pow(m.pheromones[i][j], alfa);

        double sumatoria = 0.0;
        for (int k = 0; k < cantNodos; k++) {
            if (!visitados.containsKey(k)) {
                //sumatoria += Math.pow((1 / m.distances[i][k]), beta) * Math.pow(m.pheromones[i][k], alfa);
                sumatoria += m.dtcpowtobeta[i][k] * m.pheromonespowtoalfa[i][k];
            }
        }
        if (sumatoria == 0)//cuando la sumatoria se va a cero, hacer 0 la probabilidad
        {
            return 0;
        }
        //System.out.println(heuristica/sumatoria);
        //despues hay que normalizar las probabilidades
        return heuristica / sumatoria;
    }

}
