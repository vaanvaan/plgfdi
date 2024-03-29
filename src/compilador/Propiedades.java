/**
 * 
 */
package compilador;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author DaNieLooP
 *
 */
public class Propiedades {
	private String t;
	private String valor;
	private int n;
	private Propiedades tbase;
	private Hashtable<String, CCampos> campos;
	private int nivel;
	private ArrayList<CParams> params;
	private int dir;
	private int tam;
	private int inicio;
	
	public Propiedades(){
		campos = new Hashtable<String, CCampos>(10);
		params = new ArrayList<CParams>();
	}

	
	public Propiedades(Propiedades props) {
		t = props.getT();
		valor = props.getValor();
		n = props.getN();
		tbase = props.getTbase();
		// FIXME
		// Creo que no copia los valores. Problema en declaraci�n m�ltiple de registros.
		campos = (Hashtable<String, CCampos>) props.getCampos().clone();
		nivel = props.getNivel();
		params = (ArrayList<CParams>) props.getParams().clone();
		dir = props.getDir();
		tam = props.getTam();
		inicio = props.getInicio();
		
	}


	/**
	 * @return the inicio
	 */
	public int getInicio() {
		return inicio;
	}


	/**
	 * @param inicio the inicio to set
	 */
	public void setInicio(int inicio) {
		this.inicio = inicio;
	}


	/**
	 * @return the t
	 */
	public String getT() {
		return t;
	}

	/**
	 * @param t the t to set
	 */
	public void setT(String t) {
		this.t = t;
	}

	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * @return the n
	 */
	public int getN() {
		return n;
	}

	/**
	 * @param n the n to set
	 */
	public void setN(int n) {
		this.n = n;
	}

	/**
	 * @return the tbase
	 */
	public Propiedades getTbase() {
		return tbase;
	}

	/**
	 * @param tbase the tbase to set
	 */
	public void setTbase(Propiedades tbase) {
		this.tbase = tbase;
	}

	/**
	 * @return the campos
	 */
	public Hashtable<String, CCampos> getCampos() {
		return campos;
	}

	/**
	 * @return the nivel
	 */
	public int getNivel() {
		return nivel;
	}

	/**
	 * @param nivel the nivel to set
	 */
	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	/**
	 * @return the params
	 */
	public ArrayList<CParams> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(ArrayList<CParams> params) {
		this.params = params;
	}

	/**
	 * @return the dir
	 */
	public int getDir() {
		return dir;
	}

	/**
	 * @param dir the dir to set
	 */
	public void setDir(int dir) {
		this.dir = dir;
	}

	/**
	 * @return the tam
	 */
	public int getTam() {
		return tam;
	}

	/**
	 * @param tam the tam to set
	 */
	public void setTam(int tam) {
		this.tam = tam;
	}
	
	public void addCampo(String id, Propiedades tipo, int desp){
		campos.put(id, new CCampos(id,tipo,desp));
	}
	
	public void addCampo(CCampos c){
		campos.put(c.getId(), c);
	}	
	public void addParam(String modo, Propiedades tipo, int dir){
		params.add(new CParams(modo,tipo,dir));
	}
	
	public void addParam(CParams p){
		params.add(p);
	}

	public void setCampos(Hashtable<String, CCampos> campos) {
		this.campos = campos;
	}
	
}
