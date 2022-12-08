package examen.ord202001;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.junit.Before;
import org.junit.Test;

public class T1 {

	// T4
	@Test
	public void testRenderer4Cuartiles() {
		TablaEstadistica tablaEst = new TablaEstadistica();
		tablaEst.addColumna( "A", new Integer( 0 ) );
		tablaEst.addColumna( "B", new Integer( 0 ) );
		tablaEst.addColumna( "C", new Integer( 0 ) );
		tablaEst.addDataLine( new ArrayList<Object>( Arrays.asList( 1, 2, 3 ) ) );
		tablaEst.addDataLine( new ArrayList<Object>( Arrays.asList( 4, 5, 6 ) ));
		tablaEst.addDataLine( new ArrayList<Object>( Arrays.asList( 7, 8, 9 ) ));
		tablaEst.addDataLine( new ArrayList<Object>( Arrays.asList( 10, 11, 12 ) ));
		tablaEst.addDataLine( new ArrayList<Object>( Arrays.asList( 13, 14, 15 ) ));
		VentanaTabla VT = new VentanaTabla( null, "TEST", true );
		VT.setTabla( tablaEst );
		tablaEst.setRenderer4Cuartiles(VT, false, "A", "C", new Color( 0, 0, 0 ), new Color( 0, 0, 255 ), 
				new Color( 255, 0, 255 ), new Color(255, 255, 255) );
		// TEST (INFO) i --> Filas y j --> Columnas
		for ( int i = 0; i < tablaEst.size(); i++ ) {
			for ( int j = 0; j < tablaEst.getWidth(); j++ ) {
				int num = (Integer) tablaEst.get( i, j );
				Color col = getRenderer( VT.getJTable(), i, j ).getBackground();
				String coordenada = "(" + i + "," + j + ")";
				if (num < 4) { // Primer Cuartil
					assertEquals( coordenada, col.getRed(), 0 );
					assertEquals( coordenada, col.getGreen(), 0 );
				} else if (num < 12) { // Tercer Cuartil
					assertEquals( coordenada, col.getBlue(), 255 );
					assertEquals( coordenada, col.getGreen(), 0 );
				} else {
					assertEquals( coordenada, col.getBlue(), 255 );
					assertEquals( coordenada, col.getRed(), 255 );
				}
			}
		}
	}

	// Método de utilidad: devuelve el JLabel que se está usando para hacer el render de la celda fila, col de la JTable indicada
	private JLabel getRenderer( JTable jTable, int fila, int col ) {
		return (JLabel) jTable.prepareRenderer( jTable.getCellRenderer( fila, col ), fila, col );
	}
}
