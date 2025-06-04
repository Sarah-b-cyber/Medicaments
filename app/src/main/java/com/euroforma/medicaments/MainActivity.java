package com.euroforma.medicaments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String USER_STATUS_OK = "Authentifié";
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_STATUS = "userStatus";
    DatabaseHelper dbm;
    ListView listviewResult;
    Spinner spinnerVoiesAdmin;
    Button btnSearch;
    ImageButton btn_logout;
    CheckBox checkboxGenerique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isUserAuthenticated()) {
            Intent authIntent = new Intent(this, Authentification.class);
            startActivity(authIntent);
            finish();
        }


        // Initialisation des composants
        dbm = new DatabaseHelper(this);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerVoiesAdmin = findViewById(R.id.spinnerVoiesAdmin);
        listviewResult = findViewById(R.id.listViewResults);
       // Button btnComposition = findViewById(R.id.btnComposition);
        //Button btnPresentation = findViewById(R.id.btnPresentation);
        btn_logout = findViewById(R.id.btn_logout);
        checkboxGenerique = findViewById(R.id.checkbox_generique);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authIntent = new Intent(MainActivity.this, UserInformation.class);
                startActivity(authIntent);
                finish();
            }
        });


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
                Medicament selectedMedicament = (Medicament) adapterView.getItemAtPosition(position);
                afficherCompositionMedicament(selectedMedicament);
            }
        });
    }

    private void setupVoiesAdminSpinner() {
        List<String> voiesAdminList = dbm.getDistinctVoiesdadministration();
        if (voiesAdminList == null || voiesAdminList.isEmpty()) {
            voiesAdminList.add("Aucune voie disponible");
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, voiesAdminList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVoiesAdmin.setAdapter(spinnerAdapter);
    }

    private void performSearch() {
        // Récupérer les valeurs saisies par l'utilisateur
        String VoieAdmin = (spinnerVoiesAdmin.getSelectedItem() != null) ? spinnerVoiesAdmin.getSelectedItem().toString() : "";
        String denomination = getEditTextValue(R.id.editTextDenomination);
        String formePharmaceutique = getEditTextValue(R.id.editTextFormePharmaceutique);
        String titulaires = getEditTextValue(R.id.editTextTitulaires);
        String denominationSubstance = getEditTextValue(R.id.editTextDenominationSubstance);
        String dateAMM = getEditTextValue(R.id.editTextDate);
        boolean generiqueChecked = checkboxGenerique.isChecked();


        // Masquer le clavier
        cacherClavier();

        // Recherche dans la base de données
        List<Medicament> searchResults = dbm.searchMedicaments(denomination, formePharmaceutique, titulaires, denominationSubstance, VoieAdmin, dateAMM, generiqueChecked);

        // Affichage des résultats
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "Aucun médicament ne correspond", Toast.LENGTH_SHORT).show();
        } else {
            MedicamentAdapter adapter = new MedicamentAdapter(this, searchResults);
            adapter.setOnButtonCClickListener(new MedicamentAdapter.OnButtonCClickListener() {
                @Override
                public void onButtonCClick(Medicament medicament) {
                    afficherCompositionMedicament(medicament);
                }
            });
            adapter.setOnButtonPClickListener(new MedicamentAdapter.OnButtonPClickListener() {
                @Override
                public void onButtonPClick(Medicament medicament) {
                    afficherPresentationMedicament(medicament);
                }
            });
            listviewResult.setAdapter(adapter);
        }
    }

    private void afficherCompositionMedicament(Medicament medicament) {
        List<String> composition = dbm.getCompositionMedicament(medicament.getCodeCIS());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Composition de " + medicament.getCodeCIS());

        StringBuilder compositionText = new StringBuilder();
        if (composition.isEmpty()) {
            compositionText.append("Aucune composition disponible pour ce médicament.\n");
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

    private void afficherPresentationMedicament(Medicament medicament) {
        List<String> presentation = dbm.getPresentationMedicament(medicament.getCodeCIS());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Présentations de " + medicament.getCodeCIS());

        StringBuilder presentationText = new StringBuilder();
        if (presentation.isEmpty()) {
            presentationText.append("Aucune présentation disponible pour ce médicament.\n");
        } else {
            for (String item : presentation) {
                presentationText.append(item).append("\n");
            }
        }

        builder.setMessage(presentationText.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cacherClavier() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View vueCourante = getCurrentFocus();
        if (vueCourante != null) {
            imm.hideSoftInputFromWindow(vueCourante.getWindowToken(), 0);
        }
    }

    private String getEditTextValue(int editTextId) {
        EditText editText = findViewById(editTextId);
        return (editText.getText() != null) ? editText.getText().toString().trim() : "";
    }

    private String removeAccents(String input) {
        if (input == null) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    private boolean isUserAuthenticated() {

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userStatus = preferences.getString(KEY_USER_STATUS, "");

        // Vérifiez si la chaîne d'état de l'utilisateur est "authentification=OK"
        return USER_STATUS_OK.equals(userStatus);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Logique de déconnexion
                        logoutUser();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Annuler la déconnexion
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void logoutUser() {
        // Supprimer les données de connexion
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_USER_STATUS); // Effacer uniquement le statut de connexion
        editor.apply();

        // Rediriger vers l'écran de connexion
        Intent intent = new Intent(MainActivity.this, Authentification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Supprime l'historique de navigation
        startActivity(intent);
        finish(); // Ferme l'activité actuelle
    }

}
