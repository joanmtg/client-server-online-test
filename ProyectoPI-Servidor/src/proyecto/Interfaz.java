/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;


public class Interfaz extends javax.swing.JFrame{

    // Declaración de variables globales
    
    private Manejadora manejadora; // Se instancia un objeto de la clase manejadora
    private ArrayList<JLabel> labelsNombres; // Arreglo en el cual se almacenarán los labels con los nombres
                                             // de los usuarios
    
    public Interfaz() {
        
        initComponents(); // Método que inicializa los componentes de la interfaz
        setResizable(false); // Método para no permitir al usuario cambiar el tamaño de la ventana
                       
        // Se cambia el estado de los splits a: no editables, esto se hace con el objetivo
        // de no permitir al usuario cambiar los valores de estos manualmente.
        ((DefaultEditor) spCantidadPreguntas.getEditor()).getTextField().setEditable(false);
        ((DefaultEditor) spDuracionExamen.getEditor()).getTextField().setEditable(false);
        
        labelsNombres = new ArrayList<>(); // Se inicializa el Arreglo de labels
        // Se agregan cada uno de los labels que van a contener los nombres de los usuarios
        // en el arreglo designado para ellos.
        labelsNombres.add(lNombreUsuario1); 
        labelsNombres.add(lNombreUsuario2);
        labelsNombres.add(lNombreUsuario3);  
        
        manejadora = new Manejadora(); // Se inicializa el objeto de la clase manejadora
        
        añadirListeners(); // Se agregan las escuchas a todos los botones de la interfaz, para que estos
                           // estén hábiles para realizar sus respectivas operaciones.
        
        // Se deshabilitan los contenedores del ingreso de preguntas, seleccion de preguntas
        // y visualizar configuración, de esta forma se restringe al usuario para que realice
        // las operaciones principales, sin las cuales no se podrían hacer las otras.
        activaContenedor(pIngresoPreguntas, false);
        activaContenedor(pSeleccionPreguntas, false);
        activaContenedor(pVisualizarConfiguracion, false);
        
        // Se deshabilitan los botones que realizan operaciones que no se pueden realizar
        // antes de que una de las operaciones principales haya sido realizada primero.
        bFinalizarIngreso.setEnabled(false);
        bGuardarConfiguracion.setEnabled(false);
        bCambiarConfiguracion.setEnabled(false);
        bDesarrollarExamen.setEnabled(false);
        bIniciarExamen.setEnabled(false);
        
        // Se deshabilitan las pestañas correspondientes al desarrolo del examen y a la visualización de
        // resultados, debido a que aún no se ha configurado ningún examen para ser desarrollado, por
        // consiguiente no se pueden visualizar los resultados de ningún examen.
        pestañas.setEnabledAt(3, false);
        pestañas.setEnabledAt(4, false);
    }
    
    
    /**
     * Nombre del método: añadirListeners
     * Descripción: Se toma cada botón perteneciente a la interfaz y se le agrega a cada uno la escucha para
     *              que pueda realizar la operación que le fue designada, como párametro de ActionListener
     *              se le pasa el objeto de la clase manejadora de eventos declarado anteriormente.
     */
    public void añadirListeners(){
        
        bSeleccionarCategoria.addActionListener(manejadora);
        bAsignarNombreC.addActionListener(manejadora);
        bAñadirPregunta.addActionListener(manejadora);
        bAñadirPreguntaExamen.addActionListener(manejadora);
        bFinalizarIngreso.addActionListener(manejadora);
        bIniciarExamen.addActionListener(manejadora);
        bMostrarEnunciado.addActionListener(manejadora);
        bMostrarEnunciadoRespondida.addActionListener(manejadora);
        bTerminarConfigGeneral.addActionListener(manejadora);
        bGuardarConfiguracion.addActionListener(manejadora);
        bSeleccionarConfiguracion.addActionListener(manejadora);
        bMostrarPreguntaSeleccionada.addActionListener(manejadora);
        bCambiarConfiguracion.addActionListener(manejadora);
        bDesarrollarExamen.addActionListener(manejadora);
        bTerminarVerResultados.addActionListener(manejadora);
        this.addWindowListener(manejadora);
        
    }    
    
    /**
     * Nombre del método: activaContenedor
     * Descripción: Se recibe como parametro un objeto el cual es el que se desea deshabilitar o activar y
     *              un valor booleano, el cual designara si el objeto se activará o se desactivará. Se evalua
     *              si el objeto recibido es un contenedor o un componente, para así hacer las respectivas 
     *              acciones.
     * 
     * @param object
     * @param activa 
     */
    public void activaContenedor(Object object, boolean activa){
        
        // En caso de que el objeto recibido sea un contenedor, entonces se procede a extraer
        // el arreglo de componentes que hacen parte de este contenedor para así deshabilitar
        // o habilitar cada uno de ellos, esto se hace de forma recursiva, debido a que se 
        // puede tener un contenedor dentro de otro.
        if(object instanceof Container){
            
            Container contenedor = (Container) object; // Se hace el debido Casting para el contenedor
            Component [] componentes = contenedor.getComponents(); // Se obtiene el arreglo de los componentes
                                                                   // dentro del contenedor
        
            // Ciclo que recorre el arreglo de componentes y activa o desactiva cada uno de estos 
            // componentes, de igual forma se llama el método recursivamente para estar seguros 
            // de deshabilitar cada uno de los contenedores.
            for (int i = 0; i < componentes.length; i++) {
                
                componentes[i].setEnabled(activa);
                activaContenedor(componentes[i], activa);
            }
        }
        
        // Si el objeto no es un contenedor, quiere decir que es un componente común (JButton, JTextFiled, etc)
        // por lo tanto, lo único que se hace es habilitar o deshabilitar este componente, dependiendo de la 
        // instrucción dada por el booleano recibido.
        else if (object instanceof Component){
            
            Component componente = (Component) object; // Se hace el debido Casting para un componente
            componente.setEnabled(activa);
        }
    }
    
    
    /**
     * Clase: Manejadora
     */
    public class Manejadora implements ActionListener, WindowListener {
        
        // Declaración de las variables a utilizar
        private Servidor server; // Objeto de la clase servidor
        private int contadorPreguntasTotal; // Contador para la cantidad de preguntas
        private int contadorPreguntasCategoria; // Contador para el número de preguntas de una categoria
        private int contadorExamenes; // Contador para los examenes
        private int numeroExamenSeleccionado; // Se almacenará el número de examen que se ha seleccionado
        private int numeroPreguntasExamenSeleccionado; // Se almacenará la cantidad de preguntas del examen seleccionado
        private int duracionExamenSeleccionado; // Se almacenará la duración del examen que ha sido seleccionado
        private int preguntaActual; // Identificador para el número de pregunta que se esta agregando a una categoria
        private int cantidadPreguntasExamen; // Se almacenarpa la cantidad de preguntas del examen a configurar
        private String nombreCategoria; // Se almacenará el nombre de la categoria ingresado por el usuario.
        private ArrayList<Pregunta> preguntasTemp; // Arreglo para las preguntas que se van agregando a cada categoria.
        private Pregunta preguntaTemp; // Objeto de la clase Pregunta para almacenar una pregunta recibida.

        // Constructor
        public Manejadora() {
            
            server = new Servidor(); // Se inicializa el objeto de la clase servidor
            server.abrirConexion(12345); // Se abre la conexión para que el servidor este listo para recibir un cliente.
            server.getAdminBd().reiniciarDatosBD(); // Se reinician los valores correspondientes a los registros
                                                    // tomados sobre la realización de un examen, tales como:
                                                    // las respuestas dadas a las preguntas, la calificación, etc.
            
            // Se extrae el numero de preguntas registradas actualmente en la base de datos y ese valor
            // se incrementa en 1, la misma acción se realiza con el numero de examenes registrados.
            contadorPreguntasTotal = server.getAdminBd().numeroRegistros("preguntas") + 1;
            contadorExamenes = server.getAdminBd().numeroRegistros("examenes") + 1;
            contadorPreguntasCategoria = 1;
            
            // Se actualiza el comboBox que permite ver los examenes registrados anteriormente
            // dependiendo del número de examenes que se encuentran registrados en la base de
            // datos.
            actualizarConfiguraciones(); 

            preguntasTemp = new ArrayList<>(); // Se inicializa el arreglo de preguntas
            
            // Se pasa por parametro los diferentes componentes de la interfaz que el servidor va a poder 
            // modificar desde la clase Servidor misma.
            server.agregarComponentesInterfaz(labelsNombres, bIniciarExamen, lEstadoExamen, lTiempo, pestañas, lCalificacionGrupo);

        }
        
        
        /**
         * Nombre del método: actualizarConfiguraciones
         * Descripción: Si hay examenes registrados en la base de datos, entonces se muestra
         *              en el comboBox de las configuraciones de examenes a seleccionar una 
         *              lista con la cantidad de examenes registrados
         */
        public void actualizarConfiguraciones() {

            // Ciclo que se encarga de agregar Items al comboBox hasta que el contador de los
            // examenes sea el valor del contador menos 1, debido a que inciando habíamos
            // sumado uno a este valor.
            for (int i = 0; i < contadorExamenes - 1; i++) {

                // Se agrega el item al contador, con su respectivo identificador.
                cbConfiguracionesGuardadas.addItem("Examen " + (i + 1)); 
            }

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
            // En caso de que el boton para asignar el nombre de una categoria sea presionado por el usuario.
            if (e.getSource() == bAsignarNombreC) {

                // Se asigna el valor obtenido del campo de texto a la variable designada para almacenar
                // el nombre de la categoria
                nombreCategoria = tfNombreCategoria.getText();

                // Si el usuario no ingresa ningun nombre y aún asi presiona el botón, entonces se envía
                // un mensaje por pantalla informando que el campo del nombre de categoria se encuentra
                // vacío
                if (nombreCategoria.trim().equals("")) {

                    JOptionPane.showMessageDialog(null, "Campo vacío!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                // En caso de que el campo de texto no este vacío, entonces se procede a validar que el nombre
                // de categoria ingresado no sea una categoria la cual ya tenga preguntas ingresadas en la base
                // de datos, es decir, no haya una categoria ya registrada con el mismo nombre.
                else if (server.getAdminBd().validarCategoria(nombreCategoria)) {
                    
                    // Se imprime un mensaje informando al usuario que el nombre de la categoria se asignó
                    // correctamente
                    JOptionPane.showMessageDialog(null, "Nombre de categoría asignado correctamente");
                    activaContenedor(pNombreCategoria, false); // Se desactiva el contenedor donde se ingresaba el 
                                                               // nombre de categoria y todos sus componentes.
                    
                    activaContenedor(pIngresoPreguntas, true); // Se activa el contenedor para el ingreso de
                                                               // preguntas de la categoria.
                    
                    // Se deshabilitan las pestañas de configuración de un examen y de inicio de un examen.
                    pestañas.setEnabledAt(1, false);
                    pestañas.setEnabledAt(2, false);

                } else {
                    // En caso de que haya una categoria registrada con el mismo nombre, entonces se informa al usuario
                    // mediante un mensaje en pantalla
                    JOptionPane.showMessageDialog(null, "La categoría ya está registrada", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            // En caso de que el boton de añadir una pregunta de una categoria sea presionado por el usuario
            else if (e.getSource() == bAñadirPregunta) {

                // Se toman los respectivos valores del area y los campos de texto, posteriormente
                // se asignan los valores que fueron tomados a sus respectivas variables.
                String enunciado = areaEnunciado.getText();
                String opcion1 = tfOpcion1.getText();
                String opcion2 = tfOpcion2.getText();
                String opcion3 = tfOpcion3.getText();
                String opcion4 = tfOpcion4.getText();

                // En caso de que alguno de los campos de la pregunta se encuentre vacío, ya sea enunciado,
                // opcion1, opcion2, opcion3 o opción4; entonces se informa al usuario mediante un mensaje
                // que debe rellenar todos los campos para poder ingresar una pregunta.
                if (server.camposPreguntaVacios(enunciado, opcion1, opcion2, opcion3, opcion4)) {

                    JOptionPane.showMessageDialog(null, "Rellene todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    // Si el usuario rellenó todos los campos correctamente, se procede a verificar
                    // que haya al menos una pregunta registrada a la categoria que se esta regis-
                    // trando, si la hay, se guarda inmediatamente la categoria en la base de datos
                    // esto como soporte de la información en caso de algun problema inesperado.
                    if (contadorPreguntasCategoria == 1) {
                        server.getAdminBd().guardarCategoria(nombreCategoria);
                    }

                    // Se extrae el número de la respuesta correcta del comboBox y se hace la asignación
                    // correspondiente a la variable designada para ello.
                    int correcta = Integer.parseInt(cbOpcionCorrecta.getSelectedItem().toString());

                    // Se declara un arreglo de Strings en el cual se almacenaran cada una de las opciones
                    // que el usuario tiene para dar respuesta a la pregunta.
                    String[] opciones = new String[4];
                    // Se asignan los valores de cada una de las posiciones del arreglo.
                    opciones[0] = opcion1;
                    opciones[1] = opcion2;
                    opciones[2] = opcion3;
                    opciones[3] = opcion4;

                    // Se crea una nueva pregunta que contiene cada uno de los valores recibidos y necesarios para 
                    // su creación, indicando también el número de pregunta para poder identificarla después.
                    Pregunta pregunta = new Pregunta(enunciado, opciones, correcta, nombreCategoria, contadorPreguntasTotal);
                    
                    // Se guarda el registro de la pregunta en la base de datos
                    server.getAdminBd().guardarPregunta(pregunta);

                    // Se informa al usuario mediante un mensaje en pantalla que la pregunta fue ingresada
                    // correctamente.
                    JOptionPane.showMessageDialog(null, "Pregunta ingresada correctamente", "Ingreso de Preguntas", JOptionPane.INFORMATION_MESSAGE);

                    // Se activa el boton para finalizar el ingreso de preguntas, debido a que ya se ha asegurado
                    // al menos una pregunta para dicha categoria.
                    bFinalizarIngreso.setEnabled(true);

                    
                    contadorPreguntasTotal++; // Se incrementa el contador de las preguntas en 1.
                    areaEnunciado.setText(""); // Se limpia el area del enunciado.
                    
                    // Se limpian los campos de texto de las opciones de respuesta para poder ingresar otra
                    // pregunta de una forma mas rápida y comoda.
                    tfOpcion1.setText("");
                    tfOpcion2.setText("");
                    tfOpcion3.setText("");
                    tfOpcion4.setText("");
                    contadorPreguntasCategoria++;
                }

            }
            
            // En caso de que el boton de finalizar ingreso sea presionado por el usuario.
            else if (e.getSource() == bFinalizarIngreso) {

                // Se imprime un mensaje en pantalla informando que el ingreso se completó satisfactoriamente.
                JOptionPane.showMessageDialog(null, "Ingreso completado exitosamente", "Ingreso de Cstegoría", JOptionPane.INFORMATION_MESSAGE);
                preguntasTemp.clear(); // Se limpia el arreglo de preguntas temporales, para poder utilizarlo de nuevo.
                tfNombreCategoria.setText(""); // Se limpia el campo de texto del nombre de la categoria
                activaContenedor(pNombreCategoria, true); // Se activa el contenedor donde se asigna el nombre de la 
                                                          // categoria, debido a que se pueden ingresar mas categorias.
                
                activaContenedor(pIngresoPreguntas, false); // Se desactiva el contenedor de ingreso de preguntas.
                bFinalizarIngreso.setEnabled(false); // Se deshabilita el botón de finalizar ingreso.
                
                // Se habilitan las pestañas de configuración de un examen y inicio de un examen, debido a que
                // al menos ya se tiene una categoria con al menos una pregunta.
                pestañas.setEnabledAt(1, true);
                pestañas.setEnabledAt(2, true);
                
                // Se limpian el area y los campos de texto para que sea mas rápido y comodo para el usuario
                // ingresar una nueva categoria con nuevas preguntas.
                areaEnunciado.setText("");
                tfOpcion1.setText("");
                tfOpcion2.setText("");
                tfOpcion3.setText("");
                tfOpcion4.setText("");
                contadorPreguntasCategoria = 1;

            }
            
            
            // En caso de que el usuario presione el boton de terminar configuracion general,
            // la cual es correspondiente al tiempo del examen y a la cantidad de preguntas
            // que tendrá el examen.
            else if (e.getSource() == bTerminarConfigGeneral) {

                // Se obtiene el arreglo con las categorias que se encuentran en la base de datos.
                ArrayList<String> categorias = server.getAdminBd().obtenerCategorias();

                // Si el arreglo que se obtuvó se encuentra vacío, entonces se imprime en pantalla 
                // un mensaje al usuario informando que no hay categorias registradas en la base
                // de datos.
                if (categorias.isEmpty()) {

                    JOptionPane.showMessageDialog(null, "No hay categorias registradas!", "Error", JOptionPane.WARNING_MESSAGE);
                }
                
                
                else {
                    // Si al menos hay una categoria, entonces se procede a obtener el numero de preguntas del
                    // examen y asignar la cantidad a la variable designada para ello.
                    cantidadPreguntasExamen = (Integer) spCantidadPreguntas.getValue();

                    // Si la cantidad de preguntas que se eligió para el examen es menor o igual
                    // que la cantidad de preguntas que se encuentran registradas en la base de
                    // datos, entonces se actualiza el comboBox de categorias con las categorias
                    // que se obtuvieron de la base de datos.
                    if (cantidadPreguntasExamen <= (contadorPreguntasTotal - 1)) {

                        // Ciclo que recorre el arreglo de categorias obtenido de la base de datos
                        // y agrega cada categoria del arreglo como un nuevo Item al comboBox de 
                        // categorias.
                        for (int i = 0; i < categorias.size(); i++) {

                            cbCategoriaExamen.addItem(categorias.get(i));
                        }

                        // Se desactiva el contenedor de configuracion general, ya que el usuario no podrá
                        // cambiar estos valores ya almacenados
                        activaContenedor(pConfiguracionGeneral, false);
                        // Se activa el contenedor de seleccion de preguntas, debido a que ya se ha verificado
                        // todo lo necesario para poder seleccionar las preguntas de un examen.
                        activaContenedor(pSeleccionPreguntas, true);
                        
                        // Se deshabilitan las pestañas de ingreso de categorias y de inicio de un examen
                        pestañas.setEnabledAt(0, false);
                        pestañas.setEnabledAt(2, false);

                        server.informacionBaseDeDatos();
                    }
                    
                    // Si la cantidad de preguntas que se eligió para el examen es mayor que la cantidad
                    // de preguntas que se encuentran registradas en la base de datos, se imprime un men-
                    // saje en pantalla informando al usuario que debe elegir una cantidad de preguntas
                    // entre la cantidad registradas.
                    else {
                        JOptionPane.showMessageDialog(null, "Seleccione una cantidad de preguntas menor o igual a " + (contadorPreguntasTotal - 1), "Error", JOptionPane.WARNING_MESSAGE);
                    }

                }

            }
            
            // En caso de que el usuario presione el boton de seleccionar categoria
            else if (e.getSource() == bSeleccionarCategoria) {
                
                // Se verifica que el item seleccionado del comboBox de categorias sea distinto del
                // item por defecto..
                if (cbCategoriaExamen.getSelectedIndex() != 0) {

                    // En caso de que sea distinto, entonces tomamos el nombre de la categoria del comboBox
                    // y la asignamos a la variable nombreCategoria designada para ello.
                    nombreCategoria = cbCategoriaExamen.getSelectedItem().toString();
                    
                    // Se hace el llamado del método del servidor que se encarga de llenar el comboBox con
                    // los números de preguntas de dicha categoria seleccionada.
                    server.preguntasPorCategoria(nombreCategoria, cbNumPregunta);

                }
                
                // Si el Item seleccionado es el Item por defecto, entonces se informa al usuario que debe
                // seleccionar una categoria.
                else {

                    JOptionPane.showMessageDialog(null, "Seleccione una categoría", "Error", JOptionPane.WARNING_MESSAGE);
                }

            }
            
            // En caso de que el usuario presione el boton para mostrar el enunciado de una pregunta
            else if (e.getSource() == bMostrarEnunciado) {

                // Se verifica que el Item seleccionado por el usuario sea diferente del Item que está
                // seleccionado por defecto.
                if (cbNumPregunta.getSelectedIndex() != 0) {

                    // Si lo es, entonces se toma del item seleccionado el identificador de la pregunta
                    // el cual tiene relación con el número de la pregunta.
                    int id = Integer.parseInt(cbNumPregunta.getSelectedItem().toString().split(" ")[1]);

                    // Asignamos a la variable para almacenar una pregunta temporal, la pregunta que tiene
                    // el identificador anteriormente asignado. La cual es retornada por un método del 
                    // servidor que busca en el arreglo de preguntas de la categoria correspondiente
                    // la pregunta con el identificador solicitado.
                    preguntaTemp = server.extraerCategoria(nombreCategoria).extraerPregunta(id);

                    // Se muestra en el area el enunciado de la pregunta que se extrajó anteriormente.
                    areaMostrarEnunciado.setText(preguntaTemp.getEnunciado());
                }
                
                // Si el Item Seleccionado es el item por defecto, entonces se informa al usuario mediante
                // un mensaje en pantalla que debe seleccionar una pregunta.
                else {
                    JOptionPane.showMessageDialog(null, "Seleccione una pregunta", "Error", JOptionPane.WARNING_MESSAGE);
                }

            }
            
            
            // En caso de que el usuario desee añadir la pregunta seleccionada al examen
            else if (e.getSource() == bAñadirPreguntaExamen) {

                // Se verifica que el valor de la pregunta temporal sea distinto de null.
                if ((preguntaTemp != null)) {

                    preguntasTemp.add(preguntaTemp); // Se agrega la pregunta al arreglo temporal de preguntas 
                                                     // del examen.
                    
                    // Se imprime un mensaje en pantalla informando al usuario que la pregunto se añadió
                    // satisfactoriamente.
                    JOptionPane.showMessageDialog(null, "Pregunta añadida correctamente", "Configuración de examen", JOptionPane.INFORMATION_MESSAGE);
                   
                    // Se elimina del comboBox el Item que estaba seleccionado, el cual correspondía a la pregunta
                    // que se acababa de añadir.
                    cbNumPregunta.removeItemAt(cbNumPregunta.getSelectedIndex());
                    
                    // Se llama el metodo que se encarga de eliminar la pregunta agregada recientemente
                    // del arreglo de preguntas de la categoria correspondiente.
                    server.eliminarPregunta(nombreCategoria, preguntaTemp.getNumeroPregunta());

                    preguntaActual++; // Se incrementa el valor de preguntaActual en 1
                    // Se actualiza la información del label de la cantidad de preguntas seleccionadas para
                    // el examen.
                    lPreguntasSeleccionadas.setText("Preguntas seleccionadas: " + preguntaActual);
                    preguntaTemp = null; // Se reinicia el valor de preguntaTemp a null.
                    areaMostrarEnunciado.setText(""); // Se limpia el area de texto

                    // Si la cantidad de preguntas agregadas al examen es igual a la cantidad de preguntas
                    // que se había escogido para el examen, entonces se habilita el boton de guardar con-
                    // figuración y se desactiva el contenedor de seleccion de preguntas.
                    if (preguntaActual == cantidadPreguntasExamen) {

                        bGuardarConfiguracion.setEnabled(true);
                        activaContenedor(pSeleccionPreguntas, false);
                        // Se informa al usuario mediante un mensaje en pantalla que ha alcanzado el limite
                        // de preguntas.
                        JOptionPane.showMessageDialog(null, "Número límite de preguntas alcanzado", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
                // Si resulta que la variable preguntaTemp tiene como valor null, quiere decir que no se ha
                // seleccionado una pregunta, por lo tanto se imprime un mensaje en pantalla al usuario
                // solicitando que por favor seleccione una pregunta.
                else {
                    JOptionPane.showMessageDialog(null, "Seleccione una pregunta", "Error", JOptionPane.WARNING_MESSAGE);
                }

            }
            
            
            // En caso de que el usuario presione el boton de guardar configuracion
            else if (e.getSource() == bGuardarConfiguracion) {
                
                // Se asigna el valor obtenido para la duracion del examen a una variable de tipo int, designada
                // especialmente para ello.
                int duracionExamen = (Integer) spDuracionExamen.getValue();

                // Se crea un nuevo objeto examen con la informacion obtenida (cantidad de preguntas del examen,
                // duracion del examen y las preguntas seleccionadas) y un identificador, el cual será
                // el número de examen que se ha configurado.
                Examen nuevoExamen = new Examen(contadorExamenes, cantidadPreguntasExamen, duracionExamen, preguntasTemp);

                // Se llama el método que se encarga de almacenar el examen creado recientemente en la
                // base de datos.
                server.getAdminBd().guardarExamen(nuevoExamen);

                // Ciclo que recorre el arreglo de preguntas temporales y se encarga de ingresar el registro de las 
                // preguntas seleccionadas para el examen recien configurado en la base de datos.
                for (int i = 0; i < preguntasTemp.size(); i++) {

                    server.getAdminBd().guardarPreguntasSeleccionadas(preguntasTemp.get(i), i + 1, contadorExamenes);

                }

                // Se imprime en pantalla un mensaje informando al usuario que la configuración del examen fue guardada
                // con éxito.
                JOptionPane.showMessageDialog(null, "La configuración fue guardada exitosamente", "Guardar Configuración", JOptionPane.INFORMATION_MESSAGE);

                // Se activa el contenedor de configuracion general para permitir realizar otra configuración.
                activaContenedor(pConfiguracionGeneral, true);
                // Se desactiva el contenedor de seleccion de preguntas.
                activaContenedor(pSeleccionPreguntas, false);
                
                // Se activan las pestañas de ingreso de categorias y seleccion de una configuracion de un examen.
                pestañas.setEnabledAt(0, true);
                pestañas.setEnabledAt(2, true);
                
                spCantidadPreguntas.setValue(1); // Se reinicia el valor del split de la cantidad de preguntas.
                spDuracionExamen.setValue(30); // Se reinicia el valor del split de la duracion del examen
                bGuardarConfiguracion.setEnabled(false); // Se deshabilita el boton de guardar configuracion
                
                // Se agrega al comboBox de configuraciones de examenes guardados, el examen configurado recientemente.
                cbConfiguracionesGuardadas.addItem("Examen " + contadorExamenes);

                preguntaActual = 0; // Se reinicia el valor de la cantidad de preguntas seleccionadas.
                duracionExamen = 1; // Se reinicia el valor de la duracion del examen
                preguntasTemp.clear(); // Se limpia el arreglo de preguntas temporales.
                contadorExamenes++; // Se incrementa el contador de examenes en 1.
                
                cbCategoriaExamen.removeAllItems(); // Se eliminan todos los items del comboBox de categorías
                // Se agrega el Item que estará por defecto en el comboBox de categorías
                cbCategoriaExamen.addItem("Seleccione una categoría"); 
                cbNumPregunta.removeAllItems(); // Se eliminan todos los items del comboBox de preguntas
                // Se agrega el Item que estará por defecto en el comboBox de preguntas
                cbNumPregunta.addItem("Seleccione una pregunta");
                areaMostrarEnunciado.setText(""); // Se limpia el area de texto
                // Se reinicia el valor del label de las preguntas seleccionadas
                lPreguntasSeleccionadas.setText("Preguntas seleccionadas: 0");
            }
            
            // En caso de que el usuario presione el boton de seleccionar una configuración
            else if (e.getSource() == bSeleccionarConfiguracion) {

                // Se verifica que la opción escogida por el usuario no sea la seleccionada por defecto
                if (cbConfiguracionesGuardadas.getSelectedIndex() != 0) {

                    // Se extrae el numero del examen del Item seleccionado. 
                    numeroExamenSeleccionado = Integer.parseInt(cbConfiguracionesGuardadas.getSelectedItem().toString().split(" ")[1]);

                    // Con el número de examen obtenido se obtiene  el número de preguntas de dicho examen
                    // y de igual forma se extrae la duración del examen, estos datos se obtienen mediante
                    // una consulta en la base de datos
                    numeroPreguntasExamenSeleccionado = server.getAdminBd().numeroRegistros("preguntasSeleccionadas WHERE numeroExamen = '" + numeroExamenSeleccionado + "' ");
                    duracionExamenSeleccionado = server.getAdminBd().duracionExamen(numeroExamenSeleccionado);

                    // Se actualizan los labels de numero de preguntas y duración del examen con los valores
                    // obtenidos recientemente.
                    lNumPregunasVerCongif.setText("Cantidad de Preguntas: " + numeroPreguntasExamenSeleccionado);
                    lDuracionVerConfig.setText("Duración del Examen: " + duracionExamenSeleccionado + " minutos");

                    // Ciclo que se encarga de ir ingresando la cantidad de preguntas que el examen
                    // tiene en el comboBox de preguntas, para que puedan ser visualizadas por el
                    // usuario
                    for (int i = 0; i < numeroPreguntasExamenSeleccionado; i++) {

                        cbPreguntasConfiguradas.addItem("Pregunta " + (i + 1));
                    }

                    // Se desactiva el contenedor de escoger una configuración, pues ya se ha escogido una
                    // y se activa el contenedor para poder visualizar las preguntas del examen que se
                    // seleccionó.
                    activaContenedor(pEscogerConfiguracion, false);
                    activaContenedor(pVisualizarConfiguracion, true);
                    
                    // Se habilitan los botones de cambiar configuración y desarrollar examen, debido a que el
                    // usuario puede tener la necesidad de cambiar el examen que desea realizar o también
                    // puede elegir desarrolar el examen
                    bCambiarConfiguracion.setEnabled(true);
                    bDesarrollarExamen.setEnabled(true);

                }
                
                // En caso de que el Item seleccionado sea el que esta por defecto, entonces se imprime en
                // pantalla un mensaje informando al usuario que debe seleccionar un examen.
                else {
                    JOptionPane.showMessageDialog(null, "Seleccione un examen", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
            
            // En caso de que el usuario presione el boton de mostrar la pregunta seleccionada
            else if (e.getSource() == bMostrarPreguntaSeleccionada) {

                // Si el item seleccionado por el usuario es diferente del item que esta por defecto, entonces
                // se procede a hacer lo siguiente:
                if (cbPreguntasConfiguradas.getSelectedIndex() != 0) {

                    // Se obtiene el número de la pregunta seleccionada, separando el String y tomando la posición
                    // que corresponde al número de la pregunta.
                    int preguntaSeleccionada = Integer.parseInt(cbPreguntasConfiguradas.getSelectedItem().toString().split(" ")[1]);

                    // Se llama el método correspondiente que permite obtener una pregunta mediante el número de examen
                    // y el número de pregunta.
                    String enunciadoPregunta = server.getAdminBd().obtenerPreguntaExamen(numeroExamenSeleccionado, preguntaSeleccionada).getEnunciado();
                    areaPreguntaConfigurada.setText(enunciadoPregunta); // Se muestra en el area de texto el enunciado de 
                                                                        // la pregunta que se obtuvo recientemente

                }
                
                // Si el usuario presiona el boton de mostrar enunciado pero no ha seleccionado ninguna pregunta,
                // entonces se imprime en pantalla un mensaje informando al usuario que debe seleccionar una
                // pregunta
                else {
                    JOptionPane.showMessageDialog(null, "Seleccione una pregunta", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
            
            // En caso de que el usuario presione el boton de cambiar configuración
            else if (e.getSource() == bCambiarConfiguracion) {

                // Se reinician las variables de: numero examen seleccionado, el numero de pregunta
                // seleccionada y la duración designada para el examen. Esto con el fin de permitir
                // al usuario elegir otra configuración.
                numeroExamenSeleccionado = 0;
                numeroPreguntasExamenSeleccionado = 0;
                duracionExamenSeleccionado = 0;

                // Se eliminan todas las preguntas que se encuentren dentro del comboBox y se
                // agrega el Item por defecto.
                cbPreguntasConfiguradas.removeAllItems();
                cbPreguntasConfiguradas.addItem("Seleccione una pregunta");
                
                // Se limpia el area de texto en donde se muestra el enunciado de la pregunta que se 
                // había seleccionado.
                areaPreguntaConfigurada.setText("");
                
                
                // Se actualizan los labels de la cantidad de preguntas del examen y duración del examen
                lNumPregunasVerCongif.setText("Cantidad de Preguntas: ");
                lDuracionVerConfig.setText("Duración del examen: ");

                // Se activa el contenedor de escoger configuración, para permitir al usuario elegir una nueva
                // configuración y se desactiva el contenedor que permite visualizar las preguntas, pues aún
                // no se ha seleccionado ningun examen.
                activaContenedor(pEscogerConfiguracion, true);
                activaContenedor(pVisualizarConfiguracion, false);
                
                // Se desactivan los botones de cambiar configuración y desarrollar examen, debido a que no
                // hay ningun examen seleccionado.
                bCambiarConfiguracion.setEnabled(false);
                bDesarrollarExamen.setEnabled(false);
                // Se cambia el item seleccionado del comboBox de configuraciones por el item por defecto. 
                cbConfiguracionesGuardadas.setSelectedIndex(0);
            }
            
            // En caso de que el usuario presione el boton para desarrollar el examen seleccionado
            else if (e.getSource() == bDesarrollarExamen) {

                // Se asignan la duración, numero de examen y numero de preguntas del examen seleccionado
                // a las variables que representan el examen actual.
                server.setDuracionExamenActual(duracionExamenSeleccionado);
                server.setNumeroExamen(numeroExamenSeleccionado);
                server.setNumeroPreguntasExamen(numeroPreguntasExamenSeleccionado);

                // Se actualizan los labels de la cantidad de preguntas del examen y duración del examen
                // para que el usuario los pueda visualizar antes de iniciar el examen.
                lCantidadSeccionada.setText("Cantidad de Preguntas: " + numeroPreguntasExamenSeleccionado);
                lDuracionSeleccionada.setText("Duración del examen: " + duracionExamenSeleccionado + " minutos");

                // Se deshabilitan las pestañas de ingresar categorias, configurar un examen y en la que se 
                // puede seleccionar una configuración de examen anteriormente guardada
                pestañas.setEnabledAt(0, false);
                pestañas.setEnabledAt(1, false);
                pestañas.setEnabledAt(2, false);
                pestañas.setEnabledAt(3, true);
                pestañas.setSelectedIndex(3);  // Se posiciona al usuario en la pestaña de desarrollo del examen.

                // Con el siguiente ciclo se ingresan las preguntas del examen al comboBox que permite seleccionar
                // una pregunta y ver los resultados de cada una de ellas
                for (int i = 0; i < numeroPreguntasExamenSeleccionado; i++) {

                    cbPreguntaRespondida.addItem("Pregunta " + (i + 1));
                }
                
                // Se activa el contenedor de escoger una configuración y se desactiva el que permite visualizar
                // las preguntas de la configuración de examen seleccionada.
                activaContenedor(pEscogerConfiguracion, true);
                activaContenedor(pVisualizarConfiguracion, false);
                
                // Se deshabilitan los botones de cambiar configuración y desarrollar examen.
                bCambiarConfiguracion.setEnabled(false);
                bDesarrollarExamen.setEnabled(false);
                // Se posiciona el comboBox de escoger una configuración en el Item por defecto
                cbConfiguracionesGuardadas.setSelectedIndex(0);
                // Se eliminan todas las preguntas que estaban en el comboBox de preguntas y se agrega el Item
                // que va a estar por defecto.
                cbPreguntasConfiguradas.removeAllItems();
                cbPreguntasConfiguradas.addItem("Seleccione una pregunta");

            }
            
            // En caso de que el usuario decida iniciar el examen.
            else if (e.getSource() == bIniciarExamen) {

                // Se llama el método del servidor que se encarga de enviar las preguntas a todos los clientes
                // que se encuentran conectados.
                server.enviarPreguntas();
                // Se actualiza el label del estado del examen para permitir visualizar al usuario que el examen
                // se esta realizando.
                lEstadoExamen.setText("Estado del Examen: Realizándose");
                bIniciarExamen.setEnabled(false); // Se deshabilita el boton de iniciar el examen.
            }
            
            // En caso de que el usuario presione el boton de mostrar el enunciado de la pregunta seleccionada
            // en la pestaña de los resultados.
            else if (e.getSource() == bMostrarEnunciadoRespondida) {

                // Se verifica que el Item seleccionado por el usuario no sea el item que esta por defecto.
                if (cbPreguntaRespondida.getSelectedIndex() != 0) {

                    // Se obtiene el número de pregunta seleccionada, separando el String y tomando la posición que
                    // contiene dicho número.
                    int numeroPregunta = Integer.parseInt(cbPreguntaRespondida.getSelectedItem().toString().split(" ")[1]);
                    
                                        
                    // Se obtiene la pregunta seleccionada, llamando el método del servidor que permite obtener una
                    // pregunta dado el número de la pregunta en el examen.
                    Pregunta pregunta = server.preguntaSeleccionadaCliente(numeroPregunta);

                    String enunciado = pregunta.getEnunciado(); // Se obtiene el enunciado de la pregunta obtenida reciente-
                                                                // mente y se almacena en una variable String designada para
                                                                // esto.
                    
                    // Se obtiene la información de la respuesta dada por el usuario y la respuesta correcta de la pregunta
                    // los valores anteriores se encontraban almacenados en la base de datos, entonces mediante una consulta
                    // estos pueden ser obtenidos.
                    String[] informacionPregunta = server.getAdminBd().resultadosPregunta(numeroPregunta, server.getNumeroExamen()).split(" ");

                    // Se asigna el número de respuesta dada y el número de la respuesta correcta en variables
                    // de tipo int para que sea más fácil manejar estos valores.
                    int respuestaDada = Integer.parseInt(informacionPregunta[0]);
                    int respuestaCorrecta = Integer.parseInt(informacionPregunta[1]);

                    // Se define un String para mostrar la respuesta que dieron los estudiantes como grupo
                    // a cada pregunta, se inicializa en No respondida en caso de que los estudiantes no
                    // respondan alguna de las preguntas.
                    String respuestaGrupo = "No respondida";
                    
                    // En caso de que la respuesta dada sea un valor valido, es decir, sea distinta de 0,
                    // entonces se cambia el valor de la respuesta del grupo por la respuesta que se le 
                    // dio a la pregunta junto con el número de dicha opción.
                    if(respuestaDada != 0){
                        respuestaGrupo = respuestaDada + ". " + pregunta.getRespuestas()[respuestaDada - 1];
                    }
                                        
                    // Se asigna un String para guardar el número y la información de la respuesta correcta
                    // a la pregunta.
                    String respuestaC = respuestaCorrecta + ". " + pregunta.getRespuestas()[respuestaCorrecta - 1];

                    // Se obtiene la calificación total del grupo y se almacena temporalmente en una variable
                    // designado para esto.
                    
                    // Se muestra el enunciado de la pregunta en el area de texto
                    areaEnunciadoRespondida.setText(enunciado);
                    // Se muestra en el label de la respuesta dada la información referente a esta.
                    lRespuestaDada.setText("Respuesta dada por el grupo: " + respuestaGrupo);
                    // Se muestra en el label de la respuesta correcta la información referente a esta.
                    lRespuestaCorrecta.setText("Respuesta Correcta: " + respuestaC);
                    

                }
                
                // Si el Item seleccionado es el item que esta por defecto, se imprime un mensaje en pantalla
                // informando al usuario que debe seleccionar una pregunta de la lista que se muestra en el
                // comboBox.
                else {
                    JOptionPane.showMessageDialog(null, "Seleccione una pregunta", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
            
            // En caso de que el usuario presione el boton de terminar de ver los resultados
            else if (e.getSource() == bTerminarVerResultados){
                
                // Se habilitan todas las pestañas menos la de visualizar resultados, para permitir
                // al usuario realizar los que desea.
                pestañas.setEnabledAt(0, true);
                pestañas.setEnabledAt(1, true);
                pestañas.setEnabledAt(2, true);
                pestañas.setEnabledAt(4, false);
                // Se ubica al usuario en la primera pestaña, en la cual tiene la opción de ingresar categorias,
                // lo anterior no quiere decir que deba ingresar categorias, el usuario puede seleccionar una
                // configuracion de examen almacenada y desarrollar dicho examen.
                pestañas.setSelectedIndex(0);
                server.getAdminBd().reiniciarDatosBD(); // Se reinician los valores de los campos de la base de datos
                                                        // referentes a el desarrollo del examen, como las respuestas
                                                        // dadas a las preguntas, el estudiante que la respondio, etc.
                
                // Se reinician los valores de los labels de la cantidad de preguntas del examen y la duración del 
                // mismo.
                lCantidadSeccionada.setText("Cantidad de Preguntas:");
                lDuracionSeleccionada.setText("Duración del Examen:");
                // Se eliminan todos los items del comboBox de preguntas de la pestaña de resultados
                // y se agrega el Item que va a estar por defecto.
                cbPreguntaRespondida.removeAllItems();
                cbPreguntaRespondida.addItem("Seleccione una pregunta");
                
                //Se reinician valores de componentes de la pestaña
                
                areaEnunciadoRespondida.setText("");
                lRespuestaDada.setText("Respuesta Dada: ");
                lRespuestaCorrecta.setText("Respuesta Correcta: ");
                lCalificacionGrupo.setText("Calificación del grupo: ");
                
            }
        }

        // Para cerrar la ventana
        @Override
        public void windowClosing(WindowEvent we) {
        
            // En caso de que el examen no se haya iniciado aún, entonces se le pregunta al usuario
            // si esta seguro de querer salir y si la respuesta del usuario es sí, entonces se 
            // da la orden de salir, de lo contrario la ventaba permanece abierta.
            if (!server.isExamenIniciado()) {

                int option = JOptionPane.showConfirmDialog(null, "¿Desea salir?");

                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
            // Si el examen ya se había iniciado, entonces se informa al usuario mediante un mensaje en pantalla
            // que el examen ha sido iniciado y por lo tanto no puede salir.
            else
            {
                JOptionPane.showMessageDialog(null, "El examen ha sido iniciado, no puede salir");
            }
            
        }
        
        @Override
        public void windowOpened(WindowEvent we) {}

        @Override
        public void windowClosed(WindowEvent we) {}

        @Override
        public void windowIconified(WindowEvent we) {}

        @Override
        public void windowDeiconified(WindowEvent we) {}

        @Override
        public void windowActivated(WindowEvent we) {}

        @Override
        public void windowDeactivated(WindowEvent we) {}

    }       

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        panelPrincipal = new javax.swing.JPanel();
        pestañas = new javax.swing.JTabbedPane();
        pIngresarCategoria = new javax.swing.JPanel();
        pTituloCategoria = new javax.swing.JPanel();
        logoUV1 = new javax.swing.JLabel();
        lExamen1 = new javax.swing.JLabel();
        lIngresoCategoria = new javax.swing.JLabel();
        pNombreCategoria = new javax.swing.JPanel();
        lNombre = new javax.swing.JLabel();
        tfNombreCategoria = new javax.swing.JTextField();
        bAsignarNombreC = new javax.swing.JButton();
        pIngresoPreguntas = new javax.swing.JPanel();
        lEnunciado = new javax.swing.JLabel();
        lOpcion1 = new javax.swing.JLabel();
        lOpcion2 = new javax.swing.JLabel();
        lOpcion3 = new javax.swing.JLabel();
        lOpcion4 = new javax.swing.JLabel();
        lOpcionCorrecta = new javax.swing.JLabel();
        barras = new javax.swing.JScrollPane();
        areaEnunciado = new javax.swing.JTextArea();
        tfOpcion1 = new javax.swing.JTextField();
        tfOpcion2 = new javax.swing.JTextField();
        tfOpcion3 = new javax.swing.JTextField();
        tfOpcion4 = new javax.swing.JTextField();
        cbOpcionCorrecta = new javax.swing.JComboBox();
        bAñadirPregunta = new javax.swing.JButton();
        bFinalizarIngreso = new javax.swing.JButton();
        pConfigurarExamen = new javax.swing.JPanel();
        pTituloExamen = new javax.swing.JPanel();
        logoUV2 = new javax.swing.JLabel();
        lExamen2 = new javax.swing.JLabel();
        lConfigExamen = new javax.swing.JLabel();
        pConfiguracionGeneral = new javax.swing.JPanel();
        lNumPreguntas = new javax.swing.JLabel();
        lDuracionExamen = new javax.swing.JLabel();
        lMinutos = new javax.swing.JLabel();
        bTerminarConfigGeneral = new javax.swing.JButton();
        spCantidadPreguntas = new javax.swing.JSpinner();
        spDuracionExamen = new javax.swing.JSpinner();
        pSeleccionPreguntas = new javax.swing.JPanel();
        lPreguntasSeleccionadas = new javax.swing.JLabel();
        barrasEnunciado = new javax.swing.JScrollPane();
        areaMostrarEnunciado = new javax.swing.JTextArea();
        cbNumPregunta = new javax.swing.JComboBox();
        bMostrarEnunciado = new javax.swing.JButton();
        bAñadirPreguntaExamen = new javax.swing.JButton();
        lCategoria = new javax.swing.JLabel();
        cbCategoriaExamen = new javax.swing.JComboBox();
        bSeleccionarCategoria = new javax.swing.JButton();
        bGuardarConfiguracion = new javax.swing.JButton();
        pSeleccionarConfiguracion = new javax.swing.JPanel();
        pTituloConfig = new javax.swing.JPanel();
        logoUV5 = new javax.swing.JLabel();
        lExamen5 = new javax.swing.JLabel();
        lConfigExamen1 = new javax.swing.JLabel();
        pEscogerConfiguracion = new javax.swing.JPanel();
        cbConfiguracionesGuardadas = new javax.swing.JComboBox();
        bSeleccionarConfiguracion = new javax.swing.JButton();
        pVisualizarConfiguracion = new javax.swing.JPanel();
        barrasEnunciado1 = new javax.swing.JScrollPane();
        areaPreguntaConfigurada = new javax.swing.JTextArea();
        cbPreguntasConfiguradas = new javax.swing.JComboBox();
        bMostrarPreguntaSeleccionada = new javax.swing.JButton();
        lDuracionVerConfig = new javax.swing.JLabel();
        lNumPregunasVerCongif = new javax.swing.JLabel();
        bDesarrollarExamen = new javax.swing.JButton();
        bCambiarConfiguracion = new javax.swing.JButton();
        pIniciarExamen = new javax.swing.JPanel();
        pTituloRealizarExamen = new javax.swing.JPanel();
        logoUV3 = new javax.swing.JLabel();
        lExamen3 = new javax.swing.JLabel();
        lInicioExamen = new javax.swing.JLabel();
        pInformacionExamen = new javax.swing.JPanel();
        lCantidadSeccionada = new javax.swing.JLabel();
        lDuracionSeleccionada = new javax.swing.JLabel();
        pRealizacionExamen = new javax.swing.JPanel();
        lUsuarios = new javax.swing.JLabel();
        lUsuario1 = new javax.swing.JLabel();
        lUsuario2 = new javax.swing.JLabel();
        lUsuario3 = new javax.swing.JLabel();
        lTiempoRestante = new javax.swing.JLabel();
        lEstadoExamen = new javax.swing.JLabel();
        bIniciarExamen = new javax.swing.JButton();
        lNombreUsuario1 = new javax.swing.JLabel();
        lNombreUsuario2 = new javax.swing.JLabel();
        lNombreUsuario3 = new javax.swing.JLabel();
        lTiempo = new javax.swing.JLabel();
        pResultados = new javax.swing.JPanel();
        pTituloResultados = new javax.swing.JPanel();
        logoUV4 = new javax.swing.JLabel();
        lExamen4 = new javax.swing.JLabel();
        lResultados = new javax.swing.JLabel();
        pResultadosExamen = new javax.swing.JPanel();
        barrasEnunciadoP = new javax.swing.JScrollPane();
        areaEnunciadoRespondida = new javax.swing.JTextArea();
        cbPreguntaRespondida = new javax.swing.JComboBox();
        bMostrarEnunciadoRespondida = new javax.swing.JButton();
        lRespuestaDada = new javax.swing.JLabel();
        lRespuestaCorrecta = new javax.swing.JLabel();
        lCalificacionGrupo = new javax.swing.JLabel();
        bTerminarVerResultados = new javax.swing.JButton();

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jScrollPane4.setViewportView(jTextArea4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelPrincipal.setBackground(new java.awt.Color(255, 255, 255));

        pestañas.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pestañas.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N

        pIngresarCategoria.setBackground(new java.awt.Color(178, 34, 34));

        pTituloCategoria.setBackground(new java.awt.Color(178, 34, 34));
        pTituloCategoria.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        logoUV1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UniversidaddelValleCali.gif"))); // NOI18N

        lExamen1.setFont(new java.awt.Font("SansSerif", 3, 26)); // NOI18N
        lExamen1.setForeground(new java.awt.Color(255, 255, 255));
        lExamen1.setText("Examen Colaborativo Univalle");

        lIngresoCategoria.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        lIngresoCategoria.setForeground(new java.awt.Color(255, 255, 255));
        lIngresoCategoria.setText("Ingreso de Categorías");

        javax.swing.GroupLayout pTituloCategoriaLayout = new javax.swing.GroupLayout(pTituloCategoria);
        pTituloCategoria.setLayout(pTituloCategoriaLayout);
        pTituloCategoriaLayout.setHorizontalGroup(
            pTituloCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTituloCategoriaLayout.createSequentialGroup()
                .addGroup(pTituloCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lIngresoCategoria)
                    .addComponent(lExamen1, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logoUV1)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        pTituloCategoriaLayout.setVerticalGroup(
            pTituloCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoUV1, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .addGroup(pTituloCategoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lExamen1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lIngresoCategoria)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pNombreCategoria.setBackground(new java.awt.Color(178, 34, 34));
        pNombreCategoria.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Nombre de Categoría", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lNombre.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lNombre.setForeground(new java.awt.Color(255, 255, 255));
        lNombre.setText(" Nombre:");
        lNombre.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tfNombreCategoria.setForeground(new java.awt.Color(61, 61, 61));

        bAsignarNombreC.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bAsignarNombreC.setText("Asignar nombre");

        javax.swing.GroupLayout pNombreCategoriaLayout = new javax.swing.GroupLayout(pNombreCategoria);
        pNombreCategoria.setLayout(pNombreCategoriaLayout);
        pNombreCategoriaLayout.setHorizontalGroup(
            pNombreCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pNombreCategoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(tfNombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(bAsignarNombreC)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pNombreCategoriaLayout.setVerticalGroup(
            pNombreCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pNombreCategoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pNombreCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(tfNombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bAsignarNombreC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pIngresoPreguntas.setBackground(new java.awt.Color(178, 34, 34));
        pIngresoPreguntas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ingreso de Preguntas", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lEnunciado.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lEnunciado.setForeground(new java.awt.Color(255, 255, 255));
        lEnunciado.setText(" Enunciado: ");
        lEnunciado.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lOpcion1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lOpcion1.setForeground(new java.awt.Color(255, 255, 255));
        lOpcion1.setText(" Opción 1:");
        lOpcion1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lOpcion2.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lOpcion2.setForeground(new java.awt.Color(255, 255, 255));
        lOpcion2.setText(" Opción 2:");
        lOpcion2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lOpcion3.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lOpcion3.setForeground(new java.awt.Color(255, 255, 255));
        lOpcion3.setText(" Opción 3:");
        lOpcion3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lOpcion4.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lOpcion4.setForeground(new java.awt.Color(255, 255, 255));
        lOpcion4.setText(" Opción 4:");
        lOpcion4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lOpcionCorrecta.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lOpcionCorrecta.setForeground(new java.awt.Color(255, 255, 255));
        lOpcionCorrecta.setText(" Opción Correcta:");
        lOpcionCorrecta.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        areaEnunciado.setColumns(20);
        areaEnunciado.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        areaEnunciado.setForeground(new java.awt.Color(61, 61, 61));
        areaEnunciado.setLineWrap(true);
        areaEnunciado.setRows(5);
        areaEnunciado.setWrapStyleWord(true);
        barras.setViewportView(areaEnunciado);

        tfOpcion1.setForeground(new java.awt.Color(61, 61, 61));

        tfOpcion2.setForeground(new java.awt.Color(61, 61, 61));

        tfOpcion3.setForeground(new java.awt.Color(61, 61, 61));

        tfOpcion4.setForeground(new java.awt.Color(61, 61, 61));

        cbOpcionCorrecta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4" }));

        bAñadirPregunta.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bAñadirPregunta.setText("Añadir pregunta");

        javax.swing.GroupLayout pIngresoPreguntasLayout = new javax.swing.GroupLayout(pIngresoPreguntas);
        pIngresoPreguntas.setLayout(pIngresoPreguntasLayout);
        pIngresoPreguntasLayout.setHorizontalGroup(
            pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIngresoPreguntasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lOpcion3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lOpcion2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lOpcion1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lEnunciado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lOpcion4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23)
                .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barras, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pIngresoPreguntasLayout.createSequentialGroup()
                        .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tfOpcion3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                                .addComponent(tfOpcion2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(tfOpcion1, javax.swing.GroupLayout.Alignment.LEADING))
                            .addComponent(tfOpcion4, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pIngresoPreguntasLayout.createSequentialGroup()
                                .addComponent(lOpcionCorrecta, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbOpcionCorrecta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(bAñadirPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 31, Short.MAX_VALUE))
        );
        pIngresoPreguntasLayout.setVerticalGroup(
            pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIngresoPreguntasLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lEnunciado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lOpcion1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfOpcion1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pIngresoPreguntasLayout.createSequentialGroup()
                        .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lOpcion2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pIngresoPreguntasLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(tfOpcion2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfOpcion3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lOpcion3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lOpcion4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfOpcion4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pIngresoPreguntasLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pIngresoPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lOpcionCorrecta, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbOpcionCorrecta, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(bAñadirPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        bFinalizarIngreso.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        bFinalizarIngreso.setText("Terminar ingreso");

        javax.swing.GroupLayout pIngresarCategoriaLayout = new javax.swing.GroupLayout(pIngresarCategoria);
        pIngresarCategoria.setLayout(pIngresarCategoriaLayout);
        pIngresarCategoriaLayout.setHorizontalGroup(
            pIngresarCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIngresarCategoriaLayout.createSequentialGroup()
                .addGroup(pIngresarCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pIngresarCategoriaLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(pIngresarCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pTituloCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pIngresarCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(pIngresoPreguntas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pNombreCategoria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(pIngresarCategoriaLayout.createSequentialGroup()
                        .addGap(254, 254, 254)
                        .addComponent(bFinalizarIngreso)))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        pIngresarCategoriaLayout.setVerticalGroup(
            pIngresarCategoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIngresarCategoriaLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pTituloCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pNombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pIngresoPreguntas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bFinalizarIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        pestañas.addTab("Ingresar Categorías", pIngresarCategoria);

        pConfigurarExamen.setBackground(new java.awt.Color(178, 34, 34));

        pTituloExamen.setBackground(new java.awt.Color(178, 34, 34));
        pTituloExamen.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        logoUV2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UniversidaddelValleCali.gif"))); // NOI18N

        lExamen2.setFont(new java.awt.Font("SansSerif", 3, 26)); // NOI18N
        lExamen2.setForeground(new java.awt.Color(255, 255, 255));
        lExamen2.setText("Examen Colaborativo Univalle");

        lConfigExamen.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        lConfigExamen.setForeground(new java.awt.Color(255, 255, 255));
        lConfigExamen.setText("Configuración del Examen");

        javax.swing.GroupLayout pTituloExamenLayout = new javax.swing.GroupLayout(pTituloExamen);
        pTituloExamen.setLayout(pTituloExamenLayout);
        pTituloExamenLayout.setHorizontalGroup(
            pTituloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTituloExamenLayout.createSequentialGroup()
                .addGroup(pTituloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lExamen2)
                    .addComponent(lConfigExamen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(logoUV2)
                .addGap(35, 35, 35))
        );
        pTituloExamenLayout.setVerticalGroup(
            pTituloExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoUV2, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .addGroup(pTituloExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lExamen2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lConfigExamen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pConfiguracionGeneral.setBackground(new java.awt.Color(178, 34, 34));
        pConfiguracionGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configuración General", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lNumPreguntas.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lNumPreguntas.setForeground(new java.awt.Color(255, 255, 255));
        lNumPreguntas.setText("Cantidad de Preguntas:");
        lNumPreguntas.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lDuracionExamen.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lDuracionExamen.setForeground(new java.awt.Color(255, 255, 255));
        lDuracionExamen.setText("Duración del examen:");
        lDuracionExamen.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lMinutos.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lMinutos.setForeground(new java.awt.Color(255, 255, 255));
        lMinutos.setText("minutos");
        lMinutos.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        bTerminarConfigGeneral.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bTerminarConfigGeneral.setText("Aceptar");

        spCantidadPreguntas.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));

        spDuracionExamen.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(30), Integer.valueOf(1), null, Integer.valueOf(1)));

        javax.swing.GroupLayout pConfiguracionGeneralLayout = new javax.swing.GroupLayout(pConfiguracionGeneral);
        pConfiguracionGeneral.setLayout(pConfiguracionGeneralLayout);
        pConfiguracionGeneralLayout.setHorizontalGroup(
            pConfiguracionGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pConfiguracionGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pConfiguracionGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pConfiguracionGeneralLayout.createSequentialGroup()
                        .addComponent(lNumPreguntas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spCantidadPreguntas, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addComponent(lDuracionExamen)
                        .addGap(35, 35, 35)
                        .addComponent(spDuracionExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(lMinutos, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pConfiguracionGeneralLayout.createSequentialGroup()
                        .addGap(221, 221, 221)
                        .addComponent(bTerminarConfigGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pConfiguracionGeneralLayout.setVerticalGroup(
            pConfiguracionGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pConfiguracionGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pConfiguracionGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lNumPreguntas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lDuracionExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lMinutos, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spCantidadPreguntas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spDuracionExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bTerminarConfigGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pSeleccionPreguntas.setBackground(new java.awt.Color(178, 34, 34));
        pSeleccionPreguntas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Selección de Preguntas", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lPreguntasSeleccionadas.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lPreguntasSeleccionadas.setForeground(new java.awt.Color(255, 255, 255));
        lPreguntasSeleccionadas.setText("Preguntas Seleccionadas: 0");
        lPreguntasSeleccionadas.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        areaMostrarEnunciado.setColumns(20);
        areaMostrarEnunciado.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        areaMostrarEnunciado.setForeground(new java.awt.Color(61, 61, 61));
        areaMostrarEnunciado.setRows(5);
        areaMostrarEnunciado.setText("Pregunta n:\n");
        barrasEnunciado.setViewportView(areaMostrarEnunciado);

        cbNumPregunta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una pregunta" }));

        bMostrarEnunciado.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bMostrarEnunciado.setText("Seleccionar Pregunta");

        bAñadirPreguntaExamen.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bAñadirPreguntaExamen.setText("Añadir Pregunta");

        lCategoria.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lCategoria.setForeground(new java.awt.Color(255, 255, 255));
        lCategoria.setText("Categoría:");
        lCategoria.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        cbCategoriaExamen.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una categoría" }));

        bSeleccionarCategoria.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bSeleccionarCategoria.setText("Seleccionar Categoría");

        javax.swing.GroupLayout pSeleccionPreguntasLayout = new javax.swing.GroupLayout(pSeleccionPreguntas);
        pSeleccionPreguntas.setLayout(pSeleccionPreguntasLayout);
        pSeleccionPreguntasLayout.setHorizontalGroup(
            pSeleccionPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSeleccionPreguntasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pSeleccionPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pSeleccionPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(pSeleccionPreguntasLayout.createSequentialGroup()
                            .addComponent(bAñadirPreguntaExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lPreguntasSeleccionadas, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(barrasEnunciado, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pSeleccionPreguntasLayout.createSequentialGroup()
                        .addComponent(cbNumPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bMostrarEnunciado, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pSeleccionPreguntasLayout.createSequentialGroup()
                        .addComponent(lCategoria)
                        .addGap(18, 18, 18)
                        .addComponent(cbCategoriaExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bSeleccionarCategoria)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pSeleccionPreguntasLayout.setVerticalGroup(
            pSeleccionPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSeleccionPreguntasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pSeleccionPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lCategoria)
                    .addComponent(cbCategoriaExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bSeleccionarCategoria))
                .addGap(18, 18, 18)
                .addGroup(pSeleccionPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbNumPregunta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bMostrarEnunciado, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(barrasEnunciado, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(pSeleccionPreguntasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bAñadirPreguntaExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lPreguntasSeleccionadas, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        bGuardarConfiguracion.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        bGuardarConfiguracion.setText("Guardar Configuración");

        javax.swing.GroupLayout pConfigurarExamenLayout = new javax.swing.GroupLayout(pConfigurarExamen);
        pConfigurarExamen.setLayout(pConfigurarExamenLayout);
        pConfigurarExamenLayout.setHorizontalGroup(
            pConfigurarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pConfigurarExamenLayout.createSequentialGroup()
                .addGroup(pConfigurarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pConfigurarExamenLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(pConfigurarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pTituloExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pConfigurarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(pSeleccionPreguntas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pConfiguracionGeneral, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(pConfigurarExamenLayout.createSequentialGroup()
                        .addGap(241, 241, 241)
                        .addComponent(bGuardarConfiguracion)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        pConfigurarExamenLayout.setVerticalGroup(
            pConfigurarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pConfigurarExamenLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pTituloExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pConfiguracionGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pSeleccionPreguntas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(bGuardarConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        pestañas.addTab("Configurar Examen", pConfigurarExamen);

        pSeleccionarConfiguracion.setBackground(new java.awt.Color(178, 34, 34));

        pTituloConfig.setBackground(new java.awt.Color(178, 34, 34));
        pTituloConfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        logoUV5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UniversidaddelValleCali.gif"))); // NOI18N

        lExamen5.setFont(new java.awt.Font("SansSerif", 3, 26)); // NOI18N
        lExamen5.setForeground(new java.awt.Color(255, 255, 255));
        lExamen5.setText("Examen Colaborativo Univalle");

        lConfigExamen1.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        lConfigExamen1.setForeground(new java.awt.Color(255, 255, 255));
        lConfigExamen1.setText("Seleccionar Configuración");

        javax.swing.GroupLayout pTituloConfigLayout = new javax.swing.GroupLayout(pTituloConfig);
        pTituloConfig.setLayout(pTituloConfigLayout);
        pTituloConfigLayout.setHorizontalGroup(
            pTituloConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTituloConfigLayout.createSequentialGroup()
                .addGroup(pTituloConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lExamen5)
                    .addComponent(lConfigExamen1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(logoUV5)
                .addGap(63, 63, 63))
        );
        pTituloConfigLayout.setVerticalGroup(
            pTituloConfigLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoUV5, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .addGroup(pTituloConfigLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lExamen5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lConfigExamen1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pEscogerConfiguracion.setBackground(new java.awt.Color(178, 34, 34));
        pEscogerConfiguracion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configuración del Examen", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        cbConfiguracionesGuardadas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione un examen" }));

        bSeleccionarConfiguracion.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bSeleccionarConfiguracion.setText("Seleccionar Configuración");

        javax.swing.GroupLayout pEscogerConfiguracionLayout = new javax.swing.GroupLayout(pEscogerConfiguracion);
        pEscogerConfiguracion.setLayout(pEscogerConfiguracionLayout);
        pEscogerConfiguracionLayout.setHorizontalGroup(
            pEscogerConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pEscogerConfiguracionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbConfiguracionesGuardadas, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(bSeleccionarConfiguracion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pEscogerConfiguracionLayout.setVerticalGroup(
            pEscogerConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pEscogerConfiguracionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pEscogerConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bSeleccionarConfiguracion)
                    .addComponent(cbConfiguracionesGuardadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pVisualizarConfiguracion.setBackground(new java.awt.Color(178, 34, 34));
        pVisualizarConfiguracion.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Visualización de Configuración", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        areaPreguntaConfigurada.setColumns(20);
        areaPreguntaConfigurada.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        areaPreguntaConfigurada.setForeground(new java.awt.Color(61, 61, 61));
        areaPreguntaConfigurada.setRows(5);
        areaPreguntaConfigurada.setText("Pregunta n:\n");
        barrasEnunciado1.setViewportView(areaPreguntaConfigurada);

        cbPreguntasConfiguradas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una pregunta" }));

        bMostrarPreguntaSeleccionada.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bMostrarPreguntaSeleccionada.setText("Ver Pregunta");

        lDuracionVerConfig.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lDuracionVerConfig.setForeground(new java.awt.Color(255, 255, 255));
        lDuracionVerConfig.setText("Duración del examen:");
        lDuracionVerConfig.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lNumPregunasVerCongif.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lNumPregunasVerCongif.setForeground(new java.awt.Color(255, 255, 255));
        lNumPregunasVerCongif.setText("Cantidad de Preguntas:");
        lNumPregunasVerCongif.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout pVisualizarConfiguracionLayout = new javax.swing.GroupLayout(pVisualizarConfiguracion);
        pVisualizarConfiguracion.setLayout(pVisualizarConfiguracionLayout);
        pVisualizarConfiguracionLayout.setHorizontalGroup(
            pVisualizarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pVisualizarConfiguracionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pVisualizarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(barrasEnunciado1, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pVisualizarConfiguracionLayout.createSequentialGroup()
                        .addGroup(pVisualizarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbPreguntasConfiguradas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lNumPregunasVerCongif))
                        .addGap(34, 34, 34)
                        .addGroup(pVisualizarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lDuracionVerConfig)
                            .addComponent(bMostrarPreguntaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(73, Short.MAX_VALUE))
        );
        pVisualizarConfiguracionLayout.setVerticalGroup(
            pVisualizarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pVisualizarConfiguracionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pVisualizarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lDuracionVerConfig, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lNumPregunasVerCongif, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pVisualizarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bMostrarPreguntaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbPreguntasConfiguradas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(barrasEnunciado1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        bDesarrollarExamen.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        bDesarrollarExamen.setText("Desarrollar Examen");

        bCambiarConfiguracion.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        bCambiarConfiguracion.setText("Cambiar Configuración");

        javax.swing.GroupLayout pSeleccionarConfiguracionLayout = new javax.swing.GroupLayout(pSeleccionarConfiguracion);
        pSeleccionarConfiguracion.setLayout(pSeleccionarConfiguracionLayout);
        pSeleccionarConfiguracionLayout.setHorizontalGroup(
            pSeleccionarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSeleccionarConfiguracionLayout.createSequentialGroup()
                .addGroup(pSeleccionarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pSeleccionarConfiguracionLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(pSeleccionarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pTituloConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pSeleccionarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(pVisualizarConfiguracion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pEscogerConfiguracion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(pSeleccionarConfiguracionLayout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(bCambiarConfiguracion)
                        .addGap(67, 67, 67)
                        .addComponent(bDesarrollarExamen)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        pSeleccionarConfiguracionLayout.setVerticalGroup(
            pSeleccionarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pSeleccionarConfiguracionLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pTituloConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pEscogerConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pVisualizarConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pSeleccionarConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bCambiarConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bDesarrollarExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(134, Short.MAX_VALUE))
        );

        pestañas.addTab("Configuración", pSeleccionarConfiguracion);

        pIniciarExamen.setBackground(new java.awt.Color(178, 34, 34));

        pTituloRealizarExamen.setBackground(new java.awt.Color(178, 34, 34));
        pTituloRealizarExamen.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        logoUV3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UniversidaddelValleCali.gif"))); // NOI18N

        lExamen3.setFont(new java.awt.Font("SansSerif", 3, 26)); // NOI18N
        lExamen3.setForeground(new java.awt.Color(255, 255, 255));
        lExamen3.setText("Examen Colaborativo Univalle");

        lInicioExamen.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        lInicioExamen.setForeground(new java.awt.Color(255, 255, 255));
        lInicioExamen.setText("Inicio del Examen");

        javax.swing.GroupLayout pTituloRealizarExamenLayout = new javax.swing.GroupLayout(pTituloRealizarExamen);
        pTituloRealizarExamen.setLayout(pTituloRealizarExamenLayout);
        pTituloRealizarExamenLayout.setHorizontalGroup(
            pTituloRealizarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTituloRealizarExamenLayout.createSequentialGroup()
                .addGroup(pTituloRealizarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lInicioExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lExamen3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addComponent(logoUV3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        pTituloRealizarExamenLayout.setVerticalGroup(
            pTituloRealizarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoUV3, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .addGroup(pTituloRealizarExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lExamen3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lInicioExamen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pInformacionExamen.setBackground(new java.awt.Color(178, 34, 34));
        pInformacionExamen.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Configuración del examen\n\n\n", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lCantidadSeccionada.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lCantidadSeccionada.setForeground(new java.awt.Color(255, 255, 255));
        lCantidadSeccionada.setText("Cantidad de Preguntas:");
        lCantidadSeccionada.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lDuracionSeleccionada.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lDuracionSeleccionada.setForeground(new java.awt.Color(255, 255, 255));
        lDuracionSeleccionada.setText("Duración del examen:");
        lDuracionSeleccionada.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout pInformacionExamenLayout = new javax.swing.GroupLayout(pInformacionExamen);
        pInformacionExamen.setLayout(pInformacionExamenLayout);
        pInformacionExamenLayout.setHorizontalGroup(
            pInformacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pInformacionExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pInformacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lCantidadSeccionada)
                    .addComponent(lDuracionSeleccionada))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pInformacionExamenLayout.setVerticalGroup(
            pInformacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pInformacionExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lCantidadSeccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lDuracionSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pRealizacionExamen.setBackground(new java.awt.Color(178, 34, 34));
        pRealizacionExamen.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Realización del examen", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        lUsuarios.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lUsuarios.setForeground(new java.awt.Color(255, 255, 255));
        lUsuarios.setText("Usuarios conectados:");
        lUsuarios.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lUsuario1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lUsuario1.setForeground(new java.awt.Color(255, 255, 255));
        lUsuario1.setText("Usuario 1: ");
        lUsuario1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lUsuario2.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lUsuario2.setForeground(new java.awt.Color(255, 255, 255));
        lUsuario2.setText("Usuario 2: ");
        lUsuario2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lUsuario3.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lUsuario3.setForeground(new java.awt.Color(255, 255, 255));
        lUsuario3.setText("Usuario 3: ");
        lUsuario3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lTiempoRestante.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lTiempoRestante.setForeground(new java.awt.Color(255, 255, 255));
        lTiempoRestante.setText("Tiempo Restante: ");
        lTiempoRestante.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lEstadoExamen.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lEstadoExamen.setForeground(new java.awt.Color(255, 255, 255));
        lEstadoExamen.setText("Estado del Examen: No disponible");
        lEstadoExamen.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        bIniciarExamen.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bIniciarExamen.setText("Iniciar Examen");

        lNombreUsuario1.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lNombreUsuario1.setForeground(new java.awt.Color(255, 255, 255));
        lNombreUsuario1.setText("Desconectado");
        lNombreUsuario1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lNombreUsuario2.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lNombreUsuario2.setForeground(new java.awt.Color(255, 255, 255));
        lNombreUsuario2.setText("Desconectado");
        lNombreUsuario2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lNombreUsuario3.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lNombreUsuario3.setForeground(new java.awt.Color(255, 255, 255));
        lNombreUsuario3.setText("Desconectado");
        lNombreUsuario3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lTiempo.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        lTiempo.setForeground(new java.awt.Color(255, 255, 255));
        lTiempo.setText("00:00");
        lTiempo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout pRealizacionExamenLayout = new javax.swing.GroupLayout(pRealizacionExamen);
        pRealizacionExamen.setLayout(pRealizacionExamenLayout);
        pRealizacionExamenLayout.setHorizontalGroup(
            pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                        .addComponent(lUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(92, 92, 92)
                        .addComponent(lTiempoRestante, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lTiempo)
                        .addContainerGap(160, Short.MAX_VALUE))
                    .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                        .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lUsuario3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lUsuario2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lUsuario1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                                .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lNombreUsuario1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lNombreUsuario2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                                .addComponent(lNombreUsuario3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
            .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lEstadoExamen))
                    .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                        .addGap(213, 213, 213)
                        .addComponent(bIniciarExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pRealizacionExamenLayout.setVerticalGroup(
            pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pRealizacionExamenLayout.createSequentialGroup()
                .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lTiempoRestante, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lTiempo))
                .addGap(1, 1, 1)
                .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lUsuario1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lNombreUsuario1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lUsuario2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lNombreUsuario2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRealizacionExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lUsuario3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lNombreUsuario3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lEstadoExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addComponent(bIniciarExamen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );

        javax.swing.GroupLayout pIniciarExamenLayout = new javax.swing.GroupLayout(pIniciarExamen);
        pIniciarExamen.setLayout(pIniciarExamenLayout);
        pIniciarExamenLayout.setHorizontalGroup(
            pIniciarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIniciarExamenLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(pIniciarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pTituloRealizarExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pIniciarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pRealizacionExamen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pInformacionExamen, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        pIniciarExamenLayout.setVerticalGroup(
            pIniciarExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pIniciarExamenLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pTituloRealizarExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pInformacionExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pRealizacionExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(123, Short.MAX_VALUE))
        );

        pestañas.addTab("Desarrollo del Examen", pIniciarExamen);

        pResultados.setBackground(new java.awt.Color(178, 34, 34));

        pTituloResultados.setBackground(new java.awt.Color(178, 34, 34));
        pTituloResultados.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        logoUV4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UniversidaddelValleCali.gif"))); // NOI18N

        lExamen4.setFont(new java.awt.Font("SansSerif", 3, 26)); // NOI18N
        lExamen4.setForeground(new java.awt.Color(255, 255, 255));
        lExamen4.setText("Examen Colaborativo Univalle");

        lResultados.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        lResultados.setForeground(new java.awt.Color(255, 255, 255));
        lResultados.setText("Resultados del examen");

        javax.swing.GroupLayout pTituloResultadosLayout = new javax.swing.GroupLayout(pTituloResultados);
        pTituloResultados.setLayout(pTituloResultadosLayout);
        pTituloResultadosLayout.setHorizontalGroup(
            pTituloResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTituloResultadosLayout.createSequentialGroup()
                .addGroup(pTituloResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lExamen4, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(logoUV4, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pTituloResultadosLayout.setVerticalGroup(
            pTituloResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logoUV4, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
            .addGroup(pTituloResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lExamen4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lResultados)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pResultadosExamen.setBackground(new java.awt.Color(178, 34, 34));
        pResultadosExamen.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Resultados por pregunta", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 2, 14), new java.awt.Color(255, 255, 255))); // NOI18N

        areaEnunciadoRespondida.setColumns(20);
        areaEnunciadoRespondida.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        areaEnunciadoRespondida.setForeground(new java.awt.Color(61, 61, 61));
        areaEnunciadoRespondida.setRows(5);
        areaEnunciadoRespondida.setText("Pregunta n:\n");
        barrasEnunciadoP.setViewportView(areaEnunciadoRespondida);

        cbPreguntaRespondida.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione una pregunta" }));

        bMostrarEnunciadoRespondida.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        bMostrarEnunciadoRespondida.setText("Mostrar Enunciado y respuestas");

        lRespuestaDada.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lRespuestaDada.setForeground(new java.awt.Color(255, 255, 255));
        lRespuestaDada.setText("Respuesta dada por el grupo: ");
        lRespuestaDada.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lRespuestaCorrecta.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lRespuestaCorrecta.setForeground(new java.awt.Color(255, 255, 255));
        lRespuestaCorrecta.setText("Respuesta Correcta:");
        lRespuestaCorrecta.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lCalificacionGrupo.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        lCalificacionGrupo.setForeground(new java.awt.Color(255, 255, 255));
        lCalificacionGrupo.setText("Calificación del grupo: ");
        lCalificacionGrupo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout pResultadosExamenLayout = new javax.swing.GroupLayout(pResultadosExamen);
        pResultadosExamen.setLayout(pResultadosExamenLayout);
        pResultadosExamenLayout.setHorizontalGroup(
            pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadosExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(barrasEnunciadoP)
                    .addGroup(pResultadosExamenLayout.createSequentialGroup()
                        .addComponent(cbPreguntaRespondida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bMostrarEnunciadoRespondida))
                    .addComponent(lRespuestaDada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lRespuestaCorrecta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lCalificacionGrupo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(193, Short.MAX_VALUE))
        );
        pResultadosExamenLayout.setVerticalGroup(
            pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadosExamenLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pResultadosExamenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbPreguntaRespondida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bMostrarEnunciadoRespondida, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(barrasEnunciadoP, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lRespuestaDada, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lRespuestaCorrecta, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lCalificacionGrupo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        bTerminarVerResultados.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        bTerminarVerResultados.setText("Terminar");

        javax.swing.GroupLayout pResultadosLayout = new javax.swing.GroupLayout(pResultados);
        pResultados.setLayout(pResultadosLayout);
        pResultadosLayout.setHorizontalGroup(
            pResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadosLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(pResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pResultadosLayout.createSequentialGroup()
                        .addComponent(pTituloResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(114, Short.MAX_VALUE))
                    .addGroup(pResultadosLayout.createSequentialGroup()
                        .addComponent(pResultadosExamen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(pResultadosLayout.createSequentialGroup()
                .addGap(289, 289, 289)
                .addComponent(bTerminarVerResultados)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pResultadosLayout.setVerticalGroup(
            pResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pResultadosLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pTituloResultados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pResultadosExamen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(bTerminarVerResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(157, Short.MAX_VALUE))
        );

        pestañas.addTab("Resultados", pResultados);

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pestañas, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pestañas)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        //</editor-fold>
        
        Interfaz interfazServidor = new Interfaz();
        interfazServidor.setVisible(true);
        interfazServidor.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaEnunciado;
    private javax.swing.JTextArea areaEnunciadoRespondida;
    private javax.swing.JTextArea areaMostrarEnunciado;
    private javax.swing.JTextArea areaPreguntaConfigurada;
    private javax.swing.JButton bAsignarNombreC;
    private javax.swing.JButton bAñadirPregunta;
    private javax.swing.JButton bAñadirPreguntaExamen;
    private javax.swing.JButton bCambiarConfiguracion;
    private javax.swing.JButton bDesarrollarExamen;
    private javax.swing.JButton bFinalizarIngreso;
    private javax.swing.JButton bGuardarConfiguracion;
    private javax.swing.JButton bIniciarExamen;
    private javax.swing.JButton bMostrarEnunciado;
    private javax.swing.JButton bMostrarEnunciadoRespondida;
    private javax.swing.JButton bMostrarPreguntaSeleccionada;
    private javax.swing.JButton bSeleccionarCategoria;
    private javax.swing.JButton bSeleccionarConfiguracion;
    private javax.swing.JButton bTerminarConfigGeneral;
    private javax.swing.JButton bTerminarVerResultados;
    private javax.swing.JScrollPane barras;
    private javax.swing.JScrollPane barrasEnunciado;
    private javax.swing.JScrollPane barrasEnunciado1;
    private javax.swing.JScrollPane barrasEnunciadoP;
    private javax.swing.JComboBox cbCategoriaExamen;
    private javax.swing.JComboBox cbConfiguracionesGuardadas;
    private javax.swing.JComboBox cbNumPregunta;
    private javax.swing.JComboBox cbOpcionCorrecta;
    private javax.swing.JComboBox cbPreguntaRespondida;
    private javax.swing.JComboBox cbPreguntasConfiguradas;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JLabel lCalificacionGrupo;
    private javax.swing.JLabel lCantidadSeccionada;
    private javax.swing.JLabel lCategoria;
    private javax.swing.JLabel lConfigExamen;
    private javax.swing.JLabel lConfigExamen1;
    private javax.swing.JLabel lDuracionExamen;
    private javax.swing.JLabel lDuracionSeleccionada;
    private javax.swing.JLabel lDuracionVerConfig;
    private javax.swing.JLabel lEnunciado;
    private javax.swing.JLabel lEstadoExamen;
    private javax.swing.JLabel lExamen1;
    private javax.swing.JLabel lExamen2;
    private javax.swing.JLabel lExamen3;
    private javax.swing.JLabel lExamen4;
    private javax.swing.JLabel lExamen5;
    private javax.swing.JLabel lIngresoCategoria;
    private javax.swing.JLabel lInicioExamen;
    private javax.swing.JLabel lMinutos;
    private javax.swing.JLabel lNombre;
    private javax.swing.JLabel lNombreUsuario1;
    private javax.swing.JLabel lNombreUsuario2;
    private javax.swing.JLabel lNombreUsuario3;
    private javax.swing.JLabel lNumPregunasVerCongif;
    private javax.swing.JLabel lNumPreguntas;
    private javax.swing.JLabel lOpcion1;
    private javax.swing.JLabel lOpcion2;
    private javax.swing.JLabel lOpcion3;
    private javax.swing.JLabel lOpcion4;
    private javax.swing.JLabel lOpcionCorrecta;
    private javax.swing.JLabel lPreguntasSeleccionadas;
    private javax.swing.JLabel lRespuestaCorrecta;
    private javax.swing.JLabel lRespuestaDada;
    private javax.swing.JLabel lResultados;
    private javax.swing.JLabel lTiempo;
    private javax.swing.JLabel lTiempoRestante;
    private javax.swing.JLabel lUsuario1;
    private javax.swing.JLabel lUsuario2;
    private javax.swing.JLabel lUsuario3;
    private javax.swing.JLabel lUsuarios;
    private javax.swing.JLabel logoUV1;
    private javax.swing.JLabel logoUV2;
    private javax.swing.JLabel logoUV3;
    private javax.swing.JLabel logoUV4;
    private javax.swing.JLabel logoUV5;
    private javax.swing.JPanel pConfiguracionGeneral;
    private javax.swing.JPanel pConfigurarExamen;
    private javax.swing.JPanel pEscogerConfiguracion;
    private javax.swing.JPanel pInformacionExamen;
    private javax.swing.JPanel pIngresarCategoria;
    private javax.swing.JPanel pIngresoPreguntas;
    private javax.swing.JPanel pIniciarExamen;
    private javax.swing.JPanel pNombreCategoria;
    private javax.swing.JPanel pRealizacionExamen;
    private javax.swing.JPanel pResultados;
    private javax.swing.JPanel pResultadosExamen;
    private javax.swing.JPanel pSeleccionPreguntas;
    private javax.swing.JPanel pSeleccionarConfiguracion;
    private javax.swing.JPanel pTituloCategoria;
    private javax.swing.JPanel pTituloConfig;
    private javax.swing.JPanel pTituloExamen;
    private javax.swing.JPanel pTituloRealizarExamen;
    private javax.swing.JPanel pTituloResultados;
    private javax.swing.JPanel pVisualizarConfiguracion;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JTabbedPane pestañas;
    private javax.swing.JSpinner spCantidadPreguntas;
    private javax.swing.JSpinner spDuracionExamen;
    private javax.swing.JTextField tfNombreCategoria;
    private javax.swing.JTextField tfOpcion1;
    private javax.swing.JTextField tfOpcion2;
    private javax.swing.JTextField tfOpcion3;
    private javax.swing.JTextField tfOpcion4;
    // End of variables declaration//GEN-END:variables

   

    
}
