package ua.dlsi.pl.p3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//voy a indentar la salida porque soy una psicopata

public class TraductorDR {
    
    private String indent = "";
    
    private void indentUp() {
        indent += "\t";
    }
    
    private void indentDown() {
        indent = indent.substring(0, indent.length() - 1);
    }
    
    
    
    private Token token;
    private AnalizadorLexico al;
    
    private boolean mostrarNumeros = true;
    private StringBuilder numeros = new StringBuilder();
    
    public void toggleMostrarNumeros() {
        mostrarNumeros = !mostrarNumeros;
    }
    
    public TraductorDR(AnalizadorLexico al) {
        this.al = al;
        token = al.siguienteToken();
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
    
    public final String emparejar(int tokenEsperado) {
        //calcula la indentación segun los tipos del token
        switch(token.tipo) {
            case Token.LBRA:
                indentUp();
                break;
            case Token.RBRA:
                indentDown();
                break;
        }
        String output = "";
        if (token.tipo == tokenEsperado) {
            output = token.lexema;
            token = al.siguienteToken();
        } else {
            errorSintaxis(tokenEsperado);
        }
        return output;
    }
    
    //EDITADO PREFIJO INICIAL
    public String S(String s) {
        String output = "";
        switch(token.tipo) {
            case Token.CLASS:
                numeros.append("1 ");
                output += emparejar(Token.CLASS) + " ";
                output += emparejar(Token.ID) + " ";
                output += emparejar(Token.LBRA) + "\n" + indent;
                output += M() + "\n" + indent;
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.RBRA) + "\n" + indent;
                break;
            default:
                errorSintaxis(Token.CLASS);
        }
        return output; //EDITADO
    }
    public String M() {
        String output = "";
        switch(token.tipo) {
            case Token.FUN:
                numeros.append("2 ");
                output += Fun();
                output += M();
                break;
            case Token.CLASS:
                numeros.append("3 ");
                output += S(""); //EDITADO PREFIJO INICIAL
                output += M();
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
        return output;
    }
    public String Fun() {
        String output = "";
        switch(token.tipo) {
            case Token.FUN:
                numeros.append("5 ");
                output += emparejar(Token.FUN) + " ";
                output += emparejar(Token.ID) + " ";
                output += A();
                output += emparejar(Token.LBRA) + "\n" + indent;
                output += M();
                output += Cod();
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.RBRA) + "\n" + indent;
                break;
            default:
                errorSintaxis(Token.FUN);
        }
        return output;
    }
    public String A() {
        String output = "";
        switch(token.tipo) {
            case Token.INT:
            case Token.FLOAT:
                numeros.append("6 ");
                output += DV();
                output += Ap();
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT);
        }
        return output;
    }
    public String Ap() {
        String output = "";
        switch(token.tipo) {
            case Token.PYC:
                numeros.append("7 ");
                output += emparejar(Token.PYC) + " ";
                output += DV();
                output += Ap();
                break;
            case Token.LBRA:
                numeros.append("8 ");
                break;
            default:
                errorSintaxis(Token.PYC, Token.LBRA);
        }
        return output;
    }
    public String DV() {
        String output = "";
        switch(token.tipo) {
            case Token.INT:
            case Token.FLOAT:
                numeros.append("9 ");
                output += Tipo();
                output += emparejar(Token.ID) + " ";
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT);
        }
        return output;
    }
    public String Tipo() {
        String output = "";
        switch(token.tipo) {
            case Token.INT:
                numeros.append("10 ");
                output += emparejar(Token.INT) + " ";
                break;
            case Token.FLOAT:
                numeros.append("11 ");
                output += emparejar(Token.FLOAT) + " ";
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT);
        }
        return output;
    }
    public String Cod() {
        String output = "";
        switch(token.tipo) {
            case Token.LBRA:
            case Token.ID:
            case Token.IF:
            case Token.PRINT:
            case Token.INT:
            case Token.FLOAT:
                numeros.append("12 ");
                output += I();
                output += Codp() + "\n" + indent;
                break;
            default:
                errorSintaxis(Token.LBRA, Token.ID, Token.IF, Token.PRINT, Token.INT, Token.FLOAT);
        }
        return output;
    }
    public String Codp() {
        String output = "";
        switch(token.tipo) {
            case Token.PYC:
                numeros.append("13 ");
                output += emparejar(Token.PYC) + "\n" + indent;
                output += I();
                output += Codp();
                break;
            case Token.RBRA:
                numeros.append("14 ");
                break;
            default:
                errorSintaxis(Token.PYC, Token.RBRA);
        }
        return output;
    }
    public String I() {
        String output = "";
        switch(token.tipo) {
            case Token.INT:
            case Token.FLOAT:
                numeros.append("15 ");
                output += DV();
                break;
            case Token.LBRA:
                numeros.append("16 ");
                output += emparejar(Token.LBRA) + "\n" + indent;
                output += Cod();
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.RBRA);
                break;
            case Token.ID:
                numeros.append("17 ");
                output += emparejar(Token.ID) + " ";
                output += emparejar(Token.ASIG) + " ";
                output += Expr();
                break;
            case Token.IF:
                numeros.append("18 ");
                output += emparejar(Token.IF) + " ";
                output += Expr();
                
                indentUp();
                output += emparejar(Token.DOSP) + "\n" + indent;
                output += I() + "\n" + indent;
                output = output.substring(0, output.length() - 1); 
                output += Ip();
                indentDown();
                
                break;
            case Token.PRINT:
                numeros.append("21 ");
                output += emparejar(Token.PRINT) + " ";
                output += Expr();
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT, Token.LBRA, Token.ID, Token.IF, Token.PRINT);
        }
        return output;
    }
    public String Ip() {
        String output = "";
        switch(token.tipo) {
            case Token.ELSE:
                numeros.append("19 ");
                output += emparejar(Token.ELSE) + "\n" + indent;
                output += I() + "\n" + indent;
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.FI);
                break;
            case Token.FI:
                numeros.append("20 ");
                output += emparejar(Token.FI);
                break;
            default:
                errorSintaxis(Token.ELSE, Token.FI);
        }
        return output;
    }
    public String Expr() {
        String output = "";
        switch(token.tipo) {
            case Token.ID:
            case Token.NUMENTERO:
            case Token.NUMREAL:
            case Token.PARI:
                numeros.append("22 ");
                output += E();
                output += Exprp();
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
        return output;
    }
    public String Exprp() {
        String output = "";
        switch(token.tipo) {
            case Token.OPREL:
                numeros.append("23 ");
                output += emparejar(Token.OPREL) + " ";
                output += E();
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
        return output;
    }
    public String E() {
        String output = "";
        switch(token.tipo) {
            case Token.ID:
            case Token.NUMENTERO:
            case Token.NUMREAL:
            case Token.PARI:
                numeros.append("25 ");
                output += T();
                output += Ep();
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
        return output;
    }
    public String Ep() {
        String output = "";
        switch(token.tipo) {
            case Token.OPAS:
                numeros.append("26 ");
                output += emparejar(Token.OPAS) + " ";
                output += T();
                output += Ep();
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
        return output;
    }
    public String T() {
        String output = "";
        switch(token.tipo) {
            case Token.ID:
            case Token.NUMENTERO:
            case Token.NUMREAL:
            case Token.PARI:
                numeros.append("28 ");
                output += F();
                output += Tp();
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
        return output;
    }
    public String Tp() {
        String output = "";
        switch(token.tipo) {
            case Token.OPMUL:
                numeros.append("29 ");
                output += emparejar(Token.OPMUL) + " ";
                output += F();
                output += Tp();
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
        return output;
    }
    public String F() {
        String output = "";
        switch(token.tipo) {
            case Token.ID:
                numeros.append("31 ");
                output += emparejar(Token.ID) + " ";
                break;
            case Token.NUMENTERO:
                numeros.append("32 ");
                output += emparejar(Token.NUMENTERO) + " ";
                break;
            case Token.NUMREAL:
                numeros.append("33 ");
                output += emparejar(Token.NUMREAL) + " ";
                break;
            case Token.PARI:
                numeros.append("34 ");
                output += emparejar(Token.PARI) + " ";
                output += Expr() + " ";
                output += emparejar(Token.PARD) + " ";
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
        return output;
    }
    
    public void comprobarFinFichero() {
        if(token.tipo != Token.EOF) {
            errorSintaxis(Token.EOF);
        } else if (mostrarNumeros) {
            System.out.println(numeros.toString());
        }
    }
}
