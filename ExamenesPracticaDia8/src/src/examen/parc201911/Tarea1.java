package src.examen.parc201911;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

import src.examen.parc201911.TablaAnalisis;
import src.examen.parc201911.TablaEstadistica.TipoEstad;
import src.examen.parc201911.TablaEstadistica;

public class Tarea1 {
	
	private TablaEstadistica tablaPrueba;
	private ArrayList<Double> datosPrueba;

	@Before
	public void setUp() throws Exception{
		tablaPrueba = new TablaEstadistica();
		tablaPrueba.addColumna("tipo", "");
		tablaPrueba.addColumna("valor", new Double(0));
		Random r = new Random();
		datosPrueba = new ArrayList<>();
		for ( int i = 0; i < 100; i++ ) {
			tablaPrueba.addDataLine();
			double d = r.nextDouble() * 100.0 - 50.0;
			if ( d < 0 ) {
				tablaPrueba.set( "tipo", "neg" );
			} else {
				tablaPrueba.set( "tipo", "pos" );
			}
			datosPrueba.add( d );
			tablaPrueba.set( "valor", d );
		}
	}
	
	@Test
	public void test() {
		double sumatorioPositivos = 0;
		int nPositivos = 0;
		double sumatorioNegativos = 0;
		int nNegativos = 0;
		for (double d : datosPrueba) {
			if (d<0) {
				sumatorioNegativos += d;
				nNegativos++;
			} else {
				sumatorioPositivos += d;
				nPositivos++;
			}
		}
		double mediaPositivos = sumatorioPositivos / nPositivos;
		double mediaNegativos = sumatorioNegativos / nNegativos;
		
		TablaAnalisis tablaEst = tablaPrueba.creaTablaEstad( TipoEstad.MEDIA, "tipo" );
		assertEquals( 2, tablaEst.size() );
		assertEquals( "pos", tablaEst.get( 1, "tipo" ) );
		assertEquals( "neg", tablaEst.get( 0, "tipo" ) );
		assertEquals( 2, tablaEst.getWidth() );
		assertEquals( "tipo", tablaEst.getCabecera( 0 ) );
		assertEquals( "valor", tablaEst.getCabecera( 1 ) );
		assertEquals( String.class, tablaEst.getTipos().get( 0 ) );
		assertEquals( Double.class, tablaEst.getTipos().get( 1 ) );
		assertEquals( mediaNegativos, ((Double) tablaEst.get( 0, "valor" )).doubleValue(), 0.1 );
		assertEquals( mediaPositivos, ((Double)tablaEst.get( 1, "valor" )).doubleValue(), 0.1 );
	}
	
	
	
}
