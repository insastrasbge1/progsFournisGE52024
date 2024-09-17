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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * représentation swing d'un damier
 */

public class OthelloPanel extends JPanel{

    /** todoDoc. */
    public Damier leDamier;

    /** todoDoc. */
    public Scheduler sc;

    /** todoDoc. */
    public OthelloPanel() {
    try {
      this.sc = new Scheduler(this);
      this.leDamier = new Damier();
      jPanelDamier = new DamierPanel(this.leDamier,sc);
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

    /**
     *
     * 
     */
    public void affMessage(String mess) {
    this.jLabelMess.setForeground(Color.BLUE);
    this.jLabelMess.setText(mess);
  }

    /**
     *
     * 
     */
    public void affErreur(String mess) {
    this.jLabelErreurs.setForeground(Color.RED);
    this.jLabelErreurs.setText(mess);
  }

    /**
     *
     * 
     */
    public void affWarning(String mess) {
    this.jLabelErreurs.setForeground(Color.yellow);
    this.jLabelErreurs.setText(mess);
  }

    /** todoDoc. */
    public void clearAllMessages() {
    this.jLabelErreurs.setText("");
    this.jLabelMess.setText("");
  }

  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jCheckBoxNoirOrdi.setText("Ordi joue les noirs");
    jCheckBoxBlancOrdi.setText("Ordi joue les blancs");
    jPanel2.setLayout(new BoxLayout(this.jPanel2, BoxLayout.Y_AXIS));
    jButtonNouvelle.setText("Nouvelle Partie");
    jButtonNouvelle.addActionListener(new
        OthelloPanel_jButtonNouvelle_actionAdapter(this));
    jButtonPasse.setText("Passe");
    jButtonPasse.addActionListener(new OthelloPanel_jButtonPasse_actionAdapter(this));
    jLabelMess.setText("messages");
    jPanel1.setLayout(gridLayout1);
    gridLayout1.setColumns(1);
    gridLayout1.setRows(2);
    jLabelErreurs.setText("erreurs");
    jLabelBlancs.setText(" = Blancs");
    jLabelBlancs.setIcon(CaseBouton.PION_BLANC);
    jLabelNoirs.setText(" = Noirs");
    jLabelNoirs.setIcon(CaseBouton.PION_NOIR);
    this.add(jPanel2, java.awt.BorderLayout.EAST);
    jPanel2.add(jLabelBlancs);
    jPanel2.add(jLabelNoirs);
    jPanel2.add(jCheckBoxNoirOrdi);
    jPanel2.add(jCheckBoxBlancOrdi);
    jPanel2.add(jButtonNouvelle);
    jPanel2.add(jButtonPasse);
    this.add(jPanelDamier, java.awt.BorderLayout.CENTER);
    this.add(jPanel1, java.awt.BorderLayout.SOUTH);
    jPanel1.add(jLabelErreurs);
    jPanel1.add(jLabelMess);
  }



  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  JCheckBox jCheckBoxNoirOrdi = new JCheckBox();
  JCheckBox jCheckBoxBlancOrdi = new JCheckBox();
  JButton jButtonNouvelle = new JButton();
  DamierPanel jPanelDamier;
  JButton jButtonPasse = new JButton();
  JLabel jLabelMess = new JLabel();
  JPanel jPanel1 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JLabel jLabelErreurs = new JLabel();
  JLabel jLabelBlancs = new JLabel();
  JLabel jLabelNoirs = new JLabel();

    /**
     *
     * 
     */
    public void jButtonNouvelle_actionPerformed(ActionEvent e) {
    this.sc.nouvellePartieEvent();
  }

    /**
     *
     * 
     */
    public void jButtonPasse_actionPerformed(ActionEvent e) {
    this.sc.joueurPasseEvent();
  }

}

class OthelloPanel_jButtonPasse_actionAdapter
    implements ActionListener {
  private OthelloPanel adaptee;
  OthelloPanel_jButtonPasse_actionAdapter(OthelloPanel adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonPasse_actionPerformed(e);
  }
}

class OthelloPanel_jButtonNouvelle_actionAdapter
    implements ActionListener {
  private OthelloPanel adaptee;
  OthelloPanel_jButtonNouvelle_actionAdapter(OthelloPanel adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonNouvelle_actionPerformed(e);
  }

}
