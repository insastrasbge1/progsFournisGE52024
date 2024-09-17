/*
    Copyright 2000-2011 Francois de Bertrand de Beuvron

    This file is part of UtilsBeuvron.

    UtilsBeuvron is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UtilsBeuvron is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UtilsBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.insa.beuvron.utils.resources;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * define where and how to find icons
 */

public class FindResource implements java.io.Serializable { static final long serialVersionUID =30101L;
//  public static URL findHTMLTemplate(String relPath) {
///*DEBUG*///    System.out.println("trying to find : "+"html/templates/" + relPath);System.out.flush();
//    return FindResource.findInResource("html/templates/" + relPath);
//  }
//
//  public static URL findInResource(String relPath) {
///*DEBUG*///    System.out.print("searching resource : " + relPath);
//    URL res = FindResource.class.getClassLoader().getResource("resources/"+relPath);
///*DEBUG*///    System.out.println(" --> " + res);
//    return res;
//  }
  
  public static URL findInClassFolder(Object obj,String relPath) {
    URL absPath = obj.getClass().getResource(relPath);
    if (absPath == null) {
      throw new Error("resource " + relPath  + " not found for class "+obj.getClass());
    }
    return absPath;
  }

  public static URL findInClassFolderStatic(Class cl,String relPath) {
    URL absPath = cl.getResource(relPath);
    if (absPath == null) {
      throw new Error("resource " + relPath  + " not found for class "+cl);
    }
    return absPath;
  }

}
