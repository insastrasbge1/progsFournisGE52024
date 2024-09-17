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
import java.io.IOException;
import java.util.Arrays;

/**
 * Title: micro othello Description: Création d'un petit programme d'othello. Ce
 * programme respecte les règles de l'othello, mais joue la première case
 * jouable, sans aucune stratégie
 *
 * @author F. de Beuvron
 * @version 1.0
 *
 */
public class Damier {

    /**
     * todoDoc.
     */
    private Case[][] cases;

    /**
     * crée un damier avec la position initiale.
     */
    public Damier() {
        this.cases = new Case[8][8];
        this.init();
    }
    
    /**
     * utilisé en interne pour la copie.
     * @param cases 
     */
    private Damier(Case[][] cases) {
        this.cases = cases;
    }

    /**
     * initialise le damier contenant la position initiale d'une partie
     */
    public void init() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.cases[i][j] = new Case();
                this.cases[i][j].contenu = Case.VIDE;
            }
        }
        this.cases[3][3].contenu = Case.BLANC;
        this.cases[3][4].contenu = Case.NOIR;
        this.cases[4][3].contenu = Case.NOIR;
        this.cases[4][4].contenu = Case.BLANC;
    }

    public Damier copie() {
        Case[][] dnew = new Case[this.cases.length][this.cases[0].length];
        for (int lig = 0; lig < this.cases.length; lig++) {
            for (int col = 0; col < this.cases[0].length; col++) {
                dnew[lig][col] = this.cases[lig][col].copie();
            }
        }
        Damier res = new Damier();
        res.cases = dnew;
        return res;
    }
    
    public Case getVal(int lig,int col) {
        return this.cases[lig][col];
    }

    /**
     *
     *
     */
    public String toString() {
        String res = " 12345678\n";
        for (int i = 0; i < 8; i++) {
            res = res + ((char) (((int) 'A') + i));
            for (int j = 0; j < 8; j++) {
                res = res + this.cases[i][j].toString();
            }
            res = res + "\n";
        }
        return res;
    }

    /**
     * todoDoc.
     */
    public static void test1() {
        Console.println(new Damier().toString());
    }

    /**
     * teste si une case est vide
     *
     *
     */
    public boolean estVide(Position p) {
        return this.cases[p.ligne][p.col].contenu == Case.VIDE;
    }

    /**
     * place un pion du joueur sur le damier
     *
     *
     */
    public void posePion(Position p, Joueur j) {
        this.cases[p.ligne][p.col].contenu = j.couleur;
    }

    /**
     * retourne un pion sur le damier
     *
     */
    public void retournePion(Position p) {
        if (this.cases[p.ligne][p.col].contenu == Case.NOIR) {
            this.cases[p.ligne][p.col].contenu = Case.BLANC;
        } else if (this.cases[p.ligne][p.col].contenu == Case.BLANC) {
            this.cases[p.ligne][p.col].contenu = Case.NOIR;
        } else {
            throw new Error("erreur interne : tentative de retourner un pion sur une case vide");
        }
    }

    /**
     * teste si une case contient un pion à la couleur du joueur
     *
     *
     *
     */
    public boolean estAuJoueur(Position p, Joueur j) {
        return this.cases[p.ligne][p.col].contenu == j.couleur;
    }

    /**
     * compte le nombre de pion d'un joueur
     *
     *
     */
    public int comptePions(Joueur j) {
        int res = 0;
        Position p = new Position();
        for (p.ligne = 0; p.ligne < 8; p.ligne++) {
            for (p.col = 0; p.col < 8; p.col++) {
                if (this.estAuJoueur(p, j)) {
                    res++;
                }
            }
        }
        return res;
    }

    /**
     * teste si un joueur retourne au moins un pion adverse dans une direction
     * donnée en jouant à une position donnée
     *
     *
     *
     *
     */
    public boolean jouableDirection(Position p, Direction d, Joueur j) {
        boolean res;
        Position posActuelle = p.bouge(d);
        int nbrAdverses = 0;
        while (posActuelle.estValide() && this.estAuJoueur(posActuelle, j.adversaire())) {
            posActuelle = posActuelle.bouge(d);
            nbrAdverses = nbrAdverses + 1;
        }
        if (nbrAdverses > 0 && posActuelle.estValide() && this.estAuJoueur(posActuelle, j)) {
            res = true;
        } else {
            res = false;
        }
        return res;
    }

    /**
     * teste si une position est jouable pour un joueur
     *
     *
     *
     */
    public boolean jouable(Position p, Joueur j) {
        boolean res = false;
        Direction cur = new Direction();
        if (this.estVide(p)) {
            cur.dligne = -1;
            while (cur.dligne <= 1 && !res) {
                cur.dcol = -1;
                while (cur.dcol <= 1 && !res) {
                    if (cur.estValide()) {
                        res = this.jouableDirection(p, cur, j);
                    }
                    cur.dcol = cur.dcol + 1;
                }
                cur.dligne = cur.dligne + 1;
            }
        }
        return res;
    }

    /**
     * modifie le damier suivant le coup de l'un des joueur
     *
     *
     */
    public void effectueCoup(Position p, Joueur j) {
        Direction d = new Direction();
        this.posePion(p, j);
        for (d.dligne = -1; d.dligne <= 1; d.dligne++) {
            for (d.dcol = -1; d.dcol <= 1; d.dcol++) {
                if (d.estValide() && this.jouableDirection(p, d, j)) {
                    Position posActuelle = p.bouge(d);
                    while (posActuelle.estValide() && this.estAuJoueur(posActuelle, j.adversaire())) {
                        this.retournePion(posActuelle);
                        posActuelle = posActuelle.bouge(d);
                    }
                }
            }
        }
    }

    /**
     * dans cette version, l'ordinateur joue la première case jouable en partant
     * du coin haut gauche, et en testant toutes les colonnes puis toutes les
     * lignes si aucune case n'est jouable, ordinateurJoue renvoie la Position
     * invalide (-1,-1)
     *
     *
     */
    public Position ordinateurJoue(Joueur j) {
        boolean caseTrouvee = false;
        Position res = new Position();
        res.ligne = 0;
        while (!caseTrouvee && res.ligne < 8) {
            res.col = 0;
            while (!caseTrouvee && res.col < 8) {
                if (this.jouable(res, j)) {
                    caseTrouvee = true;
                } else {
                    res.col = res.col + 1;
                }
            }
            if (!caseTrouvee) {
                res.ligne = res.ligne + 1;
            }
        }
        if (!caseTrouvee) {
            res.ligne = -1;
            res.col = -1;
        }
        return res;
    }

    /**
     * teste s'il existe au moins une case jouable pour le joueur
     *
     *
     */
    public boolean auMoinsUneCaseJouable(Joueur j) {
        boolean caseTrouvee = false;
        Position p = new Position();
        p.ligne = 0;
        while (!caseTrouvee && p.ligne < 8) {
            p.col = 0;
            while (!caseTrouvee && p.col < 8) {
                if (this.jouable(p, j)) {
                    caseTrouvee = true;
                } else {
                    p.col++;
                }
            }
            p.ligne++;
        }
        return caseTrouvee;
    }

    /**
     * demande la case jouée par le joueur, vérifie qu'elle est valide et la
     * retourne retourne la position invalide (-1,-1) si le joueur doit passer
     *
     *
     */
    public Position humainJoue(Joueur j) {
        Position res = null;
        if (!this.auMoinsUneCaseJouable(j)) {
            Console.println("vous êtes obligés de passer");
            res = new Position();
            res.ligne = -1;
            res.col = -1;
        } else {
            boolean ok = false;
            while (!ok) {
                Console.println(j + " : où jouez-vous ?");
                res = Position.entree();
                ok = this.jouable(res, j);
                if (!ok) {
                    Console.println("coup non jouable");
                }
            }
        }
        return res;
    }

    /**
     * fait jouer un des joueurs, et modifie le damier renvoir false si le
     * joueur a du passer
     *
     *
     */
    public boolean joueUnCoup(Joueur j) {
        boolean res;
        Position p;
        if (j.ordinateur) {
            p = this.ordinateurJoue(j);
        } else {
            p = this.humainJoue(j);
        }
        if (p.estValide()) {
            this.effectueCoup(p, j);
            Console.println(j + " joue en " + p);
            res = true;
        } else {
            Console.println(j + " passe");
            res = false;
        }
        return res;
    }

    /**
     * todoDoc.
     */
    public static void jouePartie() {
        Damier da = new Damier();
        int nbrCoup = 0;
        Joueur jBlanc = Joueur.blanc();
        char rep;
        do {
            rep = Console.entreeChar("l'ordinateur joue les blancs (o/n) ?");
            rep = Character.toUpperCase(rep);
        } while (rep != 'O' && rep != 'N');
        jBlanc.ordinateur = (rep == 'O');
        Joueur jNoir = Joueur.noir();
        do {
            rep = Console.entreeChar("l'ordinateur joue les noirs (o/n) ?");
            rep = Character.toUpperCase(rep);
        } while (rep != 'O' && rep != 'N');
        jNoir.ordinateur = (rep == 'O');
        boolean j1PeutJouer = true;
        boolean j2PeutJouer = true;
        Joueur jCourant = jNoir;
        while ((j1PeutJouer || j2PeutJouer) && nbrCoup < 60) {
            Console.println(da.toString());
            j1PeutJouer = j2PeutJouer;
            j2PeutJouer = da.joueUnCoup(jCourant);
            if (j2PeutJouer) {
                nbrCoup++;
            }
            if (jCourant == jNoir) {
                jCourant = jBlanc;
            } else {
                jCourant = jNoir;
            }
        }
        Console.println(da.toString());
        Console.println("score final : \n"
                + "joueur Blanc : " + da.comptePions(jBlanc) + "\n"
                + "joueur Noir : " + da.comptePions(jNoir) + "\n");
    }

    /**
     *
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        jouePartie();
    }
}
