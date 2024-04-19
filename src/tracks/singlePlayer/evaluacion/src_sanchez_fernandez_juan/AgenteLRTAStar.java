package tracks.singlePlayer.evaluacion.src_SANCHEZ_FERNANDEZ_JUAN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class AgenteLRTAStar extends AbstractPlayer{
	Vector2d fescala, portal;
	Stack<ACTIONS> camino;
	ArrayList<Observation>[] obstaculos;

	// Mapa que almacena los nodos y sus heuristicas previas.
	HashMap<NodoRTA, Integer> heuristicas;
	
	public AgenteLRTAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		init(stateObs, elapsedTimer);
	}

	public void init(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		heuristicas = new HashMap<NodoRTA, Integer>();

		fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
				stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

		// Obtenemos las posiciones de los portales
		ArrayList<Observation>[] objetivos = stateObs.getPortalsPositions(stateObs.getAvatarPosition());

		// Obtenemos la posicion del portal más cercano
		portal = objetivos[0].get(0).position;
		portal.x = Math.floor(portal.x / fescala.x);
		portal.y = Math.floor(portal.y / fescala.y);
	}

	private boolean esTransitable(StateObservation stateObs, Vector2d posicion) {
		ArrayList<Observation>[] obsCercanos = stateObs.getImmovablePositions(stateObs.getAvatarPosition());
		for (int i = 0; i < 2; i++) {
			for (Observation obs : obsCercanos[i]) {
				int xObstaculo = (int) (obs.position.x / fescala.x);
				int yObstaculo = (int) (obs.position.y / fescala.y);

				if (posicion.x == xObstaculo && posicion.y == yObstaculo) {
					return false;
				}
			}
		}

		return true;
	}

	private int distManhattan(Vector2d a, Vector2d b) {
		return (int) (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
	}

	private ACTIONS LRTAStar(StateObservation stateObs, Vector2d portal) {
		PriorityQueue<NodoRTA> sucesores = new PriorityQueue<NodoRTA>();

		// Obtenemos la posicion y orientación del avatar
		Vector2d posicion = new Vector2d(stateObs.getAvatarPosition().x / fescala.x,
				stateObs.getAvatarPosition().y / fescala.y);
		Vector2d orientActual = stateObs.getAvatarOrientation();

		NodoRTA nodoActual = new NodoRTA(posicion, orientActual);

		if (nodoActual.getPosicion().equals(portal)) {
			System.out.println("Numero de nodos expandidos:" + heuristicas.size());
			return ACTIONS.ACTION_NIL;
		}

		// Generamos sucesores

		// Arriba
		NodoRTA hijoUp = new NodoRTA(nodoActual);
		Vector2d newPosUp = new Vector2d(nodoActual.getColumna(), nodoActual.getFila() - 1);

		if (hijoUp.getFila() - 1 >= 0 && esTransitable(stateObs, newPosUp)) {
			/**
			 * Si el nodo es transitable ajustamos la posición 
			 * del nodo @hijoUp para que represente el nodo superior.
			 */
			hijoUp.setPosicion(newPosUp);

			/**
			 * Buscamos el nodo actual en la tabla hash de heursticas y nodos.
			 * Si el nodo no está en la tabla hash, su heurística es la distancia 
			 * Manhattan.
			 * Si el nodo ya ha sido visitado usamos la heuristica almacenada
			 */
			if (heuristicas.containsKey(hijoUp))
				hijoUp.setH(heuristicas.get(hijoUp));
			else
				hijoUp.setH(distManhattan(hijoUp.getPosicion(), portal));

			/**
			 * Añadimos el coste de moverse a dicha casilla (siempre 1) y la accion
			 * que origina dciho movimiento. Finalemente añadimos el nodo a la cola
			 * con prioridad para eventualmente obtener el de menor coste.
			 */
			hijoUp.setG(1);
			hijoUp.addAccion(ACTIONS.ACTION_UP);
			sucesores.add(hijoUp);
		} else {
			/**
			 * Si la casilla no es transitable, añadimos el nodo pero con una h infinita.
			 * Lo almacenamos en la tabla de heurísticas para no volver a expandirlo.
			 */
			hijoUp.setH(Integer.MAX_VALUE);
			sucesores.add(hijoUp);
		}

		// Abajo
		NodoRTA hijoDown = new NodoRTA(nodoActual);
		Vector2d newPosDown = new Vector2d(nodoActual.getColumna(), nodoActual.getFila() + 1);

		if (hijoDown.getFila() + 1 <= (stateObs.getObservationGrid()[0].length - 1) && esTransitable(stateObs, newPosDown)) {

			hijoDown.setPosicion(newPosDown);

			if (heuristicas.containsKey(hijoDown))
				hijoDown.setH(heuristicas.get(hijoDown));
			else
				hijoDown.setH(distManhattan(hijoDown.getPosicion(), portal));

			hijoDown.setG(1);
			hijoDown.addAccion(ACTIONS.ACTION_DOWN);
			sucesores.add(hijoDown);
		} else {
			hijoDown.setH(Integer.MAX_VALUE);
			sucesores.add(hijoDown);
		}

		// Izquierda
		NodoRTA hijoLeft = new NodoRTA(nodoActual);
		Vector2d newPosLeft = new Vector2d(nodoActual.getColumna() - 1, nodoActual.getFila());

		if (hijoLeft.getColumna() - 1 >= 0 && esTransitable(stateObs, newPosLeft)) {

			hijoLeft.setPosicion(newPosLeft);

			if (heuristicas.containsKey(hijoLeft))
				hijoLeft.setH(heuristicas.get(hijoLeft));
			else
				hijoLeft.setH(distManhattan(hijoLeft.getPosicion(), portal));

			hijoLeft.setG(1);
			hijoLeft.addAccion(ACTIONS.ACTION_LEFT);
			sucesores.add(hijoLeft);
		} else {
			hijoLeft.setH(Integer.MAX_VALUE);
			sucesores.add(hijoLeft);
		}

		// Derecha
		NodoRTA hijoRight = new NodoRTA(nodoActual);
		Vector2d newPosRight = new Vector2d(nodoActual.getColumna() + 1, nodoActual.getFila());

		if (hijoRight.getColumna() + 1 <= (stateObs.getObservationGrid().length - 1)
				&& esTransitable(stateObs, newPosRight)) {
			hijoRight.setPosicion(newPosRight);

			if (heuristicas.containsKey(hijoRight))
				hijoRight.setH(heuristicas.get(hijoRight));
			else
				hijoRight.setH(distManhattan(hijoRight.getPosicion(), portal));

			hijoRight.setG(1);
			hijoRight.addAccion(ACTIONS.ACTION_RIGHT);
			sucesores.add(hijoRight);
		} else {
			hijoRight.setH(Integer.MAX_VALUE);
			sucesores.add(hijoRight);
		}

		/**
		 * - Estrategia de movimiento: cogemos el nodo más prometedor
		 * ( @mejorSucesor ) y devolvemos la acción que lo ha originado.
		 * - Espacio local de aprendizaje: @nodoActual
		 * - Regla de aprendizaje: h(x) = max(h(x), 2º min(f(y))
		 *
		 */

		NodoRTA mejorSucesor = sucesores.poll();
		ACTIONS accionSiguiente = mejorSucesor.getAccion();

		int heuristicaAprendida = Math.max(nodoActual.getH(), mejorSucesor.f());

		heuristicas.put(nodoActual, heuristicaAprendida);

		return accionSiguiente;
	}

	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		return LRTAStar(stateObs, portal);
	}


}
