package com.fiap.N.I.B;

import com.fiap.N.I.B.domains.*;
import com.fiap.N.I.B.gateways.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

@RequiredArgsConstructor
@SpringBootApplication
public class Application {

	private final UsuarioRepository usuarioRepository;
	private final EnderecoRepository enderecoRepository;
	private final ProfissionalRepository profissionalRepository;
	private final HistoricoRepository historicoRepository;
	private final DiarioRepository diarioRepository;
	private final ConsultaRepository consultaRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@EventListener(value = ApplicationReadyEvent.class)
	public void setupDados() {
		LocalDate now = LocalDate.now();

		// Criando o usuário
		Usuario usuarioSalvo = Usuario.builder()
				.cpfUser("98684948009")
				.nomeUser("Teste fora")
				.sobrenomeUser("Tchau VM")
				.telefoneUser("1234567890")
				.dataNascimentoUser(LocalDate.of(1990, 1, 1))
				.planoUser("Premium")
				.emailUser("maria.fogolin@example.com")
				.diarios(new ArrayList<>())
				.consultas(new ArrayList<>())
				.build();
		usuarioSalvo = usuarioRepository.save(usuarioSalvo);

		// Criando e vinculando o endereço ao usuário
		Endereco endereco = Endereco.builder()
				.ruaEndereco("Rua 1")
				.numeroEndereco(101)
				.complementoEndereco(null)
				.bairroEndereco("Bairro 1")
				.cidadeEndereco("Cidade 1")
				.cepEndereco("12345-01")
				.estadoEndereco("SP")
				.usuario(usuarioSalvo)
				.build();
		enderecoRepository.save(endereco);

		usuarioSalvo.setEndereco(endereco);
		usuarioSalvo = usuarioRepository.save(usuarioSalvo);

		// Criando e salvando o endereço do profissional
		Endereco endereco2 = Endereco.builder()
				.ruaEndereco("Rua 2")
				.numeroEndereco(102)
				.complementoEndereco("Bloco 2")
				.bairroEndereco("Bairro 2")
				.cidadeEndereco("Cidade 2")
				.cepEndereco("12345-02")
				.estadoEndereco("SP")
				.build();
		enderecoRepository.save(endereco2);

		// Criando o profissional e vinculando o endereço salvo
		Profissional profissional = Profissional.builder()
				.nomeProfissional("João")
				.sobrenomeProfissional("Silva")
				.telefoneProfissional("11987654321")
				.tipoProfissional("Dentista")
				.dataInscricaoProfissional(Date.valueOf(LocalDate.of(2024, 9, 27)))
				.registroProfissional("1234567890")
				.emailProfissional("joao.silva@exemplo.com")
				.endereco(endereco2)
				.consultas(new ArrayList<>())
				.build();
		profissionalRepository.save(profissional);

		endereco2.setProfissional(profissional);
		enderecoRepository.save(endereco2);

		// Criando e vinculando o histórico ao usuário
		Historico historico = Historico.builder()
				.tratamentoHistorico(1)
				.canalHistorico(1)
				.limpezaHistorico(0)
				.carieHistorico(0)
				.ortodonticoHistorico(1)
				.cirurgiaHistorico(1)
				.usuario(usuarioSalvo)
				.build();
		historicoRepository.save(historico);
		usuarioSalvo.setHistorico(historico);
		usuarioRepository.save(usuarioSalvo);

		// Criando e adicionando o diário ao usuário
		Diario diario = Diario.builder()
				.dataRegistro(LocalDate.of(2024, 10, 3))
				.escovacaoDiario(1)
				.usoFioDiario(1)
				.usoEnxaguanteDiario(1)
				.sintomaDiario("Registro com mês diferente")
				.usuario(usuarioSalvo)
				.build();
		usuarioSalvo.getDiarios().add(diario);
		diarioRepository.save(diario);

		// Criando e adicionando a consulta ao usuário e ao profissional
		Consulta consulta = Consulta.builder()
				.dataConsulta(LocalDate.of(2024, 10, 2))
				.descricaoConsulta("Consulta de teste")
				.profissional(profissional)
				.usuario(usuarioSalvo)
				.build();
		usuarioSalvo.getConsultas().add(consulta);
		profissional.getConsultas().add(consulta);
		consultaRepository.save(consulta);

		// Atualizando o profissional e o usuário com as novas consultas
		profissionalRepository.save(profissional);
		usuarioRepository.save(usuarioSalvo);

		System.out.println("Usuário, profissional, histórico, diário e consulta criados com sucesso.");
	}
}
