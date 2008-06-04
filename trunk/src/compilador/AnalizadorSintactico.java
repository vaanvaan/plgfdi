/**
 * 
 */
package compilador;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.io.*;

/**
 * Clase que se encarga de ver si un programa es sintacticamente correcto dentro de nuestro subconjunto de Pascal, y 
 * genera las instrucciones necesarias para ejecutarlo despues en la maquina P.
 * 
 * @author DaNieLooP
 *
 */
public class AnalizadorSintactico {
	/**
	 * Instancia del analizador lexico utilizada en el analizador sintactico.
	 */
	private AnalizadorLexico anaLex;
	/**
	 * Tabla de dispersion que hace las veces de tabla de simbolos.
	 */
	private PilaTS pilaTablaSim;
	/**
	 * Vector que almacena el conjunto de instrucciones emitidas hasta el momento.
	 */
	private Vector<String> instrucciones;
	/**
	 * String con el path del archivo que se tiene que escribir.
	 */
	private String pathDestino;
	
	private int n;
	
	private int dir;
	
	private  int longApilaRet;
	
	private int longPrologo;
	
	private int longEpilogo;
	
	private int longInicioPaso;
	
	private int longFinPaso;
	
	private int longdireccionParFormal;
	
	private int longPasoParametro;
	
	private int longInicio;
	
	private int etq;
	
	private boolean parh;
	
	/**Constructor del analizador sintactico, donde se crea el analizador lexico y la tabla de simbolos. 
	 * 
	 * @param path Ruta del archivo que se va a analizar.
	 */
	public AnalizadorSintactico(String path,String path2){
		this.longPrologo = 13;
		this.longdireccionParFormal = 2;
		this.longPasoParametro = 1;
		this.longEpilogo = 13;
		this.longInicio = 4;
		this.longInicioPaso = 3;
		this.longFinPaso = 1;
		this.longApilaRet = 5;
		anaLex = new AnalizadorLexico(path);
		this.pilaTablaSim = new PilaTS();
		instrucciones = new Vector<String>(0,1);
		pathDestino = path2;
	}
	
	
	public void apilaRet(int ret){
		emite("apila-dir 0");
		emite("apila 1");
		emite("suma");
		emite("apila "+ret);
		emite("desapila-ind");
	}
	
	public void accesoVar(entradaTS infoID){
		emite("apila-dir "+(1+infoID.getProps().getNivel()));
		emite("apila "+(infoID.getProps().getDir()));
		emite("suma");
		if(infoID.getClase().compareTo("pvar")==0){
			emite("apila-ind");
		}
	}
	
	public int longAccesoVar(entradaTS infoID){
		if(infoID.getClase().compareTo("pvar")==0){
			return 4;
		}else{
			return 3;
		}
	}
	
	public void inicio(int numNiveles,int tamDatos){
		emite("apila "+(numNiveles+1));
		emite("desapila-dir 1");
		emite("apila "+(1+numNiveles+tamDatos));
		emite("desapila-dir 0");
	}
	
	public void prologo(int nivel,int tamlocales){
		emite("apila-dir 0");
		emite("apila 2");
		emite("suma");
		emite("apila-dir "+(1+nivel));
		emite("desapila-ind");
		emite("apila-dir 0");
		emite("apila 3");
		emite("suma");
		emite("desapila-dir "+(1+nivel));
		emite("apila-dir(0)");
		emite("apila "+(tamlocales+2));
		emite("suma");
		emite("desapila-dir 0");
	}
	
	public void epilogo(int nivel){
		emite("apila-dir "+(1+nivel));
		emite("apila 2");
		emite("resta");
		emite("apila-ind");
		emite("apila-dir "+(1+nivel));
		emite("apila 3");
		emite("resta");
		emite("copia");
		emite("desapila-dir 0");
		emite("apila 2");
		emite("suma");
		emite("apila-ind");
		emite("desapila-dir "+(1+nivel));
	}
	
	public void direccionParFormal(CParams pformal){
		emite("apila "+pformal.getDir());
		emite("suma");
	}
	
	public void pasoParametro(String modoReal, CParams pformal){
		if(pformal.getModo().compareTo("val")==0 || modoReal.compareTo("var")==0){
			emite("mueve "+pformal.getTipo().getTam());
		}else{
			emite("desapila-ind");
		}
	}
	
	public void inicioPaso(){
		emite("apila-dir 0");
		emite("apila 3");
		emite("suma");
	}
	
	public void finPaso(){
		emite("desapila");
	}
	
	/**
	 * Sustituye la instrucción con etiqueta flag, por la correcta.
	 * @param tipo tipo de instrucción a sustituir. 0:inicio, 1:ir-a, 2:ir-f, 3:ir-v
	 * @param flag dirección de la instrucción
	 */
	public void parchea(int tipo,int flag){
		int etqAux = etq;
		etq = flag;
		switch (tipo){
		case 0:
			emiteP("apila "+(n+1)+")");
			etq++;
			emiteP("desapila-dir 1)");
			etq++;
			emiteP("apila "+(1+n+dir));
			etq++;
			emiteP("desapila-dir 0");
			break;
		case 1:
			emiteP("ir-a "+etq); 
			break;
		case 2:
			emiteP("ir-f "+etq);
			break;
		case 3:
			emiteP("ir-v "+etq);
			break;
		}
		etq = etqAux;
	}
	
	/**Método que reconoce un programa completo de Pascal de nuestro subconjunto.
	 * 
	 * @throws Exception Se recoge el error de cualquiera de las funciones que componen el programa.
	 */
	public void programa() throws Exception{
		try{
			this.compara("program");
			this.id(); 
			this.compara(";");
			this.pilaTablaSim.creaTS();
			this.dir = 0;
			this.n = 0;
			this.etq = 0;
			inicio(n,dir);
			int flag = etq;
			emite("ir-a "+etq);
			etq = longInicio+1;
			this.bloqueDecls();
			this.parchea(0, 0);
			this.parchea(1, flag);
			this.proposicion_compuesta(); 
			this.compara(".");
			this.anaLex.scanner();
			this.emite(this.anaLex.getToken());
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		if(!Global.getError()){
			try
	        {
	            FileWriter fichero = new FileWriter(pathDestino);
	            PrintWriter pw = new PrintWriter(fichero);
	            for (int i = 0; i < instrucciones.size(); i++)
	                pw.println(instrucciones.elementAt(i));
	            pw.close();
	            System.out.println("Compilado satisfactoriamente.");
	        } catch (Exception e)
	        {
	        	System.out.println(e.getMessage());
	        }
		}
		
	}
	
	private void bloqueDecls()throws Exception{
		this.declaraciones();
		this.dec_Procs();
	}
	
	/**
	 * Método que reconoce la parte de las declaraciones de un programa escrito en Pascal.
	 * 
	 * @throws Exception Se recoge cualquier error lanzado dentro de las declaraciones.
	 */
	private void declaraciones() throws Exception{
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0||this.anaLex.getLex().compareTo("const")==0){  
			this.declaracion();
			this.declaracionesR();
		}
	}
	
	/**
	 * Método que reconoce una parte de las declaraciones de un programa escrito en Pascal. Es distinto de declaraciones()
	 * para evitar la recursion a izquierdas.
	 * 
	 * @throws Exception Se recoge cualquier error lanzado dentro de esta parte de las declaraciones.
	 */
	private void declaracionesR() throws Exception{
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0||this.anaLex.getLex().compareTo("const")==0){  
			this.declaracion();
			this.declaracionesR();
		}
	}
	
	/**Método que reconoce una declaracion, ya sea constante o variable.
	 * 
	 * @throws Exception Recoge cualquier error generado dentro de la declaracion.
	 */
	private void declaracion() throws Exception{
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0){
			this.variable(); 
		}else if(this.anaLex.getLex().compareTo("const")==0){
			Tupla t = new Tupla(2);
			t = this.constante();
			this.pilaTablaSim.añadeID(n,(String)t.getnTupla(0), "const",(Propiedades)t.getnTupla(1));
			dir = dir + ((Propiedades)t.getnTupla(1)).getTam();
		}
	}
	
	/**Método que se encarga de reconocer una constante.
	 * 
	 * @return Se devuelve una tupla que se ira pasando de un metodo a otro y al final se añadira a la tabla de simbolos.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de la constante.
	 */
	private Tupla constante() throws Exception{
		Tupla t = new Tupla(2);
		this.compara("const");
		String lex0 = this.id();
		if (this.pilaTablaSim.getTSnivel(n).existeID(lex0)) 
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');		
		t.setnTupla(0, lex0);
		this.compara("=");
		Tupla rValor = new Tupla(2);
		rValor = this.valor();
		this.compara(";");
		Propiedades p = new Propiedades();
		p.setT((String)rValor.getnTupla(0));
		p.setValor(((String)rValor.getnTupla(1)));
		p.setTam(1);
		p.setDir(dir);
		p.setNivel(n);
		t.setnTupla(1, p);
		return t;
	}
	
	/**Método que se encarga de reconocer una variable.
	 * 
	 * @return Se devuelve una tupla que se ira pasando de un metodo a otro y al final se añadira a la tabla de simbolos.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de la variable.
	 */
	private void variable() throws Exception{
		this.compara("var");
		int posAux = Global.getGlobalPos();
		int colAux = Global.getColumna();
		//TODO CACAO MARAVILLAO
		/*
		 *  1º comprobamos sintaxis correcta (var Lista_ID : Tipo ;)Necesitamos parámetros sintetizados de Tipo para lanzar Lista_ID
			Y ASI COMO LECHES VERIFICAMOS LA SINTAXIS?????? el : y ;
			2º ejecutamos Tipo()
			3º ejecutamos Lista_ID()
			Tipo(out props) 
			Lista_ID(in props)
			????this.compara(";");
		 */
		this.anaLex.gotoUntil(":");
		Propiedades p = this.tipo();
		int posAux2 = Global.getGlobalPos();
		int colAux2 = Global.getColumna();
		Global.setGlobalPos(posAux);
		Global.setColumna(colAux);
		this.lista_id(p);
		Global.setGlobalPos(posAux2);
		Global.setColumna(colAux2);
		this.compara(";");
		
	}
	
	/**Método que reconoce una lista de identificadores.
	 * 
	 * 
	 * @return se devuelve una lista con todos los lexemas de los identificadores.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de una lista de identificadores.
	 */
	private void lista_id(Propiedades props) throws Exception{
		String lex = this.id();
		if (this.pilaTablaSim.getTSnivel(n).existeID(lex)) 
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		props.setDir(dir);
		props.setNivel(n);
		this.pilaTablaSim.añadeIDcima(lex, "var", props);
		dir = dir + props.getTam();
		this.lista_idR(props);
	}
	
	
	/**Método que reconoce parte de una secuencia de identificadores. Se creo para evitar la recursion a izquierdas en
	 * el metodo lista_id
	 * 
	 * @param listaIDh0 Lista que se pasa por parametro para agregar mas identificadores.
	 * @return Se devuelve una lista con los lexemas de los identificadores.
	 * @throws Exception Se recoge cualquier tipo de error ocurrido dentro de esta parte de la lista de identificadores.
	 */
	private void lista_idR(Propiedades props) throws Exception{
		this.anaLex.predice();
		String aux = this.anaLex.getToken();
		if(aux.compareTo("coma")==0){
			this.compara(",");
			String lex = this.id();
			if (this.pilaTablaSim.getTSnivel(n).existeID(lex)) 
				throw new Exception("Error sintaxis: ID ya existente"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			props.setDir(dir);
			props.setNivel(n);
			this.pilaTablaSim.añadeID(n,lex, "var", props);
			dir = dir + props.getTam();
			this.lista_idR(props);
		}
	}
	
		
	/**Método que sirve para reconocer el valor de una constante.
	 * 
	 * @param tipo Se pasa por parametro el tipo para ver si concuerda con el valor.
	 * @return Se devuelve un String con el valor de la constante.
	 * @throws Exception Se genera una excepcion si el tipo y el valor de una constante no concuerdan.
	 */
	private Tupla valor() throws Exception{
		Tupla t = new Tupla(2);
		anaLex.scanner();
		String token = anaLex.getToken();
		String lex = anaLex.getLex();
		if(token.compareTo("num")==0){
			t.setnTupla(0,"integer");
			t.setnTupla(1, lex);
		}else if(lex.compareTo("true")==0 || lex.compareTo("false")==0){
			t.setnTupla(0,"boolean");
			t.setnTupla(1, lex);
		}else if(token.compareTo("numReal")==0){
			t.setnTupla(0,"real");
			t.setnTupla(1, lex);
		}else if(token.compareTo("char")==0){
			t.setnTupla(0,"char");
			t.setnTupla(1, lex);;
		}else{
			//throw new Exception("Error sintaxis: valor no corresponde con tipo.");
			Global.setErrorMsg("Violación restricciones. Tipo no encontrado.");
		}
		return t;
	}
	
	/**Metodo que diferencia entre tipos construidos y tipos estandar
	 * 
	 * @return Unas propiedades con todo lo referente al tipo.
	 * @throws Exception
	 */
	private Propiedades tipo() throws Exception{
		this.anaLex.predice();
		String lex = this.anaLex.getLex();
		if(lex.compareTo("boolean")==0){
			return this.tipo_estandar();
		}else if(lex.compareTo("integer")==0){
			return this.tipo_estandar();
		}else if(lex.compareTo("real")==0){
			return this.tipo_estandar();
		}else if(lex.compareTo("char")==0){
			return this.tipo_estandar();
		}else{
			return this.tipo_construido();
		}
	}
	
	/**Reconoce un tipo estandar, ya sea boolean,integer,real o char.
	 * 
	 * @return Devuelve unas propiedades con el tipo y el tamaño.
	 * @throws Exception
	 */
	private Propiedades tipo_estandar() throws Exception{
		anaLex.scanner();
		Propiedades p = new Propiedades();
		String lex = anaLex.getLex();
		if(lex.compareTo("boolean")==0){
			p.setT("boolean");
			p.setTam(1);
		}else if(lex.compareTo("integer")==0){
			p.setT("integer");
			p.setTam(1);
		}else if(lex.compareTo("real")==0){
			p.setT("real");
			p.setTam(1);
		}else if(lex.compareTo("char")==0){
			p.setT("char");
			p.setTam(1);
		}else{
			//throw new Exception("Error sintaxis: valor no corresponde con tipo.");
			Global.setErrorMsg("Violación restricciones. Tipo no encontrado.");
		}
		return p;
	}
	
	private Propiedades tipo_construido() throws Exception{
		anaLex.predice();
		String lex = this.anaLex.getLex();
		Propiedades p = new Propiedades();
		if(lex.compareTo("array")==0){
			this.compara("array");
			this.compara("[");
			int longt = this.subrango();
			this.compara("]");
			this.compara("of");
			Propiedades p1 = this.tipo();
			p.setT("array");
			p.setN(longt);
			p.setTbase(p1);
			p.setTam(longt*p1.getTam());
		}else if(lex.compareTo("^")==0){
			this.compara("^");
			Propiedades p2 = this.tipo();
			p.setT("pointer");
			p.setTbase(p2);
			p.setTam(1);
		}else if(lex.compareTo("record")==0){
			this.compara("record");
			Tupla t = this.campos();
			this.compara("end");
			p.setT("reg");
			p.setCampos((Hashtable<String, CCampos>) t.getnTupla(0));
			p.setTam(Integer.parseInt(t.getnTupla(1).toString()));
		}else{
			Global.setErrorMsg("Violación restricciones. Tipo incorrecto");
			// throw new Exception("Error sintaxis: Tipo incorrecto.");
		}
		return p;
	}
	
	private Tupla campos()throws Exception{
		int desp = 0;
		Tupla t = this.campo(desp);
		Hashtable<String, CCampos> campos0 = new Hashtable<String, CCampos>(10);
		campos0.put(((CCampos) t.getnTupla(0)).getId(), (CCampos) t.getnTupla(0));
		int desp1 = desp + Integer.parseInt(t.getnTupla(1).toString());
		Tupla t2 = this.camposR(campos0,desp1);
		int tam2 = Integer.parseInt(t.getnTupla(1).toString()) + Integer.parseInt(t2.getnTupla(1).toString());
		Tupla tf = new Tupla(2);
		tf.setnTupla(0, t2.getnTupla(0));
		tf.setnTupla(1, tam2);
		return tf;
	}
	
	private Tupla camposR(Hashtable<String, CCampos> campos0, int desp0)throws Exception{
		anaLex.predice();
		String lex = this.anaLex.getLex();
		Tupla tf = new Tupla(2);
		if(lex.compareTo("end")==0){
			tf.setnTupla(0, campos0);
			tf.setnTupla(1, desp0);
		}else{
			Tupla t = this.campo(desp0);
			campos0.put(((CCampos) t.getnTupla(0)).getId(), (CCampos) t.getnTupla(0));
			int desp1 = desp0 + Integer.parseInt(t.getnTupla(1).toString());
			Tupla t2 = this.camposR(campos0, desp1);
			int tam2 = Integer.parseInt(t.getnTupla(1).toString()) + Integer.parseInt(t2.getnTupla(1).toString());
			tf.setnTupla(0, t2.getnTupla(0));
			tf.setnTupla(1, tam2);
		}
		return tf;
	}
	
	private Tupla campo(int desp)throws Exception{
		String lex = this.id();
		Tupla t = new Tupla(2); 
		if(this.pilaTablaSim.getTSnivel(n).existeID(lex)){
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}else{
			this.compara(":");
			Propiedades props = this.tipo();
			CCampos c = new CCampos(lex,props,desp);
			t.setnTupla(0, c);
			t.setnTupla(1, props.getTam());
			this.compara(";");
		}
		return t;
	}
	
	private int subrango()throws Exception{
		anaLex.scanner();
		String token = anaLex.getToken();
		String lex = anaLex.getLex();
		int longt = -1;
		if(token.compareTo("num")==0 && Integer.parseInt(lex)>=0){
			this.compara(".");
			this.compara(".");
			anaLex.scanner();
			String token2 = anaLex.getToken();
			String lex2 = anaLex.getLex();
			if(token2.compareTo("num")==0 && Integer.parseInt(lex2)>=0){
				if(Integer.parseInt(lex)>Integer.parseInt(lex2)){
					longt = Integer.parseInt(lex2) - Integer.parseInt(lex) + 1;
				}else{
					throw new Exception("Subrango del array invalido"
							+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
				}
			}else{
				throw new Exception("Tipo incorrecto"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
		}else{
			throw new Exception("Tipo incorrecto"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}
		return longt;
	}
	
	private void dec_Procs()throws Exception{
		anaLex.predice();
		String lex = this.anaLex.getLex();
		if(lex.compareTo("procedure")==0){
			this.dec_Proc();
			this.dec_Procs();
		}
	}
	
	private void dec_Proc()throws Exception{
		this.compara("procedure");
		String lex = this.id();
		if(this.pilaTablaSim.getTSnivel(n).existeID(lex)){
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}
		int dirAnt = dir;
		dir = 0;
		ArrayList<CParams> params = this.parametros();
		this.compara(";");
		int inicio = etq;
		Propiedades props = new Propiedades();
		props.setT("proc");
		props.setParams(params);
		props.setNivel(n);
		props.setInicio(inicio);
		this.pilaTablaSim.añadeID(n,lex, "proc", props);
		this.pilaTablaSim.creaTS();
		/*
		Propiedades props2 = new Propiedades();
		props2.setT("proc");
		props2.setParams(params);
		props2.setInicio(inicio);
		*/
		props.setNivel(n+1);
		this.pilaTablaSim.añadeID(n,lex, "proc", props);
		n = n + 1;
		this.bloqueDecls();
		prologo(n,dir);
		etq = etq + longPrologo;
		this.proposicion_compuesta();
		epilogo(n);
		etq = etq + longEpilogo+1;
		emite("ir-ind");
		dir = dirAnt;
		//desapila TS (solo -1 en la cima supongo)
		n = n - 1;
	}
	
	private ArrayList<CParams> parametros()throws Exception{
		ArrayList<CParams> params = null;
		anaLex.predice();
		String lex = this.anaLex.getLex();
		if(lex.compareTo("(")==0){
			this.compara("(");
			params = lista_Params();
			this.compara(")");
		} else params = new ArrayList<CParams>();
		return params;
	}
	
	private ArrayList<CParams> lista_Params()throws Exception{
		Tupla t = param();
		this.pilaTablaSim.añadeID(n,(String)t.getnTupla(0), (String)t.getnTupla(1),(Propiedades)t.getnTupla(2));
		dir = dir + Integer.parseInt(t.getnTupla(3).toString());
		ArrayList<CParams> params0 = new ArrayList<CParams>();
		params0.add((CParams)t.getnTupla(3));
		ArrayList<CParams> params1 = this.lista_ParamsR(params0);
		return params1;
	}
	
	private ArrayList<CParams> lista_ParamsR(ArrayList<CParams> params0)throws Exception{
		anaLex.predice();
		String lex = this.anaLex.getLex();
		if(lex.compareTo(",")==0){
			this.compara(",");
			Tupla t = param();
			params0.add((CParams)t.getnTupla(4));
			this.pilaTablaSim.añadeID(n,(String)t.getnTupla(0), (String)t.getnTupla(1),(Propiedades)t.getnTupla(2));
			dir = dir + Integer.parseInt(t.getnTupla(3).toString());
			ArrayList<CParams> params2 = this.lista_ParamsR(params0);
			return params2;
		}else{
			return params0;
		}
	}
	
	private Tupla param()throws Exception{
		anaLex.predice();
		String lexa = this.anaLex.getLex();
		Tupla t = new Tupla(5);
		if(lexa.compareTo("var")==0){
			this.compara("var");
			String clase = "pvar";
			String lex = this.id();
			if(this.pilaTablaSim.getTSnivel(n).existeID(lex)){
				throw new Exception("Error sintaxis: ID ya existente"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara(":");
			Propiedades props = this.tipo();
			int tam = 1;
			CParams param = new CParams("variable",props,dir);
			props.setNivel(n);
			props.setDir(dir);
			t.setnTupla(0, lex);
			t.setnTupla(1, clase);
			t.setnTupla(2, props);
			t.setnTupla(3, tam);
			t.setnTupla(4, param);
		}else{
			String lex = this.id();
			if(this.pilaTablaSim.getTSnivel(n).existeID(lex)){
				throw new Exception("Error sintaxis: ID ya existente"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara(":");
			Propiedades props = this.tipo();
			String clase = "var";
			int tam = props.getTam();
			CParams param = new CParams("valor",props,dir);
			props.setNivel(n);
			props.setDir(dir);
			t.setnTupla(0, lex);
			t.setnTupla(1, clase);
			t.setnTupla(2, props);
			t.setnTupla(3, tam);
			t.setnTupla(4, param);
		}
		return t;
	}

// FIN PARTE DECLARACIONES ///////////////////////////////////
	
	/**Método que reconoce la parte de las instrucciones de un programa escrito en Pascal.
	 * 
	 * @throws Exception Se recoge cualquier tipo de error generado dentro de esta parte del programa.
	 */
	private void proposicion_compuesta() throws Exception{
		this.compara("begin");
		this.proposiciones_optativas();
		this.compara("end"); 
	}
	
	/**Método que reconoce si en la parte de las instrucciones hay instrucciones o no.
	 * 
	 * @throws Exception Se recoge cualquier tipo de error que haya ocurrido en la parte de las instrucciones del programa.
	 */
	private void proposiciones_optativas() throws Exception{
		this.anaLex.predice();
		String lex = this.anaLex.getLex();
		if (lex.compareTo("end")!=0){
			// Si no es programa vacío, seguimos.
			this.lista_proposiciones();
		}
	}
	
	/**Método que reconoce todas las proposiciones que conforman las instrucciones del programa.
	 * 
	 * @throws Exception Se recoge cualquier error generado dentro de cualquier proposicion.
	 */
	private void lista_proposiciones() throws Exception{
		this.proposicion();
		this.lista_proposicionesR();
	}
	
	/**Método que reconoce parte de las proposiciones que conforman las instrucciones del programa.
	 * Se creo para evitar la recursion a izquierdas en lista_proposiciones()
	 * 
	 * @throws Exception Se recoge cualquier error generado dentro de cualquier proposicion.
	 */
	private void lista_proposicionesR() throws Exception{
		this.anaLex.predice();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo(";")==0){
			this.compara(";");
			this.proposicion();
			this.lista_proposicionesR();
		}
	}
	
	private void proposicion() throws Exception{
		this.anaLex.predice();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("if")==0){
			this.compara("if");
			this.parh = false;
			Tupla t = this.expresion();
			if(t.getnTupla(0).toString()!="boolean"){
				throw new Exception("Proposicion no válida."
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara("then");
			int flag1 = etq;
			emite("ir-f "+etq);
			etq = etq+1;
			this.proposicion();
			int flag2 = etq;
			emite("ir-a "+etq);
			etq = etq+1;
			parchea(2,flag1);
			this.pElse();
			parchea(1,flag2);
		}else if(lexTipo.compareTo("while")==0){
			this.compara("while");
			parh = false;
			int etqb = etq;
			Tupla t = this.expresion();
			if(t.getnTupla(0).toString()!="boolean"){
				throw new Exception("Proposicion no válida."
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara("do");
			int flag = etq;
			emite("ir-f "+etq);
			etq = etq+1;
			this.proposicion();
			emite("ir-a("+etqb);
			etq = etq+1;
			parchea(2,flag);
		}else{
			this.proposicion_simple();
		}
	}
	
	private void pElse() throws Exception{
		this.anaLex.predice();
		String lex = anaLex.getLex();
		if(lex.compareTo("else")==0){
			this.compara("else");
			this.proposicion();
		}
	}
	
	
	
	/**Método que reconoce cualquier tipo de proposicion (instruccion) del subconjunto de Pascal.
	 * 
	 * @throws Exception Se recogen errores de niveles inferiores de la jerarquia y se lanzan otros si hay algun tipo de asignacion incorrecta.
	 */
	private void proposicion_simple() throws Exception{
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		String lexTipo = anaLex.getLex();
		if(lexToken.compareTo("identificador")==0){
			// FIXME Duda
			/** ¿Se puede predecir consecutivamente?
			 ** ¿O predice lo mismo 2 veces?
			 ** Supongo que predice lo mismo en ambas. En ese caso está bien.  
			 **/
			Tupla t = this.id_comp();
			this.anaLex.predice();
			lexTipo = anaLex.getLex();
			// Si encontramos :=, es una asignación.
			if(lexTipo.compareTo(":=")==0){
				this.compara(":=");
				parh = false;
				Tupla t2 = this.expresion(); // Devuelve tipo
				if (!compatibles((String)t.getnTupla(1), (String)t2.getnTupla(1)) || 
						(this.pilaTablaSim.getTSnivel(n).getEntrada((String) t.getnTupla(0)).getClase() != "var")){
					Global.setErrorMsg("Violación restricciones. Asignación incorrecta");
					throw new Exception("Error sintaxis: Asignación incorrecta"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
				}
				if(compatibles(t.getnTupla(1).toString(),"integer") || 
						compatibles(t.getnTupla(1).toString(),"numReal") || 
						compatibles(t.getnTupla(1).toString(),"boolean")){
					emite("desapila-ind");
				}else{
					this.emite("mueve "+(this.pilaTablaSim.getTSnivel(n).getEntrada((String) t.getnTupla(0)).getProps().getTam()));
					etq = etq + 1;
				}
			// Si no, es una invocación a procedimiento
			}else{
				this.proposicion_proc((String) t.getnTupla(0));
			}
		}else if(lexTipo.compareTo("read")==0){
			this.compara("read");
			this.compara("(");
			Tupla t = this.id_comp();
			this.compara(")");
			if(this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getClase().compareTo("var")!=0){
				throw new Exception("Error de sintaxis: No se puede modificar una constante"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.emite("read");
			emite("desapila-dir "+this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getProps().getDir());
			etq = etq + 2;
		}else if(lexTipo.compareTo("write")==0){
			this.compara("write");
			this.compara("(");
			parh = false;
			this.expresion();
			this.compara(")");
			this.emite("write");
			etq = etq+1;
		}else if(lexTipo.compareTo("new")==0){
			this.compara("new");
			this.compara("(");
			Tupla t = this.id_comp();
			this.compara(")");
			if(this.pilaTablaSim.getTSnivel(n).getEntrada((String) t.getnTupla(0)).getClase().compareTo("var")!=0 || 
					((String) t.getnTupla(1)).compareTo("pointer")!=0){
				throw new Exception("Error: no se puede instanciar memoria para este tipo"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			if(this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getProps().getTbase().getT().compareTo("pointer")==0){
				emite("new "+(this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getProps().getTam()));
			}else{
				emite("new "+(this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getProps().getTbase().getTam()));
			}
			emite("desapila-ind");
			etq = etq+2;
		}else if(lexTipo.compareTo("dispose")==0){
			this.compara("dispose");
			this.compara("(");
			Tupla t = this.id_comp();
			this.compara(")");
			if(this.pilaTablaSim.getTSnivel(n).getEntrada((String) t.getnTupla(0)).getClase().compareTo("var")!=0 || 
					((String) t.getnTupla(1)).compareTo("pointer")!=0){
				throw new Exception("Error: no se puede instanciar memoria para este tipo"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			if(this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getProps().getTbase().getT().compareTo("pointer")==0){
				emite("del "+(this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getProps().getTam()));
			}else{
				emite("del "+(this.pilaTablaSim.getTSnivel(n).getEntrada(t.getnTupla(0).toString()).getProps().getTbase().getTam()));
			}
		}else
			// Si lo siguiente no empieza por "begin" ya dará error.
			this.proposicion_compuesta();
	}
	
	private Tupla id_comp() throws Exception{
		String id = this.id();
		if (!this.pilaTablaSim.getTSnivel(n).existeID(id))
			{
			throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
			}
		String tipo = this.id_compR(id);
		Tupla rtupla = new Tupla(2);
		rtupla.setnTupla(0, id);
		rtupla.setnTupla(1, tipo);
		return rtupla;
	}
	
	private String id_compR(String id) throws Exception{
		Propiedades propsID = this.pilaTablaSim.getTSnivel(n).getEntrada(id).getProps();
		this.anaLex.predice();
		String lex = this.anaLex.getLex();
		// 1. Si encontramos [
		if (lex.compareTo("[")==0){
			this.compara("[");
			parh = false;
			Tupla t = this.expresion();
			this.compara("]");
			if ((this.pilaTablaSim.getTSnivel(n).getEntrada(id).getClase().compareTo("var")!=0) ||
					t.getnTupla(0).toString().compareTo("integer")!=0 || 
					propsID.getT().compareTo("array")!=0)
				{
				throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
				}
			this.id_compR(id);
			this.emite("apila "+ propsID.getTbase().getN());
			this.emite("multiplica");
			this.emite("suma");
			etq = etq+3;
			return propsID.getTbase().getT();
			
		// 2. Si encontramos ^
		} else if (lex.compareTo("^")==0){
			this.compara("^");
			if(propsID.getT().compareTo("pointer")!=0){
				throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
			}
			this.emite("apila-ind");
			etq++;
			return propsID.getTbase().getT();
		
		// 3. Si encontramos .
		} else if (lex.compareTo(".")==0){
			this.compara(".");
			String id2 = this.id();
			if ((this.pilaTablaSim.getTSnivel(n).getEntrada(id).getClase().compareTo("var")!=0) ||
					propsID.getT().compareTo("reg")!=0 || !propsID.getCampos().containsKey(id2))
				{
				throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
				}
			this.id_compR(id);
			this.emite("apila "+ propsID.getCampos().get(id2).getDesp());
			this.emite("suma");
			etq = etq + 2;
			return propsID.getCampos().get(id2).getTipo().getT();
			
		// Si no, es una variable normal o el ID de un procedimiento.	
		} else {
			// Si es una variable, accedemos.
			if (this.pilaTablaSim.getTSnivel(n).getEntrada(id).getClase().compareTo("var")!=0){
				entradaTS entradaID = this.pilaTablaSim.getTSnivel(n).getEntrada(id); 
				this.accesoVar(entradaID);
				etq = etq + this.longAccesoVar(entradaID);
				}
			return propsID.getT();
		}		
	}
	
	private void proposicion_proc(String lex)throws Exception{
		if(this.pilaTablaSim.getTSnivel(n).getEntrada(lex).getClase().compareTo("proc")!=0)
		{
			throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
		}
		this.compara("(");
		ArrayList<CParams> fparams = this.pilaTablaSim.getTSnivel(n).getEntrada(lex).getProps().getParams();
		apilaRet(etq);
		etq = etq+longApilaRet;
		this.Aexps(fparams);
		this.compara(")");
		emite("ir-a "+this.pilaTablaSim.getTSnivel(n).getEntrada(lex).getProps().getInicio());
		etq = etq+1;
	}
	
	private void Aexps(ArrayList<CParams> fparams)throws Exception{
		this.anaLex.predice();
		String lex = anaLex.getLex();
		if(lex.compareTo(")")!=0){
			inicioPaso();
			etq = etq+longInicioPaso;
			int nparams = this.lista_exps(fparams);
			if(nparams!=fparams.size()){
				throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
			}
			finPaso();
			etq = etq+longFinPaso;
		}else{
			if(fparams.size()>0){
				throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
			}
		}
	}
	
	private int lista_exps(ArrayList<CParams> fparams)throws Exception{
		emite("copia");
		etq = etq+1;
		parh = (fparams.get(0).getModo().compareTo("var")==0);
		Tupla t = this.expresion();
		if(fparams.size()==0||fparams.get(0).getModo().compareTo(t.getnTupla(1).toString())!=0
				||!compatibles(fparams.get(0).getTipo().getT(),(String) t.getnTupla(0))){
				throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
			}
		pasoParametro((String) t.getnTupla(1),fparams.get(0));
		etq = etq+longPasoParametro;
		int nparams1 = 1;
		return this.lista_expsR(fparams,nparams1);
	}
	
	private int lista_expsR(ArrayList<CParams> fparams,int nparams)throws Exception{
		this.anaLex.predice();
		String lex = anaLex.getLex();
		if(lex.compareTo(",")==0){
			this.compara(",");
			emite("copia");
			etq = etq+1;
			parh = (fparams.get(nparams).getModo().compareTo("var")==0);
			Tupla t = this.expresion();
			if(fparams.size()<nparams || fparams.get(nparams).getModo().compareTo((String) t.getnTupla(1))!=0 || 
					!compatibles(fparams.get(nparams).getTipo().getT(),(String) t.getnTupla(0))){
				throw new Exception("Error:"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');	
			}
			pasoParametro(t.getnTupla(1).toString(),fparams.get(nparams));
			etq = etq+longPasoParametro;
			int nparams1 = nparams+1;
			return this.lista_expsR(fparams, nparams1);
		}else{
			return nparams;
		}
	}
	
	/**Método que reconoce una expresion.
	 * 
	 * @return Devuelve un string del token analizado.
	 * @throws Exception Se recoge cualquier tipo de error que haya ocurrido en cualquier metodo de los niveles
	 * inferiores de la jerarquia.
	 */
	private Tupla expresion() throws Exception{
		Tupla tup = new Tupla(2);
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("char")==0){
			this.anaLex.scanner();
			this.emite("apila " + anaLex.getLex());
			etq = etq+1;
			String modo = "val";
			String tipo = "char";
			tup.setnTupla(0, tipo);
			tup.setnTupla(1, modo);
		}else{
			// FIXME Comprobar si después de Expresión_Simple viene OPREL
			// Si ExpresiónR<> vacío entonces parh = false si no parh
			// ARREGLADO!
			Tupla t1 = this.expresion_simple();
			Tupla t2 = this.expresionR((String) t1.getnTupla(0));
			if (((String) t2.getnTupla(1)).compareTo("val")==0){
				tup.setnTupla(1, t2.getnTupla(1));
			} else tup.setnTupla(1, t1.getnTupla(1));
			if (((String) t2.getnTupla(0)).compareTo("")==0) {
				tup.setnTupla(0, "boolean");
			} else tup.setnTupla(0, t1.getnTupla(0));
		}
		return tup;
	}
	
	/**Método que reconoce una parte de una expresion. Se creo para evitar la recursion a izquierdas.
	 * 
	 * @param tipo Se pasa por parametro el tipo para ver si concuerda con el de la expresion.
	 * @return Se devuelve un string de la expresion analizada.
	 * @throws Exception Se genera un error si hay una violacion de tipos.
	 */
	private Tupla expresionR(String tipo0) throws Exception {
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		Tupla t = new Tupla(2);
		if(lexToken.compareTo("igual")==0 || lexToken.compareTo("distintos")==0 || lexToken.compareTo("mayor")==0
			|| lexToken.compareTo("mayor_igual")==0 || lexToken.compareTo("menor")==0 || lexToken.compareTo("menor_igual")==0){
				String op = this.operador();
				parh = false;
				Tupla t1 = this.expresion_simple();
				if (!comparables(tipo0,"int")||!comparables(tipo0,"numReal")||!comparables(tipo0,"boolean")) {
					//throw new Exception("Error sintaxis: tipos no compatibles.");
					Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
				}
				String modo = "val";
				emite("apila "+op);
				etq = etq+1;
				t.setnTupla(0, t1.getnTupla(0));
				t.setnTupla(1, modo);
		} else { 
			t.setnTupla(0, "");
			t.setnTupla(1, "");
		}
		return t;
	}
	
	/**Método que reconoce una expresion simple.
	 * 
	 * @return Se devuelve un string del token analizado.
	 * @throws Exception Se recoge cualquier tipo de error generado mas abajo en la jerarquia.
	 */
	private Tupla expresion_simple() throws Exception {
		this.anaLex.predice();
		String lexToken = anaLex.getToken();
		Tupla tup;
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0){
			String op = this.operador();
			tup = this.termino();
			if(((String) tup.getnTupla(0)).compareTo("integer")!=0 ||
					((String) tup.getnTupla(0)).compareTo("numReal")!=0){
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			emite("apila "+op);
			etq = etq+1;
			return tup;
		} else {
			// FIXME Comprobar si después de Expresión_Simple viene OPSUMA ó OR
			// Si exp_simpleR <> vacío entonces parh = false si no parh
			// ARREGLADO!!
			tup = new Tupla(2);
			Tupla t1 = this.termino();
			Tupla t2 = this.exp_simpleR((String) t1.getnTupla(0));
			if (((String) t2.getnTupla(1)).compareTo("val")==0){
				tup.setnTupla(1, t2.getnTupla(1));
			} else tup.setnTupla(1, t1.getnTupla(1));
			if (((String) t2.getnTupla(0)).compareTo("numReal")==0) {
				tup.setnTupla(0, "numReal");
			} else tup.setnTupla(0, t1.getnTupla(0));
		}
		return tup;
	}

	/**Método que reconoce una parte de una expresion simple. Creado para evitar la recursion a izquierdas.
	 * 
	 * @param tipo0 Se pasa por parametro el tipo para ver si concuerda con el resto de la expresion.
	 * @throws Exception Se lanza una excepcion si los tipos son incompatibles.
	 */
	private Tupla exp_simpleR(String tipo0) throws Exception {
		this.anaLex.predice();
		String lexToken=this.anaLex.getToken();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0){
			String op = this.operador();
			parh = false;
			Tupla t1 = this.expresion_simple();
			if ((tipo0.compareTo("integer")!=0 && t1.getnTupla(0).toString().compareTo("integer")!=0)
					||(tipo0.compareTo("numReal")!=0 && t1.getnTupla(0).toString().compareTo("numReal")!=0)){
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			emite("apila "+op);
			String modo = "val";
			etq = etq + 1;
			Tupla t = new Tupla(2);
			t.setnTupla(0, t1.getnTupla(0).toString());
			t.setnTupla(1, modo);
			return t;
		}else if(lexToken.compareTo("ologica")==0){
			this.operador();
			parh = false;
			emite("copia");
			int flag = etq + 1;
			emite("ir-v "+etq);
			emite("desapila");
			etq = etq+3;
			Tupla t1 = this.expresion_simple();
			parchea(3,flag);
			if (tipo0.compareTo("boolean")!=0 ||((String) t1.getnTupla(0)).compareTo("boolean") != 0){
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			String modo0 = "val";
			Tupla t = new Tupla(2);
			t.setnTupla(0, t1.getnTupla(0));
			t.setnTupla(1, modo0);
			return t;
		}else{
			Tupla t = new Tupla(2);
			t.setnTupla(0, "");
			t.setnTupla(1, "");
			return t;
		}
	}
	
	/**Método que reconoce un termino.
	 * 
	 * @return Se devuelve un string con el lexema del termino.
	 * @throws Exception Se recoge cualquier tipo de error que se produzca dentro del termino.
	 */
	private Tupla termino() throws Exception {
		Tupla tup = new Tupla(2);
		// FIXME Comprobar si después de Expresión_Simple viene viene OPMULT ó AND()
		// Si ExpresiónR<> vacío entonces parh = false si no parh
		// ARREGLADO!!
		Tupla t1 = this.factor();
		Tupla t2 = this.terminoR((String) t1.getnTupla(0));
		if (((String) t2.getnTupla(1)).compareTo("val")==0){
			tup.setnTupla(1, t2.getnTupla(1));
		} else tup.setnTupla(1, t1.getnTupla(1));
		if (((String) t2.getnTupla(0)).compareTo("numReal")==0) {
			tup.setnTupla(0, "numReal");
		} else tup.setnTupla(0, t1.getnTupla(0));
		return tup;
	}

	/**Método que reconoce una parte de un termino. Se creo para evitar la recursion a izquierdas.
	 * 
	 * @param tipo Se pasa por parametro el tipo de la otra parte del termino reconocida para ver si concuerda.
	 * @throws Exception Se lanza una excepcion si los tipos no son compatibles.
	 */
	private Tupla terminoR(String tipo0) throws Exception {
		this.anaLex.predice();
		String lexToken = this.anaLex.getToken();
		if (lexToken.compareTo("multiplica")==0 || lexToken.compareTo("divide")==0
				|| lexToken.compareTo("modulo")==0 || lexToken.compareTo("divide_real")==0){
			String op =this.operador();
			parh = false;
			Tupla t1 = this.termino();
			if ((tipo0.compareTo("integer")!=0 && t1.getnTupla(0).toString().compareTo("integer")!=0)
					||(tipo0.compareTo("numReal")!=0 && t1.getnTupla(0).toString().compareTo("numReal")!=0)){
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			emite("apila "+op);
			String modo0= "val";
			etq = etq+1;
			Tupla t = new Tupla(2);
			t.setnTupla(0, t1.getnTupla(0));
			t.setnTupla(1, modo0);
			return t;
		}else if(lexToken.compareTo("ylogica")==0){
			parh = false;
			int flag = etq;
			emite("ir-f "+etq);
			etq = etq+1;
			Tupla t1 = this.termino();
			if (tipo0.compareTo("boolean")!=0 ||t1.getnTupla(0).toString().compareTo("boolean")!=0){
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			String modo0="val";
			t1.setnTupla(1, modo0);
			etq = etq + 1;
			parchea(2,flag);
			etq = etq - 1;
			int etqAux = etq + 2;
			emite("ir-a "+(etqAux));
			emite("apila 0");
			etq = etq+2;
			return t1;
		}else {
			Tupla t = new Tupla(2);
			t.setnTupla(0, "");
			t.setnTupla(1, "");
			return t;
		}
	}


	
	/**Método que reconoce un factor.
	 * 
	 * @return Se devuelve un string con el valor del factor.
	 * @throws Exception Se lanzan errores si el identificador no existe.
	 */
	private Tupla factor() throws Exception {
		this.anaLex.predice();
		String token = this.anaLex.getToken();
		String lex = this.anaLex.getLex();
		if (token.compareTo("identificador")==0){
			String tipo1 = (String) this.id_comp().getnTupla(1);
			if(compatibles(tipo1,"integer")||compatibles(tipo1,"numReal")||compatibles(tipo1,"boolean") && (parh==false)){
				emite("apila-ind");
				etq = etq+1;
			}
			String modo = "var";
			Tupla t = new Tupla(2);
			t.setnTupla(0, tipo1);
			t.setnTupla(1, modo);
			return t;
		} else if (token.compareTo("lparen")==0){
			this.compara("(");
			Tupla t = this.expresion();
			this.compara(")");
			return t;
		} else if (lex.compareTo("not")==0){
			this.anaLex.scanner();
			String signo = this.anaLex.getLex();
			parh = false;
			Tupla t = this.factor();
			this.emite(signo);
			if(t.getnTupla(0).toString().compareTo("booleano")!=0){
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");}
			return t;
		} else if (lex.compareTo("true")==0 || lex.compareTo("false")==0){
			this.anaLex.scanner();
			String tipo1 = "boolean";
			String modo = "val";
			this.emite("apila "+ this.anaLex.getLex());
			etq = etq+1;
			Tupla t = new Tupla(2);
			t.setnTupla(0, tipo1);
			t.setnTupla(1, modo);
			return t;
		} else if (token.compareTo("num")==0){
			this.anaLex.scanner();
			String tipo1 = "integer";
			String modo = "val";
			this.emite("apila("+ this.anaLex.getLex()+")");
			etq = etq+1;
			Tupla t = new Tupla(2);
			t.setnTupla(0, tipo1);
			t.setnTupla(1, modo);
			return t;
		} else if (token.compareTo("numReal")==0){
			this.anaLex.scanner();
			String tipo1 = "numReal";
			String modo = "val";
			this.emite("apila("+ this.anaLex.getLex()+")");
			etq = etq+1;
			Tupla t = new Tupla(2);
			t.setnTupla(0, tipo1);
			t.setnTupla(1, modo);
			return t;
		} else return null;
	}

	
	/**Método que reconoce cualquier tipo de operador.
	 * 
	 * @return Se devuelve un string con el lexema del operador.
	 */
	private String operador() {
		try{
		this.anaLex.scanner();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return anaLex.getToken();
	}
	
	
	/** Método que reconoce un identificador.
	 * 
	 * @throws Exception Se genera una excepcion si el identificador no es valido.
	 */
	private String id() throws Exception{
		this.anaLex.scanner();
		if(anaLex.getToken().compareTo("identificador")!=0){
			throw new Exception("Error sintaxis: identificador no válido"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}else{
			return this.anaLex.getLex();
		}
	}
//*************************************************************************************************
//                             FUNCIONES AUXILIARES
//*************************************************************************************************




	/** Funcion que compara un string dado con el siguiente elemento lexico a analizar.
	 * 
	 * @param tok String a comparar con el token del programa.
	 * @throws Exception 
	 */
	private void compara(String tok) throws Exception{
		anaLex.scanner();
		String lexema = anaLex.getLex();
		if(lexema.compareTo(tok)!=0){
			throw new Exception("Error sintaxis: Programa mal formado"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}
	}
	
	/** Funcion que dice si dos tipos son iguales
	 * 
	 * @param tipo1 Tipo de la primera expresion
	 * @param tipo2 Tipo de la segunda expresion
	 * @return Cierto si los tipos son iguales, y falso si son tipos distintos.
	 */
	private boolean compatibles(Propiedades p1,Propiedades p2,TS ts,ArrayList<String[]> visitados)
	{
		
	boolean devolver=true;
	boolean existe=false;
	String[] aux=new String[2];
	for(int i=0;i<visitados.size();i++)
	{
		aux[0]=visitados.get(i)[0];
		aux[1]=visitados.get(i)[1];
		if((compatibles(aux[0],p1.getT())&&compatibles(aux[1],p2.getT()))||(compatibles(aux[1],p1.getT())&&compatibles(aux[0],p2.getT())))
				existe=true;
	}
	aux[0]=p1.getT();
	aux[1]=p2.getT();
	if(existe) devolver=true;
	else 
	{
	visitados.add(aux);
	if(comparables(p1.getT(), p2.getT()))
    devolver= true;

	else if((compatibles(p1.getT(),p2.getT())&&(p1.getT().compareTo("array"))==0))
		if(p1.getN()==p2.getN())
			devolver= comparables(p1.getTbase().getT(),p2.getTbase().getT());
		else devolver=false;
	else if(p1.getT().compareTo(p2.getT())==0 &&(p1.getT().compareTo("reg")==0))
	{
		if(p1.getCampos().size()==p2.getCampos().size())
			{
				for(int i=0;i<p1.getCampos().size();i++)
				{
					if(compatibles(((CCampos)p1.getCampos().get(i)).getTipo(),((CCampos)p2.getCampos().get(i)).getTipo(),ts,visitados)==false)
						devolver= false;
				}
				devolver=devolver&&true;
			}
		else devolver=false;
	}
	else if(p1.getT().compareTo(p2.getT())==0 &&(p1.getT().compareTo("pointer")==0))
    devolver=compatibles(p1.getTbase(),p2.getTbase(),ts,visitados);
	
	else devolver=false;
	}
	return devolver;
}


	/**Funcion que se encarga de emitir los distintos tipos de operaciones.
	 * 
	 * @param a String que se pasa por parametro para diferenciar el tipo de operacion a emitir.
	 */
	private void emite(String a){
		this.instrucciones.add(a);
	}

	/**
	 * Sustituye la instrucción en la posición etq por otra.
	 * @param a Instrucción que va a ser añadida.
	 */
	private void emiteP(String a){
		this.instrucciones.set(etq, a);
	}
	
	/**Función que comprueba que los dos tipos sean comparables
	 * 
	 * 
	 * @param t1 de tipo string (Tipo1)
	 * @param t2 de tipo string (Tipo2)
	 * @return
	 */
	private boolean comparables(String t1, String t2) {
		boolean value = compatibles(t1, t2);
		value=value&&(compatibles(t1,"boolean")||compatibles(t1,"real")||compatibles(t1,"integer"));
		
		return value;
	}
	private boolean compatibles(String tipo1,String tipo2){
		return (tipo1.compareTo(tipo2)==0);
	}

	/**Función que comprueba que los dos tipos sean comparables
	 * 
	 * 
	 * @param t1 de tipo string (Tipo1)
	 * @param t2 de tipo string (Tipo2)
	 * @return

	
	/**
	 * 
	 * @return
	 */
	
	
	/**public static void main(String[] args) {
		AnalizadorSintactico anaSin = new AnalizadorSintactico("c:/prueba.txt");
		try {
			anaSin.programa();
			System.out.println(Global.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
