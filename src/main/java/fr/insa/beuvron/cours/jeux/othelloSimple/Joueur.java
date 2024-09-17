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
 * deux possibilités de joueurs : noir ou blanc. Comme pour {@link Case}, on
 * choisi de coder la couleur du joeur par un entier (même code que {@link Case}
 * sans la possibilité de VIDE)
 */
public class Joueur {

    /**
     * utiliser les constantes {@link Case#NOIR} et {@link Case#BLANC} comme
     * valeur de ce champ
     */
    public int couleur;
    /**
     * vrai si ce joueur est joué par l'ordinateur
     */
    public boolean ordinateur;

    /**
     * retourne systématiquement un nouveau joueur NOIR
     * 
     */
    public static Joueur noir() {
        Joueur res = new Joueur();
        res.couleur = Case.NOIR;
        return res;
    }

    /**
     * retourne systématiquement un nouveau joueur BLANC
     * 
     */
    public static Joueur blanc() {
        Joueur res = new Joueur();
        res.couleur = Case.BLANC;
        return res;
    }

    /**
     *
     * 
     */
    public Joueur adversaire() {
        Joueur res = new Joueur();
        if (this.couleur == Case.NOIR) {
            res.couleur = Case.BLANC;
        } else {
            res.couleur = Case.NOIR;
        }
        return res;
    }

    /**
     *
     * 
     */
    public boolean estNoir() {
        return this.couleur == Case.NOIR;
    }

    /**
     *
     * 
     */
    public String toString() {
        if (this.couleur == Case.NOIR) {
            return "[Joeur Noir]";
        } else {
            return "[Joeur Blanc]";
        }
    }
}
