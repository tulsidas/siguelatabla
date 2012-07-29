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

   public List<Partido> getPartidos(Torneo torneo) {
      return em()
            .createQuery(
                  "from Partido where torneo = :torneo and (local = :equipo or visitante = :equipo)",
                  Partido.class).setParameter("torneo", torneo).setParameter("equipo", this)
            .getResultList();
   }
}
