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

    private Simbolo crearSimbolo(String nombre, int tipoSimbolo, String nombreTrad) {
        Simbolo output = new Simbolo(nombre, tipoSimbolo, nombreTrad);
        boolean creado = tsActual.anyadir(output);
        if (!creado) {
            errorSemantico(ERRYADECL, errT);
        }
        return output;
    }
    
    private Simbolo buscarSimbolo(String nombre) {
        Simbolo output = tsActual.buscar(nombre);
        if(output == null) {
            errorSemantico(ERRNODECL, errT);
        } else if (output.tipo != Simbolo.REAL && output.tipo != Simbolo.ENTERO) {
            errorSemantico(ERRNOSIMPLE, errT);
        }
        return output;
    }
    
    //ANALIZADOR LEXICO
    
    private Token token;
    private AnalizadorLexico al;
    
    //SECUENCIA DE REGLAS
    
    private StringBuilder numeros = new StringBuilder();
    
    private boolean mostrarNumeros = false;
    public void toggleMostrarNumeros() {
        mostrarNumeros = !mostrarNumeros;
    }
    
    //MENSAJES DE ERROR
    Token errT; //sirve para guardar los errores de token usados en la interfaz de simbolo
    
  private final int ERRYADECL=1,ERRNODECL=2,ERRTIPOS=3,ERRNOSIMPLE=4,ERRNOENTERO=5;
  private void errorSemantico(int nerror,Token tok)
  {
    System.err.print("Error semantico ("+tok.fila+","+tok.columna+"): en '"+tok.lexema+"', ");
    switch (nerror) {
      case ERRYADECL: System.err.println("ya existe en este ambito");
         break;
      case ERRNODECL: System.err.println("no ha sido declarado");
         break;
      case ERRTIPOS: System.err.println("tipos incompatibles entero/real");
         break;
      case ERRNOSIMPLE: System.err.println("debe ser de tipo entero o real");
         break;
      case ERRNOENTERO: System.err.println("debe ser de tipo entero");
         break;
    }
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
        
        public Simbolo simbolo;
    }
    
    private class S extends Elemento {
        public S(String prefix, String indent) {
            switch(token.tipo) {
                case Token.CLASS:
                    numeros.append("1 ");
                    emparejar(Token.CLASS);
                    errT = token;
                    IdToken idT = new IdToken(Token.ID);
                    idT.simbolo = crearSimbolo(idT.trad, Simbolo.CLASS, prefix+idT.trad);
                    crearAmbito();
                    emparejar(Token.LBRA);
                    Elemento M = new M(idT.simbolo.nomtrad+"_", indent);
                    emparejar(Token.RBRA);
                    cerrarAmbito();
                    if (!M.trad.equals("")) {
                        M.trad = "\n" + M.trad;
                    }
                    this.trad = indent+"// class "+idT.simbolo.nomtrad
                            +M.trad;
                    break;
                default:
                    errorSintaxis(Token.CLASS);
            }
        }
    } 
    private class M extends Elemento {
        public M(String prefix, String indent) {
            Elemento M;
            switch(token.tipo) {
                case Token.FUN:
                    numeros.append("2 ");
                    Elemento Fun = new Fun(prefix, indent);
                    M = new M(prefix, indent);
                    this.trad = "\n" + Fun.trad + "\n"
                            + M.trad;
                    break;
                case Token.CLASS:
                    numeros.append("3 ");
                    Elemento S = new S(prefix, indent);
                    M = new M(prefix, indent);
                    this.trad = "\n" + S.trad + "\n"
                            + M.trad;
                    break;
                case Token.RBRA:
                case Token.LBRA:
                case Token.ID:
                case Token.IF:
                case Token.PRINT:
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("4 ");
                    this.trad = "";
                    break;
                default:
                    errorSintaxis(Token.FUN, Token.CLASS, Token.RBRA, Token.LBRA, Token.ID, Token.IF, Token.PRINT, Token.INT, Token.FLOAT);
            }
        }
    }
    private class Fun extends Elemento {
        public Fun(String prefix, String indent) {
            switch(token.tipo) {
                case Token.FUN:
                    numeros.append("5 ");
                    emparejar(Token.FUN);
                    errT = token;
                    IdToken idT = new IdToken(Token.ID);
                    idT.simbolo = crearSimbolo(idT.trad, Simbolo.FUN, prefix+idT.trad);
                    crearAmbito();
                    Elemento A = new A();
                    emparejar(Token.LBRA);
                    Elemento M = new M(idT.simbolo.nomtrad+"_", indent+"\t");
                    Elemento Cod = new Cod(idT.simbolo.nomtrad+"_", indent+"\t");
                    emparejar(Token.RBRA);
                    cerrarAmbito();
                    
                    if (!M.trad.equals("")) {
                        M.trad = "\n" + M.trad;
                    }
                    
                    this.trad = indent + "void " + idT.simbolo.nomtrad + "("+A.trad+") {"
                            + M.trad + "\n"
                            + Cod.trad
                            + indent + "} // "+idT.simbolo.nomtrad;
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
                    Elemento DV = new DV("");
                    Elemento Ap = new Ap();
                    this.trad = DV.trad + Ap.trad;
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
                    Elemento DV = new DV("");
                    Elemento Ap = new Ap();
                    this.trad = ", "+DV.trad+Ap.trad;
                    break;
                case Token.LBRA:
                    numeros.append("8 ");
                    this.trad = "";
                    break;
                default:
                    errorSintaxis(Token.PYC, Token.LBRA);
            }
        }
    }
    private class DV extends Elemento {
        public DV(String prefix) {
            switch(token.tipo) {
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("9 ");
                    Tipo Tipo = new Tipo();
                    errT = token;
                    IdToken idT = new IdToken(Token.ID);
                    idT.simbolo = crearSimbolo(idT.trad, Tipo.tipo, prefix+idT.trad);
                    this.trad = Tipo.trad + " " + idT.simbolo.nomtrad;
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
                    this.tipo = Simbolo.ENTERO;
                    this.trad = "int";
                    break;
                case Token.FLOAT:
                    numeros.append("11 ");
                    emparejar(Token.FLOAT);
                    this.tipo = Simbolo.REAL;
                    this.trad = "float";
                    break;
                default:
                    errorSintaxis(Token.INT, Token.FLOAT);
            }
        }
        
        public int tipo;
    }
    private class Cod extends Elemento {
        public Cod(String prefix, String indent) {
            switch(token.tipo) {
                case Token.LBRA:
                case Token.ID:
                case Token.IF:
                case Token.PRINT:
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("12 ");
                    Elemento I = new I(prefix, indent);
                    Elemento Codp = new Codp(prefix, indent);
                    this.trad = indent + I.trad + Codp.trad + "\n";
                    break;
                default:
                    errorSintaxis(Token.LBRA, Token.ID, Token.IF, Token.PRINT, Token.INT, Token.FLOAT);
            }
        }
    }
    private class Codp extends Elemento {
        public Codp(String prefix, String indent) {
            switch(token.tipo) {
                case Token.PYC:
                    numeros.append("13 ");
                    emparejar(Token.PYC);
                    Elemento I = new I(prefix, indent);
                    Elemento Codp = new Codp(prefix, indent);
                    this.trad = "\n" + indent + I.trad + Codp.trad;
                    break;
                case Token.RBRA:
                    numeros.append("14 ");
                    this.trad = "";
                    break;
                default:
                    errorSintaxis(Token.PYC, Token.RBRA);
            }
        }
    }
    private class I extends Elemento {
        public I(String prefix, String indent) {
            IdToken idT;
            Expr Expr;
            Token err;
            switch(token.tipo) {
                case Token.INT:
                case Token.FLOAT:
                    numeros.append("15 ");
                    Elemento DV = new DV(prefix);
                    this.trad = DV.trad + ";";
                    break;
                case Token.LBRA:
                    numeros.append("16 ");
                    crearAmbito();
                    emparejar(Token.LBRA);
                    Elemento Cod = new Cod(prefix+"_", indent+"\t");
                    emparejar(Token.RBRA);
                    cerrarAmbito();
                    this.trad = "{\n"
                            +Cod.trad
                            +indent + "}";
                    break;
                case Token.ID:
                    numeros.append("17 ");
                    errT = token;
                    idT = new IdToken(Token.ID);
                    idT.simbolo = buscarSimbolo(idT.trad);
                    err = token;
                    emparejar(Token.ASIG);
                    Expr = new Expr();
                    if (idT.simbolo.tipo == Simbolo.ENTERO && Expr.tipo == Simbolo.REAL) {
                        errorSemantico(ERRTIPOS, err);
                    }
                    if (idT.simbolo.tipo == Simbolo.REAL && Expr.tipo == Simbolo.ENTERO) {
                        Expr.trad = "itor("+Expr.trad+")";
                    }
                    this.trad = idT.simbolo.nomtrad + " = " + Expr.trad + ";";
                    break;
                case Token.IF:
                    numeros.append("18 ");
                    err = token;
                    emparejar(Token.IF);
                    Expr = new Expr();
                    if (Expr.tipo != Simbolo.ENTERO) {
                        errorSemantico(ERRNOENTERO, err);
                    }
                    emparejar(Token.DOSP);
                    Elemento I = new I(prefix, indent+"\t");
                    Elemento Ip = new Ip(prefix, indent);
                    this.trad = "if ("+Expr.trad+")\n"
                            + indent + "\t" + I.trad + Ip.trad;
                    break;
                case Token.PRINT:
                    numeros.append("21 ");
                    emparejar(Token.PRINT);
                    Expr = new Expr();
                    String text;
                    if (Expr.tipo == Simbolo.REAL) {
                        text = "f";
                    } else {
                        text = "d";
                    }
                    this.trad = "printf(\"%" + text + "\", " + Expr.trad + ");";
                    break;
                default:
                    errorSintaxis(Token.INT, Token.FLOAT, Token.LBRA, Token.ID, Token.IF, Token.PRINT);
            }
        }
    }
    private class Ip extends Elemento {
        public Ip(String prefix, String indent) {
            switch(token.tipo) {
                case Token.ELSE:
                    numeros.append("19 ");
                    emparejar(Token.ELSE);
                    Elemento I = new I(prefix, indent+"\t");
                    emparejar(Token.FI);
                    this.trad = "\n"
                            + indent + "else\n"
                            + indent + "\t" + I.trad;
                    break;
                case Token.FI:
                    numeros.append("20 ");
                    emparejar(Token.FI);
                    this.trad = "";
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
                    E E = new E();
                    Exprp Exprp = new Exprp(E.tipo);
                    this.tipo = Exprp.tipo;
                    if (Exprp.previousItor) {
                        E.trad = "itor(" + E.trad + ")";
                    }
                    this.trad = E.trad + Exprp.trad;
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
        
        public int tipo;
    }
    private class Exprp extends Elemento {
        public Exprp(int tipoPredecesor) {
            this.previousItor = false;
            switch(token.tipo) {
                case Token.OPREL:
                    numeros.append("23 ");
                    String oprelTrad = emparejar(Token.OPREL);
                    E E = new E();
                    int oprelTipo;
                    if (tipoPredecesor == Simbolo.ENTERO && E.tipo == Simbolo.ENTERO) {
                        oprelTipo = Simbolo.ENTERO;
                        oprelTrad = oprelTrad + "i";
                    } else {
                        oprelTipo = Simbolo.REAL;
                        oprelTrad = oprelTrad + "r";
                        if (tipoPredecesor == Simbolo.ENTERO) {
                            this.previousItor = true;
                        } else if (E.tipo == Simbolo.ENTERO) {
                            E.trad = "itor(" + E.trad + ")";
                        }
                    }
                    this.tipo = Simbolo.ENTERO;
                    this.trad = " " + oprelTrad + " " + E.trad;
                    break;
                case Token.PYC:
                case Token.RBRA:
                case Token.ELSE:
                case Token.FI:
                case Token.DOSP:
                case Token.PARD:
                    numeros.append("24 ");
                    this.tipo = tipoPredecesor;
                    this.trad = "";
                    break;
                default:
                    errorSintaxis(Token.OPREL, Token.PYC, Token.RBRA, Token.ELSE, Token.FI, Token.DOSP, Token.PARD);
            }
        }
        
        public boolean previousItor;
        public int tipo;
    }
    private class E extends Elemento {
        public E() {
            switch(token.tipo) {
                case Token.ID:
                case Token.NUMENTERO:
                case Token.NUMREAL:
                case Token.PARI:
                    numeros.append("25 ");
                    T T = new T();
                    Ep Ep = new Ep(T.tipo);
                    this.tipo = Ep.tipo;
                    if (Ep.previousItor) {
                        T.trad = "itor("+T.trad;
                    }
                    this.trad = T.trad + Ep.trad;
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
        
        public int tipo;
    }
    private class Ep extends Elemento {
        public Ep(int tipoPredecesor) {
                this.previousItor = false;
            switch(token.tipo) {
                case Token.OPAS:
                    numeros.append("26 ");
                    String opasTrad = emparejar(Token.OPAS);
                    T T = new T();
                    int opasTipo;
                    if (tipoPredecesor == Simbolo.ENTERO && T.tipo == Simbolo.ENTERO) {
                        opasTipo = Simbolo.ENTERO;
                        opasTrad = " " + opasTrad + "i";
                    } else {
                        opasTipo = Simbolo.REAL;
                        opasTrad = " " + opasTrad + "r";
                        if (T.tipo == Simbolo.ENTERO) {
                            T.trad = "itor("+T.trad+")";
                        }
                        
                        if (tipoPredecesor == Simbolo.ENTERO) {
                            this.previousItor = true;
                            opasTrad = ")" + opasTrad;
                        }
                    }
                    Ep Ep = new Ep(opasTipo);
                    this.tipo = Ep.tipo;
                    if(Ep.previousItor) {
                        this.previousItor = true;
                    }
                    this.trad = opasTrad + " " + T.trad + Ep.trad;
                    break;
                case Token.OPREL:
                case Token.PYC:
                case Token.RBRA:
                case Token.ELSE:
                case Token.FI:
                case Token.DOSP:
                case Token.PARD:
                    numeros.append("27 ");
                    this.tipo = tipoPredecesor;
                    this.trad = "";
                    break;
                default:
                    errorSintaxis(Token.OPAS, Token.OPREL, Token.PYC, Token.RBRA, Token.ELSE, Token.FI, Token.DOSP, Token.PARD);
            }
        }
        
        public boolean previousItor;
        public int tipo;
    }
    private class T extends Elemento {
        public T() {
            switch(token.tipo) {
                case Token.ID:
                case Token.NUMENTERO:
                case Token.NUMREAL:
                case Token.PARI:
                    numeros.append("28 ");
                    F F = new F();
                    Tp Tp = new Tp(F.tipo);
                    this.tipo = Tp.tipo;
                    if(Tp.previousItor) {
                        F.trad = "itor("+F.trad;
                    }
                    this.trad = F.trad + Tp.trad;
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
        
        public int tipo;
    }
    private class Tp extends Elemento {
        public Tp(int tipoPredecesor) {
            this.previousItor = false;
            switch(token.tipo) {
                case Token.OPMUL:
                    numeros.append("29 ");
                    String opmulTrad = emparejar(Token.OPMUL);
                    F F = new F();
                    int opmulTipo;
                    if(tipoPredecesor == Simbolo.ENTERO && F.tipo == Simbolo.ENTERO) {
                        opmulTipo = Simbolo.ENTERO;
                        opmulTrad = " " + opmulTrad + "i";
                    } else {
                        opmulTipo = Simbolo.REAL;
                        opmulTrad = " " + opmulTrad + "r";
                        if (F.tipo == Simbolo.ENTERO) {
                            F.trad = "itor(" + F.trad + ")";
                        }
                        
                        if (tipoPredecesor == Simbolo.ENTERO) {
                            this.previousItor = true;
                            opmulTrad = ")" + opmulTrad;
                        }
                    }
                    Tp Tp = new Tp(opmulTipo);
                    this.tipo = Tp.tipo;
                    if(Tp.previousItor) {
                        this.previousItor = true;
                    }
                    this.trad = opmulTrad + " " + F.trad + Tp.trad;
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
                    this.tipo = tipoPredecesor;
                    this.trad = "";
                    break;
                default:
                    errorSintaxis(Token.OPMUL, Token.OPAS, Token.OPREL, Token.PYC, Token.RBRA, Token.ELSE, Token.FI, Token.DOSP, Token.PARD);
            }
        }
        
        public boolean previousItor;
        public int tipo;
    }
    private class F extends Elemento {
        public F() {
            switch(token.tipo) {
                case Token.ID:
                    numeros.append("31 ");
                    errT = token;
                    IdToken idT = new IdToken(Token.ID);
                    idT.simbolo = buscarSimbolo(idT.trad);
                    this.tipo = idT.simbolo.tipo;
                    this.trad = idT.simbolo.nomtrad;
                    break;
                case Token.NUMENTERO:
                    numeros.append("32 ");
                    String numenteroTrad = emparejar(Token.NUMENTERO);
                    this.tipo = Simbolo.ENTERO;
                    this.trad = numenteroTrad;
                    break;
                case Token.NUMREAL:
                    numeros.append("33 ");
                    String numrealTrad = emparejar(Token.NUMREAL);
                    this.tipo = Simbolo.REAL;
                    this.trad = numrealTrad;
                    break;
                case Token.PARI:
                    numeros.append("34 ");
                    emparejar(Token.PARI);
                    Expr Expr = new Expr();
                    emparejar(Token.PARD);
                    this.tipo = Expr.tipo;
                    this.trad = "("+Expr.trad+")";
                    break;
                default:
                    errorSintaxis(Token.ID, Token.NUMENTERO, Token.NUMREAL, Token.PARI);
            }
        }
        
        public int tipo;
    }
    
    //INTERFAZ PRACTICA
    
    //EDITADO PREFIJO INICIAL
    public String S(String prefix) {
        Elemento S = new S(prefix, "");
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
