package com.fiap.N.I.B.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Schema(description = "Diário do usuário", example = """
{
    "dataRegistro": "2024-10-03",
    "escovacaoDiario": 1,
    "usoFioDiario": 1,
    "usoEnxaguanteDiario": 1,
    "sintomaDiario": "Registro com mês diferente"
}
""")
public class Diario extends RepresentationModel<Diario> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate dataRegistro;

    @NotNull
    @Max(99)
    private int escovacaoDiario;

    @NotNull
    @Max(99)
    private int usoFioDiario;

    @NotNull
    @Max(99)
    private int usoEnxaguanteDiario;


    @Size(max = 30, message = "Sintoma deve ter no máximo 30 caracteres")
    private String sintomaDiario;

    @ManyToOne
    @JsonIgnore
    private Usuario usuario;
}
