package com.laboratorio.services.impl;

import com.laboratorio.model.Auditoria;
import com.laboratorio.model.Paciente;
import com.laboratorio.repository.PacienteRepository;
import com.laboratorio.service.PacienteService;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    @Autowired
    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    private void validarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        if (email.contains(" ")) {
            throw new IllegalArgumentException("El correo no debe contener espacios");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Falta el símbolo @");
        }

        String[] partes = email.split("@");

        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato inválido de correo");
        }

        if (partes[0].isBlank()) {
            throw new IllegalArgumentException("Falta nombre de usuario antes del @");
        }

        if (!partes[1].matches(".*\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("El dominio debe incluir extensión válida (ej: gmail.com)");
        }

        if (partes[1].startsWith(".")) {
            throw new IllegalArgumentException("Dominio inválido");
        }
    }

    @Override
    public List<Paciente> getAll() {
        return pacienteRepository.findAll();
    }

    @Override
    public Paciente get(Paciente paciente) {
        return pacienteRepository.findById(paciente.getIdPaciente()).orElse(null);
    }

    @Override
    @Transactional
    public void save(Paciente paciente) {

        boolean esNuevo = paciente.getIdPaciente() == null
                || !pacienteRepository.existsById(paciente.getIdPaciente());

        if (paciente.getCedula() == null || !paciente.getCedula().matches("\\d{9}")) {
            throw new IllegalArgumentException("La cédula debe tener exactamente 9 dígitos");
        }

        if (paciente.getEmail() != null && !paciente.getEmail().isBlank()) {

            paciente.setEmail(paciente.getEmail().trim());

            if (!paciente.getEmail().equals(paciente.getEmail().toLowerCase())) {
                throw new IllegalArgumentException("El correo debe estar en minúsculas");
            }

            paciente.setEmail(paciente.getEmail().toLowerCase());

            validarEmail(paciente.getEmail());
        }

        if (esNuevo) {

            if (pacienteRepository.existsByCedula(paciente.getCedula())) {
                throw new IllegalArgumentException("La cédula ya está registrada");
            }

            if (pacienteRepository.existsByTelefono(paciente.getTelefono())) {
                throw new IllegalArgumentException("El teléfono ya está registrado");
            }

            if (paciente.getEmail() != null && !paciente.getEmail().isBlank()
                    && pacienteRepository.existsByEmail(paciente.getEmail())) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }

        } else {

            Paciente existente = pacienteRepository.findById(paciente.getIdPaciente())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

            if (!paciente.getCedula().equals(existente.getCedula())
                    && pacienteRepository.existsByCedula(paciente.getCedula())) {
                throw new IllegalArgumentException("La cédula ya está registrada");
            }

            if (!paciente.getTelefono().equals(existente.getTelefono())
                    && pacienteRepository.existsByTelefono(paciente.getTelefono())) {
                throw new IllegalArgumentException("El teléfono ya está registrado");
            }

            if (paciente.getEmail() != null && !paciente.getEmail().isBlank()
                    && !paciente.getEmail().equals(existente.getEmail())
                    && pacienteRepository.existsByEmail(paciente.getEmail())) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }
        }

        if (esNuevo) {
            paciente.setFechaCreacion(new Date());
        }

        pacienteRepository.save(paciente);
    }

    @Override
    public void delete(Paciente paciente) {
        pacienteRepository.delete(paciente);
    }

    @Override
    public int getMaxSequenceForYear(String anio) {
        return pacienteRepository.getMaxSequenceForYear(anio);
    }

    @Override
    public List<Paciente> buscarPacientes(String query) {

        return pacienteRepository.buscarActivosPorQuery(query);
    }

    @Override
    public List<Paciente> buscarPacientesInactivos(String query) {
        return pacienteRepository.buscarInactivosPorQuery(query);
    }

    @Override
    public Paciente getPaciente(String id) {
        return pacienteRepository.findByIdPaciente(id);
    }

    @Override
    public List<Paciente> getPacientesActivos() {
        return pacienteRepository.findByActivoTrue();
    }

    @Override
    public List<Paciente> getPacientesInactivos() {
        return pacienteRepository.findByActivoFalse();
    }

    @Override
    public Paciente get(String id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con id: " + id));
    }

    private void registrarAuditoria(String accion, Paciente paciente) {
        System.out.println("AUDITORÍA → Acción: " + accion + ", Paciente ID: " + paciente.getIdPaciente());
    }

}
