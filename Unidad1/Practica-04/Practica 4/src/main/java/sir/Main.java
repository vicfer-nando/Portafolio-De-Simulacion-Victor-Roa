package sir;

import sir.controller.SimulacionController;
import sir.controller.ExportadorCSV;
import sir.model.ParametrosSimulacion;
import sir.model.RegistroPaso;
import sir.model.ResultadoSimulacion;
import sir.random.GeneradorJava;
import sir.simulation.MotorSimulacionSIR;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        int codigo = ejecutar(args, System.out, System.err);
        if (codigo != 0) {
            System.exit(codigo);
        }
    }

    static int ejecutar(String[] args, PrintStream salida, PrintStream errores) {
        try {
            Opciones opciones = leerArgumentos(args);
            if (opciones.ayuda()) {
                salida.println("Uso: java -jar target/sir-simulacion.jar [--salida archivo.csv] [--help]");
                salida.println("Los parámetros se editan en ParametrosSimulacion.java; después debes recompilar.");
                salida.println("--help no ejecuta ni crea archivos.");
                salida.println("Salida predeterminada: resultados/resultados_java.csv. Las salidas existentes se sobrescriben.");
                return 0;
            }
            ParametrosSimulacion parametros = ParametrosSimulacion.predeterminados();
            // Una instancia nueva por ejecución; se comparte para inicializar y evolucionar.
            MotorSimulacionSIR motor = new MotorSimulacionSIR(new GeneradorJava(parametros.semilla()));
            SimulacionController controller = new SimulacionController(motor, new ExportadorCSV());
            ResultadoSimulacion resultado = controller.ejecutarYExportar(parametros, opciones.destino());
            mostrarResumen(resultado, opciones.destino(), salida);
            return 0;
        } catch (IllegalArgumentException | IOException | SecurityException e) {
            errores.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private static Opciones leerArgumentos(String[] args) {
        Path destino = Path.of("resultados/resultados_java.csv");
        boolean ayuda = false;
        Set<String> vistos = new HashSet<>();
        for (int i = 0; i < args.length; i++) {
            String argumento = args[i];
            if (!Set.of("--salida", "--help").contains(argumento)) {
                throw new IllegalArgumentException("Argumento desconocido: " + argumento);
            }
            if (!vistos.add(argumento)) {
                throw new IllegalArgumentException("Argumento repetido: " + argumento);
            }
            if (argumento.equals("--help")) {
                ayuda = true;
            } else {
                if (i + 1 >= args.length || args[i + 1].startsWith("--") || args[i + 1].isBlank()) {
                    throw new IllegalArgumentException("Falta el valor de " + argumento);
                }
                destino = Path.of(args[++i]);
            }
        }
        return new Opciones(destino, ayuda);
    }

    private static void mostrarResumen(ResultadoSimulacion resultado, Path destino, PrintStream salida) {
        ParametrosSimulacion p = resultado.parametros();
        RegistroPaso ultimo = resultado.historial().get(resultado.historial().size() - 1);
        RegistroPaso pico = resultado.historial().get(0);
        for (RegistroPaso registro : resultado.historial()) {
            if (registro.infectados() > pico.infectados()) {
                pico = registro;
            }
        }
        salida.println("Simulación SIR completada.");
        salida.println("Población=" + p.poblacion() + ", infectados iniciales=" + p.infectadosIniciales()
                + ", beta=" + p.beta() + ", gamma=" + p.gamma() + ", semilla=" + p.semilla());
        salida.println("Pasos=" + p.tiempoSimulacion() + ", registros=" + resultado.historial().size());
        salida.println("Final: S=" + ultimo.susceptibles() + ", I=" + ultimo.infectados() + ", R=" + ultimo.recuperados());
        salida.println("Máximo de infectados=" + pico.infectados() + ", primer instante=" + pico.tiempo());
        salida.println("Tiempo de cálculo=" + resultado.tiempoCalculoNanosegundos() + " ns");
        salida.println("CSV: " + destino.toAbsolutePath().normalize());
    }

    private record Opciones(Path destino, boolean ayuda) { }
}
