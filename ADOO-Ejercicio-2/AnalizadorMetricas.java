import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.util.*;

public class AnalizadorMetricas
{
	ArrayList <String> subclases;
	ArrayList <String> clases;
	ArrayList <String> metodos;
	ArrayList <Integer> COM;
	File file;
	File reporte;
	int comSuma, numClases;
	int [] NOC;
	int [] DIT;
	boolean [][] arbol;
	String clase;
	String nombreArchivo;

	public AnalizadorMetricas ( String ruta ) throws IOException 
	{
		String palabra;
		file = new File(ruta);
		clases = new ArrayList <String>();
		COM = new ArrayList <Integer>();
		metodos = new ArrayList <String> ();
		numClases = comSuma = 0;
		nombreArchivo = ruta.substring(0, ruta.length() - 5);

		generarReporte();
		analizarArchivo();
	}

	//Comienza el analisis de un archivo
	public void analizarArchivo ( ) throws IOException 
	{
		buscarClases();
		crearContadorHijos();
		crearArbol();

		for (int i = 0; i < numClases; i++)
			analizarClase(clases.get(i));
		
		for (int i = 0; i < numClases; i++)
		{
			determinarDIT(clases.get(i));
			buscarMetodos(clases.get(i));
		}
	}

	//Busca las clases en el archivo dado
	public void buscarClases ( ) throws IOException 
	{
		Scanner scan = new Scanner(file);
		String palabra = scan.next();

		while (scan.hasNextLine())
		{
			if (palabra.equals("class"))
				clases.add(scan.next());
			
			palabra = scan.next();
		}

		numClases = clases.size();		
	}

	//Se crea el arreglo donde se tendra el numero de hijos de la clase
	public void crearContadorHijos ( )
	{
		NOC = new int[numClases];
		
		for (int i = 0; i < NOC.length; i++)
			NOC[i] = 0;
	}

	//Se crea el arbol de herencia
	public void crearArbol ( )
	{
		arbol = new boolean[numClases][numClases];
		DIT = new int[numClases];
		

		for (int i = 0; i < arbol.length; i++)
			for (int j = 0; j < arbol.length; j++)
				arbol[i][j] = false;

		for (int i = 0; i < DIT.length; i++)
			DIT[i] = 0;
	}

	//Analiza una clase del archivo
	public void analizarClase ( String clase) throws IOException
	{
		Scanner scan = colocarFichero(clase);
		String palabra = scan.next();

		while (scan.hasNextLine() && !palabra.equals("{"))
		{
			if (palabra.equals("extends")) //Se determina si la clase hereda a otra
			{
				palabra = scan.next();

				NOC[clases.indexOf(palabra)]++;
				arbol[clases.indexOf(clase)][clases.indexOf(palabra)] = true;
			}
			palabra = scan.next();
		}
	}

	//Busca los metodos en la clase de un archivo
	public void buscarMetodos ( String clase) throws IOException
	{
		Stack <String> llaves = new Stack <String>();
		String palabra = "";
		String metodo;
		metodos.clear();
		COM.clear();

		Scanner scan = colocarFichero(clase);

		while (!palabra.equals("{"))
			palabra = scan.next();
		
		llaves.push("{");
		while (scan.hasNextLine() && !llaves.isEmpty()) //El proceso de repite hasta alcanzar el ultimo corchete de la clase
		{
			metodo = palabra;
			palabra = scan.next();

			if (palabra.equals("{"))
				llaves.push("{");
			else if (palabra.equals("}"))
				llaves.pop();
			else if (palabra.equals("class"))
				descartarClase(scan);
			else if (palabra.equals("("))
				metodos.add(metodo);
		}

		for (int i = 0; i < metodos.size(); i++)
			analizarMetodo(metodos.get(i));	

		reportePorClase(clase);
	}

	//Analiza la iformacion de un metodo, siguiendo la misma metodologia de los corchetes
	public void analizarMetodo ( String metodo) throws IOException
	{
		Stack <String> llaves = new Stack <String>();
		String palabra = "";
		String sentencia = "";
		Scanner scan;
		comSuma = 0;

		scan = colocarFichero(metodo);

		if (clases.contains(metodo))
			while (!palabra.equals(metodo))
				palabra = scan.next();

		while (!palabra.equals("{"))
			palabra = scan.next();

		llaves.push("{");

		while (!llaves.isEmpty())
		{
			sentencia = palabra;
			palabra = scan.next();

			if (palabra.equals("{"))
				llaves.push("{");
			else if (palabra.equals("}"))
				llaves.pop();
			else if (esSentencia(sentencia) && !esSentencia(palabra))
				comSuma++;
		}

		COM.add(comSuma);
	}

	//En un fichero, se salta el codigo referente a una clase
	Scanner descartarClase ( Scanner scan)
	{
		Stack <String> llaves = new Stack <String>();
		String palabra = scan.next();

		while (!palabra.equals("{"))
			palabra = scan.next();
		
		llaves.push("{");
		
		while (!llaves.isEmpty())
		{
			palabra = scan.next();
			
			if (palabra.equals("{"))
				llaves.push("{");
			else if (palabra.equals("}"))
				llaves.pop();
			else if (palabra.equals("class"))
				descartarClase(scan);
		}

		return scan;
	}

	//Coloca un fichero en la palabra deseada
	Scanner colocarFichero ( String palabra) throws IOException
	{
		Scanner scan = new Scanner(file);
		String s = scan.next();

		while (scan.hasNextLine() && !s.equals(palabra))
		{
			s = scan.next();

			if (s.equals("extends"))
				scan.next();
		}

		return scan;
	}

	//Genera el archivo de reporte
	public void generarReporte ( ) throws IOException
	{
		String contenido = "Reporte del archivo: " + nombreArchivo + "\n\n";

		reporte = new File(nombreArchivo + ".txt");

		if (reporte.exists())
			reporte.delete();
		else
			reporte.createNewFile();

		FileWriter escritor = new FileWriter(reporte.getName(), true);
		escritor.write(contenido);
		escritor.close();
	}

	//Escribe la informacion referente de cada clase en un fichero
	void reportePorClase ( String clase) throws IOException
	{
		String contenido = "Clase: " + clase + "\n\n";

		contenido += "WMC = " + calcularWMC() + " | DIT = " + DIT[clases.indexOf(clase)] + " | NOC = " + NOC[clases.indexOf(clase)];
		contenido += "\n\nMetodo - Complejidad del metodo";

		for (int i = 0; i < COM.size(); i++)
			contenido += "\n" + metodos.get(i) + " - " + COM.get(i);

		contenido += "\n\n\n";
		
		FileWriter escritor = new FileWriter(reporte.getName(), true);
		escritor.write(contenido);
		escritor.close();
	}

	boolean esSentencia ( String palabra )
	{	
		switch (palabra) 
		{
			case "if":
				return true;
			case "else":
				return true;
			case "while":
				return true;
			case "for":
				return true;
			case "try":
				return true;
			case "catch":
				return true;
			case "finally":
				return true;
			case "switch":
				return true;
			case "case":
				return true;
			case "default":
				return true;				
		}
		return false;
	}

    //Calcula el WMC de una clase
	int calcularWMC ( )
	{
		int suma = 0;

		for (int i = 0; i < COM.size(); i++)
			suma += COM.get(i);

		return suma;
	}

    //Calcula el DIT de una clase
    public void determinarDIT ( String clase)
	{
		int claseNum = clases.indexOf(clase);
		int dit = 0, i = 0;

		while (i < numClases)
		{
			if (arbol[claseNum][i])
			{
				claseNum = i;
				i = 0;
				dit++;
			}
			else
				i++;
		}

		DIT[clases.indexOf(clase)] = dit;
	}

    //Clases para el testeo del programa
	class Prueba1 extends ClasePrueba2
	{
		int funcion1 ( )
		{
			return 0;
		}

		int funcion2 ( )
		{
            if (1 == 1);
            
            if (2 == 2);

            if (3 == 3);

            if (3 == 3);
            
            if (4 == 4);

            return 0;
		}
	}

	public class ClasePrueba2
	{
		String funcion3 ( )
		{
			return "";
		}
	}

	public class ClasePrueba3 extends ClasePrueba2
	{

	}

    //Metodo main en el que se reciben los archivos como argumentos
	public static void main ( String[] args ) throws IOException 
	{
		if (args.length == 0)
		{
			System.out.println("No se ingreso un archivo");
			System.exit(-1);
		}
		
		AnalizadorMetricas a[] = new AnalizadorMetricas[args.length];

		for (int i = 0; i < args.length; i++)
			a[i] = new AnalizadorMetricas(args[i]); 
			
	}
}