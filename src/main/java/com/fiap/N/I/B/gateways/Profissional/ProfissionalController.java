package com.fiap.N.I.B.gateways.Profissional;

import com.fiap.N.I.B.domains.Profissional;
import com.fiap.N.I.B.gateways.requests.ProfissionalPatch;
import com.fiap.N.I.B.gateways.responses.ProfissionalPostResponse;
import com.fiap.N.I.B.usecases.Profissional.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/profissional")
@RequiredArgsConstructor
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    @Operation(summary = "Buscar profissional por registro", description = "Endpoint acessível para buscar um profissional pelo registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado para o registro especificado")
    })
    @GetMapping("/registroProfissional/{registroProfissional}")
    public ResponseEntity<EntityModel<Profissional>> buscarPorRegistro(@PathVariable String registroProfissional) {
        Optional<Profissional> profissional = profissionalService.buscarProfissional(registroProfissional);

        return profissional.map(p -> {
            EntityModel<Profissional> resource = EntityModel.of(p);
            Link selfLink = linkTo(methodOn(ProfissionalController.class).buscarPorRegistro(registroProfissional)).withSelfRel();
            Link allProfessionalsLink = linkTo(methodOn(ProfissionalController.class).buscarTodos()).withRel("all-professionals");
            resource.add(selfLink);
            resource.add(allProfessionalsLink);
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar um novo profissional", description = "Endpoint acessível para a criação de um novo profissional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Novo profissional cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito ao cadastrar o novo profissional")
    })
    @PostMapping("/criar")
    public ResponseEntity<EntityModel<ProfissionalPostResponse>> criarProfissional(@RequestBody Profissional profissionalParaCriar) {
        ProfissionalPostResponse respostaCriacao = profissionalService.criarProfissional(profissionalParaCriar);

        EntityModel<ProfissionalPostResponse> resource = EntityModel.of(respostaCriacao);
        Link selfLink = linkTo(methodOn(ProfissionalController.class).buscarPorRegistro(profissionalParaCriar.getRegistroProfissional())).withSelfRel();
        Link allProfessionalsLink = linkTo(methodOn(ProfissionalController.class).buscarTodos()).withRel("all-professionals");

        resource.add(selfLink);
        resource.add(allProfessionalsLink);

        if (respostaCriacao.getMensagem().equals("Novo profissional cadastrado")) {
            return ResponseEntity.status(201).body(resource);
        } else {
            return ResponseEntity.status(409).body(resource);
        }
    }

    @Operation(summary = "Buscar todos os profissionais", description = "Endpoint acessível para buscar todos os profissionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissionais encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum profissional encontrado")
    })
    @GetMapping("/todos")
    public ResponseEntity<List<EntityModel<Profissional>>> buscarTodos() {
        List<Profissional> todosProfissionais = profissionalService.buscarTodos();

        List<EntityModel<Profissional>> todosProfissionaisComLink = todosProfissionais.stream().map(profissional -> {
            EntityModel<Profissional> resource = EntityModel.of(profissional);
            Link selfLink = linkTo(methodOn(ProfissionalController.class).buscarPorRegistro(profissional.getRegistroProfissional())).withSelfRel();
            Link allProfessionalsLink = linkTo(methodOn(ProfissionalController.class).buscarTodos()).withRel("all-professionals");

            resource.add(selfLink);
            resource.add(allProfessionalsLink);

            return resource;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(todosProfissionaisComLink);
    }

    @Operation(summary = "Buscar profissionais por categoria", description = "Endpoint acessível para buscar profissionais por categoria (tipoProfissional)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissionais encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum profissional encontrado para a categoria especificada")
    })
    @GetMapping("/categoria/{tipoProfissional}")
    public ResponseEntity<List<EntityModel<Profissional>>> buscarPorCategoria(@PathVariable String tipoProfissional) {
        List<Profissional> profissionaisPorCategoria = profissionalService.buscarPorCategoria(tipoProfissional);

        List<EntityModel<Profissional>> profissionaisComLink = profissionaisPorCategoria.stream().map(profissional -> {
            EntityModel<Profissional> resource = EntityModel.of(profissional);
            Link selfLink = linkTo(methodOn(ProfissionalController.class).buscarPorRegistro(profissional.getRegistroProfissional())).withSelfRel();
            Link allProfessionalsLink = linkTo(methodOn(ProfissionalController.class).buscarTodos()).withRel("all-professionals");

            resource.add(selfLink);
            resource.add(allProfessionalsLink);

            return resource;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(profissionaisComLink);
    }

    @Operation(summary = "Atualizar profissional", description = "Endpoint acessível para atualizar os dados de um profissional pelo registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado para atualização")
    })
    @PutMapping("/atualizar/{registroProfissional}")
    public ResponseEntity<EntityModel<Profissional>> atualizarProfissional(@PathVariable String registroProfissional,
                                                                           @RequestBody Profissional profissionalParaAtualizar) {
        Optional<Profissional> profissionalAtualizado = profissionalService.atualizarProfissional(registroProfissional, profissionalParaAtualizar);

        return profissionalAtualizado.map(p -> {
            EntityModel<Profissional> resource = EntityModel.of(p);
            Link selfLink = linkTo(methodOn(ProfissionalController.class).buscarPorRegistro(registroProfissional)).withSelfRel();
            Link allProfessionalsLink = linkTo(methodOn(ProfissionalController.class).buscarTodos()).withRel("all-professionals");

            resource.add(selfLink);
            resource.add(allProfessionalsLink);

            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar email e telefone de um profissional", description = "Endpoint acessível para atualizar email e telefone de um profissional pelo registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email e telefone atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado para atualização parcial")
    })
    @PatchMapping("/atualizar-email-telefone/{registroProfissional}")
    public ResponseEntity<EntityModel<Profissional>> atualizarEmailTelefone(@PathVariable String registroProfissional,
                                                                            @RequestBody ProfissionalPatch profissionalPatch) {
        Optional<Profissional> profissionalAtualizado = profissionalService.atualizarEmailTelefone(registroProfissional, profissionalPatch);

        return profissionalAtualizado.map(p -> {
            EntityModel<Profissional> resource = EntityModel.of(p);
            Link selfLink = linkTo(methodOn(ProfissionalController.class).buscarPorRegistro(registroProfissional)).withSelfRel();
            Link allProfessionalsLink = linkTo(methodOn(ProfissionalController.class).buscarTodos()).withRel("all-professionals");

            resource.add(selfLink);
            resource.add(allProfessionalsLink);

            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar profissional", description = "Endpoint acessível para deletar um profissional pelo registro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profissional deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado para deleção")
    })
    @DeleteMapping("/deletar/{registroProfissional}")
    public ResponseEntity<Void> deletarProfissional(@PathVariable String registroProfissional) {
        boolean deletado = profissionalService.deletarProfissional(registroProfissional);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
