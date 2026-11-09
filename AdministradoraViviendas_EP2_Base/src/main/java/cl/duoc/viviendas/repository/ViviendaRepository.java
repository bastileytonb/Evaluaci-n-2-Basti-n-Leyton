package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.dao.JsonViviendaDAO;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {

    private final JsonViviendaDAO dao = new JsonViviendaDAO();
    private final ObservableList<Vivienda> viviendas = FXCollections.observableArrayList();

    public ObservableList<Vivienda> getViviendas() {
        return viviendas;
    }

    public void cargar() throws IOException {
        viviendas.addAll(dao.leerDepartamentos());
        viviendas.addAll(dao.leerCasas());
    }

    public void agregar(Vivienda vivienda) throws IOException {
        viviendas.add(vivienda);
        guardar(vivienda);
    }

    public void eliminar(Vivienda vivienda) throws IOException {
        viviendas.remove(vivienda);
        guardar(vivienda);
    }

    private void guardar(Vivienda referencia) throws IOException {
        if (referencia instanceof Departamento) {
            List<Departamento> lista = new ArrayList<>();
            for (Vivienda v : viviendas) {
                if (v instanceof Departamento d) {
                    lista.add(d);
                }
            }
            dao.guardarDepartamentos(lista);
        } else {
            List<Casa> lista = new ArrayList<>();
            for (Vivienda v : viviendas) {
                if (v instanceof Casa c) {
                    lista.add(c);
                }
            }
            dao.guardarCasas(lista);
        }
    }
}
