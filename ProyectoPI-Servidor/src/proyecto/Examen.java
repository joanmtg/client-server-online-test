/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: Categoria.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : Categoria 
* Responsabilidad : Esta clase se encarga de guardar y gestionar la información
* de la categoría de un examen, junto con sus preguntas.
* 
*/

package proyecto;

import java.util.*;

/**
 *
 * @author jacrodas
 */
public class Examen {
    
    // Declaración de las variables globales a utilizar
    
    private int numeroExamen; // Variable de tipo int que nos permitirá almacenar el numero de examen
    private int cantidadPreguntas; // Esta variable nos permitirá almacenar la cantidad de preguntas que tiene el examen
    private int tiempoDuracion; // Se almacenará el tiempo de duración designado para el examen
    private ArrayList<Pregunta> preguntasExamen;

    /**
     * Constructor de la clase Examen
     * @param numeroExamen
     * @param cantidadPreguntas
     * @param tiempoDuracion
     * @param preguntasExamen 
     */
    public Examen(int numeroExamen, int cantidadPreguntas, int tiempoDuracion, ArrayList<Pregunta> preguntasExamen) {
        this.numeroExamen = numeroExamen;
        this.cantidadPreguntas = cantidadPreguntas;
        this.tiempoDuracion = tiempoDuracion;
        this.preguntasExamen = preguntasExamen;
    }

    /**
     * Método que retorna el número de examen
     * @return 
     */
    public int getNumeroExamen() {
        return numeroExamen;
    }

    
    /**
     * Método que recibe por párametro un numero de examen y le asigna
     * el valor de este párametro a la variable local numero de examen
     * @param numeroExamen 
     */
    public void setNumeroExamen(int numeroExamen) {
        this.numeroExamen = numeroExamen;
    } 
    
    /**
     * Método que retorna la cantidad de preguntas del examen
     * @return 
     */
    public int getCantidadPreguntas() {
        return cantidadPreguntas;
    }

    /**
     * Método que recibe por párametro una cantidad de preguntas determinada
     * y le asigna el valor de este párametro a la variable local donde
     * se almacena la cantidad de preguntas del examen
     * @param cantidadPreguntas 
     */
    public void setCantidadPreguntas(int cantidadPreguntas) {
        this.cantidadPreguntas = cantidadPreguntas;
    }

    /**
     * Método que retorna el tiempo de duración designado para un examen
     * @return 
     */
    public int getTiempoDuracion() {
        return tiempoDuracion;
    }

    /**
     * Método que recibe por párametro un tiempo de duración para un examen
     * y le asigna el valor de este párametro a la variable local donde
     * se almacena el tiempo de duración
     * @param tiempoDuracion 
     */
    public void setTiempoDuracion(int tiempoDuracion) {
        this.tiempoDuracion = tiempoDuracion;
    }

    
    
    
}
