package vn.iotstar.dto;


public class RevenueByMonthDTO {

    private String month;
    private int revenue;

    public RevenueByMonthDTO(String month, int revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    // Getter và Setter
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }
}
