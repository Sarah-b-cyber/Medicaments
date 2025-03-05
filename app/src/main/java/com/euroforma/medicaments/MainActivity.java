package com.euroforma.medicaments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbm;
    ListView listviewResult;
    Spinner spinnerVoiesAdmin;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des composants
        dbm = new DatabaseHelper(this);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerVoiesAdmin = findViewById(R.id.spinnerVoiesAdmin);
        listviewResult = findViewById(R.id.listViewResults);
        Button btnComposition = findViewById(R.id.btnComposition);
        Button btnPresentation = findViewById(R.id.btnPresentation);

        // Configuration du bouton de recherche
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // Configuration du Spinner
        setupVoiesAdminSpinner();


        // Gestion des clics sur la liste des résultats
        listviewResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Récupérer l'élément sélectionné
                Medicament selectedMedicament = (Medicament) adapterView.getItemAtPosition(position);
                // Afficher la composition du médicament sélectionné
                afficherCompositionMedicament(selectedMedicament);
            }
        });
    }

    private void setupVoiesAdminSpinner() {
        List<String> voiesAdminList = dbm.getDistinctVoiesdadministration();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, voiesAdminList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVoiesAdmin.setAdapter(spinnerAdapter);
    }

    private void performSearch() {
        // Récupérer les valeurs saisies par l'utilisateur
        String VoieAdmin = spinnerVoiesAdmin.getSelectedItem().toString();
        String denomination = ((EditText) findViewById(R.id.editTextDenomination)).getText().toString().trim();
        String formePharmaceutique = ((EditText) findViewById(R.id.editTextFormePharmaceutique)).getText().toString().trim();
        String titulaires = ((EditText) findViewById(R.id.editTextTitulaires)).getText().toString().trim();
        String denominationSubstance = ((EditText) findViewById(R.id.editTextDenominationSubstance)).getText().toString().trim();
        String dateAMM = ((EditText) findViewById(R.id.editTextDate)).getText().toString().trim();

        // Masquer le clavier
        cacherClavier();

        // Recherche dans la base de données
        List<Medicament> searchResults = dbm.searchMedicaments(denomination, formePharmaceutique, titulaires, denominationSubstance, VoieAdmin, dateAMM);

        // Affichage des résultats
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "Aucun médicament ne correspond", Toast.LENGTH_SHORT).show();
        } else {
            MedicamentAdapter adapter = new MedicamentAdapter(this, searchResults);
            listviewResult.setAdapter(adapter);
        }
    }

    private void afficherCompositionMedicament(Medicament medicament) {
        List<String> composition = dbm.getCompositionMedicament(medicament.getCodeCIS());

        // Création de la boîte de dialogue pour afficher la composition
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Composition de " + medicament.getCodeCIS());

        StringBuilder compositionText = new StringBuilder();
        if (composition.isEmpty()) {
            compositionText.append("Aucune composition disponible pour ce médicament.").append("\n");
        } else {
            for (String item : composition) {
                compositionText.append(item).append("\n");
            }
        }

        builder.setMessage(compositionText.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cacherClavier() {
        // Masquer le clavier si un champ de texte est actif
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View vueCourante = getCurrentFocus();
        if (vueCourante != null) {
            imm.hideSoftInputFromWindow(vueCourante.getWindowToken(), 0);
        }
    }

    private String removeAccents(String input) {
        if (input == null) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}