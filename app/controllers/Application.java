package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import org.apache.commons.lang.StringUtils;

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

      Liga liga = ligas.get(0); // agarro cualquiera
      if (ligaId != null) {
         liga = Liga.findById(ligaId);
      }

      List<Torneo> torneos = Torneo.find("byLiga", liga).fetch();

      Torneo torneo = torneos.get(0);
      if (torneoId != null) {
         torneo = Torneo.findById(torneoId);
      }

      List<Partido> partidos = Partido.em()
            .createQuery("from Partido where torneo = :torneo order by cuando asc", Partido.class)
            .setParameter("torneo", torneo).getResultList();

      boolean showHelp = !session.contains("closedHelp");

      render(pais, ligas, liga, torneos, torneo, partidos, showHelp);
   }

   public static void equipo(Long equipoId, Long torneoId) {
      if (equipoId == null || torneoId == null) {
         redirect("Application.index");
      }

      Torneo torneo = Torneo.findById(torneoId);
      Equipo equipo = Equipo.findById(equipoId);

      if (equipo == null || torneo == null) {
         redirect("Application.index");
      }

      List<Partido> partidos = equipo.getPartidos(torneo);

      render(equipo, torneo, partidos);
   }

   public static void closedHelp() {
      session.put("closedHelp", "true");
      renderText("");
   }

   public static void contacto() {
      render();
   }

   public static void doContacto(String nombre, String email, String comentario) {
      if (StringUtils.isNotEmpty(comentario)) {
         Comentario c = new Comentario();
         c.nombre = nombre;
         c.email = email;
         c.comentario = comentario;
         c.save();

         flash("msg", "¡Gracias por contactarte!");
      }

      index(null, null, null);
   }
}

// TODO mostrar cuando un cambio cambia la tabla (animacion de equipo subiendo /
// bajando)
// TODO resaltar al primero
// TODO resaltar a los descensos

// @formatter:off

// INSERT INTO Equipo VALUES (null, '{{cells["nombre"].value}}', PAIS, 0, 0,
// '{{cells["nombre"].value}}');
// INSERT INTO Equipo_Torneo VALUES ( LAST_INSERT_ID(), TORNEO_ID);

// cross(cell, "equipos", "nombre").cells["id"].value[0]

// INSERT INTO Partido VALUES (null, false, {{cells["fecha"].value}}, 0, 0,
// '{{cells["cuando"].value.toString("yyyy-MM-dd
// 00:00:00")}}', {{cells["local_id"].value}}, <<torneo>>, {{cells["visitante_id"].value}});