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
    private Simbolo buscarSimbolo(String nombre) {
        Simbolo output = tsActual.buscar(nombre);
        if(output == null) {
            errorSemantico(2);
        }
        return output;
    }
    
    
//INDENTACION
    
    private String indent = "";
    
    private void indentUp() {
        indent += "\t";
    }
    private void indentDown() {
        indent = indent.substring(0, indent.length() - 1);
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
    
    private void errorSemantico(int codErr) {
        String output = "Error semantico ("+token.fila+","+token.columna+"): '"+token.lexema+"'";
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
    
    
//REGLAS DE LA GRAMATICA
    
    public final String emparejar(int tokenEsperado, SimboloWrapper salida_simbolo) {
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
            //comprueba que exista el identificador y devuelve su traduccion (si es id)
            if(token.tipo == Token.ID) {
                Simbolo simb = buscarSimbolo(token.lexema);
                if (salida_simbolo != null) {
                    salida_simbolo.value = simb;
                }
                output = simb.nomtrad;
            }
            token = al.siguienteToken();
        } else {
            errorSintaxis(tokenEsperado);
        }
        return output;
    }
    
    
    //EDITADO PREFIJO INICIAL
    public String S(String entrada_prefijo) {
        String output = "";
        switch(token.tipo) {
            case Token.CLASS:
                numeros.append("1 ");
                output += emparejar(Token.CLASS, null) + " ";
                crearSimbolo(token.lexema, Simbolo.CLASS, "CLASS_"+token.lexema);
                output += emparejar(Token.ID, null) + " ";
                crearAmbito();
                output += emparejar(Token.LBRA, null) + "\n" + indent;
                output += M() + "\n" + indent;
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.RBRA, null) + "\n" + indent;
                cerrarAmbito();
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
                output += emparejar(Token.FUN, null) + " ";
                crearSimbolo(token.lexema, Simbolo.FUN, "FUN_"+token.lexema);
                output += emparejar(Token.ID, null) + " ";
                crearAmbito();
                output += A();
                output += emparejar(Token.LBRA, null) + "\n" + indent;
                output += M();
                output += Cod();
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.RBRA, null) + "\n" + indent;
                cerrarAmbito();
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
                output += emparejar(Token.PYC, null) + " ";
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
                IntegerWrapper tipo = new IntegerWrapper();
                output += Tipo(tipo);
                String prefix = "";
                switch(tipo.value) {
                    case Simbolo.ENTERO:
                        prefix = "ENTERO_";
                        break;
                    case Simbolo.REAL:
                        prefix = "REAL_";
                        break;
                    default:
                }
                crearSimbolo(token.lexema, tipo.value, prefix+token.lexema);
                output += emparejar(Token.ID, null) + " ";
                break;
            default:
                errorSintaxis(Token.INT, Token.FLOAT);
        }
        return output;
    }
    public String Tipo(IntegerWrapper salida_tipo) {
        String output = "";
        switch(token.tipo) {
            case Token.INT:
                numeros.append("10 ");
                salida_tipo.value = Simbolo.ENTERO;
                output += emparejar(Token.INT, null) + " ";
                break;
            case Token.FLOAT:
                salida_tipo.value = Simbolo.REAL;
                numeros.append("11 ");
                output += emparejar(Token.FLOAT, null) + " ";
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
                output += emparejar(Token.PYC, null) + "\n" + indent;
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
                crearAmbito();
                output += emparejar(Token.LBRA, null) + "\n" + indent;
                output += Cod();
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.RBRA, null);
                cerrarAmbito();
                break;
            case Token.ID:
                numeros.append("17 ");
                SimboloWrapper identificador = new SimboloWrapper();
                output += emparejar(Token.ID, identificador) + " ";
                output += emparejar(Token.ASIG, null) + " ";
                output += Expr();
                break;
            case Token.IF:
                numeros.append("18 ");
                output += emparejar(Token.IF, null) + " ";
                output += Expr();
                
                indentUp();
                output += emparejar(Token.DOSP, null) + "\n" + indent;
                output += I() + "\n" + indent;
                output = output.substring(0, output.length() - 1); 
                output += Ip();
                indentDown();
                
                break;
            case Token.PRINT:
                numeros.append("21 ");
                output += emparejar(Token.PRINT, null) + " ";
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
                output += emparejar(Token.ELSE, null) + "\n" + indent;
                output += I() + "\n" + indent;
                output = output.substring(0, output.length() - 1);
                output += emparejar(Token.FI, null);
                break;
            case Token.FI:
                numeros.append("20 ");
                output += emparejar(Token.FI, null);
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
                output += emparejar(Token.OPREL, null) + " ";
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
                output += emparejar(Token.OPAS, null) + " ";
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
                output += emparejar(Token.OPMUL, null) + " ";
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
                output += emparejar(Token.ID, null) + " ";
                break;
            case Token.NUMENTERO:
                numeros.append("32 ");
                output += emparejar(Token.NUMENTERO, null) + " ";
                break;
            case Token.NUMREAL:
                numeros.append("33 ");
                output += emparejar(Token.NUMREAL, null) + " ";
                break;
            case Token.PARI:
                numeros.append("34 ");
                output += emparejar(Token.PARI, null) + " ";
                output += Expr() + " ";
                output += emparejar(Token.PARD, null) + " ";
                break;
            default:
                errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
        }
        return output;
    }
    
    
//OTRAS COSAS
    public void comprobarFinFichero() {
        if(token.tipo != Token.EOF) {
            errorSintaxis(Token.EOF);
        } else if (mostrarNumeros) {
            System.out.println(numeros.toString());
        }
    }
    
    public class IntegerWrapper {public int value;}
    public class SimboloWrapper {public Simbolo value;}
}
