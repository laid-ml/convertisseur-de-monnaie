import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class ConvertisseurDevises extends JFrame {
    // Variables en français
    private JLabel labelMontant;
    private JTextField champMontant;
    private JButton boutonConvertir;
    private JLabel labelResultat;
    private JComboBox<String> comboBoxDevisesSource;
    private JComboBox<String> comboBoxDevisesCible;

    
    private static final String API_KEY = "0YxHxLAn3k89M9C1WVPH5duWOUDns05g";

    // Constructeur
    public ConvertisseurDevises() {
        // Configurer la fenêtre
        setTitle("Convertisseur de devises");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Créer et configurer les éléments de l'interface utilisateur
        labelMontant = new JLabel("Montant :");
        champMontant = new JTextField(10);
        boutonConvertir = new JButton("Convertir");
        labelResultat = new JLabel("Résultat :");

        comboBoxDevisesSource = new JComboBox<>(new String[]{"EUR", "USD", "GBP", "JPY", "CAD"});
        comboBoxDevisesCible = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY", "CAD"});

        // Ajouter un écouteur d'événements au bouton
        boutonConvertir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String deviseSource = (String) comboBoxDevisesSource.getSelectedItem();
                    String deviseCible = (String) comboBoxDevisesCible.getSelectedItem();

                    double montantSource = Double.parseDouble(champMontant.getText());
                    JSONObject tauxDeChange = getTauxDeChange(deviseSource);

                    if (tauxDeChange.getBoolean("success")) {
                        double taux = tauxDeChange.getJSONObject("quotes").getDouble(deviseSource + deviseCible);
                        double montantCible = montantSource * taux;
                        labelResultat.setText("Résultat : " + montantCible + " " + deviseCible);
                    } else {
                        JOptionPane.showMessageDialog(null, "Erreur lors de la récupération du taux de change.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, "Erreur lors de la récupération du taux de change.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Ajouter les éléments à la fenêtre
        setLayout(new FlowLayout());
        add(labelMontant);
        add(champMontant);
        add(comboBoxDevisesSource);
        add(comboBoxDevisesCible);
        add(boutonConvertir);
        add(labelResultat);

        // Rendre la fenêtre visible
        setVisible(true);
    }

    private JSONObject getTauxDeChange(String deviseSource) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.apilayer.com/exchangerates_data/live?base=" + deviseSource + "&apikey=" + API_KEY))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ConvertisseurDevises();
            }
        });
    }
}


