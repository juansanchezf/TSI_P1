package tracks.singlePlayer.evaluacion.src_SANCHEZ_FERNANDEZ_JUAN;
import java.util.ArrayList;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.PriorityQueue;
import java.util.Stack;
import java.util.HashSet;

public class AgenteAStar extends AbstractPlayer{
	boolean intransitables[][];
	Vector2d fescala;
	Stack<ACTIONS> camino;
	Vector2d portal;
	boolean planificado;
	
	/**
	 * @brief Constructor del agente
	 * @param stateObs
	 * @param elapsedTimer
	 */
	public AgenteAStar(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		init(stateObs, elapsedTimer);
	}
	
	/**
	 * @brief Inicializa el agente
	 * @param stateObs
	 * @param elapsedTimer
	 */
	public void init(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		// Obtenemos las posiciones de los obstaculos
		ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();
		
		fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
				stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
		
		// Inicializamos la matriz de intransitables al tamaño del grid
		intransitables = new boolean[stateObs.getObservationGrid().length][stateObs.getObservationGrid()[0].length];
		
		// Marcamos las posiciones de los obstaculos
		for (int i = 0; i < obstaculos.length; i++) {
			for (int j = 0; j < obstaculos[i].size(); j++) {
				Vector2d pos = obstaculos[i].get(j).position;
				intransitables[(int) (pos.x/fescala.x)][(int)(pos.y/fescala.y)] = true;
			}
		}
		
		// Obtenemos las posiciones de los portales
		ArrayList<Observation>[] objetivos = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
		
		// Obtenemos la posicion del portal más cercano
		portal = objetivos[0].get(0).position;
		portal.x = Math.floor(portal.x / fescala.x);
		portal.y = Math.floor(portal.y / fescala.y);
		
		planificado = false;
	}
	
	/**
	 * @brief Comprueba si una posicion determinada es transitable
	 * @param posicion
	 * @return
	 */
	private boolean esTransitable(Vector2d posicion) {
		return !intransitables[(int)posicion.x][(int) posicion.y];
	} 
	
	/**
	 * @brief Reconstruye el camino a partir del nodo final recorriendo los padres de cada nodo
	 * @param finalNodo
	 * @return
	 */
	private Stack<ACTIONS> reconstruirCamino(Nodo finalNodo)
	{
		Stack<ACTIONS> camino = new Stack<ACTIONS>();
		Nodo actual = finalNodo;
		
		while(actual.padre != null) 
		{
			camino.push(actual.accion);
			actual = actual.padre;
		}
		
		return camino;
	}
	
	/**
	 * @brief Calcula la distancia de Manhattan entre dos puntos
	 * @param a
	 * @param b
	 * @return
	 */
	private int distManhattan(Vector2d a, Vector2d b) {
		return (int) (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
	}
	
	/**
	 * @brief Implementacion del algoritmo A*
	 * @param stateObs
	 * @param portal
	 * @param elapsedTimer
	 * @return
	 */
	private boolean AStar(StateObservation stateObs, Vector2d portal, ElapsedCpuTimer elapsedTimer)
	{
		// Inicializamos la cola de prioridad de nodos abiertos y el conjunto de nodos visitados
		PriorityQueue<Nodo> abiertos = new PriorityQueue<Nodo>();
		HashSet<Nodo> cerrados = new HashSet<Nodo>();
		
		// Obtenemos la posicion del avatar y creamos el nodo inicial
		Vector2d posAvatar = new Vector2d(stateObs.getAvatarPosition().x /fescala.x ,	stateObs.getAvatarPosition().y/fescala.y);
		
		//Se inicializa el nodo a la posición y orientación del avatar
		Nodo inicial = new Nodo(posAvatar, stateObs.getAvatarOrientation());
		inicial.setH(distManhattan(posAvatar, portal));
		
		abiertos.add(inicial);
		
		Nodo actual = null;
		
		while(!abiertos.isEmpty()) {
			actual = abiertos.poll();
			
			if (actual.getPosicion().equals(portal)) {
				camino = reconstruirCamino(actual);
				System.out.println("Numero de nodos expandidos: " + cerrados.size());
				return true;
			}
			
			if(!cerrados.add(actual))	continue;
			
			Vector2d orientActual = actual.getOrientacion();
			
			//ARRIBA
			Nodo hijoUp = new Nodo(actual);
			Vector2d newPosUp = new Vector2d(actual.getColumna(), actual.getFila()-1);
			Vector2d orientacionUp = new Vector2d(0,-1);
			
			if(hijoUp.getFila() - 1 >= 0 && esTransitable(newPosUp)) 
			{
				if(orientActual.equals(orientacionUp)) 
					hijoUp.setPosicion(newPosUp);
				else
					hijoUp.setOrientacion(orientacionUp);
				
				if(!cerrados.contains(hijoUp)) 
				{
					hijoUp.setG(actual.getG() + 1);
					hijoUp.setH(distManhattan(hijoUp.getPosicion(), portal));
					hijoUp.addAccion(ACTIONS.ACTION_UP);
					abiertos.add(hijoUp);
				}
			}
			
			//ABAJO
			Nodo hijoDown = new Nodo(actual);
			Vector2d newPosDown = new Vector2d(actual.getColumna(), actual.getFila() + 1);
			Vector2d orientacionDown = new Vector2d(0,1); 
			
			if (hijoDown.getFila() + 1 <= (stateObs.getObservationGrid()[0].length - 1) && esTransitable(newPosDown))
			{
				if (orientActual.equals(orientacionDown))
					hijoDown.setPosicion(newPosDown);
				else
					hijoDown.setOrientacion(orientacionDown);
				
				if (!cerrados.contains(hijoDown)) 
				{
					hijoDown.setG(actual.getG() + 1);
					hijoDown.setH(distManhattan(hijoDown.getPosicion(), portal));
					hijoDown.addAccion(ACTIONS.ACTION_DOWN);
					abiertos.add(hijoDown);
				}
			}
			
			// IZQUIERDA
			Nodo hijoLeft = new Nodo(actual);
			Vector2d newPosLeft = new Vector2d(actual.getColumna() - 1, actual.getFila());
			Vector2d orientacionLeft = new Vector2d(-1,0);
						
			if(hijoLeft.getColumna() - 1 >= 0 && esTransitable(newPosLeft))
            {				
				if(orientActual.equals(orientacionLeft))
                    hijoLeft.setPosicion(newPosLeft);
				else
                	hijoLeft.setOrientacion(orientacionLeft);
				
				if(!cerrados.contains(hijoLeft))
                {
					hijoLeft.setG(actual.getG() + 1);
					hijoLeft.setH(distManhattan(hijoLeft.getPosicion(), portal));
                    hijoLeft.addAccion(ACTIONS.ACTION_LEFT);
                    abiertos.add(hijoLeft);
                }
            }
			
			// DERECHA
			Nodo hijoRight = new Nodo(actual);
			Vector2d newPosRight = new Vector2d(actual.getColumna() + 1, actual.getFila());
			Vector2d orientacionRight = new Vector2d(1,0);			
			if(hijoRight.getColumna() + 1 <= (stateObs.getObservationGrid().length - 1) && esTransitable(newPosRight))
            {                
                if(orientActual.equals(orientacionRight))
                    hijoRight.setPosicion(newPosRight);
                else
                	hijoRight.setOrientacion(orientacionRight);
                
                if(!cerrados.contains(hijoRight))
                {
					hijoRight.setG(actual.getG() + 1);
					hijoRight.setH(distManhattan(hijoRight.getPosicion(), portal));
					hijoRight.addAccion(ACTIONS.ACTION_RIGHT);
					abiertos.add(hijoRight);
                }
            }
		}
		
		return false;
	}

	
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		// Llamamos a A* si no hay aun un camino
		if(!planificado) 
		{
			// Llamamos a A*
			long tInicio = System.nanoTime();
			boolean hayCamino = AStar(stateObs, portal, elapsedTimer);
			long tFin = System.nanoTime();
			long tiempoTotalms = (tFin - tInicio)/1000000;
			
			if(hayCamino) 
			{
				System.out.println("Tiempo de planificación A*: " + tiempoTotalms + " ms");
				System.out.println("Tamaño de la ruta: " + camino.size());
			}
			else
				System.out.println("Camino no encontrado.");
			
			planificado = true;
		}
		
		// Devolvemos la siguiente accion del camino si hay uno
		if (camino != null && !camino.isEmpty()) {
			return camino.pop();
		}
		else
			return ACTIONS.ACTION_NIL;
	}
}
