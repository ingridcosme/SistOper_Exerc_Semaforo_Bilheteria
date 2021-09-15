/*
 * Considere o seguinte cenário:
 * Um grande show acontecerá no Brasil, em uma casa com capacidade para 100 pes_
 * soas. A venda será feita excluisvamente pelo sistema. Simultaneamente, 300 
 * pessoas, no primeiro instante acessam o sistema de compra. As pessoas podem 
 * comprar de 1 a 4 ingressos por compra, sendo que isso é uma condição aleató_
 * ria. Os passos para a compra são: 
 * 1) Login no sistema: Processo que pode demorar de 50 ms a 2 s, sendo que, se 
 * o tempo passar de 1s, ao final do tempo de espera de login, o comprador rece_
 * be uma mensagem de timeout e, por não conseguir fazer o login, não poderá fa_
 * zer a compra.
 * 2) Processo de compra: Processo que pode demorar de 1 s a 3 s, sendo que, se 
 * o tempo passar de 2,5s, ao final do tempo de espera da compra, o comprador 
 * recebe uma mensagem de final de tempo de sessão e, por estourar o tempo de 
 * sessão, não poderá fazer a compra.
 * 3) Validação da compra: O sistema deve verificar se há ingressos suficientes 
 * para finalizar a compra. Se houver, faz a compra e subtrai do total de in_
 * gressos disponíveis. O sistema comunica a venda da quantidade de ingressos 
 * para o usuário e a quantidade de ingressos ainda disponíveis. Se não houver a 
 * totalidade dos ingressos disponibiliados, o comprador recebe mensagem sobre a
 * indisponibilidade dos ingressos e, como não é possível fracionar a quantidade 
 * pedida, este perde a possibilidade de compra na sessão.
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
			sleep(tempoLogin);  //Simulação do tempo para realização do processo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(tempoLogin > 1000) {  
			/*Caso leve mais de 1 s, o comprador não faz login e não realiza a compra*/
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
			sleep(tempoCompra);  //Simulação do tempo para realização do processo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(tempoCompra > 2500) {
			/*Caso leve mais de 2,5 s, o comprador excede o tempo de sessão e não realiza a compra*/
			System.err.println("Comprador #"+idComprador+": Tempo de sessão excedido!");
		} else {
			/*Caso o comprador não estoure o tempo, continua para o Processo de Validação da Compra*/
			
			/*Início Seção Crítica*/
			try {
				semaforo.acquire();
				validacaoCompra();  //Apenas 1 comprador pode validar a compra por vez
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				semaforo.release();
			}
			/*Fim Seção Crítica*/
		}
	}

	private void validacaoCompra() {
		/*Processo de Validação da Compra*/
		if(quantMaxIng >= qntDesejada) {
			/*Caso tenha a quantidade de ingressos disponíveis para venda*/
			quantMaxIng -= qntDesejada;
			if(qntDesejada == 1) {
				System.out.println("Parabéns Comprador #"+idComprador+"! "
						+ "Você adquiriu "+qntDesejada+" ingresso! "
						+ "\nQuantidade de ingressos ainda disponíveis: "+quantMaxIng+".");
			} else {
				System.out.println("Parabéns Comprador #"+idComprador+"! "
						+ "Você adquiriu "+qntDesejada+" ingressos! "
						+ "\nQuantidade de ingressos ainda disponíveis: "+quantMaxIng+".");
			}
			
		} else {
			/*Caso não tenha a quantidade de ingressos disponíveis*/
			System.err.println("Comprador #"+idComprador+": Quantidade de ingressos indisponível!");
		}
	}
}
