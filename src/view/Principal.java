/*
 * Considere o seguinte cen�rio:
 * Um grande show acontecer� no Brasil, em uma casa com capacidade para 100 pes_
 * soas. A venda ser� feita excluisvamente pelo sistema. Simultaneamente, 300 
 * pessoas, no primeiro instante acessam o sistema de compra. As pessoas podem 
 * comprar de 1 a 4 ingressos por compra, sendo que isso � uma condi��o aleat�_
 * ria. Os passos para a compra s�o: 
 * 1) Login no sistema: Processo que pode demorar de 50 ms a 2 s, sendo que, se 
 * o tempo passar de 1s, ao final do tempo de espera de login, o comprador rece_
 * be uma mensagem de timeout e, por n�o conseguir fazer o login, n�o poder� fa_
 * zer a compra.
 * 2) Processo de compra: Processo que pode demorar de 1 s a 3 s, sendo que, se 
 * o tempo passar de 2,5s, ao final do tempo de espera da compra, o comprador 
 * recebe uma mensagem de final de tempo de sess�o e, por estourar o tempo de 
 * sess�o, n�o poder� fazer a compra.
 * 3) Valida��o da compra: O sistema deve verificar se h� ingressos suficientes 
 * para finalizar a compra. Se houver, faz a compra e subtrai do total de in_
 * gressos dispon�veis. O sistema comunica a venda da quantidade de ingressos 
 * para o usu�rio e a quantidade de ingressos ainda dispon�veis. Se n�o houver a 
 * totalidade dos ingressos disponibiliados, o comprador recebe mensagem sobre a
 * indisponibilidade dos ingressos e, como n�o � poss�vel fracionar a quantidade 
 * pedida, este perde a possibilidade de compra na sess�o.
 */

package view;

import java.util.concurrent.Semaphore;

import controller.ThreadBilheteria;

public class Principal {

	public static void main(String[] args) {
		int permissoes = 1;
		Semaphore mutex = new Semaphore(permissoes);
		
		for(int idComprador = 1; idComprador <= 300; idComprador++) {
			int qntDesejada = (int)((Math.random() * 4) + 1);  //Gerando um valor entre 1 e 4 ingressos para comprar
			Thread tBilheteria = new ThreadBilheteria(idComprador, qntDesejada, mutex);
			tBilheteria.start();
		}
	}

}
