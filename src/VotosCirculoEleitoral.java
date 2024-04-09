import java.io.*;
import java.util.*;

public class VotosCirculoEleitoral implements Serializable {
    private String nomeCirculo;
    private Map<String, VotosConcelho> votosPorConcelho;

    public VotosCirculoEleitoral(String nomeCirculo) {
        this.nomeCirculo = nomeCirculo;
        this.votosPorConcelho = new HashMap<>();
    }

    public void adicionarVotosConcelho(String concelho, VotosConcelho dadosVotosConcelho) {
        votosPorConcelho.put(concelho, dadosVotosConcelho);
    }

    public String getNomeCirculo() {
        return nomeCirculo;
    }

    public Map<String, VotosConcelho> getVotosPorConcelho() {
        return votosPorConcelho;
    }
}
