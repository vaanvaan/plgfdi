package maquinaP;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

import compilador.AuxFun;
/*
 * Int�rprete / simulador de la m�quina P. 
 * Este int�rprete tambi�n funcionar� en modo l�nea de comandos,
 * tomando como argumento el archivo donde est� el c�digo P a ejecutar,
 * as� como un flag que indicar� si la ejecuci�n debe ser en modo normal o en modo traza.
 * Como resultado ejecutar� dicho c�digo:
 * 	En modo normal el int�rprete no mostrar� ning�n tipo de traza. De esta forma, 
 * 		el �nico resultado visible ser� el producido por las instrucciones espec�ficas de lectura / escritura
 * 		incluidas en el lenguaje de la m�quina P.
 * 	En modo traza el int�rprete visualizar� los distintos pasos de ejecuci�n.
 * 		En cada paso el int�rprete: (1) mostrar� los contenidos de la pila de ejecuci�n y de las celdas relevantes
 * 		en la memoria de datos, (2) esperar� a que el usuario decida proseguir la ejecuci�n, (3) mostrar� la instrucci�n a ejecutar,
 * 		y la ejecutar�, avanzando, de esta manera, al siguiente paso. 
 *		En cualquier caso, la traza se mostrar� siempre por la consola de la l�nea de comandos. 
 */
/**
 * 
 * @author GRUPO PLG N�13
 *
 * Clase principal de la m�quina P
 * 
 */
public class Maquina_P{
	
	
	private Stack pila = new Stack();
	/*
	 * Habbr� que iicializar la memoria
	 */
	private ArrayList memoria = new ArrayList();
	private ArrayList memoriaref = new ArrayList();
	private Long pc = new Long(0);
	private boolean halt = false;
	private boolean error = false;
	private boolean debug = false;
	private String path;
	private int TAM_MEM_ESTATICA = 100;
	private int TAM_HEAP = 300;
	
	
	/**
	 * M�todo que se encarga de pintar el estado de la m�quina P cuando el modo debug est� habilitado.
	 * 
	 * @param op	opreraci�n que se aplicar� sobre la m�quina P.
	 * @param oper	operando que acompa�a a la operaci�n.
	 */
	public void print_state(String op, String oper){
		
		if(debug){
			Iterator it = memoriaref.iterator();
			ArrayList memref = new ArrayList();
			Map<String,String> mapmem = new HashMap<String,String>();
			
			System.out.println("=============================");
			System.out.println("OPERACION: "+ op + " " + oper);
			System.out.print("STACK: ");
			System.out.println(pila);
			
			while (it.hasNext()){
				String aux = it.next().toString();
				if (esEntero(aux)){
					int pos = Integer.parseInt(aux);
					mapmem.put("M" + aux,memoria.get(pos).toString());
				}
			}
			System.out.println("MEMORY: " + mapmem);	
			System.out.println("PC = "+(pc-1));
		}
	}
	
	/**
	 * Este m�todo se encarga de inicializar las posiciones dememoria.
	 */
	public void inicializa_mem_pru(){
		for(int i=0; i<TAM_MEM_ESTATICA; i++){
			String a = "#";
			memoria.add(a);
		}
//		for(int i=0; i<TAM_HEAP; i++){
//			String a = "#";
//			heap.add(a);
//		}
	}
	
	/**
	 * M�todo que se encarga de procesar el fichero de las instrucciones generado por el analizador sint�ctico.
	 * Su funcionamiento es s�mple, como cada instrucci�n se despliega sobre una linea, tomamos esa linea, y
	 * analizamos a qu� fuci�n llamar.
	 */
	public void procesa_programa(){
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
				System.out.println("Error demasiados par�metros");
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
//		Iterator itcomandos = comandos.iterator();
//		while(itcomandos.hasNext() && !halt && !error){
		boolean fin = pc >= comandos.size();
		while(!fin && !halt && !error){
//			ComandoVO com = (ComandoVO) itcomandos.next();
			ComandoVO com = (ComandoVO) comandos.get(pc.intValue());
			String accion = com.getAccion();
			pc++;
			if(debug){
				//esperamos a que nos digan que ejecutemos:
				if(com.getOperando() != null){
					System.out.println("Instruccion: "+ com.getAccion() + " "+com.getOperando().toString());
				}else{
					System.out.println("Instruccion: "+ com.getAccion());
				}
				System.out.println("Seguir? [Y/N]");
				Scanner scan = new Scanner(System.in);
				String cad = scan.nextLine();
				if (cad.equalsIgnoreCase("n")){
					halt = true;
				}
			}
			if(error){
				halt = true;
				System.out.println("ERROR EN EJECUCION");
				System.out.println("PARADA DEL SISTEMA");
			}
			
			if(accion.equalsIgnoreCase("apila")){
				apilar(com.getOperando());
				print_state("apila", com.getOperando());
			}else if(accion.equalsIgnoreCase("apila-dir")){
				apila_dir(Integer.parseInt(com.getOperando()));
				if(!memoriaref.contains(com.getOperando())){
					memoriaref.add(com.getOperando());
				}
				print_state("apila_dir", com.getOperando());
			}else if(accion.equalsIgnoreCase("apila-ind")){
				apila_ind();
//				if(!memoriaref.contains(com.getOperando())){
//					memoriaref.add(com.getOperando());
//				}
				print_state("apila_ind", com.getOperando());
			}else if(accion.equalsIgnoreCase("desapila")){
				desapila();
				print_state("desapila","");
			}else if(accion.equalsIgnoreCase("desapila-dir")){
				desapila_dir(Integer.parseInt(com.getOperando()));
				if(!memoriaref.contains(com.getOperando())){
					memoriaref.add(com.getOperando());
				}
				print_state("desapila_dir",com.getOperando());
			}else if(accion.equalsIgnoreCase("desapila-ind")){
				desapila_ind();
//				if(!memoriaref.contains(com.getOperando())){
//					memoriaref.add(com.getOperando());
//				}
				// tener cuidado con la memoria que se referencia
				print_state("desapila_ind",com.getOperando());
			}else if(accion.equalsIgnoreCase("suma")){
				suma();
				print_state("suma","");
			}else if(accion.equalsIgnoreCase("resta")){
				resta();
				print_state("resta","");
			}else if(accion.equalsIgnoreCase("multiplica")){
				multiplica();
				print_state("multiplica","");
			}else if(accion.equalsIgnoreCase("divide")){
				divide();
				print_state("divide","");
			}else if(accion.equalsIgnoreCase("divide_real")){
				divide_real();
				print_state("divide_real","");
			}else if(accion.equalsIgnoreCase("modulo")){
				modulo();
				print_state("modulo","");
			}else if(accion.equalsIgnoreCase("negativo")){
				negativo();
				print_state("negativo","");
			}else if(accion.equalsIgnoreCase("mayor")){
				mayor();
				print_state("mayor","");
			}else if(accion.equalsIgnoreCase("mayor_igual")){
				mayor_igual();
				print_state("mayor_igual","");
			}else if(accion.equalsIgnoreCase("menor")){
				menor();
				print_state("menor","");
			}else if(accion.equalsIgnoreCase("menor_igual")){
				menor_igual();
				print_state("menor_igual","");
			}else if(accion.equalsIgnoreCase("igual")){
				igual();
				print_state("igual","");
			}else if(accion.equalsIgnoreCase("distintos")){
				distintos();
				print_state("distintos","");
			}else if(accion.equalsIgnoreCase("ylogica")){
				ylogica();
				print_state("ylogica","");
			}else if(accion.equalsIgnoreCase("ologica")){
				ologica();
				print_state("ologica","");
			}else if(accion.equalsIgnoreCase("not")){
				notlogico();
				print_state("not","");
			}else if(accion.equalsIgnoreCase("read")){
				read();
				print_state("read","");
			}else if(accion.equalsIgnoreCase("masN")){
				masN();
				print_state("masN","");
			}else if(accion.equalsIgnoreCase("menosN")){
				menosN();
				print_state("menosN","");
			}else if(accion.equalsIgnoreCase("write")){
				escribir();
				print_state("write","");
			}else if(accion.equalsIgnoreCase("copia")){
				copia_cima();
				print_state("copia cima","");
			}else if(accion.equalsIgnoreCase("ir-a")){
				ir_a(new Long(com.getOperando().toString()));
				print_state("ir_a",com.getOperando().toString());
			}else if(accion.equalsIgnoreCase("ir-f")){
				ir_f(new Long(com.getOperando().toString()));
				print_state("ir_f",com.getOperando().toString());
			}else if(accion.equalsIgnoreCase("ir-v")){
				ir_v(new Long(com.getOperando().toString()));
				print_state("ir_v",com.getOperando().toString());
			}else if(accion.equalsIgnoreCase("ir-ind")){
				ir_ind();
				print_state("ir_ind","");
			}else if(accion.equalsIgnoreCase("new")){
				heap_new(Integer.parseInt(com.getOperando().toString()));
				print_state("new",com.getOperando().toString());
			}else if(accion.equalsIgnoreCase("del")){
				heap_del(Integer.parseInt(com.getOperando().toString()));
				print_state("del",com.getOperando().toString());
			}else if(accion.equalsIgnoreCase("mueve")){
				mueve(Integer.parseInt(com.getOperando().toString()));
				print_state("mueve",com.getOperando().toString());
			}else if(accion.equalsIgnoreCase("stop")){
				stop();
				print_state("stop","");
			}
			
		}
		if(error){
			halt = true;
			System.out.println("ERROR EN EJECUCION");
			System.out.println("PARADA DEL SISTEMA");
		}
	}
	
	
	/**
	 * M�todo que realiza la operaci�n de apilar sobre la pila de la m�quina
	 * 
	 * @param o	 este es el objeto que se colocar� en la cima de la pila.
	 */
	public void apilar(Object o){
		pila.push(o);
	}
	/**
	 * M�todo que realiza la operaci�n de apilar un elemento almacenado en memoria
	 * 
	 * @param dir	direcci�n de memoria del que tomamos el dato.
	 */
	public void apila_dir(int dir){
		if ((memoria.size() < dir)){
			//Error -> fuera de memoria paramos la m�quina
			halt = true;
			error = true;
			System.out.println("Out of memory!");
		}else{
			Object o = memoria.get(dir);
			if(o.toString().equals("#")){
				halt = true;
				error = true;
				System.out.println("MQ_P/> Memory access vioaltion! Posicion de memoria inv�lida.");
				System.out.println("MQ_P/> Instrucci�n:"+ (pc-1));
				System.out.println("MQ_P/> Posici�n referneciada:" + dir);
				System.out.println("MQ_P/> Operaci�n que produjo el error:"+" apila_dir("+dir+")");
			}else{
				pila.push(o);
			}
		}
	}
	/**
	 * Interpreta el valor d en la cima de la pila como un n�mero
	 * de celda en la memoria, y substituye dicho valor por el almacenado en dicha
	 * celda.
	 * Pila[ST] <- Mem[Pila[ST]]
	 * PC <- PC+1
	 */
	public void apila_ind(){
		if (pila.isEmpty()){
			error=true;
			System.out.println("Pila vacia!");
		}else{
			//tomamos el valor de la cima como la direcci�n
			String cima = pila.pop().toString();
			if (esEntero(cima)){
				Integer dir_mem = Integer.parseInt(cima);
				String valor = memoria.get(dir_mem).toString();
				if(valor.equals("#")){
					halt = true;
					error = true;
					System.out.println("MQ_P/> Memory access vioaltion! Posicion de memoria inv�lida.");
					System.out.println("MQ_P/> Instrucci�n:"+ (pc-1));
					System.out.println("MQ_P/> Posici�n referneciada:" + dir_mem);
					System.out.println("MQ_P/> Operaci�n que produjo el error:"+" apila_dir("+dir_mem+")");
				}else{
					pila.push(valor);
				}
			}else{
				halt = true;
				error = true;
				System.out.println("Out of memory! OR Pila vacia!");
			}
		}
	}
	/**
	 * M�todo que elimina el elemento situado en la cima de la pila, quedando en la cima
	 * el elemnto inmediatamente inferior. 
	 */
	public void  desapila(){
		if (pila.isEmpty()){
			error = true;
			System.out.println("Pila vaciay!");
		}
		pila.pop();
	}
	
	/**
	 * M�todo que se encarga de realizar la operaci�n de desapilar el elemto en la cima
	 * de la pila y colocarlo en la posici�n de memoria especificada.
	 * 
	 * @param dir	posici�n de memoria donde se guardar� del dato de la cima. 
	 */
	public void desapila_dir(int dir){
		if ((memoria.size() < dir) || pila.isEmpty() ){
			halt = true;
			error = true;
			System.out.println("Out of memory! OR Pila vacia!");
		}else{
			String o = pila.pop().toString();
			memoria.set(dir, o);
		}
	}
	
	/**
	 * Desapila el valor de la cima v y la subcima d, interpreta d
	 * como un n�mero de celda en la memoria, y almacena v en dicha celda.
	 * Mem[Pila[ST-1]] <- Pila[ST]
	 * ST<-ST-2  (la pilase queda sin 2 elementos)
	 * PC <-PC+1
	 */
	public void desapila_ind(){
		if (pila.size() < 2) {
			halt = true;
			error = true;
			System.out.println("Out of memory! OR Pila vacia!");
		}else{
			String cima = pila.pop().toString();
			String subcima = pila.pop().toString();
			if (esEntero(subcima)) {
				Integer dir = Integer.parseInt(subcima);
				if(dir > memoria.size()){
					System.out.println("MQ_P/> Memory access vioaltion! Posicion de memoria inv�lida.");
					System.out.println("MQ_P/> Instrucci�n:"+ (pc-1));
					System.out.println("MQ_P/> Posici�n referneciada:" + dir);
					System.out.println("MQ_P/> Operaci�n que produjo el error:"+" apila_ind()");
				}else{
					memoria.set(dir, cima);
				}
			} else {
				halt = true;
				error = true;
				System.out.println("Dato no numerico!");
			}
		}
	}
	
	/**
	 * Dicha instrucci�n encuentra en la cima la direcci�n origen o y en la
	 * subcima la direcci�n destino d, y realiza el movimiento de s celdas desde o a s.
	 * para i<-0 hasta s-1 hacer
	 * Mem[Pila[ST-1]+i] <- Mem[Pila[ST]+i]
	 * ST<-ST-2
	 * PC <- PC+1
	 * 
	 * @param num_celdas	Numero de celdas a desplazar
	 */
	public void mueve(int num_celdas){
		if (pila.size() < 2) {
			halt = true;
			error = true;
			System.out.println("La pila se queda vacia!");
		} else {
			String cima = pila.pop().toString();
			String subcima = pila.pop().toString();
			if (esEntero(cima) && esEntero(subcima)){
				Integer origen = Integer.parseInt(cima);
				Integer destino = Integer.parseInt(subcima);
				// habra q comprobar q son positivos...(o no?)
				if((origen < memoria.size())&&(destino < memoria.size())){
					for (int i = 0; i < num_celdas; i++) {
						String obj = memoria.get(origen+i).toString();
						memoria.set((destino+i), obj);
					}
				}
			}else{
				halt = true;
				error = true;
				System.out.println("La pila se queda vacia!");
			}
		}
	}
	
	/**
	 * Salto incondicional a la instrucci�n i.
	 * 
	 * @param ci el nuevo valor del contador de instrucciones
	 */
	public void ir_a(Long ci){
		pc = ci;
	}
	
	/**
	 * Desapila el valor de la cima de la pila. Si es 0
	 * (falso), salta a la instrucci�n i. En otro caso, sigue la
	 * ejecuci�n en secuencia.
	 * 
	 * @param ci	nuevo contador de instrucciones
	 */
	public void ir_f(Long ci){
		if(ci >0){
			String cima = pila.pop().toString();
			if(esBooleano(cima)){
				if(esBooleano(cima)){
					if(cima.trim().equalsIgnoreCase("false")){
						pc = ci;
					}
				}
			}else{
				halt = true;
				error = true;
				System.out.println("Cima no numerica!");
			}
		}
	}
	
	/**
	 * Desapila el valor de la cima de la pila. Si es 1
	 * (verdadero), salta a la instrucci�n i. En otro caso, sigue la
	 * ejecuci�n en secuencia.
	 * 
	 * @param ci		nuevo contador de instrucciones
	 */
	public void ir_v(Long ci){
		if(ci >0){
			String cima = pila.pop().toString();
			if(esBooleano(cima)){
				Boolean cond = Boolean.getBoolean(cima);
				if(cond){
					pc = ci;
				}
			}else{
				halt = true;
				error = true;
				System.out.println("Cima no numerica!");
			}
		}
	}
	
	/**
	 * Esta funci�n se encarga de realizar un salto a la direcci�n que apunta la cima de la pila
	 * consumi�ndo la cima.
	 */
	public void ir_ind(){
		if (!pila.isEmpty()) {
			String obj = pila.pop().toString();
			if(esEntero(obj)){
				Long dir = new Long(obj);
				pc = dir;
			}else{
				halt = true;
				error = true;
				System.out.println("Error en salto de instruccion");
			}
		}else{
			halt = true;
			error = true;
			System.out.println("Pila vacia");
		}
	}
	/**
	 * Copia el vlor de la cima
	 */
	public void copia_cima(){
		if (!pila.isEmpty()) {
			String obj1 = pila.pop().toString();
			String obj2 = obj1;
			pila.push(obj1);
			pila.push(obj2);
		}else{
			halt = true;
			error = true;
			System.out.println("Pila vacia");
		}
	}
	
	/**
	 * M�todo que se encarga de actualizar el tama�o de segmento de memoria estatica.
	 * El valor por defecto es 100
	 * 
	 * @param tam		tama�o deseado de segmento
	 */
	public void seg(int tam){
		TAM_MEM_ESTATICA = tam;
		if(tam < TAM_MEM_ESTATICA){
			memoria = (ArrayList) memoria.subList(0, TAM_MEM_ESTATICA-1);
		}else if (tam > TAM_MEM_ESTATICA) {
			while(TAM_MEM_ESTATICA < tam){
				String a = "#";
				memoria.add(a);
				TAM_MEM_ESTATICA++;
			}
		}
		
	}
	
	/**
	 * Reserva espacio en el heap para t celdas consecutivas y apila
	 * en la cima de la pila la direcci�n de comienzo.
	 * Direccion de comienzo:
	 * 
	 * 		|base|...|elem1|elem2| ....
	 * 					^inicio
	 * 
	 * @param nceldas		numero de celdas
	 */
	public void heap_new(int nceldas){
		int ini = memoria.size();
		if(ini+nceldas> TAM_HEAP){
			error=true;
			halt=true;
			System.out.println("MEMORY ERROR");
			System.out.println("HEAP supera el tama�o m�ximo.");
			System.out.println("Tama�o actual del Heap: "+ TAM_HEAP);
		}else{
			pila.push(String.valueOf(ini));
			for (int i = 0; i < nceldas; i++) {
				String a = "#";
				memoria.add(a);
			}
		}
	}
	
	/**
	 * Desapila una direcci�n de comienzo d de la cima de la pila, y
	 * libera en el heap t celdas consecutivas a partir de d.
	 * 
	 * @param nceldas		numero de celdas
	 */
	public void heap_del(int nceldas){
		if ((memoria.size()<=TAM_MEM_ESTATICA) || (nceldas > memoria.size()) || pila.isEmpty()) {
			halt = true;
			error = true;
			System.out.println("Heap/Pila vacio/a!");
		} else {
			String cima = pila.pop().toString();
			if(esEntero(cima)){
				Integer dir = Integer.parseInt(cima);
				try{
					for (int i = 0; i < nceldas; i++) {
						memoria.remove((dir+i));
					}
				}catch (Exception e) {
					halt = true;
					error = true;
					System.out.println("Out of bounds access HEAP!");
				}
			}else{
				halt = true;
				error = true;
				System.out.println("Cima no numerica!");
			}
		}
	}
	
	/**
	 * M�todo que se encarga de realizar la suma, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void suma(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();
			String subcima = pila.pop().toString();
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = a + b;
				pila.push(a);
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = a + b;
					pila.push(a);
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
	
	/**
	 * M�todo que se encarga de realizar la resta, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La resta se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA - CIMA
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO ser�a el resultado de la operaci�n de resta y ser�a la nueva cima de la pila.
	 */
	public void resta(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = (String) pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b - a;
				pila.push(a);
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = b - a;
					pila.push(a);
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
	
	/**
	 * M�todo que se encarga de realizar la multiplicaci�n, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void multiplica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = a * b;
				pila.push(a);
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = a * b;
					pila.push(a);
				}else{
					error = true; 
					System.out.println("Elemento no numerico");
				}
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de realizar la divisi�n entera, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La divsi�n se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA / CIMA
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO ser�a el resultado de la operaci�n de divisi�n entera y ser�a la nueva cima de la pila.
	 */
	public void divide(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b / a;
				pila.push(a);
			}else{
					error = true; 
					System.out.println("Elemento no entero");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de realizar la divisi�n real, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La divis�n se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA / CIMA
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO ser�a el resultado de la operaci�n de divisi�n real y ser�a la nueva cima de la pila.
	 */
	public void divide_real(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;	
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				a = b / a;
				pila.push(a);
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de realizar el m�dulo, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La operacion se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA % CIMA    (mod = %)
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO ser�a el resultado de la operaci�n de divisi�n entera y ser�a la nueva cima de la pila.
	 */
	public void modulo(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b % a;
				pila.push(a);
			}else{
					error = true; 
					System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar la operaci�n del operador unario "-"
	 * para indicar que un elemnto es negativo.
	 * Esta operaci�n se aplica sobre el elemento situado en la cima de la pila.
	 */
	public void negativo(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if(esEntero(cima)){
				int a = Integer.parseInt(cima);
				if(a>0){
					a = a*(-1);
				}
				pila.push(a);
			}else if(esReal(cima)){
				float a = Float.parseFloat(cima);
				if(a>0){
					a = a*(-1);
				}
				pila.push(a);
			}
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar la operaci�n de comparaci�n mayor, aplic�ndose del
	 * siqguiente modo:
	 * 		??? = SUBCIMA > CIMA
	 */
	public void mayor(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b > a;
				pila.push(c);
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar la operaci�n de comparaci�n mayor-igual, aplic�ndose del
	 * siqguiente modo:
	 * 		??? = SUBCIMA >= CIMA
	 */
	public void mayor_igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b >= a;
				pila.push(c);
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar la operaci�n de comparaci�n menor, aplic�ndose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA < CIMA
	 */
	public void menor(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b < a;
				pila.push(c);
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar la operaci�n de comparaci�n menor-igual, aplic�ndose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA <= CIMA
	 */
	public void menor_igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b <= a;
				pila.push(c);
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de realizar la operaci�n de comparaci�n de igualdad, aplic�ndose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA = CIMA
	 */
	public void igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			Boolean res = subcima.equals(cima);
			pila.push(res);
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de realizar la operaci�n de comparaci�n de desigualdad, aplic�ndose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA <> CIMA
	 */
	public void distintos(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			Boolean res = !subcima.equals(cima);
			pila.push(res);
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar la AND l�gica, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void ylogica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if(esBooleano(cima) && esBooleano(subcima)){
				Boolean a = Boolean.valueOf(cima);
				Boolean b = Boolean.valueOf(subcima);
				Boolean c = a && b;
				pila.push(c);
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar la OR l�gica, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void ologica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if(esBooleano(cima) && esBooleano(subcima)){
				Boolean a = Boolean.valueOf(cima);
				Boolean b = Boolean.valueOf(subcima);
				Boolean c = a || b;
				pila.push(c);
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	/**
	 * M�todo que se encarga de realizar el NOT l�gico, si procede, del elemento situado
	 * en la cima de la pila.
	 */
	public void notlogico(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			if(esBooleano(cima)){
				Boolean a = Boolean.valueOf(cima);
				pila.push(!a);
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Este m�todo se encarga de implementar el comando de lectura.
	 * Se encarga de leer toda la linea que se le introduce por entrada y la almacena en
	 * la cima de la pila.
	 */
	public void read(){
		System.out.print("MAQ_P/>");
		Scanner scan = new Scanner(System.in);
		String linea = scan.nextLine();
		//chequeo de si es un dato aceptable
		boolean aceptable = esBooleano(linea) || esEntero(linea) || esReal(linea) || linea.length()==1;
		while (!aceptable){
			System.out.println("MAQ_P/> Dato introducido incompatible. Datos aceptados: Eteros, Reales, Booleanos y Caracteres.");
			System.out.print("MAQ_P/>");
			linea = scan.nextLine();
			aceptable = esBooleano(linea) || esEntero(linea) || esReal(linea) || linea.length()==1;
		}
		if(linea.length()==1){
			if(!esEntero(linea)&& !esReal(linea)) linea = "'"+linea+"'";
		}
		pila.push(linea);
	}
	/**
	 * M�todo que se encarga de convertir un n�mero en un n�mero de signo positivo.
	 * Esta operaci�n se aplica sobre el elemento situado en la cima de la pila.
	 */
	public void masN(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if (esEntero(cima)){
				Integer i = Integer.parseInt(cima);
				if (i < 0){
					i = -1 * i;
				}
				pila.push(i);
			}else{
				if(esReal(cima)){
					Float f = Float.valueOf(cima);
					if (f < 0){
						f = -1*f;
					}
					pila.push(f);
				}
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de convertir un n�mero en un n�mero de signo negativo.
	 * Esta operaci�n se aplica sobre el elemento situado en la cima de la pila.
	 */
	public void menosN(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if (esEntero(cima)){
				Integer i = Integer.parseInt(cima);
				if (i > 0){
					i = -1 * i;
				}
				pila.push(i);
			}else{
				if(esReal(cima)){
					Float f = Float.valueOf(cima);
					if (f > 0){
						f = -1*f;
					}
					pila.push(f);
				}
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de implementar la escritura, muestreo del elementto situado
	 * en la cima de la pila.
	 */
	public void escribir(){
		if (!pila.empty()){
			String cima = pila.peek().toString();
			System.out.println("MQ_P/>"+cima);
		}else{
			error = true;
		}
	}
	
	/**
	 * M�todo que se encarga de parar la m�quina P.
	 */
	public void stop(){
		halt = true;
	}
	
	/**
	 * M�todo que chequea si la m�quina est� parada.
	 * @return  estado del flag Halt (true/false)
	 */
	public boolean isHalt() {
		return halt;
	}
	/**
	 * M�todo que se encarga de fijar el flag de parada.
	 * @param halt
	 * Valor que le asignamos al flag.
	 */
	public void setHalt(boolean halt) {
		this.halt = halt;
	}
	/**
	 * M�todo que nos indica si se ha producido un error en
	 * el procesamieto.
	 * @return (true/false) dependiendo si se ha producido error o no.
	 */
	public boolean isError() {
		return error;
	}

	protected void setError(boolean error) {
		this.error = error;
	}
	/**
	 * M�todo que indica si se encuentra la m�quina en modo debug o no.
	 * @return
	 * (true/false) dependiendo del flag.
	 */
	public boolean isDebug() {
		return debug;
	}
	/**
	 * M�todo que establece el modo debug.
	 * @param debug  Habilitar� o deshabilitar� el modo debug.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	/**
	 * M�todo que informa sobre la ruta del fichero a procesar.
	 * @return  la ruta del fichero.
	 */
	public String getPath() {
		return path;
	}
	/**
	 * M�todo que estblece el la ruta del fichero a procesar
	 * @param path ruta del fichero.
	 */
	public void setPath(String path) {
		this.path = path;
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
			if(!o.toString().equalsIgnoreCase("true") && !o.toString().equalsIgnoreCase("false")){
				return false;
			}
			Boolean b = Boolean.valueOf(o.toString());
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String orden;
		boolean debug=false;
		Scanner scanEntrada = new Scanner(System.in);
		do{
			System.out.print("MQ_P/> Modo debug si/no : ");
			orden = scanEntrada.nextLine();
		}while(orden.toLowerCase().compareTo("si")!=0&&orden.toLowerCase().compareTo("no")!=0);
		if(orden.toLowerCase().compareTo("si")==0){
			debug = true;
		}
		System.out.print("MQ_P/> Path del bytecode: ");
		orden = scanEntrada.nextLine();
		Maquina_P maq = new Maquina_P();
		maq.setPath(orden);
		maq.inicializa_mem_pru();
		maq.setDebug(debug); // <--- modo debug
		maq.procesa_programa();
	}
}
