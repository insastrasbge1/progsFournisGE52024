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

package fr.insa.beuvron.cours.jeux.othelloSimple.gui;

import fr.insa.beuvron.cours.jeux.othelloSimple.Joueur;
import fr.insa.beuvron.cours.jeux.othelloSimple.Position;
import javax.swing.*;


/**
 * l'automate représentant les différents états possibles de l'interface
 */

public class Scheduler{

  private static class EtatPartie {
    public int code;
    public String message;

    public EtatPartie(int code,String mess) {
      this.code = code;
      this.message = mess;
    }

    public String toString() {
      return "[" + super.toString() + " " + this.code + "]";
    }
  }

    /** todoDoc. */
    public static final EtatPartie ETAT_NEUTRE = new EtatPartie(0,"Cliquez sur 'nouvelle partie'");

    /** todoDoc. */
    public static final EtatPartie ETAT_ATTENTE_JOUEUR_BLANC = new EtatPartie(1,
      "Joeur blanc : cliquez sur le damier pour jouer, ou passez");

    /** todoDoc. */
    public static final EtatPartie ETAT_ATTENTE_JOUEUR_NOIR = new EtatPartie(2,
      "Joeur noir : cliquez sur le damier pour jouer, ou passez");

    /** todoDoc. */
    public static final EtatPartie DEBUT_JEU_ORDINATEUR = new EtatPartie(3,
      "L'ordinateur est en train de jouer ...");

    /**
     *
     * 
     */
    public void setEtat(EtatPartie nEtat) {
    this.othPanel.affMessage(nEtat.message);
    this.etat = nEtat;
  }

    /** todoDoc. */
    public void nouvellePartieEvent() {
    this.othPanel.leDamier.init();
    this.othPanel.jPanelDamier.initCases();
    this.joueurCourant = Joueur.blanc();
    this.debutCoupJoueur();
  }

    /**
     *
     * 
     * 
     */
    public Position coupOrdi(Joueur j) {
    return this.othPanel.leDamier.ordinateurJoue(j);
  }

    /** todoDoc. */
    public void debutCoupJoueur() {
    if(this.ordinateurJoue(this.joueurCourant)) {
      this.setEtat(this.DEBUT_JEU_ORDINATEUR);
      if (this.othPanel.leDamier.auMoinsUneCaseJouable(this.joueurCourant)) {
        Position oj = this.coupOrdi(this.joueurCourant);
        this.othPanel.leDamier.effectueCoup(oj,this.joueurCourant);
        this.adversaireAPasse = false;
        this.othPanel.jPanelDamier.redessine();
        this.joueurCourant = this.joueurCourant.adversaire();
        this.debutCoupJoueur();
      }
      else {
        this.othPanel.affWarning("Ordinateur passe");
        if (this.adversaireAPasse) {
          this.finPartieEvent();
        }
        else {
          this.adversaireAPasse = true;
          this.joueurCourant = this.joueurCourant.adversaire();
          this.debutCoupJoueur();
        }
      }
    }
    else {
      if(this.joueurCourant.estNoir()) {
        this.setEtat(ETAT_ATTENTE_JOUEUR_NOIR);
      }
      else {
        this.setEtat(ETAT_ATTENTE_JOUEUR_BLANC);
      }
    }
  }

    /**
     *
     * 
     */
    public void boutonCliqueEvent(CaseBouton cb) {
    if (this.etat == ETAT_ATTENTE_JOUEUR_BLANC ||
        this.etat == ETAT_ATTENTE_JOUEUR_NOIR) {
      if (this.verifieCoup(cb.laPosition)) {
        this.othPanel.clearAllMessages();
        this.othPanel.leDamier.effectueCoup(cb.laPosition, this.joueurCourant);
        this.adversaireAPasse = false;
        this.othPanel.jPanelDamier.redessine();
        this.joueurCourant = this.joueurCourant.adversaire();
        this.debutCoupJoueur();
      }
      else {
        this.othPanel.affErreur("Coup invalide");
      }
    }
  }

    /**
     *
     * 
     * 
     */
    public boolean verifieCoup(Position p) {
    return this.othPanel.leDamier.jouable(p,this.joueurCourant);
  }

    /**
     *
     * 
     */
    public boolean verifiePasse() {
    return ! this.othPanel.leDamier.auMoinsUneCaseJouable(this.joueurCourant);
  }

    /** todoDoc. */
    public void joueurPasseEvent() {
    if (this.etat == ETAT_ATTENTE_JOUEUR_BLANC ||
        this.etat == ETAT_ATTENTE_JOUEUR_NOIR) {
      if (this.verifiePasse()) {
        this.othPanel.clearAllMessages();
        if (this.adversaireAPasse) {
          this.finPartieEvent();
        }
        else {
          this.adversaireAPasse = true;
          this.joueurCourant = this.joueurCourant.adversaire();
          this.debutCoupJoueur();
        }
      }
      else {
        this.othPanel.affErreur("Passe impossible : au moins un coup jouable");
      }
    }
  }

    /** todoDoc. */
    public void finPartieEvent() {
    long nbrNoirs = this.othPanel.leDamier.comptePions(Joueur.noir());
    long nbrBlancs = this.othPanel.leDamier.comptePions(Joueur.blanc());
    JComponent[] mess = new JComponent[3];
    mess[0] = new JLabel(" : " + nbrNoirs,CaseBouton.PION_NOIR,SwingConstants.TRAILING);
    mess[1] = new JLabel(" : " + nbrBlancs,CaseBouton.PION_BLANC,SwingConstants.TRAILING);
    if (nbrNoirs > nbrBlancs) {
      mess[2] = new JLabel(" GAGNE",CaseBouton.PION_NOIR,SwingConstants.TRAILING);
    }
    else if ( nbrBlancs  > nbrNoirs) {
      mess[2] = new JLabel(" GAGNE",CaseBouton.PION_BLANC,SwingConstants.TRAILING);
    }
    else {
      mess[2] = new JLabel("MATCH NUL !!!");
    }
    JOptionPane.showMessageDialog(this.othPanel,mess,"Fin de Partie",JOptionPane.OK_OPTION);
    this.setEtat(ETAT_NEUTRE);

  }

    /**
     *
     * 
     */
    public Scheduler(OthelloPanel op) {
    this.othPanel = op;
    this.etat = ETAT_NEUTRE;
    this.adversaireAPasse = false;
  }

    /**
     *
     * 
     * 
     */
    public boolean ordinateurJoue(Joueur j) {
    if(j.estNoir()) {
      return this.othPanel.jCheckBoxNoirOrdi.isSelected();
    }
    else {
      return this.othPanel.jCheckBoxBlancOrdi.isSelected();
    }
  }

    /** todoDoc. */
    public OthelloPanel othPanel;

    /** todoDoc. */
    public EtatPartie etat;

    /** todoDoc. */
    public Joueur joueurCourant;

    /** todoDoc. */
    public boolean adversaireAPasse;

}
