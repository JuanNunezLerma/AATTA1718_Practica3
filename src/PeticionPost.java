import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Aplicaciones Telemáticas para la Administración
 * 
 * Este programa debe ller el nombre y NIF de un usuario del DNIe, formar el identificador de usuario y autenticarse con un servidor remoto a través de HTTP 
 * @author Juan Carlos Cuevas Martínez
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

	public PeticionPost (String url) throws MalformedURLException{
		this.url = new URL(url);
		data="";
	}

	public void add (String propiedad, String valor) throws UnsupportedEncodingException{
		//codificamos cada uno de los valores
		if (data.length()>0)
		data+= "&"+ URLEncoder.encode(propiedad, "UTF-8")+ "=" +URLEncoder.encode(valor, "UTF-8");
		else
		data+= URLEncoder.encode(propiedad, "UTF-8")+ "=" +URLEncoder.encode(valor, "UTF-8");
	}

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