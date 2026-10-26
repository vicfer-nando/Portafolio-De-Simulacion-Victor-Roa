package sir.controller;

import sir.model.RegistroPaso;
import sir.model.ResultadoSimulacion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExportadorCSV {
    public void exportar(ResultadoSimulacion resultado, Path destino) throws IOException {
        Files.createDirectories(destino.toAbsolutePath().getParent());
        try (BufferedWriter escritor = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            escritor.write("tiempo,susceptibles,infectados,recuperados\n");
            for (RegistroPaso registro : resultado.historial()) {
                escritor.write(registro.tiempo() + "," + registro.susceptibles() + ","
                        + registro.infectados() + "," + registro.recuperados() + "\n");
            }
        }
    }
}
