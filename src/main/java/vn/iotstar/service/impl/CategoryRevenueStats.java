package vn.iotstar.service.impl;

public class CategoryRevenueStats {

	private String categoryName;
    private int totalQuantity;
    private double totalRevenue;
    private String formattedRevenue;  // Doanh thu đã định dạng

    public CategoryRevenueStats(String categoryName, int totalQuantity, double totalRevenue) {
        this.categoryName = categoryName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getFormattedRevenue() {
        return formattedRevenue;
    }

    public void setFormattedRevenue(String formattedRevenue) {
        this.formattedRevenue = formattedRevenue;
    }
}
