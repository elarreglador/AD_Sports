import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws SQLException {
/*
		 Escribe un programa que se conecte a una base de datos HSQLDB y use statements para:

			1. Crear (si no existe) una base de datos con tablas de Sports y Players

			2. Hacer un menú con las siguientes opciones:
				2.1. Preguntar al usuario para añadir un deporte nuevo
				2.2. Preguntar al usuario para añadir un jugador nuevo
				2.3. Mostrar el nombre de cada jugador y el deporte que juega usando una query
				2.4. Preguntar al usuario el COD de un deporte para borrarlo a éste y a los jugadores asociados
*/
		Scanner teclado = new Scanner(System.in);

		System.out.println("Conectamos a la BD");
		String urlJDBC = "jdbc:hsqldb:sample.db";
		Connection con = DriverManager.getConnection(urlJDBC);
		
        System.out.println("Crea tablas si no existían.");
        creaTablas(con);
        
        boolean salir = false;
        while(!salir) {
        	
	        switch (menu(teclado)) {
		        case 0:
		        	salir = true;
		        	break;
		        case 1:
		        	//1) Agregar nuevo deporte
		            creaNuevoDeporte(con, teclado);
		        	break;
		        case 2:
		        	//2) Agregar nuevo jugador
		        	creaNuevoJugador(con, teclado);
		        	break;
		        case 3:
		        	//3) Info. de jugador por su ID
		        	muestraJugadorID(con, teclado);
		        	break;
		        case 4:
		        	//4) Borrar deporte y sus jugadores
		        	borraDeporteID(con, teclado);
		        	break;
		        case 5:
		        	//5) Muestra deportes
		        	muestraDeportes(con);
		        	break;
		        case 6:
		        	//6) Muestra Jugadores
		        	muestraJugadores(con);
		        	break;
	        }
    	}
        
        
        System.out.println("Cerramos conexion a la BD.");
        con.close();
        
        teclado.close();
	}
	
	
	
	private static void borraDeporteID(Connection con, Scanner teclado) throws SQLException {
		    System.out.print("ID del deporte: ");
		    int sportID = teclado.nextInt();

		    String query = "DELETE FROM sports WHERE id = ?;";
		    PreparedStatement pstmt = con.prepareStatement(query);
		    pstmt.setInt(1, sportID);

		    pstmt.executeUpdate(); //Como no esperamos retorno usamos .executeUpdate() en lugar de .executeQuery()

		    System.out.println("Eliminado.");
	}



	private static void muestraJugadorID(Connection con, Scanner teclado) throws SQLException {
	    System.out.print("ID del jugador: ");
	    int jugadorID = teclado.nextInt();

	    String query = "SELECT * FROM players WHERE id = ?";
	    PreparedStatement pstmt = con.prepareStatement(query);
	    pstmt.setInt(1, jugadorID);

	    ResultSet rs = pstmt.executeQuery();

	    Boolean encontrado = false;
	    String nombre = null;
	    int sportId = 0;
	    if (rs.next()) {
	    	encontrado = true;
	    	sportId = rs.getInt("sportsId");
	        nombre = rs.getString("name");
	    } else {
	        System.out.println("Jugador no encontrado.");
	    }
	    
	    
	    if (encontrado) { 
		    query = "SELECT * FROM sports WHERE id = ?";
		    pstmt = con.prepareStatement(query);
		    pstmt.setInt(1, sportId);
	
		    rs = pstmt.executeQuery();
	
		    String deporte;
		    rs.next();
		    deporte = rs.getString("name");
		    
		    System.out.println(jugadorID + " | " + nombre + " | " + deporte);
	    }
	    
	    // Cerrar recursos
	    rs.close();
	    pstmt.close();
	}

	
	
	
	private static void creaNuevoJugador(Connection con, Scanner teclado) throws SQLException {
		System.out.print("Nombre del nuevo deportista: ");
		String nombre = teclado.next();
		System.out.print("ID de su deporte: ");
		int sportID = teclado.nextInt();
		
		String query = "INSERT INTO players (name, sportsId) VALUES (?,?); ";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, nombre);
		ps.setInt(2, sportID);
		ps.executeUpdate();
		
		System.out.println("Agregado " + nombre + " a la tabla players y enlazado a la sportID Num:" + sportID);
	}



	private static void creaNuevoDeporte(Connection con, Scanner teclado) throws SQLException {
		System.out.print("Nombre del nuevo deporte: ");
		String deporte = teclado.next();
		
		String query = "INSERT INTO sports (name) VALUES (?); ";
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, deporte);
		ps.executeUpdate();
		
		System.out.println("Agregado " + deporte + " a la tabla sports.");
		System.out.println();
		muestraDeportes(con);
	}

	
	
	private static void muestraDeportes(Connection con) throws SQLException {
	    String query = "SELECT id, name FROM sports;";
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    
	    System.out.println("Lista de deportes:");
	    while (rs.next()) {
	        int id = rs.getInt("id");
	        String deporte = rs.getString("name");
	        System.out.println("ID: " + id + " - Deporte: " + deporte);
	    }
	    
	    rs.close();
	    stmt.close();
	}
	
	
	
	private static void muestraJugadores(Connection con) throws SQLException {
	    String query = "SELECT id, name FROM players;";
	    Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    
	    System.out.println("Lista de jugadores:");
	    while (rs.next()) {
	        int id = rs.getInt("id");
	        String deporte = rs.getString("name");
	        System.out.println("ID: " + id + " - Nombre: " + deporte);
	    }
	    
	    rs.close();
	    stmt.close();
	}
	
	

	public static void creaTablas(Connection con) throws SQLException {
        // Crear tablas, si no existen
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sports ("
                    + "id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, "
                    + "name VARCHAR(20)"
                    + ");");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS players ("
                    + "id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, "
                    + "name VARCHAR(20),"
                    + "sportsId INT,"
                    + "FOREIGN KEY (sportsId) REFERENCES sports(id) ON DELETE CASCADE"
                    + ");");
        }

	}
	
	
	
	public static int menu(Scanner teclado) {
        int retorno;

        do {
            System.out.println();
            System.out.println("1) Agregar nuevo deporte");
            System.out.println("2) Agregar nuevo jugador");
            System.out.println("3) Info. de jugador por su ID");
            System.out.println("4) Borrar deporte y sus jugadores");
            System.out.println("5) Muestra deportes");
            System.out.println("6) Muestra jugadores");
            System.out.println();
            System.out.println("0) Salir");
            System.out.println();
            System.out.print("Elige una opción y pulsa Intro: ");
            
            retorno = teclado.nextInt();

            if (retorno < 0 || retorno > 6) {
                System.out.println("Opción inválida. Inténtalo de nuevo.");
            }
        } while (retorno < 0 || retorno > 6);

        return retorno;
    }
        
}
