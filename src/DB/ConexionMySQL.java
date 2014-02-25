package DB;
import java.sql.*;
import javax.swing.JOptionPane;

public class ConexionMySQL {
    
    //Variables para definir la cadena de conexion...
    public String db;
    public String url;
    public String user;
    public String pass;

    public ConexionMySQL() {
        this.db = "checkmein";
        this.url = "jdbc:mysql://localhost/"+db;
        this.user = "rootuser";
        this.pass = "root";
    }
    
    //Creamos un metodo que devuelve un valor de tipo Connection para crear la conexión...
    public Connection Conectar(){
        
        Connection link = null;
        
        //Manejamos la conexion con excepciones...
        try{
            //Cargar el driver MySQL...
            Class.forName("com.mysql.jdbc.Driver");
            //Crear la conexion a la base de datos...
            link = DriverManager.getConnection(this.url, this.user,this.pass);
            
        }catch(Exception ex){
            //Si ocurre un error...Se muestra en un cuadro de dialogo...
            JOptionPane.showMessageDialog(null,"Oops!, " + ex);
        }
        
        return link;
    }
        
}