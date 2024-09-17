/*
 Copyright 2000-2011 Francois de Bertrand de Beuvron

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
package fr.insa.beuvron.cours.jeux.othelloSimple;

import fr.insa.beuvron.utils.Console;

/**
 * Title: micro othello Description: Création d'un petit programme d'othello. Ce
 * programme respecte les règles de l'othello, mais joue la première case
 * jouable, sans aucune stratégie Copyright: Copyright (c) 2001 Company: INSA
 * Strasbourg
 *
 * @author F. de Beuvron
 * @version 1.0
 *
 * représentation d'une position sur un {@link Damier}
 */
public class Position {

    /** todoDoc. */
    public int ligne;

    /** todoDoc. */
    public int col;

    /**
     *
     * 
     */
    public String toString() {
        return "[Pos " + this.ligne + "," + this.col + "]";
    }

    /**
     *
     * 
     */
    public static Position entree() {
        char clig = 'A';
        char ccol = '0';
        boolean ok;
        do {
            ok = false;
            String rep = Console.entreeString("entrez une position (exemple B3 pour 2ième ligne, 3ième colonne) : ");
            rep = rep.trim().toUpperCase();
            if (rep.length() == 2) {
                clig = rep.charAt(0);
                if (clig >= 'A' && clig <= 'H') {
                    ccol = rep.charAt(1);
                    if (ccol >= '1' && ccol <= '8') {
                        ok = true;
                    }
                }
            }
            if (!ok) {
                Console.println("Position non reconnue");
            }
        } while (!ok);
        Position res = new Position();
        res.ligne = ((int) clig) - ((int) 'A');
        res.col = ((int) ccol) - ((int) '1');
        return res;
    }

    /**
     * une Position est valide si elle correspond effectivement à une case du
     * damier d'othello
     * 
     */
    public boolean estValide() {
        return this.ligne >= 0 && this.ligne < 8 && this.col >= 0 && this.col < 8;
    }

    /**
     *
     * 
     * 
     */
    public Position bouge(Direction d) {
        Position res = new Position();
        res.ligne = this.ligne + d.dligne;
        res.col = this.col + d.dcol;
        return res;
    }
}
