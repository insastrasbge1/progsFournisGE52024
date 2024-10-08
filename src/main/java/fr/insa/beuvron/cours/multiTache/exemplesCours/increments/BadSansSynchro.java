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
 * Un même objet Runnable est exécuté par plusieurs Thread. Les Threads
 * partagent donc les attributs de l'objet Si plusieurs Thread agissent en
 * lecture/ecriture sur la valeur, le résultat dépend de l'ordre d'exécution
 * controlée par l'ordonnanceur. Ce phénomène peut apparaitre même sur une
 * instruction aussi simple que ++ : L'incrémentation d'une variable v peut se
 * décomposer en . l(v) : lecture de la valeur de v dans le registre R . +1 :
 * ajout de 1 . e(v) écriture de la valeur du registre dans v
 *
 * si deux Thread executent cette séquence, le résultat dépend de l'ordre :
 * supposons une valeur initiale de 0
 *
 * <pre>
 * {@code
 *   Thread1      |      Thread2     |  v (valeur après instruction) |
 * -----------------------------------------
 *                |                  |  0  |
 * l(v)           |                  |  0  |
 * +1             |                  |  0  |
 * e(v)           |                  |  1  |
 *                | l(v)             |  1  |
 *                | +1               |  1  |
 *                | e(v)             |  2  |
 * ==> tout c'est bien passé
 *
 *   Thread1      |      Thread2     |  v (valeur après instruction) |
 * -----------------------------------------
 *                |                  |  0  |
 * l(v)           |                  |  0  |
 * +1             |                  |  0  |
 *                | l(v)             |  0  |
 * e(v)           |                  |  1  |
 *                | +1               |  1  |
 *                | e(v)             |  1  |
 * ==> résultat non attendu
 * }
 * </pre>
 *
 * @author francois
 */
public class BadSansSynchro implements Runnable {

    private long partage = 0;

    private final long nbrIter;

    /**
     *
     * @param nbrIter
     */
    public BadSansSynchro(long nbrIter) {
        this.nbrIter = nbrIter;
    }

    /** todoDoc. */
    @Override
    public void run() {
        for (long i = 0; i < this.nbrIter; i++) {
            this.partage++;
        }
    }

    /**
     *
     * @param nbrIter
     * @param nbrThread
     * @return
     */
    public static long exec(long nbrIter, int nbrThread) {
        BadSansSynchro partage = new BadSansSynchro(nbrIter);
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
                throw new Error("unexpected interrupt ",ex);
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
        System.out.println("diff res-expected : " + (res-(nbrIter * nbrThread)));
    }

}
