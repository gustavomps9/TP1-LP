import java.io.*;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final String DIRETORIA_DADOS = "dados";
    private static final String DIRETORIO_RESULTADOS = "resultados";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static void main(String[] args) {
        Path diretorioDados = Paths.get(DIRETORIA_DADOS);

        // Mapa para armazenar os resultados nacionais
        Map<String, Integer> resultadosNacionais = new HashMap<>();

        try {
            // Verificar se o diretório existe
            if (Files.exists(diretorioDados)) {
                // Iterar sobre os ficheiros .dat no diretório
                Files.newDirectoryStream(diretorioDados, "*.dat").forEach(ficheiro -> {
                    // Processar cada ficheiro .dat
                    processarCirculoEleitoral(ficheiro, resultadosNacionais);
                });
            } else {
                System.out.println("A diretoria '" + DIRETORIA_DADOS + "' não existe.");
            }
        } catch (IOException e) {
            System.err.println("Ocorreu um erro ao acessar a diretoria: " + e.getMessage());
            e.printStackTrace();
        }

        // Gerar ficheiro com os resultados nacionais
        gerarFicheiroResultadoNacional(resultadosNacionais);
    }

    private static void processarCirculoEleitoral(Path ficheiroBinario, Map<String, Integer> resultadosNacionais) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ficheiroBinario.toFile()))) {
            VotosCirculoEleitoral votosCirculo = (VotosCirculoEleitoral) ois.readObject();
            String nomeCirculo = votosCirculo.getNomeCirculo();
            Map<String, Integer> resultadosCirculo = processarVotosCirculo(votosCirculo);

            // Atualizar resultados nacionais
            resultadosCirculo.forEach((partido, votos) -> resultadosNacionais.merge(partido, votos, Integer::sum));

            gerarFicheiroResultado(nomeCirculo, resultadosCirculo);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Integer> processarVotosCirculo(VotosCirculoEleitoral votosCirculo) {
        Map<String, Integer> resultados = new HashMap<>();
        int totalVotantes = 0;
        int totalVotosValidos = 0;
        int totalVotosBrancos = 0;
        int totalVotosNulos = 0;

        for (VotosConcelho votosConcelho : votosCirculo.getVotosPorConcelho().values()) {
            for (Map.Entry<String, Integer> entry : votosConcelho.getVotosPorPartido().entrySet()) {
                String partido = entry.getKey();
                int votos = entry.getValue();

                totalVotantes += votos;
                totalVotosValidos += votos;

                // Adicionar votos ao total do partido
                resultados.merge(partido, votos, Integer::sum);
            }
        }

        resultados.put("Nº de votantes", totalVotantes);
        resultados.put("Nº de votos válidos", totalVotosValidos);
        resultados.put("Nº de votos brancos", totalVotosBrancos);
        resultados.put("Nº de votos nulos", totalVotosNulos);

        return resultados;
    }


    private static void gerarFicheiroResultado(String nomeCirculo, Map<String, Integer> resultados) {
        Path diretorioResultados = Paths.get(DIRETORIO_RESULTADOS);
        try {
            Files.createDirectories(diretorioResultados);
            String pathFile = diretorioResultados.resolve(nomeCirculo + ".txt").toString();
            try (PrintWriter out = new PrintWriter(pathFile)) {
                out.println("Nome do círculo: " + nomeCirculo);
                int totalVotantes = resultados.getOrDefault("Nº de votantes", 0);
                int totalVotosValidos = resultados.getOrDefault("Nº de votos válidos", 0);
                int totalVotosBrancos = resultados.getOrDefault("Nº de votos brancos", 0);
                int totalVotosNulos = resultados.getOrDefault("Nº de votos nulos", 0);

                double percentualValidos = (double) totalVotosValidos / totalVotantes * 100;
                double percentualBrancos = (double) totalVotosBrancos / totalVotantes * 100;
                double percentualNulos = (double) totalVotosNulos / totalVotantes * 100;

                out.println("Nº de votantes: " + totalVotantes);
                out.println("Nº de votos válidos: " + totalVotosValidos + " (" + DECIMAL_FORMAT.format(percentualValidos) + "%)");
                out.println("Nº de votos brancos: " + totalVotosBrancos + " (" + DECIMAL_FORMAT.format(percentualBrancos) + "%)");
                out.println("Nº de votos nulos: " + totalVotosNulos + " (" + DECIMAL_FORMAT.format(percentualNulos) + "%)");
                out.println("Resultados:");

                // Exibir resultados dos partidos
                Map<String, Integer> resultadosPartidos = resultados.entrySet().stream()
                        .filter(entry -> !entry.getKey().startsWith("Nº"))
                        .sorted(Map.Entry.comparingByKey())
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));

                for (Map.Entry<String, Integer> entry : resultadosPartidos.entrySet()) {
                    double percentualPartido = (double) entry.getValue() / totalVotosValidos * 100;
                    out.println(entry.getKey() + " - " + DECIMAL_FORMAT.format(percentualPartido) + "% (" + entry.getValue() + " votos)");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void gerarFicheiroResultadoNacional(Map<String, Integer> resultadosNacionais) {
        try {
            String pathFile = DIRETORIO_RESULTADOS + File.separator + "TotalNacional.txt";
            try (PrintWriter out = new PrintWriter(pathFile)) {
                out.println("Resultados Nacionais");
                int totalEleitores = resultadosNacionais.getOrDefault("Nº de votantes", 0);
                int totalValidos = resultadosNacionais.getOrDefault("Nº de votos válidos", 0);
                int totalBrancos = resultadosNacionais.getOrDefault("Nº de votos brancos", 0);
                int totalNulos = resultadosNacionais.getOrDefault("Nº de votos nulos", 0);

                double percentualValidos = (double) totalValidos / totalEleitores * 100;
                double percentualBrancos = (double) totalBrancos / totalEleitores * 100;
                double percentualNulos = (double) totalNulos / totalEleitores * 100;

                out.println("Nº total de eleitores: " + totalEleitores);
                out.println("Nº total de votos válidos: " + totalValidos + " (" + DECIMAL_FORMAT.format(percentualValidos) + "%)");
                out.println("Nº total de votos brancos: " + totalBrancos + " (" + DECIMAL_FORMAT.format(percentualBrancos) + "%)");
                out.println("Nº total de votos nulos: " + totalNulos + " (" + DECIMAL_FORMAT.format(percentualNulos) + "%)");
                out.println("Resultados por partido:");

                // Exibir resultados dos partidos
                resultadosNacionais.entrySet().stream()
                        .filter(entry -> !entry.getKey().startsWith("Nº"))
                        .forEach(entry -> {
                            double percentualPartido = (double) entry.getValue() / totalValidos * 100;
                            out.println(entry.getKey() + " - " + DECIMAL_FORMAT.format(percentualPartido) + "% (" + entry.getValue() + " votos)");
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
