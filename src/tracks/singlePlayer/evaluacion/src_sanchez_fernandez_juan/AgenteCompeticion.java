package tracks.singlePlayer.evaluacion.src_SANCHEZ_FERNANDEZ_JUAN;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Pair;
import tools.Vector2d;

public class AgenteCompeticion extends AbstractPlayer{
	boolean intransitables[][];
	Vector2d fescala;
	Stack<ACTIONS> camino;
	Vector2d portal;
	boolean planificado;
	
	ArrayList<Vector2d> posGemas;
	int gemasRecogidas;
	boolean gemaEncontrada;
	boolean planFinal;
	/**
	 * @brief Constructor del agente
	 * @param stateObs
	 * @param elapsedTimer
	 */
	public AgenteCompeticion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
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
		ArrayList<Observation>[] gemas = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
		
		
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
		
		posGemas = new ArrayList<Vector2d>();
		for(int i = 0; i < gemas.length; i++)
		{
			for (int j = 0; j < gemas[i].size(); j++) 
			{
				Vector2d posG = gemas[i].get(j).position;
				posG.x = Math.floor(posG.x / fescala.x);
				posG.y = Math.floor(posG.y / fescala.y);
				posGemas.add(posG);
				
//				System.out.println("Gema en: " + gemas[i].get(j).position.x + " " + gemas[i].get(j).position.y);
			}
		}
		
				
		// Obtenemos las posiciones de los portales
		ArrayList<Observation>[] objetivos = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
		
		// Obtenemos la posicion del portal más cercano
		portal = objetivos[0].get(0).position;
		portal.x = Math.floor(portal.x / fescala.x);
		portal.y = Math.floor(portal.y / fescala.y);
		
		
		planificado = false;
		gemasRecogidas = 0;
		gemaEncontrada = false;
		planFinal = false;
	}
	
	private boolean cercaNPC(StateObservation stateObs) 
	{
		Vector2d posAvatar = new Vector2d(stateObs.getAvatarPosition().x /fescala.x ,	stateObs.getAvatarPosition().y/fescala.y);
		
		
		ArrayList<Observation>[] npcs = stateObs.getNPCPositions(posAvatar);
		
		for (int i = 0; i < npcs.length; i++) {
			for (int j = 0; j < npcs[i].size(); j++) {
				Vector2d posNPC = npcs[i].get(j).position;
				posNPC.x = Math.floor(posNPC.x / fescala.x);
				posNPC.y = Math.floor(posNPC.y / fescala.y);

				if (distManhattan(posAvatar, posNPC) < 4) {
					return true;
				}
			}
		}
		
		
		return false;
		
	}
	
	private ACTIONS alejarNPC(StateObservation stateObs) {
		Vector2d posAvatar = new Vector2d(stateObs.getAvatarPosition().x / fescala.x,
				stateObs.getAvatarPosition().y / fescala.y);

		ArrayList<Observation>[] npcs = stateObs.getNPCPositions(posAvatar);

		for (int i = 0; i < npcs.length; i++) {
			for (int j = 0; j < npcs[i].size(); j++) {
				Vector2d posNPC = npcs[i].get(j).position;
				posNPC.x = Math.floor(posNPC.x / fescala.x);
				posNPC.y = Math.floor(posNPC.y / fescala.y);

				/* La x son las columnas y la y las filas 
				 * entonces si la x del npc es mayor a la mia significa que está a la derecha
				 * por lo que me muevo a la izquierda y si no alreves.
				 * En el caso de las filas si la y del NPC es mayor significa que está
				 * ABAJO por lo que me muevo arriba.
				 */
				if (posAvatar.x < posNPC.x) 
				{
					return ACTIONS.ACTION_LEFT;
				} 
				else if (posAvatar.x > posNPC.x) 
				{
					return ACTIONS.ACTION_RIGHT;
				} else if (posAvatar.y < posNPC.y) 
				{
					return ACTIONS.ACTION_UP;
				} else if (posAvatar.y > posNPC.y) 
				{
					return ACTIONS.ACTION_DOWN;
				}
				
			}
		}

		return ACTIONS.ACTION_NIL;
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
	private boolean AStar(StateObservation stateObs, Vector2d objetivo, ElapsedCpuTimer elapsedTimer)
	{
		// Inicializamos la cola de prioridad de nodos abiertos y el conjunto de nodos visitados
		PriorityQueue<Nodo> abiertos = new PriorityQueue<Nodo>();
		HashSet<Nodo> cerrados = new HashSet<Nodo>();
		
		// Obtenemos la posicion del avatar y creamos el nodo inicial
		Vector2d posAvatar = new Vector2d(stateObs.getAvatarPosition().x /fescala.x ,	stateObs.getAvatarPosition().y/fescala.y);
		
		//Se inicializa el nodo a la posición y orientación del avatar
		Nodo inicial = new Nodo(posAvatar, stateObs.getAvatarOrientation());
		inicial.setH(distManhattan(posAvatar, objetivo));
		
		abiertos.add(inicial);
		
		Nodo actual = null;
		
		while(!abiertos.isEmpty()) {
			actual = abiertos.poll();
			
			if (actual.getPosicion().equals(objetivo)) {
				camino = reconstruirCamino(actual);
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
					hijoUp.setH(distManhattan(hijoUp.getPosicion(), objetivo));
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
					hijoDown.setH(distManhattan(hijoDown.getPosicion(), objetivo));
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
					hijoLeft.setH(distManhattan(hijoLeft.getPosicion(), objetivo));
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
					hijoRight.setH(distManhattan(hijoRight.getPosicion(), objetivo));
					hijoRight.addAccion(ACTIONS.ACTION_RIGHT);
					abiertos.add(hijoRight);
                }
            }
		}
		
		return false;
	}

	
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		while(stateObs.getGameScore() < 10) 
		{
			if(!gemaEncontrada) 
			{
				Vector2d sigGema = posGemas.get(0);
				gemaEncontrada = AStar(stateObs, sigGema, elapsedTimer);
				System.out.println("Gema encontrada en: " + sigGema.x + " " + sigGema.y);
			}
			
			if(camino.isEmpty()) {
				posGemas.remove(0);
                gemaEncontrada = false;
            }
			else if(!cercaNPC(stateObs))
			{
				while (!camino.isEmpty() && ! (camino == null)) 
				{
					return camino.pop();
				}
			}
			else {
				return alejarNPC(stateObs);
			}
		}
		
		if(!planFinal) 
		{
			planFinal = AStar(stateObs, portal, elapsedTimer);
			
			while (!camino.isEmpty()) {
				return camino.pop();
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		while(posGemas.size() > 0) 
//		{
//			if(!planificado)
//			{
//				Vector2d gemaPos = posGemas.get(0);
//                boolean hayCamino = AStar(stateObs, gemaPos, elapsedTimer);
//                
//                if (hayCamino)
//                {
//                    System.out.println("Gema encontrada en: " + gemaPos.x + " " + gemaPos.y);
//                    planificado = true;
//                    
//                    //reinicializamos la posición de la gema para ir a la siguiente
//                    posGemas.remove(0);
//                }
//                
//			}
//			
//			boolean hayPeligro = cercaNPC(stateObs);
//			
//			if (camino != null && !camino.isEmpty() && !hayPeligro) 
//            {
//                return camino.pop();
//            }
//            else if (hayPeligro) 
//            {
//            	System.out.println("NPC cerca");
//            	planificado = false;
//                return alejarNPC(stateObs);
//            }
//            else 
//            {
//                planificado = false;
//            }
//
//		}
//		
//		planificado = false;
//		
//		System.out.println("Gemas recogidas");
//		double recogidas = stateObs.getGameScore();
//		System.out.println("RECOGIDAS: " + recogidas);
//		
//		if(!planificado) 
//		{
//			boolean hayCamino = AStar(stateObs, portal, elapsedTimer);
//			if (hayCamino) {
//				System.out.println("Portal encontrado en: " + portal.x + " " + portal.y);
//				planificado = true;
//			}
//			
//			if (camino != null && !camino.isEmpty()) {
//				return camino.pop();
//			} else {
//				planificado = false;
//			}
//		}
		
		return ACTIONS.ACTION_NIL;
	}
}
