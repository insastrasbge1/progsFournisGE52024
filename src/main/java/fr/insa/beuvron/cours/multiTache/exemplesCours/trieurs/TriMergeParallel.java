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

import fr.insa.beuvron.cours.multiTache.exemplesCours.trie.TriSequentiel;
import fr.insa.beuvron.cours.multiTache.utils.ThreadUtils;
import java.util.concurrent.atomic.AtomicInteger;

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
public class TriMergeParallel extends Trieur {

    private int[] buf;
    private TrieStatus statusPremiereMoitie;
    private TrieStatus statusSecondeMoitie;
    private TrieStatus finalStatus;

    public TriMergeParallel(long timeLimit, AtomicInteger threadCounter, int[] tab, int minIndex, int maxIndex, int[] buf) {
        super(timeLimit, threadCounter, tab, minIndex, maxIndex);
        this.buf = buf;
    }

    public TriMergeParallel(long timeLimit, AtomicInteger threadCounter, int[] tab, int minIndex, int maxIndex) {
        this(timeLimit, threadCounter, tab, minIndex, maxIndex, new int[tab.length]);
    }

    /**
     * un trieur partiel partage le temps limite, le compteur de thread le
     * tableau et le buffer avec son créateur.
     */
    protected TriMergeParallel(TriMergeParallel createur, int minIndex, int maxIndex) {
        this(createur.timeLimit, createur.threadCounter, createur.tab, minIndex, maxIndex, createur.buf);
    }

    @Override
    public TrieStatus effectueTrie() {
        try {
            this.ensureNoTimeout();
            if (max - min < 4) {
                for (int i = min; i < max - 1; i++) {
                    for (int j = i + 1; j < max; j++) {
                        if (tab[i] > tab[j]) {
                            int temp = tab[i];
                            tab[i] = tab[j];
                            tab[j] = temp;
                        }
                    }
                }
                this.finalStatus = TrieStatus.OK;
                return this.finalStatus;
            } else {
                int m2 = (max + min) / 2;
                int m1 = (m2 + min) / 2;
                int m3 = (max + m2) / 2;
                this.declareNewThread(3);
                Thread premiereMoitie = new Thread(() -> {
                    TriMergeParallel premierQuart = new TriMergeParallel(this, min, m1);
                    TriMergeParallel secondQuart = new TriMergeParallel(this, m1, m2);
                    Thread t1 = new Thread(() -> premierQuart.effectueTrie());
                    t1.start();
                    // j'effectue le second quart du trie dans le thread courant
                    // (le thread affecté au trie de la première moitié)
                    secondQuart.effectueTrie();
                    ThreadUtils.joinSansInterrupt(t1);
                    if (premierQuart.finalStatus == TrieStatus.OK
                            && secondQuart.finalStatus == TrieStatus.OK) {
                        TriMergeSequentiel.fusion(tab, min, m1, m2, buf);
                        this.statusPremiereMoitie = TrieStatus.OK;
                    } else {
                        if (premierQuart.finalStatus != TrieStatus.OK) {
                            this.statusPremiereMoitie = premierQuart.finalStatus;
                        } else {
                            this.statusPremiereMoitie = secondQuart.finalStatus;
                        }
                    }
                });
                Thread secondeMoitie = new Thread(() -> {
                });
                premiereMoitie.start();
                // j'effectue la seconde moitié dans le thread courant
                // (le main thread)
                TriMergeParallel troisemeQuart = new TriMergeParallel(this, m2, m3);
                TriMergeParallel quatriemeQuart = new TriMergeParallel(this, m3, max);
                Thread t3 = new Thread(() -> troisemeQuart.effectueTrie());
                t3.start();
                quatriemeQuart.effectueTrie();
                ThreadUtils.joinSansInterrupt(t3);
                if (troisemeQuart.finalStatus == TrieStatus.OK
                        && quatriemeQuart.finalStatus == TrieStatus.OK) {
                    TriMergeSequentiel.fusion(tab, m2, m3, max, buf);
                    this.statusSecondeMoitie = TrieStatus.OK;
                } else {
                    if (troisemeQuart.finalStatus != TrieStatus.OK) {
                        this.statusSecondeMoitie = troisemeQuart.finalStatus;
                    } else {
                        this.statusSecondeMoitie = quatriemeQuart.finalStatus;
                    }
                }
                premiereMoitie.join();
                secondeMoitie.join();
                if (this.statusPremiereMoitie != TrieStatus.OK) {
                    this.finalStatus = this.statusPremiereMoitie;
                    return this.finalStatus;
                }
                if (this.statusSecondeMoitie != TrieStatus.OK) {
                    this.finalStatus = this.statusSecondeMoitie;
                    return this.finalStatus;
                }
                TriMergeSequentiel.fusion(buf, min, m2, max, tab);
                this.finalStatus = TrieStatus.OK;
                return this.finalStatus;
            }
        } catch (TimeoutException ex) {
            this.finalStatus = TrieStatus.TIMEOUT;
            return this.finalStatus;
        } catch (TooMuchThreadException ex) {
            this.finalStatus = TrieStatus.TOO_MANY_THREADS;
            return this.finalStatus;
        } catch (InterruptedException ex) {
            this.finalStatus = TrieStatus.INTERNAL_ERROR;
            return this.finalStatus;
        } catch (Throwable ex) {
            this.finalStatus = TrieStatus.INTERNAL_ERROR;
            return this.finalStatus;
        }
    }

    public static void test() {
        int[] tt = Testeur.tabAlea(32, 20);
        long timeLimit = Long.MAX_VALUE;
        AtomicInteger maxThread = new AtomicInteger(100);
        TriMergeParallel trieur = new TriMergeParallel(timeLimit, maxThread, tt, 0, tt.length);
        TrieStatus res = trieur.effectueTrie();
        System.out.println("res = " + res);
        System.out.println("test : " + Testeur.verifieTrie(tt));

    }

    public static void test2() {
        for (int i = 0; i < 51; i++) {
            int[] tt = Testeur.tabAlea(i, 20);
            long timeLimit = Long.MAX_VALUE;
            AtomicInteger maxThread = new AtomicInteger(20);
            TriMergeParallel trieur = new TriMergeParallel(timeLimit, maxThread, tt, 0, tt.length);
            TrieStatus res = trieur.effectueTrie();
            if (res != TrieStatus.OK) {
                System.out.println("i = " + i + "res = " + res);
            }
            if (!Testeur.verifieTrie(tt)) {
                System.out.println("i = " + i + "test : false");

            }
        }
    }

    public static void main(String[] args) {
        test();
    }

}
