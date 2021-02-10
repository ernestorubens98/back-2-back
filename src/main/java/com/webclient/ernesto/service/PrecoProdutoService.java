package com.webclient.ernesto.service;

import com.webclient.ernesto.model.ProdutoComPreco;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class PrecoProdutoService {

   @Autowired
   private WebClient webClientProdutos;

   @Autowired
   private WebClient webClientPrecos;

   public ProdutoComPreco obterPorCodigo(Long codigoProduto) {

      // WebClient webClient = WebClient.create("http://localhost:8080"); Uma das
      // formas, mas é bom fazer um bean
      Mono<ProdutoComPreco> monoProduto =  this.webClientProdutos
         .method(HttpMethod.GET)
         .uri("/produtos/{codigo}", codigoProduto)
         .retrieve()
         .bodyToMono(ProdutoComPreco.class);

      Mono<ProdutoComPreco> monoPreco = this.webClientPrecos
         .method(HttpMethod.GET)
         .uri("/precos/{codigo}", codigoProduto)
         .retrieve()
         .bodyToMono(ProdutoComPreco.class);
      
      // ProdutoComPreco produto = monoProduto.block();
      // ProdutoComPreco preco = monoPreco.block();
      // produto.setPreco(preco.getPreco());

      ProdutoComPreco produtoComPreco = Mono.zip(monoProduto, monoPreco).map(tuple -> {
         tuple.getT1().setPreco(tuple.getT2().getPreco());
         return tuple.getT1();
      }).block();

      // monoProduto.subscribe(p -> {
      //    System.out.println("Aqui sim, finalizou de verdade");
      // });

      return produtoComPreco;
   }
}
