package tracks.singlePlayer.evaluacion.src_SANCHEZ_FERNANDEZ_JUAN;
import java.util.ArrayList;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.HashSet;


/**
 * 
*/
public class AgenteDijkstra extends AbstractPlayer{	
	boolean intransitables[][];
	Vector2d fescala;
	Queue<ACTIONS> camino;
	
	/**
	 * @brief Constructor del agente
	 * @param stateObs
	 * @param elapsedTimer
	 */
	public AgenteDijkstra(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        init(stateObs,elapsedTimer);
	}
	
	//private boolean esTransitable(double x, double y);
	
	private boolean esTransitable(Vector2d posicion) {
		return !intransitables[(int)posicion.x][(int) posicion.y];
	}
	
	private boolean Dijkstra(StateObservation stateObs, Vector2d portal, ElapsedCpuTimer elapsedTimer) {
		// Inicializamos la cola de prioridad de nodos abiertos y el conjunto de nodos visitados
		PriorityQueue<Nodo> Abiertos = new PriorityQueue<Nodo>();
		HashSet<Nodo> Visitados = new HashSet<Nodo>();
		
		
		// Obtenemos la posicion del avatar y creamos el nodo inicial
		Vector2d avatar = new Vector2d(stateObs.getAvatarPosition().x /fescala.x ,	stateObs.getAvatarPosition().y/fescala.y);
		
		//Se inicializa el nodo a la posición y orientación del avatar y a distancia 0
		Nodo actual = new Nodo(avatar, stateObs.getAvatarOrientation(), 0);
		
		actual.clearSecuencia();
		
		Abiertos.add(actual);
		
		Integer contador = 0;
		
		System.out.println("Comenzando la búsqueda del camino");
		System.out.println("Posición del avatar: " + avatar.x + " " + avatar.y);
		System.out.println("Posición del portal: " + portal.x + " " + portal.y);
		System.out.println("Tamaño de Abiertos: " + Abiertos.size());
		
		System.out.println("La orientación del avatar es: " + actual.getOrientacion().x + " " + actual.getOrientacion().y);
		
		while(!Abiertos.isEmpty() && actual.getFila() != portal.y && actual.getColumna() != portal.x){

			Visitados.add(actual);
			
			System.out.println("AAAPosición actual: " + actual.getPosicion().x + " " + actual.getPosicion().y);
			
			// Arriba
			Vector2d newPosUp = new Vector2d(actual.getColumna(), actual.getFila() - 1);
			System.out.println("Nueva posición arriba: " + newPosUp.x + " " + newPosUp.y);
			if(actual.getFila() - 1 >= 0 && esTransitable(newPosUp)){
				if(actual.getOrientacion() == new Vector2d(-1,0)) {
					Nodo hijoUp = new Nodo(newPosUp, actual.getOrientacion(), actual.getDistancia() + 1);
					hijoUp.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoUp)) {
						hijoUp.addAccion(ACTIONS.ACTION_UP);
						Abiertos.add(hijoUp);
					}
					else {
						System.out.println("Ya estaba visitado");
					}
				}
				else {
					Nodo hijoUp = new Nodo(actual.getPosicion(), new Vector2d(-1, 0), actual.getDistancia());
					hijoUp.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoUp)) {
						hijoUp.addAccion(ACTIONS.ACTION_UP);
						Abiertos.add(hijoUp);
					}
				}
			}
			else {
				System.out.println("No era transitable");
			}
			
			// Abajo
			Vector2d newPosDown = new Vector2d(actual.getColumna(), actual.getFila() + 1);
			System.out.println("Nueva posición abajo: " + newPosDown.x + " " + newPosDown.y);
			if(actual.getFila() + 1 <= (stateObs.getObservationGrid()[0].length - 1)){
				if(actual.getOrientacion() == new Vector2d(1,0)) {
					Nodo hijoDown = new Nodo(newPosDown, actual.getOrientacion(), actual.getDistancia() + 1);
					hijoDown.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoDown)) {
						hijoDown.addAccion(ACTIONS.ACTION_DOWN);
						Abiertos.add(hijoDown);
					}
					else {
						System.out.println("Ya estaba visitado");
					}
				}
				else {
					Nodo hijoDown = new Nodo(actual.getPosicion(), new Vector2d(1, 0), actual.getDistancia());
					hijoDown.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoDown)) {
						hijoDown.addAccion(ACTIONS.ACTION_DOWN);
						Abiertos.add(hijoDown);
					}
				}
			}
			else {
				System.out.println("No era transitable");
			}
			
			
			// Izquierda
			Vector2d newPosLeft = new Vector2d(actual.getColumna() - 1, actual.getFila());
			System.out.println("Nueva posición izquierda: " + newPosLeft.x + " " + newPosLeft.y);
			if(actual.getColumna() - 1 >= (0) && esTransitable(newPosLeft)){
				if(actual.getOrientacion() == new Vector2d(0,-1)) {
					Nodo hijoLeft = new Nodo(newPosLeft, actual.getOrientacion(), actual.getDistancia() + 1);
					hijoLeft.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoLeft)) {
						hijoLeft.addAccion(ACTIONS.ACTION_LEFT);
						Abiertos.add(hijoLeft);
					}
					else {
						System.out.println("Ya estaba visitado");
					}
				}
				else {
					Nodo hijoLeft = new Nodo(actual.getPosicion(), new Vector2d(0, -1), actual.getDistancia());
					hijoLeft.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoLeft)) {
						hijoLeft.addAccion(ACTIONS.ACTION_LEFT);
						Abiertos.add(hijoLeft);
					}
				}
			}
			else {
				System.out.println("No era transitable");
			}
			
			// Derecha
			Vector2d newPosRight = new Vector2d(actual.getColumna() + 1, actual.getFila());
			System.out.println("Nueva posición derecha: " + newPosRight.x + " " + newPosRight.y);
			if(actual.getColumna() + 1 <= (stateObs.getObservationGrid().length - 1) && esTransitable(newPosRight)){
				if(actual.getOrientacion() == new Vector2d(0,1)) {
					Nodo hijoRight = new Nodo(newPosRight, actual.getOrientacion(), actual.getDistancia() + 1);
					hijoRight.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoRight)) {
						hijoRight.addAccion(ACTIONS.ACTION_RIGHT);
						Abiertos.add(hijoRight);
					}
				}
				else {
					Nodo hijoRight = new Nodo(actual.getPosicion(), new Vector2d(0,1), actual.getDistancia());
					hijoRight.setSecuencia(actual.getSecuencia());
					
					if (!Visitados.contains(hijoRight)) {
						hijoRight.addAccion(ACTIONS.ACTION_RIGHT);
						Abiertos.add(hijoRight);
					}
					else {
						System.out.println("Ya estaba visitado");
					}
				}
			}
			else {
				System.out.println("No era transitable");
			}
			
			if (!Abiertos.isEmpty()) {
				System.out.println("Tamaño de Abiertos: " + Abiertos.size());
				
				System.out.println("El primer nodo a escoger es " + Abiertos.peek().getPosicion().x + " " + Abiertos.peek().getPosicion().y + " con orientación " + Abiertos.peek().getOrientacion().x + " " + Abiertos.peek().getOrientacion().y);
				actual = Abiertos.poll();
				
				while (Visitados.contains(actual) && !Abiertos.isEmpty()) {
					actual = Abiertos.poll();
				}
			}
			
			System.out.println("Posición actual: " + actual.getPosicion().x + " " + actual.getPosicion().y);
			contador++;
			
			if(contador > 10)
				break;
		}
		System.out.println("Tamaño de Visitados: " + Visitados.size());
		System.out.println("Número de nodos visitados: " + contador);
		System.out.println("Terminada la búsqueda del camino");
			
		if (actual.getFila() == portal.y && actual.getColumna() == portal.x) {
			System.out.println("Camino encontrado");
			camino = actual.getSecuencia();
			System.out.println("Tamaño del camino: " + camino.size());
			return true;
		}
		else {
			System.out.println("Camino no encontrado");
			return false;
		}
		
    }
	
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
		Vector2d portal = objetivos[0].get(0).position;
		portal.x = Math.floor(portal.x / fescala.x);
		portal.y = Math.floor(portal.y / fescala.y);
						
		// Llamamos a Dijkstra
		if (Dijkstra(stateObs, portal, elapsedTimer)) {
			System.out.println("Camino encontrado");
		} else {
			System.out.println("Camino no encontrado");
		}
	}
	
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		if (camino != null && !camino.isEmpty()) {
			return camino.poll();
		}
		else
			return ACTIONS.ACTION_NIL;
	}

}