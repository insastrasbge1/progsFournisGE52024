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
package fr.insa.beuvron.cours.multiTache.exemplesCours.trieurs;

/**
 * TODO !!!
 *
 * @author francois
 */
public class TriDecoupeParNbrCoeurs {

//    private static int[] triParCoeur(int[] tab) {
//        int nbrCoeur = Runtime.getRuntime().availableProcessors();
//        if (nbrCoeur > tab.length) {
//            int[] res = Arrays.copyOf(tab, tab.length);
//            TrieDirectBorne trieur = new TrieDirectBorne(res, 0, res.length);
//            trieur.run();
//            return trieur.getTab();
//        } else {
//            TrieDirectBorne[] trieurs = new TrieDirectBorne[nbrCoeur];
//            int curNbrBout = nbrCoeur;
//            int curReste = tab.length;
//            int[] limites = new int[nbrCoeur + 1];
//            limites[0] = 0;
//            for (int i = 0; i < nbrCoeur; i++) {
//                int tbout = curReste / curNbrBout;
//                limites[i + 1] = limites[i] + tbout;
//                trieurs[i] = new TrieDirectBorne(tab, limites[i], limites[i + 1]);
//                trieurs[i].start();
//                curReste = curReste - tbout;
//                curNbrBout = curNbrBout - 1;
//            }
//            for (int i = 0; i < nbrCoeur; i++) {
//                ThreadUtils.joinSansInterrupt(trieurs[i]);
//            }
//            for (int i = 0; i < nbrCoeur; i++) {
//            }
//            return fusionMultiple(tab, limites);
//        }
//    }
//
//    /**
//     *
//     * @param tab
//     * @param limites
//     * @return
//     */
//    public static int[] fusionMultiple(int[] tab, int[] limites) {
//        boolean encore = true;
//        int[] res = new int[tab.length];
//        int[] curs = Arrays.copyOf(limites, limites.length - 1);
//        int pos = 0;
//        while (encore) {
//            int j = 0;
//            while (j < curs.length && curs[j] >= limites[j + 1]) {
//                j++;
//            }
//            if (j == curs.length) {
//                encore = false;
//            } else {
//                int jmin = j;
//                for (j = jmin + 1; j < curs.length; j++) {
//                    if (curs[j] < limites[j + 1]) {
//                        if (tab[j] < tab[jmin]) {
//                            jmin = j;
//                        }
//                    }
//                }
//                res[pos] = tab[jmin];
//                pos++;
//                curs[jmin]++;
//            }
//        }
//        return res;
//    }
//
//    /**
//     *
//     * @param size
//     * @param bmax
//     */
//    public static void test(int size,int bmax) {
//        int[] t = TriSequentiel.tabAlea(size,bmax);
//        System.out.println("trie tableau taille : " + size
//                + " (0 <= e < " + bmax + ")");
//        long deb = System.currentTimeMillis();
//        int[] res = TriDecoupeParNbrCoeurs.triParCoeur(t);
//        long duree = System.currentTimeMillis() - deb;
//        System.out.println("test : " + TriSequentiel.testTrie(res));
//        System.out.println("in " + duree + " ms");
//
//    }
//
//    /**
//     *
//     * @param args
//     */
//    public static void main(String[] args) {
//        test(TriSequentiel.SIZE,TriSequentiel.BMAX);
//    }

}
