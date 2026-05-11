import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Vehicule;

public class Registre<T extends Vehicule> {
    private List<T> elements = new ArrayList<>();

    public void ajouter(T element) {
        elements.add(element);
    }

    public void supprimer(T element) {
        elements.remove(element);
    }

    public List<T> getAll() {
        return elements;
    }

    public List<T> filtrerParEtat(String etat) {
        return elements.stream()
                .filter(e -> e.getEtat().equalsIgnoreCase(etat))
                .collect(Collectors.toList());
    }

    public List<T> trierParKilometrage() {
        return elements.stream()
                .sorted((a, b) -> a.getKilometrage() - b.getKilometrage())
                .collect(Collectors.toList());
    }

    public int compter() {
        return elements.size();
    }
}