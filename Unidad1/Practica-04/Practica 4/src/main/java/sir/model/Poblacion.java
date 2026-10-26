package sir.model;

import java.util.List;
import java.util.Objects;

/** Población inmutable; el índice coincide con el identificador y el acceso es O(1). */
public final class Poblacion {
    private final List<Individuo> individuos;

    public Poblacion(List<Individuo> individuos) {
        this.individuos = List.copyOf(individuos);
        if (this.individuos.isEmpty()) {
            throw new IllegalArgumentException("La población no puede estar vacía.");
        }
        for (int i = 0; i < this.individuos.size(); i++) {
            if (this.individuos.get(i).identificador() != i) {
                throw new IllegalArgumentException("Los identificadores deben ser únicos y estar ordenados de 0 a N-1.");
            }
        }
    }

    public List<Individuo> individuos() {
        return individuos;
    }

    public int tamano() {
        return individuos.size();
    }

    public Individuo individuo(int identificador) {
        return individuos.get(identificador);
    }

    public RegistroPaso contar(int tiempo) {
        int s = 0;
        int i = 0;
        int r = 0;
        for (Individuo individuo : individuos) {
            switch (individuo.estado()) {
                case SUSCEPTIBLE -> s++;
                case INFECTADO -> i++;
                case RECUPERADO -> r++;
            }
        }
        if ((long) s + i + r != tamano()) {
            throw new IllegalStateException("No se conserva la población.");
        }
        return new RegistroPaso(tiempo, s, i, r);
    }

    /** Se ejecuta siempre, sin depender de que estén habilitadas las aserciones de Java. */
    public void verificarTransicionDesde(Poblacion anterior) {
        Objects.requireNonNull(anterior);
        if (tamano() != anterior.tamano()) {
            throw new IllegalStateException("La población debe permanecer cerrada.");
        }
        for (int id = 0; id < tamano(); id++) {
            EstadoSalud antes = anterior.individuo(id).estado();
            EstadoSalud despues = individuo(id).estado();
            boolean permitida = antes == despues
                    || antes == EstadoSalud.SUSCEPTIBLE && despues == EstadoSalud.INFECTADO
                    || antes == EstadoSalud.INFECTADO && despues == EstadoSalud.RECUPERADO;
            if (!permitida) {
                throw new IllegalStateException("Transición SIR inválida para el individuo " + id);
            }
        }
        RegistroPaso antes = anterior.contar(0);
        RegistroPaso despues = contar(0);
        if (despues.susceptibles() > antes.susceptibles() || despues.recuperados() < antes.recuperados()) {
            throw new IllegalStateException("No se cumple la monotonicidad de S y R.");
        }
    }
}
