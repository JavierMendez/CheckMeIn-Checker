package Container;

import DB.ConexionMySQL;
import Internals.reloj;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import static com.digitalpersona.onetouch.processing.DPFPTemplateStatus.TEMPLATE_STATUS_FAILED;
import static com.digitalpersona.onetouch.processing.DPFPTemplateStatus.TEMPLATE_STATUS_READY;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class FrmLector extends javax.swing.JFrame {
    
    /*Variable para manejar el lector...*/
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();
    
    /*Varible que permite establecer las capturas de la huellas, para determina sus caracteristicas
    y poder estimar la creacion de un template de la huella para luego poder guardarla*/
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    
    /*Esta variable tambien captura una huella del lector y crea sus caracteristcas para auntentificarla 
    o verificarla con alguna guardada en la BD*/
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
    
    /*Variable que para crear el template de la huella luego de que se hallan creado las caracteriticas
    necesarias de la huella si no ha ocurrido ningun problema*/
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";

    public FrmLector() {
        initComponents();
        reloj obj = new reloj(lblHora);
        obj.start();
        this.setLocationRelativeTo(null);
    }
    
    public Image getIconImage(){
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Resources/icon.png"));
        return retValue;
    }
    
    protected void Iniciar(){
        //Captura de huella...
        Lector.addDataListener(new DPFPDataAdapter(){
            @Override public void dataAcquired(final DPFPDataEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        EnviarTexto("The fingerprint has been captured");
                        ProcesarCaptura(e.getSample());
                    }
                });
            }
        });
        //Manejo del estado del lector...
        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter(){
            //Lector activado...
            @Override public void readerConnected(final DPFPReaderStatusEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        EnviarTexto("The fingerprint reader is activated");
                    }
                });
            }
            //Lector desactivado...
            @Override public void readerDisconnected(final DPFPReaderStatusEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                    EnviarTexto("The fingerprint reader is not available");
                    }
                });
            }
        });
        //Manejo del Sensor...
        Lector.addSensorListener(new DPFPSensorAdapter(){
            //Sensor de colocación sobre la huella digital...
            @Override public void fingerTouched(final DPFPSensorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        EnviarTexto("The finger has been placed");
                    }
                });
            }
            //Sensor de retiro de dedo del lector...
            @Override public void fingerGone(final DPFPSensorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        EnviarTexto("The finger has been removed");
                        try{
                            identificarHuella();
                            Reclutador.clear();
                        }catch(IOException ex){
                            Logger.getLogger(FrmLector.class.getName()).log(Level.SEVERE, null, ex);
                        }finally{
                            lblImagenHuella.setIcon(null);
                        }
                    }
                });
            }
        });
        //Manejor de errores...
        Lector.addErrorListener(new DPFPErrorAdapter(){
            public void errorReader(final DPFPErrorEvent e){
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        EnviarTexto("Error: "+e.getError());
                    }
                });
            }
        });
    }
    
    //Variables para guardar y verificar las huellas en la BD...
    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;
    
    public  DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose){
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        }catch (DPFPImageQualityException e){
            return null;
        }
    }
    
    public Image CrearImagenHuella(DPFPSample sample){
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }
    
    public void DibujarHuella(Image image){
        lblImagenHuella.setIcon(new ImageIcon(image.getScaledInstance(lblImagenHuella.getWidth(),lblImagenHuella.getHeight(),Image.SCALE_DEFAULT)));
        repaint();
    }
    
    public void EstadoHuellas(){
        //EnviarTexto("Muestra de Huellas Necesarias para Guardar la Huella. Template "+ Reclutador.getFeaturesNeeded());
    }

    public void EnviarTexto(String string){
       //txtArea.append(string + "\n");
        lblStatus.setText(string);
    }

    public void start(){
        Lector.startCapture();
        EnviarTexto("Fingerprint reader is being used");
    }

    public void stop(){
        Lector.stopCapture();
        EnviarTexto("Fingerprint reader is not being used");
    } 

    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }

    public  void ProcesarCaptura(DPFPSample sample){
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null)
            try{
                //System.out.println("Las Caracteristicas de la Huella han sido creada");
                Reclutador.addFeatures(featuresinscripcion);// Agregar las caracteristicas de la huella a la plantilla a crear
                
                // Dibuja la huella dactilar capturada.
                Image image=CrearImagenHuella(sample);
                DibujarHuella(image);
     
//                btnVerificar.setEnabled(true);
//                btnIdentificar.setEnabled(true);

            }catch(DPFPImageQualityException ex){
                JOptionPane.showMessageDialog(null, ex);
//                System.err.println("Error: "+ex.getMessage());
            }finally{
                EstadoHuellas();
                // Comprueba si la plantilla se ha creado.
                switch(Reclutador.getTemplateStatus()){
                    case TEMPLATE_STATUS_READY:	// informe de éxito y detiene  la captura de huellas
                        stop();
                        setTemplate(Reclutador.getTemplate());
                        EnviarTexto("The fingerprint has been created, now you can save it");
                    break;
                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
                        JOptionPane.showMessageDialog(FrmLector.this, "The fingerprint cannot been created", "CheckMeIn - Fingerprint Template System", JOptionPane.ERROR_MESSAGE);
                        start();
                    break;
                }
            }
    }

    public static int usuario;
    public void identificarHuella() throws IOException{
        
        //Conexion a la base de datos...
        ConexionMySQL mysql = new ConexionMySQL();
        Connection con = mysql.Conectar();
        
        try{
            //Obtiene todas las huellas de la bd
            PreparedStatement identificarStmt = con.prepareStatement("SELECT (idEmployee) AS ID, blobHuellaA FROM tblemployee");
            ResultSet rs = identificarStmt.executeQuery();

            //Si se encuentra el nombre en la base de datos
            while(rs.next()){
                //Lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("blobHuellaA");
                usuario = rs.getInt("ID");
                //Crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //Envia la plantilla creada al objeto contendor de Template del componente de huella digital
                setTemplate(referenceTemplate);

                // Compara las caracteriticas de la huella recientemente capturda con la
                // alguna plantilla guardada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
                //compara las plantilas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa
                //e indica el nombre de la persona que coincidió.
                if (result.isVerified()){
                    //crea la imagen de los datos guardado de las huellas guardadas en la base de datos
                    //JOptionPane.showMessageDialog(null, "Las huella capturada es de "+nombre,"Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE);
                    //Huella capturada encontrada en la BD... x lo tanto se guarda la asistencia
                    registrarAsistencia();
                     return;
                }
            }
            //Si no encuentra alguna huella correspondiente al nombre lo indica con un mensaje
            JOptionPane.showMessageDialog(null, "There is no record that matches the fingerprint", "Error", JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
            }catch(SQLException e) {
                //Si ocurre un error lo indica en la consola
                //System.err.println("Error al identificar huella dactilar."+e.getMessage());
                JOptionPane.showMessageDialog(null, "Failed to identify the fingerprint" + e);
            }
    }
    
    public void registrarAsistencia(){
        //Variables de conexion..
        ConexionMySQL mysql = new ConexionMySQL();
        Connection con = mysql.Conectar();
        
        //Variables
        String qSql;
        int nd, emp;
                        
        emp = usuario;
                                
        qSql = "INSERT INTO tblattendance(idEmployee, dteDate, tmeTime)"
                + " VALUES(?,current_date(),current_time())";
        
        String men = "Recorded Attendance";
                 
        try {
            //Preparamos la consulta...
            PreparedStatement pst = con.prepareStatement(qSql);
            
            //Insertamos el valor en los campos de la base de datos...
            pst.setInt(1, emp);
            nd = pst.executeUpdate();
            
            if(nd > 0){
                JOptionPane.showMessageDialog(null, men, "Time Attendance Checker", 1);            
            }
            
        } catch (SQLException ex) {
            //JOptionPane.showMessageDialog(null, ex);
        }
                
    }
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblImagenHuella = new javax.swing.JLabel();
        lblHora = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CAI - Lector Biométrico");
        setIconImage(getIconImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 0, 36)); // NOI18N
        jLabel1.setText("CheckMeIn");

        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 24)); // NOI18N
        jLabel2.setText("Time Attendance Checker");

        lblImagenHuella.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblHora.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/administrator2-128.png"))); // NOI18N

        lblStatus.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHora, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(158, 158, 158)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblStatus)
                    .addComponent(lblImagenHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addComponent(jLabel5)
                .addContainerGap(168, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHora, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblImagenHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(35, 35, 35)))
                .addGap(18, 18, 18)
                .addComponent(lblStatus)
                .addContainerGap(99, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        Iniciar();
	start();
        //EstadoHuellas();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        stop();
        FrmContainer obj = new FrmContainer();
        obj.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    public static void main(String args[]) {
        /* Set the Windows look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmLector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmLector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmLector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmLector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmLector().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblImagenHuella;
    private javax.swing.JLabel lblStatus;
    // End of variables declaration//GEN-END:variables
}
