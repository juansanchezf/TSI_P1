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
	int g = Integer.MAX_VALUE;
	int h = 0;
	int f = 0;
	
	
	public Nodo(double x, double y) {
		posicion = new Vector2d(x,y);
		this.secuencia = new LinkedList<ACTIONS>();
		this.orientacion = null;
	}
	
	public Nodo(Vector2d posicion, Vector2d orientacion, int g) {
		this.posicion = new Vector2d(posicion.x, posicion.y);
		this.orientacion = new Vector2d(orientacion.x, orientacion.y);
		this.g = g;
		this.secuencia = new LinkedList<ACTIONS>();
	}
	
	public Nodo(Nodo otro) {
		this.posicion = new Vector2d(otro.posicion.x, otro.posicion.y);
		this.orientacion = new Vector2d(otro.orientacion.x, otro.orientacion.y);
		this.g = otro.g;
		this.h = otro.h;
		this.f = otro.f;
		this.secuencia = new LinkedList<ACTIONS>(otro.secuencia);
	}

	public Vector2d getPosicion() {
		return this.posicion;
	}
	
	public void setPosicion(Vector2d posicion) {
		this.posicion = posicion;
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
	
	public int getG() {
		return this.g;
	}
	
	public void setG(int g) {
		this.g = g;
	}
	
	public int getH() {
		return this.h;
	}
	
	public void setH(int h) {
		this.h = h;
	}
	
	public void actualizaF() {
		this.f = this.g + this.h;
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
		actualizaF();
		
		return Integer.compare(this.f, otro.f);
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    Nodo otro = (Nodo) obj;
	    
	    double thisX = Math.round(this.posicion.x);
	    double thisY = Math.round(this.posicion.y);
	    double otroX = Math.round(otro.posicion.x);
	    double otroY = Math.round(otro.posicion.y);
	    
	    double thisOrientX = Math.round(this.orientacion.x);
	    double thisOrientY = Math.round(this.orientacion.y);
	    double otroOrientX = Math.round(otro.orientacion.x);
	    double otroOrientY = Math.round(otro.orientacion.y);
	    
	    return (thisX == otroX) && (thisY == otroY) && (thisOrientX == otroOrientX) && (thisOrientY == otroOrientY);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(Math.round(posicion.x), Math.round(posicion.y), Math.round(orientacion.x), Math.round(orientacion.y));
	}

}
