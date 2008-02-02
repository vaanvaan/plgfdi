package maquinaP;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

import compilador.AuxFun;

/**
 * 
 * @author Ivan
 *
 * Clase principal de la máquina P
 */
public class Maquina_P {
	private Stack pila = new Stack();
	/*
	 * Habbrá que iicializar la memoria
	 */
	private ArrayList memoria = new ArrayList();
	private Long pc = new Long(0);
	private boolean halt = false;
	private boolean error = false;
	
	
	/**
	 * TODO:
	 * (dia 22) --
	 * Hay que crear un método que se encargue de procesar lo que se le pasa de entrada,
	 * es decir, si le aparece la palabra "apilar" debe llamar a la funcion apilar(num), 
	 * Así con el resto. Será una especie de case gigante! Una cosa más, hay que definir lo que
	 * vamos a hacer con los errores.
	 * Asi que queda:
	 * 
	 * 		HECHO>1º Analizador del String que nos devuelve la función declarada en AuxFun.java
	 * 		10%Hecho>2º Chequeo de errores y notificación apropiada. (Añadir una traza de Log o System.out)
	 * 		3º Leer un caracter?? de teclado? --> podemos usar la clase Scanner
	 * 		4º Escribir() ??? dónde escribimos?? por pantalla??
	 * 		HECHO>5º El arrayList "memoria" se debe inicializar, puede albergar diferentes tipos de objetos
	 * 		asi que podemos definir un caracter especial para indicar que está vacia la posicion.
	 *  
	 */
	
	public void inicializa_mem_pru(){
		for(int i=0;i<100;i++){
			memoria.add(i);
		}
	}
	
	public void procesa_programa(){
		String path = "c:/pru.txt";
		String programa = AuxFun.getTextoFichero(path);
		StringTokenizer enLineas = new StringTokenizer(programa,"\r\n");
		ArrayList lineas = new ArrayList();
		while (enLineas.hasMoreTokens()){
			String aux = enLineas.nextToken();
			lineas.add(aux);
		}
		//ahora en comandos y operandos
		ArrayList comandos = new ArrayList();
		Iterator itlineas = lineas.iterator();
		while(itlineas.hasNext()){
			String comando = (String) itlineas.next();
			ComandoVO c = new ComandoVO();
			StringTokenizer enSent = new StringTokenizer(comando," ");
			if(enSent.countTokens() > 2){
				System.out.println("Error demasiados parámetros");
			}else{
				if(enSent.countTokens() == 2){
					c.setAccion(enSent.nextToken());
					c.setOperando(enSent.nextToken());
					comandos.add(c);
				}else{
					c.setAccion(enSent.nextToken());
					c.setOperando(null);
					comandos.add(c);
				}
			}
		}
		//	Procesando los comandos
		Iterator itcomandos = comandos.iterator();
		while(itcomandos.hasNext()){
			ComandoVO com = (ComandoVO) itcomandos.next();
			String accion = com.getAccion();
			if(accion.equalsIgnoreCase("apila")){
				apilar(com.getOperando());
				System.out.println("apila");
			}else if(accion.equalsIgnoreCase("apila_dir")){
				apila_dir(Integer.parseInt(com.getOperando()));
			}else if(accion.equalsIgnoreCase("desapila")){
				desapila();
			}else if(accion.equalsIgnoreCase("desapila_dir")){
				desapila_dir(Integer.parseInt(com.getOperando()));
			}else if(accion.equalsIgnoreCase("suma")){
				suma();
			}else if(accion.equalsIgnoreCase("resta")){
				resta();
				System.out.println("resta");
			}else if(accion.equalsIgnoreCase("multiplica")){
				multiplica();
			}else if(accion.equalsIgnoreCase("divide")){
				divide();
			}else if(accion.equalsIgnoreCase("divide_real")){
				divide_real();
			}else if(accion.equalsIgnoreCase("modulo")){
				modulo();
			}else if(accion.equalsIgnoreCase("negacion")){
				negacion();
			}else if(accion.equalsIgnoreCase("mayor")){
				mayor();
			}else if(accion.equalsIgnoreCase("mayor_igual")){
				mayor_igual();
			}else if(accion.equalsIgnoreCase("igual")){
				igual();
			}else if(accion.equalsIgnoreCase("distintos")){
				distintos();
			}else if(accion.equalsIgnoreCase("ylogica")){
				ylogica();
			}else if(accion.equalsIgnoreCase("ologica")){
				ologica();
			}else if(accion.equalsIgnoreCase("notlogico")){
				notlogico();
			}else if(accion.equalsIgnoreCase("reed")){
				reed();
			}else if(accion.equalsIgnoreCase("masN")){
				masN();
			}else if(accion.equalsIgnoreCase("menosN")){
				menosN();
			}else if(accion.equalsIgnoreCase("escribir")){
				escribir();
			}else if(accion.equalsIgnoreCase("stop")){
				stop();
			}
			
		}
	}
	
	
	/**
	 * @param o
	 */
	public void apilar(Object o){
		pila.push(o);
		pc++;
	}
	public void apila_dir(int dir){
		if ((memoria.size() < dir) || (pila.isEmpty())){
			//Error -> fuera de memoria OR pila vacia
			// paramos la máquina
			halt = true;
			error = true;
			System.out.println("Out of memory!");
		}else{
			Object o = memoria.get(dir);
			pila.push(o);
			pc++;
		}
		
	}
	// ya la necesitaremos más adelante
	public void  desapila(){
		if (pila.isEmpty()){
			error = true;
			System.out.println("Pila vaciay!");
		}
		pila.pop();
		pc++;
	}
	
	public void desapila_dir(int dir){
		if ((memoria.size() < dir) || pila.isEmpty() ){
			halt = true;
			error = true;
			System.out.println("Out of memory! OR Pila vacia!");
		}else{
			String o = (String) pila.pop();
			memoria.set(dir, o);
			pc++;
		}
	}
	
	/*
	 *  COMPROBAR LOS TIPOS
	 */
	public void suma(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = a + b;
				pila.push(a);
				pc++;
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = a + b;
					pila.push(a);
					pc++;
				}else{
					error = true; 
					System.out.println("Elemento no numerico");
				}
			}
		}else{
			error = true;
			System.out.println("Pila vacia");
		}
	}
	
	public void resta(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = (String) pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b - a;
				pila.push(a);
				pc++;
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = b - a;
					pila.push(a);
					pc++;
				}else{
					error = true; 
					System.out.println("Elemento no numerico");
				}
			}
		}else{
			error = true;
			System.out.println("Pila vacia");
		}
	}
	
	public void multiplica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = a * b;
				pila.push(a);
				pc++;
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = a * b;
					pila.push(a);
					pc++;
				}else{
					error = true; 
					System.out.println("Elemento no numerico");
				}
			}
		}else{
			error = true;
		}
	}
	
	public void divide(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b / a;
				pila.push(a);
				pc++;
			}else{
					error = true; 
					System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	public void divide_real(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;	
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				a = a + b;
				pila.push(a);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	public void modulo(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b % a;
				pila.push(a);
				pc++;
			}else{
					error = true; 
					System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	public void negacion(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if(esBooleano(cima)){
				Boolean b = Boolean.valueOf(cima);
				b = !b;
				pila.push(b);
			}
			pc++;
		}else{
			error = true;
		}
	}
	
	public void mayor(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b > a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	public void mayor_igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b >= a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	public void menor(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b < a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	public void menor_igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b <= a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	public void igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			Boolean res = subcima.equals(cima);
			pila.push(res);
			pc++;
		}else{
			error = true;
		}
	}
	
	public void distintos(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			Boolean res = !subcima.equals(cima);
			pila.push(res);
			pc++;
		}else{
			error = true;
		}
	}
	
	public void ylogica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if(esBooleano(cima) && esBooleano(subcima)){
				Boolean a = Boolean.valueOf(cima);
				Boolean b = Boolean.valueOf(subcima);
				Boolean c = a && b;
				pila.push(c);
				pc++;
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	
	public void ologica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if(esBooleano(cima) && esBooleano(subcima)){
				Boolean a = Boolean.valueOf(cima);
				Boolean b = Boolean.valueOf(subcima);
				Boolean c = a || b;
				pila.push(c);
				pc++;
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	
	public void notlogico(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			if(esBooleano(cima)){
				Boolean a = Boolean.valueOf(cima);
				pila.push(!a);
				pc++;
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	
	//obtiene el valor de la cima
	public void reed(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			/*
			 * ¿qué hago?
			 */
			pc++;
		}else{
			error = true;
		}
	}
	
	public void masN(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if (esEntero(cima)){
				Integer i = Integer.parseInt(cima);
				if (i < 0){
					i = -1 * i;
				}
				pila.push(i);
				pc++;
			}else{
				if(esReal(cima)){
					Float f = Float.valueOf(cima);
					if (f < 0){
						f = -1*f;
					}
					pila.push(f);
					pc++;
				}
			}
		}else{
			error = true;
		}
	}
	
	public void menosN(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if (esEntero(cima)){
				Integer i = Integer.parseInt(cima);
				if (i > 0){
					i = -1 * i;
				}
				pila.push(i);
				pc++;
			}else{
				if(esReal(cima)){
					Float f = Float.valueOf(cima);
					if (f > 0){
						f = -1*f;
					}
					pila.push(f);
					pc++;
				}
			}
		}else{
			error = true;
		}
	}
	
	public void escribir(){
		if (pila.size() > 1){
			String cima = pila.peek().toString();
			/*
			 * pintamos donde?
			 */
			System.out.print(cima);
			pc++;
		}else{
			error = true;
		}
	}
	
	public void stop(){
		halt = true;
	}
	
	/*------------------------------------------------------------------------
	 * Funciones auxiliares de chequeo de tipos
	 * -----------------------------------------------------------------------
	 */
	
	private boolean esEntero(String o){
		try{
			Integer a = Integer.valueOf(o.toString());
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean esReal(String o){
		try{
			Float f = Float.valueOf(o.toString());
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean esBooleano(String o){
		try{
			Boolean b = Boolean.valueOf(o.toString());
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
}
