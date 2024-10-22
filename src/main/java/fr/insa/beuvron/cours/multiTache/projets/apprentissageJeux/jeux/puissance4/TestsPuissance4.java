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

import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.ChoixCoup;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.Joueur;
import fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux.OracleStupide;
import java.util.Random;

/**
 *
 * @author francois
 */
public class TestsPuissance4 {

    public static void testStupid() {
        JeuPuissance4 jt = new JeuPuissance4();
        jt.partie(
                new OracleStupide<SituationPuissance4>(Joueur.J1), ChoixCoup.ALEA,
                new OracleStupide<SituationPuissance4>(Joueur.J2), ChoixCoup.ALEA,
                false, false,new Random(), true);

    }

    public static void testHvsH() {
        JeuPuissance4 jt = new JeuPuissance4();
        jt.partie(
                new OracleStupide<SituationPuissance4>(Joueur.J1), ChoixCoup.ALEA,
                new OracleStupide<SituationPuissance4>(Joueur.J2), ChoixCoup.ALEA,
                true, true,new Random(), true);

    }

    public static void main(String[] args) {
        testStupid();
//        testHvsH();
                
    }

}
