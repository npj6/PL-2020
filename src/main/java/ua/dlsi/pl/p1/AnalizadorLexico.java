package ua.dlsi.pl.p1;

import java.io.RandomAccessFile;
import java.util.ArrayList;

//ESTUPIDA NO HAS HECHO NADA DE GITHUB???
//METE ESTE PROYECTO EN GITHUB ZORRA


public class AnalizadorLexico {
    
    private static final char EOF = (char) -1; //End Of File
    private static final char SOC = (char) -2; //Start Of Comment
    
    RandomAccessFile fichero;
    ArrayList<Character> buffer;
    
    public AnalizadorLexico(RandomAccessFile entrada) {
        this.fichero = entrada;
        this.buffer = new ArrayList<>();
    }
    
    public Token siguienteToken() {
        int estado = 0;
        Token token = null;
        
        
        //Tema 2 pagina 9
        //EOT Windows vs Ubuntu recordar
        //Hacer movida de github y movida de entrega
        
        return token;
    }
    
    
    private int delta(int estado, char caracter) {
        return 0;
    }
    
    private char siguienteChar() {
        //Si buffer no está vacio, los lees de ahí, eliminandolos
        
        //Si buffer esta vacio, haces fichero.readByte()
          //comprobar EOF y SOC
          //SOC necesita que leas un caracter mas, si no es SOC, lo metes al buffer
        return '\0';
    }
}
