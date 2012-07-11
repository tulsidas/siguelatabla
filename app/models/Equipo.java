package models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Equipo extends Model {
   @Required
   public String nombre;

   @Override
   public String toString() {
      return nombre;
   }
}
