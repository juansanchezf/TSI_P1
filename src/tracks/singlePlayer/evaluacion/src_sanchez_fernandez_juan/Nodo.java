package tracks.singlePlayer.evaluacion.src_SANCHEZ_FERNANDEZ_JUAN;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import ontology.Types.ACTIONS;

import tools.Vector2d;

public class Nodo implements Comparable<Nodo>{
	Vector2d posicion;
	Vector2d orientacion;
	Queue<ACTIONS> secuencia;
	int distancia = Integer.MAX_VALUE;
	
	
	public Nodo(double x, double y) {
		posicion = new Vector2d(x,y);
		this.secuencia = new LinkedList<ACTIONS>();
		this.orientacion = null;
	}
	
	public Nodo(Vector2d posicion, Vector2d orientacion, int distancia) {
		this.posicion = new Vector2d(posicion.x, posicion.y);
		this.orientacion = new Vector2d(orientacion.x, orientacion.y);
		this.distancia = distancia;
		this.secuencia = new LinkedList<ACTIONS>();
	}

	public Vector2d getPosicion() {
		return this.posicion;
	}
	
	public double getFila() {
		return posicion.y;
	}
	
	public void setFila(double y) {
		this.posicion.y = y;
	}
	
	public double getColumna() {
		return this.posicion.x;
	}
	
	public void setColumna(double x) {
		this.posicion.x = x;
	}
	
	public int getDistancia() {
		return this.distancia;
	}
	
	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}
	
	public Vector2d getOrientacion() {
		return this.orientacion;
	}
	
	public void setOrientacion(Vector2d orientacion) {
		this.orientacion = orientacion;
	}
	
	
	
	public void clearSecuencia() {
		this.secuencia.clear();
	}
	
	public void setSecuencia(Queue<ACTIONS> secuencia) {
		this.secuencia = secuencia;
	}
	
	public void addAccion(ACTIONS accion) {
		this.secuencia.add(accion);
	}
	
	Queue<ACTIONS> getSecuencia() {
		return this.secuencia;
	}
	
	
	@Override
	public int compareTo(Nodo otro) {
		return Integer.compare(this.distancia, otro.distancia);
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    Nodo node = (Nodo) obj;
	    // Comprueba igualdad tanto de posición como de orientación
	    return Double.compare(posicion.x, node.posicion.x) == 0 &&
	           Double.compare(posicion.y, node.posicion.y) == 0 &&
	           Double.compare(orientacion.x, node.orientacion.x) == 0 &&
	           Double.compare(orientacion.y, node.orientacion.y) == 0;
	}


	@Override
	public int hashCode() {
	    // Incluye orientación en el cálculo del hash
	    return Objects.hash(posicion.x, posicion.y, orientacion.x, orientacion.y);
	}

}
