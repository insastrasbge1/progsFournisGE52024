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

import fr.insa.beuvron.cours.jeux.othelloSimple.Case;
import fr.insa.beuvron.cours.jeux.othelloSimple.Position;
import fr.insa.beuvron.utils.resources.FindResource;
import java.awt.Color;
import java.net.URL;
import javax.swing.*;

/**
 * représentation swing d'un damier
 */

public class CaseBouton extends JButton {

    /** todoDoc. */
    public static final Icon PION_NOIR
            = findIcon("icons/pionNoir.png");

    /** todoDoc. */
    public static final Icon PION_BLANC
            = findIcon("icons/pionBlanc.png");

    /** todoDoc. */
    public static final Icon CASE_VIDE
            = findIcon("icons/caseVide.png");
    
    /**
     *
     * 
     * 
     */
    public static Icon findIcon(String relPath) {
        URL uicon = FindResource.findInClassFolderStatic(CaseBouton.class, relPath);
        if (uicon != null) {
            return new ImageIcon(uicon);
        } else {
            return null;
        }
        
    }
    
    /** todoDoc. */
    public Case laCase;

    /** todoDoc. */
    public Position laPosition;

    /**
     *
     * 
     * 
     */
    public CaseBouton(Case c,Position p) {
    super();
    this.laCase = c;
    this.laPosition = p;
    this.fixeIcone();
  }

    /** todoDoc. */
    public void fixeIcone() {
        if (this.laCase.estVide()) {
            if (CASE_VIDE != null) {
                this.setIcon(CaseBouton.CASE_VIDE);
            } else {
                this.setBackground(Color.lightGray);
            }
        } else if (this.laCase.estNoir()) {
            if (PION_NOIR != null) {
                this.setIcon(CaseBouton.PION_NOIR);
            } else {
                this.setBackground(Color.BLACK);
            }
        } else {
            if (PION_BLANC != null) {
                this.setIcon(CaseBouton.PION_BLANC);
            } else {
                this.setBackground(Color.WHITE);
            }
        }
    }


}
