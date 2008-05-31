/**
 * 
 */
package compilador;

import java.util.ArrayList;

/**
 * @author DaNieLooP
 *
 */
public class Propiedades {
	private String t;
	private String valor;
	private int n;
	private Propiedades tbase;
	private ArrayList<CCampos> campos;
	private int nivel;
	private ArrayList<CParams> params;
	private int dir;
	private int tam;
	private int inicio;
	
	public Propiedades(){
		campos = new ArrayList<CCampos>();
		params = new ArrayList<CParams>();
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
	public ArrayList<CCampos> getCampos() {
		return campos;
	}

	/**
	 * @param campos the campos to set
	 */
	public void setCampos(ArrayList<CCampos> campos) {
		this.campos = campos;
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
		campos.add(new CCampos(id,tipo,desp));
	}
	
	public void addCampo(CCampos c){
		campos.add(c);
	}
	
	public void addParam(String modo, Propiedades tipo, int dir){
		params.add(new CParams(modo,tipo,dir));
	}
	
	public void addParam(CParams p){
		params.add(p);
	}
	
}
