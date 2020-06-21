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

//Clase Categoria
public class Categoria {
    
    //Atributos de la clase Categoria
    private String nombreCategoria;
    private ArrayList<Pregunta> preguntas;

    //Constructor de la clase Categoria.
    public Categoria(String nombreCategoria, ArrayList<Pregunta> preguntas) {
        this.nombreCategoria = nombreCategoria;
        this.preguntas = preguntas;
    }

    
    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    
    public String getNombreCategoria() {
        return nombreCategoria;
    }
    
    //Se define el método extrerPregunta(), el cual recibe como parámetro un dato de tipo entero. Crea un objeto Pregunta y a través de un
    //ciclo for evalúa si el dato que entra como parámetro es igual al atributo numeroPregunta de alguno de los objetos almacenados en el 
    //atributo preguntas, cuando lo encuentra, iguala este objeto con el objeto previamente creado, rompe con el ciclo y retorna el objeto.
    public Pregunta extraerPregunta(int id){
        
        Pregunta pregunta = null;
        
        for (int i = 0; i < preguntas.size(); i++) {
            
            if(preguntas.get(i).getNumeroPregunta() == id){
                
                pregunta = preguntas.get(i);
                break;
            }
        }
        
        return pregunta;
        
    }

        
    
    
}
