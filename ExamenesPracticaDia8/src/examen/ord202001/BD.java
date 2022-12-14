package examen.ord202001;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/** Clase de gestión de base de datos del examen 201911
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class BD {
	
	private static boolean LOGGING = true;
	private static boolean ACTIVADA = true;

	/** Inicializa una BD SQLITE y devuelve una conexión con ella
	 * @param nombreBD	Nombre de fichero de la base de datos
	 * @return	Conexión con la base de datos indicada. Si hay algún error, se devuelve null
	 */
	public static Connection initBD( String nombreBD ) {
		if (!ACTIVADA) return null;
		try {
		    Class.forName("org.sqlite.JDBC");
		    Connection con = DriverManager.getConnection("jdbc:sqlite:" + nombreBD );
			log( Level.INFO, "Conectada base de datos " + nombreBD, null );
		    return con;
		} catch (ClassNotFoundException | SQLException e) {
			log( Level.SEVERE, "Error en conexión de base de datos " + nombreBD, e );
			return null;
		}
	}
	
	/** Crea las tablas de la base de datos. Si ya existen, las deja tal cual. Devuelve un statement para trabajar con esa base de datos
	 * @param con	Conexión ya creada y abierta a la base de datos
	 * @param cargar	true si se quiere no solo crear las tablas, sino insertar sus datos
	 * @param tabla	Tabla de donde tomar los datos (la primera columna se considera como tipo)
	 * @return	sentencia de trabajo si se crea correctamente, null si hay cualquier error
	 */
	public static Statement usarCrearTablasBD( Connection con, boolean cargar, Tabla tabla ) {
		if (!ACTIVADA) return null;
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			try {
				// Creación de tablas
				String createStat = "create table if not exists Tipo " +
						"(id integer primary key autoincrement" + // Identificador de tipo (número único)
						", nombre varchar(50)" +                  // Nombre de tipo
						", valor varchar(50)" +                   // Valor de tipo
						", fechaUltModif integer" +               // Fecha de última modificación de tipo
						");";
				log( Level.INFO, "BD creación de tabla\t" + createStat, null );
				statement.executeUpdate( createStat );
				String estructuraColumnas = "";
				for (int col=1; col<tabla.getWidth(); col++) {
					String tipoDato = "text";
					if (tabla.getTipos().get(col)==Integer.class) tipoDato = "integer";
					else if (tabla.getTipos().get(col)==Double.class) tipoDato = "real";
					estructuraColumnas += (", " + tabla.getCabecera(col) + " " + tipoDato);
				}
				createStat = "create table if not exists Dato " +
						"(id integer primary key autoincrement" + // Identificador de dato (número único)
						", idTipo integer" +                      // clave externa de tipo (identificador)
						estructuraColumnas +                      // Campos correspondientes a las columnas de la tabla
						");";
				log( Level.INFO, "BD creación de tabla\t" + createStat, null );
				statement.executeUpdate( createStat );
				if (cargar) {
					insertarTipos( statement, tabla );
					insertarDatos( statement, tabla );
				}
			} catch (SQLException e) {} // Tabla ya existe. Nada que hacer
			return statement;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en creación de base de datos", e );
			return null;
		}
	}
	
	/** Borra las tablas de la base de datos. 
	 * UTILIZAR ESTE MËTODO CON PRECAUCIÓN. Borra todos los datos que hubiera ya en las tablas
	 * @param con	Conexión ya creada y abierta a la base de datos
	 */
	public static void borrarBD( Connection con ) {
		if (!ACTIVADA) return;
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			statement.executeUpdate("drop table if exists Tipo");
			statement.executeUpdate("drop table if exists Dato");
			log( Level.INFO, "Reiniciada base de datos", null );
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en reinicio de base de datos", e );
		}
	}
	
	/** Cierra la base de datos abierta
	 * @param con	Conexión abierta de la BD
	 * @param st	Sentencia abierta de la BD
	 */
	public static void cerrarBD( Connection con, Statement st ) {
		if (!ACTIVADA) return;
		try {
			if (st!=null) st.close();
			if (con!=null) con.close();
			log( Level.INFO, "Cierre de base de datos", null );
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en cierre de base de datos", e );
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//                      Operaciones sobre tablas                   //
	/////////////////////////////////////////////////////////////////////
	
	
	
	/** Añade los tipos a la tabla Tipo de BD, usando la sentencia INSERT de SQL desde los datos de la tabla de memoria (primera columna)
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param tabla	Tabla de datos desde la que introducir los tipos
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean insertarTipos( Statement st, Tabla tabla ) {
		if (!ACTIVADA) return false;
		String sentSQL = "";
		try {
			String nombre = tabla.getCabecera(0);
			// Crear lista de valores
			HashSet<String> vals = new HashSet<>();
			for (int fila=0; fila<tabla.size(); fila++) {
				Object val = tabla.get( fila, 0 );
				if (val!=null) {
					vals.add( val.toString() );
				}
			}
			// Comprobar si cada valor existe o no
			for (String valor : vals) {
				sentSQL = "select * from Tipo where nombre='" + nombre + "' and valor ='" + valor + "';";
				ResultSet rs = st.executeQuery( sentSQL );
				log( Level.INFO, "BD tipo buscado\t" + sentSQL, null );
				if (rs.next()) { // Existe: hay que hacer un update
					rs.close();
					sentSQL = "update Tipo set" +
							" fechaUltModif=" + System.currentTimeMillis() +
							" where nombre='" + nombre + "' and valor ='" + valor + "';";
					int val = st.executeUpdate( sentSQL );
					log( Level.INFO, "BD tipo modificado " + val + " fila\t" + sentSQL, null );
					if (val!=1) {  // Se tiene que modificar 1 - error si no
						log( Level.SEVERE, "Error en update de BD\t" + sentSQL, null );
						return false;  
					}
				} else { // No existe: hay que hacer un insert
					rs.close();
					sentSQL = "insert into Tipo (nombre, valor, fechaUltModif) values (" +
							"'" + nombre + "', " +   
							"'" + valor + "', " +
							System.currentTimeMillis() +
							");";
					int val = st.executeUpdate( sentSQL );
					log( Level.INFO, "BD añadida " + val + " fila\t" + sentSQL, null );
					if (val!=1) {  // Se tiene que añadir 1 - error si no
						log( Level.SEVERE, "Error en insert de BD\t" + sentSQL, null );
						return false;  
					}
				}
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			return false;
		}
	}

	/** Añade los datos a la tabla Tipo de BD, usando la sentencia INSERT de SQL desde los datos de la tabla de memoria (primera columna).
	 * Supone que los datos no estaban añadidos (si se vuelve a ejecutar se duplicarán).
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param tabla	Tabla de datos desde la que introducir los tipos
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean insertarDatos( Statement st, Tabla tabla ) {
		if (!ACTIVADA) return false;
		String sentSQL = "";
		try {
			for (int fila=0; fila<tabla.size(); fila++) {
				// Buscar el tipo
				Object valTipo = tabla.get(fila,0);
				if (valTipo==null) continue; // Si el tipo es nulo no se puede insertar
				sentSQL = "select * from Tipo where nombre='" + tabla.getCabecera(0) + "' and valor ='" + valTipo.toString() + "';";
				ResultSet rs = st.executeQuery( sentSQL );
				log( Level.INFO, "BD tipo buscado\t" + sentSQL, null );
				int idTipo = 0;
				if (rs.next()) { // Existe
					idTipo = rs.getInt( "id" );
					rs.close();
				} else { // No existe: no se puede actualizar
					rs.close();
					log( Level.SEVERE, "Error en búsqueda de tipo de BD\t" + sentSQL, null );
					return false;
				}
				
				// Mirar si ya esta el Dato en la base de datos para solo hacer update
				sentSQL = "select * from Dato where idTipo=" + idTipo + " and nick ='" + tabla.get( fila, "nick" ) + "';";
				rs = st.executeQuery( sentSQL );
				if (rs.next()) {
					rs.close();
					// Update el dato
					for ( int i = 1; i < tabla.getWidth(); i++ ) {
						String cabecera = tabla.getCabecera( i );
						if ( !cabecera.equals( "idTipo" ) && !cabecera.equals( "nick" ) ) {
							sentSQL = "update Dato set" + cabecera + "=" + tabla.get( fila, cabecera );
							if ( i < tabla.getWidth() - 1 ) {
								sentSQL = "update Dato set,";
							}
						}
					}
					sentSQL = "update Dato set where idTipo=" + idTipo + " and nick ='" + tabla.get(fila,"nick") + "';";
				} else {
					// Insertar el dato
					String cabs = "";
					for (int col=1; col<tabla.getWidth(); col++) {
						cabs += tabla.getCabecera(col);
						if (col<tabla.getWidth()-1) cabs += ", ";
					}
					sentSQL = "insert into Dato (idTipo, " + cabs +
							") values (" + idTipo + ", ";
					for (int col=1; col<tabla.getWidth(); col++) {
						Object o = tabla.get(fila,col);
						if (o==null) {
							sentSQL += "NULL";
						} else if (tabla.getTipos().get(col)==String.class) {
							sentSQL += ("'" + o + "'");
						} else {
							sentSQL += (o.toString());
						}
						if (col<tabla.getWidth()-1) {
							sentSQL += ", ";
						}
					}
					sentSQL += ");";
					int val = st.executeUpdate( sentSQL );
					log( Level.INFO, "BD añadida " + val + " fila\t" + sentSQL, null );
					if (val!=1) {  // Se tiene que añadir 1 - error si no
						log( Level.SEVERE, "Error en insert de BD\t" + sentSQL, null );
						return false;  
					}	
				}
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			return false;
		}
	}

	/** Modifica un cambio de edición de una valor de una tabla que ya estaba en base de datos (tomando la columna 1 como referencia)
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param tabla	Tabla de datos desde la que tomar el valor modificado
	 * @param fila	valor de la fila del valor que ha sido modificado
	 * @param columna	valor de la columna del valor que ha sido modificado
	 * @return	true si la modificación es correcta, false en caso contrario
	 */
	public static boolean updateValor( Statement st, Tabla tabla, int fila, int columna ) {
		if (!ACTIVADA) return false;
		String sentSQL = "";
		try {
			// Modificar la tabla Dato
			Object o = tabla.get(fila,columna);
			String valorBD = "NULL";
			if (o!=null) {
				if (o instanceof String) {
					valorBD = "'" + o + "'";
				} else {
					valorBD = o.toString();
				}
			}
			sentSQL = "update Dato set " +
					tabla.getCabecera( columna ) + "=" + valorBD +
					" where " + tabla.getCabecera(1) + "='" + tabla.get(fila,1) + "';";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD modificada " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que modificar 1 - error si no
				log( Level.SEVERE, "Error en update de BD\t" + sentSQL, null );
				return false;  
			}
			// Modificar la tabla Tipo
			sentSQL = "update Tipo set" +
					" fechaUltModif=" + System.currentTimeMillis() +
					" where nombre='" + tabla.getCabecera(0) + "' and valor ='" + tabla.get(fila,0) + "';";
			val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD tipo modificado " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que modificar 1 - error si no
				log( Level.SEVERE, "Error en update de BD\t" + sentSQL, null );
				return false;  
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////////
	//                      Logging                                    //
	/////////////////////////////////////////////////////////////////////
	
	private static Logger logger = null;
	
	// Método local para loggear
	private static void log( Level level, String msg, Throwable excepcion ) {
		if (!LOGGING) return;
		if (logger==null) {  // Logger por defecto local:
			logger = Logger.getLogger( BD.class.getName() );  // Nombre del logger - el de la clase
			logger.setLevel( Level.ALL );  // Loguea todos los niveles
		}
		if (excepcion==null)
			logger.log( level, msg );
		else
			logger.log( level, msg, excepcion );
	}
	
}
