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

import fr.insa.beuvron.cours.jeux.othelloSimple.Damier;
import fr.insa.beuvron.cours.jeux.othelloSimple.Position;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;


/**
 * représentation swing d'un damier
 */

public class DamierPanel extends JPanel{

    /** todoDoc. */
    public Damier leDamier;

    /** todoDoc. */
    public CaseBouton[][] rep;

    /** todoDoc. */
    public Scheduler scheduler;

    /**
     *
     * 
     * 
     */
    public DamierPanel(Damier d,Scheduler s) {
    this.leDamier = d;
    this.scheduler = s;
    GridLayout l = new GridLayout(8,8,3,3);
    this.setLayout(l);
    this.initCases();
  }

    /** todoDoc. */
    public void initCases() {
    this.removeAll();
    this.rep = new CaseBouton[8][8];
    ButtonAction action = new ButtonAction();
    for(int i = 0 ; i < 8 ; i ++) {
      for(int j = 0 ; j < 8 ; j ++) {
        Position p = new Position();
        p.ligne = i;
        p.col = j;
        CaseBouton b = new CaseBouton(this.leDamier.getVal(i, j),p);
        b.addActionListener(action);
        this.rep[i][j] = b;
        this.add(b);
      }
    }
    this.revalidate();
  }

    /** todoDoc. */
    public void redessine() {
    for(int i = 0 ; i < 8 ; i ++) {
      for(int j = 0 ; j < 8 ; j ++) {
        this.rep[i][j].fixeIcone();
      }
    }
    this.revalidate();

  }

    /** todoDoc. */
    public class ButtonAction extends AbstractAction {

        /**
         *
         * 
         */
        public void actionPerformed(ActionEvent e) {
      CaseBouton src = (CaseBouton) e.getSource();
/*DEBUG*///      System.out.println("coucou " + src.laPosition);
      DamierPanel.this.scheduler.boutonCliqueEvent(src);
    }
  }

}
