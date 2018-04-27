import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;


/**
 * Aplicaciones Telematicas para la Administracion
 * 
 * Este programa debe leer el nombre y NIF de un usuario del DNIe, formar el identificador de usuario y autenticarse con un servidor remoto a traves de HTTP 
 * @author Juan Núñez Lerma / Fernando Cabrera Caballero
 * @version 1.0
 */

public class Main {
    /**
     * @param args the command line arguments
     */
	
	/**
    Funcion principal del programa:
    Obtiene los datos, realiza la peticion post al servidor y recibe respuesta.
    */
    public static void main(String[] args) throws Exception{
        ByteArrayInputStream bais=null;
        
        //TAREA 2. Conseguir que el metodo LeerNIF de ObtenerDatos devuelva el 
        //         correctamente los datos de usuario 
        ObtenerDatos od = new ObtenerDatos();
        Usuario user = od.LeerNIF();
        if(user!=null)
            System.out.println("usuario: "+user.toString());
        
        //TAREA 3. AUTENTICAR EL CLIENTE CON EL SERVIDOR
       
        char n, ap2;
        String ap1;
        String usuario;
        String nick, dni;
        
        try {
	        usuario=user.getNombre();
	        n=usuario.charAt(0);
	        
	        ap1=user.getApellido1();
	        
	        usuario=user.getApellido2();
	        ap2=usuario.charAt(0);
	        
	        nick=n+ap1+ap2;
	        dni=user.getNif();
	        try {
		        PeticionPost post = new PeticionPost ("http://localhost:8081/p3/autentication");
		        post.add("user", nick);
		        post.add("pass", dni);
		        String respuesta = post.getRespueta();
		        String respuestaOK = "200 OK";
		        String respuestaBad = "400 BAD REQUEST";
		        
		        if (respuesta.contains(respuestaBad)==true) { //si recibe un 400, mostramos mensaje de usuario incorrecto.
		        	JOptionPane.showMessageDialog(null, "Usuario y contraseña incorrectos. Debe de solicitar su acceso.");
		        }
		        else if (respuesta.contains(respuestaOK)==true) { //si recibe un 200, mostramos mensaje de OK.
		        	JOptionPane.showMessageDialog(null, "Login correcto.");
		        }
		        else {
		        	JOptionPane.showMessageDialog(null, "Error de acceso");
		        }
		        System.out.println(respuesta); 
	        }catch(IOException e) { //Excepcion, por ejemplo, si no está arrancada la BBDD
	        	 System.out.println("Exception catched: " + e.getMessage());
	        	 JOptionPane.showMessageDialog(null, "Error en el sistema");
	        }  
        }
        catch(Exception e) { //Excepcion por si no se pueden leer DNI o si no se ha introducido DNI en el lector.
        	System.out.println("Exception catched: " + e.getMessage());
        	JOptionPane.showMessageDialog(null, "No ha sido posible leer los datos");
        }
    }
}
