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
package fr.insa.beuvron.cours.multiTache.exemplesCours.increments;

/**
 * En synchronisant chaque incrément. C'est correct, mais très lent !
 *
 * @author francois
 */
public class OKSynchroChaqueIncrement implements Runnable {

    private long partage = 0;

    private final long nbrIter;

    /**
     *
     * @param nbrIter
     */
    public OKSynchroChaqueIncrement(long nbrIter) {
        this.nbrIter = nbrIter;
    }

    /** todoDoc. */
    @Override
    public void run() {
        for (long i = 0; i < this.nbrIter; i++) {
            synchronized (this) {
                // deux thread travaillant sur la meme instance
                // de AccesVariablePartagee (this)
                // ne peuvent pas entrer ensemble dans ce code
                this.partage++;
            }
        }
    }

    /**
     *
     * @param nbrIter
     * @param nbrThread
     * @return
     */
    public static long exec(long nbrIter, int nbrThread) {
        OKSynchroChaqueIncrement partage = new OKSynchroChaqueIncrement(nbrIter);
        Thread[] trs = new Thread[nbrThread];
        for (int i = 0; i < trs.length; i++) {
            trs[i] = new Thread(partage, "toto" + i);
        }
        for (int i = 0; i < trs.length; i++) {
            trs[i].start();
        }
        for (int i = 0; i < trs.length; i++) {
            try {
                trs[i].join();
            } catch (InterruptedException ex) {
                throw new Error("unexpected interrupt ", ex);
            }
        }
        return partage.partage;

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        long nbrIter = 10000000;
        int nbrThread = 10;
        long res = exec(nbrIter, nbrThread);
        System.out.println("res : " + res);
        System.out.println("diff res-expected : " + (res - (nbrIter * nbrThread)));
    }

}
