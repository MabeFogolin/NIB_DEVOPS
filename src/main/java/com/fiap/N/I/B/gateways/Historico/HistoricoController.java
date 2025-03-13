package com.fiap.N.I.B.gateways.Historico;

import com.fiap.N.I.B.domains.Historico;
import com.fiap.N.I.B.gateways.requests.HistoricoPatch;
import com.fiap.N.I.B.gateways.responses.HistoricoPostResponse;
import com.fiap.N.I.B.usecases.Historico.HistoricoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/historico")
@RequiredArgsConstructor
public class HistoricoController {

    private final HistoricoService historicoService;

    @Operation(summary = "Inserir novo histórico para um usuário", description = "Endpoint acessível para a inserção de um novo histórico atribuindo um usuário pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Histórico inserido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao inserir histórico")
    })
    @PostMapping("/inserir/{cpfUser}")
    public ResponseEntity<EntityModel<HistoricoPostResponse>> inserirNoHistorico(@PathVariable String cpfUser, @RequestBody Historico historico) {
        HistoricoPostResponse resposta = historicoService.inserirNoHistorico(cpfUser, historico);

        if (resposta.getHistorico() != null) {
            EntityModel<HistoricoPostResponse> entityModel = EntityModel.of(resposta,
                    linkTo(methodOn(HistoricoController.class).buscarHistoricoPorUsuario(cpfUser)).withRel("buscar-historico"),
                    linkTo(methodOn(HistoricoController.class).listarTodos()).withRel("listar-todos"));

            return ResponseEntity.created(linkTo(methodOn(HistoricoController.class).inserirNoHistorico(cpfUser, historico)).toUri())
                    .body(entityModel);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Buscar histórico por CPF do usuário", description = "Endpoint acessível para buscar o histórico de um usuário específico pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico não encontrado para o usuário especificado")
    })
    @GetMapping("/buscar/{cpfUser}")
    public ResponseEntity<EntityModel<Historico>> buscarHistoricoPorUsuario(@PathVariable String cpfUser) {
        Optional<Historico> historico = historicoService.buscarHistoricoPorUsuario(cpfUser);

        return historico.map(h -> {
            EntityModel<Historico> entityModel = EntityModel.of(h,
                    linkTo(methodOn(HistoricoController.class).buscarHistoricoPorUsuario(cpfUser)).withSelfRel(),
                    linkTo(methodOn(HistoricoController.class).listarTodos()).withRel("listar-todos"),
                    linkTo(methodOn(HistoricoController.class).atualizarHistoricoCompleto(cpfUser, h)).withRel("atualizar-historico"),
                    linkTo(methodOn(HistoricoController.class).deletarHistorico(cpfUser)).withRel("deletar-historico"));

            return ResponseEntity.ok(entityModel);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar todo o histórico do usuário", description = "Endpoint acessível para atualizar completamente o histórico de um usuário pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico não encontrado para atualização")
    })
    @PutMapping("/atualizar/{cpfUser}")
    public ResponseEntity<EntityModel<Historico>> atualizarHistoricoCompleto(@PathVariable String cpfUser, @RequestBody Historico historicoParaAtualizar) {
        Optional<Historico> historicoAtualizado = historicoService.atualizarHistoricoCompleto(cpfUser, historicoParaAtualizar);

        return historicoAtualizado.map(h -> {
            EntityModel<Historico> entityModel = EntityModel.of(h,
                    linkTo(methodOn(HistoricoController.class).buscarHistoricoPorUsuario(cpfUser)).withRel("buscar-historico"),
                    linkTo(methodOn(HistoricoController.class).listarTodos()).withRel("listar-todos"));

            return ResponseEntity.ok(entityModel);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar parcialmente o histórico do usuário", description = "Endpoint acessível para atualizar parcialmente o histórico de um usuário pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico parcialmente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico não encontrado para atualização parcial")
    })
    @PatchMapping("/atualizar-parcial/{cpfUser}")
    public ResponseEntity<EntityModel<Historico>> atualizarInformacoesHistorico(@PathVariable String cpfUser, @RequestBody HistoricoPatch historicoPatch) {
        Optional<Historico> historicoAtualizado = historicoService.atualizarInformacoesHistorico(cpfUser, historicoPatch);

        return historicoAtualizado.map(h -> {
            EntityModel<Historico> entityModel = EntityModel.of(h,
                    linkTo(methodOn(HistoricoController.class).buscarHistoricoPorUsuario(cpfUser)).withRel("buscar-historico"),
                    linkTo(methodOn(HistoricoController.class).listarTodos()).withRel("listar-todos"));

            return ResponseEntity.ok(entityModel);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar histórico do usuário por CPF", description = "Endpoint acessível para deletar o histórico de um usuário pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Histórico deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Histórico não encontrado para deleção")
    })
    @DeleteMapping("/deletar/{cpfUser}")
    public ResponseEntity<Void> deletarHistorico(@PathVariable String cpfUser) {
        boolean deletado = historicoService.deletarHistorico(cpfUser);
        if (deletado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @Operation(summary = "Listar todos os históricos", description = "Endpoint acessível para listar todos os históricos no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Históricos encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum histórico encontrado")
    })
    @GetMapping("/todos")
    public ResponseEntity<List<EntityModel<Historico>>> listarTodos() {
        List<EntityModel<Historico>> historicos = historicoService.listarTodos().stream()
                .map(h -> EntityModel.of(h,
                        linkTo(methodOn(HistoricoController.class).buscarHistoricoPorUsuario(String.valueOf(h.getId()))).withRel("buscar-historico")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(historicos);
    }
}
