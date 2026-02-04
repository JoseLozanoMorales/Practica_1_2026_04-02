package com.example.practica1202604_02;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView valor_humedad;
    TextView valor_presion;
    TextView valor_velocidad;
    TextView valor_Temperatura;
    EditText setvalor_temperatura;
    EditText setvalor_Humedad;

    DatabaseReference HumedadRef;
    DatabaseReference presionRef;
    DatabaseReference VelocidadRef;
    DatabaseReference TemperauraRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        valor_humedad = findViewById(R.id.valor_humedad);
        valor_presion = findViewById(R.id.valor_presion);
        valor_velocidad = findViewById(R.id.valor_velocidad);
        valor_Temperatura = findViewById(R.id.valor_Temperatura);
        setvalor_temperatura = findViewById(R.id.setvalor_temperatura);
        setvalor_Humedad = findViewById(R.id.setvalor_Humedad);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        HumedadRef = database.getReference("sensores/humedad");
        presionRef = database.getReference("sensores/presion");
        VelocidadRef = database.getReference("sensores/velocidad");
        TemperauraRef = database.getReference("sensores/temperatura");

        HumedadRef.addValueEventListener(setListener(valor_humedad, "%"));
        presionRef.addValueEventListener(setListener(valor_presion, "hPa"));
        VelocidadRef.addValueEventListener(setListener(valor_velocidad, "km/h"));
        TemperauraRef.addValueEventListener(setListener(valor_Temperatura, "°C"));


    }

    public ValueEventListener setListener(TextView txt, String UnidadMedida) {

        return (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    txt.setText(snapshot.getValue().toString() + " " + UnidadMedida);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txt.setText("");
            }
        });
    }
    public void clickBotonTemperatura(View view){
        TemperauraRef.setValue(Float.parseFloat(setvalor_temperatura.getText().toString()));
    }
    public void clickBotonHumedad(View view){
        HumedadRef.setValue(Float.parseFloat(setvalor_Humedad.getText().toString()));
    }

}