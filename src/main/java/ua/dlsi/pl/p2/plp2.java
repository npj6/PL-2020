package ua.dlsi.pl.p2;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

class plp2 {
    public static void main(String[] args) {

        if (args.length == 1)
        {
          try {
            RandomAccessFile entrada = new RandomAccessFile(args[0],"r");
            AnalizadorLexico al = new AnalizadorLexico(entrada);
            AnalizadorSintacticoSLR aslr = new AnalizadorSintacticoSLR(al);

            aslr.analizar();
          }
          catch (FileNotFoundException e) {
            System.out.println("Error, fichero no encontrado: " + args[0]);
          }
        } 
        else System.out.println("Error, uso: java plp2 <nomfichero>");
                //TEST
        String filename = plp2.class.getClassLoader().getResource("p06.txt").getFile();
        try {
            System.out.println("TESTING");
            RandomAccessFile entrada = new RandomAccessFile(filename,"r");
            AnalizadorLexico al = new AnalizadorLexico(entrada);
            AnalizadorSintacticoSLR aslr = new AnalizadorSintacticoSLR(al);

            aslr.analizar();
        }
        catch (FileNotFoundException e) {
          System.out.println("Error, fichero no encontrado: " + filename);
          System.out.println(System.getProperty("user.dir"));
        }
    }
    
    
}

