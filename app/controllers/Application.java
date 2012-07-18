package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import models.*;

public class Application extends Controller {

   public static void index(Pais pais, Long ligaId, Long torneoId) {
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

      // TODO optimizar y traer en 1 query equipos y estadios
      // List<Partido> partidos = Partido.find("byTorneoAndFecha", torneo, fecha).fetch();
      List<Partido> partidos = Partido.find("byTorneo", torneo).fetch();

      render(pais, ligas, liga, torneos, torneo, partidos);
   }
}

// TODO mostrar los cambios hechos por el usuario y que pueda deshacerlos (boton X por ej)
// TODO mostrar cuando un cambio cambia la tabla (animacion de equipo subiendo / bajando)
// TODO promedio