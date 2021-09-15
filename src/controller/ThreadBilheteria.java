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

package controller;

import java.util.concurrent.Semaphore;

public class ThreadBilheteria extends Thread {

	private static int quantMaxIng = 100;  //Quantidade total de ingressos para o evento
	private int idComprador;
	private int qntDesejada;  //Quantidade de ingressos desejada por cada comprador (entre 1 e 4)
	private Semaphore semaforo;
	
	public ThreadBilheteria(int idComprador, int qntDesejada, Semaphore semaforo) {
		this.idComprador = idComprador;
		this.qntDesejada = qntDesejada;
		this.semaforo = semaforo;
	}

	@Override
	public void run() {
		loginSistema();		
	}

	private void loginSistema() {
		/*Login no Sistema*/
		int tempoLogin = (int)((Math.random() * 1951) + 50);  //Processo leva de 50 ms a 2 s
		try {
			sleep(tempoLogin);  //Simula��o do tempo para realiza��o do processo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(tempoLogin > 1000) {  
			/*Caso leve mais de 1 s, o comprador n�o faz login e n�o realiza a compra*/
			System.err.println("Comprador #"+idComprador+": Tempo de login excedido!");
		} else {
			/*Caso o comprador consiga fazer o login, continua para o Processo de Compra*/
			processoCompra();
		}
	}

	private void processoCompra() {
		/*Processo de Compra*/
		int tempoCompra = (int)((Math.random() * 2001) + 1000);  //Processo leva de 1 s a 3 s
		try {
			sleep(tempoCompra);  //Simula��o do tempo para realiza��o do processo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(tempoCompra > 2500) {
			/*Caso leve mais de 2,5 s, o comprador excede o tempo de sess�o e n�o realiza a compra*/
			System.err.println("Comprador #"+idComprador+": Tempo de sess�o excedido!");
		} else {
			/*Caso o comprador n�o estoure o tempo, continua para o Processo de Valida��o da Compra*/
			
			/*In�cio Se��o Cr�tica*/
			try {
				semaforo.acquire();
				validacaoCompra();  //Apenas 1 comprador pode validar a compra por vez
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				semaforo.release();
			}
			/*Fim Se��o Cr�tica*/
		}
	}

	private void validacaoCompra() {
		/*Processo de Valida��o da Compra*/
		if(quantMaxIng >= qntDesejada) {
			/*Caso tenha a quantidade de ingressos dispon�veis para venda*/
			quantMaxIng -= qntDesejada;
			if(qntDesejada == 1) {
				System.out.println("Parab�ns Comprador #"+idComprador+"! "
						+ "Voc� adquiriu "+qntDesejada+" ingresso! "
						+ "\nQuantidade de ingressos ainda dispon�veis: "+quantMaxIng+".");
			} else {
				System.out.println("Parab�ns Comprador #"+idComprador+"! "
						+ "Voc� adquiriu "+qntDesejada+" ingressos! "
						+ "\nQuantidade de ingressos ainda dispon�veis: "+quantMaxIng+".");
			}
			
		} else {
			/*Caso n�o tenha a quantidade de ingressos dispon�veis*/
			System.err.println("Comprador #"+idComprador+": Quantidade de ingressos indispon�vel!");
		}
	}
}
