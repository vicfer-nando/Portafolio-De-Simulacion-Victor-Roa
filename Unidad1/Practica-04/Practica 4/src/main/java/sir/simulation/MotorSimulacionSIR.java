package sir.simulation;

import sir.model.EstadoSalud;
import sir.model.Individuo;
import sir.model.ParametrosSimulacion;
import sir.model.Poblacion;
import sir.model.RegistroPaso;
import sir.model.ResultadoSimulacion;
import sir.random.GeneradorAleatorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MotorSimulacionSIR {
    private final GeneradorAleatorio aleatorio;

    public MotorSimulacionSIR(GeneradorAleatorio aleatorio) {
        this.aleatorio = Objects.requireNonNull(aleatorio);
    }

    public ResultadoSimulacion ejecutar(ParametrosSimulacion parametros) {
        Objects.requireNonNull(parametros);
        long inicio = System.nanoTime();
        Poblacion actual = inicializar(parametros);
        List<RegistroPaso> historial = new ArrayList<>();
        for (int tiempo = 0; ; tiempo++) {
            historial.add(actual.contar(tiempo));
            if (tiempo == parametros.tiempoSimulacion()) {
                break;
            }
            Poblacion siguiente = actualizar(actual, parametros);
            siguiente.verificarTransicionDesde(actual);
            actual = siguiente;
        }
        long duracion = System.nanoTime() - inicio;
        return new ResultadoSimulacion(parametros, historial, duracion);
    }

    Poblacion inicializar(ParametrosSimulacion parametros) {
        int n = parametros.poblacion();
        List<Individuo> individuos = new ArrayList<>(n);
        int[] identificadores = new int[n];
        for (int id = 0; id < n; id++) {
            individuos.add(new Individuo(id, EstadoSalud.SUSCEPTIBLE));
            identificadores[id] = id;
        }
        // Fisher-Yates completo, incluso si hay cero infectados iniciales.
        for (int i = n - 1; i >= 1; i--) {
            int j = aleatorio.siguienteEntero(i + 1);
            int auxiliar = identificadores[i];
            identificadores[i] = identificadores[j];
            identificadores[j] = auxiliar;
        }
        for (int i = 0; i < parametros.infectadosIniciales(); i++) {
            int id = identificadores[i];
            individuos.set(id, new Individuo(id, EstadoSalud.INFECTADO));
        }
        return new Poblacion(individuos);
    }

    private Poblacion actualizar(Poblacion actual, ParametrosSimulacion parametros) {
        List<Individuo> siguiente = new ArrayList<>(actual.individuos());
        for (Individuo individuo : actual.individuos()) {
            if (individuo.estado() != EstadoSalud.INFECTADO) {
                continue;
            }
            int contacto = aleatorio.siguienteEntero(actual.tamano());
            // Consultar siempre actual, aunque otro infectado ya haya escrito en siguiente.
            if (actual.individuo(contacto).estado() == EstadoSalud.SUSCEPTIBLE) {
                if (aleatorio.siguienteDouble() < parametros.beta()) {
                    siguiente.set(contacto, new Individuo(contacto, EstadoSalud.INFECTADO));
                }
            }
            if (aleatorio.siguienteDouble() < parametros.gamma()) {
                siguiente.set(individuo.identificador(),
                        new Individuo(individuo.identificador(), EstadoSalud.RECUPERADO));
            }
        }
        return new Poblacion(siguiente);
    }
}
