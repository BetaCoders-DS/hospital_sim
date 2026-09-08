package com.github.betacoders.maps;

import com.github.betacoders.entities.StaticEntities;
import com.github.betacoders.grid.Grid;
import processing.core.PApplet;

public class MapLoader {
    private final PApplet app;
    private Grid<StaticEntities> mapa;
    private int geradores;
    private int removedores;

    public MapLoader(PApplet app) {
        this.app = app;
    }
    public boolean carregar(String arquivo) {
        String[] linhas = app.loadStrings(arquivo);

        if (linhas == null || linhas.length == 0) {
            return false;
        }
        String[] dimensoes = PApplet.splitTokens(linhas[0].trim());
        if (dimensoes.length != 2) {
            return false;
        }
        int linhasMapa;
        int colunasMapa;

        try {
            linhasMapa = Integer.parseInt(dimensoes[0]);
            colunasMapa = Integer.parseInt(dimensoes[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (linhasMapa <= 0 || colunasMapa <= 0) {
            return false;
        }
        if (linhas.length != linhasMapa + 1) {
            return false;
        }
        mapa = new Grid<>(colunasMapa, linhasMapa);
        geradores = 0;
        removedores = 0;
        for (int y = 0; y < linhasMapa; y++) {
            String linha = linhas[y + 1];
            if (linha.length() != colunasMapa) {
                return false;
            }
            for (int x = 0; x < colunasMapa; x++) {
                char tipo = linha.charAt(x);
                StaticEntities entidade = criarEntidade(tipo);
                if (entidade == null) {
                    return false;
                }
                mapa.set(x, y, entidade);

                if (tipo == 'G') {
                    geradores++;
                }
                if (tipo == 'R') {
                    removedores++;
                }
            }
        }
        return true;
    }
    private StaticEntities criarEntidade(char tipo) {
        switch (tipo) {
            case '.':
                return new StaticEntities.Floor();
            case '#':
                return new StaticEntities.Wall();
            case 'G':
                return new StaticEntities.Generator();
            case 'R':
                return new StaticEntities.Remover();
            case 'T':
                return new StaticEntities.Totem();
            case 'A':
                return new StaticEntities.Seat();
            case 'E':
                return new StaticEntities.Nurse();
            case 'M':
                return new StaticEntities.Medic();
            default:
                return null;
        }
    }
    public Grid<StaticEntities> getMapa() {
        return mapa;
    }
    public int getGeradores() {
        return geradores;
    }
    public int getRemovedores() {
        return removedores;
    }
}