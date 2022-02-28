package com.example.ceep.ui.recyclerview.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ceep.R;
import com.example.ceep.model.Nota;

import java.util.List;

public class ListaNotasAdapter extends RecyclerView.Adapter<ListaNotasAdapter.NotaViewHolder> {

    private final List<Nota> notas;
    private final Context context;
    // restante dos atributos
    private int quantidadeViewHolder = 0;

    public ListaNotasAdapter(Context context, List<Nota> notas) {
        this.context = context;
        this.notas = notas;
    }

    @NonNull
    @Override
    public ListaNotasAdapter.NotaViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        quantidadeViewHolder++;

        View viewCriada = LayoutInflater.from(context).inflate(R.layout.item_nota, parent, false);
        Log.i("recyclerView adapter",
                "quantidade view holder: " + quantidadeViewHolder);

        return new NotaViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(ListaNotasAdapter.NotaViewHolder holder, int position) {
        Nota nota = notas.get(position);
        holder.vincula(nota);
    }

    @Override
    public int getItemCount() {
        return this.notas.size();
    }

    class NotaViewHolder extends RecyclerView.ViewHolder {
        private final TextView titulo;
        private final TextView descricao;

        public NotaViewHolder(View itemView) {
            super(itemView);
            this.titulo = itemView.findViewById(R.id.item_nota_titulo);
            this.descricao = itemView.findViewById(R.id.item_nota_descricao);
        }

        public void vincula(Nota nota){
            preencheCampo(nota);
        }

        private void preencheCampo(Nota nota) {
            this.titulo.setText(nota.getTitulo());
            this.descricao.setText(nota.getDescricao());
        }
    }

    public void adiciona(Nota nota){
        this.notas.add(nota);
        this.notifyDataSetChanged();
    }
}
