// import com.sun.org.apache.xpath.internal.functions.FuncStringLength;

import java.io.IOException;

/**
 * Created by caroles on 29/03/2017.
 */
/**Roda o jogo
 * */
public class Bozo {
    String jogar() throws IOException{
        String texto = null;

        RolaDados dados;
        dados = new RolaDados(5);
        Placar placar;
        placar = new Placar();

        EntradaTeclado teclado = new EntradaTeclado();

        int numeroRodadas = 10;
        

        while (numeroRodadas > 0) {
            System.out.printf("Digite ENTER para iniciar a rodada\n");
            String enter = teclado.leString();

            int resultados[] = dados.rolar();
            int counterDado = 0;
            System.out.println(dados);
            texto = dados.toString();
            System.out.printf("Escolha os dados a serem rolados novamente ou digite ENTER para finalizar a rodada\n");
            String ASeremRolados = teclado.leString();
            if (!ASeremRolados.equals("")) {
                resultados = dados.rolar(ASeremRolados);
                System.out.println(dados);

                System.out.printf("Escolha os dados a serem rolados novamente ou digite ENTER para finalizar a rodada\n");
                ASeremRolados = teclado.leString();

                if (!ASeremRolados.equals("")) {

                    resultados = dados.rolar(ASeremRolados);

                }
                System.out.println(dados);
            } else System.out.println(dados);

            System.out.println(placar);
            System.out.printf("Escolha uma posicao de 1 a 10 para ser ocupada\n");
            int pos = teclado.leInt();

            try {
                placar.add(pos, resultados);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Escolha uma posicao VALIDA para ser ocupada");
                pos = teclado.leInt();
                placar.add(pos, resultados);
            }
                System.out.println(placar);

            numeroRodadas--;
        }

        System.out.println("Você obteve " + placar.getScore() + " pontos!\n");


        return texto;

    }
}
