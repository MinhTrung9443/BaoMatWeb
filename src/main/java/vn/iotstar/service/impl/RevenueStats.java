package vn.iotstar.service.impl;

public class RevenueStats {
	private long totalProductsSold;
    private String totalRevenue;  // Sử dụng String cho doanh thu đã định dạng

    // Constructor, Getter và Setter
    public RevenueStats(long totalProductsSold, String totalRevenue) {
        this.totalProductsSold = totalProductsSold;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalProductsSold() {
        return totalProductsSold;
    }

    public void setTotalProductsSold(long totalProductsSold) {
        this.totalProductsSold = totalProductsSold;
    }

    public String getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(String totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
