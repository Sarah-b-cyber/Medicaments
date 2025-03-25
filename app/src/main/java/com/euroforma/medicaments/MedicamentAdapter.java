package com.euroforma.medicaments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.euroforma.medicaments.Medicament;
import com.euroforma.medicaments.R;

import java.util.List;

public class MedicamentAdapter extends ArrayAdapter<Medicament> {

    private OnButtonCClickListener onButtonCClickListener;
    private OnButtonPClickListener onButtonPClickListener;

    // Interface pour le bouton C
    public interface OnButtonCClickListener {
        void onButtonCClick(Medicament medicament);
    }

    // Interface pour le bouton P
    public interface OnButtonPClickListener {
        void onButtonPClick(Medicament medicament);
    }

    // Méthodes pour définir les écouteurs
    public void setOnButtonCClickListener(OnButtonCClickListener listener) {
        this.onButtonCClickListener = listener;
    }

    public void setOnButtonPClickListener(OnButtonPClickListener listener) {
        this.onButtonPClickListener = listener;
    }


        public MedicamentAdapter(Context context, List<Medicament> medicaments) {
            super(context, 0, medicaments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Medicament medicament = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_medicament, parent, false);
            }

            // Populate the data into the template view using the data object
            TextView tvCodeCIS = convertView.findViewById(R.id.tvCodeCIS);
            TextView tvDenomination = convertView.findViewById(R.id.tvDenomination);
            TextView tvFormePharmaceutique = convertView.findViewById(R.id.tvFormePharmaceutique);
            TextView tvVoiesAdmin = convertView.findViewById(R.id.tvVoiesAdmin);
            TextView tvTitulaires = convertView.findViewById(R.id.tvTitulaires);
            TextView tvStatutadministratif = convertView.findViewById(R.id.tvStatutAdministratif);
            TextView tvNb_Molecule = convertView.findViewById(R.id.nb_molecule);
            TextView tvDateAMM = convertView.findViewById(R.id.DateAMM);
            Button tvbtnComposition= convertView.findViewById(R.id.btnComposition);
            Button tvbtnPresentation = convertView.findViewById(R.id.btnPresentation);

            tvCodeCIS.setText("CIS: "+ String.valueOf(medicament.getCodeCIS()));
            tvDenomination.setText("Dénomination : " + medicament.getDenomination());
            tvFormePharmaceutique.setText(medicament.getFormePharmaceutique());
            tvVoiesAdmin.setText(medicament.getVoiesAdmin());
            tvTitulaires.setText("Fabricant : " + medicament.getTitulaires());
            tvStatutadministratif.setText(medicament.getStatutAdministratif());
            tvDateAMM.setText("DateAMM: "+ (medicament.getDateAMM()));

            tvNb_Molecule.setText(medicament.getNb_molecule()+ pluriels(Integer.parseInt(medicament.getNb_molecule())," molecule"));
            tvbtnComposition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onButtonCClickListener != null && medicament != null) {
                        onButtonCClickListener.onButtonCClick(medicament);
                    }
                }
            });
            tvbtnPresentation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onButtonPClickListener != null && medicament != null) {
                        onButtonPClickListener.onButtonPClick(medicament);
                    }
                }
            });

            // Return the completed view to render on screen

            int backgroundColor = (position % 2 == 0) ? getContext().getResources().getColor(R.color.colorLight) : getContext().getResources().getColor(R.color.colorDark);
            convertView.setBackgroundColor(backgroundColor);

            // Return the completed view to render on screen

            return convertView;
        }

    static String pluriels(int nbr, String mot){
        String un_s="";
        if (nbr>1){
            un_s="s";
        }
        return (mot+un_s);
    }
}



