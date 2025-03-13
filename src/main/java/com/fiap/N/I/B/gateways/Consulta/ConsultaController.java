package com.fiap.N.I.B.gateways.Consulta;

import com.fiap.N.I.B.domains.Consulta;
import com.fiap.N.I.B.gateways.requests.ConsultaPatch;
import com.fiap.N.I.B.gateways.responses.ConsultaPostResponse;
import com.fiap.N.I.B.usecases.Consulta.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @Operation(summary = "Cria uma nova Consulta", description = "Endpoint acessível para criação de consultas, atribuindo um usuário e funcionário de acordo com o ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito ao criar a consulta"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para criação da consulta")
    })
    @PostMapping("/criar")
    public ResponseEntity<EntityModel<ConsultaPostResponse>> criarConsulta(@RequestParam String cpfUser,
                                                                           @RequestParam String registroProfissional,
                                                                           @RequestBody Consulta consultaParaInserir) {
        ConsultaPostResponse respostaCriacao = consultaService.criarConsulta(cpfUser, registroProfissional, consultaParaInserir);
        if (respostaCriacao.getMensagem().equals("Nova consulta adicionada")) {
            EntityModel<ConsultaPostResponse> resource = EntityModel.of(respostaCriacao);
            resource.add(linkTo(methodOn(ConsultaController.class).buscarConsultasPorUsuario(cpfUser)).withRel("usuario-consultas"));
            return ResponseEntity.status(201).body(resource);
        } else {
            return ResponseEntity.status(409).body(EntityModel.of(respostaCriacao));
        }
    }

    @Operation(summary = "Buscar consultas por usuário", description = "Endpoint acessível para buscar todas as consultas de um usuário específico pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultas encontradas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma consulta encontrada para o usuário especificado")
    })
    @GetMapping("/usuario")
    public ResponseEntity<CollectionModel<EntityModel<Consulta>>> buscarConsultasPorUsuario(@RequestParam String cpfUser) {
        List<Consulta> consultas = consultaService.consultasPorUsuario(cpfUser);
        if (consultas.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<EntityModel<Consulta>> consultasModel = consultas.stream()
                    .map(consulta -> EntityModel.of(consulta,
                            linkTo(methodOn(ConsultaController.class).buscarConsultasPorUsuario(cpfUser)).withSelfRel(),
                            linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withRel("todas-consultas")
                    ))
                    .collect(Collectors.toList());
            CollectionModel<EntityModel<Consulta>> collectionModel = CollectionModel.of(consultasModel);
            collectionModel.add(linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withRel("todas-consultas"));
            return ResponseEntity.ok(collectionModel);
        }
    }

    @Operation(summary = "Buscar consultas por profissional", description = "Endpoint acessível para buscar todas as consultas de um profissional específico pelo registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultas encontradas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma consulta encontrada para o profissional especificado")
    })
    @GetMapping("/profissional")
    public ResponseEntity<CollectionModel<EntityModel<Consulta>>> buscarConsultasPorProfissional(@RequestParam String registroProfissional) {
        List<Consulta> consultas = consultaService.consultasPorProfissional(registroProfissional);
        if (consultas.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<EntityModel<Consulta>> consultasModel = consultas.stream()
                    .map(consulta -> EntityModel.of(consulta,
                            linkTo(methodOn(ConsultaController.class).buscarConsultasPorProfissional(registroProfissional)).withSelfRel(),
                            linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withRel("todas-consultas")
                    ))
                    .collect(Collectors.toList());
            CollectionModel<EntityModel<Consulta>> collectionModel = CollectionModel.of(consultasModel);
            collectionModel.add(linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withRel("todas-consultas"));
            return ResponseEntity.ok(collectionModel);
        }
    }

    @Operation(summary = "Atualizar uma consulta", description = "Endpoint acessível para atualizar completamente uma consulta pelo CPF do usuário e data da consulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada para atualização")
    })
    @PutMapping("/atualizar")
    public ResponseEntity<EntityModel<Consulta>> atualizarConsulta(@RequestParam String cpfUser,
                                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataConsulta,
                                                                   @RequestBody Consulta consultaParaAtualizar) {
        Optional<Consulta> consultaAtualizada = consultaService.atualizarConsultaTotalmente(cpfUser, dataConsulta, consultaParaAtualizar);
        return consultaAtualizada.map(consulta -> {
            EntityModel<Consulta> resource = EntityModel.of(consulta,
                    linkTo(methodOn(ConsultaController.class).buscarConsultasPorUsuario(cpfUser)).withRel("usuario-consultas"),
                    linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withRel("todas-consultas"));
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar informações específicas de uma consulta", description = "Endpoint acessível para atualizar parcialmente uma consulta pelo CPF do usuário, registro do profissional e data da consulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações da consulta atualizadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada para atualização parcial")
    })
    @PatchMapping("/atualizar-informacoes")
    public ResponseEntity<EntityModel<Consulta>> atualizarInformacoesConsulta(@RequestParam String cpfUser,
                                                                              @RequestParam String registroProfissional,
                                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataConsulta,
                                                                              @RequestBody ConsultaPatch consultaPatch) {
        Optional<Consulta> consultaAtualizada = consultaService.atualizarInformacoesConsulta(cpfUser, registroProfissional, dataConsulta, consultaPatch);
        return consultaAtualizada.map(consulta -> {
            EntityModel<Consulta> resource = EntityModel.of(consulta,
                    linkTo(methodOn(ConsultaController.class).buscarConsultasPorUsuario(cpfUser)).withRel("usuario-consultas"),
                    linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withRel("todas-consultas"));
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar uma consulta", description = "Endpoint acessível para deletar uma consulta pelo CPF do usuário e data da consulta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Consulta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada para deleção")
    })
    @DeleteMapping("/deletar")
    public ResponseEntity<Void> deletarConsulta(@RequestParam String cpfUser,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataConsulta) {
        boolean deletado = consultaService.deletarRegistro(cpfUser, dataConsulta);
        if (deletado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @Operation(summary = "Buscar todos os registros de consultas", description = "Endpoint acessível para buscar todos os registros de consultas no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultas encontradas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhuma consulta encontrada")
    })
    @GetMapping("/todos")
    public ResponseEntity<CollectionModel<EntityModel<Consulta>>> buscarTodasConsultas() {
        List<Consulta> consultas = consultaService.todosRegistros();
        if (consultas.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<EntityModel<Consulta>> consultasModel = consultas.stream()
                    .map(consulta -> EntityModel.of(consulta,
                            linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withSelfRel()
                    ))
                    .collect(Collectors.toList());
            CollectionModel<EntityModel<Consulta>> collectionModel = CollectionModel.of(consultasModel);
            collectionModel.add(linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withSelfRel());
            return ResponseEntity.ok(collectionModel);
        }
    }

    // Buscar consulta por usuário e data
    @GetMapping("/usuario/data")
    public ResponseEntity<EntityModel<Consulta>> buscarConsultaPorData(@RequestParam String cpfUser,
                                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataConsulta) {
        Optional<Consulta> consulta = consultaService.buscarConsultaPorData(cpfUser, dataConsulta);
        return consulta.map(c -> {
            EntityModel<Consulta> resource = EntityModel.of(c,
                    linkTo(methodOn(ConsultaController.class).buscarConsultasPorUsuario(cpfUser)).withRel("usuario-consultas"),
                    linkTo(methodOn(ConsultaController.class).buscarTodasConsultas()).withRel("todas-consultas"));
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
