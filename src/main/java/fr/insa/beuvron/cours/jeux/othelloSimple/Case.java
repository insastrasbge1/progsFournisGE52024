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

/**
 * Title: micro othello Description: Création d'un petit programme d'othello. Ce
 * programme respecte les règles de l'othello, mais joue la première case
 * jouable, sans aucune stratégie Copyright: Copyright (c) 2001 Company: INSA
 * Strasbourg
 *
 * @author F. de Beuvron
 * @version 1.0
 *
 * Cette classe représente le contenu d'une case. Pour pousser jusqu'au bout la
 * philosophie objet, on aurait sans doute pu créer une classe abstraite Case,
 * et trois sous-classes pour représenter les cases vides, noires, ou blanches.
 *
 * On se contente ici de représenter les trois alternatives par un entier : 
 * <pre> {@code 
 * 0 <--> case vide
 * 1 <--> case noire
 * 2 <--> case blanche
 * } </pre>
 */
public class Case {

    /** todoDoc. */
    public static final int VIDE = 0;

    /** todoDoc. */
    public static final int NOIR = 1;

    /** todoDoc. */
    public static final int BLANC = 2;

    /** todoDoc. */
    public int contenu;

    /**
     *
     * 
     */
    public boolean estVide() {
        return this.contenu == Case.VIDE;
    }

    /**
     *
     * 
     */
    public boolean estNoir() {
        return this.contenu == Case.NOIR;
    }

    /**
     *
     * 
     */
    public boolean estBlanc() {
        return this.contenu == Case.BLANC;
    }
    
    public Case copie() {
        Case res = new Case();
        res.contenu = this.contenu;
        return res;
    }

    /**
     *
     * 
     */
    public String toString() {
        if (this.contenu == Case.VIDE) {
            return ".";
        } else if (this.contenu == Case.NOIR) {
            return "X";
        } else {
            return "O";
        }
    }
}
