package com.github.betacoders.types.collections;

import com.github.betacoders.types.ManchesterNode;
import com.github.betacoders.types.ManchesterNode.Color;
import com.github.betacoders.types.ManchesterNode.Operator;

public class ManchesterTree
{
  private final ManchesterNode[] nodes = new ManchesterNode[31];

  public ManchesterTree()
  {
    // As posicoes nao utilizadas permanecem nulas.
    nodes[0] = new ManchesterNode(3, Operator.EQUAL, 1);
    nodes[1] = new ManchesterNode(Color.RED);
    nodes[2] = new ManchesterNode(0, Operator.LESS_THAN, 92);
    nodes[5] = new ManchesterNode(Color.ORANGE);
    nodes[6] = new ManchesterNode(2, Operator.GREATER_THAN_OR_EQUAL, 8);
    nodes[13] = new ManchesterNode(Color.YELLOW);
    nodes[14] = new ManchesterNode(1, Operator.GREATER_THAN_OR_EQUAL, 38);
    nodes[29] = new ManchesterNode(Color.GREEN);
    nodes[30] = new ManchesterNode(Color.BLUE);
  }

  public Color classify(float[] attributes)
  {
    if (attributes == null || attributes.length != 4)
    {
      throw new IllegalArgumentException("Provide all four patient attributes.");
    }

    // Valida tambem os atributos que podem nao ser visitados no percurso.
    for (int i = 0; i < attributes.length; i++)
    {
      if (!Float.isFinite(attributes[i]))
      {
        throw new IllegalArgumentException("The attributes must be finite numbers.");
      }
    }

    int index = 0;
    while (!nodes[index].isLeaf())
    {
      if (nodes[index].test(attributes))
      {
        index = 2 * index + 1;
      }
      else
      {
        index = 2 * index + 2;
      }
    }

    return nodes[index].color();
  }
}
