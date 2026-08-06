package com.europay.hub.features.dashboard.infrastructure;

import com.europay.hub.features.dashboard.application.DashboardMetricsRepository;
import com.europay.hub.features.dashboard.application.dto.DashboardMetrics;
import com.europay.hub.features.dashboard.application.dto.DashboardMetrics.Count;
import com.europay.hub.features.dashboard.application.dto.DashboardMetrics.DayAmount;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Computes dashboard aggregates with SQL — the read side never loads whole tables. */
@Repository
public class DashboardMetricsJdbcAdapter implements DashboardMetricsRepository {

    private final JdbcTemplate jdbc;

    public DashboardMetricsJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public DashboardMetrics load(UUID merchantId) {
        long revenueMinor = firstLong("""
                SELECT COALESCE(SUM(amount_minor) FILTER (WHERE status IN ('SUCCESS','SETTLED')), 0)
                FROM payment WHERE merchant_id = ?""", merchantId);
        long paymentCount = firstLong("SELECT COUNT(*) FROM payment WHERE merchant_id = ?", merchantId);
        long capturedCount = firstLong(
                "SELECT COUNT(*) FROM payment WHERE merchant_id = ? AND status IN ('SUCCESS','SETTLED')", merchantId);
        long pendingCount = firstLong(
                "SELECT COUNT(*) FROM payment WHERE merchant_id = ? AND status = 'PENDING'", merchantId);
        long orderCount = firstLong("SELECT COUNT(*) FROM orders WHERE merchant_id = ?", merchantId);

        int successRate = paymentCount == 0 ? 0 : (int) Math.round(capturedCount * 100.0 / paymentCount);

        List<Count> byMethod = jdbc.query(
                "SELECT payment_method, COUNT(*) FROM payment WHERE merchant_id = ? GROUP BY payment_method ORDER BY 2 DESC",
                (rs, i) -> new Count(rs.getString(1), rs.getLong(2)), merchantId);
        List<Count> byStatus = jdbc.query(
                "SELECT status, COUNT(*) FROM payment WHERE merchant_id = ? GROUP BY status ORDER BY 2 DESC",
                (rs, i) -> new Count(rs.getString(1), rs.getLong(2)), merchantId);

        List<DayAmount> revenueByDay = jdbc.query("""
                SELECT to_char(date_trunc('day', created_at), 'YYYY-MM-DD') AS d,
                       COALESCE(SUM(amount_minor) FILTER (WHERE status IN ('SUCCESS','SETTLED')), 0) AS m
                FROM payment
                WHERE merchant_id = ? AND created_at >= now() - interval '13 days'
                GROUP BY d ORDER BY d""",
                (rs, i) -> new DayAmount(rs.getString("d"), minorToMajor(rs.getLong("m"))), merchantId);

        return new DashboardMetrics(minorToMajor(revenueMinor), orderCount, paymentCount, pendingCount,
                successRate, byMethod, byStatus, revenueByDay);
    }

    private long firstLong(String sql, UUID merchantId) {
        Long value = jdbc.queryForObject(sql, Long.class, merchantId);
        return value == null ? 0L : value;
    }

    private static BigDecimal minorToMajor(long minor) {
        return BigDecimal.valueOf(minor).movePointLeft(2);
    }
}
