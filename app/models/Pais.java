package models;

public enum Pais {
   //@formatter:off
   AR("Argentina"); 
//   CL("Chile"),
//   BO("Bolivia"),
//   BR("Brasil"),
//   PY("Paraguay"), 
//   UY("Uruguay"),
//   IT("Italia"),
//   DE("Alemania"),
//   FR("Francia");
//   ES("España");
   //@formatter:on

   public String nombre;

   private Pais(String nombre) {
      this.nombre = nombre;
   }

   public String getISO() {
      return name();
   }

   @Override
   public String toString() {
      return nombre;
   }
}
