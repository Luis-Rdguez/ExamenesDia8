package examen.ord202001;

import java.awt.Color;
import java.awt.geom.Point2D;

public class T4 {
	
	private static double RADIO = 15.0;
	private static double INCREMENTO = 0.1;   // Incremento de tiempo entre prueba y prueba para el choque aproximado
	private static double T_MAXIMO = 100.0;   // Tiempo máximo de prueba para el choque aproximado
	private static double DIST_FINA = 0.0001; // Distancia de error que se quiere afinar para el choque exacto

	public static void main(String[] args) {
		dibuja();
		// T4
		if (buscaChoque(0) >= 0) {
			System.out.println( "Las bolas 1 y 2 chocan aproximadamente en el tiempo " + buscaChoque(0) );
			System.out.println( "Las bolas 1 y 2 chocan exactamente en el tiempo " + buscaChoqueExacto(0, buscaChoque(0)) );
		}else {
			System.out.println("Las bolas 1 y 2 no chocan.");
		}
	}
	
	public static boolean choqueBolas( Point2D.Double b1, Point2D.Double b2 ) {
		return distancia( b1, b2 ) <= 2 * RADIO;
	}
	
	public static double buscaChoque( double tiempoChoque ) {
		if ( tiempoChoque > T_MAXIMO ) {
			return -1;
		} else {
			Point2D.Double b1 = getBola1( tiempoChoque );
			Point2D.Double b2 = getBola2( tiempoChoque );
			if ( choqueBolas( b1, b2 ) ) {
				return tiempoChoque;
			} else {
				return buscaChoque( tiempoChoque + INCREMENTO );
			}
		}
	}
	
	public static double buscaChoqueExacto( double tiempoChoqueInicial, double tiempChoqueFinal ) {
		double tMedio = (tiempoChoqueInicial + tiempChoqueFinal) / 2;	
		Point2D.Double b1 = getBola1( tMedio );
		Point2D.Double b2 = getBola2( tMedio );
		if ( choqueBolas( b1, b2 ) ) {
			double d = distancia( b1, b2 );
			if ( d >= 2 * RADIO - DIST_FINA ) {
				return tMedio;
			} else {
				return buscaChoqueExacto( tiempoChoqueInicial, tiempChoqueFinal );
			}
		} else {
			return buscaChoqueExacto( tMedio, tiempChoqueFinal );
		}
	}
	
	// Distancia entre dos puntos
	public static double distancia( Point2D.Double b1, Point2D.Double b2 ) {
		return Math.sqrt( (b2.x-b1.x)*(b2.x-b1.x) + (b2.y-b1.y)*(b2.y-b1.y) );
	}

	// Ecuación de bola 1
	public static Point2D.Double getBola1( double tiempoSegs ) {
		Point2D.Double ret = new Point2D.Double( tiempoSegs*50, tiempoSegs*50 );
		return ret;
	}

	// Ecuación de bola 2
	public static Point2D.Double getBola2( double tiempoSegs ) {
		Point2D.Double ret = new Point2D.Double( 290 + 100 * Math.sin(tiempoSegs), 295 + 100 * Math.cos(tiempoSegs) );
		return ret;
	}
	
	// Ecuación de bola 3
	public static Point2D.Double getBola3( double tiempoSegs ) {
		Point2D.Double ret = new Point2D.Double( 260 + 200 * Math.cos(tiempoSegs*0.5), 250 + 200 * Math.sin(tiempoSegs*0.5) );
		return ret;
	}
	
	// Dibuja las bolas de billar hasta que se haga click
	private static void dibuja() {
		VentanaGrafica vg = new VentanaGrafica( 800, 600, "Bolas T4" );
		vg.setDibujadoInmediato( false );
		double tiempo = 0.0;
		while (vg.getRatonPulsado()==null && !vg.estaCerrada()) {
			Point2D.Double bola1 = getBola1( tiempo );
			Point2D.Double bola2 = getBola2( tiempo );
			Point2D.Double bola3 = getBola3( tiempo );
			vg.borra();
			vg.dibujaCirculo( bola1.x, bola1.y, RADIO, 2f, Color.blue );
			vg.dibujaCirculo( bola2.x, bola2.y, RADIO, 2f, Color.green );
			vg.dibujaCirculo( bola3.x, bola3.y, RADIO, 2f, Color.red );
			vg.repaint();
			tiempo += 0.1;
			vg.espera( 100 );
		}
		vg.acaba();
	}
	
}
