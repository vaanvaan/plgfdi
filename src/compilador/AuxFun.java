package compilador;

import  java.io.*;

/**
 * @author DaNieLooP
 *
 */
public class AuxFun {
	
	/** Dado un string con el nombre de un fichero, devuelve otro string con el contenido
	 * de ese mismo fichero. Esta funcion sirve para automatizar todo el proceso de extraccion
	 * de caracteres, usado en el analizador lexico y en la maquina-P.
	 * 
	 * @param nombreFichero Ruta del fichero del que se va a extraer la informacion.
	 * @return Se devuelve la cadena asociada al fichero.
	 */
	public static String getTextoFichero(String nombreFichero){
        try{
                /**
                 *  Apertura del archivo.
                 */
                File file = new File(nombreFichero);
                /** 
                 * Tamaño del archivo que se abre.
                 */
                int size = (int) file.length();
                /**
                 * Contador del nº de caracteres del archivo que se han leido ya.
                 */
                int chars_read = 0;
                /**
                 * Se crea un input FileReader para leer el archivo.
                 */
                FileReader in = new FileReader(file);
                /**
                 * Array de caracteres que se usa para leer el contenido del archivo.
                 */
                char[] data = new char[size];
                /**
                 * Se leen los todos los caracteres del archivo.
                 */
                while(in.ready()){
                        chars_read += in.read(data,chars_read,size-chars_read);
                }
                /**
                 * Se cierra el fichero.
                 */
                in.close();
                /**
                 * Se devuelve un string con todo el contenido del archivo.
                 */
                return (new String(data,0,chars_read));
        }
        catch(IOException e){
                System.out.println("Error en la apertura de: "+ nombreFichero);
                return null;
        }
}

}
