package ua.dlsi.pl.p1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalizadorSintacticoDR {
    
    private Token token;
    private AnalizadorLexico al;
    
    private boolean mostrarNumeros = true;
    private StringBuilder numeros = new StringBuilder();
    
    public void toggleMostrarNumeros() {
        mostrarNumeros = !mostrarNumeros;
    }
    
    public AnalizadorSintacticoDR(AnalizadorLexico al) {
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
    
    public final void emparejar(int tokenEsperado) {
        if (token.tipo == tokenEsperado) {
            token = al.siguienteToken();
        } else {
            errorSintaxis(tokenEsperado);
        }
    }
    
    public void S() {
        switch(token.tipo) {
            case Token.CLASS:
                numeros.append("1 ");
                emparejar(Token.CLASS);
                emparejar(Token.ID);
                emparejar(Token.LBRA);
                M();
                emparejar(Token.RBRA);
                break;
            default:
                errorSintaxis(Token.CLASS);
        }
    }
    public void M() {
        switch(token.tipo) {
            case Token.FUN:
                numeros.append("2 ");
                Fun();
                M();
                break;
            case Token.CLASS:
                numeros.append("3 ");
                S();
                M();
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
    public void Fun() {
        switch(token.tipo) {
            case Token.FUN:
                numeros.append("5 ");
                emparejar(Token.FUN);
                emparejar(Token.ID);
                A();
                emparejar(Token.LBRA);
                M();
                Cod();
                emparejar(Token.RBRA);
                break;
            default:
                errorSintaxis(Token.FUN);
        }
    }
    public void A() {
        switch(token.tipo) {
            case Token.INT:
            case Token.FLOAT:
                numeros.append("6 ");
                DV();
                Ap();
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT);
        }
    }
    public void Ap() {
        switch(token.tipo) {
            case Token.PYC:
                numeros.append("7 ");
                emparejar(Token.PYC);
                DV();
                Ap();
                break;
            case Token.LBRA:
                numeros.append("8 ");
                break;
            default:
                errorSintaxis(Token.PYC, Token.LBRA);
        }
    }
    public void DV() {
        switch(token.tipo) {
            case Token.INT:
            case Token.FLOAT:
                numeros.append("9 ");
                Tipo();
                emparejar(Token.ID);
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT);
        }
    }
    public void Tipo() {
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
    public void Cod() {
        switch(token.tipo) {
            case Token.LBRA:
            case Token.ID:
            case Token.IF:
            case Token.PRINT:
            case Token.INT:
            case Token.FLOAT:
                numeros.append("12 ");
                I();
                Codp();
                break;
            default:
                errorSintaxis(Token.LBRA, Token.ID, Token.IF, Token.PRINT, Token.INT, Token.FLOAT);
        }
    }
    public void Codp() {
        switch(token.tipo) {
            case Token.PYC:
                numeros.append("13 ");
                emparejar(Token.PYC);
                I();
                Codp();
                break;
            case Token.RBRA:
                numeros.append("14 ");
                break;
            default:
                errorSintaxis(Token.PYC, Token.RBRA);
        }
    }
    public void I() {
        switch(token.tipo) {
            case Token.INT:
            case Token.FLOAT:
                numeros.append("15 ");
                DV();
                break;
            case Token.LBRA:
                numeros.append("16 ");
                emparejar(Token.LBRA);
                Cod();
                emparejar(Token.RBRA);
                break;
            case Token.ID:
                numeros.append("17 ");
                emparejar(Token.ID);
                emparejar(Token.ASIG);
                Expr();
                break;
            case Token.IF:
                numeros.append("18 ");
                emparejar(Token.IF);
                Expr();
                emparejar(Token.DOSP);
                I();
                Ip();
                break;
            case Token.PRINT:
                numeros.append("21 ");
                emparejar(Token.PRINT);
                Expr();
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT, Token.LBRA, Token.ID, Token.IF, Token.PRINT);
        }
    }
    public void Ip() {
        switch(token.tipo) {
            case Token.ELSE:
                numeros.append("19 ");
                emparejar(Token.ELSE);
                I();
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
    public void Expr() {
        switch(token.tipo) {
            case Token.ID:
            case Token.NUMENTERO:
            case Token.NUMREAL:
            case Token.PARI:
                numeros.append("22 ");
                E();
                Exprp();
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
    }
    public void Exprp() {
        switch(token.tipo) {
            case Token.OPREL:
                numeros.append("23 ");
                emparejar(Token.OPREL);
                E();
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
    public void E() {
        switch(token.tipo) {
            case Token.ID:
            case Token.NUMENTERO:
            case Token.NUMREAL:
            case Token.PARI:
                numeros.append("25 ");
                T();
                Ep();
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
    }
    public void Ep() {
        switch(token.tipo) {
            case Token.OPAS:
                numeros.append("26 ");
                emparejar(Token.OPAS);
                T();
                Ep();
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
    public void T() {
        switch(token.tipo) {
            case Token.ID:
            case Token.NUMENTERO:
            case Token.NUMREAL:
            case Token.PARI:
                numeros.append("28 ");
                F();
                Tp();
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
    }
    public void Tp() {
        switch(token.tipo) {
            case Token.OPMUL:
                numeros.append("29 ");
                emparejar(Token.OPMUL);
                F();
                Tp();
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
    public void F() {
        switch(token.tipo) {
            case Token.ID:
                numeros.append("31 ");
                emparejar(Token.ID);
                break;
            case Token.NUMENTERO:
                numeros.append("32 ");
                emparejar(Token.NUMENTERO);
                break;
            case Token.NUMREAL:
                numeros.append("33 ");
                emparejar(Token.NUMREAL);
                break;
            case Token.PARI:
                numeros.append("34 ");
                emparejar(Token.PARI);
                Expr();
                emparejar(Token.PARD);
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
    }
    
    public void comprobarFinFichero() {
        if(token.tipo != Token.EOF) {
            errorSintaxis(Token.EOF);
        } else if (mostrarNumeros) {
            System.out.println(numeros.toString());
        }
    }
}
