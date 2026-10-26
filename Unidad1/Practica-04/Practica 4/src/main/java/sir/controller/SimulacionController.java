package sir.controller;

import sir.model.ParametrosSimulacion;
import sir.model.ResultadoSimulacion;
import sir.simulation.MotorSimulacionSIR;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class SimulacionController {
    private final MotorSimulacionSIR motor;
    private final ExportadorCSV exportadorCSV;

    public SimulacionController(MotorSimulacionSIR motor, ExportadorCSV exportadorCSV) {
        this.motor = Objects.requireNonNull(motor);
        this.exportadorCSV = Objects.requireNonNull(exportadorCSV);
    }

    public ResultadoSimulacion ejecutarYExportar(ParametrosSimulacion parametros, Path destinoCsv) throws IOException {
        ResultadoSimulacion resultado = motor.ejecutar(parametros);
        try {
            exportadorCSV.exportar(resultado, destinoCsv);
        } catch (IOException e) {
            throw new IOException("No se pudo exportar el CSV a " + destinoCsv + ": " + e.getMessage(), e);
        }
        return resultado;
    }
}
