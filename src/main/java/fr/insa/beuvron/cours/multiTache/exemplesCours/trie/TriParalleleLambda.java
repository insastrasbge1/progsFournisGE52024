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

import fr.insa.beuvron.cours.multiTache.utils.ThreadUtils;

/**
 * Un trie "Divide and Conquer" avec l'utilisation d'un buffer unique pour les
 * fusions.
 * <pre>
 * <p> principe : si l'on divise en deux le tableau, que l'on trie les deux
 * moitiées, lorsque l'on fait la fusion, on se retrouve d'abord avec le
 * résultat de la fusion dans le buffer et on est ensuite obligé de copier
 * du buffer vers le tableau pour obtenir le résultat final dans le tableau.
 * </p>
 * <p> pour éviter cette copie, on va diviser le tableau non pas en deux, mais
 * en quatre sous-parties : st1,st2,st3, et st4 de taille environ égales.
 * on trie chacune des sous-parties, puis on fusionne st1 et st2 dans le buffer
 * et également st3 et st4 dans le buffer. Le buffer contient alors deux sous
 * partie st1+2 et st3+4 triées. Il nous suffier alors de faire la fusion
 * de ses deux partie du buffer vers le tableau pour avoir le résultat final
 * du trie dans le tableau sans avoir fait de copies inutiles.
 * <p> algo :
 * <ul>
 *   <li> trie(t,min,max,t2) : trie le tableau t pour les élements {@code min <= i <= max} :
 *     <li> t2 est un tableau de taille {@code >= } t
 *     <li> {@code if (max-min) <  4}
 *     <ul>
 *       <li> faire un trie à bulle de ses max trois éléments de t <li>
 *     </ul>
 *     <li> sinon </li>
 *     <ul>
 *       <li> {@code m2 <- (max + min) / 2 }</li>
 *       <li> {@code m1 <- (m2 + min) / 2 }</li>
 *       <li> {@code m3 <- (max + m2 + 1) / 2 }</li>
 *       <li> trie(t,min,m1,t2) </li>
 *       <li> trie(t,m1+1,m2,t2) </li>
 *       <li> trie(t,m2+1,m3,t2) </li>
 *       <li> trie(t,m3+1,max,t2) </li>
 *       <li> fusion(t,min,m2,t2) </li>
 *       <li> fusion(t,m2+1,max,t2) </li>
 *       <li> fusion(t2,min,max,t) </li>
 *     </ul>
 *     </li>
 *   </li>
 *   <li> fusion(t,min,max,t2) :
 *     <li> {@code milieu <- (max + min) / 2 }</li>
 *     <li> suppose que les élements {@code min <= i <= milieu} sont triés </li>
 *     <li> suppose que les élements {@code milieu < i <= max} sont triés </li>
 *     <li> fusionne les éléments {@code min <= i <= max} de t dans t2 </li>
 *   </li>
 * </ul>
 * </p>
 * </pre>
 *
 * @author francois
 */
public class TriParalleleLambda {

    /**
     *
     * @param tab
     */
    public static void tri(int[] tab) {
        tri(tab, new int[tab.length]);
    }

    /**
     *
     * @param tab
     */
    public static void tri(int[] tab, int[] buf) {
        triBorne(tab, 0, tab.length - 1, buf);
    }

    /**
     *
     * @param tab
     * @param min
     * @param max
     */
    public static void triBorne(int[] tab, int min, int max, int[] buf) {
        if (max - min < 3) {
            for (int i = min; i < max; i++) {
                for (int j = i; j <= max; j++) {
                    if (tab[i] > tab[j]) {
                        int temp = tab[i];
                        tab[i] = tab[j];
                        tab[j] = temp;
                    }
                }
            }
        } else {
            int m2 = (max + min) / 2;
            int m1 = (m2 + min) / 2;
            int m3 = (max + m2 + 1) / 2;
            Thread premiereMoitie = new Thread(() -> {
                Thread premierQuart = new Thread(() -> {
                    triBorne(tab, min, m1, buf);
                });
                Thread secondQuart = new Thread(() -> {
                    triBorne(tab, m1 + 1, m2, buf);
                });
                premierQuart.start();
                secondQuart.start();
                ThreadUtils.joinSansInterrupt(premierQuart);
                ThreadUtils.joinSansInterrupt(secondQuart);
                TriSequentiel.fusion(tab, min, m2, buf);
            });
            Thread secondeMoitie = new Thread(() -> {
                Thread troisiemeQuart = new Thread(() -> {
                    triBorne(tab, m2 + 1, m3, buf);
                });
                Thread quatriemeQuart = new Thread(() -> {
                    triBorne(tab, m3 + 1, max, buf);
                });
                troisiemeQuart.start();
                quatriemeQuart.start();
                ThreadUtils.joinSansInterrupt(troisiemeQuart);
                ThreadUtils.joinSansInterrupt(quatriemeQuart);
                TriSequentiel.fusion(tab, m2+1, max, buf);
            });
            premiereMoitie.start();
            secondeMoitie.start();
            ThreadUtils.joinSansInterrupt(premiereMoitie);
            ThreadUtils.joinSansInterrupt(secondeMoitie);
            TriSequentiel.fusion(buf, min, max, tab);
        }
//        System.out.println("sorted " + Arrays.toString(tab) + " between " + min + " and " + max);
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
        int[] t = TriParalleleLambda.tabAlea(size, bmax);
        System.out.println("trie tableau taille : " + size
                + " (0 <= e < " + bmax + ")");
        long deb = System.currentTimeMillis();
        tri(t);
        long duree = System.currentTimeMillis() - deb;
        System.out.println("test : " + TriParalleleLambda.testTrie(t));
        System.out.println("in " + duree + " ms");

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        test(10000, TriSequentiel.BMAX);
    }

}
