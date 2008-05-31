/**
 * 
 */
package compilador;

import java.util.ArrayList;
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
	private Vector instrucciones;
	/**
	 * String con el path del archivo que se tiene que escribir.
	 */
	private String pathDestino;
	
	private int n;
	
	private int dir;
	
	private int etq;
	/**Constructor del analizador sintactico, donde se crea el analizador lexico y la tabla de simbolos. 
	 * 
	 * @param path Ruta del archivo que se va a analizar.
	 */
	public AnalizadorSintactico(String path,String path2){
		anaLex = new AnalizadorLexico(path);
		tablaSim = new TS();
		instrucciones = new Vector(0,1);
		pathDestino = path2;
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
			ir-a(etq);
			etq = longInicio+1;
			this.bloqueDecls();
			int inicio = etq;
			prologo(n,dir);
			etq = etq + longPrologo;
			this.proposicion_compuesta(); 
			epilogo(n);
			ir-ind();
			etq = etq + longEpilogo+1;
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
			this.pilaTablaSim.añadeIDcima((String)t.getnTupla(0), "const",(Propiedades)t.getnTupla(1));
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
			this.pilaTablaSim.añadeIDcima(lex, "var", props);
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
	private Propiedades tipo()throws Exception{
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
	private Propiedades tipo_estandar()throws Exception{
		anaLex.scanner();
		Propiedades p = new Propiedades();
		String token = anaLex.getToken();
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
	
	@SuppressWarnings("unchecked")
	private Propiedades tipo_construido()throws Exception{
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
			p.setCampos((ArrayList<CCampos>) t.getnTupla(0));
			p.setTam(Integer.parseInt(t.getnTupla(1).toString()));
		}else{
			//throw new Exception("Error sintaxis: Tipo incorrecto.");
			Global.setErrorMsg("Violación restricciones. Tipo incorrecto");
		}
		return p;
	}
	
	@SuppressWarnings("unchecked")
	private Tupla campos()throws Exception{
		int desp = 0;
		Tupla t = this.campo(desp);
		ArrayList<CCampos> campos0 = new ArrayList<CCampos>();
		campos0.add((CCampos)t.getnTupla(0));
		int desp1 = desp + Integer.parseInt(t.getnTupla(1).toString());
		Tupla t2 = this.camposR(campos0,desp1);
		int tam2 = Integer.parseInt(t.getnTupla(1).toString()) + Integer.parseInt(t2.getnTupla(1).toString());
		Tupla tf = new Tupla(2);
		tf.setnTupla(0, (ArrayList<CCampos>) t2.getnTupla(0));
		tf.setnTupla(1, tam2);
		return tf;
	}
	
	private Tupla camposR(ArrayList<CCampos> campos0, int desp0)throws Exception{
		anaLex.predice();
		String lex = this.anaLex.getLex();
		Tupla tf = new Tupla(2);
		if(lex.compareTo("end")==0){
			tf.setnTupla(0, campos0);
			tf.setnTupla(1, desp0);
		}else{
			Tupla t = this.campo(desp0);
			campos0.add((CCampos)t.getnTupla(0));
			int desp1 = desp0 + Integer.parseInt(t.getnTupla(1).toString());
			Tupla t2 = this.camposR(campos0, desp1);
			int tam2 = Integer.parseInt(t.getnTupla(1).toString()) + Integer.parseInt(t2.getnTupla(1).toString());
			tf.setnTupla(0, (ArrayList<CCampos>) t2.getnTupla(0));
			tf.setnTupla(1, tam2);
		}
		return tf;
	}
	
	private Tupla campo(int desp)throws Exception{
		String lex = this.id();
		Tupla t = new Tupla(2);
		//FIXME esta comprobacion no es realmente asi
		//existeID(ts, lex) & ts[lex].props.nivel = n 
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
		}else{
			//vacio
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
		this.pilaTablaSim.añadeIDcima(lex, "proc", props);
		this.pilaTablaSim.creaTS();
		Propiedades props2 = new Propiedades();
		props2.setT("proc");
		props2.setParams(params);
		props2.setNivel(n+1);
		props2.setInicio(inicio);
		this.pilaTablaSim.añadeIDcima(lex, "proc", props2);
		n = n + 1;
		this.bloqueDecls();
		prologo(n,dir);
		etq = etq + longPrologo;
		this.proposicion_compuesta();
		epilogo(n);
		etq = etq + longEpilogo+1;
		ir-ind()
		dir = dirAnt;
		//desapila TS (solo -1 en la cima supongo)
		n = n - 1;
	}
	
	private ArrayList<CParams> parametros()throws Exception{
		ArrayList<CParams> params = new ArrayList<CParams>();
		anaLex.predice();
		String lex = this.anaLex.getLex();
		if(lex.compareTo("(")==0){
			this.compara("(");
			ArrayList<CParams> params = lista_Params();
			this.compara(")");
		}
		return params;
	}
	
	private ArrayList<CParams> lista_Params()throws Exception{
		Tupla t = param();
		//FIXME aqui el param tiene un parametro menos xD
		this.pilaTablaSim.añadeIDcima((String)t.getnTupla(0), (String)t.getnTupla(1),(Propiedades)t.getnTupla(2));
		dir = dir + ((Propiedades)t.getnTupla(2)).getTam();
		ArrayList<CParams> params0 = new ArrayList<CParams>();
		params0.add((CParams)t.getnTupla(4));
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
			this.pilaTablaSim.añadeIDcima((String)t.getnTupla(0), (String)t.getnTupla(1),(Propiedades)t.getnTupla(2));
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
		String lexToken= anaLex.getToken();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("if")==0){
			this.compara("if");
			boolean parh = false;
			Tupla t = this.expresion(parh);
			if(t.getnTupla(0).toString()!="boolean"){
				throw new Exception("Proposicion no válida."
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara("then");
			int flag1 = etq;
			ir-f(---);
			etq = etq+1;
			this.proposicion();
			int flag2 = etq;
			ir-a(---);
			etq = etq+1;
			parchea(flag1,etq);
			this.pElse();
			parchea(flag2,etq);
		}else if(lexTipo.compareTo("while")){
			this.compara("while");
			boolean parh = false;
			int etqb = etq;
			Tupla t = this.expresion(parh);
			if(t.getnTupla(0).toString()!="boolean"){
				throw new Exception("Proposicion no válida."
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara("do");
			int flag = etq;
			ir-f(---);
			etq = etq+1;
			this.proposicion();
			ir-a(etqb);
			etq = etq+1;
			parchea(flag,etq);
		}else{
			this.proposicion_simple();
		}
	}
	
	private void pElse(){
		this.anaLex.predice();
		String lex = anaLex.getLex();
		if(lex.compareTo("else")==0){
			this.Else();
			this.proposicion();
		}else{
			//vacio
		}
	}
	
	
	
	private void proposicion_aux(){
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("if")==0){
			this.compara("if");
			boolean parh = false;
			Tupla t = this.expresion(parh);
			if(t.getnTupla(0).toString()!="boolean"){
				throw new Exception("Proposicion no válida."
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara("then");
			ir-f(a);
			etq = etq+1;
			this.proposicion_aux();
			ir-a(b);
			etq = etq+1;
			this.compara("else");
			this.proposicion_aux();
			parchea(a,b);
		}else if(lexTipo.compareTo("while")){
			this.compara("while");
			boolean parh = false;
			Tupla t = this.expresion(parh);
			if(t.getnTupla(0).toString()!="boolean"){
				throw new Exception("Proposicion no válida."
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			this.compara("do");
			ir-f(a);
			etq = etq+1;
			this.proposicion_aux();
			ir-a(b);
			etq = etq+1;
			parchea(a,b);
		}else{
			this.proposicion_simple();
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
			Tupla t = this.id_comp();
			this.compara(":=");
			boolean parh = false;
			Tupla t2 = this.expresion(parh); // Devuelve tipo
			if (!compatibles(t.getnTupla(1),t2.getnTupla(1)) || ((entradaTS)this.pilaTablaSim.getTScima().getEntrada(t.getnTupla(0).toString())).getClase()!="var"))){
				Global.setErrorMsg("Violación restricciones. Asignación incorrecta");
				throw new Exception("Error sintaxis: Asignación incorrecta"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			}
			
			//this.emite("desapila_dir "+this.tablaSim.getDir(lex));
		}else if(lexTipo.compareTo("read")==0){
			this.compara("read");
			this.compara("(");
			this.id();
			String lex = this.anaLex.getLex();
			if (!this.tablaSim.existeID(lex)) 
				throw new Exception("Error sintaxis: ID no declarado"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			this.compara(")");
			//this.emite("apila_dir "+ this.tablaSim.getDir(lex));
			this.emite("read");
			this.emite("desapila_dir "+ this.tablaSim.getDir(lex));
		}else if(lexTipo.compareTo("write")==0){
			this.compara("write");
			this.compara("(");
			this.expresion();
			this.compara(")");
			this.emite("write");
		}else{
			// Si lo siguiente no empieza por "begin" ya dará error.
			this.proposicion_compuesta();
		}
	}
	
	/**Método que reconoce una expresion.
	 * 
	 * @return Devuelve un string del token analizado.
	 * @throws Exception Se recoge cualquier tipo de error que haya ocurrido en cualquier metodo de los niveles
	 * inferiores de la jerarquia.
	 */
	private String expresion() throws Exception{
		String t="";
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("char")==0){
			t = "char";
			this.anaLex.scanner();
			this.emite("apila " + anaLex.getLex());
		}else{
			t=this.exp_simple();
			t=this.expresionR(t);
		}
		return t;
	}
	
	/**Método que reconoce una parte de una expresion. Se creo para evitar la recursion a izquierdas.
	 * 
	 * @param tipo Se pasa por parametro el tipo para ver si concuerda con el de la expresion.
	 * @return Se devuelve un string de la expresion analizada.
	 * @throws Exception Se genera un error si hay una violacion de tipos.
	 */
	private String expresionR(String tipo) throws Exception {
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("igual")==0 || lexToken.compareTo("distintos")==0 || lexToken.compareTo("mayor")==0
			|| lexToken.compareTo("mayor_igual")==0 || lexToken.compareTo("menor")==0 || lexToken.compareTo("menor_igual")==0){
				String op = this.operador();
				String t1 = this.exp_simple();
				if (!compatibles(tipo,t1)) {
					//throw new Exception("Error sintaxis: tipos no compatibles.");
					Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
				}
				this.emite(op);
				// Si todo va bien, el tipo cambia a boolean por ser relaccional.
				return "boolean";
		} else { 
			return tipo;}
	}
	
	/**Método que reconoce una expresion simple.
	 * 
	 * @return Se devuelve un string del token analizado.
	 * @throws Exception Se recoge cualquier tipo de error generado mas abajo en la jerarquia.
	 */
	private String exp_simple() throws Exception {
		String tipo="";
		this.anaLex.predice();
		String lexToken = anaLex.getToken();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0){
			this.operador();
			tipo = this.termino();
			if (lexToken.compareTo("resta")==0) this.emite("menosN");
		} else {
			tipo = this.termino();
			this.exp_simpleR(tipo);
		}
		return tipo;
	}

	/**Método que reconoce un termino.
	 * 
	 * @return Se devuelve un string con el lexema del termino.
	 * @throws Exception Se recoge cualquier tipo de error que se produzca dentro del termino.
	 */
	private String termino() throws Exception {
		String tipo=this.factor();
		this.terminoR(tipo);
		return tipo;
	}

	/**Método que reconoce un factor.
	 * 
	 * @return Se devuelve un string con el valor del factor.
	 * @throws Exception Se lanzan errores si el identificador no existe.
	 */
	private String factor() throws Exception {
		String tipo="";
		this.anaLex.predice();
		String token = this.anaLex.getToken();
		String lex = this.anaLex.getLex();
		if (token.compareTo("identificador")==0){
			this.id();
			lex = this.anaLex.getLex(); 
			if (!tablaSim.existeID(lex)) 
				throw new Exception("Error sintaxis: ID no declarado"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			tipo = this.tablaSim.devuelveTipo(lex);
			this.emite("apila_dir "+ this.tablaSim.getDir(lex));
		} else if (token.compareTo("lparen")==0){
			this.compara("(");
			tipo = this.expresion();
			this.compara(")");
		} else if (lex.compareTo("not")==0){
			this.anaLex.scanner();
			String l = this.anaLex.getLex();
			tipo = this.factor();
			this.emite(l);
		} else if (lex.compareTo("true")==0 || lex.compareTo("false")==0){
			this.anaLex.scanner();
			tipo = "boolean";
			this.emite("apila "+ this.anaLex.getLex());
		} else if (token.compareTo("num")==0){
			this.anaLex.scanner();
			tipo = "integer";
			this.emite("apila "+ this.anaLex.getLex());
		} else if (token.compareTo("numReal")==0){
			this.anaLex.scanner();
			tipo = "real";
			this.emite("apila "+ this.anaLex.getLex());
		}
		return tipo;
	}

	/**Método que reconoce una parte de un termino. Se creo para evitar la recursion a izquierdas.
	 * 
	 * @param tipo Se pasa por parametro el tipo de la otra parte del termino reconocida para ver si concuerda.
	 * @throws Exception Se lanza una excepcion si los tipos no son compatibles.
	 */
	private void terminoR(String tipo) throws Exception {
		this.anaLex.predice();
		String lexToken = this.anaLex.getToken();
		if (lexToken.compareTo("multiplica")==0 || lexToken.compareTo("divide")==0
				|| lexToken.compareTo("modulo")==0 || lexToken.compareTo("divide_real")==0
				|| lexToken.compareTo("ylogica")==0){
			String op =this.operador();
			String t= this.termino();
			if (!compatibles(tipo,t)) {
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			emite(op);
		}
	}

	/**Método que reconoce una parte de una expresion simple. Creado para evitar la recursion a izquierdas.
	 * 
	 * @param tipo0 Se pasa por parametro el tipo para ver si concuerda con el resto de la expresion.
	 * @throws Exception Se lanza una excepcion si los tipos son incompatibles.
	 */
	private void exp_simpleR(String tipo0) throws Exception {
		this.anaLex.predice();
		String lexToken=this.anaLex.getToken();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0
				|| lexToken.compareTo("ologica")==0){
			String op = this.operador();
			String t1 = this.exp_simple();
			if (!compatibles(tipo0,t1)){
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			this.emite(op);
		}
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
	private boolean compatibles(String tipo1,String tipo2){
		return (tipo1.compareTo(tipo2)==0);
	}
	

	/**Funcion que se encarga de emitir los distintos tipos de operaciones.
	 * 
	 * @param a String que se pasa por parametro para diferenciar el tipo de operacion a emitir.
	 */
	private void emite(String a){
		this.instrucciones.add(a);
	}

	
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
