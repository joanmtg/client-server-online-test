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

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;


/**
 *
 * @author joanmatg
 */
public class Cliente {
    
    // Declaración de variables globales
    
    private Socket socket = null; // Socket que se utilizará para realizar la conexión con el servidor
    private DataInputStream  entrada = null; // DataInpuntStream para recibir los datos que el servidor envía
    private DataOutputStream salida = null; // DataOutputStream para enviar los datos necesarios al servidor
    private String nombreCliente; // String en el cual se almacenará el nombre del cliente
    private MulticastSocket socketMulti; // MulticastSocket que se utilizará para enviar información a todos los clientes
    private JComboBox cbPreguntas; // ComboBox en el que se visualizarán las preguntas del examen.
    private JComboBox cbPreguntasFinal; // ComboBox en el que se visualizarán las preguntas para poder ver los resultados
    private JButton bSeleccionarPregunta; // Boton para saber cuando una pregunta es seleccionada del comboBox 
    private ArrayList<String> resultados; // Arreglo en en que se van a almacenar los resultados recibidos del servidor
    private JTabbedPane pestañas; // Pestañas de las ventanas
    private JLabel lCalificacion; // Label para mostrar la calificación
    private JLabel lTiempo;     // Label para mostrar el tiempo restante
    private InetAddress grupoClientes; // Grupo de clientes que van a estar conectados con el servidor
    private Thread escuchandoMulti; // Hilo que va a estar escuchando constantemente por el puerto multicast
    private boolean conexionRealizada; // Booleano para saber si la conexión entre el cliente y el servidor se hizo o no.
    

    /**
     * Constructor
     * @param cbPreguntas
     * @param cbPreguntasFinal
     * @param bSeleccionarPregunta
     * @param lTiempo
     * @param lCalificacion
     * @param pestañas 
     */
    public Cliente(JComboBox cbPreguntas, JComboBox cbPreguntasFinal,JButton bSeleccionarPregunta, JLabel lTiempo, JLabel lCalificacion, JTabbedPane pestañas)
    {
        this.cbPreguntas = cbPreguntas;
        this.cbPreguntasFinal = cbPreguntasFinal;
        this.bSeleccionarPregunta = bSeleccionarPregunta;
        this.lCalificacion = lCalificacion;
        this.pestañas = pestañas;
        this.lTiempo = lTiempo;
        this.resultados = new ArrayList<>();
                   
    }
    
    /**
     * Método que retorna el nombre del cliente
     * @return 
     */
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    /**
     * Método que recibe un parametro con el nombre del cliente y lo asigna a la variable local
     * del nombre del cliente.
     * @param nombreCliente 
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    /**
     * Método que retorna el booleano que designa si la conexión entre el cliente y el servidor
     * se realizó o no.
     * @return 
     */
    public boolean isConexionRealizada() {
        return conexionRealizada;
    }

    /**
     * Método que recibe como párametros el nombre de usuario, el nombre del servidor y el puerto,
     * se encarga de realizar una conexión entre el cliente y el servidor, posteriormente empieza
     * a escuchar el puerto multicast esperando que el servidor envíe algún dato, por último
     * le da la bienvenida al usuario.
     * @param nombreUsuario
     * @param nombreServidor
     * @param puertoServidor 
     */ 
    public void enviarNombreUsuario(String nombreUsuario , String nombreServidor , int puertoServidor){
        
        // Se imprime  un mensaje en pantalla informando que se esta tratando de conectar al servidoe
        JOptionPane.showMessageDialog(null,"Estableciendo conexión. Por favor espere...", "Conexión", JOptionPane.INFORMATION_MESSAGE);
                
        try
        {
            // Se inicializa el socket por el cual el servidor y el cliente se van a conectar.
            socket = new Socket(nombreServidor, puertoServidor);
            System.out.println("\n Conectado a: " + socket); // Se imprime por consola que el cliente se conectó a...
            comenzar(); // Se abren los flujos para que el cliente pueda recibir o enviar información
            empezarEscucharMulticast(); // Se llama el método que inicializa todo lo relacionado con multicast y crea un hilo
                                        // que mantiene esperando información desde el servidor.
            
            // Se imprime en pantalla un mensaje dando la bienvenida al nuevo cliente
            JOptionPane.showMessageDialog(null, "Bienvenido(a) : "+ nombreUsuario);
            conexionRealizada = true; // Se cambia el valor del booleano de conexion realizada a true, pues ya se ha realizado
                                      // la conexión entre el cliente y el servidor.
        }catch(UnknownHostException uhe)
        {
            
            //System.out.println(uhe.getMessage());
           
        }
        catch(IOException ioe)
        {
            // En caso de que suceda algun problema con la conexión, se imprime un mensaje en pantalla informando
            // que el cliente no se pudo conectar al servidor.
            System.out.println(ioe.getMessage());
            JOptionPane.showMessageDialog(null, "No se puede conectar al servidor", "Error", JOptionPane.ERROR_MESSAGE);
                        
        }  
        
        // En caso de que la conexión se haya realizado correctamente, entonces se envía la instrucción de lo que el
        // cliente le va a enviar al servidor y posteriormente se procede a enviarle el nombre de usuario.
        if(conexionRealizada){
            try{
                salida.writeUTF("Nombre de usuario"); // Envío de la instrucción
                salida.writeUTF(nombreUsuario);  // Envío del nombre de usuario
            
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
                
        
    }
    
    /**
     * Método que se encarga de enviar la solicitud de una pregunta al servidor, dependiendo del
     * número de pregunta recibida como párametro. Luego se procede a leer la información
     * de la pregunta que envía el servidor y se deshabilitan el comboBox de preguntas y el boton
     * para seleccionar una pregunta, por último se retorna un vector con la información de la
     * pregunta.
     * @param numeroPregunta
     * @return 
     */
    public ArrayList<String> enviarSolicitudPregunta(int numeroPregunta){
        
        // Se declara un arreglo para almacenar la información recibida de la pregunta
        ArrayList<String> informacionPregunta = new ArrayList<>();
        try{
            Integer numero = numeroPregunta; // Se asigna el valor del párametro recibido a una variable de tipo Integer
            salida.writeUTF("Solicitud de pregunta"); // Se envía al servidor la instrucción para que sepa qué es lo que se
                                                      // le va a enviar
            salida.writeInt(numero); // Se envía el número de pregunta que se desea solicitar
            salida.flush(); // Se asegura que los datos fueron enviados
            
            // Se agregan los datos de la pregunta al arreglo que va a contener la información de la
            // pregunta, los cuales son recibidos desde el servidor, estos datos se reciben en orden
            // por lo tanto en el arreglo también quedan en orden.
            informacionPregunta.add(entrada.readUTF());
            informacionPregunta.add(entrada.readUTF());
            informacionPregunta.add(entrada.readUTF());
            informacionPregunta.add(entrada.readUTF());
            informacionPregunta.add(entrada.readUTF());
            
            // Se deshabilitan el comboBox de las preguntas y el boton que permitía seleccionar
            // una pregunta.
            cbPreguntas.setEnabled(false);
            bSeleccionarPregunta.setEnabled(false);
                      
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
         }
        
        return informacionPregunta; // Se retorna el arreglo con la información de la pregunta.
    }
    
    /**
     * Método que permite enviar la respuesta que un cliente ha dado a una pregunta al servidor,
     * para esto se envía primero la instrucción que es la que le indicará al servidor lo que 
     * va a recibir, luego se recibe el numero de la pregunta que fue respondida y la opción que
     * fue escogida por el usuario, por último sen envía el nombre del usuario que la respondió.
     * @param numeroPregunta
     * @param opcionEscogida
     * @param nombreUsuario 
     */
    public void enviarRespuesta(int numeroPregunta, int opcionEscogida, String nombreUsuario){
        
        try{
            
            salida.writeUTF("Envío de Respuesta"); // Se envía la instrucción que permite saber al servidor qué se esta enviando
            salida.writeInt(numeroPregunta); // Se envía el número de la pregunta que ha sido respondida
            salida.writeInt(opcionEscogida); // Se envía el número de la opción que el cliente escogió
            salida.writeUTF(nombreUsuario); // Se envía el nombre del usuario que respondio la pregunta
            cbPreguntas.setEnabled(true); // Se activa el comboBox de las preguntas para que el cliente las pueda visualizar
            bSeleccionarPregunta.setEnabled(true); // Se habilita el boton de seleccionar pregunta
            
        }catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Método que envía al servidor la pregunta que el cliente quiere cancelar, para esto
     * primero se envía la instrucción que le indica al cliente lo que se le esta enviando
     * y luego le envia el número de pregunta que se desea cancelar.
     * @param numeropregunta 
     */
    public void cancelarPregunta(int numeropregunta){
        
        try{
            salida.writeUTF("Cancelar pregunta"); // Se envía la instrucción
            salida.writeInt(numeropregunta); // Se envía el número de pregunta que se desea cancelar
            cbPreguntas.setEnabled(true); // Se activa el comboBox de las preguntas 
            bSeleccionarPregunta.setEnabled(true); // Se activa el boton que permite seleccionar una pregunta
            
        }catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Método que le envía un mensaje al servidor que indica que el cliente ha cerrado
     * sesión, para esto además del mensaje que indica que el clientesalió, se le 
     * envía también el nombre del usuario que salió.
     */
    public void enviarMensajeDeSalida(){
        try{
            salida.writeUTF("Mensaje de salida"); // Instrucción que indica que un cliente ha cerrado sesión
            salida.writeUTF(nombreCliente); // Se envía el nombre del cliente que cerró sesión
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Método que mantiene escuchando constantemente los datos que el servidor le envía
     * a los clientes por multicast, estos datos llegan precedidos de una instruccion
     * la cual indica lo que se va a recibir, dependiendo de lo que se recibe se hacen
     * los debidos procedimientos para mostrar esos datos en la interfaz del cliente
     */
    public void escucharMultiCast()
    {
        while(true) // Ciclo infinito porque se necesita que escuche constantemente
        {
            
            byte [] instruccion = new byte[256]; // Arreglo de bytes para la definición del datagrama
            // Se crea un objeto de la clase DatagramPacket para recibir el datagrama que el servidor
            // le envía al cliente
            DatagramPacket dataInstruccion = new DatagramPacket(instruccion, instruccion.length);
            
            try
            {
                socketMulti.receive(dataInstruccion); // Se recibe el datagrama enviado por el servidor
                
                // Se define e inicializa un arreglo de bytes que va a ser utilizado para copia en este 
                // arreglo todos los datos del arreglo definido inicialmente.
                byte [] instruccionCopia = new byte[dataInstruccion.getLength()];
                // Se copian todos los datos del arreglo recibido al arreglo definido recientemente
                System.arraycopy (dataInstruccion.getData (),0,instruccionCopia,0, dataInstruccion.getLength ());
                
                // Se crea un String que va a servir  para almacenar los datos que estaban en el arreglo
                // de bytes de una manera mas comoda y fácil de manejar.
                String mensaje = new String(instruccionCopia);
            
                // Se extrae el comando que se envía desde el servidor, el cual indica exactamente lo que 
                // el servidor le va a enviar al cliente
                String comando = mensaje.split(" ")[0];
                                
                // Si el comando recibido hace referencia al tiempo, entonces se extrae la segunda
                // parte del string, en la cual esta almacenado el tiempo restante y se muestra
                // este valor en el label designado para ello.
                if(comando.equals("Tiempo")){
                    
                    lTiempo.setText(mensaje.split(" ")[1]); // Se muetra en el label el tiempo
                }
                
                // Si el comando recibido hace referencia a la cantidad de preguntas del examen
                // entonces se procede a llenar el comboBox de las preguntas.
                else if(comando.equals("cantidadPreguntas")){
                    
                    int cantidadPreguntas = 0; // Se define e inicializa una variable para almacenar la cantidad de preguntas
                    // Se extrae la cantidad de preguntas accediendo a la posición del arreglo de String
                    // donde quedó almacenado este valor
                    cantidadPreguntas = Integer.parseInt(mensaje.split(" ")[1]);
                    
                    // Ciclo que se encarga de llenar el comboBox de preguntas.
                    for (int i = 0; i < cantidadPreguntas; i++) 
                    {
                       cbPreguntas.addItem("Pregunta " + (i+1)); // Se agrega un Item al comboBox de preguntas
                       cbPreguntasFinal.addItem("Pregunta " + (i+1)); // Se agrega un Item al comboBox de preguntas de la
                                                                      // pestaña de resultados.
                    }
                }
                
                // Si el comando recibido hace referencia a el numero de pregunta que se encuentra 
                // ocupada por un cliente, entonces se procede a eliminar esta pregunta del comboBox
                // de preguntas, así los otros clientes no podrán seleccionar la misma pregunta
                else if(comando.equals("numeroPreguntaOcupada")){
                    
                    // Se obtiene el número de la pregunta que se encuentra ocupado, para esto se accede
                    // a la posición del arreglo de Strings que contiene este número de pregunta
                    int numeroPreguntaOcupada = Integer.parseInt(mensaje.split(" ")[1]);
                    
                    // Se elimina la pregunta que se encuentra ocupada de el comboBox
                    cbPreguntas.removeItem("Pregunta "+numeroPreguntaOcupada);
                }
                
                // Si el comando recibido hace referencia a el número de pregunta cancelada, entonces
                // se agrega la pregunta de nuevo al comboBox de preguntas
                else if(comando.equals("numeroPreguntaCancelada")){
                    
                    // Se obtiene el número de la pregunta que se ha cancelado, para esto se accede
                    // a la posición del arreglo de Strings que contiene este número de pregunta
                    int numeroPreguntaCancelada = Integer.parseInt(mensaje.split(" ")[1]);
                    
                    // Se agrega un nuevo Item que hace referencia a la pregunta que fue cancelada
                    // por un cliente
                    cbPreguntas.addItem("Pregunta "+numeroPreguntaCancelada);
                }
                
                // Si el comando recibido hace referencia a que el examen se ha terminado, entonces
                // se informa a los usuarios que el examen ha finalizado y se muestra la calificación 
                // obtenida por el grupo en el label designado para esto, luego se envía un mensaje
                // de salida al servidor, para que este actualice los labels con los nombres de los
                // clientes a "desconectado", por último se procede a cerrar la conexión entre el 
                // cliente y el servidor
                else if(comando.equals("finExamen")){
                    
                    // Se obtiene la calificación obtenida por el grupo, accediendo a la posición del
                    // arreglo de strings en donde esta dicha calificación.
                    String calificacion = mensaje.split(" ")[1];
                    // Se imprime un mensaje en pantalla informando a los usuarios que el examen ha finalizado
                    JOptionPane.showMessageDialog(null, "Fin del examen", "Información", JOptionPane.INFORMATION_MESSAGE);
                    // Se muestra la calificación del grupo en el label designado para esto
                    lCalificacion.setText("Calificación total: " + calificacion);
                    
                    // Se deshabilita la pestaña donde se desarrollaba el examen y se habilita
                    // la pestaña para la visualización de resultados.
                    pestañas.setEnabledAt(0, false);
                    pestañas.setEnabledAt(1, true);
                    pestañas.setSelectedIndex(1); // Se posiciona al usuario en la pestaña de resultados
                    enviarMensajeDeSalida(); // Se envía un mensaje de salida al servidor para que este actualice los
                                             // labels de los usuarios a "desconectado"
                    
                    detener(); // Se cierran los flujos y el socket.
                    escuchandoMulti.interrupt(); // Se interrumpe el hilo que escuchaba el multicast
                    socketMulti.close(); // Se cierra el socket de multicast
                    conexionRealizada = false; // Se cambia el valor del booleano de conexion realizada a false
                    break; 
                    
                }
               
                // Si el comando recibido hace referencia a los resultados de cada pregunta, entonces
                // se almacena la información de la calificación de cada pregunta en un arreglo
                // para luego poder mostrarla en la interfaz
                else if (comando.equals("resultadoPregunta")){
                    
                    String[] datos = mensaje.split("/"); // Se separa el String recibido mediante un slash
                    
                    String calificacion = datos[0].split(" ")[1]; // Se obtiene la calificación de la pregunta
                    String nombreUsuario = datos[1]; // Se obtiene el nombre del usuario que respondió la pregunta
                    
                    // En caso de que el nombre de usuario sea nulo, entonces se le asigna un string
                    // como de pregunta no respondida
                    if(nombreUsuario.equals("null")){
                        nombreUsuario = "No respondida";
                    }
                    
                    // Se crea un String con la calificación y el nombre de usuario, los cuales son 
                    // separados por un slash
                    String informacionPregunta = calificacion + "/" + nombreUsuario;
                                        
                    // Se agrega el string creado recientemente en el arreglo designado para guardar los
                    // resultados de cada pregunta
                    resultados.add(informacionPregunta);
                }
                            
            } catch (IOException ex) {
                System.out.println(ex);
            }
                    
        }
    }
    
    /**
     * Método que retorna la información de los resultados de una pregunta
     * determinada por el número de pregunta que se recibe por páramentro,
     * el retorno se hace mediante un string.
     * @param numeroPregunta
     * @return 
     */
    public String resultadosPregunta(int numeroPregunta){
        
        String informacion = "";
        
        // Se obtiene del arreglo donde se almacenó la información de los resultados de todas las preguntas
        // los resultados del número de pregunta que se recibió por párametro
        informacion = resultados.get(numeroPregunta-1);
        
        return informacion; // Se retorna el String con los resultados
        
    }
    
    /**
     * Método que inicializa todos los objetos referentes a el multicast y une 
     * el cliente al grupo del multicast, además se inicializa el hilo que va
     * a estar escuchando constantemente lo que el servidor envía
     * @throws IOException 
     */
    public void empezarEscucharMulticast() throws IOException
    {
        // Se inicializa el socket del multicast pasandole como parametro el puerto
        // por el cual se van a comunicar
        socketMulti = new MulticastSocket(10001);
        
        // Se inicializa el grupo al que van a pertenecer los clientes, se le pasa
        // como parametro la dirección
        grupoClientes = InetAddress.getByName("234.0.0.1");
        socketMulti.joinGroup(grupoClientes); // Se une el cliente al grupo
        
        // Se inicializa el hilo definido anteriormente y se define el método run()
        // dentro del hilo, el cual lo que va a hacer es llamar al método que
        // se encarga de escuchar el multicast constantemente.
        escuchandoMulti = new Thread()
        {
            @Override
            public void run(){
                escucharMultiCast(); // Método que escucha el multicast constantemente
            }
        };
        escuchandoMulti.start(); // Se coloca a correr el hilo.
    }
    
    /**
     * Método que se encarga de abrir los flujos para que el cliente pueda
     * leer la información que el servidor le envía y de igual forma pueda
     * enviar información al servidor.
     */
    public void comenzar()
    {
        try
        {
            // Se inicializa el DataOutputStream y el DataInputStream
            salida = new DataOutputStream(socket.getOutputStream());
            entrada = new DataInputStream(socket.getInputStream());
            salida.flush();
        }catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }
    
    /**
     * Método que se encarga de cerrar los flujos de lectura y escritura,
     * además de cerrar el socket que mantiene la conexion entre el cliente
     * y el servidor. Todo esto se hace en caso de que estos objetos sean
     * distintos de null
     */
    public void detener()
    {
        try
        {
            if(salida != null){
                salida.close(); // Se cierra el flujo de salida
            }
            if(socket != null){
                socket.close(); // Se cierra el socket de la conexión
            }
            if(entrada != null){
                entrada.close(); // Se cierra el flujo de entrada
            }
        }catch(IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }
    
}
