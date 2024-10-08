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

import java.time.Duration;
import java.util.Locale;

/**
 *
 * @author francois
 */
public class FinalResult {

    private FinalStatus status;
    private int taille;
    private long elapsedTime;
    private int totalNumberOfThreadUsed;
    private Class<? extends Trieur> trieur;

    public FinalResult(FinalStatus status, int taille,
            long elapsedTime, int totalNumberOfThreadUsed,
            Class<? extends Trieur> trieur) {
        this.status = status;
        this.taille = taille;
        this.elapsedTime = elapsedTime;
        this.totalNumberOfThreadUsed = totalNumberOfThreadUsed;
        this.trieur = trieur;
    }

    public FinalStatus getStatus() {
        return status;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public static String timeToString(long duree) {
        Duration d = Duration.ofMillis(duree);
        return d.toString().substring(2);
    }

    @Override
    public String toString() {
        return "FinalResult{" + "status=" + status + ", elapsedTime=" + timeToString(elapsedTime) + '}';
    }

    public String format(boolean logTemps) {
        if (this.status != FinalStatus.OK) {
            return this.status.toString();
        } else {
            String temps;
            if (logTemps) {
                temps = String.format(Locale.US,"%7.4g Log(ms)", Math.log10(1 + this.elapsedTime));
            } else {
                temps = timeToString(this.elapsedTime);
            }
            return temps + " ; " + this.totalNumberOfThreadUsed + "T";
        }
    }

    public String formatForTikz(boolean logTaille,boolean logTemps) {
        if (this.status != FinalStatus.OK) {
            return "";
        } else {
            String staille;
            if (logTaille) {
                staille = String.format(Locale.US,"%7.4g", Math.log10(this.getTaille()));
            } else {
                staille = "" + this.getTaille();
            }
            String temps;
            if (logTemps) {
                temps = String.format(Locale.US,"%7.4g", Math.log10(1 + this.elapsedTime));
            } else {
                temps = "" + this.elapsedTime;
            }
            return "(" + staille + "," + temps + ")";
        }
    }

    /**
     * @return the totalNumberOfThreadUsed
     */
    public int getTotalNumberOfThreadUsed() {
        return totalNumberOfThreadUsed;
    }

    /**
     * @return the taille
     */
    public int getTaille() {
        return taille;
    }

    /**
     * @return the trieur
     */
    public Class<? extends Trieur> getTrieur() {
        return trieur;
    }

}
