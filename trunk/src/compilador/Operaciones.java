package compilador;
import java.util.ArrayList;
import java.util.Stack;


public class Operaciones {

	public static void main(String[] args) {
		String path = "c:/pru.txt";
		Maquina_P maq = new Maquina_P();
		maq.inicializa_mem_pru();
		maq.procesa_programa();
	}
}
