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
package fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.puissance4;

import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Situation;
import java.util.Arrays;

/**
 * Le damier de puissance4 avec un nombre variable de lignes,colonnes
 * (normalement 6,7) et de pions alignés pour gagner (normalement 4).
 * <p>
 * Damier représenté par une matrice d'entiers :
 * <ul>
 * <li> 1 : pion noir </li> <li> 0 : case vide </li> <li> -1 : pion blanc </lI>
 * </ul>
 * </p>
 * <p>
 * Ce programme est "traduit" d'une version en langage C. D'où certaines
 * constructions valides, mais un peu bizarre en Java
 * </p>
 *
 * @author francois
 */
public class SituationPuissance4 implements Situation{

    public static final int DEFAUT_NBRLIGNES = 6;
    public static final int DEFAUT_NBRCOLS = 7;
    public static final int DEFAUT_NBR_A_ALIGNER = 4;

    private static final int VIDE = 0;
    private static final int NOIR = 1;
    private static final int BLANC = -1;

//    /**
//     * représentation d'une direction sur le damier. très simple, ne respecte
//     * pas l'encapsulation (attributs publics)
//     */
//    private static class Dir {
//
//        public int dlig;
//        public int dcol;
//
//        public Dir(int dlig, int dcol) {
//            this.dlig = dlig;
//            this.dcol = dcol;
//        }
//    }
//
//    private static final List<Dir> ALL_DIRS = List.of(
//            new Dir(-1, -1),
//            new Dir(-1, 0),
//            new Dir(-1, 1),
//            new Dir(0, -1),
//            new Dir(0, 1),
//            new Dir(1, -1),
//            new Dir(1, 0),
//            new Dir(1, 1));
//
//    /**
//     * représentation d'une position sur le damier. très simple, ne respecte pas
//     * l'encapsulation (attributs publics)
//     */
//    private static class Pos {
//
//        public int lig;
//        public int col;
//
//        public Pos(int lig, int col) {
//            this.lig = lig;
//            this.col = col;
//        }
//
//        public Pos bouge(Dir d) {
//            return new Pos(this.lig + d.dlig,this.col + d.dcol);
//        }
//    }
//
    private int nbrLig;
    private int nbrCol;
    private int nbrAlig;

    private int[][] damier;

    /**
     * cuisine interne : pour des raisons d'efficacité, on garde constament le
     * plus grand alignement actuel pour chacun des joueurs.
     * <p>
     * mis à jour lorsque l'on joue effectivement sur le damier
     * </p>
     */
    private int[] maxAlign;
    
    /** modifié à chaque coup joué */
    private int nbrCoupJoues;

    /**
     * privé car uniquement utilisé pour faire une copie
     */
    private SituationPuissance4(int nbrAlig, int[][] damier, int[] maxAlign,int nbrCoupJoues) {
        this.nbrLig = damier.length;
        this.nbrCol = damier[0].length;
        this.nbrAlig = nbrAlig;
        this.damier = damier;
        this.maxAlign = maxAlign;
        this.nbrCoupJoues = nbrCoupJoues;
    }

    public SituationPuissance4(int nbrLig, int nbrCol, int nbrAlig) {
        this.nbrLig = nbrLig;
        this.nbrCol = nbrCol;
        this.nbrAlig = nbrAlig;
        this.damier = new int[nbrLig][nbrCol];
        // inutile en Java (qui initialise à 0) mais indispensable en C
        // qui n'initialise pas
        for (int lig = 0; lig < this.nbrLig; lig++) {
            for (int col = 0; col < this.nbrCol; col++) {
                this.damier[lig][col] = VIDE;
            }
        }
        this.maxAlign = new int[2];
        this.nbrCoupJoues = 0;
    }

    public SituationPuissance4() {
        this(DEFAUT_NBRLIGNES, DEFAUT_NBRCOLS, DEFAUT_NBR_A_ALIGNER);
    }

    public boolean dansDamier(int lig, int col) {
        return lig >= 0 && col >= 0 && lig < this.getNbrLig() && col < this.getNbrCol();
    }

    public int val(int lig, int col) {
        return this.damier[lig][col];
    }

    public void setVal(int lig, int col, int val) {
        this.damier[lig][col] = val;
    }

//    public int val(Pos p) {
//        return this.val(p.lig, p.col);
//    }
//
//    public void setVal(Pos p, int val) {
//        this.setVal(p.lig, p.col, val);
//    }
    public String affCase(int valCase) {
        if (valCase == VIDE) {
            return ".";
        } else if (valCase == NOIR) {
            return "X";
        } else if (valCase == BLANC) {
            return "O";
        } else {
            throw new Error("valeur case invalide : " + valCase);
        }
    }

    /**
     * Une représentation très simple et moche du damier
     */
    @Override
    public String toString() {
        String res = "";
        for (int lig = 0; lig < this.nbrLig; lig++) {
            for (int col = 0; col < this.nbrCol; col++) {
                res = res + this.affCase(this.val(lig, col));
            }
            res = res + "\n";
        }
        return res;
    }

    public SituationPuissance4 copie() {
        // je n'ai pas trouvé de fonction prédéfinie permettant de copier
        // une matrice d'entier en java ; Incroyable !!
        // un "deepCopy" serait le bienvenu !!
        // bon, bin je le fais "à la main"
        int[][] dnew = new int[this.damier.length][];
        for (int i = 0; i < this.damier.length; i++) {
            dnew[i] = Arrays.copyOf(this.damier[i], this.damier[i].length);
        }
        int[] nAlign = Arrays.copyOf(maxAlign, maxAlign.length);
        return new SituationPuissance4(this.nbrAlig, dnew, nAlign,this.nbrCoupJoues);
    }

    public boolean colJouable(int col) {
        return this.val(0, col) == VIDE;
    }

    /**
     * compte le nombre de pion d'une certaine couleur dans la direction dir à
     * partir d'une position
     */
    public int nbrAlignDir(int lig, int col, int dlig, int dcol, int couleur) {
        int res = 0;
        lig = lig + dlig;
        col = col + dcol;
        while (this.dansDamier(lig, col) && this.val(lig, col) == couleur) {
            res++;
            lig = lig + dlig;
            col = col + dcol;
        }
        return res;
    }

    /**
     * compte le nombre de pion alignés de la couleur dans la direction
     * dlig,dcol.
     *
     * @param lig
     * @param col
     * @param dlig
     * @param dcol
     * @param couleur
     * @return
     */
    public int nbrAlignLigne(int lig, int col, int dlig, int dcol, int couleur) {
        if (this.val(lig, col) != couleur) {
            return 0;
        } else {
            return this.nbrAlignDir(lig, col, dlig, dcol, couleur) + this.nbrAlignDir(lig, col, -dlig, -dcol, couleur) + 1;
        }
    }

    /**
     * nombre de pions de la couleur alignés autour de la position lig,col.
     * <p>
     * attention : si la case lig,col ne contient pas un pion de la couleur, la
     * réponse est zéro. Cette méthode devra normalement être appelée après que
     * le damier ait été modifié pour tenir compte que le joueur de la couleur a
     * joué en lig,col
     * </p>
     *
     * @param lig
     * @param col
     * @param couleur
     * @return
     */
    public int nbrAlignMax(int lig, int col, int couleur) {
        int res = 0;
        for (int dlig = -1; dlig < 2; dlig++) {
            for (int dcol = -1; dcol < 2; dcol++) {
                if (dlig != 0 || dcol != 0) {
                    int cur = this.nbrAlignLigne(lig, col, dlig, dcol, couleur);
                    if (cur > res) {
                        res = cur;
                    }
                }
            }
        }
        return res;
    }

    private int convertionCouleurNumJoueur(int couleur) {
        if (couleur == 1) {
            return 0;
        } else {
            return 1;
        }
    }

    private void updateMaxAlign(int lig, int col, int couleur) {
        int nouvelAlign = this.nbrAlignMax(lig, col, couleur);
        int numJoueur = this.convertionCouleurNumJoueur(couleur);
        int ancien = this.maxAlign[numJoueur];
        if (nouvelAlign > ancien) {
            this.maxAlign[numJoueur] = nouvelAlign;
        }
    }

    /**
     * Modifie le damier si le joueur couleur joue en col
     *
     * @param col
     * @param couleur
     * @return la ligne ou le nouveau pion "attéri"
     */
    public int joue(int col, int couleur) {
        int lig = this.nbrLig - 1;
        while (lig > 0 && this.val(lig, col) != VIDE) {
            lig = lig - 1;
        }
        if (lig < 0) {
            throw new Error("joue sur colonne pleine : col = " + col);
        }
        this.setVal(lig, col, couleur);
        this.updateMaxAlign(lig, col, couleur);
        this.nbrCoupJoues ++;
        return lig;
    }
    
    public boolean fini() {
        return this.nbrCoupJoues == this.nbrLig*this.nbrCol;
    }
    
    public boolean noirGagne() {
        return this.maxAlign[0] >= this.nbrAlig;
    }

    public boolean blancGagne() {
        return this.maxAlign[1] >= this.nbrAlig;
    }

    /**
     * @return the nbrLig
     */
    public int getNbrLig() {
        return nbrLig;
    }

    /**
     * @return the nbrCol
     */
    public int getNbrCol() {
        return nbrCol;
    }

    /**
     * @return the nbrAlig
     */
    public int getNbrAlig() {
        return nbrAlig;
    }

    /**
     * On peut vouloir tout le damier (par exemple pour entrainer un Oracle).
     * <p>
     * Attention : ne pas modifier, sinon état partie instable
     * </p>
     *
     * @return the damier
     */
    public int[][] getDamier() {
        return damier;
    }

}
