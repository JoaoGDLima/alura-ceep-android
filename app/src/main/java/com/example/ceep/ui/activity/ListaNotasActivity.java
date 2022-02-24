package com.example.ceep.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ceep.R;
import com.example.ceep.dao.NotaDAO;
import com.example.ceep.model.Nota;
import com.example.ceep.ui.recyclerview.adapter.ListaNotasAdapter;

import java.util.List;

public class ListaNotasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);

        RecyclerView listaNotas = findViewById(R.id.lista_notas_recyclerview);

        NotaDAO notaDAO = new NotaDAO();
        notaDAO.insere(new Nota("Primeira nota", "Primeira descrição"));

        List<Nota> todasNotas = notaDAO.todos();
        listaNotas.setAdapter(new ListaNotasAdapter(todasNotas));

    }
}