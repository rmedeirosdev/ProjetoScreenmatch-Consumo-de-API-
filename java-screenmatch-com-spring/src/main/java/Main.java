import br.com.alura.screenmatch.Produto;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Produto> produtos = Arrays.asList(
                new Produto("Smartphone", 800.0, "Eletrônicos"),
                new Produto("Notebook", 1500.0, "Eletrônicos"),
                new Produto("Teclado", 200.0, "Eletrônicos"),
                new Produto("Cadeira", 300.0, "Móveis"),
                new Produto("Monitor", 900.0, "Eletrônicos"),
                new Produto("Mesa", 700.0, "Móveis"));

        Map<String, Optional<Produto>> produtoMaisCaro = produtos.stream().collect(Collectors.groupingBy(Produto::getCategoria,Collectors.maxBy(Comparator.comparing(Produto::getPreco))));

        System.out.println(produtoMaisCaro);
    }
}
