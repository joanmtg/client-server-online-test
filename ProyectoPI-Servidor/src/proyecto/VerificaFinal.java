/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: VerificaFinal.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : VerificaFinal
* Responsabilidad : Esta clase se encarga de verificar cuando se ha terminado el examen,
* y si éste terminó a causa del temporizador o no.
*  
* 
*/
package proyecto;

import static java.lang.Thread.*;
import javax.swing.*;

//Clase VerificaFinal que extiende de Thread.
public class VerificaFinal extends Thread{
    
    //Atributos de la clase VerificaFinal
    private Temporizador temporizador;
    private Servidor server;
    private ServidorHilos cliente;

    //Constructor de la clase VerificaFinal
    public VerificaFinal(Temporizador temporizador, Servidor server, ServidorHilos cliente) {
        this.temporizador = temporizador;
        this.server = server;
        this.cliente = cliente;
    }
    
    //Se sobrecarga el método run(), el cual ejecuta una serie de instrucciones.
    @Override
    public void run(){
        
        //Se define un ciclo infinito.
        while(true){
            
            //Evalúa si el valor de retorno del método isExamenFinalizado() es igual a true, en caso de ser así, pasa a evaluar otra
            //condición.
            if(server.isExamenFinalizado()){
                
                //Evalúa si el valor de retorno del método getExamenFinalizado() es igual a false, si este es el caso, ejecuta los métodos
                //enviarResulatados(), enviarCalificacion, reiniciarVariablesExamen(), interrumpe el hilo del temporizador, muestra una ventana
                //de diálogo y se ejecuta un break para terminar la evaluación del condicional.
                if(!temporizador.getExamenFinalizado()){                    
                    
                    cliente.enviarResultados();
                    cliente.enviarCalificacion();
                    server.reiniciarVariablesExamen();
                    temporizador.interrupt();
                    JOptionPane.showMessageDialog(null, "Ha respondido todas las preguntas");
                    break;
                }                
            }
                       
            //Evalúa si el valor de retorno del método getExamenFinalizado() es igual a true, si este es el caso, ejecuta los métodos
            //enviarResulatados(), enviarCalificacion, reiniciarVariablesExamen(), muestra una ventana de diálogo y se ejecuta un 
            //break para terminar la evaluación del condicional.
            else if(temporizador.getExamenFinalizado()){
                
                cliente.enviarResultados();
                cliente.enviarCalificacion();
                server.reiniciarVariablesExamen();
                JOptionPane.showMessageDialog(null, "Tiempo terminado!");
                break;     
            }
            
            
            //Evalúa si se desconectaron todos los clientes mientras se estaba realizando un examen
            //Se obtiene la calificacion, se pone en el label, se reinician las variables del examen
            //Se muestra un mensaje al servidor informando que todos se deconectaron y para el ciclo con break;
            else if ((server.getContadorClientes() == 0) && (server.isExamenIniciado())){
                
                float calificacionExamen = server.calificacionExamen();
                server.getlabelCalificacion().setText("Calificación del grupo: " + calificacionExamen);
                server.reiniciarVariablesExamen();
                JOptionPane.showMessageDialog(null, "Todos los clientes se han desconectado");
                break; 
            }
            
            //Se ejecuta el método slepp() que recibe como parámetro el número 1, esto permite que el hilo se duerma un milisegundo.
            try {
                sleep(1);
            } catch (InterruptedException ex) {
                System.out.println("Error");
            }
        }
        
    }
    
    
    
}
