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
	ArrayList <String> metodos;
	ArrayList <Integer> com;
	ArrayList <String> clases;
	Map <String, Integer> mapa = new HashMap <String, Integer> ();
	File file;
	int comSuma, DIT, NOC;
	String clase;
	String nombreArchivo;

	public AnalizadorMetricas ( String ruta ) throws IOException 
	{
		String palabra;
		file = new File(ruta);
		subclases = new ArrayList <String>();
		metodos = new ArrayList <String> ();
		ArrayList <String> aux = new ArrayList <String> ();
		com = new ArrayList <Integer> ();
		DIT = NOC = 0;

		Scanner scan = new Scanner(file);

		while (scan.hasNextLine())
		{
			palabra = scan.next();

			if (palabra.equals("class"))
			{
				clase = scan.next();
				nombreArchivo = clase;
				analizarClase(scan);
			}	
		}

		scan.close();
		aux.addAll(subclases);
	
		for (int i = 0; i < aux.size(); i++)
		{
			subclases.clear();
			metodos.clear();
			com.clear();
			DIT = NOC = 0;
			clase = aux.get(i);
			scan = new Scanner(file);
			moverFichero(scan, aux.get(i));
			analizarClase(scan);
		}
	}

	Scanner moverFichero ( Scanner scan, String clase) throws IOException
	{
		String palabra = scan.next();

		while (!palabra.equals(clase))
			palabra = scan.next();

		return scan;
	}

	public void generarReporte ( ) throws IOException
	{
		String fileContent = "Clase: " + clase;
		fileContent += "\n\nWMC = " + calculaWMC() + " | DIT = " + obtenerDIT(clase) + " | NOC = " + NOC;
		fileContent += "\n\nMetodos - COM";
		
		for(int i = 0; i < metodos.size(); i++)
			fileContent += "\n" + metodos.get(i) + " - " + com.get(i);
		
		fileContent += "\n\n\n";

		FileWriter writer = new FileWriter("/Reportes/ReporteMetricas-" + nombreArchivo +".txt", true);
		writer.write(fileContent);
		writer.close();
	}

	int obtenerDIT ( String clase)
	{
		return mapa.get(clase);
	}

	int calculaWMC ( )
	{
		int suma = 0;

		for (int i = 0; i < com.size(); i++)
			suma += com.get(i);

		return suma;
	}

	public void analizarClase ( Scanner scan ) throws IOException
	{
		while (scan.hasNextLine())
			buscarMetodo(scan);	
		
		generarReporte();
	}

	void buscarMetodo ( Scanner scan )
	{
		Stack <String> metodo = new Stack <String>();
		String palabra = "", nombre = "";
		int llaves = 0;
		comSuma = 0;
		do
		{
			nombre = palabra;
	
			if (!mapa.containsKey(clase))
				mapa.put(clase, 0);
	
			if(nombre.equals("}"))
				return;
	
			if (scan.hasNextLine())
			{
				if (palabra.equals("class"))
				{
					palabra = scan.next();
					subclases.add(palabra);
	
					palabra = scan.next();
	
					if (palabra.equals("extends"))
					{
						while (!palabra.equals("{"))
						{
							palabra = scan.next();
	
							if (palabra.equals(clase))
								NOC++;
							
							if (mapa.containsKey(palabra) && !mapa.containsKey(subclases.get(subclases.size() - 1)))
								mapa.put(subclases.get(subclases.size() - 1), mapa.get(palabra) + 1);
						}
	
						llaves = 1;
	
						while (scan.hasNextLine() && llaves != 0)
						{
							palabra = scan.next();
	
							if (palabra.equals("{"))
								llaves++;
							else if (palabra.equals("}"))
								llaves--;
						}
					}
					if (!mapa.containsKey(subclases.get(subclases.size() - 1)))
						mapa.put(subclases.get(subclases.size() - 1), 0);
					
				}
				if (scan.hasNextLine())
					palabra = scan.next();
			}
		} while (!palabra.equals("("));

		metodos.add(nombre);

		if (!scan.hasNextLine())
			return;
	
		while (!palabra.equals("{") && scan.hasNextLine())
			if (scan.hasNextLine())
				palabra = scan.next();
		
		metodo.push("{");

		while (!metodo.isEmpty() && scan.hasNextLine())
		{
			palabra = scan.next();

			if (esSentencia(palabra))
				comSuma++;
			if (palabra.equals("{"))
				metodo.push("{");
			if (palabra.equals("}"))
				metodo.pop();
		}
	
		com.add(comSuma);
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

	class Prueba1
	{
		int funcion1 ( )
		{
			return 0;
		}

		void funcion2 ( )
		{

		}
	}

	public class ClasePrueba2
	{
		String funcion3 ( )
		{
			return "";
		}
	}

	public class ClasePrueba3
	{

	}
}