package tracks.singlePlayer.evaluacion.src_SANCHEZ_FERNANDEZ_JUAN;
import java.util.Objects;
import ontology.Types.ACTIONS;
import tools.Vector2d;

public class NodoRTA implements Comparable<NodoRTA>{
	static int contadorNodos = 0;
	Vector2d posicion;
	Vector2d orientacion;
	int idNodo;
	
	ACTIONS accion;
	
	int g = Integer.MAX_VALUE;
	int h = 0;
	
	//Constructor para nodo inicial
	public NodoRTA(Vector2d posicion, Vector2d orientacion) {
		this.posicion = new Vector2d(posicion.x, posicion.y);
		this.orientacion = new Vector2d(orientacion.x, orientacion.y);
		this.g = 0;
		this.accion = null;
		this.idNodo = contadorNodos++;
	}
	
	//Constructor para nodos sucesores con misma orientacion y posicion
	public NodoRTA(NodoRTA otro) {
		this.posicion = new Vector2d(otro.posicion.x, otro.posicion.y);
		this.orientacion = new Vector2d(otro.orientacion.x, otro.orientacion.y);
		this.accion = otro.accion;
		this.g = otro.g;
		this.h = otro.h;
		this.idNodo = contadorNodos++;
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
	
	public int f() {
		return this.g + this.h;
	}
	
	public Vector2d getOrientacion() {
		return this.orientacion;
	}
	
	public void setOrientacion(Vector2d orientacion) {
		this.orientacion = orientacion;
	}
	
	public void addAccion(ACTIONS accion) {
		this.accion = accion;
	}
	
	public ACTIONS getAccion() {
		return this.accion;
	}

	@Override
	public int compareTo(NodoRTA otro) {		
		int comparaF = Integer.compare(this.f(), otro.f());
		if (comparaF != 0) return comparaF;
		int comparaG = Integer.compare(this.g, otro.g);
		if (comparaG != 0) return comparaG;
		return Integer.compare(this.idNodo, otro.idNodo);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		
		NodoRTA otro = (NodoRTA) obj;
		double thisX = Math.round(this.posicion.x);
		double thisY = Math.round(this.posicion.y);
		double otroX = Math.round(otro.posicion.x);
		double otroY = Math.round(otro.posicion.y);
		
		
		return (thisX == otroX) && (thisY == otroY);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Math.round(posicion.x), Math.round(posicion.y));
	}

}
