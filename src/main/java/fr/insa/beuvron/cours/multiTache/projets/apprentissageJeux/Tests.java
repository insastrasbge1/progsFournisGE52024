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
package fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux;

import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Coup;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Jeu;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Oracle;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Situation;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.othello.JeuOthello;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.othello.OracleStupideOthello;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.puissance4.CoupPuissance4;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.puissance4.JeuPuissance4;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.puissance4.OracleStupidePuissance4;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.puissance4.SituationPuissance4;
import fr.insa.beuvron.utils.list.ListUtils;
import java.util.List;

/**
 *
 * @author francois
 */
public class Tests {

    public static class JeuEtOracle<Sit extends Situation, Co extends Coup> {

        public Jeu<Sit, Co> jeu;
        public Oracle<Sit> oracle;

        public JeuEtOracle(Jeu<Sit, Co> jeu, Oracle<Sit> oracle) {
            this.jeu = jeu;
            this.oracle = oracle;
        }

        public Jeu<Sit, Co> getJeu() {
            return jeu;
        }

        public Oracle<Sit> getOracle() {
            return oracle;
        }

        public void jouePartieTest(boolean j1h, boolean j2h) {
            this.getJeu().partie(this.getOracle(), this.getOracle(), j1h, j2h, true);
        }

    }

    public static List<JeuEtOracle> JEUX_DISPO = List.of(
            new JeuEtOracle<>(new JeuPuissance4(), new OracleStupidePuissance4()),
            new JeuEtOracle<>(new JeuOthello(), new OracleStupideOthello()));

    public static void testPartie() {
        JeuEtOracle jeu = ListUtils.selectOne("choisissez un jeux :", JEUX_DISPO, j -> j.getJeu().getClass().getSimpleName());
        String j1 = ListUtils.selectOne("selectionnez le premier joueur", List.of("humain", "ordi"), e -> e);
        String j2 = ListUtils.selectOne("selectionnez le second joueur", List.of("humain", "ordi"), e -> e);
        boolean j1h = j1.equals("humain");
        boolean j2h = j2.equals("humain");
        jeu.jouePartieTest(j1h, j2h);

    }

    public static void main(String[] args) {
        testPartie();
    }

}
