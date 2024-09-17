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
package fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.othello;

import fr.insa.beuvron.cours.jeux.othelloSimple.Damier;
import fr.insa.beuvron.cours.jeux.othelloSimple.Position;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Jeu;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Joueur;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.StatutSituation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author francois
 */
public class JeuOthello implements Jeu<SituationOthello, CoupOthello> {

    @Override
    public SituationOthello situationInitiale() {
        Damier res = new Damier();
        return new SituationOthello(res);
    }

    /**
     * la classe OthelloSimple n'a ni constructeur, ni encapsulation : ses
     * attributs sont public ce qui n'est pas normal ; une petite méthode
     * "proxy" qui sert de pseudo-constructeur.
     *
     * @param lig
     * @param col
     * @return
     */
    private static Position creePos(int lig, int col) {
        Position res = new Position();
        res.ligne = lig;
        res.col = col;
        return res;
    }

    /**
     * La représentation d'un joueur n'est pas la même dans le jeux original
     * et dans l'api.
     * <p> une petite conversion s'impose</p>
     * <p> notez ici l'utilisation du nom complet de la classe : je ne peux
     * pas faire simplement un import puisque j'utilise deux classe qui 
     * ont exactement le même nom.
     * </p>
     * @param j un Joueur au sens de l'api apprentissage
     * @return un Joueur au sens du jeu original d'othello
     */
    private fr.insa.beuvron.cours.jeux.othelloSimple.Joueur convNewJoueurOldJoueur(Joueur j) {
        if (j == Joueur.J1) {
            return fr.insa.beuvron.cours.jeux.othelloSimple.Joueur.noir();
        } else {
            return fr.insa.beuvron.cours.jeux.othelloSimple.Joueur.blanc();
        }

    }
    
    /**
     * La représentation d'un joueur n'est pas la même dans le jeux original
     * et dans l'api.
     * <p> une petite conversion s'impose</p>
     * <p> notez ici l'utilisation du nom complet de la classe : je ne peux
     * pas faire simplement un import puisque j'utilise deux classe qui 
     * ont exactement le même nom.
     * </p>
     * @param oldJ un Joueur au sens du jeu original d'othello
     * @return un Joueur au sens de l'api apprentissage 
     */
    private Joueur convOldJoueurNewJoueur(fr.insa.beuvron.cours.jeux.othelloSimple.Joueur oldJ) {
        if (oldJ.estNoir()) {
            return Joueur.J1;
        } else {
            return Joueur.J2;
        }
    }

    /**
     * Test toutes les cases pour déterminier les coups jouables.
     * <p> le jeu original permet de tester si un coup est jouable ou non, mais
     * ne permet pas de renvoyer la liste de tous les coups jouables.
     * </p>
     * @param s
     * @param j
     * @return 
     */
    @Override
    public List<CoupOthello> coupsJouables(SituationOthello s, Joueur j) {
        List<CoupOthello> res = new ArrayList<>();
        fr.insa.beuvron.cours.jeux.othelloSimple.Joueur jConv
                = convNewJoueurOldJoueur(j);
        for (int lig = 0; lig < 8; lig++) {
            for (int col = 0; col < 8; col++) {
                if (s.getDamierReel().jouable(creePos(lig, col), jConv)) {
                    res.add(CoupOthello.coupNormal(lig, col));
                }
            }
        }
        if (res.isEmpty()) {
            res.add(CoupOthello.coupPasse());
        }
        return res;
    }

    @Override
    public SituationOthello updateSituation(SituationOthello s, Joueur j, CoupOthello c) {
        if (c.isPasse()) {
            return s;
        } else {
            SituationOthello res = new SituationOthello(s.getDamierReel().copie());
            res.getDamierReel().effectueCoup(creePos(c.getLig(), c.getCol()), convNewJoueurOldJoueur(j));
            return res;
        }
    }

    @Override
    public StatutSituation statutSituation(SituationOthello s) {
        Damier d = s.getDamierReel();
        if (d.auMoinsUneCaseJouable(convNewJoueurOldJoueur(Joueur.J1)) 
                || d.auMoinsUneCaseJouable(convNewJoueurOldJoueur(Joueur.J2))) {
            return StatutSituation.ENCOURS;
        } else {
            int pionsNoirs = d.comptePions(convNewJoueurOldJoueur(Joueur.J1));
            int pionsBlancs = d.comptePions(convNewJoueurOldJoueur(Joueur.J2));
            if (pionsNoirs> pionsBlancs) {
                return StatutSituation.J1_GAGNE;
            } else if (pionsBlancs > pionsNoirs) {
                return StatutSituation.J2_GAGNE;
            } else {
                return StatutSituation.MATCH_NUL;
            }
        }
    }

}
