/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: Interfaz.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : Interfaz 
* Responsabilidad : Esta clase se encarga de gestionar la interfaz de
* un cliente que va a desarrollar un examen colaborativo conectado a un servidor.
* 
*/

package proyecto;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


//Clase Interfaz que extiende de JFrame.
public class Interfaz extends javax.swing.JFrame {
    
    //Atributos de la clase Interfaz.
    private Manejadora manejadora;
    private ButtonGroup grupoBotones;
    
    //Constructor de la clase Interfaz().
    public Interfaz() {
        
        //Se llama el método que inicializa todas las componentes de la interfaz.
        initComponents();
        
        //Se inicializa el objeto de la clase Manejadora.
        manejadora = new Manejadora();
        
        //Se llama el método que hace que las componentes areaEnunciado, tfOpcion1, tfOpcion2, tfOpcion3, tfOpcion4 y pestañas no se puedan
        //editar.
        areaEnunciado.setEditable(false);
        tfOpcion1.setEditable(false);
        tfOpcion2.setEditable(false);
        tfOpcion3.setEditable(false);
        tfOpcion4.setEditable(false);
        pestañas.setEnabledAt(1, false);
        
        //Se llama el método que evita que la ventana de la interfaz se pueda redimensionar en tiempo de ejecución.
        setResizable(false);
        
        //Se inicializa el objeto grupoBotones de la clase ButtonGroup.
        grupoBotones = new ButtonGroup();
        
        //Se añaden los botones bOpcion1, bOpcion2, bOpcion3, bOpcion4 a grupoBotones.
        grupoBotones.add(bOpcion1);
        grupoBotones.add(bOpcion2);
        grupoBotones.add(bOpcion3);
        grupoBotones.add(bOpcion4);
        
        //Se llama el método activaContenedor que recibe como parámetro el panel pDesarrollo y la variable boolean false.
        activaContenedor(pDesarrollo, false);
        
        //Se añade la escucha a los botones bCancelar, bEnviarRespuesta, bOpcion1, bOpcion2, bOpcion3, bOpcion4, bSeleccionarPregunta,
        //bNombreUsuario, bSeleccionarPreguntaFinal y se añade la escucha a la ventana de la interfaz(addWindowListener).
        bCancelar.addActionListener(manejadora);
        bEnviarRespuesta.addActionListener(manejadora);
        bOpcion1.addActionListener(manejadora);
        bOpcion2.addActionListener(manejadora);
        bOpcion3.addActionListener(manejadora);
        bOpcion4.addActionListener(manejadora);
        bSeleccionarPregunta.addActionListener(manejadora);
        bNombreUsuario.addActionListener(manejadora);
        bSeleccionarPreguntaFinal.addActionListener(manejadora);
        addWindowListener(manejadora); 
        
    }
    
    //Se define el método activaContenedor() que recibe como parámetros una variable de tipo Object y una variable boolean
    public void activaContenedor(Object object, boolean activa){
        
        //Verifica si el objeto que recibe como parámetro es una instancia de Container(contenedor) en caso de ser así, hace ese objeto
        //igual a un objeto de tipo Container e introduce todas las componentes en un arreglo de tipo Component.
        if(object instanceof Container){
            
            Container contenedor = (Container) object;
            Component [] componentes = contenedor.getComponents();
            
            //Posteriormente se define un ciclo for para aplicar el método setEnabled (con la variable boolean que entra como parámetro)             
            //a cada uno de los elementos del arreglo de componentes, también se utiliza el ciclo para llamar recursivamente el método 
            //activaContenedor() con cada uno de los elementos del arreglo de componentes. 
            for (int i = 0; i < componentes.length; i++) {
                
                componentes[i].setEnabled(activa);
                activaContenedor(componentes[i], activa);
            }
        }
        //Si no se entra a la condición anterior, se evalúa si el objeto que recibe como parámetro es una instancia de Component, si es así,
        //se asigna el objeto a un objeto de tipo Component y se le aplica el método setEnabled() usando la variable boolean "activa". 
        else if (object instanceof Component){
            
            Component componente = (Component) object;
            componente.setEnabled(activa);
        }
    }
    
    //Se define el método limpiarInformación() que se encarga de realizar varios procesos de limpiado a componentes de la interfaz
    public void limpiarInformacion(){
        
        //Se borra cualquier texto que haya en las componentes areaEnunciado, tfOpcion1, tfOpcion2, tfOpcion3, tfOpcion4.
        areaEnunciado.setText("");
        tfOpcion1.setText("");
        tfOpcion2.setText("");
        tfOpcion3.setText("");
        tfOpcion4.setText("");
        
        //Se llama el método clearSelection() que se utiliza para "limpirar" la selección de cualquiera de los botones de grupoBotones, es 
        //decir ninguno de esos botones queda seleccionado después de llamar ejecutar el método.
        grupoBotones.clearSelection();
        
        //Se llama el método activaContendor(), pasándole como parámetros el panel pDesarrollo y la variable boolean false.
        activaContenedor(pDesarrollo, false);
        
        //Se activan los elementos cbPreguntas, bSeleccionarPregunta, lTime, lTiempoRestante.
        cbPreguntas.setEnabled(true);
        bSeleccionarPregunta.setEnabled(true);
        lTime.setEnabled(true);
        lTiempoRestante.setEnabled(true);
    }
    /**
    * Clase: Manejadora
    * Responsabilidad : Se encarga de gestionar los componentes de la interfaz, junto
    * con sus debidos Listeners. Implementa a ActionListener y WindowListener.
    */
    
    public class Manejadora implements ActionListener, WindowListener {
        
        //Atributos de la clase Manejadora.
        private Cliente cliente;
        private String nombreServidor;
        private int puertoServidor;
        private int numeroPreguntaSeleccionada;
        
        //Constructor de la clase Manejadora.
        public Manejadora() {
            
            nombreServidor = "127.0.0.1";//Dirección en donde se encuentra alojado el servidor.
            puertoServidor = 12345;//Puerto que utilizará el servidor.
            cliente = new Cliente(cbPreguntas, cbPreguntasFinal, bSeleccionarPregunta , lTiempoRestante, lCalificacionTotal, pestañas);//Objeto de la clase Cliente.
        }
        
        //Se sobrecarga el método actionPerformed() que recibe como parámetro un evento (ActionEvent).
        @Override
        public void actionPerformed(ActionEvent e) {
            
            //Evalúa si el evento que se realizó fue sobre el botón bNombreUsuario, en caso de ser así, ejecuta una serie de instrucciones.
            if (e.getSource() == bNombreUsuario) {
                
                //Define una variable String que va a ser igual al texto que se encuentra en el campo de texto tfNombreUsuario.
                String nombreUsuario = tfNombreUsuario.getText();
                
                //Evalúa si nombreUsuario es distinto de la cadena vacía, en caso de ser así, ejecuta el método setNombreCliente(), pasándole 
                //como parámetro nombreUsuario, y ejecuta el método enviarNombreUsuario(), que recibe como parámetros nombreUsuario,
                //nombreServidor y puertoServidor.
                if (!nombreUsuario.trim().equals("")) {

                    cliente.setNombreCliente(nombreUsuario);
                    cliente.enviarNombreUsuario(nombreUsuario, nombreServidor, puertoServidor);
                    
                    //Evalúa si la ejecución del método isConexionRealizada() es igual a true(es decir si se realizó la conexión), y de ser 
                    //así ejecuta los métodos limpiarInformacion(), activarContenedor() recibiendo por parámetros el panel pNombreUsuario y 
                    //el boolean false, se desactivan los botones bEnviarRespuesta y bCancelar.
                    if (cliente.isConexionRealizada()) {
                        limpiarInformacion();
                        activaContenedor(pNombreUsuario, false);
                        bEnviarRespuesta.setEnabled(false);
                        bCancelar.setEnabled(false);
                    }

                }
                //Devuelve una ventana de diálogo en caso de que nombreUsuario sea igual a la cadena vacía.
                else {
                    JOptionPane.showMessageDialog(null, "Ingrese un nombre válido");
                }

            }
            
            //Evalúa si el evento que se realizó fue sobre el botón bSeleccionarPregunta, en caso de ser así, ejecuta una serie de instrucciones.
            else if (e.getSource() == bSeleccionarPregunta) {
                
                //Evalúa si la posición seleccionada del comboBox cbPreguntas es distina de cero, si lo es, ejecuta una serie de instrucciones.
                if (cbPreguntas.getSelectedIndex() != 0) {
                    
                    //Toma el elemento seleccionado del comboBox cbPreguntas, lo convierte a String, divide este String y toma el valor que se 
                    //encuentra en la posición 1, convierte este String en un Integer y lo asigna a el atributo numeroPreguntaSeleccionada.
                    numeroPreguntaSeleccionada = Integer.parseInt(cbPreguntas.getSelectedItem().toString().split(" ")[1]);
                    //Asigna un texto a lPreguntaSeleccionada.
                    lPreguntaSeleccionada.setText("Pregunta Seleccionada: " + numeroPreguntaSeleccionada);
                    //Selecciona la posición cero del comboBox cbPreguntas.
                    cbPreguntas.setSelectedIndex(0);                    
                    
                    //Crea un ArrayList<> de tipo String y le asigna el valor de retorno del método enviarSolicitudPregunta() el cual recibe 
                    //como parámetro el atributo numeroPreguntaSeleccionada.
                    ArrayList<String> informacionPregunta = cliente.enviarSolicitudPregunta(numeroPreguntaSeleccionada);
                    
                    //Muestra lo que hay en la posición 0 del ArrayList<> en el area de texto areaEnunciado.
                    areaEnunciado.setText(informacionPregunta.get(0));
                    //Muestra lo que hay en la posición 1 del ArrayList<> en el area de texto areaEnunciado.
                    tfOpcion1.setText(informacionPregunta.get(1));
                    //Muestra lo que hay en la posición 2 del ArrayList<> en el area de texto areaEnunciado.
                    tfOpcion2.setText(informacionPregunta.get(2));
                    //Muestra lo que hay en la posición 3 del ArrayList<> en el area de texto areaEnunciado.
                    tfOpcion3.setText(informacionPregunta.get(3));
                    //Muestra lo que hay en la posición 4 del ArrayList<> en el area de texto areaEnunciado.
                    tfOpcion4.setText(informacionPregunta.get(4));
                    
                    //Llama el activaContenedor() pasándole por parámetros el panel pDesarrollo y el boolean true.
                    activaContenedor(pDesarrollo, true);
                    //Desactiva el comboBox cbPreguntas.
                    cbPreguntas.setEnabled(false);
                    //Desactiva el botón bSeleccionarPregunta.
                    bSeleccionarPregunta.setEnabled(false);
                    
                }
                //En caso de que la posición del comboBox cbPreguntas sea cero, muestra una ventana de advertencia.
                else {
                    JOptionPane.showMessageDialog(null, "Seleccione una pregunta", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
            
            //Evalúa si el evento que se realizó fue sobre el botón bCancelar, en caso de ser así, ejecuta una serie de instrucciones.           
            else if (e.getSource() == bCancelar) {

                //Ejecuta el método cancelarPregunta() que recibe como parámetro el atributo numeroPreguntaSeleccionada.
                cliente.cancelarPregunta(numeroPreguntaSeleccionada);
                //Muestra una ventana de diálogo.
                JOptionPane.showMessageDialog(null, "Pregunta cancelada", "Información", JOptionPane.INFORMATION_MESSAGE);
                //Llama el método limpiarInformacion().
                limpiarInformacion();
            }
            
            //Evalúa si el evento que se realizó fue sobre el botón bEnviarRespuesta, en caso de ser así, ejecuta una serie de instrucciones.
            else if (e.getSource() == bEnviarRespuesta) {
                
                //Se crea la variable opcionEscogida (de tipo int) con el valor de cero.
                int opcionEscogida = 0;
                
                //Evalúa si el botón bOpcion1 está seleccionado, si es así, le asigna el valor de 1 a la variable opcionEscogida.
                if (bOpcion1.isSelected()) {
                    opcionEscogida = 1;
                }
                //Evalúa si el botón bOpcion1 está seleccionado, si es así, le asigna el valor de 2 a la variable opcionEscogida.
                else if (bOpcion2.isSelected()) {
                    opcionEscogida = 2;
                }
                //Evalúa si el botón bOpcion1 está seleccionado, si es así, le asigna el valor de 3 a la variable opcionEscogida.
                else if (bOpcion3.isSelected()) {
                    opcionEscogida = 3;
                }
                //Evalúa si el botón bOpcion1 está seleccionado, si es así, le asigna el valor de 4 a la variable opcionEscogida.
                else if (bOpcion4.isSelected()) {
                    opcionEscogida = 4;
                }
                
                
                //Evalúa si el valor de la variable opcionEscogida es distinto de cero, si es así, ejecuta una serie de instrucciones.
                if (opcionEscogida != 0) {
                    //Ejecuta el método enviarRespuesta(), el cual recibe como parámetros el atributo numeroPreguntaSeleccionada, la variable
                    //opcionEscogida y el valor de retorno de la ejecución del método getNombreCliente().
                    cliente.enviarRespuesta(numeroPreguntaSeleccionada, opcionEscogida, cliente.getNombreCliente());
                    //Coloca un texto en el label lPreguntaSeleccionada.
                    lPreguntaSeleccionada.setText("Pregunta Seleccionada: ");
                    //Muestra una ventana de diálogo.
                    JOptionPane.showMessageDialog(null, "Respuesta enviada correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                    //Llama el método limpiarInformacion().
                    limpiarInformacion();
                }
                //En caso de que el valor de la variable opcionEscogida sea cero muestra una ventana de diálogo.
                else {
                    JOptionPane.showMessageDialog(null, "No ha respondido la pregunta!", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
            
            //Evalúa si el evento que se realizó fue sobre el botón bSeleccionPreguntaFinal, en caso de ser así, ejecuta una serie de instrucciones.
            else if (e.getSource() == bSeleccionarPreguntaFinal) {
                
                //Evalúa si la posición seleccionada del comboBox  cbPreguntasFinal es distinta de cero
                if (cbPreguntasFinal.getSelectedIndex() != 0) {
                    
                    //Toma el elemento seleccionado del comboBox cbPreguntasFinal, lo convierte a String, divide este String y toma el valor 
                    //que se encuentra en la posición 1, convierte este String en un Integer y lo asigna a el atributo 
                    //numeroPregunta.
                    int numeroPregunta = Integer.parseInt(cbPreguntasFinal.getSelectedItem().toString().split(" ")[1]);
                    
                    lPreguntaSeleccionadaFinal.setText("Pregunta seleccionada: " + numeroPregunta);

                    //Crea un arreglo estático de tipo String y le asigna el valor de retorno del método resultadosPregunta() (que
                    //recibe como parámetro la variable numeroPregunta) y la división de este valor de retorno.
                    String[] informacion = cliente.resultadosPregunta(numeroPregunta).split("/");
                    
                    //Se crea la variable calificacion con el valor de "Correcta".
                    String calificacion = "Correcta";

                    //Se evalúa si el String que se encuentra en la posición 0 del arreglo es igual a "0", en caso de ser así, cambia el 
                    //valor de la variable calificacion a "Incorrecta".
                    if (informacion[0].equals("0")) {
                        calificacion = "Incorrecta";
                    }
                    
                    //Coloca la calificación en el texto en el label lCalificacionPregunta.
                    lCalificacionPregunta.setText("Calificación de la pregunta: " + calificacion);
                    //Coloca el nombre de quien respondió la pregunta en el texto en el label lRespondidaPor.
                    lRespondidaPor.setText("Respondida por: " + informacion[1]);
                }
                //En caso de que la posición seleccionada del comboBox cbPreguntasFinal sea igual a cero, muestra una ventana de advertencia. 
                else {
                    JOptionPane.showMessageDialog(null, "Seleccione una pregunta!", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }

        }

        //Se sobrecarga el método windowClosing() que recibe como parámetro un evento sobre la ventana.
        @Override
        public void windowClosing(WindowEvent e) {

            //Evalúa si el cliente está conectado, en caso de ser así, ejecuta una serie de instrucciones.
            if (cliente.isConexionRealizada()) {
                
                //Se crea la variable opcion (de tipo int) cuyo valor será igual al valor de la respuesta de la ventana de confirmación.
                int opcion = JOptionPane.showConfirmDialog(null, "¿Desea cerrar la sesión?");
                
                //Evalúa si el valor de la variable opcion es igual al valor de "YES_OPTION", si es así, ejecuta el método enviarMensajeDeSalida()
                //imprime por consola un mensaje, ejecuta el método detener() y termina la ejecución del cliente.
                if (opcion == JOptionPane.YES_OPTION) {
                    cliente.enviarMensajeDeSalida();
                    cliente.detener();
                    System.exit(0);
                }
            }
            //Si el cliente no estaba conectado imprime un mensaje por consola y termina la ejecución del cliente.
            else {
                System.exit(0);
            }

        }

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }

    }           
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pestañas = new javax.swing.JTabbedPane();
        pDesarrolloExamen = new javax.swing.JPanel();
        pTituloExamen = new javax.swing.JPanel();
        logoUV5 = new javax.swing.JLabel();
        lExamen5 = new javax.swing.JLabel();
        lDesarrolloExamen = new javax.swing.JLabel();
        pNombreUsuario = new javax.swing.JPanel();
        lNombre1 = new javax.swing.JLabel();
        tfNombreUsuario = new javax.swing.JTextField();
        bNombreUsuario = new javax.swing.JButton();
        pDesarrollo = new javax.swing.JPanel();
        lEnunciado = new javax.swing.JLabel();
        barras1 = new javax.swing.JScrollPane();
        areaEnunciado = new javax.swing.JTextArea();
        tfOpcion1 = new javax.swing.JTextField();
        tfOpcion2 = new javax.swing.JTextField();
        tfOpcion3 = new javax.swing.JTextField();
        tfOpcion4 = new javax.swing.JTextField();
        cbPreguntas = new javax.swing.JComboBox();
        bSeleccionarPregunta = new javax.swing.JButton();
        bEnviarRespuesta = new javax.swing.JButton();
        bCancelar = new javax.swing.JButton();
        lPreguntaSeleccionada = new javax.swing.JLabel();
        bOpcion1 = new javax.swing.JRadioButton();
        bOpcion2 = new javax.swing.JRadioButton();
        bOpcion3 = new javax.swing.JRadioButton();
        bOpcion4 = new javax.swing.JRadioButton();
        lTime = new javax.swing.JLabel();
        lTiempoRestante = new javax.swing.JLabel();
        pResultados = new javax.swing.JPanel();
        pTituloExamen1 = new javax.swing.JPanel();
        logoUV6 = new javax.swing.JLabel();
        lExamen6 = new javax.swing.JLabel();
        lDesarrolloExamen1 = new javax.swing.JLabel();
        pResultadosExamen = new javax.swing.JPanel();
        cbPreguntasFinal = new javax.swing.JComboBox();
        bSeleccionarPreguntaFinal = new javax.swing.JButton();
        lPreguntaSeleccionadaFinal = new javax.swing.JLabel();
        lCalificacionPregunta = new javax.swing.JLabel();
        lRespondidaPor = new javax.swing.JLabel();
        lCalificacionTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pDesarrolloExamen.setBackground(new java.awt.Color(178, 34, 34));

        pTituloExamen.setBackground(new java.awt.Color(178, 34, 34));
        pTituloExamen.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        logoUV5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UniversidaddelValleCali.gif"))); // NOI18N

        lExamen5.setFont(new java.awt.Font("SansSerif", 3, 26)); // NOI18N
        lExamen5.setForeground(new java.awt.Color(255, 255, 255));
        lExamen5.setText("Examen Colaborativo Univalle");

        lDesarrolloExamen.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        lDesarrolloExamen.setForeground(new java.awt.Color(255, 255, 255));
        lDesarrolloExamen.setText("Desarrollo del Examen");

        javax.swing.GroupLayout pTituloExamenLayout = new javax.swing.GroupLayout(pTituloExamen);
        pTituloExamen.setLayout(pTituloExamenLayout);
        pTituloExamenLayout.setHorizontalGroup(
            pTituloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTituloExamenLayout.createSequentialGroup()
                .addGroup(pTituloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lDesarrolloExamen)
                    .addComponent(lExamen5, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(logoUV5, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        pTituloExamenLayout.setVerticalGroup(
            pTituloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoUV5, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .addGroup(pTituloExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lExamen5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lDesarrolloExamen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pNombreUsuario.setBackground(new java.awt.Color(178, 34, 34));
        pNombreUsuario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nombre de Usuario", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lNombre1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lNombre1.setForeground(new java.awt.Color(255, 255, 255));
        lNombre1.setText(" Nombre:");
        lNombre1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tfNombreUsuario.setForeground(new java.awt.Color(61, 61, 61));

        bNombreUsuario.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bNombreUsuario.setText("Ingresar");

        javax.swing.GroupLayout pNombreUsuarioLayout = new javax.swing.GroupLayout(pNombreUsuario);
        pNombreUsuario.setLayout(pNombreUsuarioLayout);
        pNombreUsuarioLayout.setHorizontalGroup(
            pNombreUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pNombreUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(tfNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(bNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pNombreUsuarioLayout.setVerticalGroup(
            pNombreUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pNombreUsuarioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pNombreUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(tfNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pDesarrollo.setBackground(new java.awt.Color(178, 34, 34));
        pDesarrollo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Desarrollo", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lEnunciado.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lEnunciado.setForeground(new java.awt.Color(255, 255, 255));
        lEnunciado.setText(" Enunciado: ");
        lEnunciado.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        areaEnunciado.setColumns(20);
        areaEnunciado.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        areaEnunciado.setForeground(new java.awt.Color(61, 61, 61));
        areaEnunciado.setRows(5);
        barras1.setViewportView(areaEnunciado);

        tfOpcion1.setForeground(new java.awt.Color(61, 61, 61));

        tfOpcion2.setForeground(new java.awt.Color(61, 61, 61));

        tfOpcion3.setForeground(new java.awt.Color(61, 61, 61));

        tfOpcion4.setForeground(new java.awt.Color(61, 61, 61));

        cbPreguntas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una pregunta" }));

        bSeleccionarPregunta.setText("Seleccionar pregunta");

        bEnviarRespuesta.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        bEnviarRespuesta.setText("Enviar");

        bCancelar.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bCancelar.setText("Cancelar");

        lPreguntaSeleccionada.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lPreguntaSeleccionada.setForeground(new java.awt.Color(255, 255, 255));
        lPreguntaSeleccionada.setText("Pregunta Seleccionada: ");
        lPreguntaSeleccionada.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        bOpcion1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bOpcion1.setForeground(java.awt.Color.white);
        bOpcion1.setText("Opción 1:");

        bOpcion2.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bOpcion2.setForeground(java.awt.Color.white);
        bOpcion2.setText("Opción 2:");

        bOpcion3.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bOpcion3.setForeground(java.awt.Color.white);
        bOpcion3.setText("Opción 3:");

        bOpcion4.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bOpcion4.setForeground(java.awt.Color.white);
        bOpcion4.setText("Opción 4:");

        lTime.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        lTime.setForeground(new java.awt.Color(255, 255, 255));
        lTime.setText("Tiempo Restante:");
        lTime.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lTiempoRestante.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        lTiempoRestante.setForeground(new java.awt.Color(255, 255, 255));
        lTiempoRestante.setText("00:00");
        lTiempoRestante.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout pDesarrolloLayout = new javax.swing.GroupLayout(pDesarrollo);
        pDesarrollo.setLayout(pDesarrolloLayout);
        pDesarrolloLayout.setHorizontalGroup(
            pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pDesarrolloLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pDesarrolloLayout.createSequentialGroup()
                        .addComponent(cbPreguntas, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(bSeleccionarPregunta)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pDesarrolloLayout.createSequentialGroup()
                        .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pDesarrolloLayout.createSequentialGroup()
                                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lEnunciado)
                                    .addComponent(bOpcion2)
                                    .addComponent(bOpcion3)
                                    .addComponent(bOpcion4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pDesarrolloLayout.createSequentialGroup()
                                        .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(tfOpcion4, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tfOpcion3, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tfOpcion2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(pDesarrolloLayout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(lTime, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pDesarrolloLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lTiempoRestante, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(61, 61, 61))))
                                    .addComponent(barras1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pDesarrolloLayout.createSequentialGroup()
                                .addComponent(bOpcion1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfOpcion1, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pDesarrolloLayout.createSequentialGroup()
                                .addGap(175, 175, 175)
                                .addComponent(bCancelar)
                                .addGap(72, 72, 72)
                                .addComponent(bEnviarRespuesta, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lPreguntaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        pDesarrolloLayout.setVerticalGroup(
            pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pDesarrolloLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPreguntas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bSeleccionarPregunta))
                .addGap(18, 18, 18)
                .addComponent(lPreguntaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lEnunciado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(barras1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfOpcion1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bOpcion1))
                .addGap(18, 18, 18)
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfOpcion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bOpcion2)
                    .addComponent(lTime, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfOpcion3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bOpcion3)
                    .addComponent(lTiempoRestante, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfOpcion4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bOpcion4))
                .addGap(18, 18, 18)
                .addGroup(pDesarrolloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bCancelar)
                    .addComponent(bEnviarRespuesta))
                .addContainerGap())
        );

        javax.swing.GroupLayout pDesarrolloExamenLayout = new javax.swing.GroupLayout(pDesarrolloExamen);
        pDesarrolloExamen.setLayout(pDesarrolloExamenLayout);
        pDesarrolloExamenLayout.setHorizontalGroup(
            pDesarrolloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDesarrolloExamenLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(pDesarrolloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pNombreUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pTituloExamen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pDesarrollo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        pDesarrolloExamenLayout.setVerticalGroup(
            pDesarrolloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDesarrolloExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTituloExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(pNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pDesarrollo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pestañas.addTab("Desarrollo del Examen", pDesarrolloExamen);

        pResultados.setBackground(new java.awt.Color(178, 34, 34));

        pTituloExamen1.setBackground(new java.awt.Color(178, 34, 34));
        pTituloExamen1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        logoUV6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UniversidaddelValleCali.gif"))); // NOI18N

        lExamen6.setFont(new java.awt.Font("SansSerif", 3, 26)); // NOI18N
        lExamen6.setForeground(new java.awt.Color(255, 255, 255));
        lExamen6.setText("Examen Colaborativo Univalle");

        lDesarrolloExamen1.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        lDesarrolloExamen1.setForeground(new java.awt.Color(255, 255, 255));
        lDesarrolloExamen1.setText("Resultados");

        javax.swing.GroupLayout pTituloExamen1Layout = new javax.swing.GroupLayout(pTituloExamen1);
        pTituloExamen1.setLayout(pTituloExamen1Layout);
        pTituloExamen1Layout.setHorizontalGroup(
            pTituloExamen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTituloExamen1Layout.createSequentialGroup()
                .addGroup(pTituloExamen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lDesarrolloExamen1)
                    .addComponent(lExamen6, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(logoUV6, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        pTituloExamen1Layout.setVerticalGroup(
            pTituloExamen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoUV6, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .addGroup(pTituloExamen1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lExamen6, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lDesarrolloExamen1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pResultadosExamen.setBackground(new java.awt.Color(178, 34, 34));
        pResultadosExamen.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Desarrollo", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        cbPreguntasFinal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una pregunta" }));

        bSeleccionarPreguntaFinal.setText("Seleccionar pregunta");

        lPreguntaSeleccionadaFinal.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lPreguntaSeleccionadaFinal.setForeground(new java.awt.Color(255, 255, 255));
        lPreguntaSeleccionadaFinal.setText("Pregunta Seleccionada: ");
        lPreguntaSeleccionadaFinal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lCalificacionPregunta.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lCalificacionPregunta.setForeground(new java.awt.Color(255, 255, 255));
        lCalificacionPregunta.setText("Calificación de la pregunta: ");
        lCalificacionPregunta.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lRespondidaPor.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lRespondidaPor.setForeground(new java.awt.Color(255, 255, 255));
        lRespondidaPor.setText("Respondida por: ");
        lRespondidaPor.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lCalificacionTotal.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lCalificacionTotal.setForeground(new java.awt.Color(255, 255, 255));
        lCalificacionTotal.setText("Calificación total: ");
        lCalificacionTotal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout pResultadosExamenLayout = new javax.swing.GroupLayout(pResultadosExamen);
        pResultadosExamen.setLayout(pResultadosExamenLayout);
        pResultadosExamenLayout.setHorizontalGroup(
            pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pResultadosExamenLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pResultadosExamenLayout.createSequentialGroup()
                        .addComponent(cbPreguntasFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(bSeleccionarPreguntaFinal)
                        .addGap(0, 187, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pResultadosExamenLayout.createSequentialGroup()
                        .addGroup(pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lCalificacionPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lPreguntaSeleccionadaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lRespondidaPor, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lCalificacionTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(143, 143, 143))))
        );
        pResultadosExamenLayout.setVerticalGroup(
            pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pResultadosExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPreguntasFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bSeleccionarPreguntaFinal))
                .addGap(22, 22, 22)
                .addComponent(lPreguntaSeleccionadaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lCalificacionPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lRespondidaPor, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lCalificacionTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(303, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pResultadosLayout = new javax.swing.GroupLayout(pResultados);
        pResultados.setLayout(pResultadosLayout);
        pResultadosLayout.setHorizontalGroup(
            pResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadosLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(pResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pTituloExamen1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pResultadosExamen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        pResultadosLayout.setVerticalGroup(
            pResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pTituloExamen1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pResultadosExamen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pestañas.addTab("Resultados", pResultados);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 695, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pestañas))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 727, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(pestañas, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
        Interfaz interfaz = new Interfaz();//Se crea un objeto de tipo Interfaz.
        interfaz.setVisible(true);//Se hace visible la interfaz
        interfaz.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//Desactive el cierre predeterminado con el botón de cierre de la ventana.
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaEnunciado;
    private javax.swing.JButton bCancelar;
    private javax.swing.JButton bEnviarRespuesta;
    private javax.swing.JButton bNombreUsuario;
    private javax.swing.JRadioButton bOpcion1;
    private javax.swing.JRadioButton bOpcion2;
    private javax.swing.JRadioButton bOpcion3;
    private javax.swing.JRadioButton bOpcion4;
    private javax.swing.JButton bSeleccionarPregunta;
    private javax.swing.JButton bSeleccionarPreguntaFinal;
    private javax.swing.JScrollPane barras1;
    private javax.swing.JComboBox cbPreguntas;
    private javax.swing.JComboBox cbPreguntasFinal;
    private javax.swing.JLabel lCalificacionPregunta;
    private javax.swing.JLabel lCalificacionTotal;
    private javax.swing.JLabel lDesarrolloExamen;
    private javax.swing.JLabel lDesarrolloExamen1;
    private javax.swing.JLabel lEnunciado;
    private javax.swing.JLabel lExamen5;
    private javax.swing.JLabel lExamen6;
    private javax.swing.JLabel lNombre1;
    private javax.swing.JLabel lPreguntaSeleccionada;
    private javax.swing.JLabel lPreguntaSeleccionadaFinal;
    private javax.swing.JLabel lRespondidaPor;
    private javax.swing.JLabel lTiempoRestante;
    private javax.swing.JLabel lTime;
    private javax.swing.JLabel logoUV5;
    private javax.swing.JLabel logoUV6;
    private javax.swing.JPanel pDesarrollo;
    private javax.swing.JPanel pDesarrolloExamen;
    private javax.swing.JPanel pNombreUsuario;
    private javax.swing.JPanel pResultados;
    private javax.swing.JPanel pResultadosExamen;
    private javax.swing.JPanel pTituloExamen;
    private javax.swing.JPanel pTituloExamen1;
    private javax.swing.JTabbedPane pestañas;
    private javax.swing.JTextField tfNombreUsuario;
    private javax.swing.JTextField tfOpcion1;
    private javax.swing.JTextField tfOpcion2;
    private javax.swing.JTextField tfOpcion3;
    private javax.swing.JTextField tfOpcion4;
    // End of variables declaration//GEN-END:variables
      
    
}
