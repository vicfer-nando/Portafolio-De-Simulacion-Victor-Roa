package sir.random;

public interface GeneradorAleatorio {
    /** Uniforme en [0,1). */
    double siguienteDouble();

    /** Uniforme en [0, limiteExclusivo). */
    int siguienteEntero(int limiteExclusivo);
}
