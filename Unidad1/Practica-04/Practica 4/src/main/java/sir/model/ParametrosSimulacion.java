package sir.model;

public record ParametrosSimulacion(int poblacion, int infectadosIniciales,
                                  double beta, double gamma, int tiempoSimulacion, long semilla) {
    public static final int POBLACION = 100;
    public static final int INFECTADOS_INICIALES = 5;
    public static final double BETA = 0.30;
    public static final double GAMMA = 0.10;
    public static final int TIEMPO_SIMULACION = 100;
    public static final long SEMILLA = 42L;

    public ParametrosSimulacion {
        if (poblacion <= 0) {
            throw new IllegalArgumentException("poblacion debe ser positiva.");
        }
        if (infectadosIniciales < 0 || infectadosIniciales > poblacion) {
            throw new IllegalArgumentException("infectadosIniciales debe estar entre 0 y poblacion.");
        }
        validarProbabilidad("beta", beta);
        validarProbabilidad("gamma", gamma);
        if (tiempoSimulacion < 0 || tiempoSimulacion == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "tiempoSimulacion debe estar entre 0 y 2147483646 para representar T+1 registros.");
        }
    }

    private static void validarProbabilidad(String clave, double valor) {
        if (!Double.isFinite(valor) || valor < 0 || valor > 1) {
            throw new IllegalArgumentException(clave + " debe ser finito y estar en [0,1].");
        }
    }

    public static ParametrosSimulacion predeterminados() {
        return new ParametrosSimulacion(POBLACION, INFECTADOS_INICIALES,
                BETA, GAMMA, TIEMPO_SIMULACION, SEMILLA);
    }
}
