package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonViviendaDAO {

    private final Path archivoDepartamentos = Path.of("departamentos.json");
    private final Path archivoCasas = Path.of("casas.json");
    private final Gson gson = new Gson();

    public List<Departamento> leerDepartamentos() throws IOException {
        if (Files.notExists(archivoDepartamentos)) {
            throw new IOException("No se encontró departamentos.json.");
        }
        try (Reader reader = Files.newBufferedReader(archivoDepartamentos, StandardCharsets.UTF_8)) {
            Departamento[] datos = gson.fromJson(reader, Departamento[].class);
            return datos == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(datos));
        } catch (JsonParseException e) {
            throw new IOException("departamentos.json tiene un formato inválido.");
        }
    }

    public List<Casa> leerCasas() throws IOException {
        if (Files.notExists(archivoCasas)) {
            throw new IOException("No se encontró casas.json.");
        }
        try (Reader reader = Files.newBufferedReader(archivoCasas, StandardCharsets.UTF_8)) {
            Casa[] datos = gson.fromJson(reader, Casa[].class);
            return datos == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(datos));
        } catch (JsonParseException e) {
            throw new IOException("casas.json tiene un formato inválido.");
        }
    }

    public void guardarDepartamentos(List<Departamento> departamentos) throws IOException {
        try (Writer writer = Files.newBufferedWriter(archivoDepartamentos, StandardCharsets.UTF_8)) {
            gson.toJson(departamentos, writer);
        }
    }

    public void guardarCasas(List<Casa> casas) throws IOException {
        try (Writer writer = Files.newBufferedWriter(archivoCasas, StandardCharsets.UTF_8)) {
            gson.toJson(casas, writer);
        }
    }
}
