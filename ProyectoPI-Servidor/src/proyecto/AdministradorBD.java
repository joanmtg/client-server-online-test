/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: AdministradorBD.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : AdministradorBD 
* Responsabilidad : Esta clase se encarga de conectarse a una base de datos para:
* crear tablas, hacer consultas, guardar y modificar datos en dicha base de datos.
*  
* 
*/

package proyecto;

//Se importan las librerías a utilizarse

import java.sql.*;
import java.util.*;



public class AdministradorBD {
    
    //Atributos de la clase:
    
    //Objeto de la clase FachadaBD utilizado para realizar conexiones
    //con la base de datos
    private FachadaBD fachada;
    
    //Variables de tipo String para guardar información de los campos
    //de ciertas tablas a crearse
    private String camposCategoria, camposExamen, camposPregunta, camposPreguntasExamen;        
    
    public AdministradorBD(){
        
        //Se inicializa el objeto de la clase FachadaBD
        fachada = new FachadaBD();
        
        //Campos de la tabla "categorias" a crearse en la base de datos
        
        camposCategoria = "nombreCategoria varchar(100) NOT NULL PRIMARY KEY";
        
        //Campos de la tabla "examenes" a crearse en la base de datos
        
        camposExamen = "numeroExamen smallint NOT NULL PRIMARY KEY,"
                     +   "duracion int, cantidadPreguntas int";
        
        //Campos de la tabla "preguntas" a crearse en la base de datos
        
        camposPregunta =  "enunciado varchar(255)"
               + ", opcion1 varchar(150), opcion2 varchar(150), opcion3 varchar(150)"
               + ", opcion4 varchar(150), numeroPregunta int NOT NULL PRIMARY KEY, nombreCategoria varchar(100)"
               + ", respuestaCorrecta int,"
               + "CONSTRAINT pregunta_categoria FOREIGN KEY (nombreCategoria) REFERENCES categorias(nombreCategoria)"; 
        
        
        //Campos de la tabla "preguntasseleccionadas" a crearse en la base de datos
        
        camposPreguntasExamen = "numeroPreguntaExamen int NOT NULL, numeroPregunta int NOT NULL, numeroExamen int NOT NULL,"
                + "estadoPregunta varchar(20) NOT NULL, respuestaCorrecta int, respuestaDada int, nombreUsuario varchar, calificacion int, "  
               + "CONSTRAINT preguntaS_numPregunta FOREIGN KEY (numeroPregunta) REFERENCES preguntas(numeroPregunta),"
               + "CONSTRAINT preguntaS_numExamen FOREIGN KEY (numeroExamen) REFERENCES examenes(numeroExamen)"; 
                        
        
        /**
         * Se llama el método crearTabla de la clase 4 veces,
         * una por cada tabla a crearse, y cada una con un
         * identificador diferente 1,2,3 ó 4.
         */ 
        crearTabla("categorias", camposCategoria, 1);
        crearTabla("examenes", camposExamen, 2);
        crearTabla("preguntas", camposPregunta, 3);
        crearTabla("preguntasseleccionadas", camposPreguntasExamen, 4);
        
        //obtenerCategorias();
    }
    
    
    /**
     * Método que recibe el nombre de una categoría y guarda un registro
     * en la base de datos, en la tabla categorías, con dicho nombre recibido.
     * @param nombreCategoria  String que representa el nombre de la categoría
     * @return Se retorna el número de filas
     */
    
    public int guardarCategoria(String nombreCategoria){
        
        //Se declara la variable de tipo String para almacenar la sentencia SQL
        //que va a enviarse a la base de datos para almacenar una nueva categoría
        String sql_registrar;
        
        //Se define la sentencia SQL debidamente para ingresar una nueva categoría,
        //cuyo nombre va a ser el ingresado como parámetro
        sql_registrar = "INSERT INTO categorias(nombreCategoria) VALUES ('" + nombreCategoria + "');";
        
        try
        {
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización en la base de datos y se guarda
            //el número de filas en una variable de tipo entero
            int numFilas = sentencia.executeUpdate(sql_registrar);
            //Se cierra la conexión
            conexion.close();
            
            //Se retorna el valor de la variable numFilas
            return numFilas;
        }
        catch(SQLException e)
        {
            //Manejo de expeciones SQL
            System.out.println(e);
        }
        catch(Exception e)
        {
            //Menejo de excepciones generales
            System.out.println(e);
        }
        
        //En caso de que no se hayan realizado las sentencias que hay dentro
        //del bloque try, se retorna el entero -1
        return -1;
    }
    
    /**
     * Método que retorna un ArrayList de Strings con los nombres
     * de las categorías existentes en la base de datos
     * @return 
     */
    
    public ArrayList<String> obtenerCategorias(){
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_consulta  = "SELECT * from categorias;";
        
        //Se declara e inicializa un ArrayList de Strings
        ArrayList<String> categoriasTemp = new ArrayList<>();
        
        //Se delcara un objeto de tipo ResultSet
        ResultSet informacion;
                
        try
        {
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable "informacion"
            informacion = sentencia.executeQuery(sql_consulta);
            
            //Mientras hayan registros:
            while(informacion.next()){
                
                //Se añade al Arraylist de Strings la información del registro
                categoriasTemp.add(informacion.getString(1));
            }
        }
        catch(SQLException ex){
            System.out.println(ex);
        }
        
        //Se retorna elvalor de la variable categoriasTemp
        
        return categoriasTemp;
    }
    
    
    /**
     * Método que recibe un objeto de tipo ResultSet, extrae su información,
     * la guarda en un objeto de tipo Pregunta y lo retorna como tal.
     * @param informacion
     * @return 
     */
    
    public Pregunta convertirRegistroPregunta(ResultSet informacion){
        
        //Se declara un objeto de tipo Pregunta y se le asigna el valor de null
        Pregunta nuevaPregunta = null;
        
        //Se declaran las variables para guardar la información d ela pregunta
        String enunciado, opcion1, opcion2, opcion3, opcion4, nombrecategoria;
        int numeroPregunta, opcionCorrecta;
        String[] opciones = new String[4];
        
        try {
            
            //Se extraen los datos del registro de la base de datos en orden 
            //y se guarda en las variables creadas anteriormente
            
            enunciado = informacion.getString(1); //Enunciado
            
            //Opciones de la pregunta
            opcion1 = informacion.getString(2);   
            opcion2 = informacion.getString(3);
            opcion3 = informacion.getString(4);
            opcion4 = informacion.getString(5);
            
            numeroPregunta = informacion.getInt(6); //Número de la pregunta (único)
            nombrecategoria = informacion.getString(7); //Categoría de la pregunta
            opcionCorrecta = informacion.getInt(8);  //Opción correta de la pregunta

            //Se guardan las opciones en un arreglo de tamaño 4
            opciones[0] = opcion1;
            opciones[1] = opcion2;
            opciones[2] = opcion3;
            opciones[3] = opcion4;

            
            //Se crea una nueva pregunta pasándole como parámetro los datos definidos anteriormente
            nuevaPregunta = new Pregunta(enunciado, opciones, opcionCorrecta, nombrecategoria, numeroPregunta);
        }
        catch(SQLException ex){
            
            System.out.println(ex);
        }
        
        //Se retorna el valor de la variable nuevaPregunta
        
        return nuevaPregunta;
    }
    
    
    /**
     * Método que recibe el nombre de una categoría, y retorn un ArrayList
     * de las preguntas existentes en la base de datos que pertenezacan
     * a la categoría que se recibe como parámetro
     * @param nombreCategoria
     * @return 
     */
    
    public ArrayList<Pregunta> obtenerPreguntasPorCategoria(String nombreCategoria){
        
        //Se declaran variables a utilizar
        Pregunta nuevaPregunta = null; //Para guardar una pregunta
        ArrayList<Pregunta> preguntasTemp = new ArrayList<>(); //Para añadir las preguntas y luego retornarlas todas
        
        //Se declara un objeto de tipo ResultSet
        ResultSet informacion;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_consulta = "SELECT * from preguntas where nombrecategoria = '" +nombreCategoria+ "';";
        
        try
        {
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable "informacion"
            informacion = sentencia.executeQuery(sql_consulta);
            
            while(informacion.next())
            {
                //Se convierte el registro con el método convertirRegistroPregunta
                //y se guarda en una variable de tipo Pregunta
                nuevaPregunta = convertirRegistroPregunta(informacion);
                
                //Se añada al ArrayList que se va a retornar lo convertido anteriormente
                preguntasTemp.add(nuevaPregunta);
            }
            
            //Se cierra la conexión
            conexion.close();
        }
        catch(SQLException ex)
        {
            System.out.println(ex);
        }
        
        //Se retorna el contenido del ArrayList de preguntas
        
        return preguntasTemp;
    }
    
    /**
     * Método que recibe como parámetro el número de un examen, el número de
     * la pregunta dentro del examen y retorna un objeto de tipo Pregunta
     * con la información del registro requerido
     * @param numeroExamen
     * @param numeroPreguntaExamen
     * @return 
     */
    
    
    public Pregunta obtenerPreguntaExamen(int numeroExamen, int numeroPreguntaExamen){
        
        //Se declaran variables a utilizar
        Pregunta pregunta = null;
        int numeroPregunta = 0;
        
        //Se declara un objeto de tipo ResultSet
        ResultSet informacion;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_consulta = "SELECT numeropregunta from preguntasseleccionadas where numeroexamen = '" +numeroExamen+ "'"
                     + "and numeropreguntaexamen = '"+numeroPreguntaExamen+"';";
        
        try
        {
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable "informacion"
            informacion = sentencia.executeQuery(sql_consulta);
            
            while(informacion.next()){   
                
                //Se obtiene el número único de la pregunta
                numeroPregunta = informacion.getInt(1);
            }
            
            //Se cierra la conexión
            conexion.close();
            
            //Se obtiene la pregunta por el número único de pregunta y se almacena en la variable pregunta
            pregunta = obtenerPreguntaPorNumero(numeroPregunta);            
        }
        catch(SQLException ex)
        {
            System.out.println(ex);
        }
        
        //Se retorna la pregunta obtenida
        
        return pregunta;
    }
    
    public Pregunta obtenerPreguntaPorNumero(int numeroPregunta)
    {
        Pregunta pregunta = null; 
          
        //Se declara un objeto de tipo ResultSet
        ResultSet informacion;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_consulta = "SELECT * from preguntas where numeropregunta = '" +numeroPregunta+ "' ;";
                     
        
        try
        {
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable "informacion"
            informacion = sentencia.executeQuery(sql_consulta);
            
            while(informacion.next()){
                //Se guarda en la variable pregunta el resultado de la consulta convertido
                pregunta = convertirRegistroPregunta(informacion);
            }
            
            //Se cierra la conexión
            conexion.close();
        }
        catch(SQLException ex)
        {
            System.out.println(ex);
        }
        
        //Se retorna la pregunta obtenida
        
        return pregunta;
    }
    
    /**
     * Método que recibe como parámetro un String que va a representar los campos
     * de una tabla SQL y también recibe un identificador de tipo entero.
     * Lo que hace con esta información es concatenarla con un bloque de
     * sentencias del lenguaje plsql y lo retorna como un String
     * @param sentenciaTabla
     * @param id
     * @return 
     */
    
    public String funcionTablas(String sentenciaTabla, int id){
        
       String sql_funcion;
       sql_funcion = "CREATE OR REPLACE FUNCTION crear_tabla"+id+"(TEXT) RETURNS VOID AS $$" +
              " DECLARE" +
              " nombre_tabla ALIAS FOR $1;" +
              " BEGIN" +
              " IF NOT EXISTS (SELECT * FROM information_schema.tables WHERE table_name = nombre_tabla) THEN" +
              " execute 'CREATE TABLE ' || nombre_tabla || '("+sentenciaTabla+")';" +
              " END IF;" +
              " END;" +
              " $$ LANGUAGE plpgsql;";      
       
       return sql_funcion;
    }
    
    /**
     * Método que recibe un objeto de tipo Pregunta como parámetro y guarda
     * un registro en la tabla "preguntas" de la base de datos con la información
     * de la pregunta ya mencionada
     * @param nuevaPregunta
     * @return Retorna un entero que representa el número de filas
     */
    
    public int guardarPregunta(Pregunta nuevaPregunta)
    {
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_registrar = "INSERT INTO preguntas(enunciado, opcion1, opcion2, opcion3, opcion4"
                +       ", numeroPregunta, nombreCategoria, respuestaCorrecta) VALUES ('"+nuevaPregunta.getEnunciado()+"',"
                +       "'"+nuevaPregunta.getRespuestas()[0]+"','"+nuevaPregunta.getRespuestas()[1]+"',"
                +       "'"+nuevaPregunta.getRespuestas()[2]+"','"+nuevaPregunta.getRespuestas()[3]+"',"
                +       "'"+nuevaPregunta.getNumeroPregunta()+"','"+nuevaPregunta.getCategoria()+"',"
                +       "'"+nuevaPregunta.getRespuestaCorrecta()+"');";
        
        try
        {
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización en la base de datos y se guarda
            //el número de filas en una variable de tipo entero
            int numFilas = sentencia.executeUpdate(sql_registrar);
            //Se cierra la conexión
            conexion.close();
            
            //Se retorna el valor de la variable numFilas
            return numFilas;
        }
        catch(SQLException e){
            System.out.println(e);
        }
        catch(Exception e){
            System.out.println(e);
        }
        
        //En caso de que no se completen las sentencias del bloque try, se retorna -1
        return -1;
    }
    
    /**
     * Método que recibe como parametro dos Strings. Uno de ellos va a
     * representar el nombre de la tabla a crear. El otro va a representar los
     * campos de dicha tabla. Recibe también un entero id para que no crear
     * funciones con distintos nombres, es decir, que no se repitan
     * @param nombreTabla
     * @param campos
     * @param id 
     */
    
    public void crearTabla(String nombreTabla, String campos, int id){
        
        //Se declaran dos variables de tipo String a utilizar
        String sql_crear, tabla_funcion;
        
        //Se llama al método funcionTablas, pasándole como parámetro el String
        //que contiene los campos de la tabla, y pasándole como parámetro el
        //identificador que va a ser único para cada tabla. 
        //Se guarda el resultado de dicho método en la variable tabla_funcion
        tabla_funcion= funcionTablas(campos, id);
        
        //Se asigna a la variable sql_crear una sentencia SQL que permite
        //seleccionar o llamar a la función plsql creada anteriormente. A dicha
        //función plsql se le pasa como parámetro el nombre de la tabla a crear
                
        sql_crear = "SELECT crear_tabla" + id +"('" + nombreTabla +"');";
       
        try
        {
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización que crea la función plsql destinada a crear la tabla
            sentencia.executeUpdate(tabla_funcion);
            //Se ejecuta la sentencia SQL para ejecutar la función creada anteriormente
            ResultSet informacion = sentencia.executeQuery(sql_crear);
            //Se cierra la conexión
            conexion.close();
          
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println(e);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
        }
        
    }
    
    /**
     * Método que recibe un objeto de tipo examen y lo guarda en la base de datos
     * en la tabla examenes con la información del objeto recibido.
     * @param examen
     * @return 
     */    
    
    public int guardarExamen(Examen examen){
        
        int numFilas = 0;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_guardar = "INSERT INTO examenes(numeroexamen, duracion, cantidadpreguntas) VALUES ("
                + examen.getNumeroExamen() + ", " + examen.getTiempoDuracion() + ", " + examen.getCantidadPreguntas() + ");";
        
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización en la base de datos y se guarda
            //el número de filas en una variable de tipo entero
            numFilas = sentencia.executeUpdate(sql_guardar);
            //Se cierra la conexión
            conexion.close();
            
            //Se retorna el valor de la variable numFilas
            return numFilas;
            
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        //En caso de que no se completen las sentencias del bloque try, se retorna -1
        return -1;
        
    }
    
    /**
     * Método que recibe el nombre de una tabla cuenta cuántos registros
     * de dicha tabla hay en la base de datos. Tener en cuenta que normalmente
     * se recibe el nombre de la tabla, pero también se le puede pasar un String
     * con condiciones WHERE. Por ejemplo "nombreTabla WHERE valor = valorRequerido" 
     * @param nombreTabla
     * @return 
     */
    
    public int numeroRegistros(String nombreTabla){
        
        //Se declara e inicaliza la variable que será retornada
        int numeroRegistros = 0;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String consulta ="SELECT COUNT(*) FROM "+ nombreTabla + ";";
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable resultado
            ResultSet resultado = sentencia.executeQuery(consulta);
                        
            while(resultado.next()){
                
                //Se obtiene el conteo y se guarda en la variable numeroRegistros
                numeroRegistros = resultado.getInt(1);
            }
            
            //Se cierra la conexión
            conexion.close();
               
        }catch(SQLException exc){
            System.out.println("Error");
        }
        
        
        //Se retorna el número de registros obtenido
        
        return numeroRegistros;
    }
    
    
    /**
     * Método que recibe un el número de un examne y retorna como entero
     * la duración en minutos del examen que tenga dicho número
     * @param numeroExamen
     * @return 
     */
    
    public int duracionExamen(int numeroExamen){
        
        //Se declara e inicaliza la variable que será retornada
        int duracionExamen = 0;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String consulta = "SELECT duracion FROM examenes WHERE numeroExamen = '" + numeroExamen + "';";
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable resultado
            ResultSet resultado = sentencia.executeQuery(consulta);
            
            while(resultado.next()){
                //Se guarda la duración en la variable duracionExamen
                duracionExamen = resultado.getInt(1);
            }
            
            //Se cierra la conexion
            conexion.close();
            
        }catch (SQLException ex) {       
            ex.printStackTrace();
        }
        
        //Se retorna la duración del examen
        
        return duracionExamen;
    }
    
    
    /**
     * Método que recibe el nombre de una categoría y evalúa si dicha categoría existe o no
     * en la base de datos. Si es válida (no existe en la base de datos) devuelve true
     * y si es inválida (ya existe en la base de datos) devuelve false
     * 
     * @param nombreCategoria
     * @return 
     */
    
    
    public boolean validarCategoria(String nombreCategoria){
        
        //Se declara e inicaliza la variable que será retornada
        boolean resultado = true;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String consulta ="SELECT COUNT(*) FROM categorias WHERE nombreCategoria = '" + nombreCategoria + "' ;";
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable result
            ResultSet result = sentencia.executeQuery(consulta);
            int numeroPreguntas = 0;            
            
            while(result.next()){
                
                //Se obtiene el conteo de registros donde el nombre de la
                //categoría es igual al ingresado
                numeroPreguntas = result.getInt(1);
            }
            
            //Si dicho conteo es mayor a 0, es porque ya está registrada en la base de datos
            
            if(numeroPreguntas > 0){
                
                //Se cambia el valor dela variable reultado a false
                resultado = false;
            }
            
            //Se cierra la conexion
            conexion.close();
               
        }catch(SQLException exc){
            System.out.println("Error");
        }
        
        //Se retorna el valor de la variable resultado
        
        return resultado;
    }
    
    
    /**
     * Método que recibe un objeto de la clase Pregunta, un número de pregunta dentro
     * del examen (int) y un número de examen (int), para luego guardar en la tabla
     * preguntasseleccionadas de la base de datos un registro con la información
     * ingresada como parámetro
     * @param pregunta
     * @param numeroPreguntaExamen
     * @param numeroExamen
     * @return 
     */
    
    public int guardarPreguntasSeleccionadas(Pregunta pregunta,int numeroPreguntaExamen, int numeroExamen){
        
        //Se declara e inicaliza la variable que será retornada
        int numFilas = 0;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_guardar = "INSERT INTO preguntasseleccionadas(numeroPreguntaExamen, numeroPregunta, numeroExamen, estadoPregunta,"
                + "respuestaCorrecta, respuestaDada, nombreUsuario, calificacion) VALUES ("
                + numeroPreguntaExamen + ", " + pregunta.getNumeroPregunta() + ", " + numeroExamen 
                + ",'DISPONIBLE', " + pregunta.getRespuestaCorrecta() + ", null , null , null" + ");";
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización en la base de datos y se guarda
            //el número de filas en una variable de tipo entero
            numFilas = sentencia.executeUpdate(sql_guardar);
            //Se cierra la conexión
            conexion.close();
            
            //Se retorna el valor de la variable numFilas
            return numFilas;
            
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        //En caso de que no se completen las sentencias del bloque try, se retorna -1
        return -1;
        
    }
    
    /**
     * Método que recibe un número de pregunta general como entero y un String
     * que representa un estado de pregunta. Actualiza el campo estadoPregunta
     * del registro de la tabla preguntasseleccionadas de la base de datos donde
     * la pregunta tenga el número ingresado como parámetro y el número de examen
     * sea el mismo que el ingresado como parámetro. El nuevo estado de la
     * pregunta será el recibido como parámetro
     * @param numeroPreguntaGeneral  número único de la pregunta
     * @param estadoPregunta   nuevo estado de la pregunta
     * @param numeroExamen     número del examen
     * @return 
     */
    
    public int modificarEstadoPregunta(int numeroPreguntaGeneral, String estadoPregunta, int numeroExamen){
        
        int numFilas = 0;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_modificar = "UPDATE preguntasseleccionadas SET estadoPregunta = '"+estadoPregunta+"' "
                             + "WHERE numeroPregunta = " +numeroPreguntaGeneral+ " AND numeroExamen = " + numeroExamen +";";
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización en la base de datos y se guarda
            //el número de filas en una variable de tipo entero
            numFilas = sentencia.executeUpdate(sql_modificar);
            //Se cierra la conexión
            conexion.close();
            
            //Se retorna el valor de la variable numFilas
            return numFilas;
        }
        catch(SQLException ex){
           ex.printStackTrace();
        }
        
        //En caso de que no se completen las sentencias del bloque try, se retorna -1
        return -1; 
    }
    
    /**
     * Método que califica una pregunta dentro de la base de datos, cambiando:
     * el estado, la calificación, la respuesta dada y el nombre del usuario que
     * respondió la pregunta. Dichos campos pertenecen a la tabla preguntasseleccionadas
     * 
     * @param numeroPreguntaGeneral  Número de la pregunta (único)
     * @param calificacion  nueva calificación de la pregunta
     * @param respuestaDada respuesta dada por el usuario
     * @param nombreUsuario nombre del usuario que la respondió
     * @param numeroExamen número del examen
     * @return 
     */
    
    
    public int calificarPreguntaBD(int numeroPreguntaGeneral, int calificacion, int respuestaDada, String nombreUsuario, int numeroExamen){
        
        //Se declara la variable a retornarse
        int numFilas;
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_guardar = "UPDATE preguntasseleccionadas SET estadoPregunta = 'RESPONDIDA', respuestaDada = "+respuestaDada+" "
                             + ", nombreUsuario = '"+nombreUsuario+"' , calificacion = "+calificacion+
                             "WHERE numeroPregunta ="+numeroPreguntaGeneral+ " AND numeroExamen = " + numeroExamen + ";";
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización en la base de datos y se guarda
            //el número de filas en una variable de tipo entero
            numFilas = sentencia.executeUpdate(sql_guardar);
            //Se cierra la conexión
            conexion.close();
            
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        //En caso de que no se completen las sentencias del bloque try, se retorna -1
        return -1;
        
    }
    
    /**
     * Método que devuleve los resultados de una pregunta como String, cuya
     * estructura es la siguiente "respuestaDada respuestaCorrecta". Esta información
     * se va a extraer de la tabla preguntasselecionadas de la base de datos
     * @param numeroPreguntaExamen
     * @param numeroExamen
     * @return 
     */
      
    public String resultadosPregunta(int numeroPreguntaExamen,int numeroExamen){
        
        //Se declara e inicaliza la variable que será retornada
        String informacion = "";
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_consulta = "SELECT respuestaDada, respuestaCorrecta FROM preguntasseleccionadas WHERE numeroPreguntaExamen = " + numeroPreguntaExamen  + "AND numeroExamen = "  + numeroExamen + " ;";
        
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable resultado
            ResultSet resultado = sentencia.executeQuery(sql_consulta);
            
            while(resultado.next()){
                
                //Se concatena la información con la estructura ya mencionada
                informacion += resultado.getInt(1) + " ";
                informacion += resultado.getString(2);
                                                                        
            }
            
            
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
                        
        //Se retorna el valor de la variable información        
        return informacion;
        
    }
    
    
    /**
     * Método que devuelve los resultados de un examen como un ArrayList de Strings
     * Cada uno de los Strings va a tener la siguiente estructura: "calificacion/nombresuario"
     * Dicha información se va a extraer de la tabla preguntasseleccionadas cuyo
     * número de examen coincida con el que se recibe como parámetro en este método.
     * @param numeroExamen Número del examen (único)
     * @return 
     */
    
    
    public ArrayList<String> resultadosExamen(int numeroExamen){
        
        //Se declara e inicaliza el ArrayList resultados que será retornada
        ArrayList<String> resultados = new ArrayList<>();
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_consulta = "SELECT calificacion, nombreUsuario FROM preguntasseleccionadas WHERE numeroExamen = " + numeroExamen + ";";
        
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se guarda el resultado de la consulta en la variable resultado
            ResultSet resultado = sentencia.executeQuery(sql_consulta);
            
            while (resultado.next()) {
                
                //Se guarda en un String la información del registro actual con la estructura mencionada
                String informacionPregunta = resultado.getInt(1) + "/" + resultado.getString(2);
                
                //Se añade el String definido al ArrayList de resultados
                resultados.add(informacionPregunta);
            }
            
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        //Se retorna el ArrayList con la información requerida        
        return resultados;       
        
    }
    
    
    /**
     * Método que reinicia los valores de los campos estadoPregunta, respuestaDada, calificacion y nombreUsuario
     * de la tabla preguntasseleccionadas. Los nuevos valores para cada uno serán los siguientes:
     * estadoPregunta = DISPONIBLE
     * respuestaDada = null
     * calificaicon = null
     * nombreUsuario = null
     */
    
    public void reiniciarDatosBD(){
        
        //Se declara y crea una variable de tipo String con la sentencia SQL
        String sql_actualizar = "UPDATE preguntasseleccionadas SET estadoPregunta = 'DISPONIBLE', respuestaDada = null, calificacion = null, nombreUsuario = null;";
        
        try{
            //Se declara y guarda una conexión con la base de datos
            Connection conexion = fachada.conectar();
            //Se declara y almacena un objeto de tipo Statement       
            Statement sentencia = conexion.createStatement();
            //Se ejecuta la actualización en la base de datos
            sentencia.executeUpdate(sql_actualizar);
            //Se cierra la conexión
            conexion.close();
                        
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
        
    }
    
    
    
}
