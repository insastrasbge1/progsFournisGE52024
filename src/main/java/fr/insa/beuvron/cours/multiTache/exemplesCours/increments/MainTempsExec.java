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
 *
 * @author francois
 */
public class MainTempsExec {

    /** todoDoc. */
    public static void execEtCompteTemps() {
        long nbrIter = 10000000;
        int nbrThread = 10;
        long attendu = nbrIter*nbrThread;
        System.out.println("Comparaison des temps d'exécution");
        System.out.println("nombre de thread : " + nbrThread);
        System.out.println("nombre d'incrementations du compteur dans chaque thread : " + nbrIter);
        System.out.println("résultat attendu : " + attendu);
        int i = 0;
        long t0 = System.currentTimeMillis();
        long res = BadSansSynchro.exec(nbrIter, nbrThread);
        long t1 = System.currentTimeMillis();
        System.out.println("sans synchro : res = " + res + " (" + (res == attendu) + ")");
        System.out.println("temps d'exec (ms) : " + (t1-t0));
        res = BofSynchroMethode.exec(nbrIter, nbrThread);
        t0 = System.currentTimeMillis();
        System.out.println("synchro sur methode entiere : res = " + res + " (" + (res == attendu) + ")");
        System.out.println("temps d'exec (ms) : " + (t0-t1));
        res = OKSynchroChaqueIncrement.exec(nbrIter, nbrThread);
        t1 = System.currentTimeMillis();
        System.out.println("synchro sur chaque incrément : res = " + res + " (" + (res == attendu) + ")");
        System.out.println("temps d'exec (ms) : " + (t1-t0));
        res = BofSynchroMethode.exec(nbrIter, nbrThread);
        t0 = System.currentTimeMillis();
        System.out.println("utilisation AtomicLong : res = " + res + " (" + (res == attendu) + ")");
        System.out.println("temps d'exec (ms) : " + (t0-t1));
    }
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        execEtCompteTemps();
    }

}
