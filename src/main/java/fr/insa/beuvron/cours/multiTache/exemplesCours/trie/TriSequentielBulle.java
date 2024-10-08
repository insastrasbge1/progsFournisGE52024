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
 * Le tri à bulle. Très connu, mais moins efficace que le tri par division-fusion.
 * Redonné ici pour voir son inéficacité quand le nombre de cases à trier devient
 * grand.
 * @author francois
 */
public class TriSequentielBulle {

    /**
     *
     * @param tab
     */
    public static void tri(int[] tab) {
        for(int i = 0 ; i < tab.length - 1 ; i ++) {
            for(int j = i+1 ; j < tab.length ; j ++) {
                if(tab[i] > tab[j]) {
                    int temp = tab[i];
                    tab[i] = tab[j];
                    tab[j] = temp;
                }
            }
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
        tri(t);
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
