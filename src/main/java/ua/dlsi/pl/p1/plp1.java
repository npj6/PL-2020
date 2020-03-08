package ua.dlsi.pl.p1;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

class plp1 {
    public static void main(String[] args) {

        if (args.length == 1)
        {
          try {
            RandomAccessFile entrada = new RandomAccessFile(args[0],"r");
            AnalizadorLexico al = new AnalizadorLexico(entrada);
            AnalizadorSintacticoDR asdr = new AnalizadorSintacticoDR(al);

            asdr.S(); // simbolo inicial de la gramatica
            asdr.comprobarFinFichero();
          }
          catch (FileNotFoundException e) {
            System.out.println("Error, fichero no encontrado: " + args[0]);
          }
        } else System.out.println("Error, uso: java plp1 <nomfichero>");
        //TEST
        String filename = plp1.class.getClassLoader().getResource("r.txt").getFile();
        try {
          RandomAccessFile entrada = new RandomAccessFile(filename,"r");
          AnalizadorLexico al = new AnalizadorLexico(entrada);
          Token t;
          while((t = al.siguienteToken()).tipo != Token.EOF) {
            System.out.println("Token: "+t.lexema);
            System.out.println("Tipo: "+t.toString());
            System.out.println("En: ("+t.fila+","+t.columna+")");
            System.out.println();
          }
        }
        catch (FileNotFoundException e) {
          System.out.println("Error, fichero no encontrado: " + filename);
          System.out.println(System.getProperty("user.dir"));
        }
    }
}
