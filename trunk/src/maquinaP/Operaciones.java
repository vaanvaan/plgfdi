package maquinaP;
import java.util.ArrayList;
import java.util.Stack;



public class Operaciones {

	public static void main(String[] args) {
		Maquina_P_v2 maq = new Maquina_P_v2();
		maq.setPath("c:/prueba.bin");
		maq.inicializa_mem_pru();
		maq.setDebug(false); // <--- modo debug
		maq.procesa_programa();
	}
}
