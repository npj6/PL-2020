/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.dlsi.pl.p2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Niko
 */
public class AnalizadorSintacticoSLR {
    
    //Nombres de simbolos, equivalente a Token.EOF etc

    public static final int
		S		= 0,
		M		= 1,
		Fun             = 2,
		DV              = 3,
                Tipo            = 4,
                Cod             = 5,
                I               = 6,
                E               = 7,
		F		= 8;
    
    public static ArrayList<Integer> estadosRegla = new ArrayList<>();
    
    static {
        estadosRegla.add(-1);
        estadosRegla.add(5);
        estadosRegla.add(2);
        estadosRegla.add(2);
        estadosRegla.add(0);
        estadosRegla.add(6);
        estadosRegla.add(2);
        estadosRegla.add(1);
        estadosRegla.add(1);
        estadosRegla.add(3);
        estadosRegla.add(1);
        estadosRegla.add(1);
        estadosRegla.add(3);
        estadosRegla.add(3);
        estadosRegla.add(2);
        estadosRegla.add(3);
        estadosRegla.add(1);
        estadosRegla.add(1);
        estadosRegla.add(1);
        estadosRegla.add(1);
    }
    
    public static ArrayList<Integer> izquierdaRegla = new ArrayList<>();
    
    static {
        izquierdaRegla.add(-1);
        izquierdaRegla.add(S);
        izquierdaRegla.add(M);
        izquierdaRegla.add(M);
        izquierdaRegla.add(M);
        izquierdaRegla.add(Fun);
        izquierdaRegla.add(DV);
        izquierdaRegla.add(Tipo);
        izquierdaRegla.add(Tipo);
        izquierdaRegla.add(Cod);
        izquierdaRegla.add(Cod);
        izquierdaRegla.add(I);
        izquierdaRegla.add(I);
        izquierdaRegla.add(I);
        izquierdaRegla.add(I);
        izquierdaRegla.add(E);
        izquierdaRegla.add(E);
        izquierdaRegla.add(F);
        izquierdaRegla.add(F);
        izquierdaRegla.add(F);
    }
    
    private Token token;
    private AnalizadorLexico al;
    
    
    public AnalizadorSintacticoSLR(AnalizadorLexico al) {
        this.al = al;
        this.token = al.siguienteToken();
    }
    
    private String numeros = "";
    
    static final int numEstados = 39;
    static final int numReglas = 19;
    
    static final public HashMap<ArrayList<Integer>, Integer> tablaAccion = new HashMap<>();
    static {
        //                           estado     token             accion
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {0, Token.CLASS})), accion2Num("d", 2));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {1, Token.EOF})), accion2Num("aceptar", 0));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {2, Token.ID})), accion2Num("d", 3));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {3, Token.LBRA})), accion2Num("d", 4));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.CLASS})), accion2Num("d", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.ID})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.LBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.RBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.FUN})), accion2Num("d", 8));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.INT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.FLOAT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {4, Token.PRINT})), accion2Num("r", 4));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {5, Token.RBRA})), accion2Num("d", 9));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.CLASS})), accion2Num("d", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.ID})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.LBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.RBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.FUN})), accion2Num("d", 8));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.INT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.FLOAT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {6, Token.PRINT})), accion2Num("r", 4));
        
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.CLASS})), accion2Num("d", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.ID})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.LBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.RBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.FUN})), accion2Num("d", 8));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.INT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.FLOAT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {7, Token.PRINT})), accion2Num("r", 4));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {8, Token.ID})), accion2Num("d", 12));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.CLASS})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.ID})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.LBRA})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.RBRA})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.FUN})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.INT})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.FLOAT})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.PRINT})), accion2Num("r", 1));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {9, Token.EOF})), accion2Num("r", 1));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {10, Token.ID})), accion2Num("r", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {10, Token.LBRA})), accion2Num("r", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {10, Token.RBRA})), accion2Num("r", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {10, Token.INT})), accion2Num("r", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {10, Token.FLOAT})), accion2Num("r", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {10, Token.PRINT})), accion2Num("r", 2));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {11, Token.ID})), accion2Num("r", 3));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {11, Token.LBRA})), accion2Num("r", 3));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {11, Token.RBRA})), accion2Num("r", 3));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {11, Token.INT})), accion2Num("r", 3));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {11, Token.FLOAT})), accion2Num("r", 3));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {11, Token.PRINT})), accion2Num("r", 3));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {12, Token.LBRA})), accion2Num("d", 13));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.CLASS})), accion2Num("d", 2));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.ID})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.LBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.RBRA})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.FUN})), accion2Num("d", 8));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.INT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.FLOAT})), accion2Num("r", 4));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {13, Token.PRINT})), accion2Num("r", 4));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {14, Token.ID})), accion2Num("d", 19));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {14, Token.LBRA})), accion2Num("d", 18));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {14, Token.INT})), accion2Num("d", 22));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {14, Token.FLOAT})), accion2Num("d", 23));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {14, Token.PRINT})), accion2Num("d", 20));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {15, Token.RBRA})), accion2Num("d", 24));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {15, Token.PYC})), accion2Num("d", 25));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {16, Token.RBRA})), accion2Num("r", 10));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {16, Token.PYC})), accion2Num("r", 10));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {17, Token.RBRA})), accion2Num("r", 11));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {17, Token.PYC})), accion2Num("r", 11));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {18, Token.ID})), accion2Num("d", 19));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {18, Token.LBRA})), accion2Num("d", 18));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {18, Token.INT})), accion2Num("d", 22));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {18, Token.FLOAT})), accion2Num("d", 23));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {18, Token.PRINT})), accion2Num("d", 20));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {19, Token.ASIG})), accion2Num("d", 29));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {20, Token.ID})), accion2Num("d", 34));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {20, Token.NUMENTERO})), accion2Num("d", 32));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {20, Token.NUMREAL})), accion2Num("d", 33));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {21, Token.ID})), accion2Num("d", 38));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {22, Token.ID})), accion2Num("r", 7));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {23, Token.ID})), accion2Num("r", 8));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.CLASS})), accion2Num("r", 5));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.ID})), accion2Num("r", 5));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.LBRA})), accion2Num("r", 5));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.RBRA})), accion2Num("r", 5));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.FUN})), accion2Num("r", 5));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.INT})), accion2Num("r", 5));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.FLOAT})), accion2Num("r", 5));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {24, Token.PRINT})), accion2Num("r", 5));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {25, Token.ID})), accion2Num("d", 19));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {25, Token.LBRA})), accion2Num("d", 18));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {25, Token.INT})), accion2Num("d", 22));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {25, Token.FLOAT})), accion2Num("d", 23));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {25, Token.PRINT})), accion2Num("d", 20));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {26, Token.RBRA})), accion2Num("r", 9));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {26, Token.PYC})), accion2Num("r", 9));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {27, Token.RBRA})), accion2Num("d", 28));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {27, Token.PYC})), accion2Num("d", 25));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {28, Token.RBRA})), accion2Num("r", 12));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {28, Token.PYC})), accion2Num("r", 12));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {29, Token.ID})), accion2Num("d", 34));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {29, Token.NUMENTERO})), accion2Num("d", 32));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {29, Token.NUMREAL})), accion2Num("d", 33));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {30, Token.RBRA})), accion2Num("r", 13));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {30, Token.PYC})), accion2Num("r", 13));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {30, Token.OPAS})), accion2Num("d", 35));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {31, Token.RBRA})), accion2Num("r", 16));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {31, Token.PYC})), accion2Num("r", 16));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {31, Token.OPAS})), accion2Num("r", 16));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {32, Token.RBRA})), accion2Num("r", 17));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {32, Token.PYC})), accion2Num("r", 17));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {32, Token.OPAS})), accion2Num("r", 17));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {33, Token.RBRA})), accion2Num("r", 18));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {33, Token.PYC})), accion2Num("r", 18));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {33, Token.OPAS})), accion2Num("r", 18));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {34, Token.RBRA})), accion2Num("r", 19));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {34, Token.PYC})), accion2Num("r", 19));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {34, Token.OPAS})), accion2Num("r", 19));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {35, Token.ID})), accion2Num("d", 34));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {35, Token.NUMENTERO})), accion2Num("d", 32));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {35, Token.NUMREAL})), accion2Num("d", 33));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {36, Token.RBRA})), accion2Num("r", 14));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {36, Token.PYC})), accion2Num("r", 14));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {36, Token.OPAS})), accion2Num("d", 35));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {37, Token.RBRA})), accion2Num("r", 15));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {37, Token.PYC})), accion2Num("r", 15));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {37, Token.OPAS})), accion2Num("r", 15));
        
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {38, Token.RBRA})), accion2Num("r", 6));
        tablaAccion.put(new ArrayList<>(Arrays.asList(new Integer[] {38, Token.PYC})), accion2Num("r", 6));
        
    }
    
    static final public HashMap<ArrayList<Integer>, Integer> tablaIrA = new HashMap<>();
    static {
        
        //                        estado  simbolo  ir a
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {0,   S})),     1);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {4,   S})),     7);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {4,   M})),     5);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {4,   Fun})),    6);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {6,   S})),     7);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {6,   M})),     10);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {6,   Fun})),    6);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {7,   S})),     7);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {7,   M})),     11);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {7,   Fun})),    6);
        
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {13,   S})),     7);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {13,   M})),     14);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {13,   Fun})),    6);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {14,   DV})),    17);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {14,   Tipo})),    21);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {14,   Cod})),    15);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {14,   I})),    16);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {18,   DV})),    17);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {18,   Tipo})),    21);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {18,   Cod})),    27);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {18,   I})),    16);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {20,   E})),    36);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {20,   F})),    31);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {25,   DV})),    17);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {25,   Tipo})),    21);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {25,   I})),    26);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {29,   E})),    30);
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {29,   F})),    31);
        
        tablaIrA.put(new ArrayList<>(Arrays.asList(new Integer[] {35,   F})),    37);
        
        
    }
    
    //Convierte una accion en un numero almacenable
    //si la accion está mal, intenta hacerlo petar con una reducción imposible.
    static public int accion2Num(String accion, int num) {
        switch(accion) {
            case "d":
                return num;
            case "r":
                return -num;
            case "a":
            case "aceptar":
                return numEstados;
            default:
                return -numReglas -1;
        }
    }
    
    static public String num2AccionString(int num) {
        if (num < -numReglas) {
            return "error";
        } if (num < 0) {
            return "r";
        } if(num < numEstados) {
            return "d";
        } if (num == numEstados) {
            return "aceptar";
        } else {
            return "error";
        }
    }
    
    static public int num2AccionNum(int num) {
        if (num < 0) {
            return -num;
        } if(num < numEstados) {
            return num;
        }  else {
            return 0;
        }
    }
    
 
    private void errorSintaxis(Integer state) {
        String output = "";
        for (int expectedToken=0; expectedToken<Token.nombreToken.size(); expectedToken++) {
            if(null != tablaAccion.get(new ArrayList<>(Arrays.asList(new Integer[] {state, expectedToken})))) {
                output += " "+Token.nombreToken.get(expectedToken);
            }
        }
        if (token.tipo != Token.EOF) {
            System.err.println("Error sintactico ("+token.fila+","+token.columna+"): encontrado '"+token.lexema+"', esperaba"+output);
        } else {
            System.err.println("Error sintactico: encontrado fin de fichero, esperaba"+output);
        }
        System.exit(-1);
    }
    
    private ArrayDeque<Integer> stack = new ArrayDeque<>();
    
    public void analizar() {
        stack.push(0);
        boolean aceptado = false;
        while(!aceptado) {
           Integer orden = tablaAccion.get(new ArrayList<>(Arrays.asList(new Integer[] {stack.peek(), token.tipo})));
           if (orden == null) {
               orden = -numReglas -1;
           }
            /*
                   
                       System.out.println("Orden: ");
                       System.out.println(orden);
                       System.out.println("Token:");
                       System.out.println(token.tipo);
                       System.out.println("Stack");
                       System.out.println(stack);
                       System.out.println();
            */
           switch(num2AccionString(orden)) {
               case "d":
                   stack.push(num2AccionNum(orden));
                   token = al.siguienteToken();
                   break;
               case "r":
                   int regla = num2AccionNum(orden);
                   for (int i=0; i<estadosRegla.get(regla); i++) {
                       
                       stack.pop();
                   }
                   int irA = tablaIrA.get(new ArrayList<>(Arrays.asList(new Integer[] {stack.peek(), izquierdaRegla.get(regla)})));
                   stack.push(irA);
                   numeros = Integer.toString(regla) + " " + numeros;
                   /*
                       System.out.println("Stack");
                       System.out.println(stack);
                       System.out.println("Regla: ");
                       System.out.println(regla);
                       System.out.println("Numeros");
                       System.out.println(numeros);
                       System.out.println();
                       System.out.println();
                    */
                   break;
               case "aceptar":
               case "a":
                   aceptado = true;
                   break;
               default:
                   errorSintaxis(stack.peek());
           }
        }
        System.out.println(numeros);
    }
    
}
