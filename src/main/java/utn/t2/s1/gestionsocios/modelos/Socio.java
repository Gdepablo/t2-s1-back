package utn.t2.s1.gestionsocios.modelos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="socio")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank( message = "Error, debe asignar una denominacion")
    @Column(name="denominacion")
    private String denominacion;
    @Column(name="telefono")
    @Pattern(regexp = "^\\+54\s\\d{8,10}$")
    private String telefono;
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name="tipo")
    private TipoSocio tipo;
    @Column(name="mail")
    @Email
    private String mail;
    @Column(name="descripcion")
    @Size(max = 254)
    @Size(min = 1)
    private String descripcion;
    @Column(name="web")
    @URL(message = "la url debe ser https://www.algo.com")
    private String web;
    @DateTimeFormat( pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaAlta; //TODO formatear fecha dd-MM-yyyy
    @Column(name="logo")
    @URL(message = "la url debe ser https://www.algo.com")
    private String logo;
    //@ManyToOne(cascade = CascadeType.PERSIST)
    //@JoinColumn(name = "categoria_id",referencedColumnName = "id" ) //TODO ver de pasar solo la FK en todos los metodos
    //private Categoria categoria;
    //@NotNull
    @ElementCollection(targetClass = Categoria.class)
    @JoinTable(name = "categoria", joinColumns = @JoinColumn(name = "id_socio", referencedColumnName = "id"))
    @Column(name = "categoria", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Categoria> categorias =new HashSet<>(); ;
}
/*
* @ElementCollection(targetClass = Categoria.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "socio_categorias")
    @Column(name = "categoria")
*
* */
