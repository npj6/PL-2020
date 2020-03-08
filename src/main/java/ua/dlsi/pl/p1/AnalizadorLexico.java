package ua.dlsi.pl.p1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;



public class AnalizadorLexico {
    
    Token token;
    //ultimo char leido
    int fila = 1;
    int columna = 0;
    int longitud_ultima_fila;
    //temporales para el token
    int fila_token;
    int columna_token;
    
    //disables the SOC detection
    private boolean readingComment = false;
    
    private static final char EOF = (char) -1; //End Of File
    private static final char SOC = (char) -2; //Start Of Comment
    
    RandomAccessFile fichero;
    ArrayList<Character> buffer;
    ArrayList<Character> temp_token;
    
    public AnalizadorLexico(RandomAccessFile entrada) {
        this.fichero = entrada;
        this.buffer = new ArrayList<>();
    }
    
    public Token siguienteToken() {
        int estado = 0;
        token = null;
        temp_token = new ArrayList<>();
        try {
            while(token == null) {
                estado = estados[estado].estado();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            if (0 < temp_token.size()) {
                System.err.println("Error lexico ("+fila_token+","+columna_token+"): caracter '"+temp_token.get(0)+"' incorrecto");
            } else {
                System.err.println("Error lexico: fin de fichero inesperado");
            }
            System.exit(-1);
        }
        
        return token;
    }
    
    private void anadirLetraToken(char c) {
            if (temp_token.isEmpty()) {
                fila_token = fila;
                columna_token = columna;
            }
            temp_token.add(c);
    }
    
    private char readByte() {
        char output;
        try {
            output = (char) fichero.readByte();
        } catch (IOException e) {
            output = EOF;
        }
        return output;
    }
    
    private char siguienteChar() {
        char output;
        if (0 < buffer.size()) {
            //Si buffer no está vacio, los lees de ahí, eliminandolos
            output = buffer.remove(buffer.size()-1);
        } else {
            //Si buffer esta vacio, lees del fichero
            output = readByte();
            //Comprobar SOC
            if (output == '/' && !readingComment) {
                    output = readByte();
                if (output == '*') {
                    //Si es SOC devueves SOC
                    output = SOC;
                } else {
                    //Si no es SOC metes el caracter extra al buffer
                    buffer.add(output);
                    output = '/';
                }
            }
        }
        
        
        
        if (output == '\n') {
            fila++;
            longitud_ultima_fila = columna;
            columna = 0;
        } else {
            columna++;
            if(output==SOC) {
                columna++;
            }
        }
        
        return output;
    }
    
    private String crearString() {

        StringBuilder sb = new StringBuilder();
        
        temp_token.forEach((c) -> { sb.append(c); });

        return sb.toString();
    }
    
    private void devolverAlBuffer(int n) {
        for(int i=0; i<n; i++) {
            char c = temp_token.remove(temp_token.size()-1);
            buffer.add(c);
            if (columna == 0) {
                columna = longitud_ultima_fila;
                fila--;
            } else {
                columna--;
                if (c==SOC) {
                    columna--;
                }
            }
        }
    }
    
    private Token crearToken(int tipo) {
        Token output = new Token();
        output.fila = fila_token;
        output.columna = columna_token;
        output.lexema = crearString();
        output.tipo = tipo;
        if (output.tipo == Token.ID) {
            switch (output.lexema) {
                case "class":
                    output.tipo = Token.CLASS;
                    break;
                case "fun":
                    output.tipo = Token.FUN;
                    break;
                case "int":
                    output.tipo = Token.INT;
                    break;
                case "float":
                    output.tipo = Token.FLOAT;
                    break;
                case "if":
                    output.tipo = Token.IF;
                    break;
                case "else":
                    output.tipo = Token.ELSE;
                    break;
                case "fi":
                    output.tipo = Token.FI;
                    break;
                case "print":
                    output.tipo = Token.PRINT;
                    break;
            }
        }
        return output;
    }
    
    //Funciones-Estado para acceso directo
    private int estado0() {
        char c = siguienteChar();
        int estado = -1;
        switch(c) {
            case '(':
                estado = 1;
                break;
            case ')':
                estado = 2;
                break;
            case ':':
                estado = 3;
                break;
            case '{':
                estado = 4;
                break;
            case '}':
                estado = 5;
                break;
            case ';':
                estado = 6;
                break;
            case '-':
            case '+':
                estado = 7;
                break;
            case '*':
            case '/':
                estado = 8;
                break;
            case '=':
                estado = 9;
                break;
            case '<':
            case '>':
                estado = 12;
                break;
            case '!':
                estado = 14;
                break;
            case ' ':
            case '\t':
            case '\n':
                estado = 0;
                break;
            case SOC:
                readingComment = true;
                estado = 23;
                break;
            case EOF:
                estado = 25;
                break;
            default:
                if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
                    estado = 15;
                } else if ('0' <= c && c <= '9') {
                    estado = 17;
                }
        }
        if (c!=' ' && c!='\t' && c!='\n' && c!=SOC) {
            anadirLetraToken(c);
        }
        return estado;
    }
    private int estado1() {
        token = crearToken(Token.PARI);
        return 0;
    }
    private int estado2() {
        token = crearToken(Token.PARD);
        return 0;
    }
    private int estado3() {
        token = crearToken(Token.DOSP);
        return 0;
    }
    private int estado4() {
        token = crearToken(Token.LBRA);
        return 0;
    }
    private int estado5() {
        token = crearToken(Token.RBRA);
        return 0;
    }
    private int estado6() {
        token = crearToken(Token.PYC);
        return 0;
    }
    private int estado7() {
        token = crearToken(Token.OPAS);
        return 0;
    }
    private int estado8() {
        token = crearToken(Token.OPMUL);
        return 0;
    }
    private int estado9() {
        char c = siguienteChar();
        int estado = 11;
        if (c == '=') {
            estado = 10;
        }
        anadirLetraToken(c);
        return estado;
    }
    private int estado10() {
        token = crearToken(Token.OPREL);
        return 0;
    }
    private int estado11() {
        devolverAlBuffer(1);
        token = crearToken(Token.ASIG);
        return 0;
    }
    private int estado12() {
        char c = siguienteChar();
        int estado = 13;
        if (c == '=') {
            estado = 10;
        }
        anadirLetraToken(c);
        return estado;
    }
    private int estado13() {
        devolverAlBuffer(1);
        token = crearToken(Token.OPREL);
        return 0;
    }
    private int estado14() {
        char c = siguienteChar();
        int estado = -1;
        if (c == '=') {
            estado = 10;
        }
        anadirLetraToken(c);
        return estado;
    }
    private int estado15() {
        char c = siguienteChar();
        int estado = 16;
        if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
            estado = 15;
        }
        anadirLetraToken(c);
        return estado;
    }
    private int estado16() {
        devolverAlBuffer(1);
        token = crearToken(Token.ID);
        return 0;
    }
    private int estado17() {
        char c = siguienteChar();
        int estado = 18;
        if ('0' <= c && c <= '9') {
            estado = 17;
        }
        if (c == '.') {
            estado = 19;
        }
        anadirLetraToken(c);
        return estado;
    }
    private int estado18() {
        devolverAlBuffer(1);
        token = crearToken(Token.NUMENTERO);
        return 0;
    }
    private int estado19() {
        char c = siguienteChar();
        int estado = 22;
        if ('0' <= c && c <= '9') {
            estado = 20;
        }
        anadirLetraToken(c);
        return estado;
    }
    private int estado20() {
        char c = siguienteChar();
        int estado = 21;
        if ('0' <= c && c <= '9') {
            estado = 20;
        }
        anadirLetraToken(c);
        return estado;
    }
    private int estado21() {
        devolverAlBuffer(1);
        token = crearToken(Token.NUMREAL);
        return 0;
    }
    private int estado22() {
        devolverAlBuffer(2);
        token = crearToken(Token.NUMENTERO);
        return 0;
    }
    private int estado23() {
        char c = siguienteChar();
        int estado = 23;
        if (c == '*') {
            estado = 24;
        } else if (c==EOF) {
            estado = -1;
        }
        return estado;
    }
    private int estado24() {
        char c = siguienteChar();
        int estado = 23;
        if (c == '/') {
            readingComment = false;
            estado = 0;
        } else if (c==EOF) {
            estado = -1;
        }
        return estado;
    }
    private int estado25() {
        token = crearToken(Token.EOF);
        return 0;
    }

    interface Estado {
        int estado();
    }

    final private Estado[] estados = new Estado[] {
        new Estado() { @Override public int estado() { return estado0(); } },
        new Estado() { @Override public int estado() { return estado1(); } },
        new Estado() { @Override public int estado() { return estado2(); } },
        new Estado() { @Override public int estado() { return estado3(); } },
        new Estado() { @Override public int estado() { return estado4(); } },
        new Estado() { @Override public int estado() { return estado5(); } },
        new Estado() { @Override public int estado() { return estado6(); } },
        new Estado() { @Override public int estado() { return estado7(); } },
        new Estado() { @Override public int estado() { return estado8(); } },
        new Estado() { @Override public int estado() { return estado9(); } },
        new Estado() { @Override public int estado() { return estado10(); } },
        new Estado() { @Override public int estado() { return estado11(); } },
        new Estado() { @Override public int estado() { return estado12(); } },
        new Estado() { @Override public int estado() { return estado13(); } },
        new Estado() { @Override public int estado() { return estado14(); } },
        new Estado() { @Override public int estado() { return estado15(); } },
        new Estado() { @Override public int estado() { return estado16(); } },
        new Estado() { @Override public int estado() { return estado17(); } },
        new Estado() { @Override public int estado() { return estado18(); } },
        new Estado() { @Override public int estado() { return estado19(); } },
        new Estado() { @Override public int estado() { return estado20(); } },
        new Estado() { @Override public int estado() { return estado21(); } },
        new Estado() { @Override public int estado() { return estado22(); } },
        new Estado() { @Override public int estado() { return estado23(); } },
        new Estado() { @Override public int estado() { return estado24(); } },
        new Estado() { @Override public int estado() { return estado25(); } }
    };
}

