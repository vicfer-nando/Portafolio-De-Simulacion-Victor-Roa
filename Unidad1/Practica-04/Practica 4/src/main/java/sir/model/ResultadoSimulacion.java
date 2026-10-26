package sir.model;

import java.util.List;
import java.util.Objects;

public record ResultadoSimulacion(ParametrosSimulacion parametros,
                                  List<RegistroPaso> historial, long tiempoCalculoNanosegundos) {
    public ResultadoSimulacion {
        Objects.requireNonNull(parametros);
        historial = List.copyOf(historial);
        if (tiempoCalculoNanosegundos < 0 || historial.size() != (long) parametros.tiempoSimulacion() + 1) {
            throw new IllegalArgumentException("Duración o longitud del historial inválida.");
        }
        RegistroPaso anterior = null;
        for (int t = 0; t < historial.size(); t++) {
            RegistroPaso registro = historial.get(t);
            if (registro.tiempo() != t
                    || (long) registro.susceptibles() + registro.infectados() + registro.recuperados() != parametros.poblacion()
                    || anterior != null && (registro.susceptibles() > anterior.susceptibles()
                    || registro.recuperados() < anterior.recuperados())) {
                throw new IllegalArgumentException("Historial inconsistente en el instante " + t);
            }
            anterior = registro;
        }
        RegistroPaso inicial = historial.get(0);
        if (inicial.susceptibles() != parametros.poblacion() - parametros.infectadosIniciales()
                || inicial.infectados() != parametros.infectadosIniciales() || inicial.recuperados() != 0) {
            throw new IllegalArgumentException("El registro inicial no coincide con los parámetros.");
        }
    }
}
