package utn.t2.s1.gestionsocios.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utn.t2.s1.gestionsocios.modelos.Socio;
import utn.t2.s1.gestionsocios.repositorios.SocioRepo;

@Service
public class SocioServicio {
    @Autowired
    private SocioRepo repo;

    public Socio buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Page<Socio> buscarTodos(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Socio agregar(Socio socio) {
        return repo.save(socio);
    }

    public void borrar(Long id) {
        repo.delete(this.buscarPorId(id));
    }

    public Socio modificar(Long id, Socio socio) {
        //TODO chequear si es nulo
        socio.setId(id);
        return repo.save(socio);
    }

}