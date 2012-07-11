package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

   public static void index(Pais pais, Long ligaId, Long torneoId, Integer fecha) {
      if (pais == null) {
         pais = Pais.AR;
      }

      List<Liga> ligas = Liga.find("byPais", pais).fetch();

      Liga liga = ligas.get(0);
      if (ligaId != null) {
         liga = Liga.findById(ligaId);
      }

      List<Torneo> torneos = Torneo.find("byLiga", liga).fetch();

      Torneo torneo = torneos.get(0);
      if (torneoId != null) {
         torneo = Torneo.findById(torneoId);
      }

      if (fecha == null || fecha < 1 || fecha > torneo.fechas) {
         fecha = torneo.fecha; // la ultima
      }

      List<Partido> partidos = Partido.find("byTorneoAndFecha", torneo, fecha).fetch();

      render(pais, ligas, liga, torneos, torneo, partidos, fecha);
   }

}