package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT DATE(order_date) AS tanggal, COUNT(id), SUM(total_amount) FROM orders " +
            "WHERE status NOT IN ('PENDING', 'CANCELLED') GROUP BY DATE(order_date)", nativeQuery = true)
    List<Object[]> getFullSalesReport();

    @Query(value = "SELECT DATE(order_date) AS tanggal, COUNT(id), SUM(total_amount) FROM orders " +
            "WHERE status NOT IN ('PENDING', 'CANCELLED') AND DATE(order_date) BETWEEN :start AND :end GROUP BY DATE(order_date)", nativeQuery = true)
    List<Object[]> getSalesReportByDate(LocalDate start, LocalDate end);

    @Query(value = "SELECT \n" +
            "p.name,\n" +
            "SUM(od.quantity) AS total_terjual\n" +
            "FROM order_details od\n" +
            "JOIN products p ON p.id = od.product_id\n" +
            "JOIN orders o ON o.id = od.order_id\n" +
            "WHERE o.status NOT IN ('PENDING', 'CANCELLED')\n" +
            "GROUP BY p.name\n" +
            "ORDER BY total_terjual DESC", nativeQuery = true)
    List<Object[]> getFullTopProductReport();


    @Query(value = "SELECT \n" +
            "p.name,\n" +
            "SUM(od.quantity) AS total_terjual\n" +
            "FROM order_details od\n" +
            "JOIN products p ON p.id = od.product_id\n" +
            "JOIN orders o ON o.id = od.order_id\n" +
            "WHERE o.status NOT IN ('PENDING', 'CANCELLED')\n" +
            "AND DATE(o.order_date) BETWEEN :start AND :end\n" +
            "GROUP BY p.name\n" +
            "ORDER BY total_terjual DESC", nativeQuery = true)
    List<Object[]> getTopProductReportByDate(LocalDate start, LocalDate end);

    @Query(value = "SELECT \n" +
            "c.id,\n" +
            "c.name,\n" +
            "COUNT(o.id) AS total_order\n" +
            "FROM orders o\n" +
            "JOIN customers c ON c.id = o.customer_id\n" +
            "WHERE o.status NOT IN ('PENDING', 'CANCELLED')\n" +
            "GROUP BY c.id, c.name\n" +
            "ORDER BY total_order DESC", nativeQuery = true)
    List<Object[]> getTopCustomerReport();

    @Query(value = "SELECT \n" +
            "c.id,\n" +
            "c.name,\n" +
            "COUNT(o.id) AS total_order\n" +
            "FROM orders o\n" +
            "JOIN customers c ON c.id = o.customer_id\n" +
            "WHERE o.status NOT IN ('PENDING', 'CANCELLED')\n" +
            "AND DATE(o.order_date) between :start and :end\n" +
            "GROUP BY c.id, c.name\n" +
            "ORDER BY total_order DESC", nativeQuery = true)
    List<Object[]> getTopCustomerReportByDate(LocalDate start, LocalDate end);
}
