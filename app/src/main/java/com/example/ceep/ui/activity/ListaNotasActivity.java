package com.example.ceep.ui.activity;

import static com.example.ceep.ui.activity.NotaActivityConstantes.CHAVE_NOTA;
import static com.example.ceep.ui.activity.NotaActivityConstantes.CHAVE_POSICAO;
import static com.example.ceep.ui.activity.NotaActivityConstantes.POSICAO_INVALIDA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ceep.R;
import com.example.ceep.dao.NotaDAO;
import com.example.ceep.model.Nota;
import com.example.ceep.ui.recyclerview.adapter.ListaNotasAdapter;
import com.example.ceep.ui.recyclerview.adapter.listener.OnItemClickListener;
import com.example.ceep.ui.recyclerview.helper.callback.NotaItemTouchHelperCallback;

import java.util.List;

public class ListaNotasActivity extends AppCompatActivity {

    private ListaNotasAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultEditLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_notas);
        setTitle("Notas");

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
                vaiParaFormularioNotaActivityInsere();
            }
        });
    }

    private void vaiParaFormularioNotaActivityInsere() {
        Intent iniciaFormularioNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        activityResultLauncher.launch(iniciaFormularioNota);
    }

    private void contiguraActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (ehResultadoInsereNota(result)) {
                        Nota notaRecebida = (Nota) result.getData().getSerializableExtra(CHAVE_NOTA);
                        adiciona(notaRecebida);
                    }
                });
    }

    private void contiguraActivityResultEdit() {
        activityResultEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (ehResultadoAlteraNota(result)) {
                        Nota notaRecebida = (Nota) result.getData().getSerializableExtra(CHAVE_NOTA);
                        int posicaoRecebida = result.getData().getIntExtra(CHAVE_POSICAO, POSICAO_INVALIDA);

                        if (ehPosicaoValida(posicaoRecebida)) {
                            altera(notaRecebida, posicaoRecebida);
                        } else {
                            Toast.makeText(this, "Ocorreu um problema na alteração da nota.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void altera(Nota nota, int posicao) {
        new NotaDAO().altera(posicao, nota);
        adapter.altera(posicao, nota);
    }

    private boolean ehPosicaoValida(int posicaoRecebida) {
        return posicaoRecebida > POSICAO_INVALIDA;
    }

    private void adiciona(Nota nota) {
        new NotaDAO().insere(nota);
        adapter.adiciona(nota);
    }

    private boolean ehResultadoInsereNota(ActivityResult result) {
        return resultadoOK(result) && result.getData().hasExtra(CHAVE_NOTA);
    }

    private boolean ehResultadoAlteraNota(ActivityResult result) {
        return resultadoOK(result) && result.getData().hasExtra(CHAVE_NOTA) && result.getData().hasExtra(CHAVE_POSICAO);
    }

    private boolean resultadoOK(ActivityResult result) {
        return result.getResultCode() == Activity.RESULT_OK;
    }

    private List<Nota> pegaTodasNotas() {
        NotaDAO dao = new NotaDAO();
        return dao.todos();
    }

    private void configuraReciclerView(List<Nota> todasNotas) {
        RecyclerView listaNotas = findViewById(R.id.lista_notas_recyclerview);
        configuraAdapter(todasNotas, listaNotas);
        configuraItemTouchHelper(listaNotas);
    }

    private void configuraItemTouchHelper(RecyclerView listaNotas) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new NotaItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(listaNotas);
    }

    private void configuraAdapter(List<Nota> todasNotas, RecyclerView listaNotas) {
        adapter = new ListaNotasAdapter(this, todasNotas);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(Nota nota, int posicao) {
                vaiParaFormularioNotaActivityAltera(nota, posicao);
            }
        });

        listaNotas.setAdapter(adapter);
    }

    private void vaiParaFormularioNotaActivityAltera(Nota nota, int posicao) {
        Intent abreFormularioComNota = new Intent(ListaNotasActivity.this, FormularioNotaActivity.class);
        abreFormularioComNota.putExtra(CHAVE_NOTA, nota);
        abreFormularioComNota.putExtra(CHAVE_POSICAO, posicao);
        activityResultEditLauncher.launch(abreFormularioComNota);
    }
}