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

import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.ChoixCoup;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Coup;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Jeu;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Joueur;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Oracle;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.OracleStupide;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.ResumeResultat;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Situation;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.StatutSituation;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.othello.JeuOthello;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.othello.SituationOthello;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.puissance4.JeuPuissance4;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.jeux.puissance4.SituationPuissance4;
import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.list.ListUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author francois
 */
public class Tests {

    public static class JeuEtOracles<Sit extends Situation, Co extends Coup> {

        private String description;
        private Jeu<Sit, Co> jeu;
        private Oracle<Sit> oracleJ1;
        private Oracle<Sit> oracleJ2;

        public JeuEtOracles(String description, Jeu<Sit, Co> jeu, Oracle<Sit> oracleJ1, Oracle<Sit> oracleJ2) {
            this.description = description;
            this.jeu = jeu;
            this.oracleJ1 = oracleJ1;
            this.oracleJ2 = oracleJ2;
        }

        public void jouePartieTest(boolean j1h, boolean j2h) {
            this.getJeu().partie(this.getOracleJ1(), ChoixCoup.ORACLE_MEILLEUR, this.getOracleJ2(), ChoixCoup.ORACLE_MEILLEUR,
                    j1h, j2h, new Random(), true);
        }

        /**
         * joue un ensemble de partie J1 contre J2 avec choix des coups par
         * aléatoire pondéré.
         *
         * @param nbr nombre de partie à jouer
         * @return [nombre de parties gagnées par J1,nombre de parties gagnées
         * par J2]
         */
        public int[] joueDesParties(int nbr) {
            Random rand = new Random();
            int[] res = new int[2];
            for (int i = 0; i < nbr; i++) {
                ResumeResultat<Co> resUne = this.getJeu().partie(getOracleJ1(), ChoixCoup.ORACLE_PONDERE, getOracleJ2(), ChoixCoup.ORACLE_PONDERE,
                        false, false, rand, false);
                if (resUne.getStatutFinal() == StatutSituation.J1_GAGNE) {
                    res[0]++;
                } else if (resUne.getStatutFinal() == StatutSituation.J2_GAGNE) {
                    res[1]++;
                }
            }
            return res;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return the jeu
         */
        public Jeu<Sit, Co> getJeu() {
            return jeu;
        }

        /**
         * @return the oracleJ1
         */
        public Oracle<Sit> getOracleJ1() {
            return oracleJ1;
        }

        /**
         * @return the oracleJ2
         */
        public Oracle<Sit> getOracleJ2() {
            return oracleJ2;
        }
    }

    public static List<JeuEtOracles> JEUX_DISPO = List.of(new JeuEtOracles<>("Puissance4 Oracles stupide (répondent 0.5 à toutes les situations)",
            new JeuPuissance4(), new OracleStupide<SituationPuissance4>(Joueur.J1), new OracleStupide<SituationPuissance4>(Joueur.J2)),
            new JeuEtOracles<>("Othello Oracles stupide (répondent 0.5 à toutes les situations)",
                    new JeuOthello(), new OracleStupide<SituationOthello>(Joueur.J1), new OracleStupide<SituationOthello>(Joueur.J2)));

    public static void testUnePartie() {
        JeuEtOracles jeu = ListUtils.selectOne("choisissez un jeux :", JEUX_DISPO, j -> j.getDescription());
        String j1 = ListUtils.selectOne("selectionnez le premier joueur", List.of("humain", "ordi"), e -> e);
        String j2 = ListUtils.selectOne("selectionnez le second joueur", List.of("humain", "ordi"), e -> e);
        boolean j1h = j1.equals("humain");
        boolean j2h = j2.equals("humain");
        jeu.jouePartieTest(j1h, j2h);

    }

    public static void statPlusieursParties() {
        JeuEtOracles jeu = ListUtils.selectOne("choisissez un jeux :", JEUX_DISPO, j -> j.getDescription());
        int nbr = ConsoleFdB.entreeInt("nombre de parties à jouer : ");
        int[] res = jeu.joueDesParties(nbr);
        System.out.println("Résultats : ");
        System.out.println("Parties gagnées par J1 : " + res[0]);
        System.out.println("Parties gagnées par J2 : " + res[1]);
        System.out.println("et donc matchs nuls : " + (nbr - res[0] - res[1]));
    }

    public static void testVsAlea1() {
        var jeu = new JeuOthello();
        Oracle<SituationOthello> o2 = new OracleStupide<SituationOthello>(Joueur.J1);
        int[] res1 = jeu.partieVsAlea(o2, 100, new Random());
        System.out.println("res as J1 : " + Arrays.toString(res1));
        Oracle<SituationOthello> o1 = new OracleStupide<SituationOthello>(Joueur.J2);
        int[] res2 = jeu.partieVsAlea(o1, 100, new Random());
        System.out.println("res as J2 : " + Arrays.toString(res2));
    }

    public static void main(String[] args) {
//        testUnePartie();
        statPlusieursParties();
//        testVsAlea1();
    }

}
