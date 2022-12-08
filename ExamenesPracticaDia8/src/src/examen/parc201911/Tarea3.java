package src.examen.parc201911;

import java.util.ArrayList;

public class Tarea3 {
	
	static int numeroDeBusquedas = 20;
	static int numeroObjetivo = 183;
	
	public static void main(String[] args) {
		int numeroDeSumasDiferentes = encuentraSumas( 1, numeroDeBusquedas, numeroObjetivo, 0, new ArrayList<Integer>() );
		System.out.println( "Se han hayado " + numeroDeSumasDiferentes + " sumas diferentes para conseguir el numero objetivo." );
	}
	
	public static int encuentraSumas( int numeroSiguiente, int numeroMaximo, int numeroObjetivo, int numeroActual, ArrayList<Integer> numerosEmpleados ) {
		if ( numeroSiguiente > numeroMaximo || numeroActual > numeroObjetivo) {
			return 0;
		} if ( numeroActual == numeroObjetivo ) {
			System.out.println( "Suma objetivo " + numeroActual + " hayada con numeros " + numerosEmpleados + "." );
			return 1;
		} else {
			ArrayList<Integer> numerosEmpleados2 = new ArrayList<Integer>( numerosEmpleados );
			numerosEmpleados2.add( numeroSiguiente );
			return encuentraSumas( numeroSiguiente + 1, numeroMaximo, numeroObjetivo, numeroActual, numerosEmpleados ) +
				   encuentraSumas( numeroSiguiente + 1, numeroMaximo, numeroObjetivo, numeroActual + numeroSiguiente, numerosEmpleados2 );
		}
	}
}
