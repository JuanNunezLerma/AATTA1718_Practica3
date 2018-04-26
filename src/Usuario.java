/**
 * 
 * Clase para almacenar los datos de un usuario
 * @author Juan Núñez Lerma / Fernando Cabrera Caballero
 * @version 1.0
 */

public class Usuario {
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String nif;
    
    /**
    Constructor.
    @param n nombre.
    @param a1 primer apellido.
    @param a2 segundo apellido.
    @param ni NIF
    */
    public Usuario(String n,String a1,String a2,String ni){
        nombre=n;
        apellido1=a1;
        apellido2=a2;
        nif=ni;
    }
    
    public Usuario() {
    	
    }
    
    /**
    Metodo toString de Usuario.
    @return Datos leidos del usuario.
    */
    @Override
    public String toString(){
        return nombre+" "+apellido1+" "+apellido2+" "+nif;
    }
    

    /**
    * Getter.
    * @return nombre.
    */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter.
     * @param nombre: nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
    * Getter.
    * @return primer apellido.
    */
    public String getApellido1() {
        return apellido1;
    }

    /**
     * Setter.
     * @param apellido1: primer apellido.
     */
    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    /**
    * Getter.
    * @return segundo apellido.
    */
    public String getApellido2() {
        return apellido2;
    }

    /**
     * Setter.
     * @param apellido2: segundo apellido.
     */
    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    /**
    * Getter.
    * @return NIF.
    */
    public String getNif() {
        return nif;
    }

    /**
     * Setter.
     * @param nif: NIF.
     */
    public void setNif(String nif) {
        this.nif = nif;
    }
          
}
