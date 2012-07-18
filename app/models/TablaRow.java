package models;

public class TablaRow /*implements Comparable<TablaRow>*/ {
   // public int pos;

   public Equipo equipo;

   public int puntos;

   public int jugados;

   public int ganados;

   public int empatados;

   public int perdidos;

   public int aFavor;

   public int enContra;

   public TablaRow(Equipo equipo) {
      this.equipo = equipo;
   }

   public void partido(int aFavor, int enContra) {
      this.jugados++;
      this.aFavor += aFavor;
      this.enContra += enContra;

      if (aFavor > enContra) {
         ganados++;
         puntos += 3;
      }
      else if (aFavor == enContra) {
         empatados++;
         puntos += 1;
      }
      else {
         perdidos++;
      }
   }

//   @Override
//   public int compareTo(TablaRow otra) {
//      return otra.puntos == puntos ? (otra.aFavor - otra.enContra) - (aFavor - enContra)
//            : otra.puntos - puntos;
//   }
}
