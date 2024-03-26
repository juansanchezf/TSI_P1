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
		PriorityQueue<Nodo> abiertos = new PriorityQueue<Nodo>();
		HashSet<Nodo> cerrados = new HashSet<Nodo>();

		// Obtenemos la posicion del avatar y creamos el nodo inicial
		Vector2d avatar = new Vector2d(stateObs.getAvatarPosition().x /fescala.x ,	stateObs.getAvatarPosition().y/fescala.y);
		
		//Se inicializa el nodo a la posición y orientación del avatar y a distancia 0
		Nodo inicial = new Nodo(avatar, stateObs.getAvatarOrientation(), 0);		
		abiertos.add(inicial);
		
		Integer contador = 0;
		
		
		System.out.println("Comenzando la búsqueda del camino");
		/*System.out.println("Avatar: (" + avatar.x + " , " + avatar.y + ")");
		System.out.println("Portal: (" + portal.x + " , " + portal.y + ")");
		
		System.out.println("Orientación inicial: (" + inicial.getOrientacion().x + " , " + inicial.getOrientacion().y + ")");*/
		
		Nodo actual = null;
		
		while(!abiertos.isEmpty()) {
			actual = abiertos.poll();
			
			if (actual.getPosicion().x == portal.x && actual.getPosicion().y == portal.y) {
				System.out.println("Camino encontrado");
				camino = actual.secuencia;
				return true;
			}
			
			// Si ya se ha visitado este nodo, se salta, si no, se añade a cerrados
			if (!cerrados.add(actual)) {
				continue;
			}
			
			//System.out.println("Posición actual: (" + actual.posicion.x + " , " + actual.posicion.y + ") Orientación: (" + actual.orientacion.x + " , " + actual.orientacion.y + ")");
			
			
			//Comenzamos a generar los hijos
			
			// Arriba
			Nodo hijoUp = new Nodo(actual);
			Vector2d newPosUp = new Vector2d(actual.getColumna(), actual.getFila() - 1);
			Vector2d orientacionUp = new Vector2d(0,-1);
			
			//System.out.println("Nueva posición arriba: (" + newPosUp.x + " , " + newPosUp.y + ")");
			
			if (hijoUp.getFila() - 1 >= 0 && esTransitable(newPosUp)) {
				//System.out.println("Arriba es transitable");
				if (hijoUp.getOrientacion().equals(orientacionUp)) {
					//System.out.println("Orientación igual a la que se va a meter");
					hijoUp.setPosicion(newPosUp);
					hijoUp.setDistancia(hijoUp.getDistancia() + 1);	
				} else {
					//System.out.println("Orientación distinta a la que era transitable.");
					hijoUp.setOrientacion(orientacionUp);
				}
				
				if (!cerrados.contains(hijoUp))
				{
					hijoUp.addAccion(ACTIONS.ACTION_UP);
					abiertos.add(hijoUp);
				} else 
					System.out.println("Ya estaba visitado");	
			} else {
				System.out.println("Arriba no era transitable");
			}	
			
			// Abajo
			Nodo hijoDown = new Nodo(actual);
			Vector2d newPosDown = new Vector2d(actual.getColumna(), actual.getFila() + 1);
			Vector2d orientacionDown = new Vector2d(0,1);
			
			System.out.println("Nueva posición abajo: (" + newPosDown.x + " , " + newPosDown.y + ")");
			
			if (hijoDown.getFila() + 1 <= (stateObs.getObservationGrid()[0].length - 1) && esTransitable(newPosDown))
			{
				System.out.println("Abajo es transitable");
				
				if (hijoDown.getOrientacion().equals(orientacionDown)) 
				{
					System.out.println("Orientación igual a la que se va a meter");
					hijoDown.setPosicion(newPosDown);
					hijoDown.setDistancia(hijoDown.getDistancia() + 1);
				} else 
				{
					System.out.println("Orientación distinta a la que se iba a meter.");
					hijoDown.setOrientacion(orientacionDown);
				}
				
				if (!cerrados.contains(hijoDown)) 
				{
					hijoDown.addAccion(ACTIONS.ACTION_DOWN);
					abiertos.add(hijoDown);
				} else
					System.out.println("Ya estaba visitado");
			} else
				System.out.println("Abajo no era transitable");
			
			// Izquierda
			Nodo hijoLeft = new Nodo(actual);
			Vector2d newPosLeft = new Vector2d(actual.getColumna() - 1, actual.getFila());
			Vector2d orientacionLeft = new Vector2d(-1,0);
			
			System.out.println("Nueva posición izquierda: (" + newPosLeft.x + " , " + newPosLeft.y + ")");
			
			if(hijoLeft.getColumna() - 1 >= 0 && esTransitable(newPosLeft))
            {
				System.out.println("Izquierda es transitable");
				
				if(hijoLeft.getOrientacion().equals(orientacionLeft))
				{
					System.out.println("Orientación igual a la que se va a meter");
                    hijoLeft.setPosicion(newPosLeft);
                    hijoLeft.setDistancia(hijoLeft.getDistancia() + 1);
                } else
                {
                    System.out.println("Orientación distinta a la que se iba a meter.");
                    hijoLeft.setOrientacion(orientacionLeft);
                }
				
				if(!cerrados.contains(hijoLeft))
                {
                    hijoLeft.addAccion(ACTIONS.ACTION_LEFT);
                    abiertos.add(hijoLeft);
                } else
                    System.out.println("Ya estaba visitado");
            } else
                System.out.println("Izquierda no era transitable");		
			
			// Derecha
			Nodo hijoRight = new Nodo(actual);
			Vector2d newPosRight = new Vector2d(actual.getColumna() + 1, actual.getFila());
			Vector2d orientacionRight = new Vector2d(1,0);
			
			System.out.println("Nueva posición derecha: (" + newPosRight.x + " , " + newPosRight.y + ")");
			
			if(hijoRight.getColumna() + 1 <= (stateObs.getObservationGrid().length - 1) && esTransitable(newPosRight))
            {
                System.out.println("Derecha es transitable");
                
                if(hijoRight.getOrientacion().equals(orientacionRight))
                {
                    System.out.println("Orientación igual a la que se va a meter");
                    hijoRight.setPosicion(newPosRight);
                    hijoRight.setDistancia(hijoRight.getDistancia() + 1);
                } else
                {
                    System.out.println("Orientación distinta a la que se iba a meter.");
                    hijoRight.setOrientacion(orientacionRight);
                }
                
                if(!cerrados.contains(hijoRight))
                {
                    hijoRight.addAccion(ACTIONS.ACTION_RIGHT);
                    abiertos.add(hijoRight);
                } else
                    System.out.println("Ya estaba visitado");
            } else
                System.out.println("Derecha no era transitable");
			
			if (!abiertos.isEmpty()) {
				System.out.println("Tras meter los hijos, tamaño de Abiertos: " + abiertos.size());
				Nodo nodoSiguiente = abiertos.peek();	
				
				while (cerrados.contains(nodoSiguiente) && !abiertos.isEmpty()) {
						abiertos.poll();
						nodoSiguiente = abiertos.peek();
				}
				
				if (!abiertos.isEmpty()) {
			        actual = nodoSiguiente;
			        System.out.println("El nodo en abiertos es: " + actual.getPosicion().x + " " + actual.getPosicion().y + " " + actual.getOrientacion().x + " " + actual.getOrientacion().y);
			    } else {
			        System.out.println("Todos los nodos en abiertos estaban ya en cerrados.");
			    }
			} else {
			    System.out.println("Abiertos estaba vacío");
			}
			
			contador++;
			
			
			//if(contador == 10) break;
		}
		

		System.out.println("Tamaño de Visitados: " + cerrados.size());
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