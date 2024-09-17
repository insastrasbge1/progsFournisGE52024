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
package fr.insa.beuvron.cours.multiTache.projets.apprentissageJeux.apiJeux;

import fr.insa.beuvron.utils.list.ListUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Rassemble les informations permettant de jouer une partie.
 * <p>
 * dépend de la représentation des situations et des coups possibles </p>
 * <p>
 * Nous laissons la notion de situation la plus libre possible pour pouvoir
 * utiliser des classes déjà existantes qui gère des jeux particuliers :
 * </p>
 * <p>
 * Exemple : j'ai déjà une classe existante Damier pour le jeu d'Othello qui
 * sait déterminer les coups jouables, modifier le damier lorsqu'un joueur joue
 * à une position donné ...
 * </p>
 * <p>
 * Souvent les méthodes coupsJouables, updateSituation, et statutSituation ne
 * seront que des "proxy" qui appelle les méthodes correspondante dans la
 * représentation (Damier pour l'Othello) spécifique au jeu.
 * </p>
 *
 * @author francois
 */
public interface Jeu<Sit extends Situation, Co extends Coup> {

    public Sit situationInitiale();

    /**
     * Quels sont les coups possibles pour le joueur j connaissant la situation
     * s.
     * <p>
     * Rappel : passer est un coup particulier si cela est possible dans le jeu
     * </p>
     * <p>
     * si dans le jeu, le joueur est obligé de passer, cette fonction retournera
     * une liste singleton contenant uniquement le coup "passe" </p>
     *
     * @param s
     * @param j
     * @return
     */
    public List<Co> coupsJouables(Sit s, Joueur j);

    /**
     * Quelle est la nouvelle situation, après que le joueur j ait joué en c sur
     * la situation intiale s.
     * <p>
     * on suppose que c est jouable. ie c in coupsJouables(s,j)
     * <p>
     * <p>
     * attention : suivant le jeu, il faut prendre en compte le coup particulier
     * "passe" </p>
     *
     * @param s
     * @param j
     * @param c
     * @return
     */
    public Sit updateSituation(Sit s, Joueur j, Co c);

    /**
     * Teste si la situation correspond à une victoire d'un des joueurs, à un
     * match nul, ou à une partie en cours.
     *
     * @param s
     * @return
     */
    public StatutSituation statutSituation(Sit s);

    /**
     * Partie exécutée automatiquement entre deux stratèges.
     * <p>
     * Cette méthode par défaut est donnée ici pour donner une idée de
     * l'utilisation et des interactions des méthodes définies dans les diverses
     * interfaces. Elle peut être modifiée/supprimée dans l'implémentation
     * finale.
     * </p>
     *
     * @param o1 un Oracle pour guider le joueur J1
     * @param o2 un Oracle pour guider le joueur J2
     * @param j1Humain true iff J1 est joué par un humain
     * @param j2Humain true iff J2 est joué par un humain
     * @param trace if true, affiche les situations successives
     * @return
     */
    default public List<Co> partie(Oracle<Sit> o1,
            Oracle<Sit> o2, boolean j1Humain, boolean j2Humain,
            boolean trace) {
        List<Co> res = new ArrayList<>();
        Sit curSit = this.situationInitiale();
        var joueurs = List.of(Joueur.J1, Joueur.J2);
        var oracles = List.of(o1, o2);
        var humains = List.of(j1Humain, j2Humain);
        int numJoueur = 0;   // 0 pour J1, 1 pour J2
        while (this.statutSituation(curSit) == StatutSituation.ENCOURS) {
            if (trace) {
                System.out.println("----- Sit actuelle -------");
                System.out.println(curSit);
            }
            Oracle<Sit> curOracle = oracles.get(numJoueur);
            Oracle<Sit> oracleAdversaire = oracles.get(1 - numJoueur);
            Joueur curJoueur = joueurs.get(numJoueur);
            boolean humain = humains.get(numJoueur);
            List<Co> possibles = this.coupsJouables(curSit, curJoueur);
            Co coupChoisi;
            if (humain) {
                coupChoisi = ListUtils.selectOne("choisissez votre coup : ", possibles, Object::toString);
            } else {
                // je demande au stratege courant d'évaluer les coups jouables
                // pour cela, je joue effectivement les coups, je calcule la
                // nouvelle situation, et je demande à l'oracle ADVERSE de l'évaluer
                List<Double> evals = new ArrayList<>();
                for (Co c : possibles) {
                    Sit nouvelle = this.updateSituation(curSit, curJoueur, c);
                    evals.add(oracleAdversaire.evalSituation(nouvelle));
                }
                // Clairement, on devrait se servir des évaluations pour choisir le coup
                // mais on voulait dans cette version provisoire juste montrer comment
                // peut se faire le calcul des évaluations par l'oracle
                // pour l'instant, on choisi un coup au hasard sans tenir compte des évaluations
                int numCoup = (int) (Math.random() * possibles.size());
                coupChoisi = possibles.get(numCoup);
            }
            if (trace) {
                System.out.println("je joueur " + curJoueur + " joue en " + coupChoisi);
            }
            // je change la situation en fonction du coup réellement choisi
            curSit = this.updateSituation(curSit, curJoueur, coupChoisi);
            // et je passe au joueur suivant sauf si passe
            numJoueur = 1 - numJoueur;
        }
        if (trace) {
            System.out.println("----- Sit finale -------");
            System.out.println(curSit);
            StatutSituation statut = this.statutSituation(curSit);
            if (statut == StatutSituation.MATCH_NUL) {
                System.out.println("Match nul");
            } else if (statut == StatutSituation.J1_GAGNE) {
                System.out.println("Gagnant : " + Joueur.J1);
            } else {
                System.out.println("Gagnant : " + Joueur.J2);
            }
        }
        return res;
    }

}
