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
 * <p> voir {@link TriMergeParallel} pour les principes de parallèlisation
 * </p>
 * <p> la seule différence est que l'on arrête ici la récursion lorsque
 * la taille du tableau à trier est inférieure à une valeur limite pour
 * éviter de créer de trop nombreux threads
 * </p>

 * @author francois
 */
public class TriMergeSemiParallel extends Trieur {

    private int[] buf;
    private TrieStatus statusPremiereMoitie;
    private TrieStatus statusSecondeMoitie;
    private TrieStatus finalStatus;

    /**
     * si le nombre d'élements à trier n est inférieur à minNoumberOfElemsForParallel
     * on déclenche un tri séquentiel.
     */
    private int minNoumberOfElemsForParallel;
    
    public static int defaultMinParallel(int tailleTableau) {
        return Math.max(4, tailleTableau/Runtime.getRuntime().availableProcessors());
    }

    public TriMergeSemiParallel(long timeLimit, AtomicInteger threadCounter, int[] tab,
            int minIndex, int maxIndex,
            int minNoumberOfElemsForParallel,
            int[] buf) {
        super(timeLimit, threadCounter, tab, minIndex, maxIndex);
        this.minNoumberOfElemsForParallel = minNoumberOfElemsForParallel;
        this.buf = buf;
    }

    public TriMergeSemiParallel(long timeLimit, AtomicInteger threadCounter, int[] tab,
            int minIndex, int maxIndex,
            int[] buf) {
        this(timeLimit, threadCounter, tab, minIndex, maxIndex, defaultMinParallel(tab.length), buf);
    }

    public TriMergeSemiParallel(long timeLimit, AtomicInteger threadCounter, int[] tab, int minIndex, int maxIndex) {
        this(timeLimit, threadCounter, tab, minIndex, maxIndex, new int[tab.length]);
    }

    public TriMergeSemiParallel(long timeLimit, AtomicInteger threadCounter, int[] tab,
            int minIndex, int maxIndex,
            int minNoumberOfElemsForParallel) {
        this(timeLimit, threadCounter, tab, minIndex, maxIndex,minNoumberOfElemsForParallel, new int[tab.length]);
    }

    /**
     * un trieur partiel partage le temps limite, le compteur de thread le
     * tableau et le buffer avec son créateur.
     */
    protected TriMergeSemiParallel(TriMergeSemiParallel createur, int minIndex, int maxIndex) {
        this(createur.timeLimit, createur.threadCounter, createur.tab, minIndex, maxIndex, createur.minNoumberOfElemsForParallel,createur.buf);
    }

    @Override
    public TrieStatus effectueTrie() {
        try {
            this.ensureNoTimeout();
            if (max - min < this.minNoumberOfElemsForParallel) {
                TriMergeSequentielCreationBuffersInutiles seq = new TriMergeSequentielCreationBuffersInutiles(this, min, max);
                this.finalStatus = seq.effectueTrie();
                return this.finalStatus;
            } else {
                int m2 = (max + min) / 2;
                int m1 = (m2 + min) / 2;
                int m3 = (max + m2) / 2;
                this.declareNewThread(3);
                Thread premiereMoitie = new Thread(() -> {
                    TriMergeSemiParallel premierQuart = new TriMergeSemiParallel(this, min, m1);
                    TriMergeSemiParallel secondQuart = new TriMergeSemiParallel(this, m1, m2);
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
                TriMergeSemiParallel troisemeQuart = new TriMergeSemiParallel(this, m2, m3);
                TriMergeSemiParallel quatriemeQuart = new TriMergeSemiParallel(this, m3, max);
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
        TriMergeSemiParallel trieur = new TriMergeSemiParallel(timeLimit, maxThread, tt, 0, tt.length);
        TrieStatus res = trieur.effectueTrie();
        System.out.println("res = " + res);
        System.out.println("test : " + Testeur.verifieTrie(tt));

    }

    public static void test2() {
        for (int i = 0; i < 51; i++) {
            int[] tt = Testeur.tabAlea(i, 20);
            long timeLimit = Long.MAX_VALUE;
            AtomicInteger maxThread = new AtomicInteger(20);
            TriMergeSemiParallel trieur = new TriMergeSemiParallel(timeLimit, maxThread, tt, 0, tt.length);
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
