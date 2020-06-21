/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: Temporizador.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : Temporizador 
* Responsabilidad : Esta clase hereda de Thread y se encarga de llevar el
* tiempo como una cuenta regresiva dependiendo de una cantidad de minutos
* que se recibe como parámetro en el constructor. Cada segundo, va a enviar
* por medio de un MultiCastSocket la información de dicho tiempo.
*  
* 
*/

package proyecto;

//Se importan las librerías a utilizarse

import java.io.*;
import java.net.*;
import javax.swing.*;

public class Temporizador extends Thread{
 
    //Atributos de la clase:
    
    private int segundos;  
    private int minutos;
    private boolean examenFinalizado;
    private JLabel label; //JLabel que muetsra la información del tiempo
    
    //DatagramPacket y MulticastSocket utilizados para el envío de información   
    private DatagramPacket datagrama;
    private MulticastSocket multiCast;
    
    
    /**
     * Constructor de la clase
     *      
     * @param min  Cantidad de minutos con que comienza el temporizador
     * @param label JLabel a modificarse
     * @param datagrama 
     * @param multiCast 
     */
    
    public Temporizador(int min, JLabel label, DatagramPacket datagrama, MulticastSocket multiCast){
        
        //Se asignan las variables recibidas por parámetro a los atriubutos de la clase
        minutos = min;
        this.label = label;
        this.datagrama = datagrama;
        this.multiCast = multiCast;       
    }
    
    /**
     * Método get que devuelve el valor booleano de la variable examenFinalizado
     * @return 
     */
    
    public boolean getExamenFinalizado() {
        return examenFinalizado;
    }
    
    /**
     * Método sobreescrito run() de la clase Thread, que es la clase de la cual
     * se hereda. Se ejecuta cuando se llama el método start(). En este caso,
     * este método se va a encargar de llevar el tiempo como una cuenta regresiva.
     */
    
    @Override
    public void run()
    {
        //Try Catch para el manejo de la excepción InterruptedException
        try
        {
            //Ciclo infinito que acutaliza constantemente el tiempo
            while(true)
            {   
                //Se revisa si el tiempo se haterminado, es decir, que los
                //minutos y los segundos sean 0
                if((minutos == 0) && (segundos==0)){
                    
                    //Llama al método enviarTiempo que lo envía por MultiCast
                    enviarTiempo();
                    //Le asigna el valor de true a la variable examenFinalizado
                    examenFinalizado = true;
                    //Interrumpe el hilo
                    interrupt();
                    
                }
                
                //Si el tiempo no se ha terminado:
                
                else{
                    //Envia el tiempo por MultiCast
                    enviarTiempo();
                    //Pone a dormir el hilo por 999 milisegundos
                    sleep(999);
                    
                    //Si los segundos son distintos de 0
                    if(segundos != 0){
                        //Decrementa el valor de los segundos en 1
                        segundos--;
                    }
                    
                    //Si la condición anterior no se cumple, quiere decir que el
                    //valor de los segundos es 0. Se evalúa si los minutos son distintos de 0
                    else if(minutos != 0){
                        
                        //Los segundos toman el valor de 59
                        segundos = 59;
                        //Se decrementa la variable minutos en 1
                        minutos--;
                    }          
                }              
            }
        } catch(InterruptedException ie)
        {
            //Se maneja la excepción
            System.out.println(ie.getMessage());
        }
    }
    
    /**
     * Método enviarTiempo()
     * 
     * Se utiliza para enviar por multicast el valor de los minutos y de los 
     * segundos que hayan en el momento en que sea llamado el método 
     */
    
    public void enviarTiempo(){
        
        //Se guardan los minutos y los segundos en variables de tipo String
        String min = "" + minutos;
        String sec = "" + segundos;
        
        
        //Si los minutos son menores que 10, se les concatena un 0 a la izquierda
        
        if(minutos < 10){
            min = "0" + minutos;
        }
        
        //Si los segundos son menores que 10, se les concatena un 0 a la izquierda
        
        if (segundos < 10){
            sec = "0" + segundos;
        }
        
        //Se concatenan minutos y segundos separados por ":"
        String tiempo = min + ":" + sec;  
        //Mensaje a enviar por multiCast con el identificador "Tiempo" de primero
        String tiempoEnviar = "Tiempo " + tiempo; 
        //Se asigna el texto con la información del tiempo al label
        label.setText(tiempo);  
            
        //Se guarda en un arreglo de bytes los bytes del mensaje a enviar       
        byte [] mensaje = tiempoEnviar.getBytes();
        
        //Se asigna el arreglo de bytes al datagrama como información
        datagrama.setData(mensaje);
        
        //Se asigna el tamaño al datgrama con el tamaño del mensaje
        datagrama.setLength(mensaje.length);
        
        try {
            //Se envía por medio del MultiCastSocket el datagrama con el método send
            multiCast.send(datagrama);
        } catch (IOException ex) {
            
            //Se manejan posibles excpeciones de tipo IOException
            System.out.println("Error");
        }
        
    }    
}
