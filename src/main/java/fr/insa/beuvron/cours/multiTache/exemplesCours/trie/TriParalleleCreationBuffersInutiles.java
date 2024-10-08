/*
 Copyright 2000-2014 Francois de Bertrand de Beuvron

 This file is part of CoursBeuvron.

 CoursBeuvron is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 CoursBeuvron is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.beuvron.cours.multiTache.exemplesCours.trie;

/**
 * Tri parallele.
 * On aurait pu aussi avec une classe qui implémente seulement l'interface
 * Runnable. Ici, on en a fait une sous-classe de Thread.
 * La méthode run() est l'équivalent de la méthode triBorne de la classe
 * TriSequentiel. Comme la méthode run() d'un Runnable n'a pas de paramètre,
 * on place ceux-ci comme attribut de la classe qui sont initialisé lors de 
 * la création du Thread. Puis seulement on lance effectivement le calcul
 * en parallèle avec la méthode start() de Thread.
 * Notez qu'il faut attendre (join) que les tris des deux moitiées soit finis
 * avant de lancer la fusion.
 * @author francois
 */
public class TriParalleleCreationBuffersInutiles extends Thread {

    private int[] tab;
    private int min;
    private int max;

    /**
     *
     * @param tab
     * @param min
     * @param max
     */
    public TriParalleleCreationBuffersInutiles(int[] tab, int min, int max) {
        this.tab = tab;
        this.min = min;
        this.max = max;
    }

    /** todoDoc. */
    public void run() {
//        System.out.println("sorting " + Arrays.toString(tab) + " between " + min + " and " + max);
        if (max - min < 2) {
            if (tab[min] > tab[max]) {
                int temp = tab[min];
                tab[min] = tab[max];
                tab[max] = temp;
            }
        } else {
            int milieu = (max + min) / 2;
            TriParalleleCreationBuffersInutiles inf = new TriParalleleCreationBuffersInutiles(tab, min, milieu);
            TriParalleleCreationBuffersInutiles sup = new TriParalleleCreationBuffersInutiles(tab, milieu + 1, max);
            inf.start();
            sup.start();
            try {
                inf.join();
                sup.join();
            } catch (InterruptedException ex) {
                throw new Error("ne doit pas arriver");
            }
        }
//        System.out.println("sorted " + Arrays.toString(tab) + " between " + min + " and " + max);
    }

    private void fusion() {
        int[] fu = new int[max - min + 1];
        int milieu = (max + min) / 2;
        int cur1 = min;
        int cur2 = milieu + 1;
        for (int i = 0; i < fu.length; i++) {
            if (cur1 > milieu) {
                // plus rien dans la première partie
                fu[i] = tab[cur2];
                cur2++;
            } else if (cur2 > max) {
                // plus rien dans la seconde partie
                fu[i] = tab[cur1];
                cur1++;
            } else if (tab[cur1] <= tab[cur2]) {
                fu[i] = tab[cur1];
                cur1++;
            } else {
                fu[i] = tab[cur2];
                cur2++;
            }
        }
        for (int i = 0; i < fu.length; i++) {
            tab[min + i] = fu[i];
        }
    }

    /**
     *
     * @param size
     * @param bmax
     */
    public static void test(int size,int bmax) {
        int[] t = TriSequentiel.tabAlea(size,bmax);
        System.out.println("trie tableau taille : " + size
                + " (0 <= e < " + bmax + ")");
        long deb = System.currentTimeMillis();
        TriParalleleCreationBuffersInutiles tp = new TriParalleleCreationBuffersInutiles(t, 0, t.length - 1);
        tp.start();
        try {
            tp.join();
        } catch (InterruptedException ex) {
            throw new Error("ne doit pas arriver");
        }
        long duree = System.currentTimeMillis() - deb;
        System.out.println("test : " + TriSequentiel.testTrie(t));
        System.out.println("in " + duree + " ms");

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        test(100000,TriSequentiel.BMAX);
    }

}
