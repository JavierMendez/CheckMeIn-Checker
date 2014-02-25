package Container;
import Internals.reloj;
import DB.ConexionMySQL;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class FrmAlter extends javax.swing.JFrame {

    public FrmAlter() {
        initComponents();
        this.setLocationRelativeTo(null);
        reloj obj = new reloj(lblHora);
        obj.start();
    }
    
    public Image getIconImage(){
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Resources/icon.png"));
        return retValue;
    }
    
    //Validar Contrasena y usuario...
    public boolean validarContra(String name, String pass){
           
        String qSql = "SELECT strUsername, pssPassword "
                + "FROM tblemployee "
                + "WHERE strUsername ='" + name + "' AND pssPassword ='" + pass +"'";
        
        int cont = 0;
        
        //Conexion a la base de datos...
        ConexionMySQL mysql = new ConexionMySQL();
        Connection con = mysql.Conectar();
        
        try{
            Statement st = con.createStatement();
            //Objeto que contiene los resultado de la busqueda...
            ResultSet rs = st.executeQuery(qSql);
            
            while(rs.next()){
                cont = cont + 1;
            }
                                               
        } catch(SQLException ex){
         //   JOptionPane.showMessageDialog(null, ex);
        }
        
        if(cont == 1){
                return true;
            }else{
                return false;
            }    
    }    
       
    private Integer obtenerNombre(String emp){
        int nombre = 0;
        
        //Variables de conexion..
        ConexionMySQL mysql = new ConexionMySQL();
        Connection con = mysql.Conectar();
        
        String qSql = "SELECT (idEmployee) AS ID "
                + "FROM tblemployee "
                + "WHERE strUsername ='" + emp + "'";
        
        try{
            PreparedStatement identificarStmt = con.prepareStatement(qSql);
            ResultSet rs = identificarStmt.executeQuery();

            //Si se encuentra el nombre en la base de datos
            while(rs.next()){
                nombre = rs.getInt("ID");
            }
            
        }catch(Exception ex){
            //JOptionPane.showMessageDialog(null,ex);
        }
        
        return nombre;
    }
    
    
    public void registrarAsistencia(){
        //Variables de conexion..
        ConexionMySQL mysql = new ConexionMySQL();
        Connection con = mysql.Conectar();
        
        //Variables
        String qSql;
        int nd, emple;
                          
        emple = obtenerNombre(txtUser.getText());
        
        qSql = "INSERT INTO tblattendance(idEmployee, dteDate, tmeTime)"
                + " VALUES(?,current_date(),current_time())";
        
        String men = "Recorded Attendance";
                 
        try {
            //Preparamos la consulta...
            PreparedStatement pst = con.prepareStatement(qSql);
            
            //Insertamos el valor en los campos de la base de datos...
            pst.setInt(1, emple);
            
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
        btnAceptar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        pssPass = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblHora = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CheckMeIn - Username & Password");
        setIconImage(getIconImage());
        setName("frmAlter"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(789, 444));
        jPanel1.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 0, 36)); // NOI18N
        jLabel1.setText("CheckMeIn");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(20, 20, 166, 48);

        btnAceptar.setFont(new java.awt.Font("Segoe UI Light", 0, 14)); // NOI18N
        btnAceptar.setForeground(new java.awt.Color(255, 255, 255));
        btnAceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/fondoBoton0.png"))); // NOI18N
        btnAceptar.setText("Check");
        btnAceptar.setBorder(null);
        btnAceptar.setBorderPainted(false);
        btnAceptar.setContentAreaFilled(false);
        btnAceptar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });
        jPanel1.add(btnAceptar);
        btnAceptar.setBounds(300, 250, 79, 29);

        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel3.setText("User:");
        jPanel1.add(jLabel3);
        jLabel3.setBounds(160, 170, 38, 25);

        jLabel4.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel4.setText("Password:");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(160, 210, 75, 25);

        txtUser.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jPanel1.add(txtUser);
        txtUser.setBounds(250, 170, 130, 31);

        pssPass.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jPanel1.add(pssPass);
        pssPass.setBounds(250, 210, 130, 31);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/administrator2-128.png"))); // NOI18N
        jPanel1.add(jLabel5);
        jLabel5.setBounds(20, 150, 128, 128);

        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 0, 24)); // NOI18N
        jLabel2.setText("Time Attendance Checker");
        jPanel1.add(jLabel2);
        jLabel2.setBounds(20, 70, 260, 32);

        lblHora.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        lblHora.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel1.add(lblHora);
        lblHora.setBounds(300, 30, 80, 30);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed

        String user = txtUser.getText();
        String pass = pssPass.getText();

        if(validarContra(user,pass) == true){
            registrarAsistencia();
            txtUser.setText("");
            pssPass.setText("");
            txtUser.requestFocus();
        }else if(validarContra(user,pass) == false){
            JOptionPane.showMessageDialog(null, "The username or password are not correct");
            txtUser.setText("");
            pssPass.setText("");
            txtUser.requestFocus();
        }

    }//GEN-LAST:event_btnAceptarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
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
            java.util.logging.Logger.getLogger(FrmAlter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAlter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAlter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAlter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAlter().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblHora;
    private javax.swing.JPasswordField pssPass;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}
