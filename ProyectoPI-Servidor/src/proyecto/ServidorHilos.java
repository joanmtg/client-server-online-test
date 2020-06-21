/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: ServidorHilos.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : ServidorHilos 
* Responsabilidad : Esta clase es la que representa a cada uno de los clientes
* dentro del servidor, es la que permite realizar laconexión directa con el servidor
* y también la conexión por MultiCast. 
*/

package proyecto;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

//Clase ServidorHilos que extiende de Thread.
public class ServidorHilos extends Thread{
    
    //Atributos de la clase ServidorHilos
    private Socket socket;
    private int ID = -1;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private Servidor server;
    private int numeroPreguntaSeleccionada;
    private ArrayList<JLabel> labelsNombres;
    
    //Constructor de la clase ServidorHilos.
    public ServidorHilos(Servidor serv, Socket sock){
        
        socket = sock;
        server = serv;
        ID = socket.getPort();
        labelsNombres = serv.getLabelsNombres();          
                
    }
        
    //Se sobrecarga el método run(), el cual ejecuta una serie de instrucciones.
    public void run()
    {
        //Imprime un mensaje por consola.
        System.out.println("Server Thread " + ID + " running");
        
        //Se define un ciclo infinito
        while(true)
        {
            try
            {
                //Se crea la variable instruccion, cuyo valor será igual a lo que se encuentre en el DataInputStream entrada.
                String instruccion = entrada.readUTF();
                                                
                //Evalúa si el valor de la variable instrucción es igual a "Nombre de usuario", si ese es el caso, se ejecutan una
                //serie de instrucciones.
                if(instruccion.equals("Nombre de usuario")){
                    
                    //Se crea la variable nombreUsuario cuyo valor será igual al valor que traiga del DataInputStream entrada.
                    String nombreUsuario = entrada.readUTF();
                                                                                
                    //A través de un ciclo for evalúa un grupo de labels, y aquél cuyo texto sea "Desconectado", cambiará este texto por 
                    //el nombre del usuario (nombreUsuario), luego de hacer esto rompe el ciclo.
                    for (int i = 0; i < labelsNombres.size(); i++) {
                        JLabel label = labelsNombres.get(i);
                        
                        if(label.getText().equals("Desconectado")){
                            label.setText(nombreUsuario);
                            break;
                        }
                    }
                    
                    
                }
                
                //Evalúa si el valor de la variable instrucción es igual a "Mensaje de salida", si ese es el caso, se ejecutan una
                //serie de instrucciones.
                else if(instruccion.equals("Mensaje de salida")){
                    
                    //Se crea la variable nombreUsuario cuyo valor será igual al valor que traiga del DataInputStream entrada.
                    String nombreUsuario = entrada.readUTF();
                    //Se ejecuta el método disminuirContadorClientes().
                    server.disminuirContadorClientes();
                    
                    //Evalúa si es distinto del valor de retorno del método  isExamenIniciado, si es así, desactiva el botón botonIniciar que
                    //toma del servidor, y asigna un texto al label lEstadoExamen.
                    if(!server.isExamenIniciado()){
                        server.getBotonIniciar().setEnabled(false);
                        server.getlabelEstadoExamen().setText("Estado del Examen: No disponible");
                    }
                    
                    //A través de un ciclo for se evalúa un grupo de labels, y aquél cuyo texto sea igual al valor de la variable nombreUsuario,
                    //cambiará este texto por "Desconectado".
                    for (int i = 0; i < labelsNombres.size(); i++) {
                        JLabel label = labelsNombres.get(i);
                        
                        if(label.getText().equals(nombreUsuario)){
                            label.setText("Desconectado");
                            break;
                        }
                        
                    }
                    
                    //Ejecuta el método close().
                    this.close();
                    //Ejecuta el método interrupt() para interrumpir este hilo.
                    this.interrupt();
                    
                    
                }
                
                //Evalúa si el valor de la variable instrucción es igual a "Solicitud de pregunta", si ese es el caso, se ejecutan una
                //serie de instrucciones.
                else if(instruccion.equals("Solicitud de pregunta")){
                    
                    //Le asigna al atributo numeroPreguntaSeleccionada el valor entero que trae del DataInputStream entrada.
                    numeroPreguntaSeleccionada = entrada.readInt();
                    
                    //Evalúa si el valor de numeroPreguntaSeleccionada es distinto de cero.
                    if(numeroPreguntaSeleccionada != 0){
                        
                        //Crea un objeto de tipo Pregunta y lo iguala al valor de retorno del método preguntaSeleccionadaCliente() el cual
                        //recibe como parámetro el atributo numeroPreguntaSeleccionada.
                        Pregunta preguntaSeleccionada = server.preguntaSeleccionadaCliente(numeroPreguntaSeleccionada);
                        
                        //Crea un arreglo estático de tipo String y, lo iguala al valor de retorno del método getRespuestas().
                        String[] opciones = preguntaSeleccionada.getRespuestas();
                        
                        //Escribe en el DataOutputStream salida lo que retorna el método getEnunciado().
                        salida.writeUTF(preguntaSeleccionada.getEnunciado());
                        //Escribe en el DataOutputStream salida lo que se encuentra en la posición 0 del arreglo opciones.
                        salida.writeUTF(opciones[0]);
                        //Escribe en el DataOutputStream salida lo que se encuentra en la posición 1 del arreglo opciones.
                        salida.writeUTF(opciones[1]);
                        //Escribe en el DataOutputStream salida lo que se encuentra en la posición 2 del arreglo opciones.
                        salida.writeUTF(opciones[2]);
                        //Escribe en el DataOutputStream salida lo que se encuentra en la posición 3 del arreglo opciones.
                        salida.writeUTF(opciones[3]);
                        //Se ejecuta el método flush() para asegurar de que todo lo que hay en el canal de salida (DataOutputStream), se haya
                        //enviado de forma correcta.
                        salida.flush();
                        
                        //Se ejecuta el método modificarEstadoPregunta(), el cual recibe como parámetro el valor de retorno del método 
                        //getNumeroPregunta y el String "OCUPADA".
                        server.modificarEstadoPregunta(preguntaSeleccionada.getNumeroPregunta(), "OCUPADA");
                        //Crea la variable de tipo String numeroPreguntaOcupada cuyo valor es igual a "numeroPreguntaOcupada" concatenada con
                        //el valor del atributo numeroPreguntaSeleccionada.
                        String numeroPreguntaOcupada = "numeroPreguntaOcupada "+numeroPreguntaSeleccionada;
                        //Ejecuta el método enviarPorMulticast() que recibe como parámetro la variable numeroPreguntaOcupada.
                        enviarPorMultiCast(numeroPreguntaOcupada);
                    }
                    
                }
                
                //Evalúa si el valor de la variable instrucción es igual a "Envío de Respuesta", si ese es el caso, se ejecutan una
                //serie de instrucciones.
                else if (instruccion.equals("Envío de Respuesta")){
                    
                    //Crea la variable de tipo entero numeroPregunta cuyo valor será igual al valor entero que traiga del DataInputStram entrada.
                    int numeroPregunta = entrada.readInt();
                    //Crea la variable de tipo entero OpcionEscogida cuyo valor será igual al valor entero que traiga del DataInputStram entrada.
                    int opcionEscogida = entrada.readInt();
                    //Crea la variable de tipo String nombreUsuario cuyo valor será igual a la cadena que traiga del DataInputStram entrada.
                    String nombreUsuario = entrada.readUTF();
                                                          
                    //Ejecuta el método calificarPregunta(), que recibe como parámetros las variables nombreUsuario, numeroPregunta y opcionEscogida.
                    server.calificarPregunta(nombreUsuario, numeroPregunta, opcionEscogida);
                    //Ejecuta el método verificarFinalExamen().                                       
                    server.verificarFinalExamen();
                    
                }
                //Evalúa si el valor de la variable instrucción es igual a "Cancelar pregunta", si ese es el caso, se ejecutan una
                //serie de instrucciones.
                else if (instruccion.equals("Cancelar pregunta")){
                    
                    //Crea la variable de tipo entero numeroPregunta cuyo valor será igual al valor entero que traiga del DataInputStram entrada.
                    int numeroPreguntaCancelada = entrada.readInt();
                    //Se crea un objeto de tipo Pregunta y se igual al valor de retorno del método preguntaSeleccionada() que recibe como 
                    //parámetro la variable numeroPreguntaCancelada.
                    Pregunta preguntaSeleccionada = server.preguntaSeleccionadaCliente(numeroPreguntaCancelada);
                    
                    //Se ejecuta el método modificarEstadoPregunta() que recibe como parámetros el valor de retorno del método getNumeroPregunta() y
                    //la cadena "DISPONIBLE".
                    server.modificarEstadoPregunta(preguntaSeleccionada.getNumeroPregunta(), "DISPONIBLE");
                    
                    //Crea la variable de tipo String numPreguntaOcupada cuyo valor es igual a "numeroPreguntaCancelada" concatenada con
                    //el valor del atributo numeroPreguntaCancelada.
                    String numPreguntaOcupada = "numeroPreguntaCancelada "+numeroPreguntaCancelada;
                    
                    //Se ejecuta el método enviarPorMulticast(), que recibe como parámetro la variable numPreguntaOcupada.
                    enviarPorMultiCast(numPreguntaOcupada);
                                        
                }                                              
                    
                            
            }catch(IOException ioe)
            {
                ioe.getMessage();
            }
        }
    }
    
    
    //Se define el método enviarPreguntas(), el cual crea la variable de tipo String cantidadPreguntas cuyo valor es igual a la cadena
    //"cantidadPreguntas" concatenado con el valor de retorno del método getNumeroPreguntasExamen(), luego ejecuta el método  
    //enviarPorMulticast() el cual recibe como parámetro la variable cantidadPreguntas.
    public void enviarPreguntas() {
        try{
            String cantidadPreguntas = "cantidadPreguntas "+server.getNumeroPreguntasExamen();
            enviarPorMultiCast(cantidadPreguntas);
                        
        }catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
    }
    
    //Se define el método enviarCalificacion(), el cual crea la variable de tipo double calificacionExamen cuyo valor es igual a el valor de
    //retorno del método calificacionExamen(), luego le asigna al label lCalificacion un texto concatenado con el valor de calificacionExamen,
    //posteriormente crea la variable de tipo String calificacion que será igual a "finExamen" concatenado con el valor de calificacionExamen
    //luego ejecuta el método enviarPorMulticast() el cual recibe como parámetro la variable cantidadPreguntas.
    public void enviarCalificacion(){
        try{
            float calificacionExamen = server.calificacionExamen();
            server.getlabelCalificacion().setText("Calificación del grupo: " + calificacionExamen);
            
            String calificacion = "finExamen " + calificacionExamen;
            enviarPorMultiCast(calificacion);
            
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //Se define el método enviarResultados(), el cual ejecuta una serie de instrucciones.
    public void enviarResultados(){
        //Se crea un ArrayList<> de tipo String que será igual al valor de retorno de ejecutar el método resultadosExamen().
        ArrayList<String> resultados = server.getAdminBd().resultadosExamen(server.getNumeroExamen());
        //Se crea una variable cuyo valor es la cadena vacía.
        String informacion= "";
        
                
        //Se crea un ciclo for para tomar los valores del ArrayList<> y asignarlos a la variable información, estos valores tomados del arreglo
        //se concatenan con la cadena "resultadoPregunta", imprime el String informacion y posteriormente ejecuta el método enviarPorMulticast()
        //que recibe como parámetro la variable informacion.
        try{
            for (int i = 0; i < resultados.size(); i++) {
                
                informacion = "resultadoPregunta " + resultados.get(i);
                enviarPorMultiCast(informacion);
            
            }
                        
        
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //Se define el método enviarPorMulticast() el cual recibe como parámetro un variable de tipo String, el método toma los bytes de esta variable
    //y los almacena en un arreglo estático, luego toma el datagrama e introduce el arreglo como dato, posteriormente le da un tamaño al datagrama,
    //el cual será el mismo que el del arreglo y por último toma el multicast y envía el datagrama.
    public void enviarPorMultiCast(String mensaje) throws IOException{
                
        byte[] buffer = mensaje.getBytes();
        server.getDatagrama().setData(buffer);
        server.getDatagrama().setLength(buffer.length);
        server.getMulticast().send(server.getDatagrama());
        
    }
    
    //Se define el método open(), el cual se encarga de abrir los canales de entrada y salida.
    public void open() throws IOException
    {
        entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        salida = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        salida.flush();
    }
    
    //Se define el método close(), el cual se encarga de cerrar el socket, los canales de entrada y salida (entrada, salida), mientras
    //estos sean distintos de null.
    public void close() throws IOException
    {
        if(socket != null){
            socket.close();
        }
        if(entrada != null){
            entrada.close();
        }
        if(salida != null){
            salida.close();
        }
        
    }
    
}
