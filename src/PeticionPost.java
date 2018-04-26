/**
 * 
 * Clase para realizar la peticion post
 * @author Juan Núñez Lerma / Fernando Cabrera Caballero
 * @version 1.0
 */


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
/**

 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PeticionPost {
	private URL url;
	String data;

    /**
    Constructor.
    @param url: Direccion donde se va a realizar la peticion
    */
	public PeticionPost (String url) throws MalformedURLException{
		this.url = new URL(url);
		data="";
	}

	 /**
    Funcion para añadir los datos que se envian.
    @param propiedad: nombre para identificar el dato que se ha enviado.
    @param valor: valor del datos que se ha enviado.
    */
	public void add (String propiedad, String valor) throws UnsupportedEncodingException{
		//codificamos cada uno de los valores
		if (data.length()>0)
		data+= "&"+ URLEncoder.encode(propiedad, "UTF-8")+ "=" +URLEncoder.encode(valor, "UTF-8");
		else
		data+= URLEncoder.encode(propiedad, "UTF-8")+ "=" +URLEncoder.encode(valor, "UTF-8");
	}

	/**
    Funcion para recibir la respuesta del servidor.
    @return respuesta: respuesta que es generada por el servidor y enviada al cliente.
    */
	public String getRespueta() throws IOException {
		String respuesta = "";
		//abrimos la conexion
		URLConnection conn = url.openConnection();
		//especificamos que vamos a escribir
		conn.setDoOutput(true);
		//obtenemos el flujo de escritura
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		//escribimos
		wr.write(data);
		//cerramos la conexion
		wr.close();
		
        //obtenemos el flujo de lectura
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String linea;
		//procesamos al salida
		while ((linea = rd.readLine()) != null) {
			respuesta+= linea;
		}
		return respuesta;
	}

}
