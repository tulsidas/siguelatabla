package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Estadio extends Model {
   @Required
   public String nombre;

   @Required
   @Enumerated(EnumType.STRING)
   public Pais pais;

   @OneToOne(optional = true)
   public Equipo equipo;

   @Override
   public String toString() {
      return nombre;
   }
}
