package com.fiap.N.I.B.gateways.Endereco;

import com.fiap.N.I.B.domains.Endereco;
import com.fiap.N.I.B.gateways.requests.EnderecoPatch;
import com.fiap.N.I.B.gateways.responses.EnderecoPostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoServiceImpl enderecoService;

    @Operation(summary = "Criação de um novo endereço", description = "Endpoint acessível para a criação de um novo endereço, atribuindo um usuário pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para criação do endereço")
    })
    @PostMapping("/criar/{idPessoa}")
    public ResponseEntity<EntityModel<EnderecoPostResponse>> criarEndereco(@PathVariable String idPessoa, @RequestBody Endereco endereco) {
        EnderecoPostResponse response = enderecoService.criarEndereco(idPessoa, endereco);

        EntityModel<EnderecoPostResponse> resource = EntityModel.of(response);
        Link selfLink = linkTo(methodOn(EnderecoController.class).buscarEnderecoPorProfissional(idPessoa)).withSelfRel();
        Link allEnderecosLink = linkTo(methodOn(EnderecoController.class).listarTodos()).withRel("all-enderecos");
        resource.add(selfLink);
        resource.add(allEnderecosLink);

        if (response.getEndereco() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resource);
        }
    }

    @Operation(summary = "Atualização parcial de um endereço", description = "Endpoint acessível para atualizar parcialmente um endereço pelo ID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para atualização parcial")
    })
    @PatchMapping("/{idPessoa}")
    public ResponseEntity<EntityModel<Endereco>> atualizarParcial(@PathVariable String idPessoa, @RequestBody EnderecoPatch enderecoPatch) {
        Optional<Endereco> enderecoAtualizado = enderecoService.atualizarParcial(idPessoa, enderecoPatch);

        return enderecoAtualizado.map(endereco -> {
            EntityModel<Endereco> resource = EntityModel.of(endereco);
            Link selfLink = linkTo(methodOn(EnderecoController.class).buscarEnderecoPorUsuario(idPessoa)).withSelfRel();
            Link allEnderecosLink = linkTo(methodOn(EnderecoController.class).listarTodos()).withRel("all-enderecos");
            resource.add(selfLink);
            resource.add(allEnderecosLink);
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Atualização total de um endereço", description = "Endpoint acessível para atualizar completamente um endereço pelo ID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para atualização total")
    })
    @PutMapping("/atualizar/{idPessoa}")
    public ResponseEntity<EntityModel<Endereco>> atualizarTotalmente(@PathVariable String idPessoa, @RequestBody Endereco enderecoParaAtualizar) {
        Optional<Endereco> enderecoAtualizado = enderecoService.atualizarTotalmente(idPessoa, enderecoParaAtualizar);

        return enderecoAtualizado.map(endereco -> {
            EntityModel<Endereco> resource = EntityModel.of(endereco);
            Link selfLink = linkTo(methodOn(EnderecoController.class).buscarEnderecoPorUsuario(idPessoa)).withSelfRel();
            Link allEnderecosLink = linkTo(methodOn(EnderecoController.class).listarTodos()).withRel("all-enderecos");
            resource.add(selfLink);
            resource.add(allEnderecosLink);
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Deletar endereço", description = "Endpoint acessível para deletar um endereço pelo ID do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para deleção")
    })
    @DeleteMapping("/deletar/{idPessoa}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable String idPessoa) {
        boolean deletado = enderecoService.deletarEndereco(idPessoa);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Buscar endereço por profissional", description = "Endpoint acessível para buscar um endereço pelo registro do profissional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o profissional especificado")
    })
    @GetMapping("/profissional/{registroProfissional}")
    public ResponseEntity<EntityModel<Endereco>> buscarEnderecoPorProfissional(@PathVariable String registroProfissional) {
        Optional<Endereco> endereco = enderecoService.buscarEnderecoPorProfissional(registroProfissional);

        return endereco.map(e -> {
            EntityModel<Endereco> resource = EntityModel.of(e);
            Link selfLink = linkTo(methodOn(EnderecoController.class).buscarEnderecoPorProfissional(registroProfissional)).withSelfRel();
            Link allEnderecosLink = linkTo(methodOn(EnderecoController.class).listarTodos()).withRel("all-enderecos");
            resource.add(selfLink);
            resource.add(allEnderecosLink);
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Buscar endereço por usuário", description = "Endpoint acessível para buscar um endereço pelo CPF do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o usuário especificado")
    })
    @GetMapping("/usuario/{cpfUser}")
    public ResponseEntity<EntityModel<Endereco>> buscarEnderecoPorUsuario(@PathVariable String cpfUser) {
        Optional<Endereco> endereco = enderecoService.buscarEnderecoPorUsuario(cpfUser);

        return endereco.map(e -> {
            EntityModel<Endereco> resource = EntityModel.of(e);
            Link selfLink = linkTo(methodOn(EnderecoController.class).buscarEnderecoPorUsuario(cpfUser)).withSelfRel();
            Link allEnderecosLink = linkTo(methodOn(EnderecoController.class).listarTodos()).withRel("all-enderecos");
            resource.add(selfLink);
            resource.add(allEnderecosLink);
            return ResponseEntity.ok(resource);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Listar todos os endereços", description = "Endpoint acessível para listar todos os endereços no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereços encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum endereço encontrado")
    })
    @GetMapping("/todos")
    public ResponseEntity<List<EntityModel<Endereco>>> listarTodos() {
        List<Endereco> enderecos = enderecoService.listarTodos();

        List<EntityModel<Endereco>> enderecosComLink = enderecos.stream().map(endereco -> {
            EntityModel<Endereco> resource = EntityModel.of(endereco);

            if (endereco.getUsuario() != null) {
                Link selfLinkUsuario = linkTo(methodOn(EnderecoController.class)
                        .buscarEnderecoPorUsuario(endereco.getUsuario().getCpfUser()))
                        .withSelfRel();
                resource.add(selfLinkUsuario);
            }

            if (endereco.getProfissional() != null) {
                Link selfLinkProfissional = linkTo(methodOn(EnderecoController.class)
                        .buscarEnderecoPorProfissional(endereco.getProfissional().getRegistroProfissional()))
                        .withSelfRel();
                resource.add(selfLinkProfissional);
            }

            Link allEnderecosLink = linkTo(methodOn(EnderecoController.class).listarTodos()).withRel("all-enderecos");
            resource.add(allEnderecosLink);

            return resource;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(enderecosComLink);
    }
}
