package controllers;

import java.util.List;
import java.util.Map;

import models.Comentario;
import models.Equipo;
import models.Liga;
import models.Pais;
import models.Partido;
import models.Torneo;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
      List<Integer> puntos = getPuntos(partidos, equipo);

      Equipo lider = getLider(torneo.partidos);
      List<Integer> puntosLider = getPuntos(lider.getPartidos(torneo), lider);

      render(equipo, torneo, partidos, puntos, lider, puntosLider);
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

   // ////////////////////////////////////////////////////////////////
   // private
   // ////////////////////////////////////////////////////////////////
   private static List<Integer> getPuntos(List<Partido> partidos, Equipo equipo) {
      List<Integer> puntos = Lists.newArrayList(0); // arranco en 0
      int _puntos = 0;
      for (Partido p : partidos) {
         if (p.confirmado) {
            if (p.gano(equipo)) {
               _puntos += 3;
            }
            else if (p.empate()) {
               _puntos += 1;
            }

            puntos.add(_puntos);
         }
      }

      return puntos;
   }

   private static Equipo getLider(List<Partido> partidos) {
      Map<Equipo, Integer> puntos = Maps.newHashMap();

      for (Partido p : partidos) {
         if (!puntos.containsKey(p.local)) {
            puntos.put(p.local, 0);
         }
         if (!puntos.containsKey(p.visitante)) {
            puntos.put(p.visitante, 0);
         }

         if (p.empate()) {
            puntos.put(p.local, puntos.get(p.local) + 1);
            puntos.put(p.visitante, puntos.get(p.visitante) + 1);
         }
         else if (p.golesLocal > p.golesVisitante) {
            puntos.put(p.local, puntos.get(p.local) + 3);
         }
         else {
            puntos.put(p.visitante, puntos.get(p.visitante) + 3);
         }
      }

      Equipo lider = null;
      for (Map.Entry<Equipo, Integer> entry : puntos.entrySet()) {
         if (lider == null) {
            lider = entry.getKey();
         }
         else {
            if (entry.getValue() > puntos.get(lider)) {
               lider = entry.getKey();
            }
         }
      }

      return lider;
   }
}

// TODO mostrar cuando un cambio cambia la tabla (animacion de equipo subiendo /
// bajando)

// @formatter:off

// INSERT INTO Equipo VALUES (null, '{{cells["nombre"].value}}', PAIS, 0, 0,
// '{{cells["nombre"].value}}');
// INSERT INTO Equipo_Torneo VALUES ( LAST_INSERT_ID(), TORNEO_ID);

// cross(cell, "equipos", "nombre").cells["id"].value[0]

// INSERT INTO Partido VALUES (null, false, {{cells["fecha"].value}}, 0, 0,
// '{{cells["cuando"].value.toString("yyyy-MM-dd
// 00:00:00")}}', {{cells["local_id"].value}}, <<torneo>>, {{cells["visitante_id"].value}});