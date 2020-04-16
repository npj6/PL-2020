package ua.dlsi.pl.p3;

public class Simbolo {

  // los símbolos pueden ser variables enteras, reales, clases o funciones
  public static final int ENTERO=1, REAL=2, CLASS=3, FUN=4;
  

  /**
   * nombre del símbolo en el programa fuente
   */
  public String nombre;
  
  /**
   * tipo (ENTERO, REAL, CLASS, FUN)
   */
  public int tipo;        
  
  /**
   * nombre traducido al lenguaje objeto (se genera en la declaración)
   */
  public String nomtrad;
  
  
  /**
   * constructor
   * @param nombre  nombre en el programa fuente
   * @param tipo    tipo con el que se declara
   * @param nomtrad nombre en el lenguaje objeto
   */
  public Simbolo(String nombre,int tipo,String nomtrad)
  {
    this.nombre = nombre;
    this.tipo = tipo;
    this.nomtrad = nomtrad;
  }

}
