package ua.dlsi.pl.p3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//voy a indentar la salida porque soy una psicopata

public class TraductorDR {
    
    public TraductorDR(AnalizadorLexico al) {
        this.al = al;
        token = al.siguienteToken();
    }
    
    //TABLA SIMBOLOS

    private TablaSimbolos tsActual = new TablaSimbolos(null);

    private void crearAmbito() {
        tsActual = new TablaSimbolos(tsActual);
    }
    private void cerrarAmbito() {
        tsActual = tsActual.getPadre();
    }

    private void crearSimbolo(String nombre, int tipoSimbolo, String nombreTrad) {
        boolean creado = tsActual.anyadir(new Simbolo(nombre, tipoSimbolo, nombreTrad));
        if (!creado) {
            errorSemantico(1);
        }
    }
    
    //FALTA COMPROBAR ERROR SEMANTICO 4
    private Simbolo buscarSimbolo(String nombre) {
        Simbolo output = tsActual.buscar(nombre);
        if(output == null) {
            errorSemantico(2);
        }
        return output;
    }
    
    //ANALIZADOR LEXICO
    
    private Token token;
    private AnalizadorLexico al;
    
    //SECUENCIA DE REGLAS
    
    private StringBuilder numeros = new StringBuilder();
    
    private boolean mostrarNumeros = true;
    public void toggleMostrarNumeros() {
        mostrarNumeros = !mostrarNumeros;
    }
    
    //MENSAJES DE ERROR
    private Token tokenError;
    
    private void errorSemantico(int codErr) {
        String output = "Error semantico ("+tokenError.fila+","+tokenError.columna+"): '"+tokenError.lexema+"'";
        switch(codErr) {
            case 1:
                output += ", ya existe en este ambito";
                break;
            case 2:
                output += ", no ha sido declarado";
                break;
            case 3:
                output += ", tipos incompatibles entero/real";
                break;
            case 4:
                output += " debe ser de tipo entero o real";
                break;
            case 5:
                output += " debe ser de tipo entero";
                break;
            default:
                output = ", esto no deberia de estar ocurriendo. La simulacion esta fallando.";
        }
        System.err.println(output);
        System.exit(-1);
    }
    
    static private ArrayList<Integer> order = new ArrayList<Integer>();
    
    static{
        order.add(Token.CLASS);
        order.add(Token.ID);
        order.add(Token.LBRA);
        order.add(Token.RBRA);
        order.add(Token.FUN);
        order.add(Token.PYC);
        order.add(Token.INT);
        order.add(Token.FLOAT);
        order.add(Token.ASIG);
        order.add(Token.IF);
        order.add(Token.DOSP);
        order.add(Token.ELSE);
        order.add(Token.FI);
        order.add(Token.PRINT);
        order.add(Token.OPREL);
        order.add(Token.OPAS);
        order.add(Token.OPMUL);
        order.add(Token.NUMENTERO);
        order.add(Token.NUMREAL);
        order.add(Token.PARI);
        order.add(Token.PARD);
        order.add(Token.EOF);
    }
    
    private void errorSintaxis(int ... tokenEsperados) {
        String output = "";
        for (int i=0; i<Token.nombreToken.size(); i++) {
            for (Integer t : tokenEsperados) {
                if(t.equals(order.get(i))) {
                    output += " "+Token.nombreToken.get(t);
                    break;
                }
            }
        }
        if (token.tipo != Token.EOF) {
            System.err.println("Error sintactico ("+token.fila+","+token.columna+"): encontrado '"+token.lexema+"', esperaba"+output);
        } else {
            System.err.println("Error sintactico: encontrado fin de fichero, esperaba"+output);
        }
        System.exit(-1);
    }
    
    //REGLAS DE LA GRAMATICA 
    
    private class Elemento {
        public String trad;
    }
    
    private String emparejar(int tokenEsperado) {
        String output = "";
        if (token.tipo == tokenEsperado) {
            output = token.lexema;
            token = al.siguienteToken();
        } else {
            errorSintaxis(tokenEsperado);
        }
        return output;
    }
    
    private class IdToken extends Elemento {
        public IdToken(int tokenEsperado) { 
            this.trad = emparejar(tokenEsperado);
        }
        
        public Simbolo symbol;
    }
    
    private class S extends Elemento {
        public S() {
            switch(token.tipo) {
                case Token.CLASS:
                    numeros.append("1 ");
                    emparejar(Token.CLASS);
                    Elemento idT = new IdToken(Token.ID);
                    emparejar(Token.LBRA);
                    Elemento M = new M();
                    emparejar(Token.RBRA);
                    break;
                default:
                    errorSintaxis(Token.CLASS);
            }
        }
    } 
    private class M extends Elemento {
        public M() {
            Elemento M;
            switch(token.tipo) {
                case Token.FUN:
                    numeros.append("2 ");
                    Elemento Fun = new Fun();
                    M = new M();
                    break;
                case Token.CLASS:
                    numeros.append("3 ");
                    Elemento S = new S(); //EDITADO PREFIJO INICIAL
                    M = new M();
                    break;
                case Token.RBRA:
                case Token.LBRA:
                case Token.ID:
                case Token.IF:
                case Token.PRINT:
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("4 ");
                    break;
                default:
                    errorSintaxis(Token.FUN, Token.CLASS, Token.RBRA, Token.LBRA, Token.ID, Token.IF, Token.PRINT, Token.INT, Token.FLOAT);
            }
        }
    }
    private class Fun extends Elemento {
        public Fun() {
            switch(token.tipo) {
                case Token.FUN:
                    numeros.append("5 ");
                    emparejar(Token.FUN);
                    Elemento idT = new IdToken(Token.ID);
                    Elemento A = new A();
                    emparejar(Token.LBRA);
                    Elemento M = new M();
                    Elemento Cod = new Cod();
                    emparejar(Token.RBRA);
                    break;
                default:
                    errorSintaxis(Token.FUN);
            }
        }
    }
    private class A extends Elemento {
        public A() {
            switch(token.tipo) {
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("6 ");
                    Elemento DV = new DV();
                    Elemento Ap = new Ap();
                    break;
                default:
                    errorSintaxis(Token.INT, Token.FLOAT);
            }
        }
    }
    private class Ap extends Elemento {
        public Ap() {
            switch(token.tipo) {
                case Token.PYC:
                    numeros.append("7 ");
                    emparejar(Token.PYC);
                    Elemento DV = new DV();
                    Elemento Ap = new Ap();
                    break;
                case Token.LBRA:
                    numeros.append("8 ");
                    break;
                default:
                    errorSintaxis(Token.PYC, Token.LBRA);
            }
        }
    }
    private class DV extends Elemento {
        public DV() {
            switch(token.tipo) {
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("9 ");
                    Elemento Tipo = new Tipo();
                    Elemento idT = new IdToken(Token.ID);
                    break;
                default:
                    errorSintaxis(Token.INT, Token.FLOAT);
            }
        }
    }
    private class Tipo extends Elemento {
        public Tipo() {
            switch(token.tipo) {
                case Token.INT:
                    numeros.append("10 ");
                    emparejar(Token.INT);
                    break;
                case Token.FLOAT:
                    numeros.append("11 ");
                    emparejar(Token.FLOAT);
                    break;
                default:
                    errorSintaxis(Token.INT, Token.FLOAT);
            }
        }
    }
    private class Cod extends Elemento {
        public Cod() {
            switch(token.tipo) {
                case Token.LBRA:
                case Token.ID:
                case Token.IF:
                case Token.PRINT:
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("12 ");
                    Elemento I = new I();
                    Elemento Codp = new Codp();
                    break;
                default:
                    errorSintaxis(Token.LBRA, Token.ID, Token.IF, Token.PRINT, Token.INT, Token.FLOAT);
            }
        }
    }
    private class Codp extends Elemento {
        public Codp() {
            switch(token.tipo) {
                case Token.PYC:
                    numeros.append("13 ");
                    emparejar(Token.PYC);
                    Elemento I = new I();
                    Elemento Codp = new Codp();
                    break;
                case Token.RBRA:
                    numeros.append("14 ");
                    break;
                default:
                    errorSintaxis(Token.PYC, Token.RBRA);
            }
        }
    }
    private class I extends Elemento {
        public I() {
            Elemento idT, Expr;
            switch(token.tipo) {
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("15 ");
                    Elemento DV = new DV();
                    break;
                case Token.LBRA:
                    numeros.append("16 ");
                    emparejar(Token.LBRA);
                    Elemento Cod = new Cod();
                    idT = new IdToken(Token.RBRA);
                    break;
                case Token.ID:
                    numeros.append("17 ");
                    idT = new IdToken(Token.ID);
                    emparejar(Token.ASIG);
                    Expr = new Expr();
                    break;
                case Token.IF:
                    numeros.append("18 ");
                    emparejar(Token.IF);
                    Expr = new Expr();

                    emparejar(Token.DOSP);
                    Elemento I = new I();
                    Elemento Ip = new Ip();

                    break;
                case Token.PRINT:
                    numeros.append("21 ");
                    emparejar(Token.PRINT);
                    Expr = new Expr();
                    break;
                default:
                    errorSintaxis(Token.INT, Token.FLOAT, Token.LBRA, Token.ID, Token.IF, Token.PRINT);
            }
        }
    }
    private class Ip extends Elemento {
        public Ip() {
            switch(token.tipo) {
                case Token.ELSE:
                    numeros.append("19 ");
                    emparejar(Token.ELSE);
                    Elemento I = new I();
                    emparejar(Token.FI);
                    break;
                case Token.FI:
                    numeros.append("20 ");
                    emparejar(Token.FI);
                    break;
                default:
                    errorSintaxis(Token.ELSE, Token.FI);
            }
        }
    }
    private class Expr extends Elemento {
        public Expr() {
            switch(token.tipo) {
                case Token.ID:
                case Token.NUMENTERO:
                case Token.NUMREAL:
                case Token.PARI:
                    numeros.append("22 ");
                    Elemento E = new E();
                    Elemento Experp = new Exprp();
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
    }
    private class Exprp extends Elemento {
        public Exprp() {
            switch(token.tipo) {
                case Token.OPREL:
                    numeros.append("23 ");
                    String oprelLex = emparejar(Token.OPREL);
                    Elemento E = new E();
                    break;
                case Token.PYC:
                case Token.RBRA:
                case Token.ELSE:
                case Token.FI:
                case Token.DOSP:
                case Token.PARD:
                    numeros.append("24 ");
                    break;
                default:
                    errorSintaxis(Token.OPREL, Token.PYC, Token.RBRA, Token.ELSE, Token.FI, Token.DOSP, Token.PARD);
            }
        }
    }
    private class E extends Elemento {
        public E() {
            switch(token.tipo) {
                case Token.ID:
                case Token.NUMENTERO:
                case Token.NUMREAL:
                case Token.PARI:
                    numeros.append("25 ");
                    Elemento T = new T();
                    Elemento Ep = new Ep();
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
    }
    private class Ep extends Elemento {
        public Ep() {
            switch(token.tipo) {
                case Token.OPAS:
                    numeros.append("26 ");
                    String opasLex = emparejar(Token.OPAS);
                    Elemento T = new T();
                    Elemento Ep = new Ep();
                    break;
                case Token.OPREL:
                case Token.PYC:
                case Token.RBRA:
                case Token.ELSE:
                case Token.FI:
                case Token.DOSP:
                case Token.PARD:
                    numeros.append("27 ");
                    break;
                default:
                    errorSintaxis(Token.OPAS, Token.OPREL, Token.PYC, Token.RBRA, Token.ELSE, Token.FI, Token.DOSP, Token.PARD);
            }
        }
    }
    private class T extends Elemento {
        public T() {
            switch(token.tipo) {
                case Token.ID:
                case Token.NUMENTERO:
                case Token.NUMREAL:
                case Token.PARI:
                    numeros.append("28 ");
                    Elemento F = new F();
                    Elemento Tp = new Tp();
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
    }
    private class Tp extends Elemento {
        public Tp() {
            switch(token.tipo) {
                case Token.OPMUL:
                    numeros.append("29 ");
                    String opmulLex = emparejar(Token.OPMUL);
                    Elemento F = new F();
                    Elemento Tp = new Tp();
                    break;
                case Token.OPAS:
                case Token.OPREL:
                case Token.PYC:
                case Token.RBRA:
                case Token.ELSE:
                case Token.FI:
                case Token.DOSP:
                case Token.PARD:
                    numeros.append("30 ");
                    break;
                default:
                    errorSintaxis(Token.OPMUL, Token.OPAS, Token.OPREL, Token.PYC, Token.RBRA, Token.ELSE, Token.FI, Token.DOSP, Token.PARD);
            }
        }
    }
    private class F extends Elemento {
        public F() {
            switch(token.tipo) {
                case Token.ID:
                    numeros.append("31 ");
                    Elemento idT = new IdToken(Token.ID);
                    break;
                case Token.NUMENTERO:
                    numeros.append("32 ");
                    String numenteroLex = emparejar(Token.NUMENTERO);
                    break;
                case Token.NUMREAL:
                    numeros.append("33 ");
                    String numrealLex = emparejar(Token.NUMREAL);
                    break;
                case Token.PARI:
                    numeros.append("34 ");
                    emparejar(Token.PARI);
                    Elemento Expr = new Expr();
                    emparejar(Token.PARD);
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
    }
    
    //INTERFAZ PRACTICA
    
    //EDITADO PREFIJO INICIAL
    public String S(String s) {
        Elemento S = new S();
        return S.trad; //EDITADO
    }
    
    public void comprobarFinFichero() {
        if(token.tipo != Token.EOF) {
            errorSintaxis(Token.EOF);
        } else if (mostrarNumeros) {
            System.out.println(numeros.toString());
        }
    }
}
