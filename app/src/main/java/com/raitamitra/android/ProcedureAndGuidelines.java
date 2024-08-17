package com.raitamitra.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ProcedureAndGuidelines extends AppCompatActivity {
TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedure_and_guidelines);
        text = findViewById(R.id.text);
        text.setText("Applying for any government scheme, the farmer has to read the eligibility criteria and requirements carefully, then understand the application procedure, gather all the required documents, complete the application form, double check for any errors, seek professional assistance if needed, and finally submit the application within the deadline. He then has to follow up, track the progress, and stay updated on any changes.");
    }
}