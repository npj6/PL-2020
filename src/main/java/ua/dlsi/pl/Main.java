/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.dlsi.pl;

import ua.dlsi.pl.p1.plp1;
import ua.dlsi.pl.p2.plp2;
import ua.dlsi.pl.p3.plp3;


/**
 * Se que está feo pero que quieres que diga
 * tengo que adaptarme un minimo al formato de entrega
 * 
 */
public class Main {
     public static void main(String[] args) {
         int input = 3;
         //de momento asume que todo trabaja igual
         //plpX nombreArchivo
         String[] newArgs = {Resources.getFilePath(args[0])};
         switch (input) {
             case 1:
                 plp1.main(newArgs);
                 break;
             case 2:
                 plp2.main(newArgs);
                 break;
             case 3:
                 plp3.main(newArgs);
                 break;
             default:
                 System.out.println("No such thing as plp"+input);
         }
     }
}
