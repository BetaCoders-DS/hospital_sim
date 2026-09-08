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
    public boolean mapaValido() {
        if (mapa == null) {
            return false;
        }
        if (geradores != 1 || removedores != 1) {
            return false;
        }

        int[] gerador = encontrar(StaticEntities.Generator.class);
        int[] removedor = encontrar(StaticEntities.Remover.class);

        if (gerador == null || removedor == null) {
            return false;
        }
        return existeCaminho(
                gerador[0],
                gerador[1],
                removedor[0],
                removedor[1]
        );
    }

    private int[] encontrar(Class<?> tipo) {
        for (int y = 0; y < mapa.sizeY(); y++) {
            for (int x = 0; x < mapa.sizeX(); x++) {
                StaticEntities entidade = mapa.get(x, y);

                if (tipo.isInstance(entidade)) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }

    private boolean existeCaminho(
            int inicioX,
            int inicioY,
            int destinoX,
            int destinoY) {

        boolean[][] visitado =
                new boolean[mapa.sizeY()][mapa.sizeX()];
        int capacidade = mapa.sizeX() * mapa.sizeY();

        int[] filaX = new int[capacidade];
        int[] filaY = new int[capacidade];

        int inicioFila = 0;
        int fimFila = 0;

        filaX[fimFila] = inicioX;
        filaY[fimFila] = inicioY;
        fimFila++;

        visitado[inicioY][inicioX] = true;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (inicioFila < fimFila) {
            int x = filaX[inicioFila];
            int y = filaY[inicioFila];
            inicioFila++;
            if (x == destinoX && y == destinoY) {
                return true;
            }
            for (int i = 0; i < 4; i++) {
                int novoX = x + dx[i];
                int novoY = y + dy[i];

                if (!dentroDoMapa(novoX, novoY)) {
                    continue;
                }
                if (visitado[novoY][novoX]) {
                    continue;
                }
                if (mapa.get(novoX, novoY)
                        instanceof StaticEntities.Wall) {
                    continue;
                }
                visitado[novoY][novoX] = true;
                filaX[fimFila] = novoX;
                filaY[fimFila] = novoY;
                fimFila++;
            }
        }
        return false;
    }
    private boolean dentroDoMapa(int x, int y) {
        return x >= 0
                && x < mapa.sizeX()
                && y >= 0
                && y < mapa.sizeY();
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