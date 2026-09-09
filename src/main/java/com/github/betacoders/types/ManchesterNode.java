package com.github.betacoders.types;

public class ManchesterNode
{
  public enum Color
  {
    RED, ORANGE, YELLOW, GREEN, BLUE
  }

  public enum Operator
  {
    EQUAL, LESS_THAN, GREATER_THAN_OR_EQUAL
  }

  private final int attributeIndex;
  private final Operator operator;
  private final float threshold;
  private final Color color;

  // Atributos: 0 = saturacao, 1 = temperatura, 2 = dor, 3 = consciencia alterada.
  public ManchesterNode(int attributeIndex, Operator operator, float threshold)
  {
    if (attributeIndex < 0 || attributeIndex > 3)
    {
      throw new IllegalArgumentException("Invalid attribute index.");
    }

    if (operator == null || !Float.isFinite(threshold))
    {
      throw new IllegalArgumentException("Invalid comparison.");
    }

    this.attributeIndex = attributeIndex;
    this.operator = operator;
    this.threshold = threshold;
    this.color = null;
  }

  public ManchesterNode(Color color)
  {
    if (color == null)
    {
      throw new IllegalArgumentException("A leaf must have a color.");
    }

    this.attributeIndex = -1;
    this.operator = null;
    this.threshold = 0;
    this.color = color;
  }

  public boolean isLeaf()
  {
    return color != null;
  }

  public Color color()
  {
    return color;
  }

  public boolean test(float[] attributes)
  {
    if (isLeaf())
    {
      throw new IllegalStateException("A leaf does not have a test.");
    }

    if (attributes == null || attributes.length != 4)
    {
      throw new IllegalArgumentException("Provide all four patient attributes.");
    }

    float value = attributes[attributeIndex];
    if (!Float.isFinite(value))
    {
      throw new IllegalArgumentException("The attribute must be a finite number.");
    }

    switch (operator)
    {
      case EQUAL:
        return value == threshold;
      case LESS_THAN:
        return value < threshold;
      case GREATER_THAN_OR_EQUAL:
        return value >= threshold;
      default:
        throw new IllegalStateException("Unknown operator.");
    }
  }
}
