package com.example.do_shopping.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardLowStockProductDTO {
    private String productId;
    private String productName;
    private String categoryName;
    private Integer stock;
}
