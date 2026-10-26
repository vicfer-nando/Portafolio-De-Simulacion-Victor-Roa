package sir.model;

public record RegistroPaso(int tiempo, int susceptibles, int infectados, int recuperados) {
    public RegistroPaso {
        if (tiempo < 0 || susceptibles < 0 || infectados < 0 || recuperados < 0) {
            throw new IllegalArgumentException("El tiempo y los conteos deben ser no negativos.");
        }
    }
}
