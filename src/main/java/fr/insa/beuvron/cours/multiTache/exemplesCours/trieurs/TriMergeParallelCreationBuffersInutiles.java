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
 * Un tri par division-fusion parallèle.
 * Principe :
 * <pre>
 * {@code 
 * Pour trier les élément d'index min <= i < max
 * si max-min > 1
 *   . milieu = (max+min) / 2
 *   . trier les éléments d'index min <= i < milieu
 *   . trier les éléments d'index milieu <= i < max
 *   . faire la fusion des éléments min<=i<milieu (triés) et milieu<=i<max (triés)
 *       pour obtenir les éléments min<=i<max triés
 * } </pre>
 * On voit facilement la structure récursive, et que les sous-appels récursifs de
 * trier sont indépendants puisqu'ils travaillent sur des éléments disjoints 
 * On peut donc les exécuter dans deux Thread différents.
 * @author francois
 */
public class TriMergeParallelCreationBuffersInutiles extends Trieur implements Runnable {

    private TrieStatus keepStatus;

    public TriMergeParallelCreationBuffersInutiles(long timeLimit, AtomicInteger threadCounter, int[] tab, int minIndex, int maxIndex) {
        super(timeLimit, threadCounter, tab, minIndex, maxIndex);
    }

    /**
     * un trieur partiel partage le temps limite, le compteur de thread et le
     * tableau avec son créateur.
     */
    protected TriMergeParallelCreationBuffersInutiles(Trieur createur, int minIndex, int maxIndex) {
        this(createur.timeLimit, createur.threadCounter, createur.tab, minIndex, maxIndex);
    }

    @Override
    public void run() {
        try {
            this.ensureNoTimeout();
            if (max - min <= 1) {
               this.keepStatus = TrieStatus.OK;
            } else {
                int milieu = (max + min) / 2;
                TriMergeParallelCreationBuffersInutiles gauche = new TriMergeParallelCreationBuffersInutiles(this, min, milieu);
                // j'exécute cette première partie dans un nouveau thread
                this.declareNewThread();
                Thread bGauche = new Thread(gauche);
                bGauche.start();
                TriMergeParallelCreationBuffersInutiles droite = new TriMergeParallelCreationBuffersInutiles(this, milieu, max);
                // j'exécute cette seconde partie dans le Thread courant
                droite.run();
                bGauche.join();
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
