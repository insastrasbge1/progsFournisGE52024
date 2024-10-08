package fr.insa.beuvron.cours.multiTache.exemplesCours.trie;

import java.util.Arrays;

/**
 * tri recursif sur place d'un tableau :
 * <pre> {@code
 * trier(T1) :
 *  -si taille(T1) > 1
 *   - selectionne un element e de T1
 *   - separer le tableau en 3 :
 *      - éléments < e TInf
 *      - éléments = e
 *      - éléments > e Tsup
 *   -trier(TInf)
 *   -trier(TSup)
 * } </pre>
 *
 * @author fdebertranddeb01
 *
 */
public class TriMoyenneSequentiel {

    /**
     * {@code
     * il faut choisir un entier e tel que min(t) <= e <= max(t) pour classer ceux qui sont < , > ou = à e
     * On pourrait tout simplement prendre le premier élément de t,
     * mais comme l'algorithme sera plus efficace si le nombre d'éléments < et > à e sont à peu près égaux,
     * on décide de calculer la moyenne des éléments.
     * Note : le meilleur serait de calculer la médiane, mais pour trouver la médiane, il faut en
     * gros trier le tableau, donc on tourne en rond
     * On prend donc la moyenne en espérant que la moyenne soit proche de la médiane (ce qui dépend
     * de la répartition des éléments et n'est donc pas garanti)
     * }
     */
    public static double moyenne(int[] t, int debut, int fin) {
        double moy = 0;
        double nbr = fin - debut;   // je le met directement en double pour eviter le problème de la division entiere
        for (int i = debut; i < fin; i++) {
            moy = moy + t[i] / nbr;
        }
        return moy;

    }

    public static void tri(int[] tab) {
        triBorne(tab, 0, tab.length - 1);
    }

    /**
     * {@code
     * trie les élements de t d'indice debut <= i < fin
     * }
     */
    public static void triBorne(int[] t, int debut, int fin) {
        if (fin - debut > 1) {
            int poub; // pour les échanges
            double e = moyenne(t, debut, fin);
            System.out.println("Moyenne = " + e);
            // on veut arriver à un tableau avec d'abord tous les elems < e puis tous les elems = e puis tous
            // les elems > e
            int inf = debut;  // on sait que tous les elems d'indices < inf sont inférieurs à e
            int egal = debut;  // on sait que tous les elems d'indices < egal sont inférieurs ou égaux à e
            int sup = fin;    // on sait tous les elems d'indices >= sup sont supérieurs à e
            while (egal < sup) {
                if (t[egal] < e) {
                    // il faut placer l'élément en dernier des mins
                    // le premier des égal devient le dernier des égal
                    poub = t[inf];
                    t[inf] = t[egal];
                    t[egal] = poub;
                    inf++;
                    egal++;
                } else if (t[egal] == e) {
                    // rien a faire, on a simplement trouve un égal de plus
                    egal++;
                } else {
                    // on échange l'élement courant comme nouveau premier des sups
                    // il y a un supp de plus
                    sup--;
                    poub = t[egal];
                    t[egal] = t[sup];
                    t[sup] = poub;
                }
            }
            System.out.println("t = " + Arrays.toString(t));
            System.out.println("debut = " + debut + "fin = " + fin + " ; inf = " + inf + " ; egal = " + egal + " ; sup = " + sup );
            // il nous reste à trier recursivement les inf et les supp
            triBorne(t, debut, inf);
            triBorne(t, sup, fin);
        }
    }

    /**
     *
     * @param size
     * @param bmax
     */
    public static void test(int size, int bmax) {
        int[] t = TriSequentiel.tabAlea(size, bmax);
        System.out.println("trie tableau taille : " + size
                + " (0 <= e < " + bmax + ")");
        System.out.println("t = " + Arrays.toString(t));
        long deb = System.currentTimeMillis();
        tri(t);
        long duree = System.currentTimeMillis() - deb;
        System.out.println("t = " + Arrays.toString(t));
        System.out.println("test : " + TriSequentiel.testTrie(t));
        System.out.println("in " + duree + " ms");

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        test(5, 5000);
    }

}
