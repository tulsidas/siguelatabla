package controllers;

import java.util.Date;
import java.util.List;

import models.Equipo;
import models.Liga;
import models.Pais;
import models.Partido;
import models.Torneo;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

import com.google.common.collect.Lists;

@With(Secure.class)
public class Partidos extends Controller {
   public static void pais() {
      List<Pais> paises = Lists.newArrayList(Pais.values());
      render(paises);
   }

   public static void liga(Pais pais) {
      if (pais == null) {
         notFound();
      }
      List<Liga> ligas = Liga.find("byPais", pais).fetch();
      render(ligas);
   }

   public static void torneo(long ligaId) {
      Liga liga = Liga.findById(ligaId);
      if (liga == null) {
         notFound();
      }
      List<Torneo> torneos = Torneo.find("byLiga", liga).fetch();
      render(torneos);
   }

   public static void alta(long torneoId) {
      Torneo torneo = Torneo.findById(torneoId);
      if (torneo == null) {
         notFound();
      }
      
//      List<Estadio> estadios = Estadio.find("byPais", torneo.liga.pais).fetch();

      render(torneo/*, estadios*/);
   }

   public static void doAlta(@Required Long torneoId, @Required Date cuando,
         @Required Long localId, @Required Long visitanteId, @Required Integer fecha/* TODO estadio*/) {

      Equipo local = Equipo.findById(localId);
      Equipo visitante = Equipo.findById(visitanteId);
      Torneo torneo = Torneo.findById(torneoId);

      if (local == null) {
         validation.addError("local", "validation.required");
      }
      if (visitante == null) {
         validation.addError("visitante", "validation.required");
      }

      if (localId == visitanteId) {
         validation.addError("visitante", "validation.same");
      }

      if (validation.hasErrors()) {
         params.flash(); // add http parameters to the flash scope
         validation.keep(); // keep the errors for the next request
         alta(torneoId);
      }

      Partido p = new Partido();
      p.cuando = cuando;
      p.local = local;
      p.visitante = visitante;
      p.torneo = torneo;
      p.fecha = fecha;
      // TODO estadio
      p.golesLocal = 0;
      p.golesVisitante = 0;
      p.confirmado = false;
      p.save();
      
      // flash.success("partido grabado");
      alta(torneoId);
   }
}