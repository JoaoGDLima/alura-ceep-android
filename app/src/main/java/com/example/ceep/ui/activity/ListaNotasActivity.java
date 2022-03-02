package com.example.ceep.ui.activity;

import static com.example.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.example.ceep.ui.activity.NotaActivityConstantes.CODIGO_RESULTADO_NOTA_CRIADA;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ceep.R;
import com.example.ceep.dao.NotaDAO;
import com.example.ceep.model.Nota;
import com.example.ceep.ui.recyclerview.adapter.ListaNotasAdapter;
import com.example.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class ListaNotasActivity extends AppCompatActivity {

    private ListaNotasAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultEditLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);

        List<Nota> todasNotas = pegaTodasNotas();

        configuraReciclerView(todasNotas);
        contiguraActivityResult();
        contiguraActivityResultEdit();
        configuraBotaoInsereNota();
    }

    private void configuraBotaoInsereNota() {
        TextView botaoInsereNota = findViewById(R.id.lista_notas_insere_nota);
        botaoInsereNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaiParaFormularioNotaActivity();
            }
        });
    }

    private void vaiParaFormularioNotaActivity() {
        Intent iniciaFormularioNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        activityResultLauncher.launch(iniciaFormularioNota);
    }

    private void contiguraActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (ehResultadoComNota(result)) {
                        Nota notaRecebida = (Nota) result.getData().getSerializableExtra(CHAVE_NOTA);
                        adiciona(notaRecebida);
                    }
                });
    }

    private void contiguraActivityResultEdit() {
        activityResultEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (ehResultadoComNota(result) && result.getData().hasExtra("posicao")) {
                        Nota notaRecebida = (Nota) result.getData().getSerializableExtra(CHAVE_NOTA);
                        int posicaoRecebida = result.getData().getIntExtra("posicao", -1);
                        new NotaDAO().altera(posicaoRecebida, notaRecebida);
                        adapter.altera(posicaoRecebida, notaRecebida);
                    }
                });
    }

    private void adiciona(Nota nota) {
        new NotaDAO().insere(nota);
        adapter.adiciona(nota);
    }

    private boolean ehResultadoComNota(ActivityResult result) {
        return ehCodigoResultadoNotaCriada(result) && result.getData().hasExtra(CHAVE_NOTA);
    }

    private boolean ehCodigoResultadoNotaCriada(ActivityResult result) {
        return result.getResultCode() == CODIGO_RESULTADO_NOTA_CRIADA;
    }

    private List<Nota> pegaTodasNotas() {
        NotaDAO dao = new NotaDAO();
        return dao.todos();
    }

    private void configuraReciclerView(List<Nota> todasNotas) {
        RecyclerView listaNotas = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(todasNotas, listaNotas);
    }

    private void configuraAdapter(List<Nota> todasNotas, RecyclerView listaNotas) {
        adapter = new ListaNotasAdapter(this, todasNotas);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(Nota nota, int posicao) {
                Intent abreFormularioComNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
                abreFormularioComNota.putExtra(CHAVE_NOTA, nota);
                abreFormularioComNota.putExtra("posicao", posicao);
                activityResultEditLauncher.launch(abreFormularioComNota);
            }
        });

        listaNotas.setAdapter(adapter);
    }
}