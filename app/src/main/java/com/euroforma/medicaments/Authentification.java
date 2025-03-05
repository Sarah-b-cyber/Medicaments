package com.euroforma.medicaments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Authentification extends AppCompatActivity {

    private EditText editTextCodeVisiteur, editTextCleTemporaire;
    private Button btnEnvoyerCode, btnValiderCle;
    private TextView textViewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentification);

        // Initialisation des vues
        editTextCodeVisiteur = findViewById(R.id.editTextCodeVisiteur);
        editTextCleTemporaire = findViewById(R.id.editTextCleTemporaire);
        btnEnvoyerCode = findViewById(R.id.btnEnvoyerCode);
        btnValiderCle = findViewById(R.id.btnValiderCle);


        // Masquer la deuxième partie au démarrage
        editTextCleTemporaire.setVisibility(View.GONE);
        btnValiderCle.setVisibility(View.GONE);

        btnEnvoyerCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficherDeuxiemePartie();
            }
        });

    }

    /**
     * Afficher la deuxième partie après saisie du code visiteur.
     */
    private void afficherDeuxiemePartie() {
        String codeVisiteur = editTextCodeVisiteur.getText().toString().trim();

        if (codeVisiteur.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer votre code visiteur", Toast.LENGTH_SHORT).show();
            return;
        }

        // Affichage de la deuxième partie
        editTextCleTemporaire.setVisibility(View.VISIBLE);
        btnValiderCle.setVisibility(View.VISIBLE);
    }
}