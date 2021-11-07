package io.mamish.autofactory.graph;

public class ProductEdge {

    private final double unitCost;
    private double cumulativeCost = 0;

    public ProductEdge(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setCumulativeCost(double cumulativeCost) {
        this.cumulativeCost = cumulativeCost;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getCumulativeCost() {
        return cumulativeCost;
    }
}
