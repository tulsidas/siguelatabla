package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Torneo extends Model {
   public String nombre;

   // public int anio

   @Required
   @ManyToOne
   public Liga liga;

   @ManyToMany(mappedBy = "torneos", fetch = FetchType.LAZY)
   @OrderBy("nombre")
   public List<Equipo> equipos;

   @OneToMany(mappedBy = "torneo", fetch = FetchType.LAZY)
   public List<Partido> partidos;

   // cuantas fechas tiene este torneo
   @Required
   @Min(1)
   public int fechas;

   // la fecha actual
   @Required
   public int fecha;

   @Override
   public String toString() {
      return nombre;
   }
}