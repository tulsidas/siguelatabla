package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Liga extends Model {
   @Required
   public String nombre;

   @Required
   @Enumerated(EnumType.STRING)
   public Pais pais;

   @Override
   public String toString() {
      return nombre;
   }
}
