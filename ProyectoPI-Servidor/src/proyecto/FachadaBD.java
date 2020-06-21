/**
* Proyecto - Programación Interactiva 
* Integrantes: 
* Jhonier Andrés Calero Rodas 1424599
* Juan Pablo Moreno Muñoz 1423437
* Joan Manuel Tovar Guzmán 1423124
* 
* Archivo: FachadBD.java 
* Fecha de entrega: 11/06/2015
* Programación Interactiva Universidad del Valle EISC
* 
*
* Clase : FachadaBD 
* Responsabilidad : Esta clase se encarga de realizar la conexión
* con una base de datos y retornar dicha conexión por medio de un método.
* 
*/
package proyecto;

import java.sql.*;

//Clase FachadaBD
public class FachadaBD {
    
    //Atributos de la clase FachadaBD.
    String url, usuario, password;
    Connection conexion;
    Statement instruccion;
    ResultSet temp;
   
    //Constructor de la clase FachadaBD.
    public FachadaBD(){
        
        url = "jdbc:postgresql://localhost:5432/joanmatg";
        usuario = "joanmatg";
        password = "joanmatg";
    }
    
    //Se define el método conectar() que permite cargar un driver para conectar el programa con una base de datos. Cuando se conecta
    //retorna esta conexión.
    public Connection conectar()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
           
        }
        catch(Exception e)
        {
            System.out.println("No se pudo cargar el driver.");
        }
        
        try
        {
            conexion = DriverManager.getConnection(url, usuario, password);
            return conexion;
        }
        catch(Exception e)
        {
            System.out.println("No se pudo realizar la conexion");
            //Se retorna el valor de null en caso de que no se
            //hayan completado las sentencias del bloque try
            return null;
        }
    }
}
