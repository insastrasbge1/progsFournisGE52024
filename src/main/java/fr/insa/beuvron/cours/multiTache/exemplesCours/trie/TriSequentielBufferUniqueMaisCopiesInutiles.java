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
 * Un trie "Divide and Conquer" avec l'utilisation d'un buffer unique pour
 * les fusions.
 * <pre>
 * <p> Note : on fait la fusion dans le buffer, puis on recopie le résultat
 * de la fusion dans le tableau d'origine. Cette copie peut être évitée
 * en modifiant un peu la récursion.
 * Voir {@link TriSequentiel} pour une Version qui utilise un unique buffer
 * sans copies inutiles.
 * </p>
 * <p> algo :
 * <ul>
 *   <li> trie(t,min,max,t2) : trie le tableau t pour les élements {@code min <= i <= max} :
 *     <li> t2 est un tableau de taille {@code >= } t
 *     <li> {@code if (max-min) >  0}
 *     <ul>
 *       <li> {@code milieu <- (max + min) / 2 }</li>
 *       <li> trie(t,min,milieu,t2) </li>
 *       <li> trie(t,milieu+1,max,t2) </li>
 *       <li> fusion(t,min,max,t2) </li>
 *     </ul>
 *     </li>
 *   </li>
 *   <li> fusion(t,min,max,t2) : 
 *     <li> {@code milieu <- (max + min) / 2 }</li>
 *     <li> suppose que les élements {@code min <= i <= milieu} sont triés </li>
 *     <li> suppose que les élements {@code milieu < i <= max} sont triés </li>
 *     <li> fusionne les éléments {@code min <= i <= max} de t dans t2 </li>
 *     <li> recopie les éléments {@code min <= i <= max} de t2 dans t (PAS SOUHAITABLE)</li>
 *   </li>
 * </ul>
 * </p>
 * </pre>
 *
 * @author francois
 */
public class TriSequentielBufferUniqueMaisCopiesInutiles {

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
        tri(tab,new int[tab.length]);
    }

    /**
     *
     * @param tab
     */
    public static void tri(int[] tab,int[] buf) {
        triBorne(tab, 0, tab.length - 1,buf);
    }

    /**
     *
     * @param tab
     * @param min
     * @param max
     */
    public static void triBorne(int[] tab, int min, int max,int[] buf) {
//        System.out.println("sorting " + Arrays.toString(tab) + " between " + min + " and " + max);
        if (max - min > 0) {
            int milieu = (max + min) / 2;
            triBorne(tab, min, milieu,buf);
            triBorne(tab, milieu + 1, max,buf);
            fusion(tab, min, max,buf);
        }
//        System.out.println("sorted " + Arrays.toString(tab) + " between " + min + " and " + max);
    }

    private static void fusion(int[] tab, int min, int max,int[] buf) {
//        int[] fu = new int[max - min + 1];
        int milieu = (max + min) / 2;
        int cur1 = min;
        int cur2 = milieu + 1;
        for (int i = min; i <= max; i++) {
            if (cur1 > milieu) {
                // plus rien dans la première partie
                buf[i] = tab[cur2];
                cur2++;
            } else if (cur2 > max) {
                // plus rien dans la seconde partie
                buf[i] = tab[cur1];
                cur1++;
            } else if (tab[cur1] <= tab[cur2]) {
                buf[i] = tab[cur1];
                cur1++;
            } else {
                buf[i] = tab[cur2];
                cur2++;
            }
        }
        for (int i = min; i <= max; i++) {
            tab[i] = buf[i];
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
        int[] t = TriSequentielBufferUniqueMaisCopiesInutiles.tabAlea(size, bmax);
        System.out.println("trie tableau taille : " + size
                + " (0 <= e < " + bmax + ")");
        long deb = System.currentTimeMillis();
        tri(t);
        long duree = System.currentTimeMillis() - deb;
        System.out.println("test : " + TriSequentielBufferUniqueMaisCopiesInutiles.testTrie(t));
        System.out.println("in " + duree + " ms");

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        test(10000000, TriSequentielBufferUniqueMaisCopiesInutiles.BMAX);
    }

}
