package sir.random;

import java.util.Random;

/** Adaptador de un único Random, creado con una semilla explícita por ejecución. */
public final class GeneradorJava implements GeneradorAleatorio {
    private final Random random;

    public GeneradorJava(long semilla) {
        random = new Random(semilla);
    }

    @Override
    public double siguienteDouble() {
        return random.nextDouble();
    }

    @Override
    public int siguienteEntero(int limiteExclusivo) {
        return random.nextInt(limiteExclusivo);
    }
}
