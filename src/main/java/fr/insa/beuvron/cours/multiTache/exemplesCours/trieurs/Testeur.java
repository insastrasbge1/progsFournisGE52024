/*
Copyright 2000- Francois de Bertrand de Beuvron

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

import fr.insa.beuvron.cours.multiTache.utils.Debug;
import fr.insa.beuvron.utils.exceptions.ExceptionsUtils;
import fr.insa.beuvron.utils.matrice.MatriceToText;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author francois
 */
public class Testeur {

    public static int[] tabAlea(int size, int borneMax) {
        int[] res = new int[size];
        for (int i = 0; i < res.length; i++) {
            res[i] = (int) (Math.random() * borneMax);
        }
        return res;
    }

    public static boolean verifieTrie(int[] tab) {
        boolean res = true;
        int i = 0;
        while (res && i < tab.length - 1) {
            res = tab[i] <= tab[i + 1];
            i++;
        }
        return res;
    }

    public static FinalResult test(Class<? extends Trieur> trieurType, int size, int borneMax, long maxTime, int maxThreads) {
        int[] tab = tabAlea(size, borneMax);
        return test(trieurType, tab, maxTime, maxThreads);
    }

    public static FinalResult test(Class<? extends Trieur> trieurType, int[] tab, long maxTime, int maxThreads) {
        long tempsDebut = System.currentTimeMillis();
        // le Thread courant doit compter pour un thread utilisé
        // donc il en reste maxThreads-1 disponibles
        AtomicInteger threads = new AtomicInteger(maxThreads - 1);
        try {
            Debug.trace("début de trie : "
                    + trieurType.getSimpleName()
                    + " : size = " + tab.length
            );
            Constructor<? extends Trieur> co = trieurType.getConstructor(long.class, AtomicInteger.class, int[].class, int.class, int.class);
            tempsDebut = System.currentTimeMillis();
            long tempsFin = tempsDebut + maxTime;
            Trieur trieur = co.newInstance(tempsFin, threads, tab, 0, tab.length);
            TrieStatus trieRes = trieur.effectueTrie();
            long elapsedTime = System.currentTimeMillis() - tempsDebut;
            int usedThreads = maxThreads - threads.get();
            FinalResult res;
            if (trieRes == TrieStatus.TIMEOUT) {
                res = new FinalResult(FinalStatus.TIME_OUT, tab.length, elapsedTime, usedThreads, trieurType);
            } else if (trieRes == TrieStatus.TOO_MANY_THREADS) {
                res = new FinalResult(FinalStatus.THREADS_OUT, tab.length, elapsedTime, usedThreads, trieurType);
            } else if (trieRes == TrieStatus.INTERNAL_ERROR) {
                res = new FinalResult(FinalStatus.INTERNAL_ERROR, tab.length, elapsedTime, usedThreads, trieurType);
            } else if (verifieTrie(tab)) {
                res = new FinalResult(FinalStatus.OK, tab.length, elapsedTime, usedThreads, trieurType);
            } else {
                res = new FinalResult(FinalStatus.SORT_ERROR, tab.length, elapsedTime, usedThreads, trieurType);
            }
            Debug.trace("fin trie : " + trieRes);
            return res;
        } catch (Exception ex) {
            Debug.erreur(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa.beuvron", 5));
            long elapsedTime = System.currentTimeMillis() - tempsDebut;
            int usedThreads = maxThreads - threads.get();
            return new FinalResult(FinalStatus.INTERNAL_ERROR, tab.length, elapsedTime, usedThreads, trieurType);
        }
    }

    public static List<List<FinalResult>> compare(List<Class<? extends Trieur>> trieurs,
            int sizeMin, double multiplieurTaille, int nbrPas, int borneMax,
            long maxTime, int maxThreads) {
        List<List<FinalResult>> res = new ArrayList<>(nbrPas + 1);
        Debug.info("Test des trieurs : \n"
                + "  taille initiale : " + sizeMin + "\n"
                + "  taille multipliée par : " + multiplieurTaille + " à chaque pas\n"
                + "  nombre de pas : " + nbrPas + "\n"
                + "  taille max des elements : 0 <= e < " + borneMax + "\n"
                + "  temps maximal d'exécution : " + FinalResult.timeToString(maxTime) + "\n"
                + "  nombre maximal de threads : " + maxThreads + "\n"
                + "  nombre de processeurs : " + Runtime.getRuntime().availableProcessors()
        );
        double curMult = 1;
        for (int i = 0; i < nbrPas; i++) {
            int size = (int) (sizeMin * curMult);
            Debug.info("------- taille : " + size);
            List<FinalResult> curLine = new ArrayList<>(trieurs.size() + 1);
            res.add(curLine);
            int[] leTab = tabAlea(size, borneMax);
            for (var trieurCL : trieurs) {
                Debug.info("-- trieur : " + trieurCL.getSimpleName());
                int[] copie = Arrays.copyOf(leTab, leTab.length);
                FinalResult resTrie = test(trieurCL, copie, maxTime, maxThreads);
                curLine.add(resTrie);
            }
            curMult = curMult * multiplieurTaille;
        }
        return res;
    }

    private static String finalResultToString(FinalResult fr, boolean logTemps) {
        return fr.format(logTemps);
    }

    private static String formatResults(List<List<FinalResult>> res,
            boolean logTaille, boolean logTemps,
            boolean latex) {
        var asStrings = new ArrayList<>(res.stream().map((line) -> {
            List<String> ols = new ArrayList<>(line.stream().map(fr -> finalResultToString(fr, logTemps)).toList());
            if (!line.isEmpty()) {
                int taille = line.get(0).getTaille();
                String stime;
                if (logTaille) {
                    stime = String.format(Locale.US, "%7.4g", Math.log10(taille));
                } else {
                    stime = "" + taille;
                }
                ols.addFirst(stime);
            }
            return ols;
        }).toList());
        if (!res.isEmpty()) {
            List<String> entetes = new ArrayList<>();
            if (logTaille) {
                entetes.add("log(taille)");
            } else {
                entetes.add("taille");
            }
            for (var oneRes : res.get(0)) {
                entetes.add(oneRes.getTrieur().getSimpleName());
            }
            asStrings.addFirst(entetes);
        }
        if (latex) {
            return MatriceToText.formatMatLatex(asStrings, true);

        } else {
            return MatriceToText.formatMat(asStrings, true);
        }

    }

    private static String finalResultForTikz(FinalResult fr, boolean logTaille, boolean logTemps) {
        return fr.formatForTikz(logTaille, logTemps);
    }

    private static String formatTimesForTikz(List<List<FinalResult>> res,
            boolean logTaille, boolean logTemps) {
        StringBuilder ress = new StringBuilder();
        if (!res.isEmpty()) {
            for (int col = 0; col < res.get(0).size(); col++) {
                ress.append("\\addplot coordinates {");
                for (int lig = 0; lig < res.size(); lig++) {
                    ress.append(finalResultForTikz(res.get(lig).get(col), logTaille, logTemps));
                }
                ress.append("};\n");
            }
        }
        return ress.toString();
    }

    public static List<List<FinalResult>> compareTout(int sizeMin, double multiplieurTaille, int nbrPas, int borneMax,
            long maxTime, int maxThreads) {
        List<Class<? extends Trieur>> trieurs = List.of(
                TriBulleSequentiel.class,
                TriMergeSequentiel.class,
                TriMergeParallel.class,
                TriMergeSemiParallel.class
        //                ,
        //                TriMergeSequentielCreationBuffersInutiles.class,
        //                TriMergeParallelCreationBuffersInutiles.class,
        //                TriMergeSemiParallelCreationBuffersInutiles.class
        );
        return compare(trieurs, sizeMin, multiplieurTaille, nbrPas, borneMax, maxTime, maxThreads);
    }

    public static void main(String[] args) {
        Debug.setNiveauMax(Debug.Niveau.INFO);
        List<List<FinalResult>> res = compareTout(1000, Math.pow(10, 1.0 / 2),13, 100000000, 30000, 20000);
        System.out.println(formatResults(res, false, false,false));
        System.out.println("");
        System.out.println("----------- Pour Latex -------------------");
        System.out.println(formatResults(res, false, false,true));
        System.out.println("");
        System.out.println("----------- Pour TikZ -------------------");
        System.out.println(formatTimesForTikz(res, true, true));
    }

}
