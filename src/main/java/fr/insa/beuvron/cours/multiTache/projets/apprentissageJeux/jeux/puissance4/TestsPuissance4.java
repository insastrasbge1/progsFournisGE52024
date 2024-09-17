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

/**
 *
 * @author francois
 */
public class TestsPuissance4 {

    public static void testStupid() {
        JeuPuissance4 jt = new JeuPuissance4();
        jt.partie(new OracleStupidePuissance4(), new OracleStupidePuissance4(),
                false, false, true);

    }

    public static void testHvsH() {
        JeuPuissance4 jt = new JeuPuissance4();
        jt.partie(new OracleStupidePuissance4(), new OracleStupidePuissance4(),
                true, true, true);

    }

    public static void main(String[] args) {
        testStupid();
//        testHvsH();
                
    }

}
