/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.dlsi.pl;

/**
 *
 * @author Niko
 */
public class Resources {
    static String getFilePath (String fileName) {
        return Main.class.getClassLoader().getResource(fileName).getFile();
    }
}
