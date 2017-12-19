import java.util.Vector;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Optimisation {

  private SVGDocument document;
  private Core core;

  /**
   * Constructeur
   */
  public Optimisation(Core core) {
    this.core = core;
  }

  // retourne le X actuel
  public double getCurrentX(Vector<SVGPathCollection> groups) {
    double res = 0;
    for (SVGPathCollection group : groups) {
      double max = group.getMaxBoundsX();
      if (max > res) res = max;
    }
    return res;
  }

  // affiche le X
  public void printCX(double cx) {
    core.log("X = " + (cx / core.getZoom()));
    core.log("X (scaled) = " + cx);
  }

  /**
   * Récupère le svg à traiter et renvoie le résultat
   * @param svg le document à traiter
   * @param maxHeight la hauteur du rouleau
   * @return svg le document traité
   */
  public SVGDocument getResult(SVGDocument svg, double maxHeight) throws HeightException {
    Vector<SVGPathCollection> groups = new Vector<SVGPathCollection>();
    groups = svg.getCollections();

    // tri à bulle
    int longueur = groups.size();
    SVGPathCollection tmpSwap;
    boolean inversion;
    do {
      inversion = false;
      for (int i = 0; i < longueur - 1; i++) {
        if (groups.get(i).getWidth() < groups.get(i + 1).getWidth()) {
          tmpSwap = groups.get(i);
          groups.set(i, groups.get(i + 1));
          groups.set(i + 1, tmpSwap);
          inversion = true;
        }
      }
    } while (inversion);

    boolean first = true;
    int count = 0; // tests
    double height;
    double heightBefore = 0;
    double widthBefore = 0;
    double colWidth = 0;
    int sizeVect = groups.size();
    SVGPathCollection lastGroup = groups.get(0);

    double x_before, x_after;

    int x_max = 0;

    if (maxHeight <= 0) throw new HeightException("La hauteur doit être strictement positive !");
    for (SVGPathCollection group : groups) {
      // si l'un des groupes ne rentre pas dans la taille indiquée
      if (group.getHeight() > maxHeight) {
        throw new HeightException();
      }

      x_max += group.getWidth();
    }

    core.log("Xmax = " + (x_max / core.getZoom()));
    core.log("Xmax (scaled) = " + x_max);
    x_before = getCurrentX(groups);
    printCX(x_before);
    core.log("Optimisation...");

    // pour chaque groupe
    for (SVGPathCollection group : groups) {

      count++;
      // System.out.println("**************************");
      // System.out.println("count");
      // System.out.println(count);

      height = group.getHeight();
      // System.out.println("sa hauteur :");
      // System.out.println(height);

      // place l'ensemble des chemins à gauche du document
      while (group.getBoundsX() > 0) {
        group.translate(-1, 0);
      }
      //place l'ensemble des chemins en haut du document
      while (group.getBoundsY() > 0) {
        group.translate(0, -1);
      }

      // définit s'il s'agit du premier groupe d'une colonne
      first = (heightBefore == 0) || (heightBefore + height > maxHeight);
      // System.out.println("premier chemin ?");
      // System.out.println(first);


      // System.out.println("largeur courante");
      // System.out.println(colWidth);

      if (first) {
        heightBefore = 0;
      }

      // System.out.println("translation  de largeur et hauteur avant");
      // System.out.println("hauteur avant");
      // System.out.println(heightBefore);
      // System.out.println("largeur avant");
      // System.out.println(widthBefore);
      group.translate(0, heightBefore);

      lastGroup = group;

      if (first) {
        heightBefore = height;
        widthBefore += colWidth;
        colWidth = group.getWidth();
      } else {
        double getWidthGroup = group.getWidth();
        if (colWidth < getWidthGroup) colWidth = getWidthGroup;
        heightBefore += height;
      }
      group.translate(widthBefore, 0);

      // System.out.println("hauteur avant :");
      // System.out.println(heightBefore);
      // System.out.println("largeur avant");
      // System.out.println(widthBefore);

      // if(first) { //1er de chaque col
      //   System.out.println("premier d'une colonne");
      //   heightBefore = height;
      //   colWidth = group.getWidth();
      //   widthBefore += group.getWidth();
      //   first = false;

      // } else {
      //   //changement de colonne
      //   if(heightBefore + group.getHeight() > maxHeight) {
      //     System.out.println("décalage de largeur");
      //     // group.translate((widthBefore - colWidth), 0);
      //     first = true;
      //   }

      //   //widthBefore
      //   group.translate(0, heightBefore);
      //   //se rapproche du groupe précédent le plus possible
      //   while(!group.intersect(lastGroup)) {
      //     System.out.println("- 5 de hauteur");
      //     group.translate(0, -5);
      //   }
      //   while(group.intersect(lastGroup)) {
      //     System.out.println("+1 de hauteur");
      //     group.translate(0, 1);
      //   }
      // }

      // lastGroup = group;
      // heightBefore += group.getHeight();

    }

    x_after = getCurrentX(groups);
    printCX(x_after);

    core.log("gain = " + (100 - ((x_after * 100) / x_before)) + "%");

    // // Algo qui casse sur complexe 2 par exemple.. :(
    // Rectangle2D borderLeft = new Rectangle2D.Double(2, Integer.MAX_VALUE, 0, 0);
    // double cx = getCurrentX(groups);
    // for (int i = 0; i < longueur; i++) {
    //   boolean collision = false;
    //   int maxMoves = 5000;
    //   while (!collision && maxMoves > 0) {
    //     for (int j = 0; j < longueur; j++) {
    //       if (i != j) {
    //         for (SVGPath ipath : groups.get(i).getPaths()) {
    //           for (SVGPath jpath : groups.get(j).getPaths()) {
    //             Area iarea = new Area(ipath.getPath());
    //             Area jarea = new Area(jpath.getPath());
    //             iarea.intersect(jarea);
    //             collision = !iarea.isEmpty() || groups.get(i).getBoundsX() == 0;
    //           }
    //         }
    //         if (!collision) {
    //           groups.get(i).translate(-1, 0);
    //           // DrawingZone.getInstance().repaint();
    //           // double n_cx = getCurrentX(groups);
    //           // if (n_cx < cx) {
    //           //   cx = n_cx;
    //           //   printCX(cx);
    //           // }
    //           maxMoves--;
    //         } else {
    //           break;
    //         }
    //       }
    //     }
    //   }
    // }

    return svg;
  }
}
