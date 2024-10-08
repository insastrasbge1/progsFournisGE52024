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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Un tri par division-fusion parallèle, mais si la taille à triée est inférieure
 * à un certain seuil, on lance un tri séquentiel pour éviter la création de
 * trop nombreux threads comme dans le cas de {@link TriMergeParallel}.
 * @author francois
 */
public class TriMergeSemiParallelCreationBuffersInutiles extends Trieur implements Runnable {

    private TrieStatus keepStatus;
    
    /**
     * si le nombre d'élements à trier n est inférieur à minNoumberOfElemsForParallel
     * on déclenche un tri séquentiel.
     */
    private int minNoumberOfElemsForParallel;
    
    public static int defaultMinParallel(int tailleTableau) {
        return Math.max(4, tailleTableau/Runtime.getRuntime().availableProcessors());
    }

    public TriMergeSemiParallelCreationBuffersInutiles(long timeLimit, AtomicInteger threadCounter, int[] tab,
            int minIndex, int maxIndex,
            int minNoumberOfElemsForParallel) {
        super(timeLimit, threadCounter, tab, minIndex, maxIndex);
        this.minNoumberOfElemsForParallel = minNoumberOfElemsForParallel;
    }

    public TriMergeSemiParallelCreationBuffersInutiles(long timeLimit, AtomicInteger threadCounter, int[] tab,
            int minIndex, int maxIndex) {
        this(timeLimit, threadCounter, tab, minIndex, maxIndex, defaultMinParallel(tab.length));
    }

    /**
     * un trieur partiel partage le temps limite, le compteur de thread et le
     * tableau avec son créateur.
     */
    protected TriMergeSemiParallelCreationBuffersInutiles(TriMergeSemiParallelCreationBuffersInutiles createur, int minIndex, int maxIndex) {
        this(createur.timeLimit, createur.threadCounter, createur.tab, minIndex, maxIndex,
                createur.minNoumberOfElemsForParallel);
    }

    @Override
    public void run() {
        try {
            this.ensureNoTimeout();
            if (max - min < this.minNoumberOfElemsForParallel) {
                TriMergeSequentielCreationBuffersInutiles seq = new TriMergeSequentielCreationBuffersInutiles(this, min, max);
                this.keepStatus = seq.effectueTrie();
            } else {
                int milieu = (max + min) / 2;
                TriMergeSemiParallelCreationBuffersInutiles gauche = new TriMergeSemiParallelCreationBuffersInutiles(this, min, milieu);
                this.declareNewThread();
                Thread bGauche = new Thread(gauche);
                bGauche.start();
                TriMergeSemiParallelCreationBuffersInutiles droite = new TriMergeSemiParallelCreationBuffersInutiles(this, milieu, max);
                this.declareNewThread();
                Thread bDroite = new Thread(droite);
                bDroite.start();
                bGauche.join();
                bDroite.join();
                if (gauche.keepStatus == TrieStatus.OK && droite.keepStatus == keepStatus.OK) {
                    TriMergeSequentielCreationBuffersInutiles.fusion(tab, min, milieu, max);
                    this.keepStatus = TrieStatus.OK;
                } else {
                    if (gauche.keepStatus != TrieStatus.OK) {
                        this.keepStatus = gauche.keepStatus;
                    } else {
                        this.keepStatus = droite.keepStatus;
                    }
                }
            }
        } catch (TimeoutException ex) {
            this.keepStatus = TrieStatus.TIMEOUT;
        } catch (TooMuchThreadException ex) {
            this.keepStatus = TrieStatus.TOO_MANY_THREADS;
        } catch (InterruptedException ex) {
            this.keepStatus = TrieStatus.INTERNAL_ERROR;
        }
    }

    @Override
    public TrieStatus effectueTrie() {
        this.run();
        return this.keepStatus;
    }

}
