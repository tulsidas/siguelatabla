package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Torneo extends Model {
   public String nombre;

   // public int anio

   @Required
   @ManyToOne
   public Liga liga;

   // @OneToMany
   // public List<Equipo> equipos;

   // cuantas fechas tiene este torneo
   @Required
   public int fechas;

   // la proxima fecha
   @Required
   public int fecha;

   @Override
   public String toString() {
      return nombre;
   }
}