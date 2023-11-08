package utn.t2.s1.gestionsocios.servicios;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.t2.s1.gestionsocios.dtos.EstadoEventoDTO;
import utn.t2.s1.gestionsocios.dtos.EventoDTO;
import utn.t2.s1.gestionsocios.dtos.ParticipanteDTO;
import utn.t2.s1.gestionsocios.modelos.Lugar;
import utn.t2.s1.gestionsocios.modelos.Participante;
import utn.t2.s1.gestionsocios.modelos.Evento;
import utn.t2.s1.gestionsocios.persistencia.Estado;
import utn.t2.s1.gestionsocios.persistencia.EstadoEvento;
import utn.t2.s1.gestionsocios.persistencia.Modalidad;
import utn.t2.s1.gestionsocios.repositorios.EventoRepo;
import java.util.Optional;


@Service
public class EventoServicio {

    @Autowired
    private EventoRepo eventoRepo;

    @Autowired
    private LugarServicio lugarServicio;



    public Page<Evento> buscarTodos(Pageable page) {
        return eventoRepo.findAllByEstado(page , Estado.ACTIVO) ;
    }


    public Evento buscarPorId(Long id) {
        return eventoRepo.findByIdAndEstado(id, Estado.ACTIVO).orElseThrow( () -> new EntityNotFoundException("Evento no encontrado"));
    }



    public Page<Evento> buscarPorNombre(Pageable page, String nombre) {
        return eventoRepo.findByNombreContainsAndEstado(page, nombre, Estado.ACTIVO);
    }

    //filtrado por modalidades y por estado
    public Page<Evento> buscarPorNombreFiltrandoPorModalidadYOEstadoEvento(Pageable page, String nombre,String modalidad, String estadoEvento) {

//        System.out.println("ENTRA ACAAAAAAAAAAAAAAAAAAAAAAA");
//        System.out.println(modalidad.isBlank());
//        System.out.println(modalidad.isEmpty());
//        System.out.println(  (     modalidad == null || modalidad.isBlank()     )  );
//        System.out.println((modalidad == null  ));
//        System.out.println("ENTRA ACAAAAAAAAAAAAAAAAAAAAAAA");


        if (nombre != null && modalidad != null && estadoEvento != null) {

            Modalidad modalidadEnum = Modalidad.valueOf(modalidad);
            EstadoEvento estadoEventoEnum = EstadoEvento.valueOf(estadoEvento);
            return eventoRepo.findByNombreContainsAndModalidadAndEstadoEventoAndEstado(page, nombre, modalidadEnum, estadoEventoEnum, Estado.ACTIVO);

        } else if (nombre == null && modalidad != null && estadoEvento != null) {

            Modalidad modalidadEnum = Modalidad.valueOf(modalidad);
            EstadoEvento estadoEventoEnum = EstadoEvento.valueOf(estadoEvento);
            return eventoRepo.findByModalidadAndEstadoEventoAndEstado(page, modalidadEnum, estadoEventoEnum, Estado.ACTIVO);

        } else if (nombre != null && (modalidad == null  ) && estadoEvento != null) {

//            System.out.println("ENTRA ACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            EstadoEvento estadoEventoEnum = EstadoEvento.valueOf(estadoEvento);
            return eventoRepo.findByNombreContainsAndEstadoEventoAndEstado(page, nombre, estadoEventoEnum, Estado.ACTIVO);

        } else if (nombre != null && modalidad != null && estadoEvento == null) {

            Modalidad modalidadEnum = Modalidad.valueOf(modalidad);
            return eventoRepo.findByNombreContainsAndModalidadAndEstado(page, nombre, modalidadEnum, Estado.ACTIVO);

        } else if (nombre == null && modalidad == null && estadoEvento != null) {

            EstadoEvento estadoEventoEnum = EstadoEvento.valueOf(estadoEvento);
            return eventoRepo.findByEstadoEventoAndEstado(page, estadoEventoEnum, Estado.ACTIVO);

        } else if (nombre == null && modalidad != null && estadoEvento == null) {

            Modalidad modalidadEnum = Modalidad.valueOf(modalidad);
            return eventoRepo.findByModalidadAndEstado(page, modalidadEnum, Estado.ACTIVO);

        } else if (nombre != null && modalidad == null && estadoEvento == null) {

            return eventoRepo.findByNombreContainsAndEstado(page, nombre, Estado.ACTIVO);

        } else {
            return eventoRepo.findAllByEstado(page, Estado.ACTIVO);
        }



//        return eventoRepo.findByModalidadAndEstado(page, modalidad, estadoEvento, Estado.ACTIVO);
    }


    public Evento agregar(EventoDTO eventoDTO) {


//        if (empresaEvento.getSocio() != null && empresaEvento.getOtraEmpresa() != null) {
//            throw new IllegalArgumentException("No se puede asignar un socio y otra empresa al mismo tiempo.");
//        }
//
//        if (empresaEvento.getSocio() == null && empresaEvento.getOtraEmpresa() == null) {
//            throw new IllegalArgumentException("Se debe asignar un socio o una empresa");
//        }

        ModelMapper modelMapper = new ModelMapper();
        Evento evento = modelMapper.map(eventoDTO, Evento.class);


        Lugar lugar = lugarServicio.agregar(eventoDTO.getLugar());

        if (evento.getLugar().getDireccion() == null && evento.getLugar().getLinkVirtual() == null && evento.getLugar().getLinkMaps() == null) {
            throw new IllegalArgumentException("Se debe asignar una dirección, un link de maps o un link virtual");
        }

        evento.setLugar(lugar);


        evento.setEstado(Estado.ACTIVO);

        evento = eventoRepo.save(evento);


  /*      try {
            evento = eventoRepo.save(evento);
        } catch (DataIntegrityViolationException e) {
            // Manejar la excepción de violación de restricción única (rol duplicado)
            throw new IllegalArgumentException("El nombre del participante ya existe en la base de datos.");
        }*/
        return evento;
    }


    public Evento actualizar(EventoDTO eventoDTO, long id) {
        Optional<Evento> optionalEvento = eventoRepo.findById(id);
        if (!optionalEvento.isPresent()) {
            throw new EntityNotFoundException("Evento no encontrado");
        }
        Evento eventoUpdate = optionalEvento.get();

//        ModelMapper modelMapper = new ModelMapper();
//        eventoUpdate = modelMapper.map(eventoDTO, Evento.class);

        eventoUpdate.setLugar(lugarServicio.actualizar(eventoDTO.getLugar(), eventoUpdate.getLugar().getId()));

        eventoUpdate.setNombre(eventoDTO.getNombre());
        eventoUpdate.setFechaInicio(eventoDTO.getFechaInicio());
        eventoUpdate.setFechaFin(eventoDTO.getFechaFin());
        eventoUpdate.setDescripcion(eventoDTO.getDescripcion());
        eventoUpdate.setLinkInscripcion(eventoDTO.getLinkInscripcion());
        eventoUpdate.setEstadoEvento(eventoDTO.getEstadoEvento());
        eventoUpdate.setModalidad(eventoDTO.getModalidad());


        eventoUpdate = eventoRepo.save(eventoUpdate);

        return eventoUpdate;
    }


    public void eliminar(long id) {

        Optional<Evento> optionalParticipante = eventoRepo.findById(id);
        if (!optionalParticipante.isPresent() || optionalParticipante.get().getEstado() == Estado.ELIMINADO) {
            throw new EntityNotFoundException("Participante no encontrado");
        }

        Evento evento = optionalParticipante.get();

        evento.setEstado(Estado.ELIMINADO);

        eventoRepo.save(evento);
    }


    public Evento cambiarEstado(long id, EstadoEventoDTO estadoEventoDTO) {
        Optional<Evento> optionalEvento = eventoRepo.findById(id);
        if (!optionalEvento.isPresent()) {
            throw new EntityNotFoundException("Evento no encontrado");
        }
        Evento eventoUpdate = optionalEvento.get();
//        if (estado.equals("ACTIVO")){
//            eventoUpdate.setEstadoEvento(EstadoEvento.ACTIVO);
//        }
//        if (estado.equals("INACTIVO")){
//            eventoUpdate.setEstadoEvento(EstadoEvento.INACTIVO);
//        }
//        if (estado.equals("INACTIVO")) {
//
//        } else {
//
//        }

        eventoUpdate.setEstadoEvento(estadoEventoDTO.getEstadoEvento());
        eventoUpdate = eventoRepo.save(eventoUpdate);

        return eventoUpdate;
    }

}
