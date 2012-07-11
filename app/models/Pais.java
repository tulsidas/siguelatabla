package models;

public enum Pais {
   AR("Argentina"), CL("Chile"), BO("Bolivia"), BR("Brasil"), PY("Paraguay"), UY("Uruguay"), ES("España"), IT("Italia"), DE("Alemania"), FR("Francia");

   public String nombre;

   private Pais(String nombre) {
      this.nombre = nombre;
   }
}
