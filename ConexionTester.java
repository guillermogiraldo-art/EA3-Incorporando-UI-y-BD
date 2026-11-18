import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Aplicación de escritorio (Swing) que permite ingresar la configuración de
 * una base de datos y validar la conexión mediante JDBC.
 *
 * Contiene el JFrame y la lógica de validación de la conexión.
 */
public class ConexionTester extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color COLOR_EXITO = new Color(0, 128, 0);
    private static final Color COLOR_ERROR = new Color(200, 0, 0);

    // Componentes de la interfaz
    private JTextField txtUrl;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnProbarConexion;
    private JTextArea areaResultado;

    public ConexionTester() {
        super("Probador de conexión JDBC");
        inicializarComponentes();
        configurarVentana();
    }

    // ------------------------------------------------------------------
    // Construcción de la interfaz gráfica
    // ------------------------------------------------------------------

    private void inicializarComponentes() {
        txtUrl = new JTextField("jdbc:sqlite:inventario.db", 25);
        txtUsuario = new JTextField(25);
        txtContrasena = new JPasswordField(25);
        btnProbarConexion = new JButton("Probar conexión");

        areaResultado = new JTextArea(8, 40);
        areaResultado.setEditable(false);
        areaResultado.setLineWrap(true);
        areaResultado.setWrapStyleWord(true);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        // ActionListener del botón: delega la lógica en un método propio
        btnProbarConexion.addActionListener(e -> probarConexion());

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelPrincipal.add(construirPanelFormulario(), BorderLayout.NORTH);
        panelPrincipal.add(new JScrollPane(areaResultado), BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JPanel construirPanelFormulario() {
        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 8, 8));
        panelCampos.add(new JLabel("URL de conexión:"));
        panelCampos.add(txtUrl);
        panelCampos.add(new JLabel("Usuario (vacío si es SQLite):"));
        panelCampos.add(txtUsuario);
        panelCampos.add(new JLabel("Contraseña (vacía si es SQLite):"));
        panelCampos.add(txtContrasena);

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnProbarConexion);

        JPanel panelFormulario = new JPanel(new BorderLayout(0, 10));
        panelFormulario.add(panelCampos, BorderLayout.CENTER);
        panelFormulario.add(panelBoton, BorderLayout.SOUTH);
        return panelFormulario;
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------
    // Lógica de conexión (JDBC)
    // ------------------------------------------------------------------

    /**
     * Toma los datos ingresados por el usuario e intenta abrir una conexión.
     * La conexión se declara en un try-with-resources, por lo que se cierra
     * automáticamente tanto si la prueba es exitosa como si falla.
     */
    private void probarConexion() {
        String url = txtUrl.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (url.isEmpty()) {
            mostrarMensaje("Debe ingresar la URL de conexión.", COLOR_ERROR);
            return;
        }

        try {
            // Carga explícita del driver (puede lanzar ClassNotFoundException)
            Class.forName(obtenerClaseDriver(url));

            try (Connection conexion = DriverManager.getConnection(url, usuario, contrasena)) {
                mostrarMensaje("Conexión exitosa\n"
                        + "Base de datos: " + conexion.getMetaData().getDatabaseProductName()
                        + " " + conexion.getMetaData().getDatabaseProductVersion(), COLOR_EXITO);
            } // la conexión se cierra automáticamente aquí

        } catch (ClassNotFoundException e) {
            mostrarMensaje("Fallo del driver: no se encontró la clase del driver JDBC.\n"
                    + "Verifique que el .jar del driver esté en el classpath.\n"
                    + "Detalle técnico: " + e.getMessage(), COLOR_ERROR);
        } catch (SQLException e) {
            mostrarMensaje("Fallo de conexión\n"
                    + "Detalle técnico: " + e.getMessage(), COLOR_ERROR);
        } finally {
            // Se borra la contraseña de memoria del campo por seguridad
            txtContrasena.setText("");
        }
    }

    /**
     * Determina el nombre de la clase del driver según el prefijo de la URL.
     */
    private String obtenerClaseDriver(String url) {
        String urlMinuscula = url.toLowerCase();
        if (urlMinuscula.startsWith("jdbc:sqlite:")) {
            return "org.sqlite.JDBC";
        } else if (urlMinuscula.startsWith("jdbc:mysql:")) {
            return "com.mysql.cj.jdbc.Driver";
        } else if (urlMinuscula.startsWith("jdbc:postgresql:")) {
            return "org.postgresql.Driver";
        } else if (urlMinuscula.startsWith("jdbc:h2:")) {
            return "org.h2.Driver";
        }
        // Si no se reconoce el motor, se intenta con el de SQLite por defecto
        return "org.sqlite.JDBC";
    }

    // ------------------------------------------------------------------
    // Utilidades de la interfaz
    // ------------------------------------------------------------------

    private void mostrarMensaje(String mensaje, Color color) {
        areaResultado.setForeground(color);
        areaResultado.setText(mensaje);
    }

    // ------------------------------------------------------------------
    // Punto de entrada
    // ------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConexionTester().setVisible(true));
    }
}
