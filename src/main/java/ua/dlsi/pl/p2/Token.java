package ua.dlsi.pl.p2;

import java.util.ArrayList;

public class Token {

	public int fila;
	public int columna;

	public String lexema;
	public static final ArrayList<String> nombreToken = new ArrayList<String>();


//		nombreToken.add("(");
//		nombreToken.add(")");
//		nombreToken.add(":");
//		nombreToken.add("< <= > >= == !=");
//		nombreToken.add("* /");
//		nombreToken.add("'if'");
//		nombreToken.add("'else'");
//		nombreToken.add("'fi'");

	static{
		nombreToken.add("'class'");
		nombreToken.add("identificador");
		nombreToken.add("{");
		nombreToken.add("}");
		nombreToken.add("'fun'");
		nombreToken.add("'int'");
		nombreToken.add("'float'");
		nombreToken.add(";");
		nombreToken.add("=");
		nombreToken.add("'print'");
		nombreToken.add("+ -");
		nombreToken.add("numero entero");
		nombreToken.add("numero real");
		nombreToken.add("fin de fichero");
	}

	public int tipo;		// tipo es: ID, ENTERO, REAL ...

	public static final int
		CLASS		= 0,
		ID		= 1,
		LBRA            = 2,
		RBRA            = 3,
		FUN		= 4,
		INT		= 5,
		FLOAT		= 6,
		PYC		= 7,
		ASIG		= 8,
		PRINT           = 9,
		OPAS		= 10,
		NUMENTERO	= 11,
		NUMREAL		= 12,
		EOF		= 13;

	public String toString(){
	        return nombreToken.get(tipo);
	}
}

