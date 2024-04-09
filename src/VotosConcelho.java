import java.io.*;
import java.util.*;

public class VotosConcelho implements Serializable {
    private String concelho;
    private Map<String, Integer> votosPorPartido;

    public VotosConcelho(String concelho) {
        this.concelho = concelho;
        this.votosPorPartido = new HashMap<>();
    }

    public void adicionarVotos(String partido, int votos) {
        votosPorPartido.put(partido, votos);
    }

    public String getConcelho() {
        return concelho;
    }

    public Map<String, Integer> getVotosPorPartido() {
        return votosPorPartido;
    }
}
