import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Substitua esta lista pelo caminho dos arquivos .dat dos círculos eleitorais
        String[] circulosEleitorais = {"circulo_coimbra.dat"};
        Map<String, Integer> resultadosNacionais = new HashMap<>();

        for (String arquivoCirculo : circulosEleitorais) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivoCirculo))) {
                VotosCirculoEleitoral votosCirculo = (VotosCirculoEleitoral) ois.readObject();
                Map<String, Integer> resultadosCirculo = processarVotosCirculo(votosCirculo);
                resultadosNacionais = somarResultadosNacionais(resultadosNacionais, resultadosCirculo);
                gerarArquivoResultado(votosCirculo.getNomeCirculo(), resultadosCirculo);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Gerar arquivo com os resultados nacionais
        gerarArquivoResultado("TotalNacional", resultadosNacionais);
    }

    private static Map<String, Integer> processarVotosCirculo(VotosCirculoEleitoral votosCirculo) {
        Map<String, Integer> resultados = new HashMap<>();
        for (VotosConcelho votosConcelho : votosCirculo.getVotosPorConcelho().values()) {
            for (Map.Entry<String, Integer> entrada : votosConcelho.getVotosPorPartido().entrySet()) {
                resultados.merge(entrada.getKey(), entrada.getValue(), Integer::sum);
            }
        }
        return resultados;
    }

    private static Map<String, Integer> somarResultadosNacionais(Map<String, Integer> nacional, Map<String, Integer> circulo) {
        circulo.forEach((partido, votos) -> nacional.merge(partido, votos, Integer::sum));
        return nacional;
    }

    private static void gerarArquivoResultado(String nomeCirculo, Map<String, Integer> resultados) {
        String nomeArquivo = nomeCirculo + ".txt";
        try (PrintWriter out = new PrintWriter(nomeArquivo)) {
            out.println("Resultados para: " + nomeCirculo);
            resultados.forEach((partido, votos) -> out.println(partido + ": " + votos + " votos"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
