import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.*;
import javax.swing.JOptionPane;

/**
 * La clase ObtenerDatos implementa cuatro mÃ©todos pÃºblicos que permiten obtener
 * determinados datos de los certificados de tarjetas DNIe, Izenpe y Ona.
 *
 * @author tbc
 */
public class ObtenerDatos {

    private static final byte[] dnie_v_1_0_Atr = {
        (byte) 0x3B, (byte) 0x7F, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x6A, (byte) 0x44,
        (byte) 0x4E, (byte) 0x49, (byte) 0x65, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0x00};
    private static final byte[] dnie_v_1_0_Mask = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFF};

    public ObtenerDatos() {
    }

    public Usuario LeerNIF() { // Metodo para acceder y leer el DNI.

        Usuario user = null; // Clase usuario para guardas los datos del DNI.
        byte[] datos=null; // Array de bytes para guardar los datos.
        try {
            Card c = ConexionTarjeta(); //Se crea la conexión.
            if (c == null) {
                throw new Exception("ACCESO DNIe: No se ha encontrado ninguna tarjeta");
            }
            byte[] atr = c.getATR().getBytes();
            CardChannel ch = c.getBasicChannel();

            if (esDNIe(atr)) {
                datos = leerCertificado(ch);
                if(datos!=null)
                    user = leerDatosUsuario(datos);
            }
            c.disconnect(false);

        } catch (Exception ex) {
            Logger.getLogger(ObtenerDatos.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return user;
    }

    public byte[] leerCertificado(CardChannel ch) throws CardException, CertificateException {


        int offset = 0;
        String completName = null;

        //[1] PRÃ�CTICA 3. Punto 1.a
        /*Comando SELECT, este comando permite la seleccion de fichero dedicado a (DF) o de un fichero elemental (EF). Esto lo podemos saber
        ya que en el siguiente array de byte presenta la misma estructura que su especificacion:
        - El primer octeto 0x00 es el campo CLA
        - El segundo octeto 0xA4 es el campo INS
        - El tercero octeto puede ser 0x00(Selecciona DF o EF por Id) o 0x04(Seleccion directa de DF por nombre) es el campo P1.
        - El cuarto octeto es 0x00 es el campo P2.
        - El quinto octeto es el campo LC, es la longitud del campo de datos.
        - Los siguientes campos son los datos.
        */
        //En este caso el tercer octeto es 0x04(Selección directa de DF(fichero dedicado) por nombre)
        byte[] command = new byte[]{(byte) 0x00, (byte) 0xa4, (byte) 0x04, (byte) 0x00, (byte) 0x0b, (byte) 0x4D, (byte) 0x61, (byte) 0x73, (byte) 0x74, (byte) 0x65, (byte) 0x72, (byte) 0x2E, (byte) 0x46, (byte) 0x69, (byte) 0x6C, (byte) 0x65};
       // DATOS: 4D 61 73 74 65 72 2E 46 69 6C 65(hexadecimal)->ASCII----->Master.File
        //En este caso accedemos al fichero Master.File (un comando de selección por nombre del fichero maestro )
        //LC=0x0b
        ResponseAPDU r = ch.transmit(new CommandAPDU(command));
        if ((byte) r.getSW() != (byte) 0x9000) {
            System.out.println("ACCESO DNIe: SW incorrecto");
            return null;
        }

        //[2] PRÃ�CTICA 3. Punto 1.a
        /*Comando SELECT, este comando permite la seleccion de fichero dedicado a (DF) o de un fichero elemental (EF). Esto lo podemos saber
        ya que en el siguiente array de byte presenta la misma estructura que su especificacion:
        - El primer octeto 0x00 es el campo CLA
        - El segundo octeto 0xA4 es el campo INS
        - El tercero octeto puede ser 0x00(Selecciona DF o EF por Id) o 0x04(Seleccion directa de DF por nombre) es el campo P1.
        - El cuarto octeto es 0x00 es el campo P2.
        - El quinto octeto es el campo LC, es la longitud del campo de datos.
        - Los siguientes campos son los datos.
        */
      //En este caso P1= 0x00(Selecciona DF o EF por Id)
      //LC=0x02
      //DATOS:50 15--->DECIMAL->80  
      //Los dos últimos valores hacen referencia al ID del fichero elemental 
      //P1=0x00 para poder seleccionar por ID. 
        
        command = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x50, (byte) 0x15};
        
        r = ch.transmit(new CommandAPDU(command));

        if ((byte) r.getSW() != (byte) 0x9000) {
            System.out.println("ACCESO DNIe: SW incorrecto");
            return null;
        }

        //[3] PRÃ�CTICA 3. Punto 1.a
        /*Comando SELECT, este comando permite la seleccion de fichero dedicado a (DF) o de un fichero elemental (EF). Esto lo podemos saber
        ya que en el siguiente array de byte presenta la misma estructura que su especificacion:
        - El primer octeto 0x00 es el campo CLA
        - El segundo octeto 0xA4 es el campo INS
        - El tercero octeto puede ser 0x00(Selecciona DF o EF por Id) o 0x04(Seleccion directa de DF por nombre) es el campo P1.
        - El cuarto octeto es 0x00 es el campo P2.
        - El quinto octeto es el campo LC, es la longitud del campo de datos.
        - Los siguientes campos son los datos.
        */
      //En este caso P1= 0x00(Selecciona DF o EF por Id)
      //LC=0x02
      //DATOS:60 04--->DECIMAL->96  
      //Los dos últimos valores hacen referencia al ID del fichero elemental 
      //P1=0x00 para poder seleccionar por ID. 
        command = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x60, (byte) 0x04};
        r = ch.transmit(new CommandAPDU(command));

        byte[] responseData = null;
        if ((byte) r.getSW() != (byte) 0x9000) {
            System.out.println("ACCESO DNIe: SW incorrecto");
            return null;
        } else {
            responseData = r.getData();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] r2 = null;
        int bloque = 0;

        do {
             //[4] PRÃ�CTICA 3. Punto 1.b
            final byte CLA = (byte) 0x00;//Buscar quÃ© valor poner aquÃ­ (0xFF no es el correcto)
            final byte INS = (byte) 0xB0;//Buscar quÃ© valor poner aquÃ­ (0xFF no es el correcto)
            final byte LE = (byte) 0xFF;// Identificar quÃ© significa este valor

            //[4] PRÃ�CTICA 3. Punto 1.b
            command = new byte[]{CLA, INS, (byte) bloque/*P1*/, (byte) 0x00/*P2*/, LE};//Identificar quÃ© hacen P1 y P2
            r = ch.transmit(new CommandAPDU(command));

            System.out.println("ACCESO DNIe: Response SW1=" + String.format("%X", r.getSW1()) + " SW2=" + String.format("%X", r.getSW2()));

            if ((byte) r.getSW() == (byte) 0x9000) {
                r2 = r.getData();

                baos.write(r2, 0, r2.length);

                for (int i = 0; i < r2.length; i++) {
                    byte[] t = new byte[1];
                    t[0] = r2[i];
                    System.out.println(i + (0xff * bloque) + String.format(" %2X", r2[i]) + " " + String.format(" %d", r2[i])+" "+new String(t));
                }
                bloque++;
            } else {
                return null;
            }

        } while (r2.length >= 0xfe);


         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

      

        
        return baos.toByteArray();
    }

    
    
    
    /**
     * Este mÃ©todo establece la conexiÃ³n con la tarjeta. La funciÃ³n busca el
     * Terminal que contenga una tarjeta, independientemente del tipo de tarjeta
     * que sea.
     *
     * @return objeto Card con conexiÃ³n establecida
     * @throws Exception
     */
    private Card ConexionTarjeta() throws Exception {

        Card card = null;
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        //System.out.println("Terminals: " + terminals);

        for (int i = 0; i < terminals.size(); i++) {

            // get terminal
            CardTerminal terminal = terminals.get(i);

            try {
                if (terminal.isCardPresent()) {
                    card = terminal.connect("*"); //T=0, T=1 or T=CL(not needed)
                }
            } catch (Exception e) {

                System.out.println("Exception catched: " + e.getMessage());
                card = null;
            }
        }
        return card;
    }

    /**
     * Este mÃ©todo nos permite saber el tipo de tarjeta que estamos leyendo del
     * Terminal, a partir del ATR de Ã©sta.
     *
     * @param atrCard ATR de la tarjeta que estamos leyendo
     * @return tipo de la tarjeta. 1 si es DNIe, 2 si es Starcos y 0 para los
     * demÃ¡s tipos
     */
    private boolean esDNIe(byte[] atrCard) {
        int j = 0;
        boolean found = false;

        //Es una tarjeta DNIe?
        if (atrCard.length == dnie_v_1_0_Atr.length) {
            found = true;
            while (j < dnie_v_1_0_Atr.length && found) {
                if ((atrCard[j] & dnie_v_1_0_Mask[j]) != (dnie_v_1_0_Atr[j] & dnie_v_1_0_Mask[j])) {
                    found = false; //No es una tarjeta DNIe
                }
                j++;
            }
        }

        if (found == true) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Analizar los datos leÃ­dos del DNIe para obtener
     *   - nombre
     *   - apellidos
     *   - NIF
     * @param datos
     * @return 
     */
    private Usuario leerDatosUsuario(byte[] datos) {
    	//Atributos que debemos obtener
        //Deben de estar inicializados para poder crear el objeto usuario
        String nombre = "";
        String apellido1 = "";
        String apellido2 = "";
        String nif = "";
        byte[] nifbyte = new byte[1];
        byte[] apellidobyte = new byte[1];
        byte[] nombrebyte = new byte[1];
        
        System.out.println("DNIe:");
        
        int oid=6;
        int oidApn=85;
        int oidApn2=4;
        int oidApn3=3;
        int tamDni=9;
        int set=49;
        int posicion=0;
        int i, j=0;
        
        for (i=0; i<datos.length; i++) {
        	if(datos[i]==oid && datos[i+6]==tamDni) { // Buscamos la posicion que coincide el OID y que justo 6 posiciones despues aparezca el tamaño del DNI que es 9.
        		//nos posicionamos en la posicion de inicio del dni y lo concatenamos.
        		for(j=i+7; j<i+7+tamDni; j++) {
	        		nifbyte[0] = datos[j]; //Extraemos el byte
	                nif = nif +  new String(nifbyte);//Lo convertimos a Sring y lo concatemos
        		}
        		i=datos.length;
        	} 	
        }
        
        for (i=0; i<datos.length; i++) {
        	if(datos[i]==set && datos[i+6]==85 && datos[i+7]==4 && datos[i+8]==3) { //Buscamos el set, 
        		System.out.println("Encuentra los datos");
        		posicion=i+11; //Guardamos la posicion
        		for(j=posicion; datos[j]!=32; j++) { //Recorremos los datos para extraer el primer apellido hasta que encuentre un espacio en blanco(32)
        			//System.out.println("Entra al for apellido1");
        			apellidobyte[0] = datos[j];
        			apellido1 = apellido1 + new String (apellidobyte);
        		}
        		posicion=j+1; //Actualizamos la posicion para seguir con el segundo apellido
        		
        		for(j=posicion; datos[j]!=44; j++) { // Recorremos los datos para extraer el segundo apellido hasta que encuentre una coma (44)
        			//System.out.println("Entra al for apellido 2");
        			apellidobyte[0] = datos[j];
        			apellido2 = apellido2 + new String (apellidobyte);
        		}
        		posicion=j+2; //Actualizamos la posicion para seguir con el nombre
        		
        		for(j=posicion; datos[j]!=40; j++) { // Recorremos los datos para extraer el nombre hasta que encuentre un parentesis (40)
        			//System.out.println("Entra al for nombre");
        			nombrebyte[0] = datos[j];
        			nombre = nombre + new String (nombrebyte);
        		}
        		posicion=j; //Actualizamos la posicion donde ha acabado
        		i=datos.length; //Condicion de finalización del bucle for (ya que se ha terminado de leer)
        	}
        	
        }
        System.out.println("DNIe:" + nif);
        System.out.println("Apellidos:" + apellido1);
        System.out.println(apellido2);
        System.out.println("Nombre:" + nombre);
        
       //Creamos el objeto usuario y guardamos en el los atributos obtenidos
       Usuario user=new Usuario(nombre,apellido1,apellido2,nif);
       JOptionPane.showMessageDialog(null, "Bienvenido: "+ nombre + " " + apellido1 + " " + apellido2);
       return user;        
    }
}
