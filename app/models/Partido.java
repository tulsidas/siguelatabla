package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.BatchSize;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Partido extends Model {
   @Required
   @Temporal(TemporalType.TIMESTAMP)
   public Date cuando;

   @Required
   @ManyToOne(fetch = FetchType.LAZY)
   public Equipo local;

   @Required
   @ManyToOne(fetch = FetchType.LAZY)
   public Equipo visitante;

   @Required
   @ManyToOne
   public Torneo torneo;

   public int golesLocal;

   public int golesVisitante;

   public boolean confirmado;

   // a qué fecha (número de partido dentro del torneo) corresponde
   @Required
   public int fecha;

   @Override
   public String toString() {
      return "Fecha " + fecha + " - " + local + " vs " + visitante;
   }

   /**
    * con join fetch de equipos y estadios
    */
   public static List<Partido> byTorneo(Torneo t) {
      return em()
            .createQuery("from Partido p join fetch p.torneo where p.torneo = :torneo",
                  Partido.class).setParameter("torneo", t).getResultList();
      // t join fetch t.equipos where p.torneo = :torneo
   }

   /**
    * los partidos del torneo anteriores a la fecha dada que no hayan sido
    * confirmados
    */
   public static List<Partido> findNoConfirmados(Torneo torneo, int fecha) {
      return em()
            .createQuery(
                  "from Partido p where p.torneo = :torneo and p.fecha < :fecha and confirmado = false",
                  Partido.class).setParameter("torneo", torneo).setParameter("fecha", fecha)
            .getResultList();
   }

   public boolean gano(Equipo e) {
      return (local.equals(e) && golesLocal > golesVisitante)
            || (visitante.equals(e) && golesLocal < golesVisitante);
   }

   public boolean empate() {
      return golesLocal == golesVisitante;
   }

   public boolean perdio(Equipo e) {
      return (local.equals(e) && golesLocal < golesVisitante)
            || (visitante.equals(e) && golesLocal > golesVisitante);
   }
}