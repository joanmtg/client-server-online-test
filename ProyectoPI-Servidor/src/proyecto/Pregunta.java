/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: Pregunta.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : Pregunta 
* Responsabilidad : Esta clase se encarga de almacenar y gestionar
* la información de una pregunta.
* 
*/

package proyecto;

public class Pregunta {
   
    //Atributos d ela clase:
    
    private String   enunciado;  //Enunciado de la pregunt
    private String[] respuestas; //Arreglo que contiene las posibles respuestas de la pregunta como cadenas
    private int      respuestaCorrecta; //Respuesta correcta: 1,2,3 ó 4
    private String   categoria;  //Categoría a la cual pertenece la pregunta
    private int      numeroPregunta;  //Número único que identifica a dicha pregunta

    /**
     * Constructor de la clase 
     * Recibe como parámetros cada uno de sus atributos
     * 
     * @param enunciado
     * @param respuestas
     * @param respuestaCorrecta
     * @param categoria
     * @param numeroPregunta 
     */
    
    public Pregunta(String enunciado, String[] respuestas, int respuestaCorrecta, String categoria, int numeroPregunta) {
        this.enunciado = enunciado;
        this.respuestas = respuestas;
        this.respuestaCorrecta = respuestaCorrecta;
        this.categoria = categoria;
        this.numeroPregunta = numeroPregunta;
    }
    
    /**
     * Métodos GET de los atributos de la clase
     * @return 
     */

    public int getNumeroPregunta() {
        return numeroPregunta;
    }    
       
    public String getEnunciado() {
        return enunciado;
    }    
    
    public String[] getRespuestas() {
        return respuestas;
    }    
    
    public int getRespuestaCorrecta() {
        return respuestaCorrecta;
    }    
    
    public String getCategoria() {
        return categoria;
    }    
    
}
