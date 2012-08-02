function calcularTabla() {
	filas = {};
	_.each(equipos, function(e) {
		filas[e.id] = {
			"nombre"		: e.nombre,
			"jugados"		: 0,
			"ganados"		: 0,
			"empatados"	: 0,
			"perdidos"	: 0,
			"aFavor"		: 0,
			"enContra"	: 0,
			"totJugados": e.jugadosDescenso,
			"totPuntos"	: e.puntosDescenso
		};
	});
	
	// partidos confirmados
	var confirmados = _.filter(partidos, function(p) { return p.confirmado; });

	var mods = [];

	// los partidos confirmados (y sus modificaciones)
	_.each(confirmados, function(_pc) {
		var pc = _.clone(_pc);
		var mod = modById(pc.id);
		
		// merge de lo confirmado y la modificacion
		if (!_.isUndefined(mod)) {
			if (!_.isUndefined(mod.golesLocal)) {
				pc.golesLocal = mod.golesLocal;
			}
			if (!_.isUndefined(mod.golesVisitante)) {
				pc.golesVisitante = mod.golesVisitante;
			}
		}
		
		mods.push(pc);
	});
	
	// las modificaciones de la nada (que no se aplican sobre un partido confirmado)
	_.each(modificaciones, function(mod) {
		var currentMod = _.find(mods, function(m) { return m.id == mod.id; });
		
		if (_.isUndefined(currentMod)) { // no hay mod sobre partido para esta mod
			if (_.isUndefined(mod.golesLocal)) {
				mod.golesLocal = 0;
			}
			if (_.isUndefined(mod.golesVisitante)) {
				mod.golesVisitante = 0;
			}

			mods.push(mod);
		}
	});

	_.each(mods, function(p) {
		// goles
		filas[p.local_id].aFavor += p.golesLocal;
		filas[p.local_id].enContra += p.golesVisitante;
		filas[p.local_id].jugados += 1;

		filas[p.visitante_id].aFavor += p.golesVisitante;
		filas[p.visitante_id].enContra += p.golesLocal;
		filas[p.visitante_id].jugados += 1;
		
		if (p.golesLocal == p.golesVisitante) {
			filas[p.local_id].empatados += 1;
			filas[p.visitante_id].empatados += 1;
		}
		else if (p.golesLocal > p.golesVisitante) {
			filas[p.local_id].ganados += 1;
			filas[p.visitante_id].perdidos += 1;
		}
		else if (p.golesLocal < p.golesVisitante) {
			filas[p.local_id].perdidos += 1;
			filas[p.visitante_id].ganados += 1;
		}
	});

	_.sortBy(filas, function(f) { return f.puntos*1000 + (f.aFavor - f.enContra); });
	
	var ret = 
		_.map(filas, function(e, id) {
			var puntos = (e.ganados*3 + e.empatados);
			var jugados = e.totJugados + e.jugados;
			return [
				e.nombre, puntos, e.jugados, e.ganados, e.empatados, e.perdidos, 
				e.aFavor, e.enContra, (e.aFavor - e.enContra),
				jugados > 0 ? ((e.totPuntos + puntos) / jugados).toFixed(3) : 0
			];
		})
		
	return ret;
}

function partidosDeLaFecha(fecha) {
	return _.filter(partidos, function(p) { return p.fecha == fecha; });
}

function modById(id) {
	return _.find(modificaciones, function(m) { return m.id == id; });
}

function partidoById(id) {
	return _.find(partidos, function(p) { return p.id == id; });
}

function mostrarPartidos(fecha) {
	var pdf = _.map(partidosDeLaFecha(fecha), function(_p) {
		var p = _.clone(_p);
	
		// actualizo la info segun las modificaciones del usuario
		var mod = modById(p.id);
		
			// TODO DRY ~75
		if (mod) {
			if (!_.isUndefined(mod.golesLocal)) {
				p.golesLocal = mod.golesLocal;
			}
			if (!_.isUndefined(mod.golesVisitante)) {
				p.golesVisitante = mod.golesVisitante;
			}
		}
		
		return p;
	});

	return _.map(pdf, function(p) {
		return {
			"id"							: p.id,
			"local_id"				:	p.local_id, 
			"local"						: p.local,
			"golesLocal"			: p.golesLocal,
			"golesVisitante"	: p.golesVisitante,
			"visitante_id"		: p.visitante_id,
			"visitante"				: p.visitante,
			"cuando"					: p.cuando
		};
	});
}

// quien = L|V
function updateTable(id, quien, goles) {
	var partido = partidoById(id);
	var mod = modById(id);
	
	if (_.isUndefined(mod)) {
		mod = {
			"id"						: partido.id,
			"local"					: partido.local,
			"local_id"			: partido.local_id,
			"visitante"			: partido.visitante,
			"visitante_id"	: partido.visitante_id,
			"golesLocal"		: undefined,
			"golesVisitante": undefined
		};
		modificaciones.push(mod);
	}

	if (quien == 'L') {
		mod.golesLocal = goles;
	}
	else {
		mod.golesVisitante = goles;
	}
	
	var golesLocal = _.isUndefined(mod.golesLocal) ? partido.golesLocal : mod.golesLocal;
	var golesVisitante = _.isUndefined(mod.golesVisitante) ? partido.golesVisitante : mod.golesVisitante;

	// agrego el cambio
	var txt = mod.local + " " + golesLocal + " - " + golesVisitante + " " + mod.visitante;

	var div = $("<div/>").attr("id", "cambio_"+id).addClass("alert");
	if ($('#cambio_'+id).length > 0) {
		div = $($('#cambio_'+id)[0]);
	}

	var close = '<button type="button" class="close" data-dismiss="alert">&times;</button>';
	div.html(txt + close);

	div.alert(); // activo alert
	div.bind('closed', function() {
		// saco la modificacion de la lista de modificaciones
		modificaciones = $.grep(modificaciones, function(m) { return m.id != mod.id; });

		// "recalculando"
		var dt = $('#tabla').dataTable();
		dt.fnClearTable();
		dt.fnAddData(calcularTabla());

		// cambio tabla partidos
		$("#" + mod.id + "_L").text(partido.golesLocal);
		$("#" + mod.id + "_V").text(partido.golesVisitante);
	});

	$('#cambios').append(div);
	
	var dt = $('#tabla').dataTable();
	dt.fnClearTable();
	dt.fnAddData(calcularTabla());
}

function prevFecha() { cambiarFecha(-1); };
function nextFecha() { cambiarFecha(1); };

function createdRow (nRow, aData, iDataIndex) {	
	var row = $(nRow);
	row.attr("id", "partido_" + aData.id);
	
	var tds = row.find("td");
	
	$(tds[1]).addClass("editable");
	$(tds[1]).attr("id", aData.id + "_L");
	$(tds[2]).attr("id", aData.id + "_V");
	$(tds[2]).addClass("editable");
}

function initComplete() {
	$('.editable').editable(
		function(value, settings) {
			var split = this.id.split('_');
			updateTable(split[0], split[1], parseInt(value));

	    return value;
		},
		{ 
	    type    : 'select',
			submit	: "ok",
			data		: "{'0':'0', '1':'1', '2':'2', '3':'3', '4':'4', '5':'5', '6':'6', '7':'7', '8':'8', '9':'9'}"
		}
	);
}

function cambiarFecha(d) {
	if (fecha + d > 0 && fecha + d <= fechas) {
		fecha += d;
		$('#fecha').text(fecha);

		var dt = $('#partidos').dataTable({
			"bRetrieve": true,
			"fnCreatedRow": createdRow,
		});
		
	dt.fnClearTable();
	dt.fnAddData(mostrarPartidos(fecha));
	initComplete();
	}
}