/*Clase que controla el funcionamiento del reloj en frame del lector*/
package Internals;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class reloj extends Thread{
    private JLabel lbl;
    
    public reloj(JLabel lbl){
        //Actualizar el valor de la etiqueta con la hora...
        this.lbl = lbl;
    }
    
    //Metodo que carga el hilo...
    public void run(){
        while(true){
            Date ahora = new Date();
            SimpleDateFormat formato = new SimpleDateFormat("hh:mm:ss");
            lbl.setText(formato.format(ahora));
            try{
                sleep(1000);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null,"Oops! algo salió mal con la hora" + ex);
            }
        }
    }
    
}
