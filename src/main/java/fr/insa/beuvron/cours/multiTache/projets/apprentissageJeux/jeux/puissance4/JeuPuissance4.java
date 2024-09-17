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

import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Jeu;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Joueur;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.StatutSituation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author francois
 */
public class JeuPuissance4 implements Jeu<SituationPuissance4, CoupPuissance4> {

    @Override
    public SituationPuissance4 situationInitiale() {
        return new SituationPuissance4();
    }

    @Override
    public List<CoupPuissance4> coupsJouables(SituationPuissance4 s, Joueur j) {
        List<CoupPuissance4> res = new ArrayList<>();
        for (int i = 0; i < s.getNbrCol(); i++) {
            if (s.colJouable(i)) {
                res.add(new CoupPuissance4(i));
            }
        }
        return res;
    }

    @Override
    public SituationPuissance4 updateSituation(SituationPuissance4 s, Joueur j, CoupPuissance4 c) {
        // pour puissance4 jamais de passe, simplement un test inutile
        // pour ne pas oublier dans d'autres jeux
        if (c.isPasse()) {
            return s;
        } else {
            // attention : joue dans SituationPuissance4 est destructif 
            // ==> il faut travailler sur une copie
            SituationPuissance4 res = s.copie();
            int couleurJoueur;
            if (j == Joueur.J1) {
                couleurJoueur = 1;
            } else {
                couleurJoueur = -1;
            }
            res.joue(c.getColonne(), couleurJoueur);
            return res;
        }
    }

    @Override
    public StatutSituation statutSituation(SituationPuissance4 s) {
        if (s.noirGagne()) {
            return StatutSituation.J1_GAGNE;
        } else if (s.blancGagne()) {
            return StatutSituation.J2_GAGNE;
        } else if (s.fini()) {
            return StatutSituation.MATCH_NUL;
        } else {
            return StatutSituation.ENCOURS;
        }
    }

}
