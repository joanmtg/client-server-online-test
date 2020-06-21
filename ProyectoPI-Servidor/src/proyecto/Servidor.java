/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: Servidor.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : Servidor 
* Responsabilidad : Esta clase se encarga de gestionar un servidor utilizado
* para permitir a conexión de clientes, cuyo objetivo es realizar un examen
* de manera colaborativa. Esta claase hereda de la clase Thread.
* 
*/

package proyecto;

//Se importan las librerías a utilizarse

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Servidor extends Thread{
    
    //Atributos de la clase
    
    //Administrador de la base de datos
    private AdministradorBD adminBd;  
    //ArrayList temporal de categorías
    private ArrayList<Categoria> categorias; 
    
    //Componentes de la interfaz de servidor que serán cambiados desde esta clase
    private ArrayList<JLabel> labelsNombres;
    private JButton botonIniciar;
    private JLabel lEstadoExamen;
    private JLabel lTiempo;
    private JLabel lCalificacion;
    private JTabbedPane pestañas;
    
    //ServerSocket para la recepción de clientes
    private ServerSocket serverSocket;
    //Representa a cada cliente dentro del servidor
    private ServidorHilos cliente;
    
    //Variables booleanas referentes al estado de realización del examen
    private boolean examenIniciado; 
    private boolean examenFinalizado;
    private boolean examenRecienIniciado;
    
    //Lleva la cuenta de los clientes conectados
    private int contadorClientes;
    
    //Información del examen que se está realizando
    private int numeroExamenActual;
    private int numeroPreguntasExamen;
    private int duracionExamenActual;
    
    //Objeto temporizador, para llevar una cuenta regresiva
    private Temporizador temporizador;
    //Objeto que verifica constantemente si el examen se ha finalizado
    private VerificaFinal verificaFinal;
    
    //Variables utilizadas en el envío de información por MultiCast
    private MulticastSocket multicast;
    byte [] arregloBytes = new byte [0];
    private DatagramPacket datagrama;

    /**
     * Constructor de la clase.
     */
    
    public Servidor() {
        
        //Se inicializa el objeto de la clase AdministradorBD
        adminBd = new AdministradorBD();
        
        //Se inicializa el ArrayList de categorias
        categorias = new ArrayList<>();
           
    }
    
    /**
     * Método que recibe las componentes de una interfaz y las asigna a los atributos
     * destinados para este objetivo
     * @param labelsNombres
     * @param botonIniciar
     * @param lEstadoExamen
     * @param lTiempo
     * @param pestañas
     * @param lCalificacion 
     */

    public void agregarComponentesInterfaz(ArrayList<JLabel> labelsNombres, JButton botonIniciar, JLabel lEstadoExamen, JLabel lTiempo, JTabbedPane pestañas, JLabel lCalificacion) {
        this.labelsNombres = labelsNombres;
        this.botonIniciar = botonIniciar;
        this.lEstadoExamen = lEstadoExamen;
        this.lTiempo = lTiempo;
        this.pestañas = pestañas;
        this.lCalificacion = lCalificacion;
    }
    
    /**
     * Métodos GET de los atributos de la clase. 
     */

    public ArrayList<JLabel> getLabelsNombres() {
        return labelsNombres;
    }

    public JLabel getlabelTiempo() {
        return lTiempo;
    }

    public JLabel getlabelEstadoExamen() {
        return lEstadoExamen;
    }   
    
    public int getDuracionExamenActual() {
        return duracionExamenActual;
    }

    public JButton getBotonIniciar() {
        return botonIniciar;
    }

    public JLabel getlabelCalificacion() {
        return lCalificacion;
    }

    public JTabbedPane getPestañas() {
        return pestañas;
    }

    public int getNumeroPreguntasExamen() {
        return numeroPreguntasExamen;
    }
    
    public AdministradorBD getAdminBd() {
        return adminBd;
    }
    
    public ServidorHilos getServidorHilos(){
        return cliente;
    }

    public ArrayList<Categoria> getCategorias() {
        return categorias;
    }
    
    public boolean isExamenIniciado() {
        return examenIniciado;
    }
    
    public boolean isExamenFinalizado() {
        
        return examenFinalizado;
    }
      
    public int getNumeroExamen() {
        return numeroExamenActual;
    }
    
    public MulticastSocket getMulticast() {
        return multicast;
    }

    public DatagramPacket getDatagrama() {
        return datagrama;
    }
    
    public int getContadorClientes(){
        return contadorClientes;
    }
    
    /**
     * Métodos SET de los atributos de la clase. 
     */
    
    public void setDuracionExamenActual(int duracionExamenActual) {
        this.duracionExamenActual = duracionExamenActual;
    } 

    public void setNumeroExamen(int numeroExamen) {
        this.numeroExamenActual = numeroExamen;
    }

    public void setNumeroPreguntasExamen(int numeroPreguntasExamen) {
        this.numeroPreguntasExamen = numeroPreguntasExamen;
    }
    
    /**
     * Disminuye el atributo contadorClientes en 1
     */
    
    public void disminuirContadorClientes(){
        contadorClientes--;
    }

    /**
     * Método que envía las preguntas a los clientes por medio de MultiCast
     * y cambia ciertas variables referentes al estado del examen
     */
    
    public void enviarPreguntas(){
        
        //Llama al método enviarPreguntas() de la clase ServidorHilos
        cliente.enviarPreguntas();
        //Crea un objeto de la clase temporizador y lo guarda en el atributo temporiador de la clase
        temporizador = new Temporizador(duracionExamenActual, lTiempo, datagrama, multicast);
        //Comienza el hilo definido anteriormente
        temporizador.start();
        
        //Cambia las variables referentes al estado del examen
        examenRecienIniciado = true;
        examenIniciado = true;
    }
    
    /**
     * Método que recibe como entero un puerto y abre la conexión para que
     * los clientes puedan conectarse a este servidor. 
     * @param port 
     */
    
    public void abrirConexion(int port){
        
        //Bloque try para el manejo de excepciones de tipo IO
        try{
            //Crea y guarda un ServerSocket en el puerto recibido como parámetro
            serverSocket = new ServerSocket(port);
            //Llama al método de la clase para iniciar el MultiCast
            arrancarMultiCast();
            //Comienza el hilo (se ejecuta el método run())
            start();             
        }catch(IOException ioe){
            //Manejo de excepciones
            System.out.println(ioe);
        }  
    }
    
    /**
     * Método que se encarga de abrir un MultiCastSocket y crear un DatagramPacket
     * en cierto Host y en cierto puerto
     * @throws IOException 
     */
    
    public void arrancarMultiCast() throws IOException{
        
        //Se crea y guarda un MulticastSocket
        multicast = new MulticastSocket();
        //Se obtiene el InnetAdress del host "234.0.0.1"
        InetAddress grupo = InetAddress.getByName("234.0.0.1");
        
        /**
         * Se crea y guarda un DatagramPacket pasándole como parámetros
         * El paquete
         * EL tamaño del paquete
         * La dirección de destino
         * El puerto de destino
         */
        datagrama = new DatagramPacket(arregloBytes, 0, grupo, 10001);
    }
    
    
    /**
     * Método que recibe 5 strings y valida si dichos Strings son vacíos ("")
     * Si hay sólo uno que esté vacío, devuelve true
     * Si no hay ninguno que sea vacío, de vuelve false
     * @param enunciado
     * @param opcion1
     * @param opcion2
     * @param opcion3
     * @param opcion4
     * @return 
     */
    
    public boolean camposPreguntaVacios(String enunciado, String opcion1, String opcion2, String opcion3, String opcion4){
        
        //Verificamos si hay alguno vacío
        if (enunciado.trim().equals("") || opcion1.trim().equals("") || opcion2.trim().equals("") || opcion3.trim().equals("") || opcion4.trim().equals("")){
            
            //Devuelve true
            return true;
        }
        
        else{
            //De lo contrario, devuelve false
            return false;
        }
    }
    
    /**
     * Método que trae la información de las categorías de la base de datos,
     * y lo guarda en un arreglo temporal de categorías, junto con sus pregutas
     */
    
    public void informacionBaseDeDatos(){
        
        //Limpia el ArrayList temporal de categorías
        categorias.clear();
        
        //Se guardan los nombres de las categorías en un ArrayList de Strings
        ArrayList<String> nombresCategorias = adminBd.obtenerCategorias();
        
        //Se declara e inicializa un ArrayList de preguntas
        ArrayList<Pregunta> preguntas = new ArrayList<>();
        
        //Ciclo for que recorre el ArrayList de los nombres de las categorías
        
        for (int i = 0; i < nombresCategorias.size(); i++) {
            
            //Trae las preguntas que pertenecen categoría de la posicicón actual
            preguntas = adminBd.obtenerPreguntasPorCategoria(nombresCategorias.get(i));
            
            //Crea una nueva categoría pasándole como parámetro el nombre
            //de la categoría actual y las preguntas de dicha categoría
            Categoria nuevaCategoria = new Categoria(nombresCategorias.get(i), preguntas);
            
            //Se añade la categoría al ArrayList de categorías
            categorias.add(nuevaCategoria);
        }
    }
    
    /**
     * Método que recibe un String que representa el nombre de una categoría
     * y retorna el objeto Categoría guardado en el arreglo temporal cuyo nombre
     * coincide con el ingresado por parámetro
     * @param nombreCategoria
     * @return 
     */
    
    public Categoria extraerCategoria(String nombreCategoria){
        
        //Se declara e inicializa un objeto de la clase Categoría
        Categoria categoria = null;
        
        //Ciclo for que recorre el ArrayList de categorías
        
        for (int i = 0; i < categorias.size(); i++) {
                        
            //Se verifica si el nombre de la categoría actual es igual al ingresado como parámetro
            if (categorias.get(i).getNombreCategoria().equals(nombreCategoria)){
                
                //Se asigna la categoría actual a la variable ya definida
                categoria = categorias.get(i);
                //Se detiene el ciclo
                break;
            }
        }
        
        //Se retorna el valor de la variable categoria
        return categoria;
    }
    
    /**
     * Método que recibe el nombre de una categoría y el id de una pregunta
     * y elimina la pregunta del arreglo de preguntas de la categoría que tenga
     * el mismo nombre que el ingresado como parámetro
     * @param nombreCategoria
     * @param id 
     */
    
       
    public void eliminarPregunta(String nombreCategoria ,int id){
        
        //Se extraen las preguntas de la categoría y se guardan en un ArrayList
        ArrayList<Pregunta> preguntasCategoria = extraerCategoria(nombreCategoria).getPreguntas();
        
        //Se recorren las preguntas de la categoría
        
        for(int i = 0; i < preguntasCategoria.size(); i++)
        {
            if(preguntasCategoria.get(i).getNumeroPregunta() == id){
         
                //Si el id de la pregunta actual coincide con el id ingresado como parámetro,
                //se remueve la pregunta del ArrayList de preguntas de la categoría
                preguntasCategoria.remove(i);
            }
        }        
        
    }
     
    
    /**
     * Método que recibe el nombre de una categoría y un JComboBox, para añadir
     * las preguntas de esta categoría al JComboBox  ingresado
     * @param nombreCategoria
     * @param cbPreguntas 
     */
    
    public void preguntasPorCategoria(String nombreCategoria, JComboBox cbPreguntas){
        
        //Se extraen las preguntas de la categoría y se guardan en un ArrayList de preguntas
        ArrayList<Pregunta> preguntasCategoria = extraerCategoria(nombreCategoria).getPreguntas();
        
        //Se remueven todos los items del JComboBox de preguntas y se añade
        //el ítem "Seleccione una pregunta"
        cbPreguntas.removeAllItems();
        cbPreguntas.addItem("Seleccione una pregunta");
        
        //Ciclo for que recorre las preguntas y las añade al JComboBox de esta forma:
        // "Pregunta ID"
        
        for(int i = 0; i < preguntasCategoria.size(); i++){
            
            //Se añade al JComboBox el ítem "Pregunta ID"
            cbPreguntas.addItem("Pregunta " + preguntasCategoria.get(i).getNumeroPregunta());
        }
        
        
    }
    
    /**
     * Método que recibe el número de una pregunta, trae de la base de datos
     * la pregunta con dicho número (único) y la retorna
     * @param numeroPregunta
     * @return 
     */
        
    public Pregunta preguntaSeleccionadaCliente(int numeroPregunta) {
       
        //Trae de la base de datos la pregunta
        Pregunta preguntaSeleccionada = adminBd.obtenerPreguntaExamen(numeroExamenActual, numeroPregunta);
        
        //Devuelve la pregunta seleccionada
        return preguntaSeleccionada;
    }
    
    /**
     * Método que recibe un número de pregunta, un estado como String y 
     * llama al método de la base de datos que modifica el estado de la pregunta
     * @param numeroPreguntaGeneral
     * @param estadoPregunta 
     */
    
    public void modificarEstadoPregunta(int numeroPreguntaGeneral, String estadoPregunta){
        
        //Se llama al método de la clase AdministradorBD que modifica el estado de la pregunta
        adminBd.modificarEstadoPregunta(numeroPreguntaGeneral, estadoPregunta, numeroExamenActual);
    }
    
    /**
     * Método que califica una pregunta en la base de datos
     * @param nombreUsuario Usuario que respondió la pregunta
     * @param numeroPregunta Número de la pregunta (único)
     * @param opcionEscogida pción escogida por el usuario
     */
    
    public void calificarPregunta(String nombreUsuario, int numeroPregunta, int opcionEscogida){
        
        //Se extrae la pregunta con el número ingresado como parámetro
        Pregunta preguntaRespondida = preguntaSeleccionadaCliente(numeroPregunta);
        
        //Se extraen los datos de la pregunta
        int opcionCorrecta = preguntaRespondida.getRespuestaCorrecta();
        int identificadorPregunta = preguntaRespondida.getNumeroPregunta();
        
        //Se inicaliza una variable entera calificación en 0
        int calificacion = 0;
        
        if(opcionEscogida == opcionCorrecta){
            //Si la opción escogida es igual a la correcta, la calificación de la pregunta es 1
            calificacion = 1;   
        }
        
        //Se llama al método de AdministradorBD que califica la pregunta
        adminBd.calificarPreguntaBD(identificadorPregunta, calificacion, opcionEscogida, nombreUsuario, numeroExamenActual);
        
    }
    
    /**
     * Método que devuelve la calificación del examen que se está realizando actualmente
     * @return 
     */
    
    public float calificacionExamen(){
        
        //Se declara e inicializa la variable float calificacion en 0
        float calificacion = 0;
        
        //Se calcula el número de preguntas correctas que hay en la base de datos
        
        int numeroCorrectas = adminBd.numeroRegistros("preguntasseleccionadas WHERE numeroExamen = " + numeroExamenActual  + " AND calificacion = 1");
        
        //Se guarda la cantidad de preguntas del examen en una variable de tipo float
        float cantidadPreguntas = numeroPreguntasExamen;
        
        //Se calcula la calificación y se guarda en la variable calificacion
        calificacion = numeroCorrectas * (5 / cantidadPreguntas);
                
        
        //Se retorna el valor de la variable calificacion
        return calificacion;        
        
    }
    
    /**
     * Método que verifica si el numero de preguntas repsondidas de la base de datos
     * es igual al número de preguntas del examne, es decir, si los usuarios
     * ya respondieron todas las preguntas del examen
     */
    
    public void verificarFinalExamen(){
        
        //Se calcula el número de preguntas respondidas de la base de datos        
        int numeroRespondidas = adminBd.numeroRegistros("preguntasseleccionadas WHERE numeroExamen = " 
                                                                  + numeroExamenActual
                                                                  + " AND estadoPregunta = 'RESPONDIDA'");
        
                
        if(numeroRespondidas == numeroPreguntasExamen){
            
            //Si el número de respondidas es igual al número de preguntas del examen,
            //se cambia el atributo examenFinalizado a true
            examenFinalizado = true;            
        }       
        
        
    }
    
    
    /**
     * Método que reinicia las variables y los componentes utilizados
     * durante el desarrollo del examen para que así puedan ser utilizados
     * cuando el servidor vaya a realizar otro examen en la misma ejecución.
     */
    
    public void reiniciarVariablesExamen(){
                
        botonIniciar.setEnabled(false);
        lEstadoExamen.setText("Estado del Examen: No disponible");
        lTiempo.setText("00:00"); 
        examenIniciado = false;
        examenFinalizado = false;
        numeroPreguntasExamen = 0;
        duracionExamenActual = 0;
        temporizador = null;
        verificaFinal.interrupt();
        verificaFinal = null;
        cliente = null;
        //this.interrupt();        
        
        //Se desactiva la pestaña de desarrollo del examen
        pestañas.setEnabledAt(3, false);
        //Se activa la pestaña de visualización de resultados
        pestañas.setEnabledAt(4, true);
        //Se selecciona la pestaña de visualización de resultados
        pestañas.setSelectedIndex(4);
                
    }
    
    
    /**
     * Método run() sobreescrito
     * Éste método se hereda de la clase Thread
     * En este método se está escuchando constantemente para
     * aceptar clientes que quieran conectarse al servidor a realizar un examen
     */
   
    @Override
    public void run() {
        
        //Mientras que el contador de clientes sea menor o igual a 3
        while(contadorClientes <= 3)
        {
            //Bloque try para el manejo de excepciones de tipo IO
            try {
                
                //Si el contador de clientes es menor a 3 y el examen no ha iniciado
                if((contadorClientes < 3) && !examenIniciado){
                                    
                    //Si el serverSocket está cerrado
                    if(serverSocket.isClosed()){
                        //Se crea un nuevo serverSocket en el mismo puerto
                        serverSocket = new ServerSocket(12345);
                        
                    }
                    
                    //Se crea un objeto de la clase ServidorHilos y se asigna al atributo cliente
                    cliente = new ServidorHilos(this, serverSocket.accept());
                     
                    //Mensaje de que el cliente fue aceptado
                    System.out.println("Cliente Aceptado: "+cliente.getName());
                    
                    //Se abren los flujos del objeto ServidroHilos creados y se
                    //invoca al método run() de este objeto que hereda de Thread
                    cliente.open();
                    cliente.start();
                    
                    //Se aumenta el contador de clientes en 1
                    contadorClientes++;
                    
                    //Si es el tercer cliente en entrar
                    if(contadorClientes == 3){
                        
                        //Se activa el botón de iniciar el examen
                        botonIniciar.setEnabled(true);
                        //Se muestra en el label del estado del examen un mensaje de que el exameen está preparado
                        lEstadoExamen.setText("Estado del Examen: Preparado");
                        //Se cierra el serverSocket para no aceptar más clientes
                        serverSocket.close();                        
                        
                    }
                    
                }
                
                //Si el examen está recien iniciado                
                else if(examenRecienIniciado){
                    
                    //Se crea y guarda un objeto de la clase verificaFinal
                    verificaFinal = new VerificaFinal(temporizador, this, cliente);
                    //Se llama al método run() del objeto creado que hereda de Thread
                    verificaFinal.start();
                    //Se cierra el ServerSocket
                    serverSocket.close();
                    //Se asigna false al atributo examenRecienIniciado
                    examenRecienIniciado = false;
                }              
                
            } catch (IOException ex) {
                //Manejo de excepciones de tipo IO
                System.out.println(ex);
            }
        }
        
    }
   
}
