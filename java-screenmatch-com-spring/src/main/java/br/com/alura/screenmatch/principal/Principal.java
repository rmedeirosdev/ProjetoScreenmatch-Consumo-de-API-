package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import ch.qos.logback.core.encoder.JsonEscapeUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner LEITURA = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = LEITURA.nextLine();

        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        List<DadosEpisodio> episodios = temporadas.stream().flatMap(t -> t.episodios().stream()).toList();

//        System.out.println("\n \n \n \n Top 5 Episódios \n \n");
//
//        episodios.stream().filter(e -> !e.avaliacao().equalsIgnoreCase("N/A")).sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed()).limit(5).map(e -> e.titulo().toUpperCase()).forEach(System.out::println);

        List<Episodio> episodio = temporadas.stream().flatMap(t -> t.episodios().stream().map(d -> new Episodio(t.numero(), d))).collect(Collectors.toList());
        episodio.forEach(System.out::println);

//        System.out.println("Digite o nome do episódio ou trecho");
//        var nomeEpisodio = LEITURA.nextLine();
//        LEITURA.close();
//        Optional<Episodio> buscado = episodio.stream().filter((e -> e.getTitulo().toUpperCase().contains(nomeEpisodio.toUpperCase()))).findFirst();
//
//        if (buscado.isPresent()) {
//            System.out.println("Título: " + buscado.get().getTitulo() + " Temporada: " + buscado.get().getTemporada().toString());
//        } else {
//            System.out.println("Episodio nao encontrado");
//        }

// 5 melhores avaliados: episodio.stream().filter(e -> !e.getAvaliacao().equals(0.0)).sorted(Comparator.comparingDouble(Episodio::getAvaliacao).reversed()).limit(5).map(e -> e.getTitulo().toUpperCase()).collect(Collectors.toList());

//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        var ano = LEITURA.nextInt();
//        LEITURA.nextLine();
//
//        LocalDate data = LocalDate.of(ano, 1, 1);
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodio.stream().filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(data))
//                .forEach(e -> System.out.println("Temporada: " + e.getTemporada() + " Episódio: " + e.getTitulo() + " Data lançamento: " + e.getDataLancamento().format(formatador)));


        System.out.println("Avaliação por temporada!");
        Map<Integer, Double> avaliacoesPorTemporada = episodio.stream().filter(e -> e.getAvaliacao() > 0.0).collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodio.stream().filter(e -> e.getAvaliacao() > 0.0).collect(Collectors.summarizingDouble(Episodio::getAvaliacao));


        System.out.println("Media: " + est.getAverage());
        System.out.println("Melhor Episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Total de avaliações: " + est.getCount());

    }
}