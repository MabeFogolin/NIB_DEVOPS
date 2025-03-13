package com.fiap.N.I.B.gateways.Diario;

import com.fiap.N.I.B.domains.Diario;
import com.fiap.N.I.B.gateways.requests.DiarioPatch;
import com.fiap.N.I.B.gateways.responses.DiarioPostResponse;
import com.fiap.N.I.B.usecases.Diario.DiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/diario")
@RequiredArgsConstructor
public class DiarioController {

    private final DiarioService diarioService;

    @Operation(summary = "Criar novo registro no diário", description = "Endpoint acessível para criação de registros no diário, atribuindo um usuário pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito ao criar o registro"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para criação do registro")
    })
    @PostMapping("/criar")
    public ResponseEntity<EntityModel<DiarioPostResponse>> criarRegistro(
            @RequestParam String cpfUser,
            @RequestBody Diario diarioParaCriar) {
        DiarioPostResponse respostaCriacao = diarioService.inserirNoDiario(cpfUser, diarioParaCriar);
        if (respostaCriacao.getMensagem().equals("Novo registro adicionado ao diário")) {
            EntityModel<DiarioPostResponse> resource = EntityModel.of(respostaCriacao);
            resource.add(linkTo(methodOn(DiarioController.class).buscarRegistrosPorUsuario(cpfUser)).withRel("usuario-diario"));
            return ResponseEntity.status(201).body(resource);
        } else {
            return ResponseEntity.status(409).body(EntityModel.of(respostaCriacao));
        }
    }

    @Operation(summary = "Buscar registros do diário por usuário", description = "Endpoint acessível para buscar todos os registros de um usuário específico pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum registro encontrado para o usuário especificado")
    })
    @GetMapping("/usuario")
    public ResponseEntity<CollectionModel<EntityModel<Diario>>> buscarRegistrosPorUsuario(@RequestParam String cpfUser) {
        List<Diario> registros = diarioService.buscarRegistrosPorUsuario(cpfUser);
        if (registros.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<EntityModel<Diario>> registrosModel = registros.stream()
                    .map(diario -> EntityModel.of(diario,
                            linkTo(methodOn(DiarioController.class).buscarRegistrosPorUsuario(cpfUser)).withSelfRel(),
                            linkTo(methodOn(DiarioController.class).buscarTodosRegistros()).withRel("todos-diarios")
                    ))
                    .collect(Collectors.toList());
            CollectionModel<EntityModel<Diario>> collectionModel = CollectionModel.of(registrosModel);
            collectionModel.add(linkTo(methodOn(DiarioController.class).buscarTodosRegistros()).withRel("todos-diarios"));
            return ResponseEntity.ok(collectionModel);
        }
    }

    @Operation(summary = "Buscar todos os registros do diário", description = "Endpoint acessível para buscar todos os registros do diário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registros encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum registro encontrado")
    })
    @GetMapping("/todos")
    public ResponseEntity<CollectionModel<EntityModel<Diario>>> buscarTodosRegistros() {
        List<Diario> registros = diarioService.buscarTodos();
        if (registros.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            List<EntityModel<Diario>> registrosModel = registros.stream()
                    .map(diario -> EntityModel.of(diario,
                            linkTo(methodOn(DiarioController.class).buscarTodosRegistros()).withSelfRel()
                    ))
                    .collect(Collectors.toList());
            CollectionModel<EntityModel<Diario>> collectionModel = CollectionModel.of(registrosModel);
            collectionModel.add(linkTo(methodOn(DiarioController.class).buscarTodosRegistros()).withSelfRel());
            return ResponseEntity.ok(collectionModel);
        }
    }

    @Operation(summary = "Atualizar um registro do diário", description = "Endpoint acessível para atualizar completamente um registro do diário pelo CPF do usuário e data do registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado para atualização")
    })
    @PutMapping("/atualizar")
    public ResponseEntity<EntityModel<Diario>> atualizarRegistro(
            @RequestParam String cpfUser,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataRegistro,
            @RequestBody Diario diarioParaAtualizar) {
        Optional<Diario> diarioAtualizado = diarioService.atualizarRegistro(cpfUser, dataRegistro, diarioParaAtualizar);
        if (diarioAtualizado.isPresent()) {
            EntityModel<Diario> resource = EntityModel.of(diarioAtualizado.get(),
                    linkTo(methodOn(DiarioController.class).buscarRegistrosPorUsuario(cpfUser)).withRel("usuario-diario"),
                    linkTo(methodOn(DiarioController.class).buscarTodosRegistros()).withRel("todos-diarios"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualizar informações específicas de um registro", description = "Endpoint acessível para atualizar parcialmente um registro do diário pelo CPF do usuário e data do registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do registro atualizadas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado para atualização parcial")
    })
    @PatchMapping("/atualizar-informacoes")
    public ResponseEntity<EntityModel<Diario>> atualizarInformacoesRegistro(
            @RequestParam String cpfUser,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataRegistro,
            @RequestBody DiarioPatch diarioPatch) {
        Optional<Diario> diarioAtualizado = diarioService.atualizarInformacoesRegistro(cpfUser, dataRegistro, diarioPatch);
        if (diarioAtualizado.isPresent()) {
            EntityModel<Diario> resource = EntityModel.of(diarioAtualizado.get(),
                    linkTo(methodOn(DiarioController.class).buscarRegistrosPorUsuario(cpfUser)).withRel("usuario-diario"),
                    linkTo(methodOn(DiarioController.class).buscarTodosRegistros()).withRel("todos-diarios"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deletar um registro do diário", description = "Endpoint acessível para deletar um registro do diário pelo CPF do usuário e data do registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado para deleção")
    })
    @DeleteMapping("/deletar")
    public ResponseEntity<Void> deletarRegistro(
            @RequestParam String cpfUser,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataRegistro) {
        boolean deletado = diarioService.deletarRegistro(cpfUser, dataRegistro);
        if (deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
