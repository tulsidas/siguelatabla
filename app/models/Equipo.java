package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Equipo extends Model {
   @Required
   public String nombre;

   @Required
   @Enumerated(EnumType.STRING)
   public Pais pais;

   @ManyToMany
   public List<Torneo> torneos;

   // descenso
   public int puntosDescenso;
   public int jugadosDescenso;
   // descenso

   @Override
   public String toString() {
      return nombre;
   }
}
