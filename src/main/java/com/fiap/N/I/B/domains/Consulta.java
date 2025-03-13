package com.fiap.N.I.B.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Date;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Schema(description = "Consulta", example = """
{
  "dataConsulta": "2024-10-02",
  "descricaoConsulta": "Consulta  de teste",
  "profissional": {
    "registroProfissional": "0123456789"
  },
  "usuario": {
    "cpfUser": "98684948009"
  }
}
""")
public class Consulta extends RepresentationModel<Consulta> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate dataConsulta;

    @NotNull
    @Size(max = 150, message = "Descrição deve ter no máximo 150 caracteres")
    private String descricaoConsulta;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private Usuario usuario;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private Profissional profissional;

}
