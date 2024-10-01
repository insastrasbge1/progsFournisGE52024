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
 * Un trie "Divide and Conquer" avec création dynamique de buffers pour la
 * fusion.
 * <pre>
 * <p> Note : à chaque fois, on recrée un tableau d'entier pour la fusion.
 * Cela n'est clairement pas souhaitable.
 * Voir {@link TriSequentiel} pour une Version qui utilise un unique buffer.
 * </p>
 * <p> algo :
 * <ul>
 *   <li> trie(t,min,max) : trie le tableau t pour les élements {@code min <= i <= max} :
 *     <li> {@code if (max-min) >  0}
 *     <ul>
 *       <li> {@code milieu <- (max + min) / 2 }</li>
 *       <li> trie(t,min,milieu) </li>
 *       <li> trie(t,milieu+1,max) </li>
 *       <li> fusion(t,min,max) </li>
 *     </ul>
 *     </li>
 *   </li>
 *   <li> fusion(t,min,max) : 
 *     <li> {@code milieu <- (max + min) / 2 }</li>
 *     <li> suppose que les élements {@code min <= i <= milieu} sont triés </li>
 *     <li> suppose que les élements {@code milieu < i <= max} sont triés </li>
 *     <li> crée un tableau t2 de taille max-min+1 (PAS SOUHAITABLE)</li>
 *     <li> fusionne les éléments {@code min <= i <= max} de t dans t2 </li>
 *     <li> recopie les éléments de t2 {@code 0 <= j <= max-min} 
 *          dans les éléments de t {@code min <= i <= max} (PAS SOUHAITABLE NON PLUS)</li>
 *   </li>
 * </ul>
 * </p>
 * </pre>
 *
 * @author francois
 */
public class TriSequentielCreationBuffersInutiles {

    /**
     * todoDoc.
     */
    public static int SIZE = 100000000;

    /**
     * todoDoc.
     */
    public static int BMAX = 5000;

    /**
     *
     * @param tab
     */
    public static void tri(int[] tab) {
        triBorne(tab, 0, tab.length - 1);
    }

    /**
     *
     * @param tab
     * @param min
     * @param max
     */
    public static void triBorne(int[] tab, int min, int max) {
//        System.out.println("sorting " + Arrays.toString(tab) + " between " + min + " and " + max);
        if (max - min > 0) {
            int milieu = (max + min) / 2;
            triBorne(tab, min, milieu);
            triBorne(tab, milieu + 1, max);
            fusion(tab, min, max);
        }
//        System.out.println("sorted " + Arrays.toString(tab) + " between " + min + " and " + max);
    }

    private static void fusion(int[] tab, int min, int max) {
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
     * @param tab
     * @return
     */
    public static boolean testTrie(int[] tab) {
        boolean res = true;
        int i = 0;
        while (res && i < tab.length - 1) {
            res = tab[i] <= tab[i + 1];
            i++;
        }
        return res;
    }

    /**
     *
     * @param size
     * @param borneMax
     * @return
     */
    public static int[] tabAlea(int size, int borneMax) {
        int[] res = new int[size];
        for (int i = 0; i < res.length; i++) {
            res[i] = (int) (Math.random() * borneMax);
        }
        return res;
    }

    /**
     *
     * @param size
     * @param bmax
     */
    public static void test(int size, int bmax) {
        int[] t = TriSequentielCreationBuffersInutiles.tabAlea(size, bmax);
        System.out.println("trie tableau taille : " + size
                + " (0 <= e < " + bmax + ")");
        long deb = System.currentTimeMillis();
        tri(t);
        long duree = System.currentTimeMillis() - deb;
        System.out.println("test : " + TriSequentielCreationBuffersInutiles.testTrie(t));
        System.out.println("in " + duree + " ms");

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        test(10000000, TriSequentielCreationBuffersInutiles.BMAX);
    }

}
