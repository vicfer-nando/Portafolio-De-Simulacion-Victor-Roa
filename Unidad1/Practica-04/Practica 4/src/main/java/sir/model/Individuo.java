package sir.model;

import java.util.Objects;

public record Individuo(int identificador, EstadoSalud estado) {
    public Individuo {
        if (identificador < 0) {
            throw new IllegalArgumentException("El identificador no puede ser negativo.");
        }
        Objects.requireNonNull(estado, "El estado es obligatorio.");
    }
}
